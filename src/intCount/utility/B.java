package intCount.utility;

public final class B {
	
  private static final char[] a = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  public static String a(byte[] paramArrayOfbyte) {
    int i;
    char[] arrayOfChar = new char[(i = paramArrayOfbyte.length) << 1];
    
    for (byte b1 = 0, b2 = 0; b1 < i; b1++) {
      arrayOfChar[b2++] = a[(0xF0 & paramArrayOfbyte[b1]) >>> 4];
      arrayOfChar[b2++] = a[0xF & paramArrayOfbyte[b1]];
    } 
    return new String(arrayOfChar);
  }
}
