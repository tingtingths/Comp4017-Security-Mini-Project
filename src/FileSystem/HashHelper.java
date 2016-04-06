package FileSystem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;

/**
 * Created by ting on 2016/2/16.
 */
public class HashHelper {
	public static String md5(File f) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		return hash(md, new FileInputStream(f));
	}

	public static String md5(byte[] b) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		return hash(md, new ByteArrayInputStream(b));
	}

	public static String sha1(File f) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		return hash(md, new FileInputStream(f));
	}

	public static String sha1(byte[] b) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		return hash(md, new ByteArrayInputStream(b));
	}

	public static String sha256(File f) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return hash(md, new FileInputStream(f));
	}

	public static String sha256(byte[] b) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return hash(md, new ByteArrayInputStream(b));
	}

	private static String hash(MessageDigest md, InputStream is) throws Exception {
		DigestInputStream dis = new DigestInputStream(is, md);

		byte[] buf = new byte[1024 * 32]; // 32 KB buffer
		int k = 0;
		while ((k = dis.read(buf)) != -1) {
			md.update(buf, 0, k);
		}

		// to hex
		byte[] b = md.digest();
		BigInteger bi = new BigInteger(1, b);
		return String.format("%0" + (b.length << 1) + "X", bi);
	}
}
