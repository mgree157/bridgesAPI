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
import bridges.base.SLelement;

// This program illustrates how to access the data of the US map with state
// boundaries with different colors for states and its boundaries
// Here we select 3 states and draw them with different attributes with or 
// without counties

public class NCmap_normalized {
	public static void main(String[] args) throws Exception {

		Bridges bridges = new Bridges(2006, "mgree157", "581575557990");
		DataSource ds = bridges.getDataSource();
		bridges.setTitle("GDP vs. Property Valuation in North Carolina");
		bridges.setMapOverlay(true);
		
		// get bridges NC map structure
		String[] states = {"North Carolina"};
		ArrayList<USState> map_data = ds.getUSMapCountyData(states, true);
		USState nc = map_data.get(0);

		// get counties from bridges NC map
		List<USCounty> counties = new ArrayList<>();
		for (Map.Entry<String,USCounty> e: nc.getCounties().entrySet()) {
			USCounty c = e.getValue();
			counties.add(c);
		}
		// get NORAMLIZED county data from csv
		List<County> countyData = dataFetcher();

		// align counties from bridges and county data by alphabetizing both lists
        alignCounties(counties, countyData);
		
		Number figure = null;
		for (int i = 0; i < counties.size(); i++) {
			County county = countyData.get(i);
			//////////////////////////////////////////////////////////////////////////////////////////////////

			figure = county.perCapitaIncome; // <----------------- FEATURE TO VISUALIZE

			//////////////////////////////////////////////////////////////////////////////////////////////////
			// double minVal = min.numberOfFarms;
			// double maxVal = max.numberOfFarms;
			if (figure == null) {
				System.out.println("\n!!! Skipping " + county.countyName);
				continue;
			}
			
			int[] rgb = new int[3];
			rgb = getColor(figure.doubleValue());
			
			int r = rgb[0];
			int g = rgb[1];
			int b = rgb[2];
			

			// map
			USCounty c = counties.get(i);
			c.setFillColor(new Color(r, g, b));
			c.setStrokeColor(new Color(r, g, b));
		}
		
		USMap us_maps = new USMap(map_data);
		bridges.setMap(us_maps);
        bridges.setMapOverlay(true);
		bridges.setDataStructure(us_maps);
		bridges.setTitle("per capita income");
        bridges.visualize();

		for (int i = 0; i < counties.size(); i++) {
			County county = countyData.get(i);
			//////////////////////////////////////////////////////////////////////////////////////////////////

			figure = county.k12FundingADM; // <----------------- FEATURE TO VISUALIZE

			//////////////////////////////////////////////////////////////////////////////////////////////////
			// double minVal = min.numberOfFarms;
			// double maxVal = max.numberOfFarms;
			if (figure == null) {
				System.out.println("\n!!! Skipping " + county.countyName);
				continue;
			}
			
			int[] rgb = new int[3];
			rgb = getColor(figure.doubleValue());
			
			int r = rgb[0];
			int g = rgb[1];
			int b = rgb[2];
			
			// map
			USCounty c = counties.get(i);
			c.setFillColor(new Color(r, g, b));
			c.setStrokeColor(new Color(r, g, b));
		}
		USMap us_maps2 = new USMap(map_data);
		bridges.setMap(us_maps2);
        bridges.setMapOverlay(true);
		bridges.setDataStructure(us_maps2);
		bridges.setTitle("k12 funding ADM");
        bridges.visualize();
		
		for (int i = 0; i < counties.size(); i++) {
			County county = countyData.get(i);
			//////////////////////////////////////////////////////////////////////////////////////////////////

			figure = county.propertyValuationTotal; // <----------------- FEATURE TO VISUALIZE

			//////////////////////////////////////////////////////////////////////////////////////////////////
			// double minVal = min.numberOfFarms;
			// double maxVal = max.numberOfFarms;
			if (figure == null) {
				System.out.println("\n!!! Skipping " + county.countyName);
				continue;
			}
			
			int[] rgb = new int[3];
			rgb = getColor(figure.doubleValue());
			
			int r = rgb[0];
			int g = rgb[1];
			int b = rgb[2];

			// map
			USCounty c = counties.get(i);
			c.setFillColor(new Color(r, g, b));
			c.setStrokeColor(new Color(r, g, b));
		}
		
		USMap us_maps3 = new USMap(map_data);
		bridges.setMap(us_maps3);
        bridges.setMapOverlay(true);
		bridges.setDataStructure(us_maps3);
		bridges.setTitle("property valuation total");
        bridges.visualize();
	}

