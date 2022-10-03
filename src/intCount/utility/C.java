package intCount.utility;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class C {
	private static String a = null;

	public static String a() {
		Logger.getLogger(C.class.getName()).log(Level.FINE, "start", "");

		try {
			UUID uUID = UUID.nameUUIDFromBytes(InetAddress.getLocalHost().getHostName().getBytes("UTF-8"));

			return "1" + uUID.toString();
		} catch (UnknownHostException unknownHostException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG101", unknownHostException);

		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG102", unsupportedEncodingException);
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG147", exception);
		}

		return null;
	}

	public static String b() {
		Logger.getLogger(C.class.getName()).log(Level.FINE, "start", "");

		try {
			String str = null;

			if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("windows")) {
				NetworkInterface networkInterface;

				if (!(networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost())).isLoopback()
						&& !networkInterface.isVirtual() && !networkInterface.isPointToPoint()
						&& networkInterface.getHardwareAddress() != null) {
					str = B.a(networkInterface.getHardwareAddress());
				}

				if (str == null) {
					str = g();
				}
			} else {
				str = g();
			}

			if (str != null && str.length() > 11) {
				UUID uUID = UUID.nameUUIDFromBytes(str.getBytes("UTF-8"));
				return "2" + uUID.toString();
			}
		} catch (SocketException socketException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG103", socketException);
		} catch (UnknownHostException unknownHostException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG104", unknownHostException);
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG105", unsupportedEncodingException);
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG146", exception);
		}
		return null;
	}

	public static String c() {
		Logger.getLogger(C.class.getName()).log(Level.FINE, "start", "");
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("windows")) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "start " + System.getProperty("os.name"), "");
			BufferedReader object = null;
			InputStreamReader inputStreamReader = null;
			try {
				Process process = Runtime.getRuntime().exec("cmd /C dir c:\\");
				inputStreamReader = new InputStreamReader(process.getInputStream(), "UTF-8");
				(object = new BufferedReader(inputStreamReader)).readLine();
				String str;
				if ((str = object.readLine()) != null
						&& (str = str.substring(str.length() - 9, str.length())).length() > 1)
					try {
						UUID uUID = UUID.nameUUIDFromBytes(str.getBytes("UTF-8"));
						return "3" + uUID.toString();
					} catch (UnsupportedEncodingException unsupportedEncodingException) {
						Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG145" + System.getProperty("os.name"),
								"");
					}
				return null;
			} catch (Exception exception) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG106", exception);
			} finally {
				try {
					if (object != null)
						object.close();
					if (inputStreamReader != null)
						inputStreamReader.close();
				} catch (Exception exception) {
					Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG107", exception);
				}
			}
		} else {
			if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("linux")) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "start " + System.getProperty("os.name"), "");
				String str;
				if ((str = o()) == null)
					str = p();
				if (str == null)
					str = q();
				if (str == null)
					str = r();
				if (str == null)
					str = s();
				if (str == null)
					str = t();
				if (str == null)
					str = u();
				if (str == null)
					str = v();
				return str;
			}
			if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("mac"))
				return w();
		}
		return null;
	}

	public static String d() {
		Logger.getLogger(C.class.getName()).log(Level.FINE, "start", "");
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("windows")) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "start " + System.getProperty("os.name"), "");
			File file = null;
			InputStream inputStream1 = null;
			FileOutputStream fileOutputStream = null;
			BufferedReader bufferedReader = null;
			InputStreamReader inputStreamReader = null;
			try {
				file = File.createTempFile("tmp", ".exe", new File(System.getProperty("user.home")));
			} catch (Exception exception) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG143", exception);
				try {
					file = File.createTempFile("tmp", ".exe");
				} catch (IOException iOException) {
					Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG108", iOException);
					File file1 = new File(".");
					try {
						file = File.createTempFile("tmp", ".exe", file1.getCanonicalFile());
					} catch (Exception exception1) {
						Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG109", exception1);
					}
				}
			}
			if (file != null)
				file.deleteOnExit();
			InputStream inputStream2 = null;
			try {
				inputStream1 = C.class.getResourceAsStream("resources/d.bfi");
				byte[] arrayOfByte = new byte[8192];
				fileOutputStream = new FileOutputStream(file);
				int i;
				while ((i = inputStream1.read(arrayOfByte)) != -1)
					fileOutputStream.write(arrayOfByte, 0, i);
				fileOutputStream.flush();
				fileOutputStream.close();
				inputStream1.close();
				inputStream2 = Runtime.getRuntime().exec(new String[] { file.getAbsolutePath() }).getInputStream();
				inputStreamReader = new InputStreamReader(inputStream2, "UTF-8");
				bufferedReader = new BufferedReader(inputStreamReader);
				String str2 = null;
				String str3 = null;
				boolean bool = false;
				String str1;
				while ((str1 = bufferedReader.readLine()) != null) {
					if (!bool && str1.startsWith("Product Id ="))
						str2 = str1;
					if (!bool && str1.startsWith("Serial Number =")) {
						str3 = str1;
						bool = true;
					}
				}
				try {
					Thread.sleep(50L);
				} catch (InterruptedException interruptedException) {
					Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG110", interruptedException);
				}
				inputStream2.close();
				file.delete();
				if (str2 != null && str3 != null) {
					str2 = str2.substring(str2.indexOf("[") + 1, str2.length() - 1);
					str3 = str3.substring(str3.indexOf("[") + 1, str3.length() - 1);
					if (str2.length() > 1 && str3.length() > 1)
						try {
							UUID uUID = UUID.nameUUIDFromBytes((str2 + " " + str3).getBytes("UTF-8"));
							return "4" + uUID.toString();
						} catch (UnsupportedEncodingException unsupportedEncodingException) {
							Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG111", unsupportedEncodingException);
						}
				}
			} catch (Exception exception) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG112", exception);
			} finally {
				try {
					if (inputStream1 != null)
						inputStream1.close();
					if (inputStream2 != null)
						inputStream2.close();
					if (fileOutputStream != null)
						fileOutputStream.close();
					if (bufferedReader != null)
						bufferedReader.close();
					if (inputStreamReader != null)
						inputStreamReader.close();
					if (file != null)
						file.delete();
				} catch (Exception exception) {
					Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG113", exception);
				}
			}
			return a;
		}
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("linux")) {
			String str;
			if ((str = h()) == null)
				str = i();
			if (str == null)
				str = j();
			if (str == null)
				str = k();
			if (str == null)
				str = l();
			if (str == null)
				str = m();
			return str;
		}
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("mac"))
			return n();
		return null;
	}

	public static boolean e() {
		Logger.getLogger(C.class.getName()).log(Level.FINE, "start", "");
		boolean bool = false;
		if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("windows")) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "start " + System.getProperty("os.name"), "");
			File file = null;
			InputStream inputStream1 = null;
			FileOutputStream fileOutputStream = null;
			BufferedReader bufferedReader = null;
			InputStreamReader inputStreamReader = null;
			try {
				file = File.createTempFile("tmp", ".exe", new File(System.getProperty("user.home")));
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG114", iOException);
				try {
					file = File.createTempFile("tmp", ".exe");
				} catch (IOException iOException1) {
					Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG115", iOException1);
					File file1 = new File(".");
					try {
						file = File.createTempFile("tmp", ".exe", file1.getCanonicalFile());
					} catch (Exception exception) {
						Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG116", exception);
					}
				}
			}
			if (file != null)
				file.deleteOnExit();
			InputStream inputStream2 = null;
			try {
				inputStream1 = C.class.getResourceAsStream("resources/d.bfi");
				byte[] arrayOfByte = new byte[8192];
				fileOutputStream = new FileOutputStream(file);
				int i;
				while ((i = inputStream1.read(arrayOfByte)) != -1)
					fileOutputStream.write(arrayOfByte, 0, i);
				fileOutputStream.flush();
				fileOutputStream.close();
				inputStream1.close();
				inputStream2 = Runtime.getRuntime().exec(new String[] { file.getAbsolutePath() }).getInputStream();
				inputStreamReader = new InputStreamReader(inputStream2, "UTF-8");
				bufferedReader = new BufferedReader(inputStreamReader);
				String str2 = null;
				boolean bool1 = false;
				String str1;
				while ((str1 = bufferedReader.readLine()) != null) {
					if (!bool1 && str1.startsWith("Product Id =")) {
						str2 = str1;
						bool1 = true;
					}
				}
				try {
					Thread.sleep(50L);
				} catch (InterruptedException interruptedException) {
					Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG117", interruptedException);
				}
				inputStream2.close();
				if (str2 != null)
					if (str2.toLowerCase(Locale.US).indexOf("vbox") != -1) {
						bool = true;
						a = "VirtualBox";
					} else if (str2.toLowerCase().indexOf("virtual hd") != -1) {
						bool = true;
						a = "Hyper-V";
					} else if (str2.toLowerCase(Locale.US).indexOf("vmware") != -1) {
						bool = true;
						a = "VMware";
					} else if (str2.toLowerCase(Locale.US).indexOf("qemu") != -1) {
						bool = true;
						a = "QEMU";
					} else if (str2.toLowerCase(Locale.US).indexOf("xen") != -1) {
						bool = true;
						a = "Xen";
					}
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG118", iOException);
			} finally {
				try {
					if (inputStream1 != null)
						inputStream1.close();
					if (inputStream2 != null)
						inputStream2.close();
					if (fileOutputStream != null)
						fileOutputStream.close();
					if (bufferedReader != null)
						bufferedReader.close();
					if (inputStreamReader != null)
						inputStreamReader.close();
					if (file != null)
						file.delete();
				} catch (IOException iOException) {
					Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG144", "");
				}
			}
		} else if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("linux")) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "start " + System.getProperty("os.name"), "");
			if (!(bool = x()))
				bool = y();
			if (!bool)
				bool = z();
			if (!bool)
				bool = A();
		} else if (System.getProperty("os.name").toLowerCase(Locale.US).startsWith("mac")) {
			bool = B();
		}
		return bool;
	}

	public static String f() {
		Logger.getLogger(C.class.getName()).log(Level.FINE, "start", "");
		if (a != null)
			return a;
		e();
		return null;
	}

	private static String g() throws SocketException {
		ArrayList<String> arrayList = new ArrayList<String>();
		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		while (enumeration.hasMoreElements()) {
			NetworkInterface networkInterface;
			if (!(networkInterface = enumeration.nextElement()).isLoopback() && !networkInterface.isVirtual()
					&& !networkInterface.isPointToPoint() && networkInterface.getHardwareAddress() != null)
				arrayList.add(B.a(networkInterface.getHardwareAddress()));
		}
		if (arrayList.size() > 0)
			Collections.sort(arrayList);
		String str = null;
		StringBuilder stringBuilder = new StringBuilder();
		for (byte b = 0; b < arrayList.size(); b++)
			stringBuilder.append(arrayList.get(b));
		if (stringBuilder.length() > 0)
			str = stringBuilder.toString();
		return str;
	}

	private static String h() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("hdparm -i /dev/sda");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str3 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("Model=") != -1)
					str2 = str1;
				if (str1.indexOf("SerialNo=") != -1) {
					str3 = str1;
					break;
				}
			}
			if (str2 != null && str3 != null) {
				str2 = str2.substring(str2.indexOf("=") + 1, str2.indexOf(","));
				str3 = str3.substring(str3.lastIndexOf("=") + 1, str3.length());
				if (str2.length() > 1 && str3.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str2 + " " + str3).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
		} catch (IOException iOException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG119", iOException);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG120", iOException);
			}
		}
		return null;
	}

	private static String i() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("hdparm -i /dev/hda");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str3 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("Model=") != -1)
					str2 = str1;
				if (str1.indexOf("SerialNo=") != -1) {
					str3 = str1;
					break;
				}
			}
			if (str2 != null && str3 != null) {
				str2 = str2.substring(str2.indexOf("=") + 1, str2.indexOf(","));
				str3 = str3.substring(str3.lastIndexOf("=") + 1, str3.length());
				if (str2.length() > 1 && str3.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str2 + " " + str3).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG121", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG122", iOException);
			}
		}
		return null;
	}

	private static String j() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("ls -l /dev/disk/by-id/ | grep sda");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str3 = null;
			String str4 = null;
			String str5 = null;
			String str6 = null;
			String str7 = null;
			String str8 = null;
			String str9 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("scsi-SATA") != -1 && str1.indexOf("part") == -1) {
					str2 = str1;
					str3 = str1;
					break;
				}
				if (str1.indexOf("scsi") != -1 && str1.indexOf("part") == -1) {
					str4 = str1;
					str5 = str1;
					break;
				}
				if (str1.indexOf("VBOX_HARDDISK") != -1 && str1.indexOf("part") == -1) {
					str6 = str1;
					str7 = str1;
					break;
				}
				if (str1.indexOf("ata-") != -1 && str1.indexOf("part") == -1) {
					str8 = str1;
					str9 = str1;
					break;
				}
			}
			if (str2 != null && str3 != null) {
				str2 = str2.substring(str2.indexOf("_") + 1, str2.lastIndexOf("_"));
				str3 = str3.substring(str3.lastIndexOf("_") + 1, str3.indexOf("->") - 1);
				if (str2.length() > 1 && str3.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str2 + " " + str3).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
			if (str4 != null && str5 != null) {
				str5 = str4 = str4.substring(str4.indexOf("scsi-") + 5, str4.indexOf("->") - 1);
				if (str4.length() > 1 && str5.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str4 + " " + str5).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
			if (str6 != null && str7 != null) {
				str7 = str6 = str6.substring(str6.indexOf("HARDDISK_") + 9, str6.indexOf("->") - 1);
				if (str6.length() > 1 && str7.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str6 + " " + str7).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
			if (str8 != null && str9 != null) {
				str9 = str8 = str8.substring(str8.indexOf("ata-") + 4, str8.indexOf("->") - 1);
				if (str8.length() > 1 && str9.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str8 + " " + str9).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG123", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG124", iOException);
			}
		}
		return null;
	}

	private static String k() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("ls -l /dev/disk/by-id/ | grep c0d0");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str3 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("cciss") != -1 && str1.indexOf("part") == -1) {
					str2 = str1;
					str3 = str1;
					break;
				}
			}
			object.close();
			if (str2 != null && str3 != null) {
				str3 = str2 = str2.substring(str2.indexOf("cciss-") + 6, str2.indexOf("->") - 1);
				if (str2.length() > 1 && str3.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str2 + " " + str3).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG125", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG126", iOException);
			}
		}
		return null;
	}

	private static String l() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("ls -l /dev/disk/by-id/ | grep mmc");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str3 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("mmc") != -1 && str1.indexOf("part") == -1) {
					str2 = str1;
					str3 = str1;
					break;
				}
			}
			if (str2 != null && str3 != null) {
				str2 = str2.substring(str2.indexOf("-") + 1, str2.lastIndexOf("_"));
				str3 = str3.substring(str3.lastIndexOf("_") + 1, str3.indexOf("->") - 1);
				if (str2.length() > 1 && str3.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str2 + " " + str3).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG123", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG124", iOException);
			}
		}
		return null;
	}

	private static String m() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("ls -l /dev/disk/by-id/ | grep nvme0n1");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str3 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (!str1.startsWith("nvme-eui") && str1.indexOf("nvme0n1") != -1 && str1.startsWith("nvme")) {
					str2 = str1;
					str3 = str1;
					break;
				}
			}
			if (str2 != null && str3 != null) {
				str2 = str2.substring(str2.indexOf("-") + 1, str2.lastIndexOf("_"));
				str3 = str3.substring(str3.lastIndexOf("_") + 1, str3.indexOf("->") - 1);
				if (str2.length() > 1 && str3.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str2 + " " + str3).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG123", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG124", iOException);
			}
		}
		return null;
	}

	private static String n() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime()
					.exec("system_profiler SPSerialATADataType SPSASDataType SPParallelSCSIDataType");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str3 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("Model:") != -1)
					str2 = str1;
				if (str1.indexOf("Serial Number:") != -1) {
					str3 = str1;
					break;
				}
			}
			if (str2 != null && str3 != null) {
				str2 = str2.substring(str2.indexOf(":") + 2, str2.length());
				str3 = str3.substring(str2.indexOf(":") + 2, str3.length());
				if (str2.length() > 1 && str3.length() > 1) {
					UUID uUID = UUID.nameUUIDFromBytes((str2 + " " + str3).getBytes("UTF-8"));
					return "4" + uUID.toString();
				}
			}
		} catch (IOException iOException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG127", iOException);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG128", iOException);
			}
		}
		return null;
	}

	private static String o() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec(
					"ls -l /dev/disk/by-uuid/ | grep sda1;ls -l /dev/disk/by-uuid/ | grep hda1;ls -l /dev/disk/by-uuid/ | grep xvda1");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("sda1") != -1 || str1.indexOf("hda1") != -1 || (str1.indexOf("xvda1") != -1
						&& str1.substring(str1.indexOf("->") - 37, str1.indexOf("->") - 1).length() > 30))
					str2 = str1;
			}
			if (str2 != null && str2.length() > 40
					&& (str2 = str2.substring(str2.indexOf("->") - 37, str2.indexOf("->") - 1)).length() > 1) {
				if (str2.indexOf(":") != -1)
					str2 = str2.substring(str2.length() - 16, str2.length());
				if (str2.indexOf(":") != -1)
					str2 = str2.substring(str2.length() - 9, str2.length());
				UUID uUID = UUID.nameUUIDFromBytes(str2.getBytes("UTF-8"));
				return "3" + uUID.toString();
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG129", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG130", iOException);
			}
		}
		return null;
	}

	private static String p() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime()
					.exec("ls -l /dev/disk/by-uuid/ | grep c0d0p1;ls -l /dev/disk/by-uuid/ | grep vda1");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("c0d0p1") != -1 || (str1.indexOf("vda1") != -1
						&& str1.substring(str1.indexOf("->") - 37, str1.indexOf("->") - 1).length() > 30))
					str2 = str1;
			}
			if (str2 != null && str2.length() > 40
					&& (str2 = str2.substring(str2.indexOf("->") - 37, str2.indexOf("->") - 1)).length() > 1) {
				UUID uUID = UUID.nameUUIDFromBytes(str2.getBytes("UTF-8"));
				return "3" + uUID.toString();
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG131", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG132", iOException);
			}
		}
		return null;
	}

	private static String q() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime()
					.exec("ls -l /dev/disk/by-uuid/ | grep mmcblk0p1;ls -l /dev/disk/by-uuid/ | grep xvda2");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("mmcblk0p1") != -1 || (str1.indexOf("xvda2") != -1
						&& str1.substring(str1.indexOf("->") - 37, str1.indexOf("->") - 1).length() > 30))
					str2 = str1;
			}
			if (str2 != null && str2.length() > 40
					&& (str2 = str2.substring(str2.indexOf("->") - 37, str2.indexOf("->") - 1)).length() > 1) {
				if (str2.indexOf(":") != -1)
					str2 = str2.substring(str2.length() - 16, str2.length());
				UUID uUID = UUID.nameUUIDFromBytes(str2.getBytes("UTF-8"));
				return "3" + uUID.toString();
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG129", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG130", iOException);
			}
		}
		return null;
	}

	private static String r() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("ls -l /dev/disk/by-uuid/ | grep vda2");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("vda2") != -1
						&& str1.substring(str1.indexOf("->") - 37, str1.indexOf("->") - 1).length() > 30)
					str2 = str1;
			}
			if (str2 != null && str2.length() > 40
					&& (str2 = str2.substring(str2.indexOf("->") - 37, str2.indexOf("->") - 1)).length() > 1) {
				if (str2.indexOf(":") != -1)
					str2 = str2.substring(str2.length() - 16, str2.length());
				UUID uUID = UUID.nameUUIDFromBytes(str2.getBytes("UTF-8"));
				return "3" + uUID.toString();
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG129", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG130", iOException);
			}
		}
		return null;
	}

	private static String s() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("ls -l /dev/disk/by-uuid/ | grep sda2");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("sda2") != -1
						&& str1.substring(str1.indexOf("->") - 37, str1.indexOf("->") - 1).length() > 30)
					str2 = str1;
			}
			if (str2 != null && str2.length() > 40
					&& (str2 = str2.substring(str2.indexOf("->") - 37, str2.indexOf("->") - 1)).length() > 1) {
				if (str2.indexOf(":") != -1)
					str2 = str2.substring(str2.length() - 16, str2.length());
				UUID uUID = UUID.nameUUIDFromBytes(str2.getBytes("UTF-8"));
				return "3" + uUID.toString();
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG129", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG130", iOException);
			}
		}
		return null;
	}

	private static String t() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("ls -l /dev/disk/by-uuid/ | grep dm-1");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("dm-1") != -1
						&& str1.substring(str1.indexOf("->") - 37, str1.indexOf("->") - 1).length() > 30)
					str2 = str1;
			}
			if (str2 != null && str2.length() > 40
					&& (str2 = str2.substring(str2.indexOf("->") - 37, str2.indexOf("->") - 1)).length() > 1) {
				if (str2.indexOf(":") != -1)
					str2 = str2.substring(str2.length() - 16, str2.length());
				UUID uUID = UUID.nameUUIDFromBytes(str2.getBytes("UTF-8"));
				return "3" + uUID.toString();
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG129", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG130", iOException);
			}
		}
		return null;
	}

	private static String u() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("ls -l /dev/disk/by-uuid/ | grep dm-0");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("dm-0") != -1
						&& str1.substring(str1.indexOf("->") - 37, str1.indexOf("->") - 1).length() > 30)
					str2 = str1;
			}
			if (str2 != null && str2.length() > 40
					&& (str2 = str2.substring(str2.indexOf("->") - 37, str2.indexOf("->") - 1)).length() > 1) {
				if (str2.indexOf(":") != -1)
					str2 = str2.substring(str2.length() - 16, str2.length());
				UUID uUID = UUID.nameUUIDFromBytes(str2.getBytes("UTF-8"));
				return "3" + uUID.toString();
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG129", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG130", iOException);
			}
		}
		return null;
	}

	private static String v() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime()
					.exec("ls -l /dev/disk/by-uuid/ | grep nvme0n1p1;ls -l /dev/disk/by-uuid/ | grep nvme0n1p2");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("nvme0n1p") != -1
						&& str1.substring(str1.indexOf("->") - 37, str1.indexOf("->") - 1).length() > 30)
					str2 = str1;
			}
			if (str2 != null && str2.length() > 40
					&& (str2 = str2.substring(str2.indexOf("->") - 37, str2.indexOf("->") - 1)).length() > 1) {
				if (str2.indexOf(":") != -1)
					str2 = str2.substring(str2.length() - 16, str2.length());
				UUID uUID = UUID.nameUUIDFromBytes(str2.getBytes("UTF-8"));
				return "3" + uUID.toString();
			}
		} catch (Exception exception) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG129", exception);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG130", iOException);
			}
		}
		return null;
	}

	private static String w() {
		BufferedReader object = null;
		try {
			Process process = Runtime.getRuntime().exec("diskutil info /");
			object = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = object.readLine()) != null) {
				if (str1.indexOf("Volume UUID:") != -1) {
					str2 = str1;
					break;
				}
			}
			if (str2 != null && (str2 = str2.substring(str2.length() - 36, str2.length())).length() > 1) {
				UUID uUID = UUID.nameUUIDFromBytes(str2.getBytes("UTF-8"));
				return "3" + uUID.toString();
			}
		} catch (IOException iOException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG133", iOException);
		} finally {
			try {
				if (object != null)
					object.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG134", iOException);
			}
		}
		return null;
	}

	private static boolean x() {
		boolean bool = false;
		String object = null;
		BufferedReader bufferedReader = null;
		try {
			Process process = Runtime.getRuntime().exec("dmesg |grep -i virtual");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				if (str.indexOf("Hyper-V") != -1) {
					bool = true;
					object = "Hyper-V";
					continue;
				}
				if (str.indexOf("Detected virtualization 'microsoft'") != -1) {
					bool = true;
					object = "Hyper-V";
					continue;
				}
				if (str.indexOf("Microsoft Vmbus") != -1) {
					bool = true;
					object = "Hyper-V";
					continue;
				}
				if (str.indexOf("innotek GmbH VirtualBox") != -1) {
					bool = true;
					object = "VirtualBox";
					continue;
				}
				if (str.indexOf("Detected virtualization 'oracle'") != -1) {
					bool = true;
					object = "VirtualBox";
					continue;
				}
				if (str.indexOf("VirtualBox") != -1) {
					bool = true;
					object = "VirtualBox";
					continue;
				}
				if (str.indexOf("VMware Virtual Platform") != -1) {
					bool = true;
					object = "VMware";
					continue;
				}
				if (str.indexOf("VMware Virtual") != -1) {
					bool = true;
					object = "VMware";
					continue;
				}
				if (str.indexOf("VMware") != -1) {
					bool = true;
					object = "VMware";
					continue;
				}
				if (str.indexOf("QEMU") != -1) {
					bool = true;
					object = "QEMU";
					continue;
				}
				if (str.indexOf("paravirtualized kernel on KVM") != -1) {
					bool = true;
					object = "QEMU";
					continue;
				}
				if (str.indexOf("paravirtualized kernel on Xen") != -1) {
					bool = true;
					object = "XEN";
				}
			}
		} catch (IOException iOException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG135", iOException);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException object1) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG136", object1);
			}
		}
		if (bool)
			a = object;
		return bool;
	}

	private static boolean y() {
		boolean bool = false;
		BufferedReader bufferedReader = null;
		try {
			Process process = Runtime.getRuntime().exec("ls -l /dev/disk/by-id/");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				if (str.indexOf("Virtual") != -1) {
					bool = true;
					a = "Hyper-V";
					continue;
				}
				if (str.indexOf("VBOX") != -1) {
					bool = true;
					a = "VirtualBox";
					continue;
				}
				if (str.indexOf("VMware") != -1) {
					bool = true;
					a = "VMware";
					continue;
				}
				if (str.indexOf("QEMU") != -1) {
					bool = true;
					a = "QEMU";
				}
			}
		} catch (IOException iOException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG137", iOException);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG138", iOException);
			}
		}
		return bool;
	}

	private static boolean z() {
		boolean bool = false;
		BufferedReader bufferedReader = null;
		try {
			Process process = Runtime.getRuntime()
					.exec("ls -l /dev/disk/by-uuid/ | grep xvda1;ls -l /dev/disk/by-uuid/ | grep vda1");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				if (str.indexOf("xvda1") != -1 || str.indexOf("vda1") != -1) {
					bool = true;
					a = "Xen";
				}
			}
		} catch (IOException iOException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG139", iOException);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG140", iOException);
			}
		}
		return bool;
	}

	private static boolean A() {
		boolean bool = false;
		BufferedReader bufferedReader = null;
		try {
			Process process = Runtime.getRuntime().exec("df | grep vzfs");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				if (str.indexOf("vzfs") != -1) {
					bool = true;
					a = "OpenVZ";
				}
			}
		} catch (IOException iOException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG139", iOException);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG140", iOException);
			}
		}
		return bool;
	}

	private static boolean B() {
		boolean bool = false;
		BufferedReader bufferedReader = null;
		try {
			Process process = Runtime.getRuntime()
					.exec("system_profiler SPSerialATADataType SPSASDataType SPParallelSCSIDataType");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
			String str2 = null;
			String str1;
			while ((str1 = bufferedReader.readLine()) != null) {
				if (str1.indexOf("Model:") != -1) {
					str2 = str1;
					break;
				}
			}
			if (str2 != null)
				if (str1.indexOf("Virtual") != -1) {
					bool = true;
					a = "Hyper-V";
				} else if (str1.indexOf("VBOX") != -1) {
					bool = true;
					a = "VirtualBox";
				} else if (str1.indexOf("VMware") != -1) {
					bool = true;
					a = "VMware";
				} else if (str1.indexOf("QEMU") != -1) {
					bool = true;
					a = "QEMU";
				}
		} catch (IOException iOException) {
			Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG141", iOException);
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException iOException) {
				Logger.getLogger(C.class.getName()).log(Level.FINE, "MSG142", iOException);
			}
		}
		return bool;
	}
}
