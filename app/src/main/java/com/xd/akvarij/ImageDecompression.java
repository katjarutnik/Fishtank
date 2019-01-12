package com.xd.akvarij;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class ImageDecompression {

    public byte[] readBinaryFile(String filePath) {
        try (InputStream is = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(is)) {
            int length = dis.available();
            byte[] buf = new byte[length];
            dis.readFully(buf);
            return buf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /* TODO
    public Bitmap decompress(byte[] input) {

    }
    */
}