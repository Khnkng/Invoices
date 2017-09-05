ALTER TABLE `qount`.`invoice`   
  ADD COLUMN `remainder_job_id` VARCHAR(200) NULL   COMMENT 'fk to remainder service job id' ;
ALTER TABLE `qount`.`invoice`   
  ADD COLUMN `remainder_name` VARCHAR(256) NULL ,
  ADD COLUMN `remainder_date` DATETIME NULL ;
