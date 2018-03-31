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
import org.json.JSONException;
import org.json.JSONObject;

import algorithm.GeoRecommendation;
import db.DBConnection;
import db.DBConnectionFactory;
import entity.Event;

/**
 * Servlet implementation class EventHistory
 */
@WebServlet("/EventHistory")
public class EventHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EventHistory() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		JSONArray array = new JSONArray();

		DBConnection conn = DBConnectionFactory.getDBConnection();
		Set<Event> events = conn.getFavoriteEvents(userId);
		for (Event event : events) {
			JSONObject obj = event.toJSONObject();
			try {
				obj.append("favorite", true);
				//show distance dynamically based on user's address
				if (!obj.isNull("latitude") && !obj.isNull("longitude")) {
					double distance = GeoRecommendation.getDistance(lat, lon, obj.getDouble("latitude"), obj.getDouble("longitude"));
					obj.put("distance", distance);
				} else {
					obj.put("distance", -1);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			array.put(obj);
		}
		RpcHelper.writeJsonArray(response, array);
		conn.close(); 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// Get request body and convert to JSONObject
			JSONObject input = RpcHelper.readJsonObject(request);

			// Get user_id and event_id from input
			String userId = input.getString("user_id");
			JSONArray array = (JSONArray) input.get("favorite");

			List<String> histories = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				String eventId = (String) array.get(i);
				histories.add(eventId);
			}			
			DBConnection conn = DBConnectionFactory.getDBConnection();
			conn.setFavoriteEvents(userId, histories);
			// Return save result to client
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Get request body and convert to JSONObject
			JSONObject input = RpcHelper.readJsonObject(request);

			// Get user_id and event_id from input
			String userId = input.getString("user_id");
			JSONArray array = (JSONArray) input.get("favorite");

			List<String> histories = new ArrayList<>();
			for (int i = 0; i < array.length(); i++) {
				String eventId = (String) array.get(i);
				histories.add(eventId);
			}
			DBConnection conn = DBConnectionFactory.getDBConnection();
			conn.unsetFavoriteEvents(userId, histories);
			// Return save result to client
			RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
			conn.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
