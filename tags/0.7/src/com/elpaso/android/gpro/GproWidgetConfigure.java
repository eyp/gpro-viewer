package com.elpaso.android.gpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Configuración del widget cuando se instala.
 * 
 * @author eduardo.yanez
 */
public class GproWidgetConfigure extends Activity {
    static final String TAG = "GproWidgetConfigure";

    private static final String PREFS_NAME = "com.elpaso.android.gpro.GproWidgetProvider";
    private static final String PREF_PREFIX_KEY = "gpro_";
    private static final String PREF_MANAGER_KEY = "manager_name_";
    private static final String PREF_GROUP_TYPE_KEY = "group_type_";
    private static final String PREF_GROUP_NUMBER_KEY = "group_number_";
    private static final String PREF_GROUP_UPDATE_INTERVAL_KEY = "update_interval_";
    private static final int DIALOG_ALERT_NAME_ID = 1;
    private static final int DIALOG_ALERT_GROUP_NUMBER_ID = 2;
    private static final int DIALOG_ALERT_GROUP_NUMBER_ERROR_ID = 3;
    private static final int DIALOG_ALERT_GROUP_TYPE_ERROR_ID = 4;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText managerNameInput;
    private EditText groupNumberInput;
    private Spinner groupTypes;
    //Spinner updateIntervals;
    
    public GproWidgetConfigure() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // Si se pulsa el botón de back, no instalamos el widget
        setResult(RESULT_CANCELED);

        // Mostramos el layout configurado para este componente
        setContentView(R.layout.gpro_configuration);

        // Buscamos los campos del formulario 
        // Campo de texto del nombre del manager
        this.managerNameInput = (EditText) findViewById(R.id.manager_name_input_text);

        // Número de grupo
        this.groupNumberInput = (EditText) findViewById(R.id.group_number_input_text);
        
        // Desplegable con tipos de grupo
        groupTypes = (Spinner) findViewById(R.id.group_types_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.group_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupTypes.setAdapter(adapter);
        
        // Desplegable con los intervalos de actualización
//        updateIntervals = (Spinner) findViewById(R.id.update_intervals_spinner);
//        adapter = ArrayAdapter.createFromResource(this, R.array.update_intervals, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        groupTypes.setAdapter(adapter);
        
        // Asociamos al botón de salvar la acción que debe ejecutar
        findViewById(R.id.save_button).setOnClickListener(saveButtonListener);

        // Busca el identificador del widget del Intent recibido. 
        mAppWidgetId = GproUtils.getWidgetId(this.getIntent());

        // Si el identificador no es válido, terminamos.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.w(TAG, "El identificador del widget no es válido");
            finish();
        }
    }

    
    /**
     * Listener para el botón salvar configuración.
     */
    View.OnClickListener saveButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = GproWidgetConfigure.this;

            // Salvamos todas las preferencias del usuario 
            String managerName = managerNameInput.getText().toString();
            if (managerName.equals("")) {
                showDialog(DIALOG_ALERT_NAME_ID);
                return;
            }
            saveManagerName(context, mAppWidgetId, managerName);

            if (groupTypes.getSelectedItem() == null) {
                showDialog(DIALOG_ALERT_GROUP_TYPE_ERROR_ID);
                return;
            }
            
            String groupType = groupTypes.getSelectedItem().toString();
            String groupNumber = "";
            if (!groupType.equals(context.getString(R.string.elite))) {
                groupNumber = groupNumberInput.getText().toString();
                if (groupNumber.equals("")) {
                    showDialog(DIALOG_ALERT_GROUP_NUMBER_ID);
                    return;
                }
                
                try {
                    Integer.parseInt(groupNumber);
                } catch (NumberFormatException e) {
                    showDialog(DIALOG_ALERT_GROUP_NUMBER_ERROR_ID);
                    return;
                } 
            }
            saveGroupNumber(context, mAppWidgetId, groupNumber);
//            saveUpdateInterval(context, mAppWidgetId, calcUpdateIntervalMillis());

            // Actualizamos la información que se muestra en el widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            GproWidgetProvider.updateWidget(context, appWidgetManager, mAppWidgetId, loadManagerName(context, mAppWidgetId));

            // Devolvemos el control al widget que nos llamó
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

