package com.citious.converfit.Actividades.Conversations;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.AccesoDatos.Sqlite.MessageSqlite;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.Adapters.ListConversationsAdapter;
import com.citious.converfit.Contenedores.TabContenedoraActivity;
import com.citious.converfit.Models.ConversationModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static com.citious.converfit.Utils.UtilidadesGCM.DISPLAY_MESSAGE_ACTION;

public class ListConversationsFragments extends Fragment {

    Context miContext;
    ListView miListView;
    ProgressDialog pd;
    RecuperarConversaciones thread;
    BorrarConversaciones threadBorrar;
    String mensajeError = "";
    int offSet = 0;
    int limit = 1000;
    ArrayList<ConversationModel> miConverstaionsList = new ArrayList<>();
    boolean esPrimeraVez = true;
    String tituloAlert = "";
    String codigoError = "";
    boolean borrarConversacionOk = false;
    int tapPosicionConversacion = 0;
    boolean buscandoMas = false;
    boolean isPush = false;
    ConversationsSqlite accesoDatos;
    String conversationKey = "";
    boolean needUpdate = false;
    boolean desloguear = false;
    boolean mostrarGooglePlay = false;
    Handler customHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list_conversations,container,false);

        setHasOptionsMenu(true);
        miContext = getActivity();
        miListView = (ListView)v.findViewById(R.id.lstListConversationsActivity);

        miListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                offSet = 0;
                Intent miListMessagesIntent = new Intent(miContext, ListMessagesAcitity.class);
                String conversationKey = miConverstaionsList.get(position).getConversationKey();
                miListMessagesIntent.putExtra("conversationKey", conversationKey);
                String brandName = miConverstaionsList.get(position).getFname() + " " + miConverstaionsList.get(position).getLname();
                miListMessagesIntent.putExtra("brandName", brandName);
                String userKey = miConverstaionsList.get(position).getUserKey();
                miListMessagesIntent.putExtra("userkey", userKey);
                startActivityForResult(miListMessagesIntent, 5);
                getActivity().finish();
            }
        });

        accesoDatos = new ConversationsSqlite(miContext);
        miConverstaionsList = accesoDatos.devolverConversations(miContext);
        ListConversationsAdapter miAdapter = new ListConversationsAdapter(miContext, miConverstaionsList);
        miListView.setAdapter(miAdapter);
        registerForContextMenu(miListView);
        if(Conexion.isInternetAvailable(miContext)) {
            if(esPrimeraVez) {
                thread = new RecuperarConversaciones();
                thread.execute();
                esPrimeraVez = false;
            }
        }


        getActivity().registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mHandleMessageReceiver);
    }

    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            thread = new RecuperarConversaciones();
            thread.execute();
            customHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed() && isAdded()) {
            miConverstaionsList = accesoDatos.devolverConversations(miContext);
            ListConversationsAdapter miAdapter = new ListConversationsAdapter(miContext, miConverstaionsList);
            miListView.setAdapter(miAdapter);
            if(Conexion.isInternetAvailable(miContext)) {
                customHandler = new android.os.Handler();
                customHandler.postDelayed(updateTimerThread, 0);
            }

        } else {
            offSet = 0;
            mensajeError = "";
            if(customHandler != null){
                customHandler.removeCallbacks(updateTimerThread);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        miConverstaionsList = accesoDatos.devolverConversations(miContext);
        ListConversationsAdapter miAdapter = new ListConversationsAdapter(miContext, miConverstaionsList);
        miListView.setAdapter(miAdapter);
        if(Conexion.isInternetAvailable(miContext)) {
            thread = new RecuperarConversaciones();
            thread.execute();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.lstListConversationsActivity) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            int posicion = info.position;
            menu.setHeaderTitle(miConverstaionsList.get(posicion).getFname() + " " + miConverstaionsList.get(posicion).getLname());
            String[] menuItems = getResources().getStringArray(R.array.opciones_menu_contextual_list_conversations);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            tapPosicionConversacion = info.position;
            int indexMenu = item.getItemId();
            switch (indexMenu) {
                case 0:
                    conversationKey = miConverstaionsList.get(tapPosicionConversacion).getConversationKey();
                    boolean mensajesSinLeer = accesoDatos.hayMensajesSinLeer(conversationKey);
                    MessageSqlite accesoDatosMessages = new MessageSqlite(miContext);
                    accesoDatosMessages.borrarMensajesConversacion(conversationKey);
                    accesoDatos.borrarConversation(conversationKey);
                    if(mensajesSinLeer){
                        cambiarBadgeIcon();
                    }else{
                        miConverstaionsList = accesoDatos.devolverConversations(miContext);
                        ListConversationsAdapter miAdapter = new ListConversationsAdapter(miContext, miConverstaionsList);
                        miListView.setAdapter(miAdapter);
                    }
                    if(Conexion.isInternetAvailable(miContext)) {
                        threadBorrar = new BorrarConversaciones();
                        threadBorrar.execute();
                    }else{
                        mensajeError = getResources().getString(R.string.conexion_error);
                        mostrarAlerta();
                    }
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list_conversations, menu);
        boolean isSubBrand = Utils.obtenerIsGroupSubBrand(miContext);
        if(isSubBrand){
            menu.getItem(0).setEnabled(false);
            menu.getItem(0).setVisible(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.seleccionar_brand:
                offSet = 0;
                Intent miListElegibleFav = new Intent(miContext,ListElegibleFavoritesAcitivy.class);
                startActivity(miListElegibleFav);
                getActivity().finish();
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

    public class RecuperarConversaciones extends AsyncTask<Void, Void, Void> {

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
            if(pd != null && pd.isShowing()){
               pd.dismiss();
            }
            buscandoMas = false;
            if(isAdded()) {
                if (!mensajeError.equalsIgnoreCase(getResources().getString(R.string.list_conversations_empty))) {
                    if (mensajeError.length() > 0) {
                        mostrarAlerta();
                    } else if (needUpdate) {
                        miConverstaionsList = accesoDatos.devolverConversations(miContext);
                        final ListConversationsAdapter miAdapter = new ListConversationsAdapter(miContext, miConverstaionsList);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                miAdapter.notifyDataSetChanged();
                            }
                        });
                        miListView.setAdapter(miAdapter);
                        registerForContextMenu(miListView);
                    }
                } else if (borrarConversacionOk) {
                    miConverstaionsList = accesoDatos.devolverConversations(miContext);
                    ListConversationsAdapter miAdapter = new ListConversationsAdapter(miContext, miConverstaionsList);
                    miListView.setAdapter(miAdapter);
                }else{
                    accesoDatos.borrarAllConversations();
                    miConverstaionsList.clear();
                    ListConversationsAdapter miAdapter = new ListConversationsAdapter(miContext, miConverstaionsList);
                    miListView.setAdapter(miAdapter);
                }
                if (borrarConversacionOk) {
                    borrarConversacionOk = false;
                }
            }
        }
    }

    public void getInfoServidor() {
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("conversations");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "list_conversations"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("conversations_last_update", String.valueOf(Utils.obtenerConversationsLastUpdate(miContext))));
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
                    String conversationsLastUpdate = data.getString("conversations_last_update");
                    Utils.guardarConversationsLastUpdate(miContext,conversationsLastUpdate);
                    needUpdate = data.getBoolean("need_to_update");
                    if(needUpdate) {
                        accesoDatos.borrarAllConversations();
                        JSONArray listaConversaciones = data.getJSONArray("conversations");
                        for (int indice = 0; indice < listaConversaciones.length(); indice++) {
                            ConversationModel conversation = new ConversationModel(listaConversaciones.getJSONObject(indice));
                            accesoDatos.insertarConversation(conversation);
                        }
                    }
                }else{
                    codigoError = datos.getString("error_code");
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

    public class BorrarConversaciones extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if(!borrarConversacionOk) {
                borrarConversacion();
            }
            return null;
        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            // Oculto la ventana de espera de conexión
            if (mensajeError.length() > 0) {
                mostrarAlerta();
            }
        }
    }

    public void borrarConversacion() {
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("conversations");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "delete_conversation"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("conversation_key", conversationKey));
        pairs.add(new BasicNameValuePair("app", Utils.app));

        Post post = new Post();
        try {
            JSONObject datos = post.getServerData(url, pairs);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(!resultado.equalsIgnoreCase("true")){
                    codigoError = datos.getString("error_code");
                    desloguear = Utils.comprobarDesloguear(codigoError);
                    String[] error = new Utils().devolverStringError(miContext, codigoError);
                    tituloAlert = error[0];
                    mensajeError = error[1];
                }else{
                    Utils.dbErrorContador = 0;
                    JSONObject data = datos.getJSONObject("data");
                    String conversationsLastUpdate = data.getString("conversations_last_update");
                    Utils.guardarConversationsLastUpdate(miContext,conversationsLastUpdate);
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



    //Este es el codigo que se ejecutara cuando recibamos la notificacion push y este abierto
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String pushConverstaionKey = Utils.pushConversationKey;
            offSet = 0;
            isPush = true;
            GetConversacion getConversacionThread = new GetConversacion();
            getConversacionThread.execute(pushConverstaionKey);
        }
    };

    public class GetConversacion extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            getConversacion(params[0]);
            return null;
        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            // Oculto la ventana de espera de conexión
            if (mensajeError.length() > 0) {
                mostrarAlerta();
            }else {
                final ListConversationsAdapter miAdapter = new ListConversationsAdapter(miContext, miConverstaionsList);
                miListView.setAdapter(miAdapter);
                registerForContextMenu(miListView);
            }
        }
    }

    public void getConversacion(String pushConversationKey) {
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("conversations");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "get_conversation"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("last_update", "0"));
        pairs.add(new BasicNameValuePair("conversation_key", pushConversationKey));
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
                    accesoDatos.borrarConversation(pushConversationKey);
                    JSONObject dataResultado = data.getJSONObject("conversation");
                    ConversationModel conversation = new ConversationModel(dataResultado);
                    accesoDatos.insertarConversation(conversation);
                    miConverstaionsList = accesoDatos.devolverConversations(miContext);
                }else{
                    codigoError = datos.getString("error_code");
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

    //Metodo que llamamos cuando borramos una conversacion para actualizar el badge
    private void cambiarBadgeIcon(){
        ((TabContenedoraActivity)getActivity()).llamarRecuperarMensajes();
    }
}