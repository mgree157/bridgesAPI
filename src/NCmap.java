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

		String[] states = {"North Carolina"};
		ArrayList<USState> map_data = ds.getUSMapCountyData(states, true);
		USState nc = map_data.get(0);
		
		List<County> countyList = getNCountyData();

		// get county from countyList and set color and attributes
		for (Map.Entry<String,USCounty> e: nc.getCounties().entrySet()) {
			USCounty c = e.getValue();
			System.out.println(e.getKey() + " " + c.getCountyName());
			
			for (County county : countyList) {
				if (county.countyName.equals(c.getCountyName().split(",")[0])) {
					double stat = county.foodInsecurity;
					int colorValue = (int) (255 - (stat * 255 / 100));
					c.setFillColor(new Color(255, colorValue, colorValue));
					c.setStrokeColor(new Color(255, colorValue, colorValue));
					break;
				}
			}
		}
 
		// create a USMap object with the map data
		USMap us_maps = new USMap(map_data);

		bridges.setDataStructure(us_maps);
		bridges.visualize();
	}

	public static List<County> getNCountyData(){
		String file = "ncData.csv";
        String line;
		List<County> countyList = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while ((line = br.readLine()) != null) {
				if (line.startsWith("Unnamed") || line.startsWith("Demographics")) continue;

				String[] values = line.split(",");
				if (values.length < 31) {
					System.err.println("Skipping line due to insufficient data: " + line);
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
				countyList.add(nexCounty);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return countyList;
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
	}

	public static Integer parseIntegerOrNull(String s) {
		try {
			if (s == null || s.trim().isEmpty()) return null;
			// parse as double then cast to int (to handle decimals in int fields)
			return (int) Double.parseDouble(s.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Double parseDoubleOrNull(String s) {
		try {
			if (s == null || s.trim().isEmpty()) return null;
			// Replace commas with dots just in case
			return Double.parseDouble(s.trim().replace(",", "."));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}