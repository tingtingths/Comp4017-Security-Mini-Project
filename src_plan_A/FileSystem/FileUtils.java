package FileSystem;

import java.io.*;

/**
 * Created by xupengfei on 6/4/2016.
 */
public class FileUtils {
    static byte[] readBytes(File f) throws Exception {
        return readBytes(new FileInputStream(f));
    }

    static byte[] readBytes(InputStream is) throws Exception {
        int k = 0;
        int bufSize = 1024 * 32; // 32 KB buffer
        byte[] retData = new byte[0];
        byte[] buf = new byte[bufSize];

        while ((k = is.read(buf, 0, bufSize)) != -1) {
            // backup return data
            byte[] temp = retData;
            // extend return data array
            retData = new byte[retData.length + k];
            // restore backup data
            System.arraycopy(temp, 0, retData, 0, temp.length);
            // append new data
            System.arraycopy(buf, 0, retData, temp.length, k);
        }
        is.close();

        return retData;
    }

    static void writeBytes(File f, byte[] b) throws Exception {
        writeBytes(new FileOutputStream(f), b, true);
    }

    static void writeBytes(OutputStream os, byte[] b, boolean close) throws Exception {
        os.write(b, 0, b.length);
        if (close) os.close();
    }
}
