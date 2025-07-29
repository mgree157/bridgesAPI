# pearson_features.py
import pandas as pd
import numpy as np

def load_and_clean(csv_path, features):
    df = pd.read_csv(csv_path)
    # Drop rows with nulls in any of the selected features
    df_clean = df.dropna(subset=features)
    return df_clean

def compute_feature_vectors(df, features):
    # Each feature is a column, create a vector across all counties
    return df[features].values.T  

def feature_pearson_corr_matrix(vectors, features):
    # Compute Pearson correlation matrix
    corr = np.corrcoef(vectors)
    corr_df = pd.DataFrame(corr, index=features, columns=features)
    return corr_df

def report_most_related(corr_df):
    # Mask diagonal
    corr_no_diag = corr_df.where(~np.eye(corr_df.shape[0], dtype=bool))
    # Find highest value
    max_val = corr_no_diag.max().max()
    pairs = [(i, j) for i in corr_no_diag.index for j in corr_no_diag.columns
             if i != j and corr_no_diag.loc[i,j] > .9]
    return max_val, pairs

def remove_upper_triangle(df, exclude_diag=False):
    if exclude_diag:
        return df.where(np.tril(np.ones(df.shape), k=-1).astype(bool))
    else:
        return df.where(np.tril(np.ones(df.shape)).astype(bool))

def main():
    csv_path = "/Users/mylesgreen/Documents/UNCC compsci/our/src/Counties/Normalized-Table 1.csv"
    features = ["population","populationRank","populationPercentOfState","populationPercentOfStateRank",
                "populationChangeSince2014","populationChangeSince2014Rank","populationChangeSince2014Number",
                "populationChangeSince2014NumberRank","medianAge","medianAgeRank","medianAgeChangePercent",
                "under18ChangeRank","under18Percent","under18Rank","under18ChangeSince2014Percent",
                "under18ChangeSince2014Rank","over65in2024Percent","over65in2024Rank","over65ChangeSince2014Percent",
                "over65ChangeSince2014Rank","veteranPercent","veteranRank","veteranNumber","veteranNumberRank",
                "agriculturalLandAcres","agriculturalLandRank","agriculturalLandChangeSince2017Percent",
                "agriculturalLandChangeSince2017Rank","numberOfFarms","numberOfFarmsRank","numberOfFarmsChangeSince2017Percent",
                "numberOfFarmsChangeSince2017Rank","broadbandInternetAccess2022Percent","broadbandInternetAccessPercent2022Rank",
                "broadbandInternetAccess2019Percent","broadbandInternetAccessPercent2019Rank","computingDeviceAccess2022Percent",
                "computingDeviceAccess2022PercentRank","computingDeviceAccess2019Percent","computingDeviceAccess2019PercentRank",
                "childPovertyPercent","childPovertyPercentRank","childPovertyTotal","childPovertyTotalRank","averageWeeklyWage",
                "averageWeeklyWageRank","averageWeeklyWageAsPercentofStateAverage","averageWeeklyWageAsPercentofStateAverageRank",
                "perCapitaIncome","perCapitaIncomeRank","perCapitaIncomeAsPercentofStatePCI","perCapitaIncomeAsPercentofStatePCIRank",
                "gdp2022","gdp2022Rank","Gdp2021","gdp2021Rank","preKEnrollment2023Percent","preKEnrollment2023PercentRank",
                "preKEnrollment2022Percent","preKEnrollment2022PercentRank","k12FundingADM","k12FundingADMRank","k12Funding",
                "k12FundingRank","youthOpportunity2022Percent","youthOpportunity2022PercentRank","youthOpportunity2019Percent",
                "youthOpportunity2019PercentRank","adultsWithoutHSDiplomaPercent","adultsWithoutHSDiplomaPercentRank","adultsWithoutHSDiplomaTotal",
                "adultsWithoutHSDiplomaTotalRank","educationalAttainmentPercent","educationalAttainmentPercentRank","educationalAttainmentNumber",
                "educationalAttainmentGoal2030","foodInsecurity2022Percent","foodInsecurity2022PercentRank","foodInsecurity2021Percent",
                "foodInsecurity2021PercentRank","medicaidEnrollmentTraditionalPercent","medicaidEnrollmentTraditionalPercentRank",
                "medicaidEnrollmentTraditionalNumber","medicaidEnrollmentTraditionalNumberRank","medicaidEnrollmentExpansionPercent",
                "medicaidEnrollmentExpansionPercentRank","medicaidEnrollmentExpansionNumber","medicaidEnrollmentExpansionNumberRank",
                "uninsuredResidents2021Percent","uninsuredResidents2021PercentRank","uninsuredResidents2020Percent",
                "uninsuredResidents2020PercentRank","drugOverdoseEmergenciesPerHundredThousand","drugOverdoseEmergenciesPerHundredThousandRank",
                "drugOverdoseEmergenciesTotal","drugOverdoseEmergenciesTotalRank","overDoseDeathsPerHundredThousand",
                "overDoseDeathsRank","overDoseDeathsTotal","overDoseDeathsTotalRank","propertyTaxRate","propertyTaxRateRank",
                "propertyTaxLevy","propertyTaxLevyRank","propertyTaxPerCapita","propertyTaxPerCapitaRank","propertyTaxTotalLevy",
                "propertyTaxTotalLevyRank","PropertyValuationPerCapita","PropertyValuationPerCapitaRank","PropertyValuationTotal",
                "PropertyValuationTotalRank","puvDeferredShare","puvDeferredShareRank","puvValuationDeferred","puvValuationDeferredRank",
                "localSalesTaxRate","Area (Square Mile)","Lat","Long", "demVotePercent2000", "repVotePercent2000", "demVotePercent2004", 
                "repVotePercent2004", "demVotePercent2008", "repVotePercent2008", "demVotePercent2012", "repVotePercent2012", 
                "demVotePercent2016", "repVotePercent2016", "demVotePercent2020", "repVotePercent2020", "demVotePercent2024", 
                "repVotePercent2024"]

    print(f"Loaded {len(features)} features.")
    # Remove rank and number-based features
    features = [f for f in features if not f.endswith("Rank")]
    features = [f for f in features if not f.endswith("Number")]
    features = [f for f in features if not f.endswith("Total")]
    features = [f for f in features if not f.endswith("lat")]
    features = [f for f in features if not f.endswith("lng")]

    df = load_and_clean(csv_path, features)
    vectors = compute_feature_vectors(df, features)
    corr_df = feature_pearson_corr_matrix(vectors, features)

    print("Pearson correlation between features:")
    print(corr_df.round(5), "\n")

    # Flatten lower triangle of correlation matrix (excluding diagonal)
    tril_mask = np.tril(np.ones(corr_df.shape), k=-1).astype(bool)
    tril_corr = corr_df.where(tril_mask)

    # Extract non-null values and sort
    sorted_pairs = (
        tril_corr.stack()
        .reset_index()
        .rename(columns={"level_0": "Feature 1", "level_1": "Feature 2", 0: "Correlation"})
        .sort_values(by="Correlation", ascending=False)
    )

    print("Top correlated feature pairs (sorted, no duplicates):")
    for _, row in sorted_pairs.iterrows():
        if row['Correlation'] > 0.5:
            print(f"{row['Correlation']:.8f}  • {row['Feature 1']} ↔ {row['Feature 2']}")

    # Save full sorted list to CSV
    sorted_pairs.to_csv("sorted_feature_correlations.csv", index=False)

    # Optionally save matrix with upper triangle removed
    dfOut = remove_upper_triangle(corr_df, exclude_diag=False)
    dfOut.to_csv('output_pearson.csv', index=False)


    dfOut = pd.DataFrame(corr_df)
    dfOut = remove_upper_triangle(dfOut, exclude_diag=False)
    dfOut.to_csv('output_pearson.csv', index=False)

if __name__ == "__main__":
    main()
