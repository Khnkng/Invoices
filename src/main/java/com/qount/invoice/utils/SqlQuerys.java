package com.qount.invoice.utils;

public class SqlQuerys {
	
	public final class Invoice {
		public static final String INSERT_QRY = "insert into `invoice` (`id`,`user_id`,`company_id`,`amount`,`currency`,`description`,`objectives`,`last_updated_by`,`last_updated_at`,`state`,`invoice_date`,`notes`,`discount`,`deposit_amount`,`processing_fees`,`payment_spring_customer_id`,`po_number`,`document_id`,`amount_due`,`payment_date`,`customer_id`,`sub_totoal`,`amount_by_date`,`created_at`,`amount_paid`,`number`,`created_at_millis`,`term`,`recepients_mails`,`plan_id`,`is_recurring`,`plan_frequency`,`plan_name`,`plan_amount`,`plan_day`,`plan_ends_after`,`plan_bill_immediately`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public static final String UPDATE_QRY = "UPDATE `invoice` SET  `user_id` =?,`company_id` =?,`amount` =?,`currency` =?,`description` =?,`objectives` =?,`last_updated_by` =?,`last_updated_at` =?,`state` =?,`invoice_date` =?,`notes` =?,`discount` =?,`deposit_amount` =?,`processing_fees` =?,`payment_spring_customer_id` =?,`po_number` =?,`document_id` =?,`amount_due` =?,`payment_date` =?,`sub_totoal` =?,`customer_id` =?, `amount_by_date` = ?, `amount_paid` = ?, `number` =?, `term` = ?, `recepients_mails` = ?, `plan_id` = ?, `is_recurring`=?, `plan_frequency`=?, `plan_name`=?, `plan_amount`=?, `plan_day`=?, `plan_ends_after`=?, `plan_bill_immediately`=? WHERE `id` =?";
		public final static String DELETE_QRY = "DELETE FROM invoice WHERE `id`=?;";
		public final static String GET_QRY = "SELECT cc.`card_name`,i.`plan_bill_immediately`,i.`plan_ends_after`,i.`plan_day`,i.`plan_amount`,i.`plan_name`,i.`plan_frequency`,i.`plan_id`,i.`recepients_mails`,i.`created_at` i_created_at,i.`term` i_term, i.`number` i_number,i.`id`,i.`user_id`,i.`company_id`,i.`amount`,i.`currency`,i.`description`,i.`objectives`,i.`last_updated_by`,i.`last_updated_at`,i.`customer_id`,i.`state`, i.`invoice_date`,i.`notes`,i.`discount`,i.`deposit_amount`,i.`processing_fees`,i.`is_recurring`,cc.`payment_spring_id`,i.`po_number`,i.`document_id`,i.`amount_due`,i.`payment_date`,i.`sub_totoal`, i.`amount_by_date`,c.`code`,c.`name`,c.`html_symbol`,c.`java_symbol`, il.`id` AS `ilid`,il.`item_id` AS `il_item_id`,it.`name` AS `il_item_name`,il.`invoice_id`, il.`description` `il_description`,il.`objectives` `il_objectives`,il.`amount` `il_amount`,il.`last_updated_by` `il_last_updated_by`,  il.`last_updated_at` `il_last_updated_at`,il.`quantity` `il_quantity`,il.`price` `il_price`,il.`notes` `il_notes`,il.`coa_id` `il_coa_id`,coa.`name` `il_coa_name`,    ilt.`invoice_line_id` `ilt_invoice_line_id`,ilt.`tax_id` `ilt_tax_id`,ct.`tax_rate` `ilt_tax_rate`,ct.`name` `ilt_name`,cc.`payment_spring_id`,  cc.`customer_name`,cc.`email_ids`,comp.`name` `company_name`  FROM `invoice` i    LEFT JOIN `currencies` c ON i.currency=c.code    LEFT JOIN `invoice_lines` il ON i.id=il.invoice_id    LEFT JOIN `invoice_line_taxes` ilt ON il.id=ilt.invoice_line_id    LEFT JOIN `company_taxes` ct ON ilt.tax_id = ct.`id`   LEFT JOIN `company_customers` cc ON cc.`customer_id` = i.`customer_id`  LEFT JOIN `items` it ON il.`item_id` = it.`id`  LEFT JOIN `chart_of_accounts` coa ON il.`coa_id` = coa.`id` LEFT JOIN `company` comp ON i.`company_id` = comp.`id` WHERE i.id  =?;";
		public final static String GET_INVOICES_LIST_QRY = "SELECT `number`,`id`,`po_number`,`invoice_date`,`payment_date`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`customer_id`,`state`,`amount_due`,`sub_totoal`,`amount_by_date` FROM invoice WHERE ";
		public final static String GET_INVOICES_PDF_QRY = "SELECT ip.`items`,ip.`units`,ip.`price`,ip.`amount`,ip.`default_title`,ip.`template_type`,ip.`default_sub_heading`,  c.`name`,c.`address`,c.`city`,c.`state`,c.`country`,c.`phone_number`,cc.`customer_name`,cc.`email_ids`,ip.`standard_memo`,ip.`default_footer` FROM `invoice_preferences` ip LEFT JOIN `company` c ON ip.`company_id` = c.`id` LEFT JOIN `company_customers` cc ON ip.`company_id` = cc.`company_id` WHERE ip.`company_id`= ? AND cc.customer_id=?;";
		public final static String GET_INVOICES_MAIL_QRY = "SELECT cur.`html_symbol`,i.`recepients_mails`,i.`created_at` i_created_at, i.`number` i_number,cc.`customer_name` cust_name,i.`invoice_date` invoice_date,c.`name` comp_name, i.`currency` i_currency,i.`amount` i_amount,i.`amount_by_date` i_amount_by_date,cc.`email_ids` cust_email_ids FROM invoice i LEFT JOIN `currencies` cur ON i.`currency` = cur.`code` LEFT JOIN company_customers cc ON cc.customer_id = i.customer_id LEFT JOIN company c ON c.id = i.company_id WHERE i.id =? limit 1;";
		public final static String QOUNT_QRY = "SELECT ( SELECT  COUNT(id) FROM invoice WHERE state='paid' AND company_id=? AND user_id= ? ) AS invoice_paid,( SELECT  COUNT(id) FROM invoice WHERE state!='paid' OR state IS NULL AND company_id=? AND user_id= ? ) AS invoice_unpaid,( SELECT COUNT(id) FROM `proposal` WHERE company_id=? AND user_id= ? ) AS proposal_count FROM DUAL;";
		public final static String UPDATE_STATE_QRY = "UPDATE invoice SET state=? WHERE id=?;";
	}
	
