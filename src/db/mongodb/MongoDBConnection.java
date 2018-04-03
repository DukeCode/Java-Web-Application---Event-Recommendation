package db.mongodb;

// This line needs manual import.
import static com.mongodb.client.model.Filters.eq;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import db.DBConnection;
import entity.Event;
import entity.Event.EventBuilder;
import externalAPI.API;
import externalAPI.APIFactory;

public class MongoDBConnection implements DBConnection {
	private MongoClient mongoClient;
	private MongoDatabase db;

	public MongoDBConnection() {
		// Connects to local mongodb server.
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}

	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public void setFavoriteEvents(String userId, List<String> eventIds) {
		db.getCollection("users").updateOne(new Document("user_id", userId),
				new Document("$push", new Document("favorite", new Document("$each", eventIds))));
	}

	@Override
	public void unsetFavoriteEvents(String userId, List<String> eventIds) {
		db.getCollection("users").updateOne(new Document("user_id", userId),
				new Document("$pullAll", new Document("favorite", eventIds)));
	}

	@Override
	public Set<String> getFavoriteEventIds(String userId) {
		Set<String> favoriteEvent = new HashSet<String>();
		FindIterable<Document> iterable = db.getCollection("users").find(eq("user_id", userId));
		if (iterable.first().containsKey("favorite")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("favorite");
			favoriteEvent.addAll(list);
		}
		return favoriteEvent;
	}

	@Override
	public Set<Event> getFavoriteEvents(String userId) {
		Set<String> eventIds = getFavoriteEventIds(userId);
		Set<Event> favoriteEvents = new HashSet<>();
		for (String eventId : eventIds) {
			FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", eventId));
			Document doc = iterable.first();
			EventBuilder builder = new EventBuilder();
			builder.setEventId(doc.getString("item_id"));
			builder.setName(doc.getString("name"));
			builder.setCity(doc.getString("city"));
			builder.setState(doc.getString("state"));
			builder.setCountry(doc.getString("country"));
			builder.setZipcode(doc.getString("zipcode"));
			builder.setRating(doc.getDouble("rating"));
			builder.setAddress(doc.getString("address"));
			builder.setLatitude(doc.getDouble("latitude"));
			builder.setLongitude(doc.getDouble("longitude"));
			builder.setDescription(doc.getString("description"));
			builder.setSnippet(doc.getString("snippet"));
			builder.setSnippetUrl(doc.getString("snippet_url"));
			builder.setImageUrl(doc.getString("image_url"));
			builder.setUrl(doc.getString("url"));
			builder.setCategories(getCategories(eventId));

			favoriteEvents.add(builder.build());
		}
		return favoriteEvents;
	}

	@Override
	public Set<String> getCategories(String eventId) {
		Set<String> categories = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", eventId));

		if (iterable.first().containsKey("categories")) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) iterable.first().get("categories");
			categories.addAll(list);
		}
		return categories;
	}

	@Override
	public List<Event> searchEvents(String userId, double lat, double lon, String term) {
		// Connect to external API
		API api = APIFactory.getExternalAPI(); // moved here
		List<Event> events = api.search(lat, lon, term);
		for (Event event : events) {
			saveEvent(event);
		}
		return events;
	}

	@Override
	public void saveEvent(Event event) {

		FindIterable<Document> iterable = db.getCollection("items").find(eq("item_id", event.getEventId()));
		if (iterable.first() == null) {
			db.getCollection("items")
					.insertOne(new Document().append("item_id", event.getEventId()).append("name", event.getName())
							.append("city", event.getCity()).append("state", event.getState())
							.append("country", event.getCountry()).append("zip_code", event.getZipcode())
							.append("rating", event.getRating()).append("address", event.getAddress())
							.append("latitude", event.getLatitude()).append("longitude", event.getLongitude())
							.append("description", event.getDescription()).append("snippet", event.getSnippet())
							.append("snippet_url", event.getSnippetUrl()).append("image_url", event.getImageUrl())
							.append("url", event.getUrl()).append("categories", event.getCategories()));
		}
	}

	@Override
	public String getFullname(String userId) {
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		return false;
	}
}
