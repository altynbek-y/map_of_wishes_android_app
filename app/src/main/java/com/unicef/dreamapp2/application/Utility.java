package com.unicef.dreamapp2.application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * @author Iman Augustine
 *
 * Utility.
 *
 * */

public class Utility {

    // Static global variables
    public static final String MESSAGES = "Messages";
    public static final String CHATTER_ID = "chatterId";
    public static final String CHATTER_NAME = "chatterName";
    public static final String CUSTOMER_NAME = "customerName";
    public static final String VOLUNTEER_NAME = "volunteerName";
    public static final String NAME = "name";
    public static final String LIKES = "likes";
    public static final String USERS = "Users";

    // Convert bitmap to base64
    public static String getBase64FromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
    }

    // Convert base65 to bitmap
    public static Bitmap getBitmapFromBase64(String imageStr) {
        byte[] data = Base64.decode(imageStr, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    // Get resized bitmap
    public static Bitmap getResizedBitmap(Bitmap image, int minSize) {
        float width = (float)image.getWidth();
        float height = (float)image.getHeight();

        float bitmapRatio = (float)width / (float)height;
        if (bitmapRatio <= 1) {
            width = minSize;
            height = (int)(width / bitmapRatio);
        } else {
            height = minSize;
            width = (int)(height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, (int)width, (int)height, true);
    }

}
