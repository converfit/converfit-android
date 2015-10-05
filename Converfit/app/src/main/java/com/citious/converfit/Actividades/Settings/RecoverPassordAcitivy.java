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
import android.view.Menu;
import android.view.MenuItem;
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

public class RecoverPassordAcitivy extends ActionBarActivity {

    Context miContext;
    String tituloAlert = "";
    String mensajeError = "";
    RecuperarPassword thread;
    ProgressDialog pd;
    boolean recuOK = false;
    boolean desloguear = false;
    boolean mostrarGooglePlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_passord_acitivy);

        miContext = this;
        //Activamos que muestre la flecha atras
        Toolbar toolbar = (Toolbar) findViewById(R.id.advertencia_recover_pass_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recover_passord_acitivy, menu);
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
            case R.id.recuperar_pass_recover:
                if(Conexion.isInternetAvailable(miContext)) {
                    thread = new RecuperarPassword();
                    thread.execute();
                }else{
                    mensajeError = getResources().getString(R.string.conexion_error);
                    tituloAlert = getResources().getString(R.string.error);
                    mostrarAlerta();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public class RecuperarPassword extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getInfoServidor();
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
            if(recuOK){
                tituloAlert = getResources().getString(R.string.revisar_titulo);
            }
            mostrarAlerta();
        }
    }

    public void getInfoServidor(){
        String url = Utils.devolverURLservidor("access");
        String sessionKey = Utils.obtenerSessionKey(miContext);

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "recover_password"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
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
                    mensajeError = getResources().getString(R.string.revisar_mensaje);
                    recuOK = true;
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
