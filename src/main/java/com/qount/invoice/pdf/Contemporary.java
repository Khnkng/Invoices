package com.qount.invoice.pdf;

import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
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
import com.qount.invoice.common.PropertyManager;
import com.qount.invoice.model.Company;
import com.qount.invoice.model.Currencies;
import com.qount.invoice.model.Customer;
import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoicePreference;
import com.qount.invoice.utils.CommonUtils;
import com.qount.invoice.utils.Constants;
import com.qount.invoice.utils.Utilities;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 10 Feb 2016
 *
 */

public class Contemporary {
	private static Logger LOGGER = Logger.getLogger(Contemporary.class);

	public static Document createPdf(InvoiceReference invoiceReference, Document document, FileOutputStream fout) throws Exception {
		String imgSrc = "http://lh3.ggpht.com/9tzP9G0EsVP5zCiCrFbdQfbQkFnLzX7kgxYEsTi5gxau7V5G1CsJ0EUJ8U2ugIZxSKMtW4bkbj8z6-eyBEC0eQ=s700";
		InvoicePreference invoicePreference = invoiceReference.getInvoicePreference();
		Invoice invoice = invoiceReference.getInvoice();
		Customer customer = invoiceReference.getCustomer();
		Company company = invoiceReference.getCompany();
		try {
			document.setMargins(10, 10, 4, 20);
			PdfWriter pw = PdfWriter.getInstance(document, fout);
			document.open();
			createTitle(document, invoicePreference);
			createSubheading(document, invoicePreference);
			addImage(document, imgSrc);
			createEmptyLine(document);
			createCompanyDetails(document, company);
			createEmptyLine(document);
			addLineSeparator(document);
			createInvoiceDetails(document, invoice);
			createBillDetails(document, customer);
			createInvoiceDisplayLabels(document, invoicePreference);
			createInvoiceLineItems(document, invoice);
			createTotal(document, invoice);
			createAmountDue(document, invoice);
			createNotes(document, invoice);
			createFooter(pw, document, invoicePreference);
			return document;
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void addImage(Document document, String imgSrc) throws Exception {
		if (StringUtils.isEmpty(imgSrc)) {
			return;
		}
		try {
			Image img = Image.getInstance("");
			img.scaleAbsolute(50, 50);
			float absoluteY = PageSize.A4.getHeight() - img.getScaledHeight();
			absoluteY -= 30f;
			img.setAbsolutePosition(20, absoluteY);
			document.add(img);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
		}
	}

	private static void createTitle(Document document, InvoicePreference invoicePreference) throws Exception {
		if (StringUtils.isEmpty(invoicePreference.getDefaultTitle())) {
			return;
		}
		try {
			Font titleFont = Constants.TITLE_FONT;
			String defaultTitle = StringUtils.isEmpty(invoicePreference.getDefaultTitle()) ? "" : invoicePreference.getDefaultTitle();
			Chunk c = new Chunk(defaultTitle, titleFont);
			Paragraph p = new Paragraph(c);
			p.setAlignment(Element.ALIGN_RIGHT);
			p.setIndentationRight(10);
			document.add(p);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createCompanyDetails(Document document, Company company) throws Exception {
		try {
			Font f1 = Constants.F1;
			Font f2 = Constants.F2;
			PdfPTable table = new PdfPTable(1);
			String companyName = StringUtils.isEmpty(company.getName()) ? "" : company.getName();
			Chunk c1 = new Chunk(companyName, f2);
			Phrase companyNameLabel = new Phrase(c1);
			PdfPCell cellOne = new PdfPCell(companyNameLabel);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellOne.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cellOne);

			String companyAddress = StringUtils.isEmpty(company.getAddress()) ? "" : company.getAddress();
			Chunk c2 = new Chunk(companyAddress, f1);
			Phrase companyAdrsLabel = new Phrase(c2);
			PdfPCell cellTwo = new PdfPCell(companyAdrsLabel);
			cellTwo.setBorder(Rectangle.NO_BORDER);
			cellTwo.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cellTwo);

			String companyCity = StringUtils.isEmpty(company.getCity()) ? "" : company.getCity();
			Chunk c3 = new Chunk(companyCity, f1);
			Phrase companyCityLabel = new Phrase(c3);
			PdfPCell cellThree = new PdfPCell(companyCityLabel);
			cellThree.setBorder(Rectangle.NO_BORDER);
			cellThree.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cellThree);

			String companyCountry = StringUtils.isEmpty(company.getCountry()) ? "" : company.getCountry();
			Chunk c4 = new Chunk(companyCountry, f1);
			Phrase companyCountryLabel = new Phrase(c4);
			PdfPCell cellFour = new PdfPCell(companyCountryLabel);
			cellFour.setBorder(Rectangle.NO_BORDER);
			cellFour.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cellFour);

			String phoneNumnber = StringUtils.isBlank(company.getPhone_number()) ? "" : "Phone: " + company.getPhone_number();
			Chunk c5 = new Chunk(phoneNumnber, f1);
			Phrase companyPhoneLabel = new Phrase(c5);
			PdfPCell cellFive = new PdfPCell(companyPhoneLabel);
			cellFive.setBorder(Rectangle.NO_BORDER);
			cellFive.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cellFive);

			String webSiteUrl = PropertyManager.getProperty("site.url", "www.qount.io");
			Chunk c6 = new Chunk(webSiteUrl, f1);
			Phrase companyWebsiteLabel = new Phrase(c6);
			PdfPCell cellSix = new PdfPCell(companyWebsiteLabel);
			cellSix.setBorder(Rectangle.NO_BORDER);
			cellSix.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cellSix);

