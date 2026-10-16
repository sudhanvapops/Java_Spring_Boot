#!/usr/bin/env python3
"""
k6 performance baseline infographic from a CSV.

Supported layouts (auto-detected):
 1) Summary : metric, statistic, mean_across_runs, stddev_across_runs, min, max
 2) Long    : run, metric, statistic, value
 3) Wide    : one row per run, with p95 columns

Usage:
    python plot.py baseline.csv
    python plot.py baseline.csv --out-dir out

Outputs:
    k6_baseline_linkedin.png  (portrait infographic)
    k6_baseline_detail.png    (landscape analysis chart)
"""

import argparse
import re
from pathlib import Path

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib.ticker import MaxNLocator

STATS = ["avg", "med", "p(90)", "p(95)", "p(99)"]
ENDPOINT_ORDER = ["Author search", "Member records", "Member transactions"]


# ----------------------------------------------------------------- helpers
def norm(text):
    return re.sub(r"[^a-z0-9]+", "_", str(text).strip().lower()).strip("_")


def find_col(columns, candidates):
    """Exact normalized match first, then whole-token match (never substring
    of a token, so 'run' does NOT match 'mean_across_runs')."""
    by_norm = {norm(c): c for c in columns}
    for cand in candidates:
        if norm(cand) in by_norm:
            return by_norm[norm(cand)]
    for c in columns:
        nc = norm(c)
        tokens = nc.split("_")
        for cand in candidates:
            cn = norm(cand)
            if cn in tokens or ("_" in cn and cn in nc):
                return c
    return None


def friendly_metric(metric):
    s = str(metric).lower()
    m = re.search(r"name:([^}]+)", s)
    if m:
        name = m.group(1).strip()
        if "author" in name:
            return "Author search"
        if "member" in name and "transaction" in name:
            return "Member transactions"
        if "member" in name:
            return "Member records"
        return name.replace("_", " ").title()
    if "http_req_duration" in s or s in ("overall", "overall api", "duration"):
        return "Overall API"
    return str(metric).replace("_", " ").strip().title()


def norm_stat(s):
    return re.sub(r"\s+", "", str(s).lower())


def is_p95(s):
    return bool(re.search(r"p\s*\(?\s*95", str(s).lower()))


def parse_args():
    p = argparse.ArgumentParser(description="Plot k6 baseline CSV for LinkedIn.")
    p.add_argument("csv")
    p.add_argument("--run-col")
    p.add_argument("--metric-col")
    p.add_argument("--stat-col")
    p.add_argument("--value-col")
    p.add_argument("--overall-p95-col")
    p.add_argument("--endpoint-p95-cols", nargs="*", default=None,
                   help="LABEL=COLUMN pairs (wide format only).")
    p.add_argument("--out-dir", default=".")
    p.add_argument("--title", default="API PERFORMANCE BASELINE")
    p.add_argument("--subtitle", default="k6 load test · four repeated runs")
    return p.parse_args()


def read_csv(path):
    return pd.read_csv(path, sep=None, engine="python")


# ------------------------------------------------------------ summary layout
def is_summary(df):
    n = {norm(c) for c in df.columns}
    return ("metric" in n and "statistic" in n
            and ("mean_across_runs" in n or "mean" in n))


def summary_frame(df):
    c = {norm(x): x for x in df.columns}
    mean = c.get("mean_across_runs") or c.get("mean")
    sd = c.get("stddev_across_runs") or c.get("stddev") or c.get("std")
    out = pd.DataFrame({
        "label": df[c["metric"]].map(friendly_metric),
        "stat": df[c["statistic"]].map(norm_stat),
        "mean": pd.to_numeric(df[mean], errors="coerce"),
        "min": pd.to_numeric(df[c["min"]], errors="coerce") if "min" in c else np.nan,
        "max": pd.to_numeric(df[c["max"]], errors="coerce") if "max" in c else np.nan,
        "std": pd.to_numeric(df[sd], errors="coerce") if sd else np.nan,
    })
    return out.dropna(subset=["mean"])


def ordered_endpoints(labels):
    labels = list(dict.fromkeys(labels))
    eps = [m for m in ENDPOINT_ORDER if m in labels]
    eps += [m for m in labels if m not in eps and m != "Overall API"]
    return eps


def asym_err(sub):
    lo = (sub["mean"] - sub["min"]).clip(lower=0).to_numpy()
    hi = (sub["max"] - sub["mean"]).clip(lower=0).to_numpy()
    return np.vstack([lo, hi])


