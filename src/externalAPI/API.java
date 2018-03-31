package externalAPI;

import java.util.List;
import entity.Event;

public interface API {

	public List<Event> search(double lat, double lon, String term);
	
}
