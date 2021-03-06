package uk.co.la1tv.websiteUploadProcessor.helpers;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import uk.co.la1tv.websiteUploadProcessor.Config;

public class FileHelper {
	
	private static Logger logger = Logger.getLogger(FileHelper.class);
	
	private FileHelper() {}
	
	/**
	 * Formats the path passed in so that it is correct for the filesystem it's running on. 
	 * @param path
	 * @return Formatted path
	 */
	public static String format(String path) {
		char sep = System.getProperty("file.separator").charAt(0);
		return path.replace(sep == '\\' ? '/' : '\\', sep);
	}
	
	/**
	 * Empties the working directory. Also creates it if it doesn't exist.
	 */
	public static void cleanWorkingDir() {
		if (Files.exists(Paths.get(getWorkingDir()), LinkOption.NOFOLLOW_LINKS)) {
			logger.info("Cleaning working directory...");
			try {
				FileUtils.cleanDirectory(new File(getWorkingDir()));
			} catch (IOException e) {
				throw(new RuntimeException("Error occurred when trying to clear working directory."));
			}
			logger.info("Cleaned working directory.");
		}
		else {
			logger.info("Working directory doesn't exist. Creating it...");
			try {
				FileUtils.forceMkdir(new File(getWorkingDir()));
			} catch (IOException e) {
				throw(new RuntimeException("Error occurred when trying to create working directory."));
			}
			logger.info("Created working directory.");
		}
	}
	
	/**
	 * Get the working directory for this server.
	 * @return the working directory for this server.
	 */
	public static String getWorkingDir() {
		Config config = Config.getInstance();
		return FileHelper.format(config.getString("files.workingFilesLocation")+"/"+config.getInt("server.id"));
	}
	
	public static String getFileWorkingDir(int fileId) {
		return FileHelper.format(FileHelper.getWorkingDir()+"/"+fileId);
	}
	
	public static String getSourceFilePath(int fileId) {
		return FileHelper.format(Config.getInstance().getString("files.webappFilesLocation")+"/"+fileId);
	}
	
	public static boolean moveToWebApp(File source, int id) {
		File destinationLocation = new File(FileHelper.format(Config.getInstance().getString("files.webappFilesLocation")+"/"+id));
		return moveFile(source, destinationLocation);
	}
	
	private static boolean moveFile(File source, File destination) {
		destination.delete(); // delete file at destination (if there is one)
		
		// this was originally a source.renameTo(destination) to move the file but this wasn't working when the storage directory was on a different drive for some reason. The copy then delete does. Probably down to this: http://stackoverflow.com/a/300562/1048589 ("File.renameTo generally works only on the same file system volume. I think of this as the equivalent to a "mv" command. Use it if you can, but for general copy and move support, you'll need to have a fallback.")
		// first copy the file
		try {
			FileUtils.copyFile(source, destination);
		} catch (IOException e) {
			logger.error("Exception when trying to move a file.");
			e.printStackTrace();
			return false;
		}
		
		// now remove the original
		source.delete();
		return true;	
	}
	
	public static boolean isOverQuota() {
		return isOverQuota(BigInteger.ZERO);
	}
	
	public static boolean isOverQuota(BigInteger additional) {
		Config config = Config.getInstance();
		BigInteger quota = config.getBigInteger("local.webAppSpaceQuota");
		if (quota == null || quota.equals(BigInteger.valueOf(-1))) {
			// -1 or not specified means unlimited
			return false;
		}
		return FileUtils.sizeOfAsBigInteger(new File(FileHelper.format(config.getString("files.webappFilesLocation")))).compareTo(quota.multiply(new BigInteger("1000000")).add(additional)) > 0;
	}
}
