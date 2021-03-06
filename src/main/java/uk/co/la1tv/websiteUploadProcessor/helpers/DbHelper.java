package uk.co.la1tv.websiteUploadProcessor.helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;

import uk.co.la1tv.websiteUploadProcessor.Db;
import uk.co.la1tv.websiteUploadProcessor.File;
import uk.co.la1tv.websiteUploadProcessor.fileTypes.FileType;

public class DbHelper {
	
	private static Logger logger = Logger.getLogger(DbHelper.class);
	
	private static Db db = null;
	
	private DbHelper() {}

	/**
	 * Set Db object that will be used through out the application.
	 * This means it can be retrieved from anywhere.
	 * @param db
	 */
	public static void setMainDb(Db db) {
		DbHelper.db = db;
	}
	
	/**
	 * Retrieve the main Db object that was initialised during application startup.
	 * @return The Db object
	 */
	public static Db getMainDb() {
		return db;
	}
	
	/**
	 * Builds a file object from a result set's row.
	 * @param r: The result set on the required row.
	 * @return File: A File object representing the db record.
	 * @throws SQLException
	 */
	public static File buildFileFromResult(ResultSet r) throws SQLException {
		return new File(r.getInt("id"), r.getString("filename"), r.getLong("size"), FileType.getFromId(r.getInt("file_type_id")));
	}
	
	/**
	 * Set the current processing message and/or percentage in the database for a file.
	 * @param connection: The database connection to use.
	 * @param fileId: the file id in the database
	 * @param msg: The message to set. null means no message.
	 * @param percentage: The percentage of processing from 0-100. null means no percentage
	 * @return boolean representing whether update successful.
	 */
	public static boolean updateStatus(Connection connection, double id, String msg, Integer percentage) {
		try {
			PreparedStatement s = connection.prepareStatement("UPDATE files SET msg=?, process_percentage=? WHERE id=?");
			s.setString(1, msg);
			if (percentage != null) {
				s.setInt(2, percentage);
			}
			else {
				s.setNull(2, Types.INTEGER);
			}
			s.setDouble(3, id);
			boolean result = s.executeUpdate() == 1;
			s.close();
			return result;
		} catch (SQLException e) {
			logger.warn("SQLException when trying to update status.");
			return false;
		}
	}
	
}