def plot_summary(sm, out_dir, title, subtitle):
    out_dir.mkdir(parents=True, exist_ok=True)
    eps = ordered_endpoints(sm["label"])
    p95 = sm[sm["stat"].map(is_p95)]
    if p95.empty:
        raise SystemExit("No p95 rows found in the 'statistic' column.")

    # ---------- LinkedIn portrait ----------
    fig = plt.figure(figsize=(10, 12), constrained_layout=True)
    gs = fig.add_gridspec(3, 1, height_ratios=[0.8, 1.6, 1.6])

    ax_h = fig.add_subplot(gs[0])
    ax_h.axis("off")
    ax_h.text(0.02, 0.88, "K6  /  PERFORMANCE ENGINEERING",
              fontsize=11, fontweight="bold", transform=ax_h.transAxes)
    ax_h.text(0.02, 0.57, title, fontsize=25, fontweight="bold",
              transform=ax_h.transAxes, va="center")
    ax_h.text(0.02, 0.28, subtitle, fontsize=12,
              transform=ax_h.transAxes, va="center")
    ax_h.text(0.02, 0.04,
              "mean of per-run p95  ·  whiskers = min–max across runs  ·  lower is better",
              fontsize=10, transform=ax_h.transAxes)

    # Panel 1: p95 per endpoint (mean + min/max whiskers)
    ax = fig.add_subplot(gs[1])
    rows = [p95[p95["label"] == e].iloc[0] for e in eps if not p95[p95["label"] == e].empty]
    names = [r["label"] for r in rows]
    sub = pd.DataFrame(rows)
    x = np.arange(len(sub))
    bars = ax.bar(x, sub["mean"], width=0.6)
    if sub["min"].notna().all() and sub["max"].notna().all():
        ax.errorbar(x, sub["mean"], yerr=asym_err(sub), fmt="none",
                    ecolor="black", capsize=6, lw=1.5)
    for b, (_, r) in zip(bars, sub.iterrows()):
        txt = f"{r['mean']:.1f} ms"
        if pd.notna(r["min"]) and pd.notna(r["max"]):
            txt += f"\n({r['min']:.0f}–{r['max']:.0f})"
        top = r["max"] if pd.notna(r["max"]) else b.get_height()
        ax.annotate(txt, (b.get_x() + b.get_width() / 2, top),
                    xytext=(0, 5), textcoords="offset points",
                    ha="center", va="bottom", fontsize=9)
    ax.set_xticks(x)
    ax.set_xticklabels(names)
    ax.set_ylabel("p95 latency (ms)")
    ax.set_title("p95 latency by endpoint", loc="left",
                 fontsize=14, fontweight="bold", pad=12)
    ax.set_ylim(0, (sub["max"].max() if sub["max"].notna().any() else sub["mean"].max()) * 1.2)
    ax.yaxis.set_major_locator(MaxNLocator(nbins=6))
    ax.grid(axis="y", alpha=0.25)

    # Panel 2: overall API latency profile
    ax2 = fig.add_subplot(gs[2])
    ov = sm[sm["label"] == "Overall API"]
    ov = pd.DataFrame([ov[ov["stat"] == s].iloc[0] for s in STATS
                       if not ov[ov["stat"] == s].empty])
    if not ov.empty:
        x2 = np.arange(len(ov))
        b2 = ax2.bar(x2, ov["mean"], width=0.6)
        if ov["min"].notna().all() and ov["max"].notna().all():
            ax2.errorbar(x2, ov["mean"], yerr=asym_err(ov), fmt="none",
                         ecolor="black", capsize=6, lw=1.5)
        for b, (_, r) in zip(b2, ov.iterrows()):
            top = r["max"] if pd.notna(r["max"]) else b.get_height()
            ax2.annotate(f"{r['mean']:.1f}", (b.get_x() + b.get_width() / 2, top),
                         xytext=(0, 5), textcoords="offset points",
                         ha="center", va="bottom", fontsize=9)
        ax2.set_xticks(x2)
        ax2.set_xticklabels(ov["stat"])
        ax2.set_ylabel("latency (ms)")
        ax2.set_title("Overall API latency profile", loc="left",
                      fontsize=14, fontweight="bold", pad=12)
        ax2.set_ylim(0, ov["max"].max() * 1.2 if ov["max"].notna().any() else None)
        ax2.yaxis.set_major_locator(MaxNLocator(nbins=5))
        ax2.grid(axis="y", alpha=0.25)
    else:
        ax2.axis("off")

    fig.savefig(out_dir / "k6_baseline_linkedin.png", dpi=240, bbox_inches="tight")
    plt.close(fig)

    # ---------- Detail chart: every endpoint x every stat ----------
    fig, ax = plt.subplots(figsize=(13, 7), constrained_layout=True)
    groups = ["Overall API"] + eps
    groups = [g for g in groups if g in set(sm["label"])]
    x = np.arange(len(groups))
    width = 0.8 / len(STATS)
    for j, s in enumerate(STATS):
        vals, errs = [], []
        for g in groups:
            r = sm[(sm["label"] == g) & (sm["stat"] == s)]
            if r.empty:
                vals.append(np.nan)
                errs.append([0, 0])
            else:
                r = r.iloc[0]
                vals.append(r["mean"])
                errs.append([max(r["mean"] - r["min"], 0) if pd.notna(r["min"]) else 0,
                             max(r["max"] - r["mean"], 0) if pd.notna(r["max"]) else 0])
        ax.bar(x + (j - (len(STATS) - 1) / 2) * width, vals, width=width,
               yerr=np.array(errs).T, capsize=2, label=s)
    ax.set_xticks(x)
    ax.set_xticklabels(groups)
    ax.set_ylabel("latency (ms)")
    ax.set_title("k6 baseline — latency percentiles by endpoint (mean across runs, whiskers = min–max)",
                 loc="left", fontsize=14, fontweight="bold", pad=14)
    ax.grid(axis="y", alpha=0.25)
    ax.legend(title="Statistic", ncols=len(STATS), frameon=False)
    fig.savefig(out_dir / "k6_baseline_detail.png", dpi=200, bbox_inches="tight")
    plt.close(fig)

    print(f"Saved: {out_dir / 'k6_baseline_linkedin.png'}")
    print(f"Saved: {out_dir / 'k6_baseline_detail.png'}")


