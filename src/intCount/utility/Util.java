package intCount.utility;

import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import intCount.xml.Dom;

public class Util {
	public static int count;
	public static long validityEpochTime = 10000000;
	public static long experitionEpochTime;

	public long countExperitionDate() {

		Dom dom = new Dom();

		dom.readDatFile();

		// count = CountPersistance.getData();
		if (dom.getCount() == 0) {
			experitionEpochTime = getTheEpoch() + validityEpochTime;
			dom.setExperitionEpochTime(experitionEpochTime);
			dom.write();
		} else {
			System.out.println("");
		}
		return experitionEpochTime;

	}

	public static long getTheEpoch() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		long secondsSinceEpoch = calendar.getTimeInMillis() / 1000L;
		// SimpleDateFormat simpleDateFormat =
		new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		// System.out.println(calendar.getTime());

		// MutableDateTime epoch = new MutableDateTime();
		// epoch.setDate(0); // Set to Epoch time
		// DateTime now = new DateTime();

		// Seconds seconds = Seconds.secondsBetween(epoch, now);
		// Minutes minutes = Minutes.minutesBetween(epoch, now);
		// Days days = Days.daysBetween(epoch, now);

		// Weeks weeks = Weeks.weeksBetween(epoch, now);
		// System.out.println(days.getDays());
		return secondsSinceEpoch;

	}

	public boolean count() throws SQLException {

		Dom dom = new Dom();
		dom.readDatFile();
		boolean b = true;

		if (Long.valueOf(dom.getExperitionEpochTime()) <= getTheEpoch()) {
			System.out.println("Please Contact intcount");
			b = false;
		}
		return b;
	}

	// Needs a network to fire the MAC Address, Use Serial Number instead

	public String[] getMacAddress() {
		String[] str = new String[4];
		// String EthAddress;
		String hostname;
		String UUID;
		String hddSerialNumber;
		@SuppressWarnings("unused")
		String str1 = "";
		hostname = C.a();
		// EthAddress = C.b();
		UUID = C.c();
		hddSerialNumber = C.d();
		if (C.e())
			str1 = "Virtualized " + System.getProperty("os.name") + " Operating System on " + C.f();
		// str[0] = EthAddress;
		str[0] = hostname;
		str[1] = UUID;
		str[2] = hddSerialNumber;
		return str;

	}

	public static int getCount() {
		return count;
	}

}
