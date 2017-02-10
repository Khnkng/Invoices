package com.qount.invoice.utils;

public class SqlQuerys {
	
	public final class Invoice {
		public static final String INSERT_QRY = "insert into `invoice` (`id`,`user_id`,`company_id`,`company_name`,`amount`,`currency`,`description`,`objectives`,`last_updated_by`,`last_updated_at`,`first_name`,`last_name`,`state`,`invoice_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json`,`is_recurring`,`recurring_frequency`,`recurring_frequency_value`,`recurring_start_date`,`recurring_end_date`,`is_mails_automated`,`is_cc_current_user`,`payment_spring_customer_id`,`po_number`,`document_id`,`amount_due`,`payment_date`,`sub_totoal`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public static final String UPDATE_QRY = "UPDATE `invoice` SET `user_id` = ?,`company_id` = ?,`company_name` = ?,`amount` = ?,`currency` = ?,`description` = ?,`objectives` = ?,`last_updated_by` = ?,`last_updated_at` = ?,`first_name` = ?,`last_name` = ?,`state` = ?,`invoice_date` = ?,`acceptance_date` = ?,`acceptance_final_date` = ?,`notes` = ?,`discount` = ?,`deposit_amount` = ?,`processing_fees` = ?,`remainder_json` = ?,`remainder_mail_json`= ?,`is_recurring` = ?,`recurring_frequency` = ?,`recurring_frequency_value` = ?,`recurring_start_date` = ?,`recurring_end_date` = ?,`is_mails_automated` = ?,`is_cc_current_user` = ?,`payment_spring_customer_id` = ?,`po_number` = ?,`document_id` = ?,`amount_due` = ?, `payment_date`=?, `sub_totoal`=? WHERE `id` = ?";
		public final static String DELETE_QRY = "DELETE FROM invoice WHERE `id`=?;";
		public final static String GET_QRY = "SELECT i.`id`,i.`user_id`,i.`company_id`,i.`company_name`,i.`amount`,i.`currency`,i.`description`,i.`objectives`,i.`last_updated_by`,i.`last_updated_at`,i.`first_name`,i.`last_name`,i.`state`, i.`invoice_date`,i.`acceptance_date`,i.`acceptance_final_date`,i.`notes`,i.`discount`,i.`deposit_amount`,i.`processing_fees`,i.`remainder_json`, i.`remainder_mail_json`,i.`is_recurring`,i.`recurring_frequency`,i.`recurring_frequency_value`,i.`recurring_start_date`,i.`recurring_end_date`,i.`is_mails_automated`,i.`is_cc_current_user`,i.`payment_spring_customer_id`,i.`po_number`,i.`document_id`,i.`amount_due`,i.`payment_date`,i.`sub_totoal`, il.`id` AS `ilid`,il.`item_id` AS `il_item_id`,il.`item_name` AS `il_item_name`,il.`invoice_id`,il.`description` `il_description`,il.`objectives` `il_objectives`,il.`amount` `il_amount`,il.`currency` `il_currency`,il.`last_updated_by` `il_last_updated_by`,il.`last_updated_at` `il_last_updated_at`,il.`quantity` `il_quantity`,il.`price` `il_price`,il.`notes` `il_notes`,il.`coa_id` `il_coa_id`,il.`coa_name` `il_coa_name`, ilt.`invoice_line_id` `ilt_invoice_line_id`,ilt.`tax_id` `ilt_tax_id`,ilt.`tax_rate` `ilt_tax_rate` FROM `invoice` i LEFT JOIN `invoice_lines` il ON i.id=il.invoice_id LEFT JOIN `invoice_line_taxes` ilt ON il.id =ilt.invoice_line_id WHERE i.id = ?;";
		public final static String GET_INVOICES_LIST_QRY = "SELECT `id`,`user_id`,`company_id`,`company_name`,`amount`,`currency`,`description`,`objectives`,`last_updated_by`,`last_updated_at`,`first_name`,`last_name`,`state`,`invoice_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json`,`is_recurring`,`recurring_frequency`,`recurring_frequency_value`,`recurring_start_date`,`recurring_end_date`,`is_mails_automated`,`is_cc_current_user`,`payment_spring_customer_id`,`number`,`po_number`,`document_id`,`amount_due`,`payment_date`,`sub_totoal` FROM invoice WHERE `user_id`=?;";
	}
	
	public final class InvoiceLine {
		public final static String INSERT_QRY = "INSERT INTO `invoice_lines` (`id`,`invoice_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id`,`item_name`,`coa_id`,`coa_name`) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public final static String UPDATE_QRY = "update `invoice_lines` SET `invoice_id`=?,`description`=?,`objectives`=?,`amount`=?,`currency`=?,`last_updated_by`=?,`last_updated_at`=?,`quantity`=?,`price`=?,`notes`=?, `item_id`=?, `item_name`=?, `coa_id` = ?,`coa_name` = ? where `id`=?";
		public final static String GET_LINES_QRY = "SELECT `id`,`invoice_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`,`item_id`,`item_name`,`coa_id`,`coa_name` FROM invoice_lines WHERE `invoice_id` = ?;";
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
		public final static String GET_QRY = " SELECT `id`,`company_id`,`template_type`,`company_logo`,`display_logo`,`accent_color`,`default_payment_terms`,`default_title`,`default_sub_heading`,`default_footer`,`standard_memo`,`items`,`units`,`price`,`amount`,`hide_item_name`,`hide_item_description`,`hide_units`,`hide_price`,`hide_amount` FROM invoice_preferences WHERE `company_id` = ? ";
	}
	
