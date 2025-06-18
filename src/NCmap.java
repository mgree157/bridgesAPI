package src;
import java.util.ArrayList;
import java.util.Map;
import bridges.connect.Bridges;
import bridges.connect.DataSource;
import bridges.data_src_dependent.USState;
import bridges.data_src_dependent.USCounty;
import bridges.base.USMap;
import bridges.base.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

// This program illustrates how to access the data of the US map with state
// boundaries with different colors for states and its boundaries
// Here we select 3 states and draw them with different attributes with or 
// without counties

public class NCmap {
	public static void main(String[] args) throws Exception {

		Bridges bridges = new Bridges(2001, "mgree157", "581575557990");
		DataSource ds = bridges.getDataSource();
		// set title, description, and author
		bridges.setTitle("North Carolina Map Visualization");
		// get NC map data
		String[] states = {"North Carolina"};
		ArrayList<USState> map_data = ds.getUSMapCountyData(states, true);
		USState nc = map_data.get(0);
		// get counties from NC map
		List<USCounty> counties = new ArrayList<>();
		for (Map.Entry<String,USCounty> e: nc.getCounties().entrySet()) {
			USCounty c = e.getValue();
			counties.add(c);
		}
		// get county data from csv
		List<County> countyData = dataFetcher();

		// alphabetize counties
		counties.sort((c1, c2) -> c1.getCountyName().compareTo(c2.getCountyName()));	
		countyData.sort((c1, c2) -> c1.countyName.compareTo(c2.countyName));
		System.out.println("Counties in North Carolina: " + counties.size());
		System.out.println("Counties in County Data: " + countyData.size());

		// extract average and total counties from county data
		County min = null; 
		County max = null; 
		for (int i = 0; i < countyData.size(); i++) {
			if (countyData.get(i).countyName.equals("Min")) {
				min = countyData.get(i);
				countyData.remove(i);
				continue;
			}
			if (countyData.get(i).countyName.equals("Max")) {
				max = countyData.get(i);
				countyData.remove(i);
				continue;
			}
		}
		
		String[] redScale = {
			"#fff5f0", "#fee0d2", "#fcbba1", "#fc9272", "#fb6a4a",
			"#ef3b2c", "#cb181d", "#a50f15", "#67000d"
		};

		for (int i = 0; i < counties.size(); i++) {
			County county = countyData.get(i);
			Object figure = county.numberOfFarms;
			double minVal = min.numberOfFarms;
			double maxVal = max.numberOfFarms;
			
			if (figure == null) {
				System.out.println("\n!!! Skipping " + county.countyName);
				continue;
			}

			// Clamp and scale value to index 0–8
			double normalizedVal = (((double) figure - minVal) / (maxVal - minVal))*8;
			int index = Math.round((long) normalizedVal);
			System.out.println(index + "\t" + normalizedVal + "\t" + county.countyName);
			if (index < 0) index = 0;
			if (index > 8) index = 8;

			String hex = redScale[index];

			// Convert hex to RGB
			int r = Integer.valueOf(hex.substring(1, 3), 16);
			int g = Integer.valueOf(hex.substring(3, 5), 16);
			int b = Integer.valueOf(hex.substring(5, 7), 16);
			// System.out.println(index + " " + r + ", " + g + ", " + b + "\t" + county.countyName);

			// map
			USCounty c = counties.get(i);
			c.setFillColor(new Color(r, g, b));
			c.setStrokeColor(new Color(0, 0, 0));
		}
		USMap us_maps = new USMap(map_data);
		bridges.setDataStructure(us_maps);
		bridges.visualize();
	}

