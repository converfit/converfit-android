package com.citious.converfit.Actividades.UserAcces;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.citious.converfit.Utils.Utils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.Contenedores.TabContenedoraActivity;
import com.citious.converfit.AccesoDatos.Post;

public class MainActivity extends AppCompatActivity {

    Context miContext;
    boolean logueado = false;
    private String regid;
    private GoogleCloudMessaging gcm;
    String mensajeError = "";
    boolean mostrarGooglePlay = false;
    String tituloAlert = "";
    boolean errorMainActivity = false;
    ProgressDialog pd;

    //No se si se podra borrar alguna
    private static final String PROPERTY_REG_ID = "registration_id";

    final String SENDER_ID = "332855980435";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miContext = this;

        new Utils().comprobarPrimerasVeces(miContext);
        if(Conexion.isInternetAvailable(miContext)) {
            implementarPush();

            WaitTask waitTask = new WaitTask();
            waitTask.execute();
        }else{
            mensajeError = getResources().getString(R.string.conexion_error);
            mostrarAlerta();
        }
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

    private class WaitTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            comprobarCheckSession();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(logueado){
                //Cambiar por la de chats
                Intent miChatIntent = new Intent(miContext, TabContenedoraActivity.class);
                miChatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(miChatIntent);
                finish();
            }else{
                Intent intent = new Intent(miContext, WelcomeActivity.class);
                intent.putExtra("errorMainActivity",errorMainActivity);
                startActivity(intent);
                finish();
            }
        }
    }
    public void comprobarCheckSession(){

        String url = Utils.devolverURLservidor("access");

        String sessionKey = Utils.obtenerSessionKey(miContext);

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "check_session"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("device_key", Utils.obtenerDeviceKey(miContext)));
        pairs.add(new BasicNameValuePair("last_update", String.valueOf(Utils.obtenerLastUpdate(miContext))));
        pairs.add(new BasicNameValuePair("system", Utils.SISTEMA_STRING));
        pairs.add(new BasicNameValuePair("app_version", Utils.appVersion));
        pairs.add(new BasicNameValuePair("app", Utils.app));

        Post post = new Post();
        try {
            JSONObject datos = post.getServerData(url, pairs);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(resultado.equalsIgnoreCase("true")){
                    JSONObject data = datos.getJSONObject("data");
                    String lastUpdate = data.getString("last_update");
                    Utils.guardarLastUpdate(miContext,lastUpdate);
                    logueado = true;
                }else{
                    Utils.errorCheckSession = datos.getString("error_code");
                    errorMainActivity = true;
                    Utils.comprobarDesloguear(Utils.errorCheckSession);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void implementarPush(){

        gcm = GoogleCloudMessaging.getInstance(MainActivity.this);

        //Obtenemos el Registration ID guardado
        regid = getRegistrationId();

        //Si no disponemos de Registration ID comenzamos el registro
        if (regid.equals("")) {
            TareaRegistroGCM tarea = new TareaRegistroGCM();
            tarea.execute();
        }
    }

    private String getRegistrationId()
    {
        SharedPreferences prefs = getSharedPreferences(
                MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.length() == 0)
        {
            return "";
        }
        return registrationId;
    }

    private class TareaRegistroGCM extends AsyncTask<String,Integer,String>
    {
        @Override
        protected String doInBackground(String... params) {
            try {
                if(gcm == null){
                    gcm = GoogleCloudMessaging.getInstance(miContext);
                }
                regid = gcm.register(SENDER_ID);
                guardarIdRegistro();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void guardarIdRegistro(){
        Utils.guardarDeviceKey(miContext,regid);
    }
}