	public final class InvoiceTaxes {
		public final static String INSERT_QRY = "INSERT INTO invoice_taxes (`invoice_id`,`tax_id`,`tax_rate`) VALUES (?,?,?);";
		public final static String DELETE_QRY = "DELETE FROM `invoice_taxes` WHERE `invoice_id`=?;";
		public final static String GET_QRY = "SELECT `invoice_id`,`tax_id`,`tax_rate` FROM invoice_taxes WHERE `invoice_id` = ?;";
	}
	
	public final class Proposal {
		public final static String INSERT_QRY = "INSERT INTO `proposal` (`id`,`user_id`,`company_id`,`company_name`,`amount`,`currency`,`description`,`objectives`,`last_updated_by`,`last_updated_at`,`first_name`,`last_name`,`state`,`proposal_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`item_id`,`item_name`,`coa_id`,`coa_name`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		public final static String UPDATE_QRY = "UPDATE `proposal` SET `user_id` = ?,`company_id` = ?,`company_name` = ?,`amount` = ?,`currency` = ?,`description` = ?,`objectives` = ?,`last_updated_by` = ?,`last_updated_at` = ?,`first_name` = ?,`last_name` = ?,`state` = ?,`proposal_date` = ?,`acceptance_date` = ?,`acceptance_final_date` = ?,`notes` = ?,`item_id` = ?,`item_name` = ?,`coa_id` = ?,`coa_name` = ?,`discount` = ?,`deposit_amount` = ?,`processing_fees` = ?,`remainder_json` = ?,`remainder_mail_json`= ? WHERE `id` = ?";
		public final static String DELETE_QRY = "DELETE FROM proposal WHERE `id`=?;";
		public final static String GET_QRY = "SELECT p.`id`,p.`user_id`,p.`company_id`,p.`company_name`,p.`amount`,p.`currency`,p.`description`,p.`objectives`,p.`last_updated_by`,p.`last_updated_at`,p.`first_name`,p.`last_name`,p.`state`,p.`proposal_date`,p.`acceptance_date`,p.`acceptance_final_date`,p.`notes`,p.`item_id`,p.`item_name`,p.`coa_id`,p.`coa_name`,p.`discount`,p.`deposit_amount`,p.`processing_fees`,p.`remainder_json`,p.`remainder_mail_json`, pl.`id` AS `plid`,pl.`proposal_id`,pl.`description` `pl_description`,pl.`objectives` `pl_objectives`,pl.`amount` `pl_amount`,pl.`currency` `pl_currency`,pl.`last_updated_by` `pl_last_updated_by`,pl.`last_updated_at` `pl_last_updated_at`,pl.`quantity` `pl_quantity`,pl.`price` `pl_price`,pl.`notes` `pl_notes`, plt.`proposal_line_id` `plt_proposal_line_id`,plt.`tax_id` `plt_tax_id`,plt.`tax_rate` `plt_tax_rate` FROM `proposal` p LEFT JOIN `proposal_lines` pl ON p.id=pl.proposal_id LEFT JOIN `proposal_line_taxes` plt ON pl.id =plt.proposal_line_id WHERE p.id = ?;";
		public final static String GET_PROPOSAL_LIST_QRY = "SELECT `id`,`user_id`,`company_id`,`company_name`,`amount`,`currency`,`description`, `objectives`,`last_updated_by`,`last_updated_at`,`first_name`,`last_name`,`state`,`proposal_date`,`acceptance_date`,`acceptance_final_date`,`notes`,`item_id`,`item_name`,`coa_id`,`coa_name`,`discount`,`deposit_amount`,`processing_fees`,`remainder_json`,`remainder_mail_json` FROM proposal WHERE `user_id` = ?;";
	}
	
	public final class ProposalLine {
		public final static String INSERT_QRY = "INSERT INTO `proposal_lines` (`id`,`proposal_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes`) VALUES (?,?,?,?,?,?,?,?,?,?,?);";
		public final static String UPADTE_QRY = "UPDATE `proposal_lines` SET `description` = ?,`objectives` = ?,`amount`= ?,`currency` = ?,`last_updated_by`= ?,`last_updated_at` = ?,`quantity` = ?,`price` = ?,`notes` = ? WHERE `id` = ? AND `proposal_id` = ?;";
		public final static String GET_LINES_QRY = "SELECT `id`,`proposal_id`,`description`,`objectives`,`amount`,`currency`,`last_updated_by`,`last_updated_at`,`quantity`,`price`,`notes` FROM proposal_lines WHERE `id` = ?;";
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

}