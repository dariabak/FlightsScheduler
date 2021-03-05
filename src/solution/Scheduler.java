package solution;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.Pilot;
import baseclasses.QualityScoreCalculator;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;
import baseclasses.qualitypointscalculators.MonthlyWorkingHoursCalculator;
import baseclasses.qualitypointscalculators.RestAfterLandingWrongAirportCalculator;

public class Scheduler implements IScheduler {
	Random r = new Random();
	List<FlightInfo> flights;

	@Override
	public Schedule generateSchedule(IAircraftDAO arg0, ICrewDAO arg1, IRouteDAO arg2, IPassengerNumbersDAO arg3,
			LocalDate arg4, LocalDate arg5) {
		Schedule bestSchedule = null;
		long bestSchedulePoints = 999999999999l;
		HashMap<Aircraft, String> aircraftsLocation = new HashMap<Aircraft, String>();
		List<Aircraft> allAircrafts = arg0.getAllAircraft();

		for (Aircraft a : allAircrafts) {
			aircraftsLocation.put(a, a.getStartingPosition());
		}

		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < 1000 * 100) {
			Schedule schedule = new Schedule(arg2, arg4, arg5);
			flights = schedule.getRemainingAllocations();
			for (FlightInfo flight : flights) {
				try {
					boolean aircraftAllocated = false;
					Aircraft aircraft = null;
					List<Aircraft> aircrafts = new ArrayList<>();
					for (Aircraft key : aircraftsLocation.keySet()) {
						if (aircraftsLocation.get(key).equals(flight.getFlight().getDepartureAirportCode())) {
							aircrafts.add(key);
						}
					}

					while (!aircraftAllocated) {
						if (!aircrafts.isEmpty()) {
							aircraft = aircrafts.get(r.nextInt(aircrafts.size()));
							if (!schedule.hasConflict(aircraft, flight)) {
								schedule.allocateAircraftTo(aircraft, flight);
								aircraftsLocation.put(aircraft, flight.getFlight().getArrivalAirportCode());
								aircrafts.remove(aircraft);
								aircraftAllocated = true;
							} else {
								aircrafts.remove(aircraft);
							}
						} else {
							aircraft = allAircrafts.get(r.nextInt(allAircrafts.size()));
							if (!schedule.hasConflict(aircraft, flight)) {
								schedule.allocateAircraftTo(aircraft, flight);
								aircraftsLocation.put(aircraft, flight.getFlight().getArrivalAirportCode());
								aircraftAllocated = true;
							}
						}
					}

					List<CabinCrew> cabinCrew = arg1.findCabinCrewByTypeRating(aircraft.getTypeCode());
					int t = 0;
					while (t != aircraft.getCabinCrewRequired()) {
						CabinCrew crew = null;
						if (!cabinCrew.isEmpty()) {
							crew = cabinCrew.get(r.nextInt(cabinCrew.size()));
							if (!schedule.hasConflict(crew, flight)) {
								schedule.allocateCabinCrewTo(crew, flight);
								cabinCrew.remove(crew);
								t += 1;
							} else {
								cabinCrew.remove(crew);
							}
						} else {
							List<CabinCrew> restCrew = arg1.getAllCabinCrew();
							CabinCrew crew2 = restCrew.get(r.nextInt(restCrew.size()));
							if (!schedule.hasConflict(crew2, flight)
									&& !schedule.getCabinCrewOf(flight).contains(crew2)) {
								schedule.allocateCabinCrewTo(crew2, flight);
								t += 1;
							}
						}
					}

					boolean captainAllocated = false;
					List<Pilot> pilots = arg1.findPilotsByTypeRating(aircraft.getTypeCode());
					Pilot captain = null;
					while (!captainAllocated) {
						if (!pilots.isEmpty()) {
							captain = pilots.get(r.nextInt(pilots.size()));
							if (!schedule.hasConflict(captain, flight)
									&& captain.getRank().compareTo(Pilot.Rank.CAPTAIN) == 0) {
								schedule.allocateCaptainTo(captain, flight);
								pilots.remove(captain);
								captainAllocated = true;

							} else {
								pilots.remove(captain);
							}
						} else {
							List<Pilot> restPilots = arg1.getAllPilots();
							captain = restPilots.get(r.nextInt(restPilots.size()));
							if (!schedule.hasConflict(captain, flight)) {
								schedule.allocateCaptainTo(captain, flight);
								captainAllocated = true;
							}
						}

					}

					boolean pilotAllocated = false;
					Pilot pilot = null;
					List<Pilot> pilots2 = arg1.findPilotsByTypeRating(aircraft.getTypeCode());
					while (!pilotAllocated) {
						if (!pilots2.isEmpty()) {
							pilot = pilots2.get(r.nextInt(pilots2.size()));
							if (!schedule.hasConflict(pilot, flight) && !pilot.equals(captain)
									&& pilot.getRank().compareTo(Pilot.Rank.FIRST_OFFICER) == 0) {
								schedule.allocateFirstOfficerTo(pilot, flight);
								pilots2.remove(pilot);
								pilotAllocated = true;
							} else {
								pilots2.remove(pilot);
							}
						} else {
							List<Pilot> restPilots = arg1.getAllPilots();
							pilot = restPilots.get(r.nextInt(restPilots.size()));
							if (!schedule.hasConflict(pilot, flight) && !pilot.equals(captain)) {
								schedule.allocateFirstOfficerTo(pilot, flight);
								pilotAllocated = true;
							}
						}

					}
					schedule.completeAllocationFor(flight);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			QualityScoreCalculator calculator = new QualityScoreCalculator(arg0, arg1, arg3, schedule);
			long score = calculator.calculateQualityScore();
			if (score < bestSchedulePoints) {
				bestSchedule = schedule;
				bestSchedulePoints = score;
			}

			System.out.println(bestSchedulePoints);
		}
		return bestSchedule;
	}

	@Override
	public void setSchedulerRunner(SchedulerRunner arg0) {

	}

	@Override
	public void stop() {

	}
}