			table.setLockedWidth(true);
			table.setTotalWidth(300F);
			table.setHorizontalAlignment(Element.ALIGN_RIGHT);
			document.add(table);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createEmptyLine(Document document) throws Exception {
		try {
			Paragraph p2 = new Paragraph("\n");
			p2.setSpacingBefore(-10);
			document.add(p2);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createSubheading(Document document, InvoicePreference invoicePreference) throws Exception {
		if (StringUtils.isEmpty(invoicePreference.getDefaultSubHeading())) {
			return;
		}
		try {
			Font f2 = Constants.SUBHEADING_FONT;
			String defaultSubHeading = StringUtils.isBlank(invoicePreference.getDefaultSubHeading()) ? "" : invoicePreference.getDefaultSubHeading();
			Chunk c2 = new Chunk(defaultSubHeading, f2);
			Paragraph p2 = new Paragraph(c2);
			p2.setSpacingBefore(-5);
			p2.setIndentationRight(10);
			p2.setAlignment(Element.ALIGN_RIGHT);
			document.add(p2);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void addLineSeparator(Document document) throws Exception {
		try {
			LineSeparator ls = new LineSeparator();
			ls.setLineColor(BaseColor.GRAY);
			Paragraph p2 = new Paragraph(new Chunk(ls));
			p2.setSpacingAfter(10);
			document.add(p2);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createBillDetails(Document document, Customer customer) throws Exception {
		try {
			Font f = Constants.SUBHEADING_FONT;
			Chunk c = new Chunk("BILL TO", f);
			Paragraph p = new Paragraph(c);
			p.setAlignment(Element.ALIGN_LEFT);
			p.setIndentationLeft(10);
			p.setSpacingBefore(-85);
			document.add(p);

			String customerName = StringUtils.isBlank(customer.getCustomer_name()) ? "" : customer.getCustomer_name();
			Font f2 = Constants.F2;
			Chunk c2 = new Chunk(customerName, f2);
			Paragraph p2 = new Paragraph(c2);
			p2.setAlignment(Element.ALIGN_LEFT);
			p2.setSpacingBefore(-5);
			p2.setIndentationLeft(10);
			document.add(p2);

			createEmptyLine(document);

			// String customerEmail =
			// CommonUtils.isValidJSONArray(customer.getEmail_ids()) ? "" :
			// customer.getEmail_ids().toString();
			// Font f3 = Constants.F1;
			// Chunk c3 = new Chunk(customerEmail, f3);
			// Paragraph p3 = new Paragraph(c3);
			// p3.setAlignment(Element.ALIGN_LEFT);
			// p3.setSpacingBefore(-5);
			// p3.setIndentationLeft(10);
			// document.add(p3);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createInvoiceDetails(Document document, Invoice invoice) throws Exception {
		if (invoice == null) {
			return;
		}
		try {
			Font f = Constants.F2;
			Font f2 = Constants.F1;
			Font f3 = Constants.CURRENCY_FONT;

			PdfPTable table = new PdfPTable(2);

			Chunk c1 = new Chunk("Invoice Number: ", f);
			Phrase invocieNumberLabel = new Phrase(c1);
			PdfPCell cellOne = new PdfPCell(invocieNumberLabel);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellOne.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cellOne);

			String invoiceNumberStr = invoice.getNumber() + "";
			Chunk c2 = new Chunk(invoiceNumberStr, f2);
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

			String invoicePo_number = StringUtils.isBlank(invoice.getNumber()) ? "" : invoice.getNumber();
			Chunk c4 = new Chunk(invoicePo_number, f2);
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

			String invoice_date = StringUtils.isBlank(invoice.getInvoice_date()) ? "" : invoice.getInvoice_date();
			Chunk c6 = new Chunk(invoice_date, f2);
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

			String invoicePonumber = invoice.getAmount_due() + "";
			Chunk c8 = new Chunk(invoicePonumber, f2);
			Phrase paymentDue = new Phrase(c8);
			PdfPCell cell_8 = new PdfPCell(paymentDue);
			cell_8.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_8);

			Currencies currencies = invoice.getCurrencies();
			String currenciesCode = "";
			String currenciesSymbol = "";
			if (currencies != null) {
				currenciesCode = StringUtils.isEmpty(currencies.getCode()) ? "" : currencies.getCode();
				currenciesSymbol = Utilities.getCurrencySymbol(currenciesCode);
			}
			Chunk c9 = new Chunk("Amount Due (" + currenciesCode + "): ", f);
			Phrase amountDueLabel = new Phrase(c9);
			PdfPCell cell_9 = new PdfPCell(amountDueLabel);
			cell_9.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_9.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_9);
			String curAmount = currenciesSymbol + " " + invoice.getAmount_due();
			Chunk c10 = new Chunk(curAmount, f3);
			Phrase amountDue = new Phrase(c10);
			PdfPCell cell_10 = new PdfPCell(amountDue);
			cell_10.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell_10);

			table.setLockedWidth(true);
			table.setTotalWidth(300F);
			table.setHorizontalAlignment(Element.ALIGN_RIGHT);
			document.add(table);

		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createInvoiceDisplayLabels(Document document, InvoicePreference invoicePreference) throws Exception {
		if (invoicePreference == null) {
			return;
		}
		try {
			Font f = Constants.F3;
			PdfPTable table = new PdfPTable(4);
			String items = StringUtils.isEmpty(invoicePreference.getItems()) ? "" : invoicePreference.getItems();
			Chunk c1 = new Chunk(items, f);
			Phrase invocieItemsLabel = new Phrase(c1);
			PdfPCell cellOne = new PdfPCell(invocieItemsLabel);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellOne.setBackgroundColor(BaseColor.DARK_GRAY);
			cellOne.setHorizontalAlignment(Element.ALIGN_LEFT);
			cellOne.setPaddingLeft(20f);
			cellOne.setMinimumHeight(20f);
			table.addCell(cellOne);

			String units = StringUtils.isEmpty(invoicePreference.getUnits()) ? "" : invoicePreference.getUnits();
			Chunk c3 = new Chunk(units, f);
			Phrase poNumberLabel = new Phrase(c3);
			PdfPCell cell_3 = new PdfPCell(poNumberLabel);
			cell_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_3.setBorder(Rectangle.NO_BORDER);
			cell_3.setBackgroundColor(BaseColor.DARK_GRAY);
			table.addCell(cell_3);

			String price = StringUtils.isEmpty(invoicePreference.getPrice()) ? "" : invoicePreference.getPrice();
			Chunk c5 = new Chunk(price, f);
			Phrase invocieDateLabel = new Phrase(c5);
			PdfPCell cell_5 = new PdfPCell(invocieDateLabel);
			cell_5.setBorder(Rectangle.NO_BORDER);
			cell_5.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_5.setBackgroundColor(BaseColor.DARK_GRAY);
			table.addCell(cell_5);

			String amount = StringUtils.isEmpty(invoicePreference.getAmount()) ? "" : invoicePreference.getAmount();
			Chunk c7 = new Chunk(amount, f);
			Phrase paymentDueLabel = new Phrase(c7);
			PdfPCell cell_7 = new PdfPCell(paymentDueLabel);
			cell_7.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell_7.setBorder(Rectangle.NO_BORDER);
			cell_7.setBackgroundColor(BaseColor.DARK_GRAY);
			cell_7.setPaddingRight(20f);
			table.addCell(cell_7);

			table.setSpacingBefore(50);
			table.setWidthPercentage(100);
			document.add(table);

		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createInvoiceLineItems(Document document, Invoice invoice) throws Exception {
		if (null == invoice || invoice.getInvoiceLines() == null || invoice.getInvoiceLines().isEmpty()) {
			return;
		}
		try {
			PdfPTable table = new PdfPTable(4);
			Font f = Constants.F1;
			Font f3 = Constants.CURRENCY_FONT;
			Iterator<InvoiceLine> invoiceLinesItr = invoice.getInvoiceLines().iterator();
			while (invoiceLinesItr.hasNext()) {
				InvoiceLine invoiceLine = invoiceLinesItr.next();
				String itemName = StringUtils.isEmpty(invoiceLine.getItem_name()) ? invoiceLine.getDescription() : invoiceLine.getItem_name();
				Chunk c1 = new Chunk(itemName, f);
				Phrase invocieItemsLabel = new Phrase(c1);
				PdfPCell cellOne = new PdfPCell(invocieItemsLabel);
				cellOne.setBorder(Rectangle.NO_BORDER);
				cellOne.setHorizontalAlignment(Element.ALIGN_LEFT);
				cellOne.setPaddingLeft(20f);
				cellOne.setMinimumHeight(20f);
				table.addCell(cellOne);

				String quantity = invoiceLine.getQuantity() + "";
				Chunk c3 = new Chunk(quantity, f);
				Phrase poNumberLabel = new Phrase(c3);
				PdfPCell cell_3 = new PdfPCell(poNumberLabel);
				cell_3.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell_3.setBorder(Rectangle.NO_BORDER);
				table.addCell(cell_3);

				Currencies currencies = invoice.getCurrencies();
				String currenciesSymbol = "";
				if (currencies != null) {
					currenciesSymbol = Utilities.getCurrencySymbol(invoice.getCurrencies().getCode());
					// currenciesJava_symbol =
					// StringUtils.isEmpty(currencies.getJava_symbol()) ? "" :
					// currencies.getJava_symbol();
				}
				Chunk c5 = new Chunk(currenciesSymbol + " " + invoiceLine.getPrice(), f3);
				Phrase invocieDateLabel = new Phrase(c5);
				PdfPCell cell_5 = new PdfPCell(invocieDateLabel);
				cell_5.setBorder(Rectangle.NO_BORDER);
				cell_5.setHorizontalAlignment(Element.ALIGN_RIGHT);
				table.addCell(cell_5);

				Chunk c7 = new Chunk(currenciesSymbol + " " + invoiceLine.getAmount(), f3);
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
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createTotal(Document document, Invoice invoice) throws Exception {
		if (null == invoice) {
			return;
		}
		try {
			PdfPTable table = new PdfPTable(4);
			Font f = Constants.F2;
			Font f3 = Constants.CURRENCY_FONT;
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

			Currencies currencies = invoice.getCurrencies();
			String currencySymbol = "";
			if (currencies != null) {
				currencySymbol = Utilities.getCurrencySymbol(invoice.getCurrencies().getCode());
			}
			// Chunk c3 = new Chunk(html_symbol + " " + invoice.getAmount(),
			// f3);
			Chunk c3 = new Chunk(currencySymbol + " " + invoice.getAmount(), f3);
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
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createAmountDue(Document document, Invoice invoice) throws Exception {
		if (null == invoice) {
			return;
		}
		try {
			PdfPTable table = new PdfPTable(4);
			Font f = Constants.F2;
			Font f3 = Constants.CURRENCY_FONT;

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

			Currencies currencies = invoice.getCurrencies();
			String code = "";
			String currencySymbol = "";
			if (currencies != null) {
				code = currencies.getCode();
				currencySymbol = Utilities.getCurrencySymbol(code);
			}
			Chunk c1 = new Chunk("Amount Due(" + code + "):", f);
			Phrase invocieItemsLabel = new Phrase(c1);
			PdfPCell cellOne = new PdfPCell(invocieItemsLabel);
			cellOne.setBorder(Rectangle.NO_BORDER);
			cellOne.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cellOne.setPaddingLeft(20f);
			cellOne.setMinimumHeight(20f);
			table.addCell(cellOne);

			Chunk c3 = new Chunk(currencySymbol + " " + invoice.getAmount_due(), f3);
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
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createNotes(Document document, Invoice invoice) throws Exception {
		if (null == invoice || StringUtils.isEmpty(invoice.getNotes())) {
			return;
		}
		try {
			Font f2 = Constants.F2;
			Chunk c2 = new Chunk("Notes", f2);
			Paragraph p2 = new Paragraph(c2);
			p2.setAlignment(Element.ALIGN_LEFT);
			p2.setIndentationLeft(10);

			String notes = invoice.getNotes();
			Font f = Constants.F1;
			Chunk c = new Chunk(notes, f);
			Paragraph p = new Paragraph(c);
			p.setAlignment(Element.ALIGN_LEFT);
			p.setIndentationLeft(10);

			document.add(p2);
			document.add(p);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}

	private static void createFooter(PdfWriter writer, Document document, InvoicePreference invoicePreference) throws Exception {
		if (null == invoicePreference || StringUtils.isEmpty(invoicePreference.getDefaultFooter())) {
			return;
		}
		try {
			PdfContentByte cb = writer.getDirectContent();
			Font f2 = Constants.SUBHEADING_FONT;
			Chunk c2 = new Chunk(invoicePreference.getDefaultFooter(), f2);
			Phrase p2 = new Phrase(c2);
			ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, p2, (document.right() - document.left()) / 2 + document.leftMargin(), document.bottom() - 10, 0);
		} catch (Exception e) {
			LOGGER.error(CommonUtils.getErrorStackTrace(e));
			throw e;
		}
	}
}
