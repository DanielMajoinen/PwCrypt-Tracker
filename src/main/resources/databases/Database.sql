CREATE TABLE "account" (
  `account_uuid` VARCHAR[36],
  `email` VARCHAR[256] NOT NULL UNIQUE,
  `verified` INTEGER DEFAULT 0,
  PRIMARY KEY(`account_uuid`) );
CREATE TABLE "device" (
  `device_uuid` VARCHAR[36] NOT NULL,
  `account_uuid` VARCHAR[36] NOT NULL,
  `ip_address` VARCHAR[15] NOT NULL,
  `platform` VARCHAR[256] NOT NULL,
  `public_key` VARCHAR[134] NOT NULL,
  `verified` INTEGER DEFAULT 0,
  `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(`device_uuid`,`account_uuid`),
  FOREIGN KEY(`account_uuid`) REFERENCES `account`(`account_uuid`) );
CREATE TABLE "device_verify_code" (
  `device_uuid` VARCHAR[36],
  `account_uuid` VARCHAR[36],
  `verify_code` VARCHAR[20],
  `timestamp` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(`device_uuid`,`account_uuid`,`verify_code`),
  FOREIGN KEY(`device_uuid`) REFERENCES `device`(`device_uuid`) );