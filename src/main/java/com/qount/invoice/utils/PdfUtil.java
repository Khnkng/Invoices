package com.qount.invoice.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoicePreference;

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

			Invoice invoice = new Invoice();
			invoice.setNumber(6);
			invoice.setPo_number("po1");
			invoice.setInvoice_date("February 1, 2017");
			invoice.setAcceptance_date("February 2, 2017");
			invoice.setAmount_due(1.0d);
			invoice.setCurrency("INR");
			createInvoiceDetails(document, invoice);
			
			createBillToLabel(document);

			str = "mateen";
			createBillToName(document, str);

			createEmptyLine(document);
			str = "makjavaprogrammer@gmail.com";
			createUserEmail(document, str);
			
			InvoicePreference invoicePreference = new InvoicePreference();
			invoicePreference.setItems("Items");
			invoicePreference.setUnits("Qunatity");
			invoicePreference.setPrice("Price");
			invoicePreference.setAmount("Amount");
			createInvoiceDisplayLabels(document, invoicePreference);
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
				Paragraph p2 = new Paragraph(new Chunk(ls));
				p2.setSpacingAfter(10);
				document.add(p2);
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
				p2.setSpacingBefore(-85);
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

		private static void createInvoiceDetails(Document document, Invoice invoice) {
			try {
				Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
				Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
				
				PdfPTable table = new PdfPTable(2);
				
				Chunk c1 = new Chunk("Invoice Number: ", f);
				Phrase invocieNumberLabel = new Phrase(c1);
		        PdfPCell cellOne = new PdfPCell(invocieNumberLabel);
		        cellOne.setBorder(Rectangle.NO_BORDER);
		        cellOne.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        table.addCell(cellOne);
		        
		        Chunk c2 = new Chunk(invoice.getNumber()+"", f2);
		        Phrase invoiceNumber = new Phrase(c2);
		        PdfPCell cellTwo = new PdfPCell(invoiceNumber);
		        cellTwo.setBorder(Rectangle.NO_BORDER);
		        table.addCell(cellTwo);
		        
		        Chunk c3 = new Chunk("P.O./S.O. Number: ", f);
		        Phrase poNumberLabel = new Phrase(c3);
		        PdfPCell cell_3 = new PdfPCell(poNumberLabel);
		        cell_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        cell_3.setBorder(Rectangle.NO_BORDER);
		        table.addCell(cell_3);
		        
		        Chunk c4 = new Chunk(invoice.getPo_number(), f2);
		        Phrase poNumber = new Phrase(c4);
		        PdfPCell cell_4 = new PdfPCell(poNumber);
		        cell_4.setBorder(Rectangle.NO_BORDER);
		        table.addCell(cell_4);
		        
		        
		        Chunk c5 = new Chunk("Invoice Date: ", f);
				Phrase invocieDateLabel = new Phrase(c5);
		        PdfPCell cell_5 = new PdfPCell(invocieDateLabel);
		        cell_5.setBorder(Rectangle.NO_BORDER);
		        cell_5.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        table.addCell(cell_5);
		        
		        Chunk c6 = new Chunk(invoice.getInvoice_date(), f2);
		        Phrase invoiceDate = new Phrase(c6);
		        PdfPCell cell_6 = new PdfPCell(invoiceDate);
		        cell_6.setBorder(Rectangle.NO_BORDER);
		        table.addCell(cell_6);
		        
		        Chunk c7 = new Chunk("Payment Due: ", f);
		        Phrase paymentDueLabel = new Phrase(c7);
		        PdfPCell cell_7 = new PdfPCell(paymentDueLabel);
		        cell_7.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        cell_7.setBorder(Rectangle.NO_BORDER);
		        table.addCell(cell_7);
		        
		        Chunk c8 = new Chunk(invoice.getPo_number(), f2);
		        Phrase paymentDue = new Phrase(c8);
		        PdfPCell cell_8 = new PdfPCell(paymentDue);
		        cell_8.setBorder(Rectangle.NO_BORDER);
		        table.addCell(cell_8);
		        
		        
		        Chunk c9 = new Chunk("Amount Due ("+invoice.getCurrency()+"): ", f);
		        Phrase amountDueLabel = new Phrase(c9);
		        PdfPCell cell_9 = new PdfPCell(amountDueLabel);
		        cell_9.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        cell_9.setBorder(Rectangle.NO_BORDER);
		        table.addCell(cell_9);
		        
		        Chunk c10 = new Chunk(invoice.getAmount_due()+"", f2);
		        Phrase amountDue = new Phrase(c10);
		        PdfPCell cell_10 = new PdfPCell(amountDue);
		        cell_10.setBorder(Rectangle.NO_BORDER);
		        table.addCell(cell_10);
		        
		        
		        
	            table.setLockedWidth(true);
	            table.setTotalWidth(300F);
	            table.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        document.add(table);
				
				
				
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}

		private static void createInvoiceDisplayLabels(Document document, InvoicePreference invoicePreference) {
			try {
				Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.WHITE);
				
				PdfPTable table = new PdfPTable(4);
				
				Chunk c1 = new Chunk(invoicePreference.getItems(), f);
				Phrase invocieItemsLabel = new Phrase(c1);
		        PdfPCell cellOne = new PdfPCell(invocieItemsLabel);
		        cellOne.setBorder(Rectangle.NO_BORDER);
		        cellOne.setBackgroundColor(BaseColor.DARK_GRAY);
		        cellOne.setHorizontalAlignment(Element.ALIGN_LEFT);
		        cellOne.setPaddingLeft(20f);
		        cellOne.setMinimumHeight(20f);
		        table.addCell(cellOne);
		        
		        Chunk c3 = new Chunk(invoicePreference.getUnits(), f);
		        Phrase poNumberLabel = new Phrase(c3);
		        PdfPCell cell_3 = new PdfPCell(poNumberLabel);
		        cell_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        cell_3.setBorder(Rectangle.NO_BORDER);
		        cell_3.setBackgroundColor(BaseColor.DARK_GRAY);
		        table.addCell(cell_3);
		        
		        
		        Chunk c5 = new Chunk(invoicePreference.getPrice(), f);
				Phrase invocieDateLabel = new Phrase(c5);
		        PdfPCell cell_5 = new PdfPCell(invocieDateLabel);
		        cell_5.setBorder(Rectangle.NO_BORDER);
		        cell_5.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        cell_5.setBackgroundColor(BaseColor.DARK_GRAY);
		        table.addCell(cell_5);
		        
		        
		        Chunk c7 = new Chunk(invoicePreference.getAmount(), f);
		        Phrase paymentDueLabel = new Phrase(c7);
		        PdfPCell cell_7 = new PdfPCell(paymentDueLabel);
		        cell_7.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        cell_7.setBorder(Rectangle.NO_BORDER);
		        cell_7.setBackgroundColor(BaseColor.DARK_GRAY);
		        cell_7.setPaddingRight(20f);
		        table.addCell(cell_7);
		        
		        table.setSpacingBefore(50);
		        table.setWidthPercentage(100);
//	            table.setLockedWidth(true);
//	            table.setTotalWidth(300F);
//	            table.setHorizontalAlignment(Element.ALIGN_RIGHT);
		        document.add(table);
				
				
				
			} catch (DocumentException e) {
				LOGGER.error(e);
			}
		}
		
		
		
	}
}