	public static List<County> dataFetcher(){
		String file = "src/processedData.csv";
        String line;
		List<County> countyData = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while ((line = br.readLine()) != null) {
				if (line.startsWith("Unnamed") || line.startsWith("Demographics")) continue;

				String[] values = line.split(",");
				if (values.length < 31) {
					// System.err.println("Skipping line due to insufficient data: " + line);
					continue;
				}
				County nexCounty = new County(
					values[0], // countyName
					parseDoubleOrNull(values[1]), // population
					parseIntegerOrNull(values[2]), // populationRank
					parseDoubleOrNull(values[3]), // populationPercentOfState
					parseIntegerOrNull(values[4]), // populationPercentOfStateRank
					parseDoubleOrNull(values[5]), // populationChangeSince2014
					parseIntegerOrNull(values[6]), // populationChangeSince2014Rank
					parseIntegerOrNull(values[7]), // populationChangeSince2014Number
					parseIntegerOrNull(values[8]), // populationChangeSince2014NumberRank
					parseDoubleOrNull(values[9]), // medianAge
					parseIntegerOrNull(values[10]), // medianAgeRank
					parseDoubleOrNull(values[11]), // medianAgeChangePercent
					parseIntegerOrNull(values[12]), // under18ChangeRank
					parseDoubleOrNull(values[13]), // under18Percent
					parseIntegerOrNull(values[14]), // under18Rank
					parseDoubleOrNull(values[15]), // under18ChangeSince2014Percent
					parseIntegerOrNull(values[16]), // under18ChangeSince2014Rank
					parseDoubleOrNull(values[17]), // over65in2024Percent
					parseIntegerOrNull(values[18]), // over65in2024Rank
					parseDoubleOrNull(values[19]), // over65ChangeSince2014Percent
					parseIntegerOrNull(values[20]), // over65ChangeSince2014Rank
					parseDoubleOrNull(values[21]), // veteranPercent
					parseIntegerOrNull(values[22]), // veteranRank
					parseIntegerOrNull(values[23]), // veteranNumber
					parseIntegerOrNull(values[24]), // veteranNumberRank
					parseIntegerOrNull(values[25]), // agriculturalLandAcres
					parseIntegerOrNull(values[26]), // agriculturalLandRank
					parseDoubleOrNull(values[27]), // agriculturalLandChangeSince2017Percent
					parseIntegerOrNull(values[28]), // agriculturalLandChangeSince2017Rank
					parseIntegerOrNull(values[29]), // numberOfFarms
					parseIntegerOrNull(values[30]), // numberOfFarmsRank
					parseDoubleOrNull(values[31]), // numberOfFarmsChangeSince2017Percent
					parseIntegerOrNull(values[32]), // numberOfFarmsChangeSince2017Rank
					parseDoubleOrNull(values[33]), // broadbandInternetAccess2022Percent
					parseIntegerOrNull(values[34]), // broadbandInternetAccessPercent2022Rank
					parseDoubleOrNull(values[35]), // broadbandInternetAccess2019Percent
					parseIntegerOrNull(values[36]), // broadbandInternetAccessPercent2019Rank
					parseDoubleOrNull(values[37]), // computingDeviceAccess2022Percent
					parseIntegerOrNull(values[38]), // computingDeviceAccess2022PercentRank
					parseDoubleOrNull(values[39]), // computingDeviceAccess2019Percent
					parseIntegerOrNull(values[40]), // computingDeviceAccess2019PercentRank
					parseDoubleOrNull(values[41]), // childPovertyPercent
					parseIntegerOrNull(values[42]), // childPovertyPercentRank
					parseIntegerOrNull(values[43]), // childPovertyTotal
					parseIntegerOrNull(values[44]), // childPovertyTotalRank
					parseDoubleOrNull(values[45]), // averageWeeklyWage
					parseIntegerOrNull(values[46]), // averageWeeklyWageRank
					parseDoubleOrNull(values[47]), // averageWeeklyWageAsPercentofStateAverage
					parseIntegerOrNull(values[48]), // averageWeeklyWageAsPercentofStateAverageRank
					parseDoubleOrNull(values[49]), // perCapitaIncome
					parseIntegerOrNull(values[50]), // perCapitaIncomeRank
					parseDoubleOrNull(values[51]), // perCapitaIncomeAsPercentofStatePCI
					parseIntegerOrNull(values[52]), // perCapitaIncomeAsPercentofStatePCIRank
					parseDoubleOrNull(values[53]), // gdp2022
					parseIntegerOrNull(values[54]), // gdp2022Rank
					parseDoubleOrNull(values[55]), // gdp2021
					parseIntegerOrNull(values[56]), // gdp2021Rank
					parseDoubleOrNull(values[57]), // preKEnrollment2023Percent
					parseIntegerOrNull(values[58]), // preKEnrollment2023PercentRank
					parseDoubleOrNull(values[59]), // preKEnrollment2022Percent
					parseIntegerOrNull(values[60]), // preKEnrollment2022PercentRank
					parseDoubleOrNull(values[61]), // k12FundingADM
					parseIntegerOrNull(values[62]), // k12FundingADMRank
					parseDoubleOrNull(values[63]), // k12Funding
					parseIntegerOrNull(values[64]), // k12FundingRank
					parseDoubleOrNull(values[65]), // youthOpportunity2022Percent
					parseIntegerOrNull(values[66]), // youthOpportunity2022PercentRank
					parseDoubleOrNull(values[67]), // youthOpportunity2019Percent
					parseIntegerOrNull(values[68]), // youthOpportunity2019PercentRank
					parseDoubleOrNull(values[69]), // adultsWithoutHSDiplomaPercent
					parseIntegerOrNull(values[70]), // adultsWithoutHSDiplomaPercentRank
					parseIntegerOrNull(values[71]), // adultsWithoutHSDiplomaTotal
					parseIntegerOrNull(values[72]), // adultsWithoutHSDiplomaTotalRank
					parseDoubleOrNull(values[73]), // educationalAttainmentPercent
					parseIntegerOrNull(values[74]), // educationalAttainmentPercentRank
					parseIntegerOrNull(values[75]), // educationalAttainmentNumber
					parseDoubleOrNull(values[76]), // educationalAttainmentGoal2030
					parseDoubleOrNull(values[77]), // foodInsecurity2022Percent
					parseIntegerOrNull(values[78]), // foodInsecurity2022PercentRank
					parseDoubleOrNull(values[79]), // foodInsecurity2021Percent
					parseIntegerOrNull(values[80]), // foodInsecurity2021PercentRank
					parseDoubleOrNull(values[81]), // medicaidEnrollmentTraditionalPercent
					parseIntegerOrNull(values[82]), // medicaidEnrollmentTraditionalPercentRank
					parseIntegerOrNull(values[83]), // medicaidEnrollmentTraditionalNumber
					parseIntegerOrNull(values[84]), // medicaidEnrollmentTraditionalNumberRank
					parseDoubleOrNull(values[85]), // medicaidEnrollmentExpansionPercent
					parseIntegerOrNull(values[86]), // medicaidEnrollmentExpansionPercentRank
					parseIntegerOrNull(values[87]), // medicaidEnrollmentExpansionNumber
					parseIntegerOrNull(values[88]), // medicaidEnrollmentExpansionNumberRank
					parseDoubleOrNull(values[89]), // uninsuredResidents2021Percent
					parseIntegerOrNull(values[90]), // uninsuredResidents2021PercentRank
					parseDoubleOrNull(values[91]), // uninsuredResidents2020Percent
					parseIntegerOrNull(values[92]), // uninsuredResidents2020PercentRank
					parseDoubleOrNull(values[93]), // drugOverdoseEmergenciesPerHundredThousand
					parseIntegerOrNull(values[94]), // drugOverdoseEmergenciesPerHundredThousandRank
					parseIntegerOrNull(values[95]), // drugOverdoseEmergenciesTotal
					parseIntegerOrNull(values[96]), // drugOverdoseEmergenciesTotalRank
					parseDoubleOrNull(values[97]), // overDoseDeathsPerHundredThousand
					parseIntegerOrNull(values[98]), // overDoseDeathsRank
					parseIntegerOrNull(values[99]), // overDoseDeathsTotal
					parseIntegerOrNull(values[100]), // overDoseDeathsTotalRank
					parseDoubleOrNull(values[101]), // propertyTaxRate
					parseIntegerOrNull(values[102]), // propertyTaxRateRank
					parseDoubleOrNull(values[103]), // propertyTaxLevy
					parseIntegerOrNull(values[104]), // propertyTaxLevyRank
					parseDoubleOrNull(values[105]), // propertyTaxLevyPerCapita
					parseIntegerOrNull(values[106]), // propertyTaxLevyPerCapitaRank
					parseDoubleOrNull(values[107]), // propertyTaxLevyTotal
					parseIntegerOrNull(values[108]), // propertyTaxLevyTotalRank
					parseDoubleOrNull(values[109]), // propertyValuationPerCapita
					parseIntegerOrNull(values[110]), // propertyValuationPerCapitaRank
					parseDoubleOrNull(values[111]), // propertyValuationTotal
					parseIntegerOrNull(values[112]), // propertyValuationTotalRank
					parseDoubleOrNull(values[113]), // realPropertyValuationDeferred
					parseIntegerOrNull(values[114]), // realPropertyValuationDeferredRank
					parseDoubleOrNull(values[115]), // realPropertyValuationDeferredTotal
					parseIntegerOrNull(values[116]), // realPropertyValuationDeferredTotalRank
					parseDoubleOrNull(values[117]), // localSalesTaxRate
					parseDoubleOrNull(values[118]) // areaSquareMiles
				);
				countyData.add(nexCounty);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return countyData;
	}

	public static class County {
		String countyName;
		Double population;
		Integer populationRank;
		Double populationPercentOfState;
		Integer populationPercentOfStateRank;
		Double populationChangeSince2014;
		Integer populationChangeSince2014Rank;
		Integer populationChangeSince2014Number;
		Integer populationChangeSince2014NumberRank;

		Double medianAge;
		Integer medianAgeRank;
		Double medianAgeChangePercent;

		Integer under18ChangeRank;
		Double under18Percent;
		Integer under18Rank;
		Double under18ChangeSince2014Percent;
		Integer under18ChangeSince2014Rank;

		Double over65in2024Percent;
		Integer over65in2024Rank;
		Double over65ChangeSince2014Percent;
		Integer over65ChangeSince2014Rank;

		Double veteranPercent;
		Integer veteranRank;
		Integer veteranNumber;
		Integer veteranNumberRank;

		Integer agriculturalLandAcres;
		Integer agriculturalLandRank;
		Double agriculturalLandChangeSince2017Percent;
		Integer agriculturalLandChangeSince2017Rank;

		Integer numberOfFarms;
		Integer numberOfFarmsRank;
		Double numberOfFarmsChangeSince2017Percent;
		Integer numberOfFarmsChangeSince2017Rank;

		Double broadbandInternetAccess2022Percent;
		Integer broadbandInternetAccessPercent2022Rank;
		Double broadbandInternetAccess2019Percent;
		Integer broadbandInternetAccessPercent2019Rank;

		Double computingDeviceAccess2022Percent;
		Integer computingDeviceAccess2022PercentRank;
		Double computingDeviceAccess2019Percent;
		Integer computingDeviceAccess2019PercentRank;

		Double childPovertyPercent;
		Integer childPovertyPercentRank;
		Integer childPovertyTotal;
		Integer childPovertyTotalRank;

		Double averageWeeklyWage;
		Integer averageWeeklyWageRank;
		Double averageWeeklyWageAsPercentofStateAverage;
		Integer averageWeeklyWageAsPercentofStateAverageRank;

		Double perCapitaIncome;
		Integer perCapitaIncomeRank;
		Double perCapitaIncomeAsPercentofStatePCI;
		Integer perCapitaIncomeAsPercentofStatePCIRank;

		Double gdp2022;
		Integer gdp2022Rank;
		Double gdp2021;
		Integer gdp2021Rank;

		Double preKEnrollment2023Percent;
		Integer preKEnrollment2023PercentRank;
		Double preKEnrollment2022Percent;
		Integer preKEnrollment2022PercentRank;

		Double k12FundingADM;
		Integer k12FundingADMRank;
		Double k12Funding;
		Integer k12FundingRank;

		Double youthOpportunity2022Percent;
		Integer youthOpportunity2022PercentRank;
		Double youthOpportunity2019Percent;
		Integer youthOpportunity2019PercentRank;

		Double adultsWithoutHSDiplomaPercent;
		Integer adultsWithoutHSDiplomaPercentRank;
		Integer adultsWithoutHSDiplomaTotal;
		Integer adultsWithoutHSDiplomaTotalRank;

		Double educationalAttainmentPercent;
		Integer educationalAttainmentPercentRank;
		Integer educationalAttainmentNumber;
		Double educationalAttainmentGoal2030;

		Double foodInsecurity2022Percent;
		Integer foodInsecurity2022PercentRank;
		Double foodInsecurity2021Percent;
		Integer foodInsecurity2021PercentRank;

		Double medicaidEnrollmentTraditionalPercent;
		Integer medicaidEnrollmentTraditionalPercentRank;
		Integer medicaidEnrollmentTraditionalNumber;
		Integer medicaidEnrollmentTraditionalNumberRank;

		Double medicaidEnrollmentExpansionPercent;
		Integer medicaidEnrollmentExpansionPercentRank;
		Integer medicaidEnrollmentExpansionNumber;
		Integer medicaidEnrollmentExpansionNumberRank;

		Double uninsuredResidents2021Percent;
		Integer uninsuredResidents2021PercentRank;
		Double uninsuredResidents2020Percent;
		Integer uninsuredResidents2020PercentRank;

		Double drugOverdoseEmergenciesPerHundredThousand;
		Integer drugOverdoseEmergenciesPerHundredThousandRank;
		Integer drugOverdoseEmergenciesTotal;
		Integer drugOverdoseEmergenciesTotalRank;

		Double overDoseDeathsPerHundredThousand;
		Integer overDoseDeathsRank;
		Integer overDoseDeathsTotal;
		Integer overDoseDeathsTotalRank;

		Double propertyTaxRate;
		Integer propertyTaxRateRank;
		Double propertyTaxLevy;
		Integer propertyTaxLevyRank;
		Double propertyTaxLevyPerCapita;
		Integer propertyTaxLevyPerCapitaRank;
		Double propertyTaxLevyTotal;
		Integer propertyTaxLevyTotalRank;

		Double propertyValuationPerCapita;
		Integer propertyValuationPerCapitaRank;
		Double propertyValuationTotal;
		Integer propertyValuationTotalRank;

		Double realPropertyValuationDeferred;
		Integer realPropertyValuationDeferredRank;
		Double realPropertyValuationDeferredTotal;
		Integer realPropertyValuationDeferredTotalRank;

		Double localSalesTaxRate;
		Double areaSquareMiles;

		public County(
				String countyName,
				Double population,
				Integer populationRank,
				Double populationPercentOfState,
				Integer populationPercentOfStateRank,
				Double populationChangeSince2014,
				Integer populationChangeSince2014Rank,
				Integer populationChangeSince2014Number,
				Integer populationChangeSince2014NumberRank,
				Double medianAge,
				Integer medianAgeRank,
				Double medianAgeChangePercent,
				Integer under18ChangeRank,
				Double under18Percent,
				Integer under18Rank,
				Double under18ChangeSince2014Percent,
				Integer under18ChangeSince2014Rank,
				Double over65in2024Percent,
				Integer over65in2024Rank,
				Double over65ChangeSince2014Percent,
				Integer over65ChangeSince2014Rank,
				Double veteranPercent,
				Integer veteranRank,
				Integer veteranNumber,
				Integer veteranNumberRank,
				Integer agriculturalLandAcres,
				Integer agriculturalLandRank,
				Double agriculturalLandChangeSince2017Percent,
				Integer agriculturalLandChangeSince2017Rank,
				Integer numberOfFarms,
				Integer numberOfFarmsRank,
				Double numberOfFarmsChangeSince2017Percent,
				Integer numberOfFarmsChangeSince2017Rank,
				Double broadbandInternetAccess2022Percent,
				Integer broadbandInternetAccessPercent2022Rank,
				Double broadbandInternetAccess2019Percent,
				Integer broadbandInternetAccessPercent2019Rank,
				Double computingDeviceAccess2022Percent,
				Integer computingDeviceAccess2022PercentRank,
				Double computingDeviceAccess2019Percent,
				Integer computingDeviceAccess2019PercentRank,
				Double childPovertyPercent,
				Integer childPovertyPercentRank,
				Integer childPovertyTotal,
				Integer childPovertyTotalRank,
				Double averageWeeklyWage,
				Integer averageWeeklyWageRank,
				Double averageWeeklyWageAsPercentofStateAverage,
				Integer averageWeeklyWageAsPercentofStateAverageRank,
				Double perCapitaIncome,
				Integer perCapitaIncomeRank,
				Double perCapitaIncomeAsPercentofStatePCI,
				Integer perCapitaIncomeAsPercentofStatePCIRank,
				Double gdp2022,
				Integer gdp2022Rank,
				Double gdp2021,
				Integer gdp2021Rank,
				Double preKEnrollment2023Percent,
				Integer preKEnrollment2023PercentRank,
				Double preKEnrollment2022Percent,
				Integer preKEnrollment2022PercentRank,
				Double k12FundingADM,
				Integer k12FundingADMRank,
				Double k12Funding,
				Integer k12FundingRank,
				Double youthOpportunity2022Percent,
				Integer youthOpportunity2022PercentRank,
				Double youthOpportunity2019Percent,
				Integer youthOpportunity2019PercentRank,
				Double adultsWithoutHSDiplomaPercent,
				Integer adultsWithoutHSDiplomaPercentRank,
				Integer adultsWithoutHSDiplomaTotal,
				Integer adultsWithoutHSDiplomaTotalRank,
				Double educationalAttainmentPercent,
				Integer educationalAttainmentPercentRank,
				Integer educationalAttainmentNumber,
				Double educationalAttainmentGoal2030,
				Double foodInsecurity2022Percent,
				Integer foodInsecurity2022PercentRank,
				Double foodInsecurity2021Percent,
				Integer foodInsecurity2021PercentRank,
				Double medicaidEnrollmentTraditionalPercent,
				Integer medicaidEnrollmentTraditionalPercentRank,
				Integer medicaidEnrollmentTraditionalNumber,
				Integer medicaidEnrollmentTraditionalNumberRank,
				Double medicaidEnrollmentExpansionPercent,
				Integer medicaidEnrollmentExpansionPercentRank,
				Integer medicaidEnrollmentExpansionNumber,
				Integer medicaidEnrollmentExpansionNumberRank,
				Double uninsuredResidents2021Percent,
				Integer uninsuredResidents2021PercentRank,
				Double uninsuredResidents2020Percent,
				Integer uninsuredResidents2020PercentRank,
				Double drugOverdoseEmergenciesPerHundredThousand,
				Integer drugOverdoseEmergenciesPerHundredThousandRank,
				Integer drugOverdoseEmergenciesTotal,
				Integer drugOverdoseEmergenciesTotalRank,
				Double overDoseDeathsPerHundredThousand,
				Integer overDoseDeathsRank,
				Integer overDoseDeathsTotal,
				Integer overDoseDeathsTotalRank,
				Double propertyTaxRate,
				Integer propertyTaxRateRank,
				Double propertyTaxLevy,
				Integer propertyTaxLevyRank,
				Double propertyTaxLevyPerCapita,
				Integer propertyTaxLevyPerCapitaRank,
				Double propertyTaxLevyTotal,
				Integer propertyTaxLevyTotalRank,
				Double propertyValuationPerCapita,
				Integer propertyValuationPerCapitaRank,
				Double propertyValuationTotal,
				Integer propertyValuationTotalRank,
				Double realPropertyValuationDeferred,
				Integer realPropertyValuationDeferredRank,
				Double realPropertyValuationDeferredTotal,
				Integer realPropertyValuationDeferredTotalRank,
				Double localSalesTaxRate,
				Double areaSquareMiles){
		
			this.countyName = countyName;
			this.population = population;
			this.populationRank = populationRank;
			this.populationPercentOfState = populationPercentOfState;
			this.populationPercentOfStateRank = populationPercentOfStateRank;
			this.populationChangeSince2014 = populationChangeSince2014;
			this.populationChangeSince2014Rank = populationChangeSince2014Rank;
			this.populationChangeSince2014Number = populationChangeSince2014Number;
			this.populationChangeSince2014NumberRank = populationChangeSince2014NumberRank;
			this.medianAge = medianAge;
			this.medianAgeRank = medianAgeRank;
			this.medianAgeChangePercent = medianAgeChangePercent;
			this.under18ChangeRank = under18ChangeRank;
			this.under18Percent = under18Percent;
			this.under18Rank = under18Rank;
			this.under18ChangeSince2014Percent = under18ChangeSince2014Percent;
			this.under18ChangeSince2014Rank = under18ChangeSince2014Rank;
			this.over65in2024Percent = over65in2024Percent;
			this.over65in2024Rank = over65in2024Rank;
			this.over65ChangeSince2014Percent = over65ChangeSince2014Percent;
			this.over65ChangeSince2014Rank = over65ChangeSince2014Rank;
			this.veteranPercent = veteranPercent;
			this.veteranRank = veteranRank;
			this.veteranNumber = veteranNumber;
			this.veteranNumberRank = veteranNumberRank;
			this.agriculturalLandAcres = agriculturalLandAcres;
			this.agriculturalLandRank = agriculturalLandRank;
			this.agriculturalLandChangeSince2017Percent = agriculturalLandChangeSince2017Percent;
			this.agriculturalLandChangeSince2017Rank = agriculturalLandChangeSince2017Rank;
			this.numberOfFarms = numberOfFarms;
			this.numberOfFarmsRank = numberOfFarmsRank;
			this.numberOfFarmsChangeSince2017Percent = numberOfFarmsChangeSince2017Percent;
			this.numberOfFarmsChangeSince2017Rank = numberOfFarmsChangeSince2017Rank;
			this.broadbandInternetAccess2022Percent = broadbandInternetAccess2022Percent;
			this.broadbandInternetAccessPercent2022Rank = broadbandInternetAccessPercent2022Rank;
			this.broadbandInternetAccess2019Percent = broadbandInternetAccess2019Percent;
			this.broadbandInternetAccessPercent2019Rank = broadbandInternetAccessPercent2019Rank;
			this.computingDeviceAccess2022Percent = computingDeviceAccess2022Percent;
			this.computingDeviceAccess2022PercentRank = computingDeviceAccess2022PercentRank;
			this.computingDeviceAccess2019Percent = computingDeviceAccess2019Percent;
			this.computingDeviceAccess2019PercentRank = computingDeviceAccess2019PercentRank;
			this.childPovertyPercent = childPovertyPercent;
			this.childPovertyPercentRank = childPovertyPercentRank;
			this.childPovertyTotal = childPovertyTotal;
			this.childPovertyTotalRank = childPovertyTotalRank;
			this.averageWeeklyWage = averageWeeklyWage;
			this.averageWeeklyWageRank = averageWeeklyWageRank;
			this.averageWeeklyWageAsPercentofStateAverage = averageWeeklyWageAsPercentofStateAverage;
			this.averageWeeklyWageAsPercentofStateAverageRank = averageWeeklyWageAsPercentofStateAverageRank;
			this.perCapitaIncome = perCapitaIncome;
			this.perCapitaIncomeRank = perCapitaIncomeRank;
			this.perCapitaIncomeAsPercentofStatePCI = perCapitaIncomeAsPercentofStatePCI;
			this.perCapitaIncomeAsPercentofStatePCIRank = perCapitaIncomeAsPercentofStatePCIRank;
			this.gdp2022 = gdp2022;
			this.gdp2022Rank = gdp2022Rank;
			this.gdp2021 = gdp2021;
			this.gdp2021Rank = gdp2021Rank;
			this.preKEnrollment2023Percent = preKEnrollment2023Percent;
			this.preKEnrollment2023PercentRank = preKEnrollment2023PercentRank;
			this.preKEnrollment2022Percent = preKEnrollment2022Percent;
			this.preKEnrollment2022PercentRank = preKEnrollment2022PercentRank;
			this.k12FundingADM = k12FundingADM;
			this.k12FundingADMRank = k12FundingADMRank;
			this.k12Funding = k12Funding;
			this.k12FundingRank = k12FundingRank;
			this.youthOpportunity2022Percent = youthOpportunity2022Percent;
			this.youthOpportunity2022PercentRank = youthOpportunity2022PercentRank;
			this.youthOpportunity2019Percent = youthOpportunity2019Percent;
			this.youthOpportunity2019PercentRank = youthOpportunity2019PercentRank;
			this.adultsWithoutHSDiplomaPercent = adultsWithoutHSDiplomaPercent;
			this.adultsWithoutHSDiplomaPercentRank = adultsWithoutHSDiplomaPercentRank;
			this.adultsWithoutHSDiplomaTotal = adultsWithoutHSDiplomaTotal;
			this.adultsWithoutHSDiplomaTotalRank = adultsWithoutHSDiplomaTotalRank;
			this.educationalAttainmentPercent = educationalAttainmentPercent;
			this.educationalAttainmentPercentRank = educationalAttainmentPercentRank;
			this.educationalAttainmentNumber = educationalAttainmentNumber;
			this.educationalAttainmentGoal2030 = educationalAttainmentGoal2030;
			this.foodInsecurity2022Percent = foodInsecurity2022Percent;
			this.foodInsecurity2022PercentRank = foodInsecurity2022PercentRank;
			this.foodInsecurity2021Percent = foodInsecurity2021Percent;
			this.foodInsecurity2021PercentRank = foodInsecurity2021PercentRank;
			this.medicaidEnrollmentTraditionalPercent = medicaidEnrollmentTraditionalPercent;
			this.medicaidEnrollmentTraditionalPercentRank = medicaidEnrollmentTraditionalPercentRank;
			this.medicaidEnrollmentTraditionalNumber = medicaidEnrollmentTraditionalNumber;
			this.medicaidEnrollmentTraditionalNumberRank = medicaidEnrollmentTraditionalNumberRank;
			this.medicaidEnrollmentExpansionPercent = medicaidEnrollmentExpansionPercent;
			this.medicaidEnrollmentExpansionPercentRank = medicaidEnrollmentExpansionPercentRank;
			this.medicaidEnrollmentExpansionNumber = medicaidEnrollmentExpansionNumber;
			this.medicaidEnrollmentExpansionNumberRank = medicaidEnrollmentExpansionNumberRank;
			this.uninsuredResidents2021Percent = uninsuredResidents2021Percent;
			this.uninsuredResidents2021PercentRank = uninsuredResidents2021PercentRank;
			this.uninsuredResidents2020Percent = uninsuredResidents2020Percent;
			this.uninsuredResidents2020PercentRank = uninsuredResidents2020PercentRank;
			this.drugOverdoseEmergenciesPerHundredThousand = drugOverdoseEmergenciesPerHundredThousand;
			this.drugOverdoseEmergenciesPerHundredThousandRank = drugOverdoseEmergenciesPerHundredThousandRank;
			this.drugOverdoseEmergenciesTotal = drugOverdoseEmergenciesTotal;
			this.drugOverdoseEmergenciesTotalRank = drugOverdoseEmergenciesTotalRank;
			this.overDoseDeathsPerHundredThousand = overDoseDeathsPerHundredThousand;
			this.overDoseDeathsRank = overDoseDeathsRank;
			this.overDoseDeathsTotal = overDoseDeathsTotal;
			this.overDoseDeathsTotalRank = overDoseDeathsTotalRank;
			this.propertyTaxRate = propertyTaxRate;
			this.propertyTaxRateRank = propertyTaxRateRank;
			this.propertyTaxLevy = propertyTaxLevy;
			this.propertyTaxLevyRank = propertyTaxLevyRank;
			this.propertyTaxLevyPerCapita = propertyTaxLevyPerCapita;
			this.propertyTaxLevyPerCapitaRank = propertyTaxLevyPerCapitaRank;
			this.propertyTaxLevyTotal = propertyTaxLevyTotal;
			this.propertyTaxLevyTotalRank = propertyTaxLevyTotalRank;
			this.propertyValuationPerCapita = propertyValuationPerCapita;
			this.propertyValuationPerCapitaRank = propertyValuationPerCapitaRank;
			this.propertyValuationTotal = propertyValuationTotal;
			this.propertyValuationTotalRank = propertyValuationTotalRank;
			this.realPropertyValuationDeferred = realPropertyValuationDeferred;
			this.realPropertyValuationDeferredRank = realPropertyValuationDeferredRank;
			this.realPropertyValuationDeferredTotal = realPropertyValuationDeferredTotal;
			this.realPropertyValuationDeferredTotalRank = realPropertyValuationDeferredTotalRank;
			this.localSalesTaxRate = localSalesTaxRate;
			this.areaSquareMiles = areaSquareMiles;
		}
	}

	public static Integer parseIntegerOrNull(String s) {
		try {
			if (s == null || s.trim().isEmpty()) return null;
			return (int) Double.parseDouble(s.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Double parseDoubleOrNull(String s) {
		try {
			if (s == null || s.trim().isEmpty()) return null;
			return Double.parseDouble(s.trim().replace(",", "."));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}