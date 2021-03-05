package solution;

import java.nio.file.Paths;
import java.time.LocalDate;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;

/**
 * This class allows you to run the code in your classes yourself, for testing
 * and development
 */
public class Main {

	public static void main(String[] args) {
		IAircraftDAO aircraft = new AircraftDAO();
		try {
			// Tells your Aircraft DAO to load this particular data file
			aircraft.loadAircraftData(Paths.get("./data/schedule_aircraft.csv"));

		} catch (DataLoadingException dle) {
			System.err.println("Error loading aircraft data");
			dle.printStackTrace();
		}

		ICrewDAO crew = new CrewDAO();

		try {
			crew.loadCrewData(Paths.get("./data/schedule_crew.json"));

		} catch (DataLoadingException dle) {
			System.err.println("Error loading crew data");
			dle.printStackTrace();
		}

		IRouteDAO route = new RouteDAO();

		try {
			route.loadRouteData(Paths.get(
					"/Users/daria/Desktop/advanced programming/AdvancedProgrammingAssessment1/data/schedule_routes.xml"));

		} catch (DataLoadingException dle) {
			System.err.println("Error loading route data");
			dle.printStackTrace();
		}

		IPassengerNumbersDAO passengerNumbers = new PassengerNumbersDAO();

		try {

			passengerNumbers.loadPassengerNumbersData(Paths.get("./data/schedule_passengers.db"));
		} catch (DataLoadingException dle) {
			dle.printStackTrace();
		}

		LocalDate start = LocalDate.of(2020, 7, 4);
		LocalDate end = LocalDate.of(2020, 7, 8);
		IScheduler scheduler = new Scheduler();
		scheduler.generateSchedule(aircraft, crew, route, passengerNumbers, start, end);
	}

}
