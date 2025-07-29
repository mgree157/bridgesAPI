#!/usr/bin/env python3
"""
Compute party vote‑share percentages and output to a new CSV file.

Usage:
    python county_vote_percents.py --csv NC_county_votes_by_party_year.csv --years 2004 2024
"""

import argparse
import pandas as pd
from pathlib import Path


def add_percent_columns(df: pd.DataFrame, year: int) -> pd.DataFrame:
    """
    Add demVotePercent<year> and repVotePercent<year> columns to *df*.
    """
    dem = f"democrat{year}"
    rep = f"republican{year}"

    if dem not in df.columns or rep not in df.columns:
        raise ValueError(f"Year {year} not found in the data.")

    total = df[dem] + df[rep]
    df[f"demVotePercent{year}"] = (df[dem] / total * 100).round(2)
    df[f"repVotePercent{year}"] = (df[rep] / total * 100).round(2)
    return df


def main(csv_path: Path, years: list[int]) -> None:
    df = pd.read_csv(csv_path)

    for y in years:
        df = add_percent_columns(df, y)

    output_path = csv_path.with_name(csv_path.stem + "_with_percents.csv")
    df.to_csv(output_path, index=False)
    print(f"Saved output to: {output_path}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Add Democrat/Republican vote percentage columns and export new CSV."
    )
    parser.add_argument(
        "--csv",
        default="NC_county_votes_by_party_year.csv",
        help="Path to input CSV file (default: %(default)s)",
    )
    parser.add_argument(
        "--years",
        nargs="+",
        type=int,
        required=True,
        help="Election years to compute percentages for, e.g. 2004 2024",
    )
    args = parser.parse_args()

    main(Path(args.csv), args.years)
