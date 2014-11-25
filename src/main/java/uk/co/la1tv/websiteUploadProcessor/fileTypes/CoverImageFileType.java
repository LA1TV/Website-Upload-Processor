package uk.co.la1tv.websiteUploadProcessor.fileTypes;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import uk.co.la1tv.websiteUploadProcessor.Config;
import uk.co.la1tv.websiteUploadProcessor.File;
import uk.co.la1tv.websiteUploadProcessor.helpers.DbHelper;
import uk.co.la1tv.websiteUploadProcessor.helpers.ImageFormat;
import uk.co.la1tv.websiteUploadProcessor.helpers.ImageMagickFileInfo;
import uk.co.la1tv.websiteUploadProcessor.helpers.ImageMagickFormat;
import uk.co.la1tv.websiteUploadProcessor.helpers.ImageMagickHelper;
import uk.co.la1tv.websiteUploadProcessor.helpers.ImageProcessorHelper;

public class CoverImageFileType extends FileTypeAbstract {
	
	public CoverImageFileType(int id) {
		super(id);
	}

	private static Logger logger = Logger.getLogger(CoverImageFileType.class);

	@Override
	public FileTypeProcessReturnInfo process(final Connection dbConnection, java.io.File source, java.io.File workingDir, File file) {
		Config config = Config.getInstance();
		FileTypeProcessReturnInfo returnVal = new FileTypeProcessReturnInfo();
		// ids of files that should be marked in_use when the process_state is updated at the end of processing
		returnVal.fileIdsToMarkInUse = new HashSet<Integer>();
		
		DbHelper.updateStatus(dbConnection, file.getId(), "Processing image.", null);
		
		ImageMagickFormat inputFormat = ImageMagickFormat.getFormatFromExtension(file.getExtension());
		if (inputFormat == null) {
			logger.warn("Error occurred when trying to get image magick formar from image file with id "+file.getId()+".");
			returnVal.msg = "Error trying to determine image format.";
			return returnVal;
		}
		
		final List<ImageFormat> formats = ImageProcessorHelper.getFormats(config.getList("encoding.coverImageFormats"), workingDir);
	
		returnVal.success = ImageProcessorHelper.process(dbConnection, returnVal, source, workingDir, formats, inputFormat, ImageMagickFormat.JPG, file, FileType.COVER_IMAGE_RENDER);
		return returnVal;
	}
}
