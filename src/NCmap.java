package src;
import java.util.ArrayList;
import java.util.HashMap;
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
		// check if counties match
		// binary search for statewide county
		int index = -1;
		County statewideCounty = null; 
		for (int i = 0; i < countyData.size(); i++) {
			if (countyData.get(i).countyName.equals("Statewide")) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			// remove statewide county from countyData
			statewideCounty = countyData.get(index);
			countyData.remove(index);
		}
		// check if counties match
		
		// visualize counties
		for (int i = 0; i < counties.size(); i++) {
			// data 
			County county = countyData.get(i);
			if (county.foodInsecurity == null) {
				county.toString();
				System.out.println("\n!!! Skipping " + county.countyName);
				continue;
			}
			// System.out.println(county.toString());
			double stat = county.foodInsecurity; 
			int colorValue = (int) (255 - (stat * 255 / 100));

			// map
			USCounty c = counties.get(i);
			c.setFillColor(new Color(colorValue, colorValue, colorValue));
			c.setStrokeColor(new Color(colorValue, colorValue, colorValue));
			// c.setGeoId(county.countyName);
		}

		System.out.println(statewideCounty.toString());
		USMap us_maps = new USMap(map_data);
		bridges.setDataStructure(us_maps);
		bridges.visualize();
	}

	public static List<County> dataFetcher(){
		String file = "src/ncData.csv";
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
					parseIntegerOrNull(values[1]), // population
					parseDoubleOrNull(values[2]), // populationChangeSince2014
					parseDoubleOrNull(values[3]), // medianAge
					parseIntegerOrNull(values[4]), // populationUnderAge18
					parseIntegerOrNull(values[5]), // populationAged65Plus
					parseIntegerOrNull(values[6]), // veteranPopulation
					parseDoubleOrNull(values[7]), // agriculturalLand
					parseIntegerOrNull(values[8]), // numberOfFarms
					parseDoubleOrNull(values[9]), // broadbandInternetAccess
					parseDoubleOrNull(values[10]), // computingDeviceAccess
					parseDoubleOrNull(values[11]), // childrenInPoverty
					parseDoubleOrNull(values[12]), // averageWeeklyWage
					parseDoubleOrNull(values[13]), // perCapitaIncome
					parseDoubleOrNull(values[14]), // grossDomesticProduct
					parseIntegerOrNull(values[15]), // ncPreKEnrollment
					parseDoubleOrNull(values[16]), // k12CurrentExpenseFunding
					parseDoubleOrNull(values[17]), // opportunityYouth
					parseDoubleOrNull(values[18]), // adultsWithoutHighSchoolDiploma
					parseDoubleOrNull(values[19]), // educationalAttainment
					parseDoubleOrNull(values[20]), // foodInsecurity
					parseDoubleOrNull(values[21]), // traditionalMedicaidEnrollment
					parseDoubleOrNull(values[22]), // medicaidExpansionEnrollment
					parseDoubleOrNull(values[23]), // uninsuredResidents
					parseDoubleOrNull(values[24]), // emergencyDeptVisitsForDrugOverdose
					parseDoubleOrNull(values[25]), // overdoseDeaths
					parseDoubleOrNull(values[26]), // propertyTaxRates
					parseDoubleOrNull(values[27]), // propertyTaxLevy
					parseDoubleOrNull(values[28]), // taxablePropertyValuation
					parseDoubleOrNull(values[29]), // presentUseValuation
					parseDoubleOrNull(values[30])  // localOptionSalesTaxes
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
		Integer population;
		Double populationChangeSince2014;
		Double medianAge;
		Integer populationUnderAge18;
		Integer populationAged65Plus;
		Integer veteranPopulation;
		Double agriculturalLand;
		Integer numberOfFarms;
		Double broadbandInternetAccess;
		Double computingDeviceAccess;
		Double childrenInPoverty;
		Double averageWeeklyWage;
		Double perCapitaIncome;
		Double grossDomesticProduct;
		Integer ncPreKEnrollment;
		Double k12CurrentExpenseFunding;
		Double opportunityYouth;
		Double adultsWithoutHighSchoolDiploma;
		Double educationalAttainment;
		Double foodInsecurity;
		Double traditionalMedicaidEnrollment;
		Double medicaidExpansionEnrollment;
		Double uninsuredResidents;
		Double emergencyDeptVisitsForDrugOverdose;
		Double overdoseDeaths;
		Double propertyTaxRates;
		Double propertyTaxLevy;
		Double taxablePropertyValuation;
		Double presentUseValuation;
		Double localOptionSalesTaxes;

		public County(String countyName, Integer population, Double populationChangeSince2014, Double medianAge,
					Integer populationUnderAge18, Integer populationAged65Plus, Integer veteranPopulation, Double agriculturalLand,
					Integer numberOfFarms, Double broadbandInternetAccess, Double computingDeviceAccess,
					Double childrenInPoverty, Double averageWeeklyWage, Double perCapitaIncome,
					Double grossDomesticProduct, Integer ncPreKEnrollment, Double k12CurrentExpenseFunding,
					Double opportunityYouth, Double adultsWithoutHighSchoolDiploma, Double educationalAttainment,
					Double foodInsecurity, Double traditionalMedicaidEnrollment, Double medicaidExpansionEnrollment,
					Double uninsuredResidents, Double emergencyDeptVisitsForDrugOverdose, Double overdoseDeaths,
					Double propertyTaxRates, Double propertyTaxLevy, Double taxablePropertyValuation,
					Double presentUseValuation, Double localOptionSalesTaxes) {

			this.countyName = countyName;
			this.population = population;
			this.populationChangeSince2014 = populationChangeSince2014;
			this.medianAge = medianAge;
			this.populationUnderAge18 = populationUnderAge18;
			this.populationAged65Plus = populationAged65Plus;
			this.veteranPopulation = veteranPopulation;
			this.agriculturalLand = agriculturalLand;
			this.numberOfFarms = numberOfFarms;
			this.broadbandInternetAccess = broadbandInternetAccess;
			this.computingDeviceAccess = computingDeviceAccess;
			this.childrenInPoverty = childrenInPoverty;
			this.averageWeeklyWage = averageWeeklyWage;
			this.perCapitaIncome = perCapitaIncome;
			this.grossDomesticProduct = grossDomesticProduct;
			this.ncPreKEnrollment = ncPreKEnrollment;
			this.k12CurrentExpenseFunding = k12CurrentExpenseFunding;
			this.opportunityYouth = opportunityYouth;
			this.adultsWithoutHighSchoolDiploma = adultsWithoutHighSchoolDiploma;
			this.educationalAttainment = educationalAttainment;
			this.foodInsecurity = foodInsecurity;
			this.traditionalMedicaidEnrollment = traditionalMedicaidEnrollment;
			this.medicaidExpansionEnrollment = medicaidExpansionEnrollment;
			this.uninsuredResidents = uninsuredResidents;
			this.emergencyDeptVisitsForDrugOverdose = emergencyDeptVisitsForDrugOverdose;
			this.overdoseDeaths = overdoseDeaths;
			this.propertyTaxRates = propertyTaxRates;
			this.propertyTaxLevy = propertyTaxLevy;
			this.taxablePropertyValuation = taxablePropertyValuation;
			this.presentUseValuation = presentUseValuation;
			this.localOptionSalesTaxes = localOptionSalesTaxes;
		}

		public String toString() {
			return "'" + countyName + '\'' +
					": population=" + population +
					", populationChangeSince2014=" + populationChangeSince2014 +
					", medianAge=" + medianAge +
					", populationUnderAge18=" + populationUnderAge18 +
					", populationAged65Plus=" + populationAged65Plus +
					", veteranPopulation=" + veteranPopulation +
					", agriculturalLand=" + agriculturalLand +
					", numberOfFarms=" + numberOfFarms +
					", broadbandInternetAccess=" + broadbandInternetAccess +
					", computingDeviceAccess=" + computingDeviceAccess +
					", childrenInPoverty=" + childrenInPoverty +
					", averageWeeklyWage=" + averageWeeklyWage +
					", perCapitaIncome=" + perCapitaIncome +
					", grossDomesticProduct=" + grossDomesticProduct +
					", ncPreKEnrollment=" + ncPreKEnrollment +
					", k12CurrentExpenseFunding=" + k12CurrentExpenseFunding +
					", opportunityYouth=" + opportunityYouth +
					", adultsWithoutHighSchoolDiploma=" + adultsWithoutHighSchoolDiploma +
					", educationalAttainment=" + educationalAttainment +
					", foodInsecurity=" + foodInsecurity +
					", traditionalMedicaidEnrollment=" + traditionalMedicaidEnrollment +
					", medicaidExpansionEnrollment=" + medicaidExpansionEnrollment +
					", uninsuredResidents=" + uninsuredResidents +
					", emergencyDeptVisitsForDrugOverdose=" + emergencyDeptVisitsForDrugOverdose +
					", overdoseDeaths=" + overdoseDeaths +
					", propertyTaxRates=" + propertyTaxRates +
					", propertyTaxLevy=" + propertyTaxLevy +
					", taxablePropertyValuation=" + taxablePropertyValuation +
					", presentUseValuation=" + presentUseValuation +
					", localOptionSalesTaxes=" + localOptionSalesTaxes +
				'}';
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