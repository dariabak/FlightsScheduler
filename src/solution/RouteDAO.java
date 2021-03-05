package solution;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Route;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO {
	private List<Route> routes = new ArrayList<Route>();

	/**
	 * Finds all flights that depart on the specified day of the week
	 * 
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */
	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) {
		List<Route> routesByDayOfWeek = new ArrayList<Route>();
		for (Route r : routes) {
			if (r.getDayOfWeek().equals(dayOfWeek)) {
				routesByDayOfWeek.add(r);
			}
		}
		return routesByDayOfWeek;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific
	 * day of the week
	 * 
	 * @param airportCode the three letter code of the airport to search for, e.g.
	 *                    "MAN"
	 * @param dayOfWeek   the three letter day of the week code to searh for, e.g.
	 *                    "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		List<Route> routesByDepartureAirportAndDay = new ArrayList<Route>();
		for (Route r : routes) {
			if (r.getDepartureAirportCode().equals(airportCode)) {
				if (r.getDayOfWeek().equals(dayOfWeek)) {
					routesByDepartureAirportAndDay.add(r);
				}
			}
		}
		return routesByDepartureAirportAndDay;
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * 
	 * @param airportCode the three letter code of the airport to search for, e.g.
	 *                    "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) {
		List<Route> routesByDepartingAirport = new ArrayList<Route>();
		for (Route r : routes) {
			if (r.getDepartureAirportCode().equals(airportCode)) {
				routesByDepartingAirport.add(r);
			}
		}
		return routesByDepartingAirport;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * 
	 * @param date the date to search for
	 * @return A list of all routes that dpeart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		List<Route> routesByDate = new ArrayList<Route>();
		for (Route r : routes) {
			if (r.getDayOfWeek().equals(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.UK))) {
				routesByDate.add(r);
			}
		}
		return routesByDate;
	}

	/**
	 * Returns The full list of all currently loaded routes
	 * 
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		List<Route> cloned = new ArrayList<>(routes);
		return cloned;
	}

	/**
	 * Returns The number of routes currently loaded
	 * 
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		return routes.size();
	}

	/**
	 * Loads the route data from the specified file, adding them to the currently
	 * loaded routes Multiple calls to this function, perhaps on different files,
	 * would thus be cumulative
	 * 
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause"
	 *                              indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path arg0) throws DataLoadingException {
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = db.parse(arg0.toString());

			Element root = document.getDocumentElement();
			NodeList routeNodes = root.getElementsByTagName("Route");
			for (int i = 0; i < routeNodes.getLength(); i++) {
				Node routeNode = routeNodes.item(i);
				NodeList children = routeNode.getChildNodes();
				Route route = new Route();
				for (int j = 0; j < children.getLength(); j++) {
					Node childNode = children.item(j);
					if (childNode.getNodeName().equals("FlightNumber")) {
						route.setFlightNumber(Integer.parseInt(childNode.getChildNodes().item(0).getNodeValue()));
					} else if (childNode.getNodeName().equals("DayOfWeek")) {
						route.setDayOfWeek(childNode.getChildNodes().item(0).getNodeValue());
					} else if (childNode.getNodeName().equals("DepartureTime")) {
						route.setDepartureTime(LocalTime.parse(childNode.getChildNodes().item(0).getNodeValue()));
					} else if (childNode.getNodeName().equals("DepartureAirport")) {
						route.setDepartureAirport(childNode.getChildNodes().item(0).getNodeValue());
					} else if (childNode.getNodeName().equals("DepartureAirportIATACode")) {
						route.setDepartureAirportCode(childNode.getChildNodes().item(0).getNodeValue());
					} else if (childNode.getNodeName().equals("ArrivalTime")) {
						route.setArrivalTime(LocalTime.parse(childNode.getChildNodes().item(0).getNodeValue()));
					} else if (childNode.getNodeName().equals("ArrivalAirport")) {
						route.setArrivalAirport(childNode.getChildNodes().item(0).getNodeValue());
					} else if (childNode.getNodeName().equals("ArrivalAirportIATACode")) {
						route.setArrivalAirportCode(childNode.getChildNodes().item(0).getNodeValue());
					} else if (childNode.getNodeName().equals("Duration")) {
						route.setDuration(Duration.parse(childNode.getChildNodes().item(0).getNodeValue()));
					}
				}
				routes.add(route);
			}

		} catch (Exception e) {
			throw new DataLoadingException(e);
		}
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		routes.clear();

	}

}
