ALTER TABLE `qount`.`invoice` ADD COLUMN `tax_amount` DECIMAL(15,4) NULL;
ALTER TABLE `qount`.`invoice` ADD COLUMN `proposal_id` VARCHAR(256) NULL;
ALTER TABLE `qount`.`proposal` ADD COLUMN `estimate_date` datetime NULL;
ALTER TABLE `qount`.`proposal` ADD COLUMN `invoice_id` VARCHAR(256) NULL;
ALTER TABLE `qount`.`proposal` ADD COLUMN `tax_amount` DECIMAL(15,4) NULL;