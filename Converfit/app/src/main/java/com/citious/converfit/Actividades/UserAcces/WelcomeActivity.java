package com.citious.converfit.Actividades.UserAcces;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.Contenedores.TabContenedoraActivity;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends Activity {

    Context miContext;
    boolean errorMainActivity;
    String tituloAlert = "";
    String mensajeError = "";
    EditText miEmail;
    EditText miPassword;
    Button miBtnIniciarSesion;
    TextView miOlvidoContraseña;
    TextView miRegistarse;
    String correo, password;
    ProgressDialog pd;
    Login thread;
    boolean mostrarGooglePlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        miContext = this;

        miEmail = (EditText) findViewById(R.id.edtLoginCorreo);
        miPassword = (EditText) findViewById(R.id.edtLoginPass);
        miBtnIniciarSesion = (Button) findViewById(R.id.btn_iniciar_sesion);
        miOlvidoContraseña = (TextView) findViewById(R.id.txtLoginOlvidarContraseña);
        miRegistarse = (TextView) findViewById(R.id.txt_registrar_login);

        miOlvidoContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.converfit.com/app/es/recover_password/index.html"));
                startActivity(browserIntent);
            }
        });

        miRegistarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.converfit.com/app/es/signup/index.html"));
                startActivity(browserIntent);
            }
        });


        miBtnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocultarTEclado();
                if(Conexion.isInternetAvailable(miContext)) {
                    if(comprobarFormatos()){//Comprobamos si los formatos estan rellenos correctamente
                        correo = miEmail.getText().toString();
                        password = miPassword.getText().toString();
                        thread = new Login();
                        thread.execute();
                    }else{
                        mostrarAlerta();
                    }
                }else{
                    mensajeError = getResources().getString(R.string.conexion_error);
                    mostrarAlerta();
                }
            }
        });

        errorMainActivity = getIntent().getBooleanExtra("errorMainActivity",false);
        if(Utils.bloquearSistema && errorMainActivity){
            errorMainActivity = false;
           establecerMensajeBloquerApp();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    private void establecerMensajeBloquerApp(){
        String[] error = new Utils().devolverStringError(miContext,Utils.errorCheckSession);
        tituloAlert = error[0];
        mensajeError = error[1];
        mostrarAlerta();
    }

    private void ocultarTEclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean comprobarFormatos(){
        boolean formatoOk = true;
        boolean isValidEmail = new Utils().isValidEmail(miEmail.getText().toString());
        if(!isValidEmail){
            formatoOk = false;
            String[] error = new Utils().devolverStringError(miContext,"formato_email");
            tituloAlert = error[0];
            mensajeError = error[1];
        }else if(miPassword.getText().toString().length() < 4 || miPassword.getText().toString().length() > 25){
            formatoOk = false;
            String[] error = new Utils().devolverStringError(miContext,"formato_contraseña");
            tituloAlert = error[0];
            mensajeError = error[1];
        }
        return formatoOk;
    }

    public class Login extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getInfoServidor(correo, password);
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
            if(mensajeError.length() > 0){
                mostrarAlerta();
            }else{//Cambiar por la de chats
                Intent miChatIntent = new Intent(miContext, TabContenedoraActivity.class);
                miChatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(miChatIntent);
                finish();
            }
        }
    }

    public void getInfoServidor(String email, String password){
        String sistema = Utils.SISTEMA_STRING;
        String deviceKey = Utils.obtenerDeviceKey(miContext);
        String url = Utils.devolverURLservidor("access");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "login"));
        pairs.add(new BasicNameValuePair("email", email));
        pairs.add(new BasicNameValuePair("password", password));
        pairs.add(new BasicNameValuePair("device_key", deviceKey));
        pairs.add(new BasicNameValuePair("system", sistema));
        pairs.add(new BasicNameValuePair("app_version", Utils.appVersion));
        pairs.add(new BasicNameValuePair("app", Utils.app));

        Post post = new Post();
        try {
            JSONObject datos = post.getServerData(url, pairs);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(resultado.equalsIgnoreCase("true")){
                    //Borramos los lastUpdates que pudieramos tener guardados
                    Utils.borrarLastUpdates(miContext);
                    JSONObject data = datos.getJSONObject("data");
                    String sessionKey = data.getString("session_key");
                    String  lastUpdate = data.getString("last_update");
                    Utils.guardarSessionKey(miContext, sessionKey);
                    Utils.guardarLastUpdate(miContext, lastUpdate);
                    //Borramos lo que tuvieramos guardado en Sqlite
                    Utils.borrarSqlite(miContext);
                    Utils.borrarUserLogin(miContext);
                    //String type = data.getString("brand");
                    //Utils.guardarGroupSubBrands(miContext, type);
                    JSONObject admin = data.getJSONObject("admin");
                    String idAdmin = admin.getString("id_admin");
                    Utils.guardarIdLogin(miContext, idAdmin);
                    String fname = admin.getString("fname");
                    Utils.guardarFnameLogin(miContext, fname);
                    String lname = admin.getString("lname");
                    Utils.guardarLnameLogin(miContext, lname);
                }else{
                    String codigoError = datos.getString("error_code");
                    String[] error  = new Utils().devolverStringError(miContext,codigoError);
                    tituloAlert = error[0];
                    mensajeError = error[1];

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String codigoError = getResources().getString(R.string.default_error);
            String[] error = new Utils().devolverStringError(miContext, codigoError);
            tituloAlert = error[0];
            mensajeError = error[1];
        }
    }
}
