package com.elpaso.android.gpro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;

public class IOHelper {
    public static Logger logger = LoggerFactory.getLogger("IOHelper");
    private static final String STORAGE_ROOT_DIR = "/Android/data/com.elpaso.android.gpro/files/";
    
    /**
     * Checks if the SD card is mounted and writable.
     * 
     * @return <true> if the SD card is mounted and writable, false otherwise.
     */
    public static boolean checkSDAvailability() {
        boolean externalStorageAvailable = false;
        boolean externalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            externalStorageAvailable = true;
        }
        return (externalStorageAvailable && externalStorageWriteable);
    }
    
    /**
     * Stores an image in the external storage (normally a SD card).
     * 
     * @param filename File which references the image.
     * @param image Bitmap to save.
     * @throws IOException if an error happened writing the file.
     */
    public static void storeImageInExternalStorage(String filename, Bitmap image) throws IOException {
        if (!IOHelper.checkSDAvailability()) {
            return;
        }
        File sdDir = Environment.getExternalStorageDirectory();
        File rootDir = new File(sdDir.getAbsolutePath() + STORAGE_ROOT_DIR);
        // If the root dir doesn't exist then creates it
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        
        FileOutputStream fos = null;
        try {
            // Filename comes with a path of directories so we need to make them before
            File targetFile = new File(rootDir.getAbsolutePath() + "/" + filename);
            targetFile.getParentFile().mkdirs();
            // Save the file
            fos = new FileOutputStream(targetFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } finally {
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        }
    }
    
    /**
     * Loads an image from the external storage (normally a SD card).
     * 
     * @param filename File which references the image.
     * @return a Bitmap with the image or null if it doesn't exist in the card.
     * @throws IOException if an error happened reading the file.
     */
    public static Bitmap loadImageFromExternalStorage(Resources res, String filename) throws IOException {
        if (!IOHelper.checkSDAvailability()) {
            return null;
        }
        File sdDir = Environment.getExternalStorageDirectory();
        File rootDir = new File(sdDir.getAbsolutePath() + STORAGE_ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
            return null;
        }
        File imageFile = new File(rootDir.getAbsolutePath() + "/" + filename);
        if (imageFile.exists()) {
            BitmapDrawable bd = new BitmapDrawable(res, imageFile.getAbsolutePath());
            return bd.getBitmap();
        } else {
            return null;
        }
    }
}
