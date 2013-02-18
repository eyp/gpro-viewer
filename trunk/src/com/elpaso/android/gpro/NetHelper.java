package com.elpaso.android.gpro;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;

public class NetHelper {
    private static final Logger logger = LoggerFactory.getLogger(UtilHelper.class);
    private static Map<String, Bitmap> images = new HashMap<String, Bitmap>();

    /**
     * Looks for the image in a cache, if it isn't found then reads the image from the URL.<br>
     * After having the image converts it to a {@link Bitmap} and returns it.
     * 
     * @return a {@link Bitmap} or null if the image couldn't be loaded.
     */
    public static Bitmap loadImage(String server, String filename) throws IOException {
        Bitmap imageBitmap = null;
        if (images.containsKey(filename)) {
            if (logger.isDebugEnabled()) {
                logger.info("Image {} found in cache", filename);
            }
            return images.get(filename);
        } else {
            // Searchs the file in the SD card, if it isn't there, then it loads the file from GPRO web site
            imageBitmap = IOHelper.loadImageFromExternalStorage(filename);
            if (imageBitmap == null) {
                // Fetch the image in the GPRO site
                imageBitmap = loadRemoteImage(new URL(server + filename));
                // Store the image in the SD card
                if (logger.isDebugEnabled()) {
                    logger.info("Saving image {} in external storage", filename);
                }
                IOHelper.storeImageInExternalStorage(filename, imageBitmap);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.info("Image {} found in external storage", filename);
                }
            }
            // Store in the cache
            images.put(filename, imageBitmap);
        }
        return imageBitmap;
    }
    
    /**
     * Reads an image from an URL. It converts the image to a {@link Bitmap} and returns it.
     * 
     * @param imageUrl An URL that points to an image.
     * @return a {@link Bitmap} or null if the image couldn't be loaded.
     */
    private static Bitmap loadRemoteImage(URL imageUrl) {
        Bitmap image = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
        	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);             
        	conn = (HttpURLConnection) imageUrl.openConnection();
            logger.info("URL: " + imageUrl.getPath());
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            image = BitmapFactory.decodeStream(is);
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
        return image;
    }
}
