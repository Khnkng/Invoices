
package com.qount.invoice.utils;

public class SqlQuerys {

	public final class Invoice {
		public static final String INSERT_QRY = "INSERT INTO invoice (`po_number`, `project_name`, `billing_cycle`, `billing_from`, `billing_to`, `remit_payments_to`, `late_fee_id`, `late_fee_name`, `attachments_metadata`, `id`, `user_id`, `company_id`, `customer_id`, `amount`, `currency`, `description`, `objectives`, `last_updated_by`, `last_updated_at`, `state`, `invoice_date`, `notes`, `discount`, `deposit_amount`, `processing_fees`, `number`, `document_id`, `amount_due`, `due_date`, `sub_totoal`, `amount_by_date`, `created_at`, `amount_paid`, `term`, `created_at_millis`, `recepients_mails`, `plan_id`, `is_recurring`, `payment_options`, `email_state`, `send_to`,`payment_method`,`tax_amount`,`proposal_id`,`remainder_job_id`,`remainder_name`, `recurring_frequency`,`recurring_end_date`,`job_date`,`is_discount_applied`,`discount_id`) VALUES(?,?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		public static final String UPDATE_QRY = "UPDATE invoice SET `po_number` = ?, `project_name` = ?, `billing_cycle` = ?,`billing_from` = ?, `billing_to` = ?, `remit_payments_to` = ?,`late_fee_id`=?,`late_fee_name`=?,`attachments_metadata`=?,`remainder_job_id` = ?, `remainder_name` = ?, `user_id` = ?, `company_id` = ?, `customer_id` = ?, `amount` = ?, `currency` = ?, `description` = ?, `objectives` = ?, `last_updated_by` = ?, `last_updated_at` = ?, `state` = ?, `invoice_date` = ?, `notes` = ?, `discount` = ?, `deposit_amount` = ?, `processing_fees` = ?, `number` = ?, `document_id` = ?, `amount_due` = ?, `due_date` = ?, `sub_totoal` = ?, `amount_by_date` = ?, `amount_paid` = ?, `term` = ?, `recepients_mails` = ?, `plan_id` = ?, `is_recurring` = ?, `payment_options` = ?, `send_to` = ?,`payment_method` = ?, `tax_amount` = ?,`recurring_frequency` = ?,`recurring_end_date` = ?, `job_date` = ?, `is_discount_applied` = ?,`discount_id` = ? WHERE `id` = ?;";
		public static final String UPDATE_REMAINDER_JOB_ID_QRY = "UPDATE invoice SET `remainder_job_id` = ? WHERE `id` = ?;";
		public static final String UPDATE_EMAIL_STATE_QRY = "UPDATE invoice SET `email_state` = ? WHERE `id` = ?;";
		public final static String DELETE_QRY = "DELETE FROM invoice WHERE `id`=? AND   `company_id` =?;";
		public final static String DELETE_LST_QRY = "DELETE FROM invoice WHERE `id` IN (";
		public final static String GET_QRY = "SELECT  CASE WHEN i.`state` = 'paid' THEN ip.`payment_date` ELSE NULL END payment_date, i.`recurring_frequency` , i.`recurring_end_date`,i.`po_number`, i.`project_name`, i.`billing_cycle`,i.`billing_from`, i.`billing_to`, i.`remit_payments_to`, i.`late_fee_journal_id`, i.`late_fee_name`,i.`journal_job_id`, i.`late_fee_applied`, i.`late_fee_id`, i.`late_fee_amount`, i.`attachments_metadata`, il.`rank` `il_rank`,i.`remainder_job_id`,i.`remainder_name`,i.`tax_amount`,i.`payment_method`,i.`id`, i.`user_id`, i.`company_id`, i.`customer_id`, i.`amount`, i.`currency`, i.`description`, i.`objectives`, i.`last_updated_by`, i.`last_updated_at`, i.`state`, i.`invoice_date`, i.`notes`, i.`discount`, i.`deposit_amount`, i.`processing_fees`, i.`number`, i.`document_id`, i.`amount_due`, i.`due_date`, i.`job_date`,i.`is_discount_applied`,i.`discount_id`,i.`sub_totoal`, i.`amount_by_date`, i.`created_at`, i.`amount_paid`, i.`term`, i.`created_at_millis`, i.`recepients_mails`, i.`plan_id`, i.`is_recurring`, i.`payment_options`, i.`email_state`, i.`send_to`, i.`due_date`, i.post_id, il.`id` `il_id`,il.`invoice_id` `il_invoice_id` ,il.`description` `il_description`,il.`objectives` `il_objectives`,il.`amount` `il_amount`,il.`last_updated_by` `il_last_updated_by`,il.`last_updated_at` `il_last_updated_at`,il.`quantity` `il_quantity`,il.`price` `il_price`,il.`notes` `il_notes`,il.`item_id` `il_item_id`,il.`type` `il_type`,il.`tax_id` `il_tax_id`,c.`code`,c.`name`,c.`html_symbol`,c.`java_symbol`, cc.`card_name`,cc.`payment_spring_id`,cc.`customer_name`,cc.customer_address,cc.customer_city,cc.customer_country,cc.customer_ein,cc.customer_state,cc.customer_zipcode,cc.phone_number,cc.coa,cc.term,cc.fax,cc.street_1,cc.street_2, it.`name` AS `il_item_name`,coa.`name` `il_coa_name`, ccd.`id` AS `ccd_id`, ccd.customer_id AS `ccd_customer_id`, ccd.first_name AS `ccd_first_name`, ccd.last_name AS `ccd_last_name`, ccd.mobile AS `ccd_mobile`, ccd.email AS `ccd_email`, ccd.other AS `ccd_other`, dimension.dimensionName dimensionName, dimension.dimensionValue dimensionValue  FROM `invoice` i    LEFT JOIN `currencies` c ON i.currency=c.code  LEFT JOIN `company_customers` cc ON cc.`customer_id` = i.`customer_id` LEFT JOIN `invoice_lines` il ON i.id=il.invoice_id  LEFT JOIN invoice_line_dimensions dimension ON il.id=dimension.invoice_line_id LEFT JOIN `items` it ON il.`item_id` = it.`id` LEFT JOIN `chart_of_accounts` coa ON il.`item_id` = coa.`id` LEFT JOIN `customer_contact_details` ccd ON ccd.`id` = i.`send_to` LEFT JOIN invoice_payments_lines ipl ON ipl.invoice_id = i.id LEFT JOIN invoice_payments ip ON ip.id = ipl.payment_id WHERE i.id  =? ORDER BY il.`rank`";
		public final static String GET_INVOICES_LIST_QRY = "SELECT invoice.`late_fee_id`, invoice.`late_fee_amount`, invoice.`late_fee_applied`, invoice.`email_state`,invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,invoice.`job_date`,invoice.`is_discount_applied`,invoice.`discount_id`,journals.id AS journal_id, journals.`isActive`,journals.sourceType, `company_customers`.`customer_name` FROM invoice LEFT JOIN journals ON invoice.`id` = journals.`sourceID` LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  ";
		public final static String GET_INVOICES_LIST_QRY_2 = "SELECT invoice.`late_fee_id`, invoice.`late_fee_amount`, invoice.`late_fee_applied`,invoice.`email_state`,invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,invoice.`job_date`,invoice.`is_discount_applied`,invoice.`discount_id`,journals.id AS journal_id, journals.`isActive`, journals.sourceType, `company_customers`.`customer_name` FROM invoice LEFT JOIN journals ON invoice.`id` = journals.`sourceID` LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE invoice.`company_id`= ?";
		public final static String GET_INVOICES_LIST_BY_ID_QRY = " SELECT `customer_id`,`number`,`id`,`invoice_date`,`due_date`,`amount`,`currency`,`state`,`amount_by_date`,`amount_due`,`amount_paid` FROM invoice WHERE  id in(";
		public final static String GET_INVOICES_JOBS_LIST_BY_ID_QRY = " SELECT `remainder_job_id` FROM invoice WHERE  id in (";
		public final static String GET_INVOICES_PAYMENTS_MAP_BY_INVOICE_ID_QRY = " SELECT DISTINCT(ip.id),ipl.`invoice_id` FROM `invoice_payments` ip INNER JOIN `invoice_payments_lines` ipl ON ipl.`payment_id` = ip.`id` AND ipl.`invoice_id` IN (";
		public final static String QOUNT_QRY = "SELECT ( SELECT  COUNT(id) FROM invoice WHERE company_id=?) AS invoice_count,( SELECT COUNT(id) FROM `proposal` WHERE company_id=?) AS proposal_count,( SELECT COUNT(id) FROM `invoice_payments` WHERE company_id=? ) AS payment_count FROM DUAL";
		public final static String UPDATE_STATE_QRY = "UPDATE invoice SET state=? WHERE id=?;";
		public final static String MARK_AS_PAID_QRY = "UPDATE invoice SET amount_paid=?,amount_due=?,refrence_number=?,state=? WHERE id=?;";
		public final static String GET_INVOICE_BY_NUMBER = "SELECT count(id) as count FROM invoice WHERE number=? AND company_id=?;";
		public final static String GET_INVOICE_BY_NUMBER_AND_ID = "SELECT count(id) as count FROM invoice WHERE number=? AND company_id=? AND id!=?;";
		public final static String UNAPPLIED_COUNT_QRY ="SELECT COUNT(*) unapplied_count FROM (SELECT id, payment_amount FROM invoice_payments WHERE company_id= ?)payments, (SELECT payment_id, SUM(amount) applied_amount FROM invoice_payments ip, invoice_payments_lines ipl WHERE ip.company_id= ? AND ip.id=ipl.payment_id GROUP BY payment_id)payment_lines WHERE payments.id=payment_lines.payment_id AND payment_amount <> applied_amount";
		public final static String GET_INVOICE_BY_FILTERS = "SELECT invoice.`late_fee_id`, invoice.`late_fee_amount`, invoice.`late_fee_applied`, invoice.`email_state`,invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,invoice.`job_date`,invoice.`is_discount_applied`,invoice.`discount_id`, company.name AS company_name,`company_customers`.`customer_name`  FROM invoice JOIN company ON invoice.`company_id` = company.`id` JOIN `company_customers` ON invoice.`customer_id` = company_customers.`customer_id` WHERE ";
		
		// public final static String UPDATE_INVOICE_AS_PAID_STATE_QRY = "UPDATE invoice
		// SET refrence_number=?,invoice_date=?,payment_method=?,state='paid' WHERE
		// id=?;";
		public final static String UPDATE_INVOICE_AS_PAID_STATE_QRY = "UPDATE invoice SET state='paid',amount_paid=?,amount_due=? WHERE id=?;";
		public final static String UPDATE_AS_SENT_QRY = "UPDATE invoice SET state='sent' WHERE id IN(";
		public final static String GET_BOX_VALUES = "SELECT AVG(DATEDIFF(`due_date`, NOW())) AS avg_rec_date, AVG(amount_due) AS avg_outstanding, COUNT(*) AS invoice_count, SUM(amount_due) AS total_due, (SELECT SUM(`amount_due`) FROM `invoice` WHERE `state` NOT IN ('paid', 'draft') AND `due_date` < NOW() AND `company_id` = ?) AS total_past_due, (SELECT SUM(`payment_amount`) FROM `invoice_payments` WHERE `payment_date` BETWEEN CURDATE() - INTERVAL 30 DAY AND CURDATE() AND `company_id` = ?) AS received_amount, (SELECT COUNT(CASE WHEN email_state = 'open' OR email_state = 'click' THEN 1 ELSE NULL END) AS open_invoices FROM `invoice` WHERE `company_id` = ? AND state='sent') AS open_invoices, (SELECT COUNT(CASE WHEN email_state = 'delivered' THEN 1 ELSE NULL END) AS sent_invoices FROM `invoice` WHERE `company_id` = ? AND state='sent') AS sent_invoices FROM `invoice` WHERE `state` NOT IN ('paid', 'draft') AND `company_id` = ?";
		public final static String GET_UNMAPPED_PAYMENTS = "SELECT invoice_payments.`bank_account_id`, invoice_payments.`payment_date`, invoice_payments.id, invoice_payments.`payment_amount`,company_customers.`customer_name`, invoice_payments.mapping_id,i.invoice_date, i.number FROM `invoice_payments` LEFT JOIN `invoice_payments_lines` ilp ON ilp.`payment_id` = invoice_payments.id LEFT JOIN `invoice` i ON i.id = ilp.`invoice_id` LEFT JOIN company_customers ON invoice_payments.`received_from` = company_customers.`customer_id` LEFT JOIN transaction_mappings ON invoice_payments.mapping_id = transaction_mappings.`group_id` LEFT JOIN deposits ON transaction_mappings.group_id = deposits.`mapping_id` WHERE  invoice_payments.company_id = ? AND invoice_payments.bank_account_id = ? AND (deposits.id = ? OR invoice_payments.mapping_id IS NULL) ORDER BY invoice_payments.payment_date DESC  ";
		public final static String GET_UNMAPPED_PAYMENTS_WITH_ENTITYID = "SELECT invoice_payments.`bank_account_id`, invoice_payments.`payment_date`, invoice_payments.id, invoice_payments.`payment_amount`,company_customers.`customer_name`, invoice_payments.mapping_id ,i.invoice_date, i.number FROM `invoice_payments` LEFT JOIN `invoice_payments_lines` ilp ON ilp.`payment_id` = invoice_payments.id LEFT JOIN `invoice` i ON i.id = ilp.`invoice_id` LEFT JOIN company_customers ON invoice_payments.`received_from` = company_customers.`customer_id` LEFT JOIN transaction_mappings ON invoice_payments.mapping_id = transaction_mappings.`group_id` LEFT JOIN deposits ON transaction_mappings.group_id = deposits.`mapping_id` WHERE  invoice_payments.company_id = ? AND invoice_payments.bank_account_id = ? AND company_customers.`customer_id` = ? AND (deposits.id = ? OR invoice_payments.mapping_id IS NULL) ORDER BY invoice_payments.payment_date DESC ";
		public final static String RETRIEVE_INVOICES_FOR_DASHBOARD_RECEIVABLES_QRY = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,`company_customers`.`customer_name` FROM invoice LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  invoice.`company_id` = ? AND state IN ('partially_paid','sent') ORDER BY due_date ASC";
		public final static String RETRIEVE_INVOICES_FOR_DASHBOARD_PAST_DUE_QRY = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,`company_customers`.`customer_name` FROM invoice LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  invoice.`company_id` = ? AND invoice.`state` IN ('sent','partially_paid') AND invoice.`due_date` < NOW() ORDER BY due_date ASC";
		public final static String RETRIEVE_INVOICES_FOR_DASHBOARD_OPENED_QRY = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,invoice.`email_state`,`company_customers`.`customer_name` FROM invoice LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  invoice.`company_id` = ? AND email_state IN ('open','click') ORDER BY due_date ASC";
		public final static String RETRIEVE_INVOICES_FOR_DASHBOARD_SENT_QRY = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,`company_customers`.`customer_name` FROM invoice LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id`  WHERE  invoice.`company_id` = ? AND state = 'sent' ORDER BY due_date ASC";
		public final static String RETRIEVE_INVOICES_PAID_IN_LAST_30_DAYS = "SELECT invoice.`customer_id`,invoice.`number`,invoice.`id`,invoice.`user_id`,invoice.`company_id`,invoice.`invoice_date`,invoice.`due_date`,invoice.`amount`,invoice.`currency`,invoice.`state`,invoice.`amount_by_date`,invoice.`amount_due`,invoice.`amount_paid`,`company_customers`.`customer_name`,invoice_payments.`payment_date`,`invoice_id` FROM `invoice_payments_lines` INNER JOIN `invoice_payments` ON invoice_payments.id = invoice_payments_lines.payment_id LEFT JOIN invoice ON invoice.id = invoice_payments_lines.invoice_id LEFT JOIN `company_customers` ON `invoice`.`customer_id` = `company_customers`.`customer_id` WHERE invoice_payments.`company_id` = ? AND invoice_payments.`payment_date` BETWEEN (CURDATE() - INTERVAL 1 MONTH ) AND CURDATE() ORDER BY payment_date";
		public final static String GET_UNMAPPED_INVOICES_LIST = "SELECT DISTINCT (i.id), i.`number`, i.`due_date`, i.`amount`, i.`customer_id` AS customerID, c.`customer_name`, i.`amount_due` , i.`state`, i.`currency`, ip.`mapping_id`, p.`id` AS event_id  FROM invoice i LEFT JOIN `invoice_payments_lines` ilp ON ilp.`invoice_id` = i.`id`  AND ilp.`amount` > 0 LEFT JOIN `invoice_payments` ip ON ip.`id` = ilp.`payment_id` LEFT JOIN company_customers c ON c.`customer_id` = i.`customer_id` LEFT JOIN `pay_event` p ON p.`invoice_id` = i.`id` WHERE i.`company_id`=  ?  AND  i.`customer_id` = ? AND ip.`mapping_id` IS NULL AND (i.state != 'paid' AND i.state != 'draft') ";
		public final static String GET_MAPPED_UNMAPPED_INVOICES_LIST = "SELECT DISTINCT (i.id), i.`number`, i.`due_date`, i.`amount`, i.`customer_id` AS customerID, c.`customer_name`, i.`amount_due` , i.`state`, i.`currency`, ip.`mapping_id`,p. bill_id FROM invoice i LEFT JOIN `invoice_payments_lines` ilp ON ilp.`invoice_id` = i.`id`  AND ilp.`amount` > 0  LEFT JOIN `invoice_payments` ip ON ip.`id` = ilp.`payment_id` LEFT JOIN company_customers c ON c.`customer_id` = i.`customer_id` LEFT JOIN `pay_event` p ON p.`invoice_id` = i.`id` WHERE i.`company_id`= ? AND  i.`customer_id` = ? AND ( p.bill_id = ? OR ip.`mapping_id` IS NULL ) AND (i.state != 'paid' AND i.state != 'draft') ";

	}

	public final class InvoiceLine {
		public final static String INSERT_QRY = "INSERT INTO `invoice_lines` (`rank`,`id`,`invoice_id`,`description`,`objectives`,`amount`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id`,`type`,`tax_id`) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
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
		public static final String INSERT_QRY = "INSERT INTO `invoice_preferences` (`display_commission`, `id`,`company_id`,`template_type`,`company_logo`,`display_logo`,`accent_color`,`default_payment_terms`,`default_title`,`default_sub_heading`,`default_footer`,`standard_memo`,`items`,`units`,`price`,`amount`,`hide_item_name`,`hide_item_description`,`hide_units`,`hide_price`,`hide_amount`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public static final String UPDATE_QRY = "UPDATE `invoice_preferences` SET `display_commission`=?,`template_type` = ?, `company_logo` = ?, `display_logo` = ?, `accent_color` = ?, `default_payment_terms` = ?, `default_title` = ?, `default_sub_heading` = ?, `default_footer` = ?, `standard_memo` = ?, `items` = ?, `units` = ?, `price` = ?, `amount` = ?, `hide_item_name` = ?, `hide_item_description` = ?, `hide_units` = ?, `hide_price` = ?, `hide_amount` = ?, `company_id`= ?  WHERE `id` = ?";
		public final static String DELETE_QRY = "DELETE FROM invoice_preferences WHERE `id`=?;";
		public final static String GET_QRY = " SELECT `display_commission`,`id`,`company_id`,`template_type`,`company_logo`,`display_logo`,`accent_color`,`default_payment_terms`,`default_title`,`default_sub_heading`,`default_footer`,`standard_memo`,`items`,`units`,`price`,`amount`,`hide_item_name`,`hide_item_description`,`hide_units`,`hide_price`,`hide_amount` FROM invoice_preferences WHERE `company_id` = ?  limit 1";
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
		public static final String INSERT_QRY = "INSERT INTO invoice_payments (`payment_status`, `id`,`received_from`,`payment_amount`,`currency_code`,`reference_no`,`payment_date`,`memo`,`company_id`,`type`, `payment_notes`, `bank_account_id`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `received_from` = ?, `payment_amount` = ?, `currency_code` = ?, `reference_no` = ?, `payment_date` = ?, `memo` = ?, `company_id` = ?,`type` = ?,`payment_notes` = ?, `bank_account_id` = ?, `payment_status` = ?";
		public static final String RETRIEVE_BY_COMPANYID_QRY = "SELECT invoice_payments.*,journals.`id` AS journal_id,journals.`isActive`,company_customers.`customer_name`, deposits.`id` AS deposit_id FROM invoice_payments LEFT JOIN journals ON `invoice_payments`.id = journals.`sourceID` LEFT JOIN company_customers ON company_customers.`customer_id`= invoice_payments.`received_from` LEFT JOIN transaction_mappings ON transaction_mappings.`id` = invoice_payments.`mapping_id` LEFT JOIN deposits ON deposits.`mapping_id` = transaction_mappings.`id`  WHERE invoice_payments.`company_id`= ?  ORDER BY payment_date DESC  ";
		public static final String RETRIEVE_BY_INVOICE_QRY = "SELECT invoice_payments.*,journals.`id` AS journal_id,journals.`isActive`,company_customers.`customer_name`, deposits.`id` AS deposit_id FROM invoice_payments LEFT JOIN journals ON `invoice_payments`.id = journals.`sourceID` LEFT JOIN company_customers ON company_customers.`customer_id`= invoice_payments.`received_from` LEFT JOIN transaction_mappings ON transaction_mappings.`id` = invoice_payments.`mapping_id` LEFT JOIN deposits ON deposits.`mapping_id` = transaction_mappings.`id` LEFT JOIN invoice_payments_lines  ipl ON ipl.`payment_id` = invoice_payments.`id` WHERE ipl.`invoice_id`= ?  AND ipl.`amount`>0 ORDER BY payment_date DESC  ";
		public static final String RETRIEVE_BY_PAYMENTID_QRY = "SELECT * FROM invoice_payments WHERE `id` = ?;";
		public static final String RETRIEVE_DEPOSIT_BY_MAPPING_QRY = "SELECT id FROM `deposits` WHERE mapping_id = ?;";
	}

	public final class PaymentsLines {
		public static final String INSERT_QRY = "INSERT INTO invoice_payments_lines ( `id`,`invoice_id`,`amount`,`payment_id`, `discount`) VALUES(?, ?, ?, ?, ?);";
		public static final String DELETE_QRY = "DELETE FROM invoice_payments_lines WHERE `payment_id` = ?;";
		public static final String GET_LIST_QRY = "SELECT pl.invoice_id, inv.amount, inv.invoice_date, inv.term, inv.state, pl.amount AS payment_amount FROM invoice_payments_lines AS pl, invoice AS inv WHERE pl.invoice_id = inv.id AND pl.payment_id = ?;";
		public static final String GET_PAID_AMOUNT_LIST_QRY = "SELECT payment_id,SUM(amount) applied_amount FROM invoice_payments_lines WHERE payment_id IN( ";
		public static final String GET_PAID_AMOUNT_LIST_QRY_2 = ") AND amount >0 GROUP BY payment_id";
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

	public final class Invoice_history {

		public static final String INSERT_QRY = "INSERT INTO invoice_history (`action_at_mills`,`currency`, `sub_totoal`, `amount_by_date`, `amount_paid`, `tax_amount`, `amount_due`, `amount`, `webhook_event_id`,`description`, `id`, `invoice_id`, `user_id`, `action`, `action_at`, `company_id`, `email_to`, `email_subject`, `email_from`, `created_by`, `created_at`, `last_updated_by`, `last_updated_at` ) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		public static final String UPDATE_QRY = "UPDATE invoice_history SET `amount` = ?, `webhook_event_id`=?, `description` = ?, `invoice_id` = ?, `user_id` = ?, `action` = ?, `action_at` = ?, `company_id` = ?, `email_to` = ?, `email_subject` = ?, `email_from` = ?, `last_updated_by` = ?, `last_updated_at` = ? WHERE `id` = ?;";
		public static final String DELETE_QRY = "DELETE FROM invoice_history WHERE `id` = ?;";
		public static final String DELETE_BY_IDS_QRY = "DELETE FROM invoice_history WHERE `id` IN (";
		public static final String GET_QRY = "SELECT `action_at_mills`, `currency`, `sub_totoal`, `amount_by_date`, `amount_paid`, `tax_amount`, `amount_due`, `amount`,`webhook_event_id`,`description`, `id`, `invoice_id`, `user_id`, `action`, `action_at`, `company_id`, `email_to`, `email_subject`, `email_from`, `created_by`, `created_at`, `last_updated_by`, `last_updated_at` FROM `invoice_history` WHERE `id` = ?;";
		public static final String GET_BY_WEBHOOK_ID_QRY = "SELECT `id` FROM `invoice_history` WHERE `webhook_event_id` = ? limit 1;";
		public static final String GET_BY_INVOICE_AND_ACTION_ID_QRY = "SELECT `id` FROM `invoice_history` WHERE `invoice_id` = ? AND `action` = ? limit 1;";
		public static final String GET_ALL_QRY = "SELECT `amount`, `id`, `invoice_id`, `user_id`, `action`, `action_at`, `company_id`, `email_to`, `email_subject`, `email_from`, `created_by`, `created_at`, `last_updated_by`, `last_updated_at` FROM invoice_history where created_by = ? and company_id = ?;";
		public static final String GET_ALL_BY_INVOICE_ID_QRY = "SELECT `action_at_mills`,`amount`,`webhook_event_id`,`description`, `id`, `invoice_id`, `user_id`, `action`, `action_at`, `company_id`, `email_to`, `email_subject`, `email_from`, `created_by`, `created_at`, `last_updated_by`, `last_updated_at` FROM `invoice_history` WHERE `invoice_id` = ? ORDER BY `action_at_mills` ASC;";
		public static final String GET_ALL_BY_INVOICE_ID_WTIH_LIMITED_ACTION_QRY = "SELECT CASE WHEN `action`='open' THEN 'opened' ELSE `action` END AS `action`, `action_at_mills`,`currency`, `sub_totoal`, `amount_by_date`, `amount_paid`, `tax_amount`, `amount_due`, `amount`,`webhook_event_id`,`description`, `id`, `invoice_id`, `user_id`, `action_at`, `company_id`, `email_to`, `email_subject`, `email_from`, `created_by`, `created_at`, `last_updated_by`, `last_updated_at`  FROM `invoice_history` WHERE `invoice_id` = ? AND (`action` IN (";
		public static final String LIMITED_ACTIONS = "'Mark as sent','draft','sent','partially_paid','paid','delivered','dropped','deferred','bounce','open','click'";
	}

	public final class InvoiceCommission {
		public static final String INSERT_QRY = "INSERT INTO invoice_commission (`event_date`, `id`, `invoice_id`, `invoice_amount`,`event_type`, `event_at`,`bill_id`, `company_id`, `invoice_number`, `currency`, `billCreated`, `user_id`, `created_at`, `last_updated_at`, `last_updated_by`, `vendor_id`, `amount`, `item_name`, `item_id`, `amount_type`) VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		public static final String DELETE_BY_INVOICE_ID_QRY = "delete from invoice_commission WHERE `id` = ?;";
		public static final String GET_BY_INVOICE_ID_QRY = "SELECT `event_date`, `id`, `invoice_id`, `invoice_amount`,`event_type`, `event_at`,`bill_id`, `company_id`, `invoice_number`, `currency`, `billCreated`, `user_id`, `created_at`, `last_updated_at`, `last_updated_by`, `vendor_id`, `amount`, `item_name`, `item_id`, `amount_type` FROM invoice_commission  WHERE invoice_id=?";
		public static final String UPDATE_BILL_STATE_QRY = "update invoice_commission set `billCreated` = ?, `last_updated_at` = ?, `last_updated_by` = ?  where id = ?";
	}

	public final class PayEvent {
		public final static String UPDATE = "UPDATE pay_event SET `is_eligible` = TRUE WHERE `invoice_id` = ?;";
	}

	public final class InvoiceDiscounts {

		public static final String INSERT_QRY = "INSERT INTO invoice_discounts ( `id`, `name`, `description`, `type`, `company_id`, `created_by`, `created_at`, `last_updated_by`, `last_updated_at`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ? );";
		public static final String UPDATE_QRY = "UPDATE invoice_discounts SET `name` = ?, `description` = ?, `type` = ?, `company_id` = ?, `last_updated_by` = ?, `last_updated_at` = ? WHERE `id` = ?;";
		public static final String DELETE_QRY = "DELETE FROM invoice_discounts WHERE `id` = ?;";
		public static final String DELETE_BY_COMPANY_ID_QRY = "DELETE FROM invoice_discounts WHERE `company_id` = ?";
		public static final String GET_QRY = "SELECT ids.`id`,ids.`name`,ids.`description`,ids.`type`,ids.`company_id`,ids.`created_at`,ids.`created_by`,ids.`last_updated_at`,ids.`last_updated_by`,dsr.`id` AS range_id,dsr.`discount_id`,dsr.`fromDay`,dsr.`toDay`,dsr.`value` FROM invoice_discounts ids LEFT JOIN discounts_ranges dsr ON ids.`id` = dsr.`discount_id` WHERE ids.id = ?;";
		public static final String GET_ALL_QRY = "SELECT `id`, `name`, `description`, `type`, `company_id`, `created_by`, `created_at`, `last_updated_by`, `last_updated_at` FROM invoice_discounts where created_by = ? and company_id = ?;";
	}
	
	public final class DiscountsRanges{

		public static final String INSERT_QRY = "INSERT INTO discounts_ranges ( `id`, `discount_id`,`fromDay`, `toDay`, `value` ) VALUES( ?,?, ?, ?, ? );";
		public static final String UPDATE_QRY = "UPDATE discounts_ranges SET `fromDay` = ?, `toDay` = ?, `value` = ? ,`discount_id` = ? WHERE `id` = ?;";
		public static final String DELETE_QRY = "DELETE FROM discounts_ranges WHERE `discount_id` = ?;";
		public static final String DELETE_BY_IDS_QRY = "DELETE FROM discounts_ranges WHERE `id` IN (";
		public static final String GET_QRY = "SELECT `id`,`discount_id`, `fromDay`, `toDay`, `value` FROM `discounts_ranges` WHERE `id` = ? AND `discount_id` = ?;";
		public static final String GET_ALL_QRY = "SELECT `id`,`discount_id`, `fromDay`, `toDay`, `value` FROM discounts_ranges where `discount_id` = ?";
	}

}
