INSERT  INTO USER_TBL (`ID`, `TITLE`, `FIRSTNAME`, `LASTNAME`, `EMAIL`, `MOBILE`, `PASSWORD`,`ROLE`,`CODE`,
                               `STATUS`, `REGISTERED_AT`, `VERIFIED`,`VERIFIED_AT`,`DEACTIVATED_AT`)
VALUES
	(1,'Mr','Peter','Smith','psmith@gmail.com','00000000','12345678','USER','abctegdh','REGISTERED',NOW(),FALSE,NULL,NULL),
	(2,'Mrs','Mary','Green','merry_mary@hotmail.com','00000000','12345678','ADMIN','22hwdywyddy','VERIFIED',NOW(),TRUE,NOW(),NULL),
	(3,'Miss','Judy','Brown','judge.judy@yahoo.com','00000000','12345678','USER','0u93bdug','REGISTERED',NOW(),FALSE,NULL,NULL),
	(4,'Dr.','Paul','Sam','paulosammy@gmail.com','00000000','12345678','ADMIN','2e73ged','DEACTIVATED',NOW(),TRUE,NOW(),NOW()),
	(5,'Eng.','Mike','Myers','magicmike@gmail.com','00000000','12345678','USER','0djojd3v','DEACTIVATED',NOW(),FALSE,NULL,NOW())
