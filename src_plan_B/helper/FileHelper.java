package helper;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Created by ting on 2016/2/16.
 */
public class FileHelper {

	public static byte[] readBytes(File f) throws Exception {
		return readBytes(new FileInputStream(f));
	}

	public static byte[] readBytes(InputStream is) throws Exception {
		byte[] data = new byte[0];
		int bufSize = 4096;
		byte[] buf = new byte[bufSize]; // 4KB buffer
		int k = 0;

		while ((k = is.read(buf, 0, bufSize)) != -1) {
			// copy data to temp
			byte[] temp = data;
			// extend the data array
			data = new byte[data.length + k];
			// copy back to data array
			System.arraycopy(temp, 0, data, 0, temp.length);
			// append buf to data
			System.arraycopy(buf, 0, data, temp.length, k);
		}
		is.close();

		return data;
	}

	public static void writeBytes(File f, byte[] b) throws IOException {
		writeBytes(new FileOutputStream(f), b, true);
	}

	public static void writeBytes(OutputStream os, byte[] b, boolean close) throws IOException {
		os.write(b, 0, b.length);
		if (close) os.close();
	}

	public static String[] getZipEntriesName(File f) throws IOException {
		ZipFile z = new ZipFile(f);
		Enumeration<? extends ZipEntry> entries = z.entries();
		ArrayList<String> names = new ArrayList<String>();

		int i = 0;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			names.add(entry.getName());
			i++;
		}

		z.close();
		return names.toArray(new String[names.size()]);
	}

	private static ZipEntry getZipEntry(ZipFile z, String entryName) throws IOException {
		Enumeration<? extends ZipEntry> entries = z.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry.getName().equals(entryName)) return entry;
		}

		return null;
	}

	public static byte[] getZipEntryByte(File f, String entryName) throws Exception {
		ZipFile z = new ZipFile(f);
		ZipEntry entry = getZipEntry(z, entryName);
		if (entry == null) throw new EntryNotFoundException("Entry not found");
		byte[] b = readBytes(z.getInputStream(entry));
		z.close();
		return b;
	}

	public static void putZipEntry(File zipFile, File f, String name) throws Exception {
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");

		try (FileSystem fs = getZipFileSystem(zipFile, env)) {
			Path filePath = Paths.get(f.getAbsolutePath());
			Path inZipPath = fs.getPath(name);
			Files.copy(filePath, inZipPath, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public static void removeZipEntry(File zipFile, String name) throws IOException {
		Map<String, String> env = new HashMap<>();
		env.put("create", "false");

		try (FileSystem fs = getZipFileSystem(zipFile, env)) {
			Path inZipPath = fs.getPath(name);
			Files.delete(inZipPath);
		}
	}

	public static boolean createEmptyZip(File f) throws IOException {
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");

		try {
			FileSystem fs = getZipFileSystem(f, env);
			return true;
		} catch (Exception e) {
		}

		return false;
	}

	private static FileSystem getZipFileSystem(File zipFile, Map<String, String> env) throws IOException {
		try {
			return FileSystems.newFileSystem(URI.create("jar:" + zipFile.toURI()), env);
		} catch (FileSystemAlreadyExistsException e) {
			return FileSystems.getFileSystem(URI.create("jar:" + zipFile.toURI()));
		}
	}

	// ------
	static class EntryNotFoundException extends Exception {
		public EntryNotFoundException(String msg) {
			super(msg);
		}
	}
}
