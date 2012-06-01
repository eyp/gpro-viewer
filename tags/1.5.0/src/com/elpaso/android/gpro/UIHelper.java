/*
 * Copyright 2011 Eduardo Yáñez Parareda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elpaso.android.gpro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class UIHelper {
    private static final Logger logger = LoggerFactory.getLogger(UIHelper.class);

    /**
     * Construye un dialogo de 'progreso de acción' con estilo spinner. No se puede cancelar.
     * 
     * @param context Contexto.
     * @param message Mensaje que se quiere mostrar.
     */
    public static ProgressDialog makeProgressDialog(Context context, String message) { 
        ProgressDialog progress = new ProgressDialog(context); 
        progress.setIndeterminate(true); 
        progress.setMessage(message); 
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return progress;
    }

    /**
     * Construye un dialogo de aviso con un botón Ok, y con un mensaje de información.
     * No se puede pulsar el botón 'volver' mientras se muestra este diálogo.
     * 
     * @param context Contexto.
     * @param title Título del diálogo.
     * @param message Mensaje que se quiere mostrar.
     */
    public static Dialog makeAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
        .setTitle(title)
        .setCancelable(false)
        .setNegativeButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
           }
       });
       Dialog dialog = builder.create();
       return dialog;
    }
    
    /**
     * Crea un mensaje de error para mostrar en un diálogo, log, etc. El formato es:<br><br>
     * 
     * Code NUM_ERROR - MENSAJE.<br>
     * NUM_ERROR y MENSAJE ya vienen formateados así en el mensaje de error.<br>
     * 
     * @param context Contexto.
     * @param message Mensaje de error.
     * 
     * @return el mensaje formateado e internacionalizado.
     */
    public static String makeErrorMessage(Context context, String message) {
        return String.format("%s %s", context.getString(R.string.code), message);
    }
    
    /**
     * Rotates a bitmap N degrees.
     * 
     * @param bmp Source bitmap.
     * @param degrees Degrees to rotate the original bitmap.
     * @return A new bitmap rotated N degrees.
     */
    public static Bitmap rotateBitmap(Bitmap bmp, int degrees) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(90);
        // Rotating Bitmap
        if (logger.isDebugEnabled()) {
            logger.debug("Rotating image {} degrees", degrees);
        }
        return Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
    }
}
