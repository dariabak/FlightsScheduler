package solution;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;

/**
 * The CrewDAO is responsible for loading data from JSON-based crew files It
 * contains various methods to help the scheduler find the right pilots and
 * cabin crew
 */
public class CrewDAO implements ICrewDAO {

	// The data structure we'll use to store the aircraft we've loaded
	private List<CabinCrew> cabinCrew = new ArrayList<>();
	private List<Pilot> pilots = new ArrayList<>();

	/**
	 * Loads the crew data from the specified file, adding them to the currently
	 * loaded crew Multiple calls to this function, perhaps on different files,
	 * would thus be cumulative
	 * 
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause"
	 *                              indicates the underlying exception
	 */
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
		try {
			// open a file
			BufferedReader br = Files.newBufferedReader(p);
			String json = "";
			String line = "";
			while ((line = br.readLine()) != null) {
				json = json + line;
			}
			JSONObject root = new JSONObject(json);
			JSONArray pilotsArray = root.getJSONArray("pilots");
			for (int i = 0; i < pilotsArray.length(); i++) {
				Pilot singlePilot = new Pilot();
				JSONObject pilot = pilotsArray.getJSONObject(i);
				singlePilot.setForename(pilot.getString("forename"));
				singlePilot.setSurname(pilot.getString("surname"));
				singlePilot.setHomeBase(pilot.getString("home_airport"));
				singlePilot.setRank(Pilot.Rank.valueOf(pilot.getString("rank")));
				JSONArray type = pilot.getJSONArray("type_ratings");
				for (int k = 0; k < type.length(); k++) {
					singlePilot.setQualifiedFor(type.getString(k));
				}
				pilots.add(singlePilot);
			}

			JSONArray cabinCrewArray = root.getJSONArray("cabincrew");
			for (int j = 0; j < cabinCrewArray.length(); j++) {
				CabinCrew crew = new CabinCrew();
				JSONObject cabinCrewObject = cabinCrewArray.getJSONObject(j);
				crew.setForename(cabinCrewObject.getString("forename"));
				crew.setSurname(cabinCrewObject.getString("surname"));
				crew.setHomeBase(cabinCrewObject.getString("home_airport"));
				JSONArray type = cabinCrewObject.getJSONArray("type_ratings");
				for (int t = 0; t < type.length(); t++) {
					crew.setQualifiedFor(type.getString(t));
				}
				cabinCrew.add(crew);

			}

		} catch (Exception e) {
			
			throw new DataLoadingException(e);
		}

	}

	/**
	 * Returns a list of all the cabin crew based at the airport with the specified
	 * airport code
	 * 
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified
	 *         airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		List<CabinCrew> cabinCrewByHomeBase = new ArrayList<CabinCrew>();
		for (CabinCrew c : cabinCrew) {
			if (c.getHomeBase().equals(airportCode)) {
				cabinCrewByHomeBase.add(c);
			}
		}
		return cabinCrewByHomeBase;
	}

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND
	 * qualified to fly a specific aircraft type
	 * 
	 * @param typeCode    the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND
	 *         qualified to fly a specific aircraft type
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<CabinCrew> cabinCrewByHomeBaseAndTypeRating = new ArrayList<>();
		for (CabinCrew cc : cabinCrew) {
			if (cc.getHomeBase().equals(airportCode)) {
				for (String s : cc.getTypeRatings()) {
					if (s.equals(typeCode)) {
						cabinCrewByHomeBaseAndTypeRating.add(cc);
					}
				}
			}
		}
		return cabinCrewByHomeBaseAndTypeRating;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to
	 * fly the specified type of plane
	 * 
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to
	 *         fly the specified type of plane
	 */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		List<CabinCrew> cabinCrewByTypeRating = new ArrayList<>();
		for (CabinCrew cc : cabinCrew) {
			for (String s : cc.getTypeRatings()) {
				if (s.equals(typeCode)) {
					cabinCrewByTypeRating.add(cc);
				}
			}
		}
		return cabinCrewByTypeRating;
	}

	/**
	 * Returns a list of all the pilots based at the airport with the specified
	 * airport code
	 * 
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified
	 *         airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		List<Pilot> pilotsByHomeBase = new ArrayList<>();
		for (Pilot p : pilots) {
			if (p.getHomeBase().equals(airportCode)) {
				pilotsByHomeBase.add(p);
			}
		}
		return pilotsByHomeBase;
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to
	 * fly a specific aircraft type
	 * 
	 * @param typeCode    the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to
	 *         fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<Pilot> pilotsByHomeBaseAndTypeRating = new ArrayList<>();
		for (Pilot p : pilots) {
			if (p.getHomeBase().equals(airportCode)) {
				for (String s : p.getTypeRatings()) {
					if (s.equals(typeCode)) {
						pilotsByHomeBaseAndTypeRating.add(p);
					}
				}
			}
		}
		return pilotsByHomeBaseAndTypeRating;
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly
	 * the specified type of plane
	 * 
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly
	 *         the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {
		List<Pilot> pilotsByTypeRating = new ArrayList<>();
		for (Pilot p : pilots) {
			if (p.isQualifiedFor(typeCode)) {
				pilotsByTypeRating.add(p);
			}
		}
		return pilotsByTypeRating;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * 
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() {
		List<CabinCrew> cloned = new ArrayList<>(cabinCrew);
		return cloned;
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * 
	 * @return a list of all the crew, regardless of type
	 */
	@Override
	public List<Crew> getAllCrew() {
		List<Crew> crew = new ArrayList<>();
		crew.addAll(pilots);
		crew.addAll(cabinCrew);
		return crew;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * 
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() {
		List<Pilot> cloned = new ArrayList<>(pilots);
		return cloned;
	}

	@Override
	public int getNumberOfCabinCrew() {
		// TODO Auto-generated method stub
		return cabinCrew.size();
	}

	/**
	 * Returns the number of pilots currently loaded
	 * 
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() {
		// TODO Auto-generated method stub
		return pilots.size();
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		pilots.clear();
		cabinCrew.clear();

	}

}
