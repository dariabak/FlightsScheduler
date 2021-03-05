package solution;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;

import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {
	private HashMap<HashMap<String, Integer>, Integer> data = new HashMap<HashMap<String, Integer>, Integer>();

	/**
	 * Returns the number of passenger number entries in the cache
	 * 
	 * @return the number of passenger number entries in the cache
	 */
	@Override
	public int getNumberOfEntries() {
		return data.size();
	}

	/**
	 * Returns the predicted number of passengers for a given flight on a given
	 * date, or -1 if no data available
	 * 
	 * @param flightNumber The flight number of the flight to check for
	 * @param date         the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) {
		int passengerNumbers = 0;
		HashMap<String, Integer> key = new HashMap<String, Integer>();
		key.put(date.toString(), flightNumber);
		if (data.containsKey(key)) {
			passengerNumbers = data.get(key);
		} else {
			passengerNumbers = -1;
		}

		return passengerNumbers;
	}

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a
	 * cache for future calls to getPassengerNumbersFor() Multiple calls to this
	 * method are additive, but flight numbers/dates previously cached will be
	 * overwritten The cache can be reset by calling reset()
	 * 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	public void loadPassengerNumbersData(Path p) throws DataLoadingException {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + p.toString());
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM PassengerNumbers;");

			while (rs.next()) {
				HashMap<String, Integer> key = new HashMap<String, Integer>();
				key.put(rs.getString("Date"), rs.getInt("FlightNumber"));
				data.put(key, rs.getInt("LoadEstimate"));

			}
			s.close();

		} catch (Exception e) {
			throw new DataLoadingException(e);
		}
	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
		data.clear();

	}

}
