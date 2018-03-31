package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Event {
	private String eventId;
	private String name;
	private double rating;
	private String address;
	private String city;
	private String country;
	private String state;
	private String zipcode;
	private double latitude;
	private double longitude;
	private String description;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private String snippet;
	private String snippetUrl;
	
	@Override
	// for deduplicate event returned in the favorite event
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}	
		Event other = (Event) obj;
		if (eventId == null) {
			if (other.eventId != null) {
				return false;
			}
		} else if (!eventId.equals(other.eventId)) {
			return false;
		}
		return true;
	}
	/**
	 * This is a builder pattern in Java.
	 */
	// Constructor
	private Event(EventBuilder builder) {
		this.eventId = builder.eventId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.city = builder.city;
		this.country = builder.country;
		this.state = builder.state;
		this.zipcode = builder.zipcode;
		this.latitude = builder.latitude;
		this.longitude = builder.longitude;
		this.description = builder.description;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.snippet = builder.snippet;
		this.snippetUrl = builder.snippetUrl;
	}
	
	// Builder Pattern
	public static class EventBuilder {
		private String eventId;
		private String name;
		private double rating;
		private String address;
		private String city;
		private String country;
		private String state;
		private String zipcode;
		private double latitude;
		private double longitude;
		private String description;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private String snippet;
		private String snippetUrl;

		public EventBuilder setEventId(String eventId) {
			this.eventId = eventId;
			return this;
		}

		public EventBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public EventBuilder setRating(double rating) {
			this.rating = rating;
			return this;
		}

		public EventBuilder setAddress(String address) {
			this.address = address;
			return this;
		}

		public EventBuilder setCity(String city) {
			this.city = city;
			return this;
		}

		public EventBuilder setCountry(String country) {
			this.country = country;
			return this;
		}

		public EventBuilder setState(String state) {
			this.state = state;
			return this;
		}

		public EventBuilder setZipcode(String zipcode) {
			this.zipcode = zipcode;
			return this;
		}

		public EventBuilder setLatitude(double latitude) {
			this.latitude = latitude;
			return this;
		}

		public EventBuilder setLongitude(double longitude) {
			this.longitude = longitude;
			return this;
		}

		public EventBuilder setDescription(String description) {
			this.description = description;
			return this;
		}

		public EventBuilder setCategories(Set<String> categories) {
			this.categories = categories;
			return this;
		}

		public EventBuilder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public EventBuilder setUrl(String url) {
			this.url = url;
			return this;
		}

		public EventBuilder setSnippet(String snippet) {
			this.snippet = snippet;
			return this;
		}

		public EventBuilder setSnippetUrl(String snippetUrl) {
			this.snippetUrl = snippetUrl;
			return this;
		}

		public Event build() {
			return new Event(this);
		}
	}
	
	// Getter
	public String getEventId() {
		return eventId;
	}
	public String getName() {
		return name;
	}
	public double getRating() {
		return rating;
	}
	public String getAddress() {
		return address;
	}
	public String getCity() {
		return city;
	}
	public String getCountry() {
		return country;
	}
	public String getState() {
		return state;
	}
	public String getZipcode() {
		return zipcode;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public String getDescription() {
		return description;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public String getSnippet() {
		return snippet;
	}
	public String getSnippetUrl() {
		return snippetUrl;
	}
	
	// Convert to JSON
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id", eventId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("city", city);
			obj.put("country", country);
			obj.put("state", state);
			obj.put("zipcode", zipcode);
			obj.put("latitude", latitude);
			obj.put("longitude", longitude);
			obj.put("description", description);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("snippet_url", snippetUrl);
			obj.put("snippet", snippet);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
