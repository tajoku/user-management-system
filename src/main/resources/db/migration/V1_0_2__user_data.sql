INSERT  INTO USER_TBL (`ID`, `TITLE`, `FIRSTNAME`, `LASTNAME`, `EMAIL`, `MOBILE`, `PASSWORD`,`ROLE`,
                               `STATUS`, `REGISTERED_AT`, `VERIFIED`,`VERIFIED_AT`,`DEACTIVATED_AT`)
VALUES
	(1,'Mr','Peter','Smith','psmith@gmail.com','00000000','12345678','USER','REGISTERED',NOW(),FALSE,NULL,NULL),
	(2,'Mrs','Mary','Green','merry_mary@hotmail.com','00000000','12345678','ADMIN','VERIFIED',NOW(),TRUE,NOW(),NULL),
	(3,'Miss','Judy','Brown','judge.judy@yahoo.com','00000000','12345678','USER','REGISTERED',NOW(),FALSE,NULL,NULL),
	(4,'Dr.','Paul','Sam','paulosammy@gmail.com','00000000','12345678','ADMIN','DEACTIVATED',NOW(),TRUE,NOW(),NOW()),
	(5,'Eng.','Mike','Myers','magicmike@gmail.com','00000000','12345678','USER','DEACTIVATED',NOW(),FALSE,NULL,NOW())
