package uk.co.la1tv.websiteUploadProcessor.fileTypes;

import org.apache.log4j.Logger;

import uk.co.la1tv.websiteUploadProcessor.File;

public class VODVideoRenderFileType extends FileTypeAbstract {
	
	public VODVideoRenderFileType(int id) {
		super(id);
	}

	private static Logger logger = Logger.getLogger(VODVideoRenderFileType.class);

	@Override
	public boolean process(java.io.File source, java.io.File workingDir, File file) {
		// TODO Auto-generated method stub
		return false;
	}
}