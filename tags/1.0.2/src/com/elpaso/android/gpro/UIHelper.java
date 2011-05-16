package com.elpaso.android.gpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class UIHelper {
    
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
}
