package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Event;
import entity.Event.EventBuilder;
import externalAPI.API;
import externalAPI.APIFactory;

public class MySQLConnection implements DBConnection {
	private Connection conn;
	
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance(); // Ensure the driver is imported.
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	public void setFavoriteEvents(String userId, List<String> eventIds) {
		if (conn == null) {
			return;
		}
		String query = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
		try {
			for (String i : eventIds) {
				PreparedStatement statement = conn.prepareStatement(query);
				statement.setString(1, userId);
				statement.setString(2, i);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void unsetFavoriteEvents(String userId, List<String> eventIds) {
		if (conn == null) {
			return;
		}
		String query = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			for (String i : eventIds) {
				statement.setString(1, userId);
				statement.setString(2, i);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> getFavoriteEventIds(String userId) {
		if (conn == null) {
			return null;
		}
		Set<String> favoriteEventIds = new HashSet<>();
		try {
			String sql = "SELECT item_id from history WHERE user_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				favoriteEventIds.add(rs.getString("item_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteEventIds;
	}

	@Override
	public Set<Event> getFavoriteEvents(String userId) {
		if (conn == null) {
			return null;
		}
		Set<String> eventIds = getFavoriteEventIds(userId);
		Set<Event> favoriteEvents = new HashSet<>();
		try {
			for (String eventId : eventIds) {
				String sql = "SELECT * from items WHERE item_id = ? ";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, eventId);
				ResultSet rs = statement.executeQuery();
				EventBuilder builder = new EventBuilder();

				// Because itemId is unique and given one item id there should
				// have
				// only one result returned.
				if (rs.next()) {
					builder.setEventId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setCity(rs.getString("city"));
					builder.setState(rs.getString("state"));
					builder.setCountry(rs.getString("country"));
					builder.setZipcode(rs.getString("zipcode"));
					builder.setRating(rs.getDouble("rating"));
					builder.setAddress(rs.getString("address"));
					builder.setLatitude(rs.getDouble("latitude"));
					builder.setLongitude(rs.getDouble("longitude"));
					builder.setDescription(rs.getString("description"));
					builder.setSnippet(rs.getString("snippet"));
					builder.setSnippetUrl(rs.getString("snippet_url"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
				}

				// Join categories information into builder.
				// But why we do not join in sql? Because it'll be difficult
				// to set it in builder.
				sql = "SELECT * from categories WHERE item_id = ?";
				statement = conn.prepareStatement(sql);
				statement.setString(1, eventId);
				rs = statement.executeQuery();
				Set<String> categories = new HashSet<>();
				while (rs.next()) {
					categories.add(rs.getString("category"));
				}
				builder.setCategories(categories);
				favoriteEvents.add(builder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteEvents;
	}

	@Override
	public Set<String> getCategories(String eventId) {
		if (conn == null) {
			return null;
		}
		Set<String> categories = new HashSet<>();
		try {
			String sql = "SELECT category from categories WHERE item_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, eventId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				categories.add(rs.getString("category"));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return categories;
	}

	@Override
	public List<Event> searchEvents(String userId, double lat, double lon, String term) {
		// Connect to external API
		API api = APIFactory.getExternalAPI(); 
		List<Event> events = api.search(lat, lon, term);
		for (Event event : events) {
			saveEvent(event);
		}
		return events;
	}

	@Override
	public void saveEvent(Event event) {
		if (conn == null) {
			return;
		}
		try {
			// First, insert into items table
			String sql = "INSERT IGNORE INTO items VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, event.getEventId());
			statement.setString(2, event.getName());
			statement.setString(3, event.getCity());
			statement.setString(4, event.getState());
			statement.setString(5, event.getCountry());
			statement.setString(6, event.getZipcode());
			statement.setDouble(7, event.getRating());
			statement.setString(8, event.getAddress());
			statement.setDouble(9, event.getLatitude());
			statement.setDouble(10, event.getLongitude());
			statement.setString(11, event.getDescription());
			statement.setString(12, event.getSnippet());
			statement.setString(13, event.getSnippetUrl());
			statement.setString(14, event.getImageUrl());
			statement.setString(15, event.getUrl());
			statement.execute();
			// Second, update categories table for each category.
			sql = "INSERT IGNORE INTO categories VALUES (?,?)";
			for (String category : event.getCategories()) {
				statement = conn.prepareStatement(sql);
				statement.setString(1, event.getEventId());
				statement.setString(2, category);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFullname(String userId) {
		if (conn == null) {
			return null;
		}
		String name = "";
		try {
			String sql = "SELECT first_name, last_name from users WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				name += String.join(" ", rs.getString("first_name"), rs.getString("last_name"));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return name;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			return false;
		}
		try {
			String sql = "SELECT user_id from users WHERE user_id = ? and password = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
}
