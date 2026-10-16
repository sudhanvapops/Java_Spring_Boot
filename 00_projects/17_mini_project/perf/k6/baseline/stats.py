import json
import csv
import statistics
from pathlib import Path

files = [
    Path("run1.json"),
    Path("run2.json"),
    Path("run3.json"),
    Path("run4.json"),
]

reports = [json.loads(f.read_text()) for f in files]

metric_names = [
    "http_req_duration",
    "http_req_duration{name:author_search}",
    "http_req_duration{name:member_records}",
    "http_req_duration{name:member_transaction_records}",
]

fields = ["avg", "med", "p(90)", "p(95)", "p(99)", "max"]

with open("baseline.csv", "w", newline="") as output:
    writer = csv.writer(output)
    writer.writerow([
        "metric", "statistic", "mean_across_runs",
        "stddev_across_runs", "min", "max"
    ])

    for metric in metric_names:
        for field in fields:
            values = [
                report["metrics"][metric][field]
                for report in reports
                if metric in report["metrics"]
                and field in report["metrics"][metric]
            ]

            if not values:
                continue

            writer.writerow([
                metric,
                field,
                round(statistics.mean(values), 3),
                round(statistics.stdev(values), 3)
                    if len(values) > 1 else 0,
                round(min(values), 3),
                round(max(values), 3),
            ])

print("Created baseline.csv")