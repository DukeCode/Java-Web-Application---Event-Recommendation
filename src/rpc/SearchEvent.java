package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Event;
import algorithm.GeoRecommendation;

/**
 * Servlet implementation class SearchEvent
 * Use GET to handle search request
 * Java Servlet: Java Class to handle RPC on server side
 */
@WebServlet("/SearchEvent")
public class SearchEvent extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchEvent() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String term = request.getParameter("term"); // Term can be empty or null.
		
		DBConnection conn = DBConnectionFactory.getDBConnection();
		List<Event> events = conn.searchEvents(userId, lat, lon, term);
		List<JSONObject> list = new ArrayList<>();

		Set<String> favorite = conn.getFavoriteEventIds(userId);
		try {
			// append "favorite" if user have it in favorite list
			for (Event event : events) {
				JSONObject obj = event.toJSONObject();
				if (favorite != null) {
					obj.put("favorite", favorite.contains(event.getEventId()));
				}
				// show distance dynamically based on user's address
				if (!obj.isNull("latitude") && !obj.isNull("longitude")) {
					double distance = GeoRecommendation.getDistance(lat, lon, obj.getDouble("latitude"), obj.getDouble("longitude"));
					obj.put("distance", distance);
				} else {
					obj.put("distance", -1);
				}
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONArray array = new JSONArray(list);
		RpcHelper.writeJsonArray(response, array);
		conn.close();
	}

}
