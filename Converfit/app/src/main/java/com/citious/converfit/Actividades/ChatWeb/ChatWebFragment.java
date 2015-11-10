package com.citious.converfit.Actividades.ChatWeb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.TimeLineSqlite;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.Adapters.ListNotificationsAdapter;
import com.citious.converfit.Models.TimeLineModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ChatWebFragment extends Fragment {

    Context miContext;
    ProgressDialog pd;
    int offSet = 0;
    int limit = 1000;
    boolean needUpdate = false;
    boolean desloguear = false;
    String tituloAlert = "";
    String mensajeError = "";
    TimeLineSqlite accesoDatos;
    ArrayList<TimeLineModel> miPostList = new ArrayList<>();
    RecuperarPosts thread;
    private RecyclerView recyclerView;
    boolean mostrarGooglePlay = false;
    Handler customHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_chat_web_fragment, container, false);

        miContext = getActivity();

        accesoDatos = new TimeLineSqlite(miContext);
        miPostList = accesoDatos.devolverAllPost();

        recyclerView = (RecyclerView) v.findViewById(R.id.lst_chat_web1);
        ListNotificationsAdapter adapter = new ListNotificationsAdapter(miContext, miPostList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(Conexion.isInternetAvailable(miContext)) {
            thread = new RecuperarPosts();
            thread.execute();
        }
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        miPostList = accesoDatos.devolverAllPost();
        ListNotificationsAdapter adapter = new ListNotificationsAdapter(miContext, miPostList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(Conexion.isInternetAvailable(miContext)) {
            thread = new RecuperarPosts();
            thread.execute();
        }
    }

    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            thread = new RecuperarPosts();
            thread.execute();
            customHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isResumed() && isAdded()){
            miPostList = accesoDatos.devolverAllPost();
            ListNotificationsAdapter adapter = new ListNotificationsAdapter(miContext, miPostList);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            if(Conexion.isInternetAvailable(miContext)) {
                customHandler = new android.os.Handler();
                customHandler.postDelayed(updateTimerThread, 0);
            }
        }else{
            if(customHandler != null){
                customHandler.removeCallbacks(updateTimerThread);
            }
        }
    }

    public class RecuperarPosts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getInfoServidor();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String converstLastUp = Utils.obtenerConversationsLastUpdate(miContext);
            if(converstLastUp.equalsIgnoreCase("0") && Utils.conversationsPrimeraVez) {
                Utils.conversationsPrimeraVez = false;
                DialogInterface.OnCancelListener dialogCancel = new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(miContext, getResources().getString(R.string.errorServidor), Toast.LENGTH_SHORT).show();
                    }
                };
                pd = ProgressDialog.show(miContext, getResources().getString(R.string.buscarServidor), getResources().getString(R.string.buscando), true, true, dialogCancel);
                pd.setCanceledOnTouchOutside(false);
            }
        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            // Oculto la ventana de espera de conexión
            if(pd != null && pd.isShowing()){
                pd.dismiss();
            }
            if(isAdded()) {
                if (mensajeError.length() > 0) {
                    mostrarAlerta();
                }else if (needUpdate) {
                    miPostList = accesoDatos.devolverAllPost();
                    ListNotificationsAdapter adapter = new ListNotificationsAdapter(miContext, miPostList);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            }
        }
    }

    public void getInfoServidor() {
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("brand_notifications");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "list_brand_notifications"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("brand_notifications_last_update", String.valueOf(Utils.obtenerBrandNotificationsLastUpdate(miContext))));
        pairs.add(new BasicNameValuePair("offset", String.valueOf(offSet)));
        pairs.add(new BasicNameValuePair("limit", String.valueOf(limit)));
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
                    String brandNotificationsLastUpdate = data.getString("brand_notifications_last_update");
                    Utils.guardarBrandNotificationsLastUpdate(miContext, brandNotificationsLastUpdate);
                    needUpdate = data.getBoolean("need_to_update");
                    if(needUpdate) {
                        accesoDatos.borrarAllPosts();
                        JSONArray listaPost = data.getJSONArray("brand_notifications");
                        for (int indice = 0; indice < listaPost.length(); indice++) {
                            TimeLineModel notification = new TimeLineModel(listaPost.getJSONObject(indice));
                            accesoDatos.insertarNotification(notification);
                        }
                    }
                }else{
                    String codigoError = datos.getString("error_code");
                    desloguear = Utils.comprobarDesloguear(codigoError);
                    String[] error = new Utils().devolverStringError(miContext, codigoError);
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
                    getActivity().finish();
                }else if(mostrarGooglePlay){
                    final String appPackageName = miContext.getPackageName();
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
