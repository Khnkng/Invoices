
package com.qount.invoice.utils;

public class SqlQuerys {

	public final class Invoice {
		public static final String INSERT_QRY = "INSERT INTO invoice ( `id`, `user_id`, `company_id`, `customer_id`, `amount`, `currency`, `description`, `objectives`, `last_updated_by`, `last_updated_at`, `state`, `invoice_date`, `notes`, `discount`, `deposit_amount`, `processing_fees`, `number`, `document_id`, `amount_due`, `due_date`, `sub_totoal`, `amount_by_date`, `created_at`, `amount_paid`, `term`, `created_at_millis`, `recepients_mails`, `plan_id`, `is_recurring`, `payment_options`, `email_state`, `send_to`,`payment_method`,`tax_amount`,`proposal_id`,`remainder_job_id`,`remainder_name` ) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		public static final String UPDATE_QRY = "UPDATE invoice SET `user_id` = ?, `company_id` = ?, `customer_id` = ?, `amount` = ?, `currency` = ?, `description` = ?, `objectives` = ?, `last_updated_by` = ?, `last_updated_at` = ?, `state` = ?, `invoice_date` = ?, `notes` = ?, `discount` = ?, `deposit_amount` = ?, `processing_fees` = ?, `number` = ?, `document_id` = ?, `amount_due` = ?, `due_date` = ?, `sub_totoal` = ?, `amount_by_date` = ?, `amount_paid` = ?, `term` = ?, `recepients_mails` = ?, `plan_id` = ?, `is_recurring` = ?, `payment_options` = ?, `email_state` = ?, `send_to` = ?,`payment_method` = ?, `tax_amount` = ? WHERE `id` = ?;";
		public final static String DELETE_QRY = "DELETE FROM invoice WHERE `id`=? AND   `company_id` =?;";
		public final static String DELETE_LST_QRY = "DELETE FROM invoice WHERE `id` IN (";
		public final static String GET_QRY = "SELECT  i.`remainder_job_id`,i.`remainder_name`,i.`tax_amount`,i.`payment_method`,i.`id`, i.`user_id`, i.`company_id`, i.`customer_id`, i.`amount`, i.`currency`, i.`description`, i.`objectives`, i.`last_updated_by`, i.`last_updated_at`, i.`state`, i.`invoice_date`, i.`notes`, i.`discount`, i.`deposit_amount`, i.`processing_fees`, i.`number`, i.`document_id`, i.`amount_due`, i.`due_date`, i.`sub_totoal`, i.`amount_by_date`, i.`created_at`, i.`amount_paid`, i.`term`, i.`created_at_millis`, i.`recepients_mails`, i.`plan_id`, i.`is_recurring`, i.`payment_options`, i.`email_state`, i.`send_to`, i.`due_date`,il.`id` `il_id`,il.`invoice_id` `il_invoice_id` ,il.`description` `il_description`,il.`objectives` `il_objectives`,il.`amount` `il_amount`,il.`last_updated_by` `il_last_updated_by`,il.`last_updated_at` `il_last_updated_at`,il.`quantity` `il_quantity`,il.`price` `il_price`,il.`notes` `il_notes`,il.`item_id` `il_item_id`,il.`type` `il_type`,il.`tax_id` `il_tax_id`,c.`code`,c.`name`,c.`html_symbol`,c.`java_symbol`, cc.`card_name`,cc.`payment_spring_id`,cc.`customer_name`,cc.customer_address,cc.customer_city,cc.customer_country,cc.customer_ein,cc.customer_state,cc.customer_zipcode,cc.phone_number,cc.coa,cc.term,cc.fax,cc.street_1,cc.street_2, it.`name` AS `il_item_name`,coa.`name` `il_coa_name`, ccd.`id` AS `ccd_id`, ccd.customer_id AS `ccd_customer_id`, ccd.first_name AS `ccd_first_name`, ccd.last_name AS `ccd_last_name`, ccd.mobile AS `ccd_mobile`, ccd.email AS `ccd_email`, ccd.other AS `ccd_other`, dimension.dimensionName dimensionName, dimension.dimensionValue dimensionValue  FROM `invoice` i    LEFT JOIN `currencies` c ON i.currency=c.code  LEFT JOIN `company_customers` cc ON cc.`customer_id` = i.`customer_id` LEFT JOIN `invoice_lines` il ON i.id=il.invoice_id  LEFT JOIN invoice_line_dimensions dimension ON il.id=dimension.invoice_line_id LEFT JOIN `items` it ON il.`item_id` = it.`id` LEFT JOIN `chart_of_accounts` coa ON il.`item_id` = coa.`id` LEFT JOIN `customer_contact_details` ccd ON ccd.`id` = i.`send_to`   WHERE i.id  =?";
		public final static String GET_INVOICES_LIST_QRY = " SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,journals.id AS journal_id, journals.`isActive`, `company_customers`.`customer_name` FROM invoice LEFT JOIN journals ON invoice.`id` = journals.`sourceID` LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  ";
		public final static String GET_INVOICES_LIST_QRY_2 = " SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,journals.id AS journal_id, journals.`isActive`, `company_customers`.`customer_name` FROM invoice LEFT JOIN journals ON invoice.`id` = journals.`sourceID` LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE invoice.`company_id`= ?";
		public final static String GET_INVOICES_LIST_BY_ID_QRY = " SELECT `customer_id`,`number`,`id`,`invoice_date`,`due_date`,`amount`,`currency`,`state`,`amount_by_date`,`amount_due`,`amount_paid` FROM invoice WHERE  id in(";
		public final static String GET_INVOICES_JOBS_LIST_BY_ID_QRY = " SELECT `remainder_job_id` FROM invoice WHERE  id (";
		public final static String QOUNT_QRY = "SELECT ( SELECT  COUNT(id) FROM invoice WHERE company_id=?) AS invoice_count,( SELECT COUNT(id) FROM `proposal` WHERE company_id=?) AS proposal_count,( SELECT COUNT(id) FROM `invoice_payments` WHERE company_id=? ) AS payment_count FROM DUAL";
		public final static String UPDATE_STATE_QRY = "UPDATE invoice SET state=? WHERE id=?;";
		public final static String MARK_AS_PAID_QRY = "UPDATE invoice SET amount_paid=?,amount_due=?,refrence_number=?,state=? WHERE id=?;";
		public final static String GET_INVOICE_BY_NUMBER = "SELECT count(id) as count FROM invoice WHERE number=? AND company_id=?;";
		public final static String GET_INVOICE_BY_NUMBER_AND_ID = "SELECT count(id) as count FROM invoice WHERE number=? AND company_id=? AND id!=?;";
//		public final static String UPDATE_INVOICE_AS_PAID_STATE_QRY = "UPDATE invoice SET refrence_number=?,invoice_date=?,payment_method=?,state='paid' WHERE id=?;";
		public final static String UPDATE_INVOICE_AS_PAID_STATE_QRY = "UPDATE invoice SET state='paid',amount_paid=?,amount_due=? WHERE id=?;";
		public final static String UPDATE_AS_SENT_QRY = "UPDATE invoice SET state='sent' WHERE id IN(";
		public final static String GET_BOX_VALUES = "SELECT AVG(DATEDIFF(`due_date`, NOW())) AS avg_rec_date, AVG(amount_due) AS avg_outstanding, COUNT(*) AS invoice_count, SUM(amount_due) AS total_due, (SELECT SUM(`amount_due`) FROM `invoice` WHERE `state` NOT IN ('paid', 'draft') AND `due_date` < NOW() AND `company_id` = ?) AS total_past_due, (SELECT SUM(`payment_amount`) FROM `invoice_payments` WHERE `payment_date` BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() AND `company_id` = ?) AS received_amount FROM `invoice` WHERE `state` NOT IN ('paid', 'draft') AND `company_id` = ?";
	    public final static String GET_UNMAPPED_PAYMENTS = "SELECT invoice_payments.`bank_account_id`, invoice_payments.`payment_date`, invoice_payments.id, invoice_payments_lines.`amount`,company_customers.`customer_name` FROM `invoice_payments` LEFT JOIN invoice_payments_lines ON invoice_payments.id = invoice_payments_lines.payment_id  LEFT JOIN company_customers ON invoice_payments.`received_from` = company_customers.`customer_id` WHERE  invoice_payments.company_id = ? AND invoice_payments.bank_account_id = ? AND invoice_payments.mapping_id IS NULL ";
	    public final static String RETRIEVE_INVOICES_FOR_DASHBOARD_RECEIVABLES_QRY = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,`company_customers`.`customer_name` FROM invoice LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  invoice.`company_id` = ? AND state IN ('partially_paid','sent') ORDER BY due_date ASC";
	    public final static String RETRIEVE_INVOICES_FOR_DASHBOARD_PAST_DUE_QRY = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,`company_customers`.`customer_name` FROM invoice LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  invoice.`company_id` = ? AND state = 'past_due' ORDER BY due_date ASC";
	    public final static String RETRIEVE_INVOICES_FOR_DASHBOARD_OPENED_QRY = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,`company_customers`.`customer_name` FROM invoice LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  invoice.`company_id` = ? AND state = 'opened' ORDER BY due_date ASC";
	    public final static String RETRIEVE_INVOICES_FOR_DASHBOARD_SENT_QRY = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,`company_customers`.`customer_name` FROM invoice LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  invoice.`company_id` = ? AND state = 'sent' ORDER BY due_date ASC";
	    public final static String RETRIEVE_INVOICES_PAID_IN_LAST_30_DAYS = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,`company_customers`.`customer_name`,invoice_payments.`payment_date`,`invoice_id` FROM `invoice_payments_lines` INNER JOIN `invoice_payments` ON invoice_payments.id = invoice_payments_lines.payment_id LEFT JOIN invoice ON invoice.id = invoice_payments_lines.invoice_id LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id` WHERE invoice_payments.`company_id` = ? AND invoice_payments.`payment_date` BETWEEN (CURDATE() - INTERVAL 1 MONTH ) AND CURDATE() ORDER BY payment_date";
	}

	public final class InvoiceLine {
		public final static String INSERT_QRY = "INSERT INTO `invoice_lines` (`id`,`invoice_id`,`description`,`objectives`,`amount`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id`,`type`,`tax_id`) values (?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public final static String UPDATE_QRY = "update `invoice_lines` SET `invoice_id`=?,`description`=?,`objectives`=?,`amount`=?,`last_updated_by`=?,`last_updated_at`=?,`quantity`=?,`price`=?,`notes`=?, `item_id`=?, `tax_id`=? where `id`=?";
		public final static String GET_LINES_QRY = "SELECT `tax_id`,`id`,`invoice_id`,`description`,`objectives`,`amount`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id` FROM invoice_lines WHERE `invoice_id` = ?;";
		public final static String DELETE_INVOICE_LINE_QRY = "DELETE FROM `invoice_lines` WHERE `id` = ?";
		public final static String DELETE_INVOICE_BY_ID_QRY = "DELETE FROM `invoice_lines` WHERE `invoice_id` = ?";
	}
	
	public final class InvoiceLineDimension {
		public static final String INSERT = "INSERT INTO invoice_line_dimensions (invoice_line_id, dimensionName, dimensionValue, companyID) VALUES(?,?,?,?)  ON DUPLICATE KEY UPDATE dimensionName = ?, dimensionValue = ?";
		public static final String DELETE = "DELETE FROM invoice_line_dimensions WHERE invoice_line_id = ?";
		public static final String DELETE_LIST = "DELETE FROM invoice_line_dimensions WHERE invoice_line_id IN ";
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
		public static final String INSERT_QRY = "INSERT INTO `proposal` ( `id`, `user_id`, `company_id`, `customer_id`, `amount`, `currency`, `description`, `objectives`, `last_updated_by`, `last_updated_at`, `state`, `proposal_date`, `notes`, `discount`, `deposit_amount`, `processing_fees`, `number`, `document_id`, `sub_totoal`, `amount_by_date`, `created_at`, `term`, `created_at_millis`, `recepients_mails`, `plan_id`, `is_recurring`, `email_state`, `send_to`, `estimate_date`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		public static final String UPDATE_QRY = "UPDATE proposal SET `user_id` = ?, `company_id` = ?, `customer_id` = ?, `amount` = ?, `currency` = ?, `description` = ?, `objectives` = ?, `last_updated_by` = ?, `last_updated_at` = ?, `state` = ?, `proposal_date` = ?, `notes` = ?, `discount` = ?, `deposit_amount` = ?, `processing_fees` = ?, `number` = ?, `document_id` = ?, `sub_totoal` = ?, `amount_by_date` = ?, `term` = ?, `recepients_mails` = ?, `plan_id` = ?, `is_recurring` = ?, `email_state` = ?, `send_to` = ?, `estimate_date` = ? WHERE `id` = ?;";
		public final static String DELETE_QRY = "DELETE FROM proposal WHERE `id`=? and `user_id` = ? and `company_id` = ?;";
		public final static String GET_QRY = "SELECT  i.`tax_amount`,i.`id`, i.`user_id`, i.`company_id`, i.`customer_id`, i.`amount`, i.`currency`, i.`description`, i.`objectives`, i.`last_updated_by`, i.`last_updated_at`, i.`state`, i.`proposal_date`, i.`notes`, i.`discount`, i.`deposit_amount`, i.`processing_fees`, i.`number`, i.`document_id`,   i.`sub_totoal`, i.`amount_by_date`, i.`created_at`, i.`term`, i.`created_at_millis`, i.`recepients_mails`, i.`plan_id`, i.`is_recurring`,    i.`email_state`, i.`send_to`,il.`id` `il_id`,il.`proposal_id` `il_proposal_id` ,il.`description` `il_description`,il.`objectives` `il_objectives`,il.`amount` `il_amount`,il.`last_updated_by` `il_last_updated_by`,il.`last_updated_at` `il_last_updated_at`,il.`quantity` `il_quantity`,il.`price` `il_price`,il.`notes` `il_notes`,il.`item_id` `il_item_id`,il.`type` `il_type`,il.`tax_id` `il_tax_id`,c.`code`,c.`name`,c.`html_symbol`,c.`java_symbol`, cc.`card_name`,cc.`payment_spring_id`,cc.`customer_name`,cc.customer_address,cc.customer_city,cc.customer_country,cc.customer_ein,cc.customer_state,cc.customer_zipcode,cc.phone_number,cc.coa,cc.term,cc.fax,cc.street_1,cc.street_2, it.`name` AS `il_item_name`,coa.`name` `il_coa_name`, ccd.`id` AS `ccd_id`, ccd.customer_id AS `ccd_customer_id`, ccd.first_name AS `ccd_first_name`, ccd.last_name AS `ccd_last_name`, ccd.mobile AS `ccd_mobile`, ccd.email AS `ccd_email`, ccd.other AS `ccd_other`,com.`id` AS `com_id`, com.`name` `com_name`, com.`address` `com_address`,com.`city` `com_city`,com.`contact_first_name` `com_contact_first_name`,com.`contact_last_name` `com_contact_last_name`,com.`active` `com_active`,com.`currency` `com_currency`,com.`ein` `com_ein`, com.`email` `com_email`, com.`country` `com_country`,com.`phone_number` `com_phone_number`,com.`state` `com_state`,com.`zipcode` `com_zipcode`  FROM `proposal` i    LEFT JOIN `currencies` c ON i.currency=c.code  LEFT JOIN `company_customers` cc ON cc.`customer_id` = i.`customer_id` LEFT JOIN `proposal_lines` il ON i.id=il.proposal_id  LEFT JOIN `items` it ON il.`item_id` = it.`id` LEFT JOIN `chart_of_accounts` coa ON il.`item_id` = coa.`id` LEFT JOIN `customer_contact_details` ccd ON ccd.`id` = i.`send_to` LEFT JOIN `company` com ON i.`company_id` = com.`id` WHERE i.id  =?;";
		public final static String GET_PROPOSAL_LIST_QRY = "SELECT `estimate_date`, `number`,`id`,`proposal_date`,`amount`,`currency`,`state`,`amount_by_date` FROM proposal WHERE ";
		public final static String DELETE_LST_QRY = "DELETE FROM proposal WHERE `id` IN (";
		public final static String UPDATE_AS_SENT_QRY = "UPDATE proposal SET state=? WHERE id IN(";
		public final static String GET_PROPOSAL_QRY = "SELECT * FROM proposal WHERE id IN(";
		public final static String DENY_PROPSOAL = "UPDATE proposal SET state=? WHERE id IN(";
		public final static String UPDATE_STATE = "UPDATE proposal SET `state` = ?, `invoice_id` = ? WHERE `id` = ?;";
	}

	public final class ProposalLine {
		public final static String INSERT_QRY = "INSERT INTO `proposal_lines` (`id`,`proposal_id`,`description`,`objectives`,`amount`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id`,`type`,`tax_id`) values (?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public final static String UPDATE_QRY = "update `proposal_lines` SET `proposal_id`=?,`description`=?,`objectives`=?,`amount`=?,`last_updated_by`=?,`last_updated_at`=?,`quantity`=?,`price`=?,`notes`=?, `item_id`=?, `tax_id`=? where `id`=?";
		public final static String GET_LINES_QRY = "SELECT `tax_id`,`id`,`proposal_id`,`description`,`objectives`,`amount`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id` FROM proposal_lines WHERE `proposal_id` = ?;";
		public final static String DELETE_PROPOSAL_LINE_QRY = "DELETE FROM `proposal_lines` WHERE `id` = ?";
		public final static String DELETE_PROPOSAL_BY_ID_QRY = "DELETE FROM `proposal_lines` WHERE `proposal_id` = ?";
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

	public final class Company {
		public static final String INSERT_QRY = "INSERT INTO company ( `id`, `name`, `ein`, `type`, `phone_number`, `address`, `city`, `state`, `country`, `zipcode`, `currency`, `email`, `payment_info`, `createdBy`, `modifiedBy`, `createdDate`, `modifiedDate`, `owner`, `active` ) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );";
		public static final String UPDATE_QRY = "UPDATE company SET `name` = ?, `ein` = ?, `type` = ?, `phone_number` = ?, `address` = ?, `city` = ?, `state` = ?, `country` = ?, `zipcode` = ?, `currency` = ?, `email` = ?, `payment_info` = ?, `createdBy` = ?, `modifiedBy` = ?, `createdDate` = ?, `modifiedDate` = ?, `owner` = ?, `active` = ? WHERE `id` = ?;";
		public static final String DELETE_QRY = "DELETE FROM company WHERE  WHERE `id` = ?;";
		public static final String GET_QRY = "SELECT `id`, `name`, `ein`, `type`, `phone_number`, `address`, `city`, `state`, `country`, `zipcode`, `currency`, `email`, `payment_info`, `createdBy`, `modifiedBy`, `createdDate`, `modifiedDate`, `owner`, `active` from `company` WHERE `id` = ?;";
		public static final String GET_ALL_QRY = "SELECT * FROM company;";
		public static final String GET_PAYMMENT_SPRING_COMPANY_DETAILS = "SELECT * FROM `payment_spring_company` WHERE company_id=?";
		public static final String RETRIEVE_BY_ID_QRY = "SELECT c.`id`,c.`name`,c.`ein`,c.`type`,c.`currency`,c.`email`,c.`payment_info`,c.`createdBy`,c.`modifiedBy`,c.`createdDate`,c.`modifiedDate`,adr.`address_id`,adr.`source_id`,adr.`country`,adr.`state`,adr.`city`,adr.`zipcode`,adr.`pincode`,adr.`phone_number`,adr.`line`,adr.`state_code` FROM company c LEFT JOIN  address adr ON c.id=adr.source_id  WHERE id =  ?";

	}

	public final class InvoicePayment {
		public static final String INSERT_QRY = "INSERT INTO invoice_payments ( `id`,`invoice_id`,`transaction_id`,`amount`,`transaction_date`,`status`,`period_start`,`period_end`,`currency_from`,`currency_to`,`conversion`,`conversionDate`,`currency_amount`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		public static final String GET_BY_ID_QRY = "SELECT `id`,`invoice_id`,`transaction_id`,`amount`,`transaction_date`,`status`,`period_start`,`period_end` from `invoice_payments` WHERE `id` = ?;";
		public static final String GET_BY_INVOCIE_ID_QRY = "SELECT `id`,`invoice_id`,`transaction_id`,`amount`,`transaction_date`,`status`,`period_start`,`period_end` from `invoice_payments` WHERE `invoice_id` = ?;";

	}

	public final class Payments {
		public static final String INSERT_QRY = "INSERT INTO invoice_payments ( `id`,`received_from`,`payment_amount`,`currency_code`,`reference_no`,`payment_date`,`memo`,`company_id`,`type`, `payment_notes`, `bank_account_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `received_from` = ?, `payment_amount` = ?, `currency_code` = ?, `reference_no` = ?, `payment_date` = ?, `memo` = ?, `company_id` = ?,`type` = ?,`payment_notes` = ?, `bank_account_id` = ?";
		public static final String RETRIEVE_BY_COMPANYID_QRY = "SELECT invoice_payments.*,journals.`id` AS journal_id,journals.`isActive`,company_customers.`customer_name` FROM invoice_payments LEFT JOIN journals ON `invoice_payments`.id = journals.`sourceID` LEFT JOIN company_customers ON company_customers.`customer_id`= invoice_payments.`received_from` WHERE invoice_payments.`company_id`= ?";
		public static final String RETRIEVE_BY_PAYMENTID_QRY = "SELECT * FROM invoice_payments WHERE `id` = ?;";
	}

	public final class PaymentsLines {
		public static final String INSERT_QRY = "INSERT INTO invoice_payments_lines ( `id`,`invoice_id`,`amount`,`payment_id`) VALUES( ?, ?, ?, ?);";
		public static final String DELETE_QRY = "DELETE FROM invoice_payments_lines WHERE `payment_id` = ?;";
		public static final String GET_LIST_QRY = "SELECT pl.invoice_id, inv.amount, inv.invoice_date, inv.term, inv.state, pl.amount AS payment_amount FROM invoice_payments_lines AS pl, invoice AS inv WHERE pl.invoice_id = inv.id AND pl.payment_id = ?;";
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
		public static final String UPDATE_PAYMENT_SPRING_QRY = "UPDATE  company_customers SET `payment_spring_id` = ? WHERE `customer_id`= ? ;";
		public static final String GET_CUSTOMER_CONTACT_DETAIL_ID_QRY = "SELECT `id`, `customer_id`, `first_name`, `last_name`, `mobile`, `email`, `other` FROM `customer_contact_details` WHERE id = ? ";
	}

	public final class InvoicePlan {
		public static final String INSERT_QRY = "INSERT INTO invoice_plan ( `id`, `name`, `amount`, `frequency`, `ends_after`, `bill_immediately`, `user_id`, `company_id`, `created_by`, `created_at_mills`, `last_updated_by`, `last_updated_at`, `day_month` , `day_day` , `day_week`, `plan_id`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		public static final String UPDATE_QRY = "UPDATE invoice_plan SET `name` = ?, `amount` = ?, `frequency` = ?, `ends_after` = ?, `bill_immediately` = ?,  `user_id` = ?, `company_id` = ?, `last_updated_by` = ?, `last_updated_at` = ?, `day_month` = ? , `day_day` = ? , `day_week` = ?, `plan_id` = ? WHERE `id` = ?;";
		public static final String DELETE_QRY = "DELETE FROM invoice_plan WHERE  `id` = ?;";
		public static final String GET_QRY = "SELECT `id`, `name`, `amount`, `frequency`, `ends_after`, `bill_immediately`, `user_id`, `company_id`, `created_by`, `created_at_mills`, `last_updated_by`, `last_updated_at`,`day_month` , `day_day` , `day_week`, `plan_id` FROM invoice_plan WHERE `id` = ?;";
		public static final String GET_ALL_QRY = "SELECT `id`, `name`, `amount`, `frequency`, `ends_after`, `bill_immediately`, `plan_id` FROM invoice_plan WHERE `user_id` = ? and `company_id` = ?;";
	}
}