	public final class InvoiceLine {
		public final static String INSERT_QRY = "INSERT INTO `invoice_lines` (`id`,`invoice_id`,`description`,`objectives`,`amount`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id`,`coa_id`) values (?,?,?,?,?,?,?,?,?,?,?,?);";
		public final static String UPDATE_QRY = "update `invoice_lines` SET `invoice_id`=?,`description`=?,`objectives`=?,`amount`=?,`last_updated_by`=?,`last_updated_at`=?,`quantity`=?,`price`=?,`notes`=?, `item_id`=?, `coa_id` = ? where `id`=?";
		public final static String GET_LINES_QRY = "SELECT `id`,`invoice_id`,`description`,`objectives`,`amount`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id`,`coa_id` FROM invoice_lines WHERE `invoice_id` = ?;";
		public final static String DELETE_INVOICE_LINE_QRY = "DELETE FROM `invoice_lines` WHERE `id` = ?";
		public final static String DELETE_INVOICE_BY_ID_QRY = "DELETE FROM `invoice_lines` WHERE `invoice_id` = ?";
	}
	
	public final class InvoiceLineTaxes {
		public final static String INSERT_QRY = "INSERT INTO invoice_line_taxes (`invoice_line_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";
		public final static String DELETE_QRY = "DELETE FROM invoice_line_taxes WHERE `invoice_line_id` = ?;";
	}
	
	public final class InvoicePreference {
		public static final String INSERT_QRY = "INSERT INTO `invoice_preferences` (`id`,`company_id`,`template_type`,`company_logo`,`display_logo`,`accent_color`,`default_payment_terms`,`default_title`,`default_sub_heading`,`default_footer`,`standard_memo`,`items`,`units`,`price`,`amount`,`hide_item_name`,`hide_item_description`,`hide_units`,`hide_price`,`hide_amount`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public static final String UPDATE_QRY = "UPDATE `invoice_preferences` SET `template_type` = ?, `company_logo` = ?, `display_logo` = ?, `accent_color` = ?, `default_payment_terms` = ?, `default_title` = ?, `default_sub_heading` = ?, `default_footer` = ?, `standard_memo` = ?, `items` = ?, `units` = ?, `price` = ?, `amount` = ?, `hide_item_name` = ?, `hide_item_description` = ?, `hide_units` = ?, `hide_price` = ?, `hide_amount` = ?, `company_id`= ?  WHERE `id` = ?";
		public final static String DELETE_QRY = "DELETE FROM invoice_preferences WHERE `id`=?;";
		public final static String GET_QRY = " SELECT `id`,`company_id`,`template_type`,`company_logo`,`display_logo`,`accent_color`,`default_payment_terms`,`default_title`,`default_sub_heading`,`default_footer`,`standard_memo`,`items`,`units`,`price`,`amount`,`hide_item_name`,`hide_item_description`,`hide_units`,`hide_price`,`hide_amount` FROM invoice_preferences WHERE `company_id` = ?  limit 1";
	}
	
	public final class InvoiceTaxes {
		public final static String INSERT_QRY = "INSERT INTO invoice_taxes (`invoice_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";
		public final static String DELETE_QRY = "DELETE FROM `invoice_taxes` WHERE `invoice_id`=?;";
		public final static String GET_QRY = "SELECT it.`invoice_id`,it.`tax_id`,ct.`name`,ct.`tax_rate` FROM invoice_taxes it LEFT JOIN `company_taxes` ct ON it.tax_id = ct.`id`WHERE `invoice_id` = ?;";
	}
	
	public final class Proposal {
		public final static String INSERT_QRY = "INSERT INTO `proposal` (`id`,`user_id`,`company_id`,`amount`,`currency`,`description`,`objectives`,`last_updated_by`,`last_updated_at`,`state`,`proposal_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json`,`amount_by_date`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public final static String UPDATE_QRY = "UPDATE `proposal` SET `user_id` = ?,`company_id` = ?,`amount` = ?,`currency` = ?,`description` = ?,`objectives` = ?,`last_updated_by` = ?,`last_updated_at` = ?,`state` = ?,`proposal_date` = ?,`acceptance_date` = ?,`acceptance_final_date` = ?,`notes` = ?,`discount` = ?,`deposit_amount` = ?,`processing_fees` = ?,`remainder_json` = ?,`remainder_mail_json`= ?, `amount_by_Date` = ? WHERE `id` = ?";
		public final static String DELETE_QRY = "DELETE FROM proposal WHERE `id`=?;";
		public final static String GET_QRY = "SELECT p.`id`,p.`user_id`,p.`company_id`,p.`amount`,p.`currency`,p.`description`,p.`objectives`,p.`last_updated_by`,p.`last_updated_at`,p.`state`,p.`proposal_date`,p.`acceptance_date`,p.`acceptance_final_date`,p.`notes`,p.`discount`,p.`deposit_amount`,p.`processing_fees`,p.`remainder_json`,p.`remainder_mail_json`,p.`amount_by_date`, pl.`id` AS `plid`,pl.`proposal_id`,pl.`description` `pl_description`,pl.`objectives` `pl_objectives`,pl.`amount` `pl_amount`,pl.`currency` `pl_currency`,pl.`last_updated_by` `pl_last_updated_by`,pl.`last_updated_at` `pl_last_updated_at`,pl.`quantity` `pl_quantity`,pl.`price` `pl_price`,pl.`notes` `pl_notes`, plt.`proposal_line_id` `plt_proposal_line_id`,plt.`tax_id` `plt_tax_id`,plt.`tax_rate` `plt_tax_rate` FROM `proposal` p LEFT JOIN `proposal_lines` pl ON p.id=pl.proposal_id LEFT JOIN `proposal_line_taxes` plt ON pl.id =plt.proposal_line_id WHERE p.id = ?;";
		public final static String GET_PROPOSAL_LIST_QRY = "SELECT `id`,`user_id`,`company_id`,`amount`,`currency`,`description`, `objectives`,`last_updated_by`,`last_updated_at`,`state`,`proposal_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json`,`amount_by_date` FROM proposal WHERE `user_id` = ? AND `company_id` = ?;";
	}
	
	public final class ProposalLine {
		public final static String INSERT_QRY = "INSERT INTO `proposal_lines` (`id`,`proposal_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id`,`coa_id`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public final static String UPADTE_QRY = "UPDATE `proposal_lines` SET `description` = ?,`objectives` = ?,`amount`= ?,`currency` = ?,`last_updated_by`= ?,`last_updated_at` = ?,`quantity` = ?,`price` = ?,`notes` = ?,`item_id` = ?,`coa_id` = ? WHERE `id` = ? AND `proposal_id` = ?;";
		public final static String GET_LINES_QRY = "SELECT `id`,`proposal_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id`,`coa_id` FROM proposal_lines WHERE `id` = ?;";
		public final static String DELETE_PROPOSAL_LINE_QRY = "DELETE FROM `proposal_lines` WHERE `id` = ?";
	}
	
	public final class ProposalLineTaxes {
		public final static String INSERT_QRY = "INSERT INTO proposal_line_taxes (`proposal_line_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";
		public final static String DELETE_QRY = "DELETE FROM proposal_line_taxes WHERE `proposal_line_id` = ?;";
	}
	
	public final class ProposalTaxes {
		public final static String INSERT_QRY = "INSERT INTO proposal_taxes (`proposal_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";
		public final static String DELETE_QRY = "DELETE FROM `proposal_taxes` WHERE `proposal_id`=?;";
		public final static String GET_QRY = "SELECT * FROM proposal_taxes WHERE `proposal_id` = ?;";
	}

	public final class Company{
		public static final String INSERT_QRY = "INSERT INTO company ( `id`, `name`, `ein`, `type`, `phone_number`, `address`, `city`, `state`, `country`, `zipcode`, `currency`, `email`, `payment_info`, `createdBy`, `modifiedBy`, `createdDate`, `modifiedDate`, `owner`, `active` ) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );";
		public static final String UPDATE_QRY = "UPDATE company SET `name` = ?, `ein` = ?, `type` = ?, `phone_number` = ?, `address` = ?, `city` = ?, `state` = ?, `country` = ?, `zipcode` = ?, `currency` = ?, `email` = ?, `payment_info` = ?, `createdBy` = ?, `modifiedBy` = ?, `createdDate` = ?, `modifiedDate` = ?, `owner` = ?, `active` = ? WHERE `id` = ?;";
		public static final String DELETE_QRY = "DELETE FROM company WHERE  WHERE `id` = ?;";
		public static final String GET_QRY = "SELECT `id`, `name`, `ein`, `type`, `phone_number`, `address`, `city`, `state`, `country`, `zipcode`, `currency`, `email`, `payment_info`, `createdBy`, `modifiedBy`, `createdDate`, `modifiedDate`, `owner`, `active` from `company` WHERE `id` = ?;";
		public static final String GET_ALL_QRY = "SELECT * FROM company;";

	}
	
	public final class InvoicePayment{
		public static final String INSERT_QRY = "INSERT INTO invoice_payments ( `id`,`invoice_id`,`transaction_id`,`amount`,`transaction_date`,`status`,`period_start`,`period_end`,`currency_from`,`currency_to`,`conversion`,`conversionDate`,`currency_amount`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		public static final String GET_BY_ID_QRY = "SELECT `id`,`invoice_id`,`transaction_id`,`amount`,`transaction_date`,`status`,`period_start`,`period_end` from `invoice_payments` WHERE `id` = ?;";
		public static final String GET_BY_INVOCIE_ID_QRY = "SELECT `id`,`invoice_id`,`transaction_id`,`amount`,`transaction_date`,`status`,`period_start`,`period_end` from `invoice_payments` WHERE `invoice_id` = ?;";

	}
	
	public final class Currencies {
		public static final String RETRIEVE_LIST = "select `code`,`name`,`html_symbol`,`java_symbol` from  currencies;";
		public static final String GET = "select `code`,`name`,`html_symbol`,`java_symbol` from  currencies where `code` = ?";
	}
	
	public final class Customer {
		public static final String INSERT_QRY = "INSERT INTO  company_customers (`user_id`,`company_id`,`customer_id`,`customer_address`,`customer_city`,`customer_country`,`customer_ein`,`customer_state`,`customer_name`,`customer_zipcode`,`email_ids`,`phone_number`,`coa`, `payment_spring_id`,`term`)VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public static final String UPDATE_QRY = "UPDATE  company_customers SET `customer_address` = ?,`customer_city` = ?,`customer_country` = ?,`customer_ein` = ?,`customer_state` = ?,`customer_name` = ?,`customer_zipcode` = ? , `email_ids` = ? , `phone_number` = ?, `user_id`= ?,`company_id` = ?,`coa` = ?, `payment_spring_id` = ?, `term`=?  WHERE `customer_id`= ? ;";
		public static final String DELETE_QRY = "DELETE FROM  company_customers WHERE `user_id`=? AND company_id = ? AND customer_id=?;";
		public static final String RETRIEVE_BY_ID_QRY = "SELECT `user_id`,`company_id`,`customer_id`,`customer_address`,`customer_city`,`customer_country`,`customer_ein`,`customer_state`,`customer_name`,`customer_zipcode`,`email_ids`,`phone_number`, `coa`, `term` FROM  company_customers WHERE `customer_id`= ? ";
		public static final String RETRIEVE_LIST_BY_ID_QRY = "SELECT `user_id`,`company_id`,`customer_id`,`customer_address`,`customer_city`,`customer_country`,`customer_ein`,`customer_state`,`customer_name`,`customer_zipcode`,`email_ids`,`phone_number`,`coa`, `term` FROM  company_customers WHERE `user_id`= ? AND `company_id` = ?;";
	}
	
	public final class InvoicePlan{
		public static final String INSERT_QRY = "INSERT INTO invoice_plan ( `id`, `name`, `amount`, `frequency`, `ends_after`, `bill_immediately`, `user_id`, `company_id`, `created_by`, `created_at_mills`, `last_updated_by`, `last_updated_at`, `day_month` , `day_day` , `day_week`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );";
		public static final String UPDATE_QRY = "UPDATE invoice_plan SET `name` = ?, `amount` = ?, `frequency` = ?, `ends_after` = ?, `bill_immediately` = ?,  `user_id` = ?, `company_id` = ?, `last_updated_by` = ?, `last_updated_at` = ?, `day_month` = ? , `day_day` = ? , `day_week` = ? WHERE `id` = ?;";
		public static final String DELETE_QRY = "DELETE FROM invoice_plan WHERE  `id` = ?;";
		public static final String GET_QRY = "SELECT `id`, `name`, `amount`, `frequency`, `ends_after`, `bill_immediately`, `user_id`, `company_id`, `created_by`, `created_at_mills`, `last_updated_by`, `last_updated_at`,`day_month` , `day_day` , `day_week` FROM invoice_plan WHERE `id` = ?;";
		public static final String GET_ALL_QRY = "SELECT `id`, `name`, `amount`, `frequency`, `ends_after`, `bill_immediately` FROM invoice_plan WHERE `user_id` = ? and `company_id` = ?;";
	}
}
