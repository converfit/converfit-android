package com.citious.converfit.Actividades.Settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PersonalDataActivity extends ActionBarActivity {

    Context miContext;
    EditText miEdtNombre, miEdtApellidos;
    Menu miMenu;
    String tituloAlert = "";
    String mensajeError = "";
    GuardarCambios thread;
    ProgressDialog pd;
    boolean cambiosOK = false;
    boolean desloguear = false;
    String nombre;
    String apellidos;
    boolean mostrarGooglePlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);

        //Activamos que muestre la flecha atras
        Toolbar toolbar = (Toolbar) findViewById(R.id.personal_data_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        miContext = this;
        miEdtNombre = (EditText) findViewById(R.id.edt_nombre_personal_data);
        miEdtNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Comprobamos si el texto cambia
                comprobarCamposRellenos();
            }
        });

        miEdtApellidos = (EditText) findViewById(R.id.edt_apellidos_personal_data);
        miEdtApellidos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Comprobamos si el texto cambia
                comprobarCamposRellenos();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal_data, menu);
        miMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //guardar_personal_data
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent miIntent = new Intent();
                setResult(PersonalDataActivity.RESULT_OK, miIntent);
                finish();
                return true;
            case R.id.guardar_personal_data:
                ocultarTEclado();
                if(Conexion.isInternetAvailable(miContext)) {
                    if(comprobarFormatos()){//Comprobamos si los formatos estan rellenos correctamente
                        nombre = miEdtNombre.getText().toString();
                        apellidos = miEdtApellidos.getText().toString();
                        thread = new GuardarCambios();
                        thread.execute();
                    }else{
                        mostrarAlerta();
                    }
                }else{
                    mensajeError = getResources().getString(R.string.conexion_error);
                    mostrarAlerta();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ocultarTEclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void comprobarCamposRellenos(){
        String nombre = miEdtNombre.getText().toString();
        String appellidos = miEdtApellidos.getText().toString();

        //Si hay texto introducido habilitamos el boton de iniciar sesion, sino lo deshabilitamos
        if(nombre.length() > 0  && appellidos.length() > 0){
            miMenu.getItem(0).setEnabled(true);
        }else{
            miMenu.getItem(0).setEnabled(false);
        }
    }

    private boolean comprobarFormatos(){
        boolean formatoOk = true;
        if(miEdtNombre.getText().toString().length() < 3 || miEdtNombre.getText().toString().length() > 50){
            formatoOk = false;
            String[] error = new Utils().devolverStringError(miContext,"formato_nombre");
            tituloAlert = error[0];
            mensajeError = error[1];
        }else if(miEdtApellidos.getText().toString().length() < 3 || miEdtApellidos.getText().toString().length() > 50){
            formatoOk = false;
            String[] error = new Utils().devolverStringError(miContext,"formato_apellidos");
            tituloAlert = error[0];
            mensajeError = error[1];
        }
        return formatoOk;
    }

    private void mostrarAlerta(){
        MyCustomDialog miConstructor = new MyCustomDialog(miContext, tituloAlert, mensajeError);
        String tituloBoton = getResources().getString(R.string.aceptar_alert);
        mostrarGooglePlay = false;
        if(mensajeError.equalsIgnoreCase(getResources().getString(R.string.app_version_error))){
            mostrarGooglePlay = true;
            tituloBoton = getResources().getString(R.string.google_play);
        }
        // Definimos el botón y sus acciones
        AlertDialog dialog = miConstructor.setNegativeButton(tituloBoton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mensajeError = "";
                dialog.cancel();// se cancela la ventana
                if(mostrarGooglePlay){
                    final String appPackageName = getPackageName();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + appPackageName));
                    startActivity(intent);
                }
            }
        }).show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Rojo));

    }

    public class GuardarCambios extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getInfoServidor(nombre, apellidos);
            return null;
        }
        //Se ejecuta antes de comenzar el hilo, puede tener operaciones en el UI Thread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DialogInterface.OnCancelListener dialogCancel = new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(getBaseContext(), getResources().getString(R.string.errorServidor), Toast.LENGTH_SHORT).show();
                }
            };
            pd = ProgressDialog.show(miContext, getResources().getString(R.string.buscarServidor), getResources().getString(R.string.buscando), true, true, dialogCancel);
            pd.setCanceledOnTouchOutside(false);
        }

        //Se ejecuta cuando se cancela el hilo
        @Override
        protected void onCancelled() {
            super.onCancelled();
            pd.cancel();
        }

        //Se ejecuta cuando se realiza la llamada publishProgress()
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            // Oculto la ventana de espera de conexión
            pd.dismiss();
            mostrarAlerta();
        }
    }

    public void getInfoServidor(String nombre, String apellidos){
        String url = Utils.devolverURLservidor("access");
        String sessionKey = Utils.obtenerSessionKey(miContext);

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "update_personal_data"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("fname", nombre));
        pairs.add(new BasicNameValuePair("lname", apellidos));
        pairs.add(new BasicNameValuePair("app", Utils.app));

        Post post = new Post();
        try {
            JSONObject datos = post.getServerData(url, pairs);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(resultado.equalsIgnoreCase("true")){
                    Utils.dbErrorContador = 0;
                    JSONObject data = datos.getJSONObject("data");
                    String lastUpdate = data.getString("last_update");
                    Utils.guardarLastUpdate(miContext, lastUpdate);
                    String codigoError = getResources().getString(R.string.guardarOk);
                    String[] error = new Utils().devolverStringError(miContext, codigoError);
                    tituloAlert = error[0];
                    mensajeError = error[1];
                    cambiosOK = true;
                }else{
                    String codigoError = datos.getString("error_code");
                    desloguear = Utils.comprobarDesloguear(codigoError);
                    String[] error = new Utils().devolverStringError(miContext,codigoError);
                    tituloAlert = error[0];
                    mensajeError = error[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String codigoError = getResources().getString(R.string.default_error);
            String[] error = new Utils().devolverStringError(miContext,codigoError);
            tituloAlert = error[0];
            mensajeError = error[1];
        }
    }
}
