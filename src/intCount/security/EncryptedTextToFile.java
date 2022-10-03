package intCount.security;

import java.io.BufferedInputStream;


import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jasypt.util.text.BasicTextEncryptor;


public class EncryptedTextToFile {

	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	FileInputStream fis = null;
	BufferedInputStream bis = null;
	// String fileName =
	// System.getProperty("user.dir").concat("/myDatfile.dat");

	public boolean WriteEncryptedTextToFile(String originalText, String password, String fileName) {

		try {
			fos = new FileOutputStream(fileName);
			bos = new BufferedOutputStream(fos);
			//System.out.println("originalText = " + originalText);

			/*
			 * Encrypting a text
			 */
			BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
			// Must remember this password, as it will be required at time of

			basicTextEncryptor.setPassword(password);
			String encryptedText = basicTextEncryptor.encrypt(originalText);
			//System.out.println("encryptedText = " + encryptedText);

			// convert encryptedText into byte array to write it in file
			bos.write(encryptedText.getBytes());
			bos.flush();

			//System.out.println("encryptedText has been written successfully in " + fileName);

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

	public String ReadEncryptedTextFromFile(String password, String fileName) {
		String decryptedTextOfFile = null;
		try

		{
			FileInputStream fis = new FileInputStream(fileName);
			bis = new BufferedInputStream(fis);

			// create byteArray of file's size
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes); // Read file in ByteArray

			String encryptedTextOfFile = new String(bytes); // convert ByteArray
			// to
			// String
			//System.out.println("encryptedTextOfFile = " + encryptedTextOfFile);

			/*
			 * Decrypting text of file
			 */
			BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
			// Give password given at time of encryption
			textEncryptor.setPassword(password);

			decryptedTextOfFile = textEncryptor.decrypt(encryptedTextOfFile);
			//System.out.println("decryptedTextOfFile = " + "\n" + decryptedTextOfFile);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close(); // close FileInputStream
				if (bis != null)
					bis.close(); // close BufferedInputStream
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return decryptedTextOfFile;
	}

}
