package secure;

import java.security.MessageDigest;

public class Encryption {
	public static String encrypt(String pwd) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(pwd.getBytes("UTF-8"));
		byte[] bytes = md.digest(pwd.getBytes("UTF-8"));
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < bytes.length; i++) {
	    		sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    String signature = sb.toString();
	    return signature;
	}
}
