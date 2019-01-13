package com.xd.akvarij;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.DataInputStream;
import java.io.InputStream;

public class DecompressImage {

    public static byte[] readBinaryFile(Context c, int fileId) {
        try {
            InputStream is = c.getResources().openRawResource(fileId);
            DataInputStream dis = new DataInputStream(is);
            int length = dis.available();
            byte[] buf = new byte[length];
            dis.readFully(buf);
            return buf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    // TODO
    public static Bitmap decompress(byte[] input) {
        // width and height of image
        short width = (short) (((input[1] & 0xFF) << 8) | (input[0] & 0xFF));
        short height = (short) (((input[3] & 0xFF) << 8) | (input[2] & 0xFF));
        // image
        Bitmap pic = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // blocks
        StringBuilder bits = new StringBuilder();
        for (int i = 4; i < input.length; i++) {
            bits.append(String.format("%8s", Integer.toBinaryString(input[i] & 0xFF))
                    .replace(' ', '0'));
        }
        int[] block;
        int[] blockCikCak;
        double[] blockIDCT = new double[64];
        byte[] output = new byte[64];

        while (bits.length() > 0) {
            block = RLD(bits);
            blockCikCak = cikCak(block);
            blockIDCT = IDCT(blockCikCak);
            // ...
        }
        // ...
        return pic;
    }

    // TODO
    private static int[] RLD(StringBuilder bits) {
        return new int[64];
    }

    // TODO
    private static int[] cikCak(int[] input) {
        return new int[64];
    }

    // TODO
    private static double[] IDCT(int[] input) {
        return new double[64];
    }
}