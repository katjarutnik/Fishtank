package com.xd.akvarij;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

public class ImageManipulator {

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

    public static Bitmap setTransparentBackground(Bitmap source) {
        source.setHasAlpha(true);
        for(int i = 0; i < source.getWidth(); i++) {
            for(int j = 0; j < source.getHeight(); j++) {
                if (source.getPixel(i, j) == Color.rgb(255, 255, 255)) {
                    source.setPixel(i, j, Color.TRANSPARENT);
                }
            }
        }
        return source;
    }
}
