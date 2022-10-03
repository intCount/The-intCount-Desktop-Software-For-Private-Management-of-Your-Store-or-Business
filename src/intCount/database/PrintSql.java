package intCount.database;

import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import intCount.security.EncryptedTextToFile;

public class PrintSql {

	EncryptedTextToFile encryptedTextToFile = new EncryptedTextToFile();
	static FileOutputStream fos = null;
	static BufferedOutputStream bos = null;

	public static String request() {

		String requete = "\n" + "CREATE SCHEMA" + " " + "\"DINESH\"" + ";" + "\n" + "CREATE FUNCTION" + " " + "\"APP\""
				+ "." + "\"CUSTOMER_OPENING_BALANCE\"" + "(" + "\"CUSTOMERID\""
				+ "INTEGER ) RETURNS DECIMAL ( 11 , 2 ) LANGUAGE JAVA PARAMETER STYLE JAVA READS SQL DATA CALLED ON NULL INPUT EXTERNAL NAME 'fxbilling.database.udf.DBMethods.getCustomerOpeningBalance' ;"
				+ "\n" + "CREATE FUNCTION" + " " + "\"APP\"" + "." + "\"CUSTOMER_BALANCE\"" + "(" + "\"CUSTOMERID\""
				+ " INTEGER ," + "\"ENDDATESTRING\""
				+ "CHAR ( 10 ) ) RETURNS DECIMAL ( 11 , 2 ) LANGUAGE JAVA PARAMETER STYLE JAVA READS SQL DATA CALLED ON NULL INPUT EXTERNAL NAME 'fxbilling.database.udf.DBMethods.getCustomerBalance' ;"
				+ "\n"

				+ "CREATE FUNCTION" + " " + "\"APP\"" + "." + "\"INVOICE_TOTAL\"" + "(" + "\"INVOICENUMBER\""
				+ "INTEGER ) RETURNS DECIMAL ( 11 , 2 ) LANGUAGE JAVA PARAMETER STYLE JAVA READS SQL DATA CALLED ON NULL INPUT EXTERNAL NAME 'fxbilling.database.udf.DBMethods.getInvoiceTotal' ;"
				+ "\n" + "CREATE TABLE" + " " + "\"APP\"" + "." + "\"FIRM_DETAILS\"" + "(" + "\"FIRM_NAME\""
				+ "VARCHAR ( 70 ) NOT NULL , " + "\"ADDRESS\"" + " VARCHAR ( 120 ) NOT NULL ," + "\"PHONE_NUMBERS\""
				+ "VARCHAR ( 120 ) ," + "\"EMAIL_ADDRESS\"" + " VARCHAR ( 50 ) ," + "\"LOGO\"" + " BLOB ( 524288 ) ,"
				+ "\"TAXE_PROFESSIONNEL\"" + " VARCHAR ( 50 ) ," + "\"REGISTRE_DE_COMMERCE\"" + " VARCHAR ( 50 ) ,"
				+ "\"BANK_ACCOUNT\"" + " VARCHAR ( 30 ) ," + "\"IDENTIFICATION_FISCAL\"" + " VARCHAR ( 50 ) , "
				+ "\"NUMERO_DE_CNSS\"" + " VARCHAR ( 50 ) , " + "\"ICE\"" + " VARCHAR ( 50 ) ) ;" + "\n"

				+ "CREATE TABLE" + " " + "\"APP\"" + "." + "\"MEASUREMENT_UNITS\"" + "(" + "\"ID\""
				+ "INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1 , INCREMENT BY 1 ) ," + "\"NAME\""
				+ " VARCHAR ( 40 ) NOT NULL ," + "\"ABBREVIATION\"" + " VARCHAR ( 15 ) ) ;" + "\n" + " CREATE TABLE"
				+ " " + "\"APP\"" + "." + "\"CUSTOMERS\"" + "(" + "\"CUST_ID\""
				+ "INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1 , INCREMENT BY 1 ) ," + "\"NAME\""
				+ "VARCHAR ( 70 ) NOT NULL , " + "\"CITY\"" + " VARCHAR ( 100 ) ," + "\"PHONE_NUMBERS\""
				+ " VARCHAR ( 120 ) ,"  + "\"TAXE_PROFESSIONNEL\"" + " VARCHAR ( 80 ) ," + "\"ICE\"" + " VARCHAR ( 50 ) ," + "\"OPENING_BALANCE\""
				+ " DECIMAL ( 10 , 2 ) , " + "\"BALANCE_TYPE\"" + "CHAR ( 1 ) ) ; " + "\n" + "CREATE TABLE" + " "
				+ "\"APP\"" + "." + "\"ITEMS\"" + "(" + "\"ID\""
				+ "INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1 , INCREMENT BY 1 ) ," + "\"NAME\""
				+ "VARCHAR ( 150 ) NOT NULL ," + "\"HT\"" + " DECIMAL ( 8 , 2 ) NOT NULL ," + "\"TVA\""
				+ " DECIMAL ( 4 , 2 ) NOT NULL ," + "\"DATE_ENTREE\"" + " DATE NOT NULL ," + "\"STOCK_INIT\""
				+ " INTEGER NOT NULL) ;" + "\n" + " CREATE TABLE" + " " + "\"APP\"" + "." + "\"FIRM_COUNT\"" + "("
				+ "\"FIRM_COUNT\"" + "INTEGER default 0 ) ;  " + "\n" + "CREATE TABLE" + " " + "\"APP\"" + "."
				+ "\"PAYMENTS\"" + "(" + "\"ID\""
				+ "INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1 , INCREMENT BY 1 ) ," + "\"CUSTOMERID\""
				+ " INTEGER NOT NULL ," + "\"PAYMENTDATE\"" + "DATE NOT NULL ," + "\"AMOUNT\"" + " DECIMAL ( 12 , 2 ) ,"
				+ "\"PAYMENTMODE\"" + " VARCHAR ( 20 ) NOT NULL ) ;" + "\n" + " CREATE TABLE" + " " + "\"APP\"" + "."
				+ "\"INVOICES\"" + "(" + "\"ID\""
				+ " INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1 , INCREMENT BY 1 ) ,"
				+ "\"INVOICEDATE\"" + " DATE NOT NULL ," + "\"ISCASHINVOICE\"" + " BOOLEAN NOT NULL ,"
				+ "\"CUSTOMERID\"" + " INTEGER ," + "\"DISCOUNT\"" + " DECIMAL ( 8 , 2 ) ," + "\"ADDITIONALCHARGE\""
				+ " DECIMAL ( 8 , 2 ) ," + "\"INVOICETYPE\"" + " VARCHAR ( 30 ) NOT NULL );" + "\n" + "CREATE TABLE"
				+ " " + "\"APP\"" + "." + "\"INVOICE_ITEMS\"" + "(" + "\"INVOICE_ID\"" + "INTEGER NOT NULL ,"
				+ "\"ITEM_ID\"" + " INTEGER NOT NULL ," + "\"RATE\"" + "DECIMAL ( 8 , 2 ) NOT NULL ,"
				+ "\"MEASUREMENT_UNIT\"" + " INTEGER NOT NULL ," + "\"QUANTITY\"" + " DECIMAL ( 9 , 3 ) NOT NULL ,"
				+ "\"TVA\"" + " DECIMAL ( 4 , 2 ) NOT NULL ) ;" + "\n"

				+ "CREATE TABLE" + " " + "\"APP\"" + "." + "\"PAYMENT_DETAILS\"" + "(" + "\"PAYMENTID\""
				+ " INTEGER NOT NULL ," + "\"INSTRUMENTNUMBER\"" + " VARCHAR ( 15 ) NOT NULL ," + "\"INSTRUMENTDATE\""
				+ " DATE NOT NULL ," + "\"DRAWANATBANK\"" + " VARCHAR ( 100 ) ," + "\"AMOUNTREALISED\""
				+ "BOOLEAN DEFAULT false ) ;" + "\n"

				+ "CREATE INDEX" + " " + "\"APP\"" + "." + "\"IDX_INVOICES_INVOICEDATE\"" + "ON" + "\"APP\"" + "."
				+ "\"INVOICES\"" + "(" + "\"INVOICEDATE\"" + ") ;" + "CREATE INDEX" + " " + "\"APP\"" + "."
				+ "\"IDX_INVOICES_ISCASHINVOICE\"" + "ON" + "\"APP\"" + "." + "\"INVOICES\"" + "(" + "\"ISCASHINVOICE\""
				+ ");" + "\n"

				+ "CREATE INDEX" + " " + "\"APP\"" + "." + "\"IDX_PAYMENT_DETAILS_AMOUNTREALISED\"" + "ON" + "\"APP\""
				+ "." + "\"PAYMENT_DETAILS\"" + "(" + "\"AMOUNTREALISED\"" + " ) ;" + "CREATE INDEX" + " " + "\"APP\""
				+ "." + "\"IDX_PAYMENTS_PAYMENTMODE\"" + "ON" + "\"APP\"" + "." + "\"PAYMENTS\"" + " ("
				+ "\"PAYMENTMODE\"" + ") ;" + "\n"

				+ "CREATE INDEX" + " " + "\"APP\"" + "." + "\"IDX_PAYMENTS_PAYMENTMODE\"" + "ON" + "\"APP\"" + "."
				+ "\"PAYMENTS\"" + "(" + "\"PAYMENTMODE\"" + ") ;" + "\n"

				+ "CREATE INDEX" + " " + "\"APP\"" + "." + "\"IDX_PAYMENTS_AMOUNT\"" + "ON" + "\"APP\"" + "."
				+ "\"PAYMENTS\"" + "( " + "\"AMOUNT\"" + ");" + "\n"

				+ "CREATE INDEX" + " " + "\"APP\"" + "." + "\"IDX_PAYMENTS_AMOUNT\"" + "ON" + " " + "\"APP\"" + "."
				+ "\"PAYMENTS\"" + "(" + "\"AMOUNT\"" + ");" + "\n"

				+ "CREATE INDEX" + " " + "\"APP\"" + "." + "\"IDX_PAYMENTS_PAYMENTDATE\"" + "ON" + "\"APP\"" + "."
				+ "\"PAYMENTS\"" + "(" + "\"PAYMENTDATE\"" + ");" + "\n"

				+ "ALTER TABLE" + "" + "\"APP\"" + "." + "\"INVOICES\"" + "ADD CONSTRAINT" + "\"INVOICES_PK_ID\""
				+ "PRIMARY KEY(" + "\"ID\"" + ");" + "\n"

				+ "ALTER TABLE" + " " + "\"APP\"" + "." + "\"CUSTOMERS\"" + "ADD CONSTRAINT" + "\"SQL160228221537110\""
				+ "PRIMARY KEY (" + "\"CUST_ID\"" + ") ;" + "\n"

				+ "ALTER TABLE" + " " + "\"APP\"" + "." + "\"CUSTOMERS\"" + "ADD CONSTRAINT" + "\"UNIQUE_NAME\""
				+ "UNIQUE (" + "\"NAME\"" + ") ;" + "\n"

				+

				"ALTER TABLE" + " " + "\"APP\"" + "." + "\"MEASUREMENT_UNITS\"" + "ADD " + "CONSTRAINT" + "\"PK_UOM\""
				+ " PRIMARY KEY (" + "\"ID\"" + ") ;" + "\n"

				+ "ALTER TABLE" + " " + "\"APP\"" + "." + "\"MEASUREMENT_UNITS\"" + "ADD CONSTRAINT"
				+ "\"UNQ_UOM_NAME\"" + "UNIQUE ( " + "\"NAME\"" + ") ;" + "\n"

				+ "ALTER TABLE " + " " + "\"APP\"" + "." + "\"ITEMS\"" + " ADD CONSTRAINT" + "\"ITEMS_PK_ID\""
				+ "PRIMARY KEY ( " + "\"ID\"" + ") ;" + "\n"

				+ "ALTER TABLE" + " " + "\"APP\"" + "." + "\"ITEMS\"" + "ADD CONSTRAINT" + "\"ITEMS_UNQ_NAME\""
				+ "UNIQUE ( " + "\"NAME\"" + ") ;" + "\n" + "ALTER TABLE" + " " + "\"APP\"" + "." + "\"PAYMENTS\""
				+ "ADD CONSTRAINT" + "\"PAYMENTS_PK_ID\"" + " PRIMARY KEY ( " + "\"ID\"" + ") ;" + "\n"

				+ "ALTER TABLE " + " " + "\"APP\"" + "." + "\"INVOICE_ITEMS\"" + "ADD CONSTRAINT"
				+ "\"INVOICE_ITEMS_PK\"" + "PRIMARY KEY ( " + "\"INVOICE_ID\"" + "," + "\"ITEM_ID\"" + ") ;" + "\n"

				+ "ALTER TABLE" + " " + "\"APP\"" + "." + "\"INVOICES\"" + "ADD CONSTRAINT"
				+ "\"INVOICES_FK_CUSTOMERID\"" + "FOREIGN KEY ( " + "\"CUSTOMERID\"" + ") REFERENCES" + " " + "\"APP\""
				+ "." + "\"CUSTOMERS\"" + "(" + "\"CUST_ID\"" + ")" + "ON DELETE RESTRICT ON UPDATE NO ACTION ;" + "\n"

				+ "ALTER TABLE" + " " + "\"APP\"" + "." + "\"PAYMENT_DETAILS\"" + "ADD CONSTRAINT"
				+ "\"PAYMENT_DETAILS_FK_PAYMENTID\"" + "FOREIGN KEY (" + "\"PAYMENTID\"" + ") REFERENCES" + " "
				+ "\"APP\"" + "." + "\"PAYMENTS\"" + "(" + "\"ID\"" + ") ON DELETE CASCADE ON UPDATE RESTRICT ;" + "\n"

				+ "ALTER TABLE" + " " + "\"APP\"" + "." + "\"PAYMENTS\"" + "ADD CONSTRAINT"
				+ "\"PAYMENTS_FK_CUSTOMERID\"" + "FOREIGN KEY ( " + "\"CUSTOMERID\"" + ") REFERENCES" + " " + "\"APP\""
				+ "." + "\"CUSTOMERS\"" + " (" + "\"CUST_ID\"" + ") ON DELETE RESTRICT ON UPDATE NO ACTION ;" + "\n"

				+ "ALTER TABLE" + " " + "\"APP\"" + "." + "\"INVOICE_ITEMS\"" + "ADD CONSTRAINT"
				+ "\"INVOICE_ITEMS_FK_INVOICE_ID\"" + "FOREIGN KEY (" + "\"INVOICE_ID\"" + ") REFERENCES" + " "
				+ "\"APP\"" + "." + "\"INVOICES\"" + "(" + "\"ID\"" + ") ON DELETE CASCADE ON UPDATE NO ACTION ;" + "\n"

				+ "ALTER TABLE" + " " + "\"APP\"" + "." + "\"INVOICE_ITEMS\"" + "ADD CONSTRAINT"
				+ "\"INVOICE_ITEMS_FK_ITEM_ID\"" + " FOREIGN KEY (" + "\"ITEM_ID\"" + ") REFERENCES" + " " + "\"APP\""
				+ "." + "\"ITEMS\"" + "(" + "\"ID\"" + " ) ON DELETE RESTRICT ON UPDATE NO ACTION ;" + "\n"

				+ "ALTER TABLE " + " " + "\"APP\"" + "." + "\"INVOICE_ITEMS\"" + "ADD CONSTRAINT"
				+ "\"INVOICE_ITEMS_FK_UOM\"" + " FOREIGN KEY (" + "\"MEASUREMENT_UNIT\"" + ") REFERENCES" + " "
				+ "\"APP\"" + "." + "\"MEASUREMENT_UNITS\"" + " (" + "\"ID\""
				+ ") ON DELETE RESTRICT ON UPDATE NO ACTION ;" + "\n"

				+ "ALTER TABLE " + " " + "\"APP\"" + "." + "\"CUSTOMERS\"" + "ADD CONSTRAINT" + "\"VALID_BALANCE_TYPE\""
				+ " CHECK ( upper ( balance_type ) in ( 'C' , 'D' ) ) ;" + "\n"

				+ "ALTER TABLE  " + " " + "\"APP\"" + "." + "\"PAYMENTS\"" + "ADD CONSTRAINT" + "\"PAYMENTS_CK_AMOUNT\""
				+ "CHECK ( amount > 0.0 ) ;" + "\n" + "ALTER TABLE " + " " + "\"APP\"" + "." + "\"PAYMENTS\""
				+ "ADD CONSTRAINT" + "\"PAYMENTS_CHECK_PAYMENTMODE\""
				+ "CHECK ( lower ( paymentMode ) in ( 'cash' , 'cheque' , 'dd' , 'banktransfer' ) ) ;";

		return requete;

	}

	public void encrypteDbFile() {

		encryptedTextToFile.WriteEncryptedTextToFile(request(), "[5;x')7F|(M^.168HC|4#$,;'c1J.$~d+",
				System.getProperty("user.dir").concat("/src/resources/sql").concat("/db-ddl.sql"));

	}

	public String readencryptedFile() {
		return encryptedTextToFile.ReadEncryptedTextFromFile("[5;x')7F|(M^.168HC|4#$,;'c1J.$~d+",
				System.getProperty("user.dir").concat("/src/resources/sql").concat("/db-ddl.sql"));

	}

	public boolean WriteEncryptedTextToFile(File fileName) {

		try {
			fos = new FileOutputStream(fileName);
			bos = new BufferedOutputStream(fos);

			// convert encryptedText into byte array to write it in file
			bos.write(request().getBytes());
			bos.flush();

			// System.out.println("encryptedText has been written successfully
			// in " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close(); // close FileOutputStream
				}
				if (fos != null) {
					fos.close(); // close FileOutputStream
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
