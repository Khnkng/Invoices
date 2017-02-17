package com.qount.invoice.pdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
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
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoicePreference;

public class Modern {

	private static Logger LOGGER = Logger.getLogger(PdfUtil.class);

	public static Document createPdf(InvoiceReference invoiceReference, Document document, FileOutputStream fout) throws IOException, DocumentException {
		try {
			document.setMargins(10, 10, 4, 20);
			PdfWriter pw = PdfWriter.getInstance(document, fout);
			document.open();
			String imgSrc = "http://lh3.ggpht.com/9tzP9G0EsVP5zCiCrFbdQfbQkFnLzX7kgxYEsTi5gxau7V5G1CsJ0EUJ8U2ugIZxSKMtW4bkbj8z6-eyBEC0eQ=s700";
			InvoicePreference invoicePreference = invoiceReference.getInvoicePreference();
			Invoice invoice = invoiceReference.getInvoice();
			Customer customer = invoiceReference.getCustomer();
			createTable(document, invoicePreference, invoice);
			addSpaceBefore(document, 10);
			createInvoiceDetails(document, invoice);
			createBillToName(document, customer);
			addSpaceBefore(document, 10);
			createInvoiceDisplayLabels(document);
			addLineSeparator(document);
			createInvoiceLineItems(document, invoice);
			addLineSeparator(document);
			addSpaceAfter(document, 5);
			createTotal(document, invoice);
			addSpaceAfter(document, 5);
			createAmountDue(document, invoice);
			createNotes(document, invoice);
			createFooterBeforeEnd(pw, document, invoicePreference);
			addLineSeparatorBeforeEnd(pw, document);
			addImage(document, imgSrc);
			createCompanyDetails(pw, document, customer);
		} catch (Exception e) {
			LOGGER.error(e);
		} 
		return document;
	}

	private static void createNotes(Document document, Invoice invoice) {
		if (null == invoice || StringUtils.isEmpty(invoice.getNotes())) {
			return;
		}
		try {
			Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
			Chunk c2 = new Chunk("Notes", f2);
			Paragraph p2 = new Paragraph(c2);
			p2.setAlignment(Element.ALIGN_LEFT);
			p2.setIndentationLeft(10);

			Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.LIGHT_GRAY);
			Chunk c = new Chunk(invoice.getNotes(), f);
			Paragraph p = new Paragraph(c);
			p.setAlignment(Element.ALIGN_LEFT);
			p.setIndentationLeft(10);

			document.add(p2);
			document.add(p);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void createAmountDue(Document document, Invoice invoice) {
		if (null == invoice) {
			return;
		}
		try {
			PdfPTable table = new PdfPTable(4);
			Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);

			Chunk c5 = new Chunk("", f);
			Phrase invocieDateLabel = new Phrase(c5);
			PdfPCell cell_5 = new PdfPCell(invocieDateLabel);
			cell_5.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_5);

			Chunk c7 = new Chunk("", f);
			Phrase paymentDueLabel = new Phrase(c7);
			PdfPCell cell_7 = new PdfPCell(paymentDueLabel);
			cell_7.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_7);

			Chunk c1 = new Chunk("Amount Due(" + invoice.getCurrency() + "):", f);
			Phrase invocieItemsLabel = new Phrase(c1);
			PdfPCell cellOne = new PdfPCell(invocieItemsLabel);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellOne.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellOne.setPaddingLeft(20f);
			cellOne.setMinimumHeight(20f);
			table.addCell(cellOne);

			Chunk c3 = new Chunk(invoice.getCurrency() + " " + invoice.getAmount_due(), f);
			Phrase poNumberLabel = new Phrase(c3);
			PdfPCell cell_3 = new PdfPCell(poNumberLabel);
			cell_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_3.setBorder(Rectangle.NO_BORDER);
			cell_3.setPaddingRight(20f);
			table.addCell(cell_3);
			// table.setSpacingBefore(50);
			table.setWidthPercentage(100);
			document.add(table);

		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void createTotal(Document document, Invoice invoice) {
		if (null == invoice) {
			return;
		}
		try {
			PdfPTable table = new PdfPTable(4);
			Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
			Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);

			Chunk c5 = new Chunk("", f);
			Phrase invocieDateLabel = new Phrase(c5);
			PdfPCell cell_5 = new PdfPCell(invocieDateLabel);
			cell_5.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_5);

			Chunk c7 = new Chunk("", f);
			Phrase paymentDueLabel = new Phrase(c7);
			PdfPCell cell_7 = new PdfPCell(paymentDueLabel);
			cell_7.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_7);

			Chunk c1 = new Chunk("Total:", f);
			Phrase invocieItemsLabel = new Phrase(c1);
			PdfPCell cellOne = new PdfPCell(invocieItemsLabel);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellOne.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellOne.setPaddingLeft(20f);
			cellOne.setMinimumHeight(20f);
			table.addCell(cellOne);

			Chunk c3 = new Chunk(invoice.getCurrency() + " " + invoice.getAmount(), f2);
			Phrase poNumberLabel = new Phrase(c3);
			PdfPCell cell_3 = new PdfPCell(poNumberLabel);
			cell_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_3.setBorder(Rectangle.NO_BORDER);
			cell_3.setPaddingRight(20f);
			table.addCell(cell_3);
			// table.setSpacingBefore(50);
			table.setWidthPercentage(100);
			document.add(table);

		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void createInvoiceLineItems(Document document, Invoice invoice) {
		if (null == invoice || invoice.getInvoiceLines() == null || invoice.getInvoiceLines().isEmpty()) {
			return;
		}
		try {
			PdfPTable table = new PdfPTable(4);
			Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);
			Iterator<InvoiceLine> invoiceLinesItr = invoice.getInvoiceLines().iterator();
			while (invoiceLinesItr.hasNext()) {
				InvoiceLine invoiceLine = invoiceLinesItr.next();
				Chunk c1 = new Chunk("Unde Work", f);
				Phrase invocieItemsLabel = new Phrase(c1);
				PdfPCell cellOne = new PdfPCell(invocieItemsLabel);
				cellOne.setBorder(Rectangle.NO_BORDER);
				cellOne.setHorizontalAlignment(Element.ALIGN_LEFT);
				cellOne.setPaddingLeft(20f);
				cellOne.setMinimumHeight(15f);
				table.addCell(cellOne);

				Chunk c3 = new Chunk(invoiceLine.getQuantity() + "", f);
				Phrase poNumberLabel = new Phrase(c3);
				PdfPCell cell_3 = new PdfPCell(poNumberLabel);
				cell_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell_3.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell_3);

				Chunk c5 = new Chunk(invoiceLine.getCurrency() + " " + invoiceLine.getPrice(), f);
				Phrase invocieDateLabel = new Phrase(c5);
				PdfPCell cell_5 = new PdfPCell(invocieDateLabel);
				cell_5.setBorder(Rectangle.NO_BORDER);
				cell_5.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(cell_5);

				Chunk c7 = new Chunk(invoiceLine.getCurrency() + " " + invoiceLine.getAmount(), f);
				Phrase paymentDueLabel = new Phrase(c7);
				PdfPCell cell_7 = new PdfPCell(paymentDueLabel);
				cell_7.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell_7.setBorder(Rectangle.NO_BORDER);
				cell_7.setPaddingRight(20f);
				table.addCell(cell_7);
			}
			table.setWidthPercentage(100);
			document.add(table);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void createInvoiceDisplayLabels(Document document) {
		if (document == null) {
			return;
		}
		try {
			Font f = new Font(FontFamily.HELVETICA, 12.0f, Font.BOLD, BaseColor.LIGHT_GRAY);

			PdfPTable table = new PdfPTable(4);

			Chunk c1 = new Chunk("Items", f);
			Phrase invocieItemsLabel = new Phrase(c1);
			PdfPCell cellOne = new PdfPCell(invocieItemsLabel);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellOne.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellOne.setPaddingLeft(15f);
			cellOne.setMinimumHeight(15f);
			table.addCell(cellOne);

			Chunk c3 = new Chunk("Quantity", f);
			Phrase poNumberLabel = new Phrase(c3);
			PdfPCell cell_3 = new PdfPCell(poNumberLabel);
			cell_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_3.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_3);

			Chunk c5 = new Chunk("Price", f);
			Phrase invocieDateLabel = new Phrase(c5);
			PdfPCell cell_5 = new PdfPCell(invocieDateLabel);
			cell_5.setBorder(Rectangle.NO_BORDER);
			cell_5.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell_5);

			Chunk c7 = new Chunk("Amount", f);
			Phrase paymentDueLabel = new Phrase(c7);
			PdfPCell cell_7 = new PdfPCell(paymentDueLabel);
			cell_7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_7.setBorder(Rectangle.NO_BORDER);
			cell_7.setPaddingRight(15f);
			table.addCell(cell_7);

			table.setSpacingBefore(30);
			table.setSpacingAfter(0);
			table.setWidthPercentage(100);
			document.add(table);

		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static void addSpaceBefore(Document document, float spacingBefore) {
		Paragraph p = new Paragraph("");
		p.setSpacingBefore(spacingBefore);
		try {
			document.add(p);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addSpaceAfter(Document document, float spacingAfter) {
		Paragraph p = new Paragraph("");
		p.setSpacingBefore(spacingAfter);
		try {
			document.add(p);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createTable(Document document, InvoicePreference invoicePreference, Invoice invoice) {
		PdfPTable table = new PdfPTable(2);
		try {
			table.setTotalWidth(PageSize.A4.getWidth());
			table.setLockedWidth(true);
			PdfPCell cell;
			Font f = new Font(FontFamily.HELVETICA, 18.0f, Font.NORMAL, BaseColor.WHITE);
			Chunk chunk1 = new Chunk(invoicePreference.getDefaultTitle(), f);
			Font f2 = new Font(FontFamily.HELVETICA, 11.0f, Font.NORMAL, BaseColor.WHITE);
			Chunk chunk2 = new Chunk(invoicePreference.getDefaultSubHeading(), f2);
			Phrase phrase = new Phrase();
			phrase.add(chunk1);
			phrase.add(Chunk.NEWLINE);
			phrase.add(chunk2);
			cell = new PdfPCell(phrase);

			cell.setMinimumHeight(100);
			cell.setBackgroundColor(BaseColor.DARK_GRAY);
			cell.setPadding(30f);
			cell.setColspan(1);
			table.addCell(cell);
			Font f3 = new Font(FontFamily.HELVETICA, 13.0f, Font.NORMAL, BaseColor.WHITE);
			Chunk c = new Chunk("Amount Due (INR)", f3);
			Font f4 = new Font(FontFamily.HELVETICA, 18.0f, Font.NORMAL, BaseColor.WHITE);
			Double amountDue = invoice.getAmount_due();
			Chunk c2 = new Chunk(amountDue.toString(), f4);
			Phrase p = new Phrase();
			p.add(c);
			p.add(Chunk.NEWLINE);
			p.add(c2);
			cell = new PdfPCell(p);
			cell.setBackgroundColor(BaseColor.GRAY);
			cell.setPadding(30f);
			table.addCell(cell);
			document.add(table);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addImage(Document document, String imgSrc) {
		try {
			Image img = Image.getInstance(imgSrc);
			img.scaleAbsolute(60, 60);
			img.setAbsolutePosition(30f, 40f);
			document.add(img);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
		}
	}

	private static void createCompanyDetails(PdfWriter writer, Document document, Customer customer) {
		try {
			Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
			Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.BLACK);

			PdfPTable table = new PdfPTable(2);

			Chunk c1 = new Chunk("Company1", f);
			Phrase companyName = new Phrase(c1);
			PdfPCell cell_1 = new PdfPCell(companyName);
			cell_1.setBorder(Rectangle.NO_BORDER);
			cell_1.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell_1);

			Chunk c11 = new Chunk("Contact Information", f);
			Phrase contactInfo = new Phrase(c11);
			PdfPCell cellOne = new PdfPCell(contactInfo);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellOne.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cellOne);

			Chunk c2 = new Chunk("banjara hills", f2);
			Phrase comAddress = new Phrase(c2);
			PdfPCell cell_2 = new PdfPCell(comAddress);
			cell_2.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell_2.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_2);

			Chunk c22 = new Chunk("Phone: " + "8801446657", f2);
			Phrase phone = new Phrase(c22);
			PdfPCell cellTwo = new PdfPCell(phone);
			cellTwo.setBorder(Rectangle.NO_BORDER);
			cellTwo.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cellTwo);

			Chunk c3 = new Chunk("Hyderabad, Telangana ", f2);
			Phrase comCity = new Phrase(c3);
			PdfPCell cell_3 = new PdfPCell(comCity);
			cell_3.setBorder(Rectangle.NO_BORDER);
			cell_3.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(cell_3);

			Chunk c4 = new Chunk("Toll free: 1800-989-989", f2);
			Phrase com_state = new Phrase(c4);
			PdfPCell cell_4 = new PdfPCell(com_state);
			cell_4.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_4.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_4);

			Chunk c5 = new Chunk("India", f2);
			Phrase com_country = new Phrase(c5);
			PdfPCell cell_5 = new PdfPCell(com_country);
			cell_5.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell_5.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_5);

			Chunk c55 = new Chunk("www.qount.io", f2);
			Phrase website = new Phrase(c55);
			PdfPCell cell_55 = new PdfPCell(website);
			cell_55.setBorder(Rectangle.NO_BORDER);
			cell_55.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell_55);

			table.setLockedWidth(true);
			table.setTotalWidth(300F);
			table.setHorizontalAlignment(Element.ALIGN_BOTTOM);
			table.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table.writeSelectedRows(0, -1, document.left(document.leftMargin()) + 250, table.getTotalHeight() + document.bottom(document.bottomMargin()),
					writer.getDirectContent());

			// document.add(table);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addLineSeparatorBeforeEnd(PdfWriter writer, Document document) {
		try {
			LineSeparator ls = new LineSeparator();
			ls.setLineWidth(1.5f);
			ls.setLineColor(BaseColor.LIGHT_GRAY);
			Paragraph p2 = new Paragraph(new Chunk(ls));
			PdfContentByte cb = writer.getDirectContent();
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, p2, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() + 100, 0);
			// document.add(p2);
		} catch (Exception e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}

	private static void createFooterBeforeEnd(PdfWriter writer, Document document, InvoicePreference invoicePreference) {
		if (null == invoicePreference || StringUtils.isEmpty(invoicePreference.getDefaultFooter())) {
			return;
		}
		try {
			PdfContentByte cb = writer.getDirectContent();
			Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.GRAY);
			Chunk c2 = new Chunk(invoicePreference.getDefaultFooter(), f2);
			Phrase p2 = new Phrase(c2);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, p2, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() + 120, 0);
		} catch (Exception e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}

	private static void addLineSeparator(Document document) {
		try {
			LineSeparator ls = new LineSeparator();
			ls.setLineWidth(1.5f);
			ls.setLineColor(BaseColor.LIGHT_GRAY);
			Paragraph p2 = new Paragraph(new Chunk(ls));
			p2.setSpacingAfter(-1);
			document.add(p2);
		} catch (DocumentException e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}

	private static void createBillToName(Document document, Customer customer) {
		if (StringUtils.isEmpty(customer.getCustomer_name())) {
			return;
		}
		try {
			Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.NORMAL, BaseColor.GRAY);
			Chunk c = new Chunk("BILL TO", f);
			Paragraph p = new Paragraph(c);
			p.setAlignment(Element.ALIGN_LEFT);
			p.setIndentationLeft(10);
			// p.setSpacingAfter(20);
			p.setSpacingBefore(-40);
			document.add(p);

			Font f2 = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);
			Chunk c2 = new Chunk(customer.getCustomer_name(), f2);
			Paragraph p2 = new Paragraph(c2);
			p2.setAlignment(Element.ALIGN_LEFT);
			p2.setSpacingBefore(-5);
			p2.setIndentationLeft(10);
			document.add(p2);
		} catch (Exception e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}

	private static void createInvoiceDetails(Document document, Invoice invoice) {
		if (invoice == null) {
			return;
		}
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

			Chunk c2 = new Chunk(invoice.getNumber() + "", f2);
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

			Chunk c9 = new Chunk("Amount Due (" + invoice.getCurrency() + "): ", f);
			Phrase amountDueLabel = new Phrase(c9);
			PdfPCell cell_9 = new PdfPCell(amountDueLabel);
			cell_9.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_9.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_9);

			table.setLockedWidth(true);
			table.setTotalWidth(300F);
			table.setHorizontalAlignment(Element.ALIGN_RIGHT);
			document.add(table);

		} catch (Exception e) {
			LOGGER.error(e);
			e.printStackTrace();
		}
	}

}
