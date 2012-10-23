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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.elpaso.android.gpro.beans.Manager;
import com.elpaso.android.gpro.exceptions.ConfigurationException;
import com.elpaso.android.gpro.exceptions.ParseException;

/**
 * Configuración del widget cuando se instala.
 * 
 * @author eduardo.yanez
 */
public class GproWidgetConfigure extends Activity {
    private static final Logger logger = LoggerFactory.getLogger(GproWidgetConfigure.class);

    private static final String PREFS_NAME = "com.elpaso.android.gpro.GproWidgetProvider";
    private static final String PREF_PREFIX_KEY = "gpro_";
    private static final String PREF_MANAGER_IDM_KEY = "manager_idm_";
    private static final String PREF_MANAGER_NAME_KEY = "manager_name_";
    private static final String PREF_GROUP_TYPE_KEY = "group_type_";
    private static final String PREF_GROUP_NUMBER_KEY = "group_number_";
    private static final int ALERT_DIALOG_SD_ERROR = 1;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner managers;
    private Spinner groupNumbers;
    private Spinner groupTypes;
    
    @Override
    public void onCreate(Bundle icicle) {
        if (logger.isDebugEnabled()) {
            logger.debug("onCreate");
        }
        super.onCreate(icicle);
        // Si se pulsa el botón de back, no instalamos el widget
        setResult(RESULT_CANCELED);

        // Mostramos el layout configurado para este componente
        setContentView(R.layout.gpro_configuration);

        // Buscamos los campos del formulario 
        // Desplegable con tipos de grupo
        this.groupTypes = (Spinner) findViewById(R.id.group_types_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.group_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.groupTypes.setAdapter(adapter);
        this.groupTypes.setSelected(false);
        this.groupTypes.setOnItemSelectedListener(groupTypeListener);

        // Número de grupo
        this.groupNumbers = (Spinner) findViewById(R.id.group_numbers_spinner);
        this.groupNumbers.setOnItemSelectedListener(groupNumbersListener);
        this.groupNumbers.setEnabled(false);
        
        // Campo de texto del nombre del manager
        this.managers = (Spinner) findViewById(R.id.managers_spinner);
        this.managers.setEnabled(false);

        // Asociamos al botón de salvar la acción que debe ejecutar
        findViewById(R.id.save_button).setOnClickListener(saveButtonListener);

        // Busca el identificador del widget del Intent recibido. 
        this.mAppWidgetId = UtilHelper.getWidgetId(this.getIntent());

        // Si el identificador no es válido, terminamos.
        if (this.mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            logger.error("Widget identifier invalid, finishing application");
            finish();
        }
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch(id) {
        case ALERT_DIALOG_SD_ERROR:
            // do the work to define the pause Dialog
            dialog = UIHelper.makeAlertDialog(this, this.getString(R.string.error), "La SD no está disopnible");
            break;
        default:
            dialog = null;
        }
        return dialog;
    }
    
    private List<CharSequence> getGroupNumbers(String groupType) {
        List<CharSequence> numbers = new ArrayList<CharSequence>();
        int limit = 0;
        if (groupType.equals(this.getString(R.string.rookie))) {
            limit = Integer.valueOf(this.getString(R.string.rookie_max_groups));
        } else if (groupType.equals(this.getString(R.string.amateur))) {
            limit = Integer.valueOf(this.getString(R.string.amateur_max_groups));
        } else if (groupType.equals(this.getString(R.string.pro))) {
            limit = Integer.valueOf(this.getString(R.string.pro_max_groups));
        } else if (groupType.equals(this.getString(R.string.master))) {
            limit = Integer.valueOf(this.getString(R.string.master_max_groups));
        } 
        for (int i = 1; i <= limit; i++) {
            numbers.add(String.valueOf(i));
        }
        return numbers;
    }
    
    /**
     * Listener para el botón salvar configuración.
     */
    View.OnClickListener saveButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = GproWidgetConfigure.this;

            // Salvamos todas las preferencias del usuario 
            Manager manager = (Manager) managers.getSelectedItem();
            saveManagerIdm(context, manager.getIdm());

            String groupType = groupTypes.getSelectedItem().toString();
            String groupNumber = "";
            if (!groupType.equals(context.getString(R.string.elite))) {
                groupNumber = groupNumbers.getSelectedItem().toString();
            }
            saveGroupType(context, groupType);
            saveGroupNumber(context, groupNumber);

            // Actualizamos la información que se muestra en el widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            try {
                GproWidgetProvider.setUpWidget(context, appWidgetManager, mAppWidgetId, loadManagerIdm(context));
            } catch (ParseException e) {
                logger.error("Error happened getting information from GPRO", e);
            }

            // Devolvemos el control al widget que nos llamó
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
        
    /**
     * Listener para el spinner de los tipos de grupo.
     */
    AdapterView.OnItemSelectedListener groupTypeListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
            final Context context = GproWidgetConfigure.this;
            // Si el grupo elegido es Élite, pasamos del número de grupo y cargamos los mánagers directamente 
            if (groupTypes.getSelectedItem().toString().equals(context.getString(R.string.elite))) {
                groupNumbers.setEnabled(false);
                new DownloadGroupManagersTask(context).execute();
            } else {
                // Número de grupo
                ArrayAdapter<CharSequence> groupsAdapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item, 
                        getGroupNumbers(groupTypes.getSelectedItem().toString()));
                groupsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                groupNumbers.setAdapter(groupsAdapter);
                groupNumbers.setEnabled(true);
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };
        
    /**
     * Listener para el spinner de los managers del grupo.
     */
    AdapterView.OnItemSelectedListener groupNumbersListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
            final Context context = GproWidgetConfigure.this;
            new DownloadGroupManagersTask(context).execute();
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };
        
    /**
     * Clase para que la conexión a la web, y la carga de los datos sea asíncrona y thread-safe.
     */
    private class DownloadGroupManagersTask extends AsyncTask<Integer, Void, List<Manager>> {
        private Context context;
        private ProgressDialog progressDialog;
        
        public DownloadGroupManagersTask(Context context) {
            super();
            this.context = context;
        }

        /**
         * Mostramos un diálogo de 'Cargando...' o con el mensaje que sea.
         */
        @Override
        protected void onPreExecute() {
            progressDialog = UIHelper.makeProgressDialog(context, getString(R.string.loading));
            progressDialog.show();
        }

        /**
         * Leemos de la web los managers de un grupo
         */
        protected List<Manager> doInBackground(Integer... params) {
            try {
                return GproDAO.findGroupMembers(context, 
                        groupTypes.getSelectedItem().toString(), 
                        groupNumbers.getSelectedItem().toString());
            } catch (ParseException e) {
                return null;
            }
        }

        /**
         * Actualizamos la lista de managers.
         */
        protected void onPostExecute(List<Manager> groupManagers) {
            if (groupManagers != null) {
                ArrayAdapter<Manager> managersAdapter = new ArrayAdapter<Manager>(context, android.R.layout.simple_spinner_item, groupManagers);
                managersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                managers.setAdapter(managersAdapter);
                managers.setEnabled(true);
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                managers.setEnabled(false);
                Dialog alert = UIHelper.makeAlertDialog(context, context.getString(R.string.error), 
                        UIHelper.makeErrorMessage(context, context.getString(R.string.error_101)));
                alert.show();
            }
        }
    }

    /**
     * Guardamos el nombre del manager en las preferencias.
     */
    static void saveManagerName(Context context, String name) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + PREF_MANAGER_NAME_KEY, name.trim());
        prefs.commit();
    }

    /**
     * Guardamos el IDM del manager en las preferencias.
     */
    static void saveManagerIdm(Context context, Integer idm) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + PREF_MANAGER_IDM_KEY, idm.toString());
        prefs.commit();
    }

    /**
     * Guardamos el tipo de grupo en las preferencias.
     */
    static void saveGroupType(Context context, String groupType) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + PREF_GROUP_TYPE_KEY, groupType.trim());
        prefs.commit();
    }

    /**
     * Guardamos el número de grupo en las preferencias.
     */
    static void saveGroupNumber(Context context, String groupNumber) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + PREF_GROUP_NUMBER_KEY, groupNumber.trim());
        prefs.commit();
    }

    /**
     * Recupera el identificador del grupo, es decir, el tipo de grupo junto con el número, que será usado para
     * recuperar la información de la parrilla de salida o de la clasificación, carrera, etc.
     * 
     * @return GroupType - GroupNumber, o si se trata de Elite, sólo el GroupType.
     * @throws ConfigurationException if group can't be calculated. 
     */
    static String loadGroupId(Context context) throws ConfigurationException {
        String groupType = loadGroupType(context);
        String group = null;
        if (groupType != null) {
            if (context.getString(R.string.elite).equals(groupType)) {
                group = loadGroupType(context);
            } else {
                group = String.format("%s - %s", loadGroupType(context), loadGroupNumber(context));
            }
        }
        if (group == null) {
            throw new ConfigurationException(context.getString(R.string.error_102));
        }
        return group;
    }
    
    /**
     * Recupera el nombre del mánager de la configuración.<br>
     */
    static String loadManagerName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String name = prefs.getString(PREF_PREFIX_KEY + PREF_MANAGER_NAME_KEY, null);
        if (name != null) {
            return name;
        } else {
            return null;
        }
    }

    /**
     * Recupera el IDM del mánager de la configuración.<br>
     */
    static Integer loadManagerIdm(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String idm = prefs.getString(PREF_PREFIX_KEY + PREF_MANAGER_IDM_KEY, null);
        if (idm != null) {
            return Integer.valueOf(idm);
        } else {
            return null;
        }
    }

    /**
     * Recupera el tipo de grupo configurado por el usuario (Rookie, Amateur, Pro, Master o Elite).<br>
     */
    private static String loadGroupType(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String groupType = prefs.getString(PREF_PREFIX_KEY + PREF_GROUP_TYPE_KEY, null);
        if (groupType != null) {
            return groupType;
        } else {
            return null;
        }
    }

    /**
     * Recupera el número de grupo configurado por el usuario.<br>
     */
    private static String loadGroupNumber(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String groupNumber = prefs.getString(PREF_PREFIX_KEY + PREF_GROUP_NUMBER_KEY, null);
        if (groupNumber != null) {
            return groupNumber;
        } else {
            return null;
        }
    }

}