//    private long calcUpdateIntervalMillis() {
//        int pos = updateIntervals.getSelectedItemPosition();
//        long interval = 30 * 60 * 1000;
//        if (pos == 1) {
//            interval = 60 * 60 * 1000;
//        } else if (pos == 1) {
//            interval = 24 * 60 * 60 * 1000;
//        }
//        return interval;
//    }
    
    /**
     * Método para la creación de diálogos relacionados con {@link GproWidgetConfigure}.
     */
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        final Context context = GproWidgetConfigure.this;
        switch(id) {
        case DIALOG_ALERT_NAME_ID:
            dialog = makeAlertDialog(context.getString(R.string.manager_name_mandatory));
            break;
        case DIALOG_ALERT_GROUP_NUMBER_ID:
            dialog = makeAlertDialog(context.getString(R.string.group_number_mandatory));
            break;
        case DIALOG_ALERT_GROUP_NUMBER_ERROR_ID:
            dialog = makeAlertDialog(context.getString(R.string.group_number_is_not_number));
            break;
        case DIALOG_ALERT_GROUP_TYPE_ERROR_ID:
            dialog = makeAlertDialog(context.getString(R.string.group_type_mandatory));
            break;
        default:
            dialog = null;
        }
        return dialog;
    }

    /**
     * Construye un dialogo de aviso con un botón Ok, y con un mensaje de información.
     * No se puede pulsar el botón 'volver' mientras se muestra este diálogo.
     */
    private Dialog makeAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Context context = GproWidgetConfigure.this;
        builder.setMessage(message)
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
     * Guardamos el nombre del manager en las preferencias.
     */
    static void saveManagerName(Context context, int appWidgetId, String name) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + PREF_MANAGER_KEY + appWidgetId, name.trim());
        prefs.commit();
    }

    /**
     * Guardamos el tipo de grupo en las preferencias.
     */
    static void saveGroupType(Context context, int appWidgetId, String groupType) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + PREF_GROUP_TYPE_KEY + appWidgetId, groupType.trim());
        prefs.commit();
    }

    /**
     * Guardamos el número de grupo en las preferencias.
     */
    static void saveGroupNumber(Context context, int appWidgetId, String groupNumber) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + PREF_GROUP_NUMBER_KEY + appWidgetId, groupNumber.trim());
        prefs.commit();
    }

    /**
     * Guardamos el tiempo de actualización en las preferencias.
     */
    static void saveUpdateInterval(Context context, int appWidgetId, long interval) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + PREF_GROUP_NUMBER_KEY + appWidgetId, interval);
        prefs.commit();
    }

    /**
     * Recupera el identificador del grupo, es decir, el tipo de grupo junto con el número, que será usado para
     * recuperar la información de la parrilla de salida o de la clasificación, carrera, etc.
     * 
     * @return GroupType - GroupNumber, o si se trata de Elite, sólo el GroupType.
     */
    static String loadGroupId(Context context, int appWidgetId) {
        String groupType = loadGroupType(context, appWidgetId);
        String group = "";
        if (groupType.equals(context.getString(R.string.elite))) {
            group = loadGroupType(context, appWidgetId);
        } else {
            group = String.format("%s - %s", loadGroupType(context, appWidgetId), loadGroupNumber(context, appWidgetId));
        }
        return group;
    }
    
    /**
     * Recupera el nombre del mánager de la configuración.<br>
     * El valor por defecto es 'Eduardo Yáñez Parareda'.
     */
    static String loadManagerName(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String name = prefs.getString(PREF_PREFIX_KEY + PREF_MANAGER_KEY + appWidgetId, null);
        if (name != null) {
            return name;
        } else {
            return context.getString(R.string.default_manager_name);
        }
    }

    /**
     * Recupera el nombre del mánager de la configuración.<br>
     * El valor por defecto es 'Eduardo Yáñez Parareda'.
     */
    static long loadUpdateInterval(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Long interval = prefs.getLong(PREF_PREFIX_KEY + PREF_GROUP_UPDATE_INTERVAL_KEY + appWidgetId, 
                Long.valueOf(context.getString(R.string.default_update_interval)));
        return interval;
    }

    /**
     * Recupera el tipo de grupo configurado por el usuario (Rookie, Amateur, Pro, Master o Elite).<br>
     * El valor por defecto es 'Rookie'.
     */
    private static String loadGroupType(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String groupType = prefs.getString(PREF_PREFIX_KEY + PREF_GROUP_TYPE_KEY, null);
        if (groupType != null) {
            return groupType;
        } else {
            return context.getString(R.string.default_group_type);
        }
    }

    /**
     * Recupera el número de grupo configurado por el usuario.<br>
     * El valor por defecto es '217'.
     */
    private static String loadGroupNumber(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String groupNumber = prefs.getString(PREF_PREFIX_KEY + PREF_GROUP_NUMBER_KEY + appWidgetId, null);
        if (groupNumber != null) {
            return groupNumber;
        } else {
            return context.getString(R.string.default_group_number);
        }
    }
    
}
