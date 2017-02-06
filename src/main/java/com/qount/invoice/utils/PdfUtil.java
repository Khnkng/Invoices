package com.qount.invoice.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.qount.invoice.model.Invoice;

public class PdfUtil {

	private static Logger LOGGER = Logger.getLogger(PdfUtil.class);

	public static final String DEST = "F:/1.pdf";

	public static void main(String[] args) {
		try {
			File file = new File(DEST);
			file.getParentFile().mkdirs();
			Contemporary.createPdf(DEST);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	static class Contemporary {

		public static void createPdf(String dest) throws IOException, DocumentException {

			Document document = new Document();
			document.setMargins(10, 10, 4, 20);
			PdfWriter.getInstance(document, new FileOutputStream(dest));
			document.open();

			String title = "DEFAULT TITLE 1";
			createTitle(document, title);

			String subHeading = "Default subheading 1";
			createSubheading(document, subHeading);

			String imgSrc = "http://lh3.ggpht.com/9tzP9G0EsVP5zCiCrFbdQfbQkFnLzX7kgxYEsTi5gxau7V5G1CsJ0EUJ8U2ugIZxSKMtW4bkbj8z6-eyBEC0eQ=s700";
			addImage(document, imgSrc);

			createEmptyLine(document);

			String str = "company1";
			createCompanyName(document, str);

			str = "banjara hills";
			createCompanyAddress(document, str);

			str = "Hyderabad, Telangana";
			createCompanyAddress(document, str);

			str = "India";
			createCompanyAddress(document, str);

			createEmptyLine(document);

			str = "Phone: 8801446657";
			createCompanyAddress(document, str);

			str = "Toll free: 1800-989-989";
			createCompanyAddress(document, str);

			str = "www.qount.io";
			createCompanyAddress(document, str);

			addLineSeparator(document);
			createBillToLabel(document);

			str = "mateen";
			createBillToName(document, str);

			createEmptyLine(document);
			str = "makjavaprogrammer@gmail.com";
			createUserEmail(document, str);

			str = "6";
			Invoice invoice = new Invoice();
			invoice.setNumber(6);
			invoice.setPo_number("po1");
			invoice.setAcceptance_date("February 2, 2017");
			
			createInvoiceDetails(document, str);
			
			document.close();
		}

		private static void addImage(Document document, String imgSrc) {
			try {
				Image img = Image.getInstance(imgSrc);
				img.scaleAbsolute(50, 50);
				float absoluteY = PageSize.A4.getHeight() - img.getScaledHeight();
				absoluteY -= 30f;
				img.setAbsolutePosition(20, absoluteY);
				document.add(img);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e);
			}
		}

		private static void createTitle(Document document, String title) {
			try {
				Font f = new Font(FontFamily.HELVETICA, 29.0f, Font.NORMAL, BaseColor.BLACK);
				Chunk c = new Chunk(title, f);
				Paragraph p = new Paragraph(c);
				p.setAlignment(Element.ALIGN_RIGHT);
				p.setIndentationRight(10);
				document.add(p);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void createCompanyName(Document document, String str) {
			try {
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
				Chunk c2 = new Chunk(str, f2);
				Paragraph p2 = new Paragraph(c2);
				p2.setAlignment(Element.ALIGN_RIGHT);
				p2.setIndentationRight(10);
				document.add(p2);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void createCompanyAddress(Document document, String str) {
			try {
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
				Chunk c2 = new Chunk(str, f2);
				Paragraph p2 = new Paragraph(c2);
				p2.setAlignment(Element.ALIGN_RIGHT);
				p2.setSpacingBefore(-7);
				p2.setIndentationRight(10);
				document.add(p2);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void createEmptyLine(Document document) {
			try {
				Paragraph p2 = new Paragraph("\n");
				p2.setSpacingBefore(-10);
				document.add(p2);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void createSubheading(Document document, String subHeading) {
			try {
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.GRAY);
				Chunk c2 = new Chunk(subHeading, f2);
				Paragraph p2 = new Paragraph(c2);
				p2.setSpacingBefore(-5);
				p2.setIndentationRight(10);
				p2.setAlignment(Element.ALIGN_RIGHT);
				document.add(p2);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void addLineSeparator(Document document) {
			try {
				LineSeparator ls = new LineSeparator();
				ls.setLineColor(BaseColor.GRAY);
				document.add(new Chunk(ls));
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void createBillToLabel(Document document) {
			try {
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.GRAY);
				Chunk c2 = new Chunk("BILL TO", f2);
				Paragraph p2 = new Paragraph(c2);
				p2.setAlignment(Element.ALIGN_LEFT);
				p2.setIndentationLeft(10);
				document.add(p2);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void createBillToName(Document document, String str) {
			try {
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
				Chunk c2 = new Chunk(str, f2);
				Paragraph p2 = new Paragraph(c2);
				p2.setAlignment(Element.ALIGN_LEFT);
				p2.setSpacingBefore(-5);
				p2.setIndentationLeft(10);
				document.add(p2);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void createUserEmail(Document document, String str) {
			try {
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
				Chunk c2 = new Chunk(str, f2);
				Paragraph p2 = new Paragraph(c2);
				p2.setAlignment(Element.ALIGN_LEFT);
				p2.setSpacingBefore(-5);
				p2.setIndentationLeft(10);
				document.add(p2);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void createInvoiceDetails(Document document, String str) {
			try {
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
				Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
				Chunk c2 = new Chunk("Invoice Number: ", f2);
				Paragraph p2 = new Paragraph(c2);
				p2.setAlignment(Element.ALIGN_RIGHT);
				p2.setSpacingBefore(-45);
				p2.setIndentationRight(100);
				document.add(p2);
				p2.clear();

				Chunk c = new Chunk(str, f);
				p2.setAlignment(Element.ALIGN_RIGHT);
				p2.setSpacingBefore(-18);
				p2.setIndentationRight(90);
				p2.setFont(f);
				p2.add(c);
				document.add(p2);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}
		
		private static void createPoSoNumber(Document document, String str) {
			try {
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
				Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
				Chunk c2 = new Chunk("P.O.S.O. Number: ", f2);
				Paragraph p2 = new Paragraph(c2);
				p2.setAlignment(Element.ALIGN_RIGHT);
//				p2.setSpacingBefore(-5);
				p2.setIndentationRight(100);
				document.add(p2);
				p2.clear();

				Chunk c = new Chunk(str, f);
				p2.setAlignment(Element.ALIGN_RIGHT);
				p2.setSpacingBefore(-2);
				p2.setIndentationRight(50);
				p2.setFont(f);
				p2.add(c);
				document.add(p2);
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

	}
}
