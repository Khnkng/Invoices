package com.qount.invoice.pdf;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.qount.invoice.model.Invoice;
import com.qount.invoice.model.InvoiceLine;
import com.qount.invoice.model.InvoicePreference;

/**
 * 
 * @author Mateen, Qount.
 * @version 1.0, 10 Feb 2016
 *
 */
public class PdfGenerator {

	private static final Logger LOGGER = Logger.getLogger(PdfGenerator.class);

	public static File createPdf(InvoiceReference invoiceReference) {
		File pdfFile = null;
		try {
			if (null != invoiceReference && !StringUtils.isBlank(invoiceReference.getInvoiceType())) {
				switch (invoiceReference.getInvoiceType()) {
				case "contemporary":
					pdfFile = Contemporary.createPdf(invoiceReference);
					break;

				default:
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return pdfFile;
	}
	
	public static void main(String[] args) {
		try {
			InvoicePreference invoicePreference = new InvoicePreference();
			Invoice invoice = new Invoice();
			invoice.setAmount(1.00);
			invoice.setAmount_due(1.00);
			invoice.setNotes("Standard memo 1");
			invoicePreference.setDefaultFooter("Default footer 1");
			invoicePreference.setDefaultTitle("DEFAULT TITLE 1");
			invoicePreference.setDefaultSubHeading("Default subheading 1");
			invoice.setCompany_name("company1");
			invoice.setNumber(6);
			invoice.setPo_number("po1");
			invoice.setInvoice_date("February 1, 2017");
			invoice.setAcceptance_date("February 2, 2017");
			invoice.setAmount_due(1.0d);
			invoice.setCurrency("INR");
			invoice.setCustomer_name("mateen");
			invoicePreference.setItems("Items");
			invoicePreference.setUnits("Qunatity");
			invoicePreference.setPrice("Price");
			invoicePreference.setAmount("Amount");
			ArrayList<InvoiceLine> invoiceLines = new ArrayList<>();
			InvoiceLine e = new InvoiceLine();
			e.setItem_name("a2");
			e.setQuantity(1);
			e.setPrice(1.0);
			e.setAmount(1.0);
			e.setCurrency("INR");
			invoiceLines.add(e);
			invoice.setInvoiceLines(invoiceLines);
			InvoiceReference invoiceReference = new InvoiceReference();
			invoiceReference.setInvoiceType("contemporary");
			invoiceReference.setInvoice(invoice);
			invoiceReference.setInvoicePreference(invoicePreference);
			File pdf = createPdf(invoiceReference);
			PdfUtil.deleteFile(pdf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