	public static int[] getColor(double value) {
		// Clamp and scale value to index 0–8
		String[] redScale = {
			"#fff5f0", "#fee0d2", "#fcbba1", "#fc9272", "#fb6a4a",
			"#ef3b2c", "#cb181d", "#a50f15", "#67000d"
		};

		int index = (int) Math.round(value * 8);
		if (index < 0) index = 0;
		if (index > 8) index = 8;

		String hex = redScale[index];

		// Convert hex to RGB
		int r = Integer.valueOf(hex.substring(1, 3), 16);
		int g = Integer.valueOf(hex.substring(3, 5), 16);
		int b = Integer.valueOf(hex.substring(5, 7), 16);

		return new int[]{r, g, b}; 
	}
    public static void alignCounties(List<USCounty> counties, List<County> countyData) {
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
        return;
    }

	public static List<County> dataFetcher(){
		String file = "src/Counties/Normalized-Table 1.csv";
        String line;
		List<County> countyData = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while ((line = br.readLine()) != null) {
				if (line.startsWith("County")) continue;

				String[] values = line.split(",");
				County nexCounty = new County(
					values[0], // countyName
					County.parseDoubleOrNull(values[1]), // population
					County.parseDoubleOrNull(values[2]), // populationRank
					County.parseDoubleOrNull(values[3]), // populationPercentOfState
					County.parseDoubleOrNull(values[4]), // populationPercentOfStateRank
					County.parseDoubleOrNull(values[5]), // populationChangeSince2014
					County.parseDoubleOrNull(values[6]), // populationChangeSince2014Rank
					County.parseDoubleOrNull(values[7]), // populationChangeSince2014Number
					County.parseDoubleOrNull(values[8]), // populationChangeSince2014NumberRank
					County.parseDoubleOrNull(values[9]), // medianAge
					County.parseDoubleOrNull(values[10]), // medianAgeRank
					County.parseDoubleOrNull(values[11]), // medianAgeChangePercent
					County.parseDoubleOrNull(values[12]), // under18ChangeRank
					County.parseDoubleOrNull(values[13]), // under18Percent
					County.parseDoubleOrNull(values[14]), // under18Rank
					County.parseDoubleOrNull(values[15]), // under18ChangeSince2014Percent
					County.parseDoubleOrNull(values[16]), // under18ChangeSince2014Rank
					County.parseDoubleOrNull(values[17]), // over65in2024Percent
					County.parseDoubleOrNull(values[18]), // over65in2024Rank
					County.parseDoubleOrNull(values[19]), // over65ChangeSince2014Percent
					County.parseDoubleOrNull(values[20]), // over65ChangeSince2014Rank
					County.parseDoubleOrNull(values[21]), // veteranPercent
					County.parseDoubleOrNull(values[22]), // veteranRank
					County.parseDoubleOrNull(values[23]), // veteranNumber
					County.parseDoubleOrNull(values[24]), // veteranNumberRank
					County.parseDoubleOrNull(values[25]), // agriculturalLandAcres
					County.parseDoubleOrNull(values[26]), // agriculturalLandRank
					County.parseDoubleOrNull(values[27]), // agriculturalLandChangeSince2017Percent
					County.parseDoubleOrNull(values[28]), // agriculturalLandChangeSince2017Rank
					County.parseDoubleOrNull(values[29]), // numberOfFarms
					County.parseDoubleOrNull(values[30]), // numberOfFarmsRank
					County.parseDoubleOrNull(values[31]), // numberOfFarmsChangeSince2017Percent
					County.parseDoubleOrNull(values[32]), // numberOfFarmsChangeSince2017Rank
					County.parseDoubleOrNull(values[33]), // broadbandInternetAccess2022Percent
					County.parseDoubleOrNull(values[34]), // broadbandInternetAccessPercent2022Rank
					County.parseDoubleOrNull(values[35]), // broadbandInternetAccess2019Percent
					County.parseDoubleOrNull(values[36]), // broadbandInternetAccessPercent2019Rank
					County.parseDoubleOrNull(values[37]), // computingDeviceAccess2022Percent
					County.parseDoubleOrNull(values[38]), // computingDeviceAccess2022PercentRank
					County.parseDoubleOrNull(values[39]), // computingDeviceAccess2019Percent
					County.parseDoubleOrNull(values[40]), // computingDeviceAccess2019PercentRank
					County.parseDoubleOrNull(values[41]), // childPovertyPercent
					County.parseDoubleOrNull(values[42]), // childPovertyPercentRank
					County.parseDoubleOrNull(values[43]), // childPovertyTotal
					County.parseDoubleOrNull(values[44]), // childPovertyTotalRank
					County.parseDoubleOrNull(values[45]), // averageWeeklyWage
					County.parseDoubleOrNull(values[46]), // averageWeeklyWageRank
					County.parseDoubleOrNull(values[47]), // averageWeeklyWageAsPercentofStateAverage
					County.parseDoubleOrNull(values[48]), // averageWeeklyWageAsPercentofStateAverageRank
					County.parseDoubleOrNull(values[49]), // perCapitaIncome
					County.parseDoubleOrNull(values[50]), // perCapitaIncomeRank
					County.parseDoubleOrNull(values[51]), // perCapitaIncomeAsPercentofStatePCI
					County.parseDoubleOrNull(values[52]), // perCapitaIncomeAsPercentofStatePCIRank
					County.parseDoubleOrNull(values[53]), // gdp2022
					County.parseDoubleOrNull(values[54]), // gdp2022Rank
					County.parseDoubleOrNull(values[55]), // gdp2021
					County.parseDoubleOrNull(values[56]), // gdp2021Rank
					County.parseDoubleOrNull(values[57]), // preKEnrollment2023Percent
					County.parseDoubleOrNull(values[58]), // preKEnrollment2023PercentRank
					County.parseDoubleOrNull(values[59]), // preKEnrollment2022Percent
					County.parseDoubleOrNull(values[60]), // preKEnrollment2022PercentRank
					County.parseDoubleOrNull(values[61]), // k12FundingADM
					County.parseDoubleOrNull(values[62]), // k12FundingADMRank
					County.parseDoubleOrNull(values[63]), // k12Funding
					County.parseDoubleOrNull(values[64]), // k12FundingRank
					County.parseDoubleOrNull(values[65]), // youthOpportunity2022Percent
					County.parseDoubleOrNull(values[66]), // youthOpportunity2022PercentRank
					County.parseDoubleOrNull(values[67]), // youthOpportunity2019Percent
					County.parseDoubleOrNull(values[68]), // youthOpportunity2019PercentRank
					County.parseDoubleOrNull(values[69]), // adultsWithoutHSDiplomaPercent
					County.parseDoubleOrNull(values[70]), // adultsWithoutHSDiplomaPercentRank
					County.parseDoubleOrNull(values[71]), // adultsWithoutHSDiplomaTotal
					County.parseDoubleOrNull(values[72]), // adultsWithoutHSDiplomaTotalRank
					County.parseDoubleOrNull(values[73]), // educationalAttainmentPercent
					County.parseDoubleOrNull(values[74]), // educationalAttainmentPercentRank
					County.parseDoubleOrNull(values[75]), // educationalAttainmentNumber
					County.parseDoubleOrNull(values[76]), // educationalAttainmentGoal2030
					County.parseDoubleOrNull(values[77]), // foodInsecurity2022Percent
					County.parseDoubleOrNull(values[78]), // foodInsecurity2022PercentRank
					County.parseDoubleOrNull(values[79]), // foodInsecurity2021Percent
					County.parseDoubleOrNull(values[80]), // foodInsecurity2021PercentRank
					County.parseDoubleOrNull(values[81]), // medicaidEnrollmentTraditionalPercent
					County.parseDoubleOrNull(values[82]), // medicaidEnrollmentTraditionalPercentRank
					County.parseDoubleOrNull(values[83]), // medicaidEnrollmentTraditionalNumber
					County.parseDoubleOrNull(values[84]), // medicaidEnrollmentTraditionalNumberRank
					County.parseDoubleOrNull(values[85]), // medicaidEnrollmentExpansionPercent
					County.parseDoubleOrNull(values[86]), // medicaidEnrollmentExpansionPercentRank
					County.parseDoubleOrNull(values[87]), // medicaidEnrollmentExpansionNumber
					County.parseDoubleOrNull(values[88]), // medicaidEnrollmentExpansionNumberRank
					County.parseDoubleOrNull(values[89]), // uninsuredResidents2021Percent
					County.parseDoubleOrNull(values[90]), // uninsuredResidents2021PercentRank
					County.parseDoubleOrNull(values[91]), // uninsuredResidents2020Percent
					County.parseDoubleOrNull(values[92]), // uninsuredResidents2020PercentRank
					County.parseDoubleOrNull(values[93]), // drugOverdoseEmergenciesPerHundredThousand
					County.parseDoubleOrNull(values[94]), // drugOverdoseEmergenciesPerHundredThousandRank
					County.parseDoubleOrNull(values[95]), // drugOverdoseEmergenciesTotal
					County.parseDoubleOrNull(values[96]), // drugOverdoseEmergenciesTotalRank
					County.parseDoubleOrNull(values[97]), // overDoseDeathsPerHundredThousand
					County.parseDoubleOrNull(values[98]), // overDoseDeathsRank
					County.parseDoubleOrNull(values[99]), // overDoseDeathsTotal
					County.parseDoubleOrNull(values[100]), // overDoseDeathsTotalRank
					County.parseDoubleOrNull(values[101]), // propertyTaxRate
					County.parseDoubleOrNull(values[102]), // propertyTaxRateRank
					County.parseDoubleOrNull(values[103]), // propertyTaxLevy
					County.parseDoubleOrNull(values[104]), // propertyTaxLevyRank
					County.parseDoubleOrNull(values[105]), // propertyTaxLevyPerCapita
					County.parseDoubleOrNull(values[106]), // propertyTaxLevyPerCapitaRank
					County.parseDoubleOrNull(values[107]), // propertyTaxLevyTotal
					County.parseDoubleOrNull(values[108]), // propertyTaxLevyTotalRank
					County.parseDoubleOrNull(values[109]), // propertyValuationPerCapita
					County.parseDoubleOrNull(values[110]), // propertyValuationPerCapitaRank
					County.parseDoubleOrNull(values[111]), // propertyValuationTotal
					County.parseDoubleOrNull(values[112]), // propertyValuationTotalRank
					County.parseDoubleOrNull(values[113]), // realPropertyValuationDeferred
					County.parseDoubleOrNull(values[114]), // realPropertyValuationDeferredRank
					County.parseDoubleOrNull(values[115]), // realPropertyValuationDeferredTotal
					County.parseDoubleOrNull(values[116]), // realPropertyValuationDeferredTotalRank
					County.parseDoubleOrNull(values[117]), // localSalesTaxRate
					County.parseDoubleOrNull(values[118]), // areaSquareMiles
					County.parseDoubleOrNull(values[119]), // lat
					County.parseDoubleOrNull(values[120]), // lng
					County.parseDoubleOrNull(values[121]), // demVotePercent2000
					County.parseDoubleOrNull(values[122]), // repVotePercent2000
					County.parseDoubleOrNull(values[123]), // demVotePercent2004
					County.parseDoubleOrNull(values[124]), // repVotePercent2004
					County.parseDoubleOrNull(values[125]), // demVotePercent2008
					County.parseDoubleOrNull(values[126]), // repVotePercent2008
					County.parseDoubleOrNull(values[127]), // demVotePercent2012
					County.parseDoubleOrNull(values[128]), // repVotePercent2012
					County.parseDoubleOrNull(values[129]), // demVotePercent2016
					County.parseDoubleOrNull(values[130]), // repVotePercent2016
					County.parseDoubleOrNull(values[131]), // demVotePercent2020
					County.parseDoubleOrNull(values[132]), // repVotePercent2020
					County.parseDoubleOrNull(values[133]), // demVotePercent2024
					County.parseDoubleOrNull(values[134])  // repVotePercent2024
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
		Double populationRank;
		Double populationPercentOfState;
		Double populationPercentOfStateRank;
		Double populationChangeSince2014;
		Double populationChangeSince2014Rank;
		Double populationChangeSince2014Number;
		Double populationChangeSince2014NumberRank;

		Double medianAge;
		Double medianAgeRank;
		Double medianAgeChangePercent;

		Double under18ChangeRank;
		Double under18Percent;
		Double under18Rank;
		Double under18ChangeSince2014Percent;
		Double under18ChangeSince2014Rank;

		Double over65in2024Percent;
		Double over65in2024Rank;
		Double over65ChangeSince2014Percent;
		Double over65ChangeSince2014Rank;

		Double veteranPercent;
		Double veteranRank;
		Double veteranNumber;
		Double veteranNumberRank;

		Double agriculturalLandAcres;
		Double agriculturalLandRank;
		Double agriculturalLandChangeSince2017Percent;
		Double agriculturalLandChangeSince2017Rank;

		Double numberOfFarms;
		Double numberOfFarmsRank;
		Double numberOfFarmsChangeSince2017Percent;
		Double numberOfFarmsChangeSince2017Rank;

		Double broadbandInternetAccess2022Percent;
		Double broadbandInternetAccessPercent2022Rank;
		Double broadbandInternetAccess2019Percent;
		Double broadbandInternetAccessPercent2019Rank;

		Double computingDeviceAccess2022Percent;
		Double computingDeviceAccess2022PercentRank;
		Double computingDeviceAccess2019Percent;
		Double computingDeviceAccess2019PercentRank;

		Double childPovertyPercent;
		Double childPovertyPercentRank;
		Double childPovertyTotal;
		Double childPovertyTotalRank;

		Double averageWeeklyWage;
		Double averageWeeklyWageRank;
		Double averageWeeklyWageAsPercentofStateAverage;
		Double averageWeeklyWageAsPercentofStateAverageRank;

		Double perCapitaIncome;
		Double perCapitaIncomeRank;
		Double perCapitaIncomeAsPercentofStatePCI;
		Double perCapitaIncomeAsPercentofStatePCIRank;

		Double gdp2022;
		Double gdp2022Rank;
		Double gdp2021;
		Double gdp2021Rank;

		Double preKEnrollment2023Percent;
		Double preKEnrollment2023PercentRank;
		Double preKEnrollment2022Percent;
		Double preKEnrollment2022PercentRank;

		Double k12FundingADM;
		Double k12FundingADMRank;
		Double k12Funding;
		Double k12FundingRank;

		Double youthOpportunity2022Percent;
		Double youthOpportunity2022PercentRank;
		Double youthOpportunity2019Percent;
		Double youthOpportunity2019PercentRank;

		Double adultsWithoutHSDiplomaPercent;
		Double adultsWithoutHSDiplomaPercentRank;
		Double adultsWithoutHSDiplomaTotal;
		Double adultsWithoutHSDiplomaTotalRank;

		Double educationalAttainmentPercent;
		Double educationalAttainmentPercentRank;
		Double educationalAttainmentNumber;
		Double educationalAttainmentGoal2030;

		Double foodInsecurity2022Percent;
		Double foodInsecurity2022PercentRank;
		Double foodInsecurity2021Percent;
		Double foodInsecurity2021PercentRank;

		Double medicaidEnrollmentTraditionalPercent;
		Double medicaidEnrollmentTraditionalPercentRank;
		Double medicaidEnrollmentTraditionalNumber;
		Double medicaidEnrollmentTraditionalNumberRank;

		Double medicaidEnrollmentExpansionPercent;
		Double medicaidEnrollmentExpansionPercentRank;
		Double medicaidEnrollmentExpansionNumber;
		Double medicaidEnrollmentExpansionNumberRank;

		Double uninsuredResidents2021Percent;
		Double uninsuredResidents2021PercentRank;
		Double uninsuredResidents2020Percent;
		Double uninsuredResidents2020PercentRank;

		Double drugOverdoseEmergenciesPerHundredThousand;
		Double drugOverdoseEmergenciesPerHundredThousandRank;
		Double drugOverdoseEmergenciesTotal;
		Double drugOverdoseEmergenciesTotalRank;

		Double overDoseDeathsPerHundredThousand;
		Double overDoseDeathsRank;
		Double overDoseDeathsTotal;
		Double overDoseDeathsTotalRank;

		Double propertyTaxRate;
		Double propertyTaxRateRank;
		Double propertyTaxLevy;
		Double propertyTaxLevyRank;
		Double propertyTaxLevyPerCapita;
		Double propertyTaxLevyPerCapitaRank;
		Double propertyTaxLevyTotal;
		Double propertyTaxLevyTotalRank;

		Double propertyValuationPerCapita;
		Double propertyValuationPerCapitaRank;
		Double propertyValuationTotal;
		Double propertyValuationTotalRank;

		Double realPropertyValuationDeferred;
		Double realPropertyValuationDeferredRank;
		Double realPropertyValuationDeferredTotal;
		Double realPropertyValuationDeferredTotalRank;

		Double localSalesTaxRate;
		Double areaSquareMiles;

		Double latitude;
		Double longitude;
		// demVotePercent2000	repVotePercent2000	demVotePercent2004	repVotePercent2004	demVotePercent2008	repVotePercent2008	demVotePercent2012	repVotePercent2012	demVotePercent2016	repVotePercent2016	demVotePercent2020	repVotePercent2020	demVotePercent2024	repVotePercent2024
		Double demVotePercent2000;
		Double repVotePercent2000;
		Double demVotePercent2004;
		Double repVotePercent2004;
		Double demVotePercent2008;
		Double repVotePercent2008;
		Double demVotePercent2012;
		Double repVotePercent2012;
		Double demVotePercent2016;
		Double repVotePercent2016;
		Double demVotePercent2020;
		Double repVotePercent2020;
		Double demVotePercent2024;
		Double repVotePercent2024;

		public County(
				String countyName,
				Double population,
				Double populationRank,
				Double populationPercentOfState,
				Double populationPercentOfStateRank,
				Double populationChangeSince2014,
				Double populationChangeSince2014Rank,
				Double populationChangeSince2014Number,
				Double populationChangeSince2014NumberRank,
				Double medianAge,
				Double medianAgeRank,
				Double medianAgeChangePercent,
				Double under18ChangeRank,
				Double under18Percent,
				Double under18Rank,
				Double under18ChangeSince2014Percent,
				Double under18ChangeSince2014Rank,
				Double over65in2024Percent,
				Double over65in2024Rank,
				Double over65ChangeSince2014Percent,
				Double over65ChangeSince2014Rank,
				Double veteranPercent,
				Double veteranRank,
				Double veteranNumber,
				Double veteranNumberRank,
				Double agriculturalLandAcres,
				Double agriculturalLandRank,
				Double agriculturalLandChangeSince2017Percent,
				Double agriculturalLandChangeSince2017Rank,
				Double numberOfFarms,
				Double numberOfFarmsRank,
				Double numberOfFarmsChangeSince2017Percent,
				Double numberOfFarmsChangeSince2017Rank,
				Double broadbandInternetAccess2022Percent,
				Double broadbandInternetAccessPercent2022Rank,
				Double broadbandInternetAccess2019Percent,
				Double broadbandInternetAccessPercent2019Rank,
				Double computingDeviceAccess2022Percent,
				Double computingDeviceAccess2022PercentRank,
				Double computingDeviceAccess2019Percent,
				Double computingDeviceAccess2019PercentRank,
				Double childPovertyPercent,
				Double childPovertyPercentRank,
				Double childPovertyTotal,
				Double childPovertyTotalRank,
				Double averageWeeklyWage,
				Double averageWeeklyWageRank,
				Double averageWeeklyWageAsPercentofStateAverage,
				Double averageWeeklyWageAsPercentofStateAverageRank,
				Double perCapitaIncome,
				Double perCapitaIncomeRank,
				Double perCapitaIncomeAsPercentofStatePCI,
				Double perCapitaIncomeAsPercentofStatePCIRank,
				Double gdp2022,
				Double gdp2022Rank,
				Double gdp2021,
				Double gdp2021Rank,
				Double preKEnrollment2023Percent,
				Double preKEnrollment2023PercentRank,
				Double preKEnrollment2022Percent,
				Double preKEnrollment2022PercentRank,
				Double k12FundingADM,
				Double k12FundingADMRank,
				Double k12Funding,
				Double k12FundingRank,
				Double youthOpportunity2022Percent,
				Double youthOpportunity2022PercentRank,
				Double youthOpportunity2019Percent,
				Double youthOpportunity2019PercentRank,
				Double adultsWithoutHSDiplomaPercent,
				Double adultsWithoutHSDiplomaPercentRank,
				Double adultsWithoutHSDiplomaTotal,
				Double adultsWithoutHSDiplomaTotalRank,
				Double educationalAttainmentPercent,
				Double educationalAttainmentPercentRank,
				Double educationalAttainmentNumber,
				Double educationalAttainmentGoal2030,
				Double foodInsecurity2022Percent,
				Double foodInsecurity2022PercentRank,
				Double foodInsecurity2021Percent,
				Double foodInsecurity2021PercentRank,
				Double medicaidEnrollmentTraditionalPercent,
				Double medicaidEnrollmentTraditionalPercentRank,
				Double medicaidEnrollmentTraditionalNumber,
				Double medicaidEnrollmentTraditionalNumberRank,
				Double medicaidEnrollmentExpansionPercent,
				Double medicaidEnrollmentExpansionPercentRank,
				Double medicaidEnrollmentExpansionNumber,
				Double medicaidEnrollmentExpansionNumberRank,
				Double uninsuredResidents2021Percent,
				Double uninsuredResidents2021PercentRank,
				Double uninsuredResidents2020Percent,
				Double uninsuredResidents2020PercentRank,
				Double drugOverdoseEmergenciesPerHundredThousand,
				Double drugOverdoseEmergenciesPerHundredThousandRank,
				Double drugOverdoseEmergenciesTotal,
				Double drugOverdoseEmergenciesTotalRank,
				Double overDoseDeathsPerHundredThousand,
				Double overDoseDeathsRank,
				Double overDoseDeathsTotal,
				Double overDoseDeathsTotalRank,
				Double propertyTaxRate,
				Double propertyTaxRateRank,
				Double propertyTaxLevy,
				Double propertyTaxLevyRank,
				Double propertyTaxLevyPerCapita,
				Double propertyTaxLevyPerCapitaRank,
				Double propertyTaxLevyTotal,
				Double propertyTaxLevyTotalRank,
				Double propertyValuationPerCapita,
				Double propertyValuationPerCapitaRank,
				Double propertyValuationTotal,
				Double propertyValuationTotalRank,
				Double realPropertyValuationDeferred,
				Double realPropertyValuationDeferredRank,
				Double realPropertyValuationDeferredTotal,
				Double realPropertyValuationDeferredTotalRank,
				Double localSalesTaxRate,
				Double areaSquareMiles,
				Double latitude,
				Double longitude,
				Double demVotePercent2000,
				Double repVotePercent2000,
				Double demVotePercent2004,
				Double repVotePercent2004,
				Double demVotePercent2008,
				Double repVotePercent2008,
				Double demVotePercent2012,
				Double repVotePercent2012,
				Double demVotePercent2016,
				Double repVotePercent2016,
				Double demVotePercent2020,
				Double repVotePercent2020,
				Double demVotePercent2024,
				Double repVotePercent2024) {
				
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
			this.latitude = latitude;
			this.longitude = longitude;
			this.demVotePercent2000 = demVotePercent2000;
			this.repVotePercent2000 = repVotePercent2000;
			this.demVotePercent2004 = demVotePercent2004;
			this.repVotePercent2004 = repVotePercent2004;
			this.demVotePercent2008 = demVotePercent2008;
			this.repVotePercent2008 = repVotePercent2008;
			this.demVotePercent2012 = demVotePercent2012;
			this.repVotePercent2012 = repVotePercent2012;
			this.demVotePercent2016 = demVotePercent2016;
			this.repVotePercent2016 = repVotePercent2016;
			this.demVotePercent2020 = demVotePercent2020;
			this.repVotePercent2020 = repVotePercent2020;
			this.demVotePercent2024 = demVotePercent2024;
			this.repVotePercent2024 = repVotePercent2024;
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
}