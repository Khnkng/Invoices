package com.qount.invoice.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.qount.invoice.model.Company;
import com.qount.invoice.model.Currencies;
import com.qount.invoice.model.Customer;
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
		Document document = null;
		FileOutputStream fout = null;
		try {
			if (null != invoiceReference && !StringUtils.isBlank(invoiceReference.getInvoiceType())) {
				document = new Document();
				pdfFile = new File(UUID.randomUUID().toString()+".pdf");
//				pdfFile = new File("F:/1.pdf");
				fout = new FileOutputStream(pdfFile);
//				System.out.println(pdfFile.getAbsolutePath());
				switch (invoiceReference.getInvoiceType().toLowerCase()) {
				case "contemporary":
					document = Contemporary.createPdf(invoiceReference,document,fout);
					break;
				case "modern":
					document = Modern.createPdf(invoiceReference,document,fout);
					break;
				case "classic":
					document = Classic.createPdf(invoiceReference,document,fout);
					break;

				default:
					break;
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}finally {
			PdfUtil.closeDocumentStream(document);
			PdfUtil.closeFileStream(fout);
		}
		return pdfFile;
	}
	
	
	public static InvoiceReference getMockDataForContemporary(){
		try {
			Currencies currencies = new Currencies();
			currencies.setCode("INR");
			currencies.setJava_symbol("₹");
			currencies.setName("Rupee");
			InvoicePreference invoicePreference = new InvoicePreference();
			Invoice invoice = new Invoice();
			invoice.setCurrencies(currencies);
			invoice.setAmount(1.00);
			invoice.setAmount_due(1.00);
			invoice.setNotes("Standard memo 1");
			invoicePreference.setDefaultFooter("Default footer 1");
			invoicePreference.setDefaultTitle("DEFAULT TITLE 1");
			invoicePreference.setDefaultSubHeading("Default subheading 1");
//			invoice.setNumber(6);
			invoice.setPo_number("po1");
			invoice.setInvoice_date("February 1, 2017");
			invoice.setAcceptance_date("February 2, 2017");
			invoice.setAmount_due(1.0d);
//			invoice.setCurrency("\u20B9");
			invoice.setCurrencies(currencies);
			invoicePreference.setItems("Items");
			invoicePreference.setUnits("Qunatity");
			invoicePreference.setPrice("Price");
			invoicePreference.setAmount("Amount");
			ArrayList<InvoiceLine> invoiceLines = new ArrayList<>();
			InvoiceLine e = new InvoiceLine();
//			e.setItem_name("a2");
			e.setDescription("item 1");
			e.setQuantity(1);
			e.setPrice(1.0);
			e.setAmount(1.0);
			e.setDescription("item1");
//			e.setCurrency("\u20B9");
			invoiceLines.add(e);
			invoice.setInvoiceLines(invoiceLines);
			InvoiceReference invoiceReference = new InvoiceReference();
			invoiceReference.setInvoiceType("contemporary");
			invoiceReference.setInvoice(invoice);
			invoiceReference.setInvoicePreference(invoicePreference);
			Company company = new Company();
			company.setName("company1");
			company.setAddress("banjara hills");
			company.setCity("Hyderabad");
			company.setCountry("India");
			company.setPhone_number("8801446657");
			invoiceReference.setCompany(company);
			Customer customer = new Customer();
			customer.setEmail_id("makjavaprogrammer@gmail.com");
			customer.setCustomer_name("mateen");
			invoiceReference.setCustomer(customer);
			return invoiceReference;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static InvoiceReference getMockDataForModern(){
		try {
			Currencies currencies = new Currencies();
			currencies.setCode("INR");
			currencies.setHtml_symbol("\u20B9");
			currencies.setName("Rupee");
			InvoicePreference invoicePreference = new InvoicePreference();
			Invoice invoice = new Invoice();
			invoice.setCurrencies(currencies);
			invoice.setAmount(1.00);
			invoice.setAmount_due(1.00);
			invoice.setDescription("item1");
			invoice.setNotes("Standard memo 1");
			invoicePreference.setDefaultFooter("Default footer 1");
			invoicePreference.setDefaultTitle("DEFAULT TITLE 1");
			invoicePreference.setDefaultSubHeading("Default subheading 1");
//			invoice.setNumber(6);
			invoice.setPo_number("po1");
			invoice.setInvoice_date("February 1, 2017");
			invoice.setAcceptance_date("February 2, 2017");
			invoice.setAmount_due(1.0d);
			invoice.setCurrency("INR");
			invoicePreference.setItems("Items");
			invoicePreference.setUnits("Qunatity");
			invoicePreference.setPrice("Price");
			invoicePreference.setAmount("Amount");
			ArrayList<InvoiceLine> invoiceLines = new ArrayList<>();
			InvoiceLine e = new InvoiceLine();
			e.setQuantity(1);
			e.setPrice(1.0);
			e.setAmount(1.0);
			e.setDescription("item1");
			invoiceLines.add(e);
			invoice.setInvoiceLines(invoiceLines);
			Customer customer = new Customer();
			customer.setCustomer_name("Apurva");
			customer.setCustomer_address("Banjara hills");
			customer.setCustomer_city("Hyderabad, Telengana");
			customer.setCustomer_country("India");
			customer.setPhone_number("040-232356");
			InvoiceReference invoiceReference = new InvoiceReference();
			invoiceReference.setInvoiceType("modern");
			invoiceReference.setInvoice(invoice);
			invoiceReference.setInvoicePreference(invoicePreference);
			invoiceReference.setCustomer(customer);
			return invoiceReference;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
