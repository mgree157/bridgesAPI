# cosine_features.py
import pandas as pd
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity

def load_and_clean(csv_path, features):
    df = pd.read_csv(csv_path)
    # Drop rows with nulls in any of the selected features
    df_clean = df.dropna(subset=features)
    return df_clean

def compute_feature_vectors(df, features):
    # Each feature is a column, create a vector across all counties
    return df[features].values.T  # shape: (n_features, n_counties)

def feature_cosine_sim_matrix(vectors, features):
    # Compute cosine similarity matrix (symmetric)
    sim = cosine_similarity(vectors)  # using sklearn :contentReference[oaicite:5]{index=5}
    sim_df = pd.DataFrame(sim, index=features, columns=features)
    return sim_df

def report_most_related(sim_df):
    # Mask diagonal
    sim_no_diag = sim_df.where(~np.eye(sim_df.shape[0], dtype=bool))
    # Find highest value
    max_val = sim_no_diag.max().max()
    pairs = [(i, j) for i in sim_no_diag.index for j in sim_no_diag.columns
             if i != j and np.isclose(sim_no_diag.loc[i,j], max_val)]
    return max_val, pairs

def main():
    csv_path = "src/Counties/Normalized-Table 1.csv"
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
                "localSalesTaxRate","Area (Square Mile)","Lat","Long"]

    features = [f for f in features if not f.endswith("Rank")]

    # proceed as before
    df = load_and_clean(csv_path, features)
    vectors = compute_feature_vectors(df, features)
    sim_df = feature_cosine_sim_matrix(vectors, features)

    print("Cosine similarity between features:")
    print(sim_df.round(3), "\n")

    max_val, pairs = report_most_related(sim_df)
    print(f"Most related feature pair(s) (cosine ≈ {max_val:.3f}):")
    for i, j in pairs:
        print(f"  • {i} ↔ {j}")

if __name__ == "__main__":
    main()