# ---------------------------------------------------- long / wide layouts
def get_p95_runs(df, args):
    cols = list(df.columns)
    run_col = args.run_col or find_col(cols, ["run", "run_id", "test_run", "iteration"])
    metric_col = args.metric_col or find_col(cols, ["metric", "name"])
    stat_col = args.stat_col or find_col(cols, ["statistic", "stat", "percentile"])
    value_col = args.value_col or find_col(cols, ["value", "latency_ms", "duration_ms", "result"])

    if run_col and metric_col and value_col:  # long
        w = df.copy()
        stat = w[stat_col].astype(str) if stat_col else pd.Series("p(95)", index=w.index)
        w = w[stat.map(is_p95)]
        out = pd.DataFrame({
            "run": w[run_col].astype(str),
            "metric": w[metric_col].astype(str),
            "p95": pd.to_numeric(w[value_col], errors="coerce"),
        }).dropna()
        if out.empty:
            raise SystemExit("Long format found, but no p95 rows identified.")
        return out

    if not run_col:
        raise SystemExit("Could not infer CSV layout. Use --run-col/--metric-col/--value-col.")

    explicit = {}  # wide
    if args.overall_p95_col:
        explicit["Overall API"] = args.overall_p95_col
    for item in args.endpoint_p95_cols or []:
        label, col = item.split("=", 1)
        explicit[label] = col
    if not explicit:
        for c in cols:
            if "p95" in norm(c) or "p_95" in norm(c):
                explicit[friendly_metric(c)] = c
    if not explicit:
        raise SystemExit("No p95 columns found. Use --overall-p95-col / --endpoint-p95-cols.")

    rows = []
    for label, col in explicit.items():
        vals = pd.to_numeric(df[col], errors="coerce")
        for run, v in zip(df[run_col].astype(str), vals):
            if pd.notna(v):
                rows.append({"run": run, "metric": label, "p95": float(v)})
    return pd.DataFrame(rows)


