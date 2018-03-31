package externalAPI;

/*
 * This is java factory pattern, switch to different API based on pipeline
 */
public class APIFactory {
	private static final String DEFAULT_PIPELINE = "ticketmaster";

	public static API getExternalAPI(String pipeline) {
		switch (pipeline) {
		case "restaurant":
			// return new YelpAPI(); 
			return null;
		case "job":
			// return new LinkedInAPI(); 
			return null;
		case "news":
			// return new NewYorkTimesAPI(); 
			return null;
		case "ticketmaster":
			return new TicketMasterAPI();
		default:
			throw new IllegalArgumentException("Invalid pipeline " + pipeline);
		}
	}

	public static API getExternalAPI() {
		return getExternalAPI(DEFAULT_PIPELINE);
	}
}
