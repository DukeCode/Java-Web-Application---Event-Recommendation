package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Event;

public class GeoRecommendation {
	public List<Event> recommendEvents(String userId, double lat, double lon) {
		DBConnection conn = DBConnectionFactory.getDBConnection();
		Set<String> favoriteEvents = conn.getFavoriteEventIds(userId); 
		Set<String> allCategories = new HashSet<>();
		for (String event : favoriteEvents) {
			allCategories.addAll(conn.getCategories(event)); 
		}

		allCategories.remove("Undefined"); // tune category set
		if (allCategories.isEmpty()) {
			allCategories.add("");
		}

		Set<Event> recommendedEvents = new HashSet<>(); 
		for (String category : allCategories) {
			List<Event> items = conn.searchEvents(userId, lat, lon, category); 
			recommendedEvents.addAll(items);
		}

		List<Event> filteredEvents = new ArrayList<>(); 
		for (Event item : recommendedEvents) {
			if (!favoriteEvents.contains(item.getEventId())) {
				filteredEvents.add(item);
			}
		}

		// rank based on distance
		Collections.sort(filteredEvents, new Comparator<Event>() {
			@Override
			public int compare(Event item1, Event item2) {
				double distance1 = getDistance(item1.getLatitude(), item1.getLongitude(), lat, lon);
				double distance2 = getDistance(item2.getLatitude(), item2.getLongitude(), lat, lon);
				// return the increasing order of distance.
				// use return (int)(distance1 - distance2) is a bad practice, easy to make mistake
				if (distance1 < distance2) {
					return -1;
				} 
				if (distance1 > distance2) {
					return 1;
				}
				return 0;
			}
		});

		return filteredEvents;
	}

	// Calculate the distances between two geolocations.
	// Source : http://andrew.hedges.name/experiments/haversine/
	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;
		double a = Math.sin(dlat / 2 / 180 * Math.PI) * Math.sin(dlat / 2 / 180 * Math.PI)
				+ Math.cos(lat1 / 180 * Math.PI) * Math.cos(lat2 / 180 * Math.PI) * Math.sin(dlon / 2 / 180 * Math.PI)
				* Math.sin(dlon / 2 / 180 * Math.PI);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		// Radius of earth in miles.
		double R = 3961;
		return R * c;
	}
}
