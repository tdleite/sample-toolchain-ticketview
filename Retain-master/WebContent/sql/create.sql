/*
CONNECT system/maximo
CREATE TABLESPACE ticketview datafile 'C:\users\ibm_admin\desktop\tablespace\ticketview.dbf' size 1000M autoextend on;
CREATE USER ticketview IDENTIFIED BY ticketview DEFAULT TABLESPACE ticketview TEMPORARY TABLESPACE temp;
GRANT ALL PRIVILEGES TO ticketview;
*/

/* Alter date time format */
ALTER SESSION SET nls_date_format = 'MM/DD/YYYY HH24:MI:SS';

/* Create table QUEUES */
CREATE TABLE QUEUES (
	QUEUEID		NUMBER(10)  	NOT NULL,
  	NAME          	VARCHAR2(50)  	NOT NULL,
  	LABEL         	VARCHAR2(50)  	NOT NULL);

ALTER TABLE QUEUES ADD (
	CONSTRAINT QUEUES_PK PRIMARY KEY (QUEUEID));

CREATE SEQUENCE QUEUES_SEQ;

COMMIT;

CREATE OR REPLACE TRIGGER QUEUES_BIR
BEFORE INSERT ON QUEUES
FOR EACH ROW

BEGIN
  SELECT QUEUES_SEQ.NEXTVAL
  INTO   :new.QUEUEID
  FROM   dual;
END;
/

/* Maximo IS L3 queues */
INSERT INTO QUEUES (NAME, LABEL) VALUES ('TRISL3,13K', 'Transportation');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('UTISL3,13K', 'Utilities');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('CAISL3,13K', 'Calibration');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('NUISL3,13K', 'Nuclear');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MBISL3,13K', 'Mobile');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('SPISL3,13K', 'Service Providers');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('CMISL3,13K', 'ACM');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('STISL3,13K', 'Spatial');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('OGISL3,13K', 'Oil and Gas');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('PVISL3,13K', 'Primavera');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('GOISL3,13K', 'Government');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('IDISL3,13K', 'ID');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MHSEL3,13K', 'HSE');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MXL3AV,13K', 'Aviation');

/* Maximo Core L3 queues */
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MXL3SY,13K', 'Maximo L3 System');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MXL3AP,13K', 'Maximo L3 Application');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MXL3UI,13K', 'Maximo L3 UI');

/* Maximo L2 queues */
INSERT INTO QUEUES (NAME, LABEL) VALUES ('SYST18,12H', 'SYST18,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('WMWK06,12H', 'WMWK06,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MTFEWK,12H', 'MTFEWK,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('WMWK09,12H', 'WMWK09,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MMWK10,12H', 'MMWK10,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('REPT05,12H', 'REPT05,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('TRNSFE,12H', 'TRNSFE,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MXWK12,12H', 'MXWK12,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('STFEWK,12H', 'STFEWK,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MMWK05,12H', 'MMWK05,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('SRM,14J', 'SRM,14J');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('MTLMFE,12H', 'MTLMFE,12H');
INSERT INTO QUEUES (NAME, LABEL) VALUES ('OCFEWK,12H', 'OCFEWK,12H');

/* Create table ENGINEERS */
CREATE TABLE ENGINEERS (
	ENGINEERID		NUMBER(10)  	NOT NULL,
	RETAINID       VARCHAR2(20) 	NOT NULL,
  	NAME          	VARCHAR2(100)  	NOT NULL);

ALTER TABLE ENGINEERS ADD (
	CONSTRAINT ENGINEERS_PK PRIMARY KEY (ENGINEERID));

CREATE SEQUENCE ENGINEERS_SEQ;

COMMIT;

CREATE OR REPLACE TRIGGER ENGINEERS_BIR
BEFORE INSERT ON ENGINEERS
FOR EACH ROW

BEGIN
  SELECT ENGINEERS_SEQ.NEXTVAL
  INTO   :new.ENGINEERID
  FROM   dual;
END;
/

/* Maximo IS L3 team */
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('120548', 'Gabriel do Nascimento Ribeiro');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('080302', 'Bruno Frenedozo Soave');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('053325', 'Diego Rafael dos Santos');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('107269', 'Fabiana Azevedo Kawazoe');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('117786', 'Leandro Yoshida');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('032299', 'Leonardo Cavalcante Alvino');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('120575', 'Leticia Verginia Penha');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('095721', 'Marlon Nascimento Vicente');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('003622', 'Mathias Drasbek Sorensen');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('105788', 'Patricia Yuriko Yamashita');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('109823', 'Rafael Sorana de Matos');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('118884', 'Renato Seixas Esteves');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('005604', 'Stella Fraguas');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('120740', 'Tabata Del Brollo Leite');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('018376', 'Florence Roxane Sarmah');

/* Maximo L2 team */
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('784609', 'David Leftwich');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('000625', 'Navin Desai');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('088179', 'Mary Beth Downey');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('016329', 'Suraj Singh');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('040527', 'Edgar Mengelberg');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('975598', 'Aoife O''Brien');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('005128', 'Scott Erickson');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('285010', 'Tohru Kohyama');

/* Maximo L1 team */
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('792416', 'Gabriela Bucatica');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('792726', 'Mirela Socol');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('000030', 'Sergiu Turcanu');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('500757', 'Bogdan Maria');
INSERT INTO ENGINEERS (RETAINID, NAME) VALUES ('005128', 'Scott Erickson');

/* Create table CLIENTS */
CREATE TABLE CLIENTS (
	CLIENTID		NUMBER(10)    	NOT NULL,
	ICN		    	VARCHAR2(20)	NOT NULL,
  	NAME          	VARCHAR2(100)  	NOT NULL);

ALTER TABLE CLIENTS ADD (
	CONSTRAINT CLIENTS_PK PRIMARY KEY (CLIENTID));

CREATE SEQUENCE CLIENTS_SEQ;

COMMIT;

CREATE OR REPLACE TRIGGER CLIENTS_BIR
BEFORE INSERT ON CLIENTS
FOR EACH ROW

BEGIN
  SELECT CLIENTS_SEQ.NEXTVAL
  INTO   :new.CLIENTID
  FROM   dual;
END;
/

/* Clients */
INSERT INTO CLIENTS (ICN, NAME) VALUES ('2591935', 'Duke Energy');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0871297', 'Toshiba');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0030816', 'Coor Service Management');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0505667', 'Fortum Oyj');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0745493', 'IAF Technical Manager');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0139766', 'SoTech Services');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0293286', 'Vinci PLC');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0A04EBY', 'Siemens');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('6402800', 'Nevada Power');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0695944', 'Avista');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('2614831', 'DTE Energy');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0029541', 'INSA');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0675628', 'ADWEA');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0159902', 'Eiffage Systems');
INSERT INTO CLIENTS (ICN, NAME) VALUES ('0646814', 'Texas City of Austin');

/* Create table VIEWS */
CREATE TABLE VIEWS (
	VIEWID				NUMBER(10)    		NOT NULL,
	RETAINID          	VARCHAR2(10)  		NOT NULL,
	NAME				VARCHAR2(100)		NOT NULL,
  	JSON		   		CLOB				NOT NULL,
  	ISPUBLIC			NUMBER(1)			NOT NULL,
  	ISMAIN				NUMBER(1)			NOT NULL);

ALTER TABLE VIEWS ADD (
	CONSTRAINT VIEWS_PK PRIMARY KEY (VIEWID));

CREATE SEQUENCE VIEWS_SEQ;

COMMIT;

CREATE OR REPLACE TRIGGER VIEWS_BIR
BEFORE INSERT ON VIEWS
FOR EACH ROW

BEGIN
  SELECT VIEWS_SEQ.NEXTVAL
  INTO   :new.VIEWID
  FROM   dual;
END;
/

/* Create table PMRSTATUS */
CREATE TABLE PMRSTATUS (
	PMRSTATUSID			NUMBER(10)    		NOT NULL,
	PMR		          	VARCHAR2(20)  		NOT NULL,
	STATUS				VARCHAR2(50)		NOT NULL,
  	CHANGEDON	   		DATE,
  	CHANGEDBY			VARCHAR2(20)		NOT NULL,
  	TIMEZONE			NUMBER(4)			NOT NULL);

ALTER TABLE PMRSTATUS ADD (
	CONSTRAINT PMRSTATUS_PK PRIMARY KEY (PMRSTATUSID));

CREATE SEQUENCE PMRSTATUS_SEQ;

COMMIT;

CREATE OR REPLACE TRIGGER PMRSTATUS_BIR
BEFORE INSERT ON PMRSTATUS
FOR EACH ROW

BEGIN
  SELECT PMRSTATUS_SEQ.NEXTVAL
  INTO   :new.PMRSTATUSID
  FROM   dual;
END;
/

CREATE OR REPLACE TRIGGER pmrstatus_changedon
BEFORE INSERT ON PMRSTATUS
FOR EACH ROW
BEGIN
  :new.changedon := sysdate;
END;
/

/* Create table NOTIFICATIONS */
CREATE TABLE NOTIFICATIONS (
	NOTIFICATIONID				NUMBER(10)    		NOT NULL,
	RETAINID          			VARCHAR2(10)  		NOT NULL,
  	JSON		   				CLOB				NOT NULL);

ALTER TABLE NOTIFICATIONS ADD (
	CONSTRAINT NOTIFICATIONS_PK PRIMARY KEY (NOTIFICATIONID));

CREATE SEQUENCE NOTIFICATIONS_SEQ;

COMMIT;

CREATE OR REPLACE TRIGGER NOTIFICATIONS_BIR
BEFORE INSERT ON NOTIFICATIONS
FOR EACH ROW

BEGIN
  SELECT NOTIFICATIONS_SEQ.NEXTVAL
  INTO   :new.NOTIFICATIONID
  FROM   dual;
END;
/