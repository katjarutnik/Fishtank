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
        // blocks
        int blocksN = (width / 8) * (height / 8);
        int[][] R = new int[blocksN][64];
        int[][] G = new int[blocksN][64];
        int[][] B = new int[blocksN][64];
        // encoded R B G
        short temp;
        byte currentBit = 0; // 0 - 7
        int currentBlock = 0; // 0 - blocksN
        int i = 4; // 4 - input.length
        /*
        while (i < input.length) { // idi skoz vse tri RBG
            while (currentBlock < blocksN) { // idi skoz posameznega
                // if currentbit pos
                // temp = (short) (((input[i] & 0xFF) << 8) | (input[i + 1] & 0xFF));

                // predznak DC 1 BIT

                // DC 12 BITS

                // a type || b type || c type
                // a) MAX 1 + 6 + 4 + 1 + 13
                // b) MAX 1 + 6
                // c) MAX 1 + 4 + 1 + 13
            }
        }
        */

        // image
        Bitmap pic = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        return pic;
    }

    // TODO
    private static int[] RLD() {
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