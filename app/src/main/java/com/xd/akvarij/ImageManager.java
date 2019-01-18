package com.xd.akvarij;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

import java.io.DataInputStream;
import java.io.InputStream;

public class ImageManager {

    public static Bitmap resize(Bitmap source, int newWidth, int newHeight) {
        int width = source.getWidth();
        int height = source.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                source, 0, 0, width, height, matrix, false);
        if (source != null && !source.isRecycled()) {
            source.recycle();
            source = null;
        }
        return resizedBitmap;
    }

    public static Bitmap flipHorizontally(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, source.getWidth()/2f, source.getHeight());
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap flipVertically(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postScale(1, -1, source.getWidth(), source.getHeight()/2f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap setWhitePixelsToTransparent(Bitmap source) {
        source.setHasAlpha(true);
        for (int i = 0; i < source.getWidth(); i++) {
            for (int j = 0; j < source.getHeight(); j++) {
                if (source.getPixel(i, j) == Color.rgb(255, 255, 255)) {
                    source.setPixel(i, j, Color.TRANSPARENT);
                }
            }
        }
        return source;
    }

    // TODO
    public static Bitmap decompress(Context context, int fileId) {
        byte[] binFile = readBinFile(context, fileId);
        if (binFile.length != 0) {
            // width and height of image
            short width = (short) (((binFile[1] & 0xFF) << 8) | (binFile[0] & 0xFF));
            short height = (short) (((binFile[3] & 0xFF) << 8) | (binFile[2] & 0xFF));
            // blocks
            int blocksN = (width / 8) * (height / 8);
            int[][] R = new int[blocksN][64];
            int[][] G = new int[blocksN][64];
            int[][] B = new int[blocksN][64];
            // decode R B G
            int binFileIterator = 4;
            int currentBlock = 0; // 0 < blocksN
            int currentBit = 0; // 0 < 7
            while (currentBlock < blocksN) {
                R[currentBlock] = RLD(binFile, binFileIterator, currentBit);
                G[currentBlock] = RLD(binFile, binFileIterator, currentBit);
                B[currentBlock] = RLD(binFile, binFileIterator, currentBit);
                currentBlock++;
            }
            // ...
            Bitmap pic = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            return pic;
        } else {
            return Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
        }
    }

    private static byte[] readBinFile(Context context, int fileId) {
        try {
            InputStream is = context.getResources().openRawResource(fileId);
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

    // example: bytes = 1, start = 2, range = 3: 00000000 -> 00111000
    private static int toggleBitsInRange(int start, int range, int bytes) {
        int n = 0;
        int mirror;
        if (bytes == 1) {
            mirror = 7 - start;
        } else if (bytes == 2) {
            mirror = 15 - start;
        } else {
            mirror = 31 - start;
        }
        for (int i = mirror; i > (mirror-range); i--) {
            n |= (1 << i);
        }
        return n;
    }

    // current + following byte
    private static int grabTwoBytes(byte[] input, int index) {
        return (((input[index] & 0xFF) << 8) | (input[index+1] & 0xFF));
    }

    // current + 3 following bytes
    private static int grabFourBytes(byte[] input, int index) {
        return (((input[index] & 0xFF) << 24) | ((input[index+1] & 0xFF) << 16) |
                ((input[index+2] & 0xFF) << 8) | (input[index+3] & 0xFF));
    }

    private static int advanceCurrentBit(int currentBit, int range) {
        for (int i = 0; i < range; i++) {
            currentBit++;
            if (currentBit == 8) {
                currentBit = 0;
            }
        }
        return currentBit;
    }

    private static int advanceCurrentByte(int currentByte, int currentBit, int range) {
        if (currentBit+range < 16) {
            return currentByte++;
        } else if (currentBit+range < 24) {
            return currentByte += 2;
        } else if (currentBit+range < 32) {
            return currentByte += 3;
        } else {
            return currentByte;
        }
    }

    // TODO
    private static int[] RLD(byte[] input, int inputIterator, int currentBit) {
        int[] output = new int[64];
        // if prefix == 1 then -DC else DC
        if ((input[inputIterator] & toggleBitsInRange(currentBit, 1, 1)) == 1) {
            currentBit = advanceCurrentBit(currentBit, 1);
            output[0] = -(grabFourBytes(input, inputIterator) & toggleBitsInRange(currentBit, 12, 4));
            currentBit = advanceCurrentBit(currentBit, 12);
            inputIterator = advanceCurrentByte(inputIterator, currentBit, 12);
        } else {
            currentBit = advanceCurrentBit(currentBit, 1);
            output[0] = grabFourBytes(input, inputIterator) & toggleBitsInRange(currentBit, 12, 4);
            currentBit = advanceCurrentBit(currentBit, 12);
            inputIterator = advanceCurrentByte(inputIterator, currentBit, 12);
        }
        // if prefix == 0 then 0s else AC
        if ((input[inputIterator] & toggleBitsInRange(currentBit, 1, 1)) == 1) {

        } else {

        }
        return output;
    }

    // 64 -> 8x8
    private static int[][] cikCak(int[][] input, int current) {
        int[][] output = new int[8][8];
        output[0][0] = input[current][0];
        output[0][1] = input[current][1];
        output[1][0] = input[current][2];
        output[2][0] = input[current][3];
        output[1][1] = input[current][4];
        output[0][2] = input[current][5];
        output[0][3] = input[current][6];
        output[1][2] = input[current][7];
        output[2][1] = input[current][8];
        output[3][0] = input[current][9];
        output[4][0] = input[current][10];
        output[3][1] = input[current][11];
        output[2][2] = input[current][12];
        output[1][3] = input[current][13];
        output[0][4] = input[current][14];
        output[0][5] = input[current][15];
        output[1][4] = input[current][16];
        output[2][3] = input[current][17];
        output[3][2] = input[current][18];
        output[4][1] = input[current][19];
        output[5][0] = input[current][20];
        output[6][0] = input[current][21];
        output[5][1] = input[current][22];
        output[4][2] = input[current][23];
        output[3][3] = input[current][24];
        output[2][4] = input[current][25];
        output[1][5] = input[current][26];
        output[0][6] = input[current][27];
        output[0][7] = input[current][28];
        output[1][6] = input[current][29];
        output[2][5] = input[current][30];
        output[3][4] = input[current][31];
        output[4][3] = input[current][32];
        output[5][2] = input[current][33];
        output[6][1] = input[current][34];
        output[7][0] = input[current][35];
        output[7][1] = input[current][36];
        output[6][2] = input[current][37];
        output[5][3] = input[current][38];
        output[4][4] = input[current][39];
        output[3][5] = input[current][40];
        output[2][6] = input[current][41];
        output[1][7] = input[current][42];
        output[2][7] = input[current][43];
        output[3][6] = input[current][44];
        output[4][5] = input[current][45];
        output[5][4] = input[current][46];
        output[6][3] = input[current][47];
        output[7][2] = input[current][48];
        output[7][3] = input[current][49];
        output[6][4] = input[current][50];
        output[5][5] = input[current][51];
        output[4][6] = input[current][52];
        output[3][7] = input[current][53];
        output[4][7] = input[current][54];
        output[5][6] = input[current][55];
        output[6][5] = input[current][56];
        output[7][4] = input[current][57];
        output[7][5] = input[current][58];
        output[6][6] = input[current][59];
        output[5][7] = input[current][60];
        output[6][7] = input[current][61];
        output[7][6] = input[current][62];
        output[7][7] = input[current][63];
        return output;
    }

    // 8x8 -> 8x8
    private static double[][] IDCT(int[][] F) {
        double[][] f = new double[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int u = 0; u < 8; u++) {
                    for (int v = 0; v < 8; v++) {
                        if (u == 0 && v == 0) {
                            f[x][y] += (1.0/Math.sqrt(2.0))
                                    * (1.0/Math.sqrt(2.0))
                                    * (double)F[u][v]
                                    * Math.cos(((2.0*x+1.0)*u*Math.PI)/16.0)
                                    * Math.cos(((2.0*y+1.0)*v*Math.PI)/16.0);
                        } else if (u == 0 || v == 0) {
                            f[x][y] += (1.0/Math.sqrt(2.0))
                                    * (double)F[u][v]
                                    * Math.cos(((2.0*x+1.0)*u*Math.PI)/16.0)
                                    * Math.cos(((2.0*y+1.0)*v*Math.PI)/16.0);
                        } else {
                            f[x][y] += (double)F[u][v]
                                    * Math.cos(((2.0*x+1.0)*u*Math.PI)/16.0)
                                    * Math.cos(((2.0*y+1.0)*v*Math.PI)/16.0);
                        }
                    }
                }
                f[x][y] *= 1.0/4.0;
            }
        }
        return f;
    }
}
