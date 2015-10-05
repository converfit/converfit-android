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

public class ChangePasswordActivity extends ActionBarActivity {

    Context miContext;
    EditText miEdtNewPass, miEdtOldPass;
    Menu miMenu;
    String tituloAlert = "";
    String mensajeError = "";
    GuardarCambios thread;
    ProgressDialog pd;
    boolean cambiosOK = false;
    boolean desloguear = false;
    String passOld;
    String passNew;
    boolean mostrarGooglePlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        miContext = this;
        //Activamos que muestre la flecha atras
        Toolbar toolbar = (Toolbar) findViewById(R.id.change_pass_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        miEdtOldPass = (EditText) findViewById(R.id.edt_pass_actu_change_pass);
        miEdtOldPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                comprobarCamposRellenos();
            }
        });

        miEdtNewPass = (EditText) findViewById(R.id.edt_pass_new_change_pass);
        miEdtNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                comprobarCamposRellenos();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
        miMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent miIntent = new Intent();
                setResult(PasswordMenuActivity.RESULT_OK, miIntent);
                finish();
                return true;
            case R.id.guardar_change_password:
                ocultarTEclado();
                if(Conexion.isInternetAvailable(miContext)) {
                    if(comprobarFormatos()){//Comprobamos si los formatos estan rellenos correctamente
                        passOld = miEdtOldPass.getText().toString();
                        passNew = miEdtNewPass.getText().toString();
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
        String passOld = miEdtOldPass.getText().toString();
        String passNew = miEdtNewPass.getText().toString();

        //Si hay texto introducido habilitamos el boton de iniciar sesion, sino lo deshabilitamos
        if(passOld.length() > 0  && passNew.length() > 0){
            miMenu.getItem(0).setEnabled(true);
        }else{
            miMenu.getItem(0).setEnabled(false);
        }
    }

    private boolean comprobarFormatos(){
        boolean formatoOk = true;
        if(miEdtOldPass.getText().toString().length() < 4 || miEdtOldPass.getText().toString().length() > 50){
            formatoOk = false;
            String[] error = new Utils().devolverStringError(miContext,"pass_actual");
            tituloAlert = error[0];
            mensajeError = error[1];
        }else if(miEdtNewPass.getText().toString().length() < 4 || miEdtNewPass.getText().toString().length() > 50){
            formatoOk = false;
            String[] error = new Utils().devolverStringError(miContext,"pass_nueva");
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
                if(desloguear){
                    desloguear = false;
                    Utils.desLoguear(miContext);
                    finish();
                }else if(mostrarGooglePlay){
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

            getInfoServidor(passOld, passNew);
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

            String tituloAlerta = "";
            if(!cambiosOK){
                tituloAlerta = getResources().getString(R.string.error);
            }
            mostrarAlerta();
        }
    }

    public void getInfoServidor(String passOld, String passNew){
        String url = Utils.devolverURLservidor("access");

        String sessionKey = Utils.obtenerSessionKey(miContext);

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "update_password"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("old_password", passOld));
        pairs.add(new BasicNameValuePair("new_password", passNew));
        pairs.add(new BasicNameValuePair("app_version", Utils.appVersion));
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
                    Utils.guardarLastUpdate(miContext,lastUpdate);
                    String codigoError = getResources().getString(R.string.guardarOk);
                    String[] error = new Utils().devolverStringError(miContext,codigoError);
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
