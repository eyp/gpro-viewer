/*
 * Copyright 2011 Eduardo Yáñez Parareda
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elpaso.android.gpro;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class UtilHelper {
    private static final Logger logger = LoggerFactory.getLogger(UtilHelper.class);
    
    private static Map<String, Bitmap> images = new HashMap<String, Bitmap>();
    private static Map<String, Bitmap> rotatedImages = new HashMap<String, Bitmap>();
    
    /**
     * Gets the widget identifier which has called the activity.
     * 
     * @return Widget identifier or AppWidgetManager.INVALID_APPWIDGET_ID whether it isn't found.
     */
    public static int getWidgetId(Intent intent) {
        Bundle extras = intent.getExtras();
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        return appWidgetId;
    }

    /**
     * Looks for the image in a cache, if it isn't found then reads the image from the URL.<br>
     * After having the image converts it to a {@link Bitmap} and returns it.
     * 
     * @param imageUrl An URL that points to an image.
     * @return a {@link Bitmap} or null if the image couldn't be loaded.
     */
    public static Bitmap loadImage(URL imageUrl) {
        Bitmap imageBitmap = null;
        if (images.containsKey(imageUrl.getPath())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Image {} found in cache", imageUrl.getPath());
            }
            return images.get(imageUrl.getPath());
        } else {
            imageBitmap = loadRemoteImage(imageUrl);
            images.put(imageUrl.getPath(), imageBitmap);
        }
        return imageBitmap;
    }
    
    
    /**
     * Looks for the image in a cache, if it isn't found then reads the image from the URL.<br>
     * After having the image converts it to a {@link Bitmap} ans rotates it 90 degrees, then returns it.
     * 
     * @param imageUrl An URL that points to an image.
     * @return a rotated {@link Bitmap} or null if the image couldn't be loaded.
     */
    public static Bitmap loadRotatedImage(URL imageUrl) {
        Bitmap rotatedBitmap = null;
        if (rotatedImages.containsKey(imageUrl.getPath())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Image {} found in cache", imageUrl.getPath());
            }
            return rotatedImages.get(imageUrl.getPath());
        } else {
            Bitmap imageBitmap = loadRemoteImage(imageUrl);
            rotatedBitmap = UIHelper.rotateBitmap(imageBitmap, 90);
            rotatedImages.put(imageUrl.getPath(), rotatedBitmap);
        }
        return rotatedBitmap;
    }

    /**
     * Reads an image from an URL. It converts the image to a {@link Bitmap} and returns it.
     * 
     * @param imageUrl An URL that points to an image.
     * @return a {@link Bitmap} or null if the image couldn't be loaded.
     */
    private static Bitmap loadRemoteImage(URL imageUrl) {
        Bitmap imageBitmap = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            imageBitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            logger.error("Error reading image from " + imageUrl.getPath(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("Error closing the InputStream used for reading the image from " + imageUrl.getPath(), e);
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return imageBitmap;
    }
}
