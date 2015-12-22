package com.example.apps;

import java.util.Arrays;
import java.util.List;

import com.example.apps.JnaMain.Kernel32.SYSTEMTIME;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public class JnaMain {

	public interface CLibrary extends Library {
		CLibrary INSTANCE = (CLibrary) Native.loadLibrary(
				(Platform.isWindows() ? "msvcrt" : "c"), CLibrary.class);

		void printf(String format, Object... args);
	}

	public interface Kernel32 extends StdCallLibrary {
		// Method declarations, constant and structure definitions go here
		Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32",
				Kernel32.class);
		// Optional: wraps every call to the native library in a
		// synchronized block, limiting native calls to one at a time
		Kernel32 SYNC_INSTANCE = (Kernel32) Native
				.synchronizedLibrary(INSTANCE);

		public static class SYSTEMTIME extends Structure {
			public short wYear;
			public short wMonth;
			public short wDayOfWeek;
			public short wDay;
			public short wHour;
			public short wMinute;
			public short wSecond;
			public short wMilliseconds;

			protected List<?> getFieldOrder() {
				return Arrays.asList(new String[] { "wYear", "wMonth",
						"wDayOfWeek", "wDay", "wHour", "wMinute", "wSecond",
						"wMilliseconds", });
			}
		}

		void GetSystemTime(SYSTEMTIME result);
	}

	public static void main(String[] args) {
		CLibrary.INSTANCE.printf("Hello, World\n");
		for (int i = 0; i < args.length; i++) {
			CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
		}

		Kernel32 lib = Kernel32.INSTANCE;
		SYSTEMTIME time = new SYSTEMTIME();
		lib.GetSystemTime(time);
		System.out.println("Today's integer value is " + time.wDay);
	}

}
