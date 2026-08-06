export const CURRENCY = process.env.NEXT_PUBLIC_CURRENCY_SYMBOL || "₹";

/** Zero is a word when zero is good news — a fine of nothing reads "no fine",
 * never "₹0.00" (readme.md "Content fundamentals"). */
export function formatFine(amount: number): string {
  if (amount <= 0) return "no fine";
  return `${CURRENCY}${amount.toFixed(2)}`;
}

/** Always shows the amount, even when zero — for editable/estimate contexts
 * where "no fine" would read as "field is empty". */
export function formatAmount(amount: number): string {
  return `${CURRENCY}${amount.toFixed(2)}`;
}
