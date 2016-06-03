package com.citious.converfit.Actividades.ChatWeb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.AccesoDatos.Sqlite.TimeLineSqlite;
import com.citious.converfit.Actividades.Conversations.ListMessagesAcitity;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.Contenedores.TabContenedoraActivity;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class UserTimeLine extends AppCompatActivity {

    Context miContext;
    TimeLineSqlite accesoDatos;
    String userKey, userName,htmlResult;
    WebView miWebView;
    boolean mostrarGooglePlay = false;
    String tituloAlert = "";
    String mensajeError = "";
    boolean desloguear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_time_line);
        miContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_time_line_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Utils.vieneUserTimeLine = true;
        accesoDatos = new TimeLineSqlite(miContext);

        userKey = getIntent().getStringExtra("userkey");
        userName = getIntent().getStringExtra("userName");

        miWebView = (WebView) findViewById(R.id.webViewUser);
        if(Conexion.isInternetAvailable(miContext)) {
            GetUserData thread = new GetUserData();
            thread.execute();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_time_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent miIntent = new Intent(miContext,TabContenedoraActivity.class);
                startActivity(miIntent);
                finish();
                return true;
            case R.id.open_conversation:
                lanzarConversacion();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetUserData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getInfoServidor();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(mensajeError.length() > 0){
                mostrarAlerta();
            }else {
                miWebView.loadDataWithBaseURL(null,htmlResult,"text/html","utf-8",null);
            }
        }

        public void getInfoServidor() {
            String url = Utils.devolverURLservidor("brands");
            String sessionKey = Utils.obtenerSessionKey(miContext);

            try {
                Map<String, Object> stringMap = new HashMap<>();
                stringMap.put("action", "user_data");
                stringMap.put("session_key", sessionKey);
                stringMap.put("brand_user_key", userKey);
                stringMap.put("app", Utils.app);
                JSONObject datos = Post.getServerData(stringMap,"POST",url);
                if (datos != null && datos.length() > 0) {
                    // Para cada registro obtenido se extraen sus campos
                    String resultado = datos.getString("result");
                    if (resultado.equalsIgnoreCase("true")) {

                    } else {
                        String codigoError = datos.getString("error_code");
                        desloguear = Utils.comprobarDesloguear(codigoError);
                        String[] error = new Utils().devolverStringError(miContext,codigoError);
                        tituloAlert = error[0];
                        mensajeError = error[1];
                    }
                }else{
                    String codigoError = getResources().getString(R.string.default_error);
                    String[] error = new Utils().devolverStringError(miContext,codigoError);
                    tituloAlert = error[0];
                    mensajeError = error[1];
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

    private void lanzarConversacion(){
        ConversationsSqlite accesoDatosConversations = new ConversationsSqlite(miContext);
        String conversationKey = accesoDatosConversations.existeConversacionDeUsuario(userKey);
        //Creamos el intent a lista mensajes
        Intent miListMessagesIntent = new Intent(miContext, ListMessagesAcitity.class);
        if(conversationKey.isEmpty()){
            miListMessagesIntent.putExtra("elegibleFavoritesOrigin", true);
        }else{
            miListMessagesIntent.putExtra("conversationKey", conversationKey);
        }
        miListMessagesIntent.putExtra("brandName", userName);
        miListMessagesIntent.putExtra("userkey", userKey);
        miContext.startActivity(miListMessagesIntent);
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
                if (desloguear) {
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
}
