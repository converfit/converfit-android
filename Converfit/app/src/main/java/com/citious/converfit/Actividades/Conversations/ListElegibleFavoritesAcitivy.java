package com.citious.converfit.Actividades.Conversations;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.AccesoDatos.Sqlite.UserSqlite;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.Adapters.ListUserAdapter;
import com.citious.converfit.Contenedores.TabContenedoraActivity;
import com.citious.converfit.Models.UserModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ListElegibleFavoritesAcitivy extends ActionBarActivity {

    Context miContext;
    ListView miListView;
    ProgressDialog pd;
    RecuperarBrandsFavoritas thread;
    String tituloAlert = "";
    String mensajeError = "";
    int offSet = 0;
    int limit = 1000;
    ArrayList<UserModel> miUserList = new ArrayList<>();
    SearchView searchView;
    String textoBuscado = "";
    UserSqlite accesoDatos;
    boolean needUpdate = false;
    boolean desloguear = false;
    boolean mostrarGooglePlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_elegible_favorites_acitivy);

        //Activamos que muestre la flecha atras
        Toolbar toolbar = (Toolbar) findViewById(R.id.list_elegible_fav_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_list_favorites));
        miContext = this;
        miListView = (ListView)findViewById(R.id.lstListElegibleFavctivity);
        miListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lanzarConversacion(position);
            }
        });

        accesoDatos = new UserSqlite(miContext);
        miUserList = accesoDatos.devolverUsers();
        ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
        miListView.setAdapter(miAdapter);
        if(Conexion.isInternetAvailable(miContext)) {
            thread = new RecuperarBrandsFavoritas();
            thread.execute();
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
                    finish();
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_elegible_favorites_acitivy, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_elegible_favoritos);

        searchView=(SearchView) MenuItemCompat.getActionView(searchItem);
        if(searchView==null)
        {
            MenuItemCompat.setShowAsAction(searchItem, MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setActionView(searchItem, searchView = new SearchView(miContext));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                miUserList = accesoDatos.devolverUserBuscado(query);
                ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
                miListView.setAdapter(miAdapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ((newText.equalsIgnoreCase("") || newText.isEmpty()) && textoBuscado.isEmpty()) {
                    miUserList = accesoDatos.devolverUsers();
                    ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
                    miListView.setAdapter(miAdapter);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent miBackIntent = new Intent(miContext,TabContenedoraActivity.class);
                startActivity(miBackIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent miBackIntent = new Intent(miContext,TabContenedoraActivity.class);
            startActivity(miBackIntent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class RecuperarBrandsFavoritas extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getInfoServidor();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String favLastUp = Utils.obtenerFavoritoLastUpdate(miContext);
            if(favLastUp.equalsIgnoreCase("0") && Utils.favoritesPrimeraVez) {
                Utils.favoritesPrimeraVez = false;
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
            if(pd != null && pd.isShowing()){
                pd.dismiss();
            }
            if(mensajeError.length() > 0){
                mostrarAlerta();
            }else if(needUpdate){
                miUserList = accesoDatos.devolverUsers();
                ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
                miListView.setAdapter(miAdapter);
            }
        }
    }

    public void getInfoServidor(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("brands");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "list_brand_users"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("users_last_update", String.valueOf(Utils.obtenerFavoritoLastUpdate(miContext))));
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
                    String favoritesLastUpdate = data.getString("brand_users_last_update");
                    Utils.guardarFavoritosLastUpdate(miContext, favoritesLastUpdate);
                    needUpdate = data.getBoolean("need_to_update");
                    if(needUpdate) {
                        accesoDatos.borrarAllUsers();
                        JSONArray listBrandsFavorites = data.getJSONArray("users");
                        for (int indice = 0; indice < listBrandsFavorites.length(); indice++) {
                            UserModel user = new UserModel(listBrandsFavorites.getJSONObject(indice));
                            accesoDatos.insertarUser(user);
                        }
                    }
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

    private void lanzarConversacion(int position){
        String userKey = miUserList.get(position).getUserKey();
        ConversationsSqlite accesoDatosConversations = new ConversationsSqlite(miContext);
        String conversationKey = accesoDatosConversations.existeConversacionDeUsuario(userKey);
        String brandName = miUserList.get(position).getFname() + " " + miUserList.get(position).getLname();
        //Creamos el intent a lista mensajes
        Intent miListMessagesIntent = new Intent(miContext, ListMessagesAcitity.class);
        if(conversationKey.isEmpty()){
            miListMessagesIntent.putExtra("elegibleFavoritesOrigin", true);
        }else{
            miListMessagesIntent.putExtra("conversationKey", conversationKey);
        }
        miListMessagesIntent.putExtra("brandName", brandName);
        miListMessagesIntent.putExtra("userkey", userKey);
        startActivity(miListMessagesIntent);
    }
}
