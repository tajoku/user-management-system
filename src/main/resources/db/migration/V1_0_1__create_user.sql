CREATE TABLE IF NOT EXISTS `USER_TBL` (

    `ID` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `TITLE` VARCHAR(50) NOT NULL,
    `FIRSTNAME` VARCHAR(50) NOT NULL,
    `LASTNAME` VARCHAR(50) NOT NULL,
    `EMAIL` VARCHAR(50) NOT NULL,
    `MOBILE` VARCHAR(50) NOT NULL,
    `PASSWORD` VARCHAR(50) NOT NULL,
    `ROLE` VARCHAR(20) NOT NULL,
    `STATUS` VARCHAR(20) NOT NULL,
    `REGISTERED_AT` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `VERIFIED` BOOLEAN NOT NULL DEFAULT FALSE,
    `VERIFIED_AT` TIMESTAMP,
    `DEACTIVATED_AT` TIMESTAMP
);

CREATE UNIQUE INDEX UNIQ_USER_EMAIL ON `USER_TBL` (`EMAIL`);