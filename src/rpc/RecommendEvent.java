package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import algorithm.GeoRecommendation;
import entity.Event;

/**
 * Servlet implementation class RecommendEvent
 */
@WebServlet("/RecommendEvent")
public class RecommendEvent extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendEvent() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		GeoRecommendation recommendation = new GeoRecommendation();
		List<Event> events = recommendation.recommendEvents(userId, lat, lon);

		JSONArray result = new JSONArray();
		try {
			for (Event event : events) {
				JSONObject obj = event.toJSONObject();
				// show distance dynamically based on user's address
				if (!obj.isNull("latitude") && !obj.isNull("longitude")) {
					double distance = GeoRecommendation.getDistance(obj.getDouble("latitude"), obj.getDouble("longitude"), lat, lon);
					obj.put("distance", distance);
				} else {
					obj.put("distance", -1);
				}
				result.put(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		RpcHelper.writeJsonArray(response, result);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