def plot_runs(p95, out_dir, title, subtitle):
    out_dir.mkdir(parents=True, exist_ok=True)
    p95 = p95.copy()
    p95["label"] = p95["metric"].map(friendly_metric)
    eps = ordered_endpoints(p95["label"])

    fig = plt.figure(figsize=(10, 12), constrained_layout=True)
    gs = fig.add_gridspec(3, 1, height_ratios=[0.9, 1.7, 1.4])
    ax_h = fig.add_subplot(gs[0])
    ax_h.axis("off")
    ax_h.text(0.02, 0.88, "K6  /  PERFORMANCE ENGINEERING", fontsize=11,
              fontweight="bold", transform=ax_h.transAxes)
    ax_h.text(0.02, 0.57, title, fontsize=25, fontweight="bold",
              transform=ax_h.transAxes, va="center")
    ax_h.text(0.02, 0.28, subtitle, fontsize=12, transform=ax_h.transAxes, va="center")
    ax_h.text(0.02, 0.04, f"{p95['run'].nunique()} runs  ·  p95 response time  ·  lower is better",
              fontsize=10, transform=ax_h.transAxes)

    ax = fig.add_subplot(gs[1])
    data = [p95[p95["label"] == m]["p95"].to_numpy() for m in eps]
    if eps:
        ax.boxplot(data, showmeans=True)
        ax.set_xticks(range(1, len(eps) + 1))
        ax.set_xticklabels(eps)
        for i, m in enumerate(eps, start=1):
            sub = p95[p95["label"] == m]
            xs = np.full(len(sub), i) + (np.linspace(-0.08, 0.08, len(sub)) if len(sub) > 1 else 0)
            ax.scatter(xs, sub["p95"], zorder=3, s=48)
            for xx, (_, r) in zip(xs, sub.iterrows()):
                ax.annotate(r["run"], (xx, r["p95"]), xytext=(3, 4),
                            textcoords="offset points", fontsize=7, rotation=25)
        ax.set_ylabel("p95 latency (ms)")
        ax.set_title("How much does each endpoint vary across runs?", loc="left",
                     fontsize=14, fontweight="bold", pad=12)
        ax.grid(axis="y", alpha=0.25)
    else:
        ax.axis("off")

    ax2 = fig.add_subplot(gs[2])
    ov = p95[p95["label"] == "Overall API"]
    if not ov.empty:
        x = np.arange(len(ov))
        bars = ax2.bar(x, ov["p95"], width=0.62)
        ax2.set_xticks(x)
        ax2.set_xticklabels(ov["run"])
        ax2.set_ylabel("p95 latency (ms)")
        ax2.set_title("Overall API p95 by run", loc="left", fontsize=14,
                      fontweight="bold", pad=12)
        ax2.grid(axis="y", alpha=0.25)
        for b, v in zip(bars, ov["p95"]):
            ax2.annotate(f"{v:.1f} ms", (b.get_x() + b.get_width() / 2, b.get_height()),
                         xytext=(0, 5), textcoords="offset points", ha="center", fontsize=9)
    else:
        ax2.axis("off")
    fig.savefig(out_dir / "k6_baseline_linkedin.png", dpi=240, bbox_inches="tight")
    plt.close(fig)

    fig, ax = plt.subplots(figsize=(13, 7), constrained_layout=True)
    groups = [g for g in ["Overall API"] + eps if g in set(p95["label"])]
    runs = list(p95["run"].drop_duplicates())
    x = np.arange(len(groups))
    width = min(0.8 / max(len(runs), 1), 0.22)
    for j, run in enumerate(runs):
        vals = []
        for g in groups:
            s = p95[(p95["label"] == g) & (p95["run"] == run)]
            vals.append(float(s["p95"].iloc[0]) if not s.empty else np.nan)
        ax.bar(x + (j - (len(runs) - 1) / 2) * width, vals, width=width, label=run)
    ax.set_xticks(x)
    ax.set_xticklabels(groups)
    ax.set_ylabel("p95 latency (ms)")
    ax.set_title("k6 baseline — p95 latency by endpoint and run", loc="left",
                 fontsize=16, fontweight="bold", pad=14)
    ax.grid(axis="y", alpha=0.25)
    ax.legend(title="Run", ncols=min(4, max(1, len(runs))), frameon=False)
    fig.savefig(out_dir / "k6_baseline_detail.png", dpi=200, bbox_inches="tight")
    plt.close(fig)

    print(f"Saved: {out_dir / 'k6_baseline_linkedin.png'}")
    print(f"Saved: {out_dir / 'k6_baseline_detail.png'}")


# -------------------------------------------------------------------- main
def main():
    args = parse_args()
    path = Path(args.csv)
    if not path.exists():
        raise SystemExit(f"CSV not found: {path}")
    df = read_csv(path)
    out_dir = Path(args.out_dir)

    if is_summary(df):
        plot_summary(summary_frame(df), out_dir, args.title, args.subtitle)
    else:
        plot_runs(get_p95_runs(df, args), out_dir, args.title, args.subtitle)


if __name__ == "__main__":
    main()