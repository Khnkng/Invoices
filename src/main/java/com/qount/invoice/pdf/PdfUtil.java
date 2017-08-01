package com.qount.invoice.pdf;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.qount.invoice.utils.CommonUtils;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 10 Feb 2016
 *
 */
public class PdfUtil {
	private static final Logger LOGGER = Logger.getLogger(PdfUtil.class);

	public static void closeFileStream(FileOutputStream fout) {
		try {
			if (fout != null) {
				fout.close();
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

	public static void closeDocumentStream(Document document) {
		try {
			if (document != null) {
				document.close();
			}
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

	public static void deleteFile(File file) {
		try {
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(5000);
						if (file != null && file.exists()) {
							file.delete();
						}
					} catch (InterruptedException e) {
						LOGGER.error(CommonUtils.getErrorStackTrace(e));
					}
				}
			}.start();
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

}
