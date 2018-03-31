package db;

import db.mysql.MySQLConnection;

public class DBConnectionFactory {
	private static final String DEFAULT_DB = "mysql";

	// Create a DBConnection based on given db type.
	public static DBConnection getDBConnection(String dbPipeline) {
		switch (dbPipeline) {
		case "mysql":
			return new MySQLConnection();
		case "mongodb":
			return null;
			// You may try other dbs and add them here.
		default:
			throw new IllegalArgumentException("Invalid db " + dbPipeline);
		}
	}

	// overloading
	public static DBConnection getDBConnection() {
		return getDBConnection(DEFAULT_DB);
	}
}
