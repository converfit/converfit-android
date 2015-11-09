package com.citious.converfit.Actividades.Favorites;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.AccesoDatos.Sqlite.UserSqlite;
import com.citious.converfit.Actividades.Conversations.ListMessagesAcitity;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.Adapters.ListUserAdapter;
import com.citious.converfit.Models.UserModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ListFavoritesFragment extends Fragment {
    Context miContext;
    private RecyclerView recyclerView;
    ProgressDialog pd;
    RecuperarBrandsFavoritas thread;
    String tituloAlert = "";
    String mensajeError = "";
    int offSet = 0;
    int limit = 1000;
    ArrayList<UserModel> miUserList = new ArrayList<>();
    String codigoError = "";
    boolean borrarFavoritosnOk = false;
    boolean buscandoMas = false;
    SearchView searchView;
    String textoBuscado = "";
    UserSqlite accesoDatos;
    boolean needUpdate = false;
    boolean desloguear = false;
    boolean mostrarGooglePlay = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_list_favorites,container,false);

        miContext = getActivity();

        accesoDatos = new UserSqlite(miContext);
        miUserList = accesoDatos.devolverUsers();
        ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
        recyclerView = (RecyclerView) v.findViewById(R.id.lstListFavBrandActivity);
        recyclerView.setAdapter(miAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        hayFavoritos();
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        miUserList = accesoDatos.devolverUsers();
        hayFavoritos();
        ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
        recyclerView.setAdapter(miAdapter);
        if(Conexion.isInternetAvailable(miContext)) {
            thread = new RecuperarBrandsFavoritas();
            thread.execute();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isResumed() && isAdded()){
            miUserList = accesoDatos.devolverUsers();
            hayFavoritos();
            ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
            recyclerView.setAdapter(miAdapter);
            if(Conexion.isInternetAvailable(miContext)) {
                thread = new RecuperarBrandsFavoritas();
                thread.execute();
            }
        }else{
            offSet = 0;
            buscandoMas = false;
            mensajeError = "";
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
                if (desloguear) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search_favoritos:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list_favorites, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search_favoritos);

        searchView=(SearchView)MenuItemCompat.getActionView(searchItem);
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
                recyclerView.setAdapter(miAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ((newText.equalsIgnoreCase("") || newText.isEmpty()) && textoBuscado.isEmpty()) {
                    miUserList = accesoDatos.devolverUsers();
                    ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
                    recyclerView.setAdapter(miAdapter);
                }
                return false;
            }
        });
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
            if(isAdded()) {
                if (!mensajeError.equalsIgnoreCase(getResources().getString(R.string.list_favorites_empty))) {
                    if (mensajeError.length() > 0) {
                        mostrarAlerta();
                    } else if (needUpdate) {
                        miUserList = accesoDatos.devolverUsers();
                        ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
                        recyclerView.setAdapter(miAdapter);
                        registerForContextMenu(recyclerView);
                    }
                } else if (borrarFavoritosnOk) {
                    miUserList = accesoDatos.devolverUsers();
                    ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
                    recyclerView.setAdapter(miAdapter);
                }
            }
        }
    }

    public void getInfoServidor(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("brands");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "list_users"));
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
                    String favoritesLastUpdate = data.getString("users_last_update");
                    Utils.guardarFavoritosLastUpdate(miContext,favoritesLastUpdate);
                    needUpdate = data.getBoolean("need_to_update");
                    if(needUpdate) {
                        accesoDatos.borrarAllUsers();
                        JSONArray brands = data.getJSONArray("users");
                        for (int indice = 0; indice < brands.length(); indice++) {
                            UserModel user = new UserModel(brands.getJSONObject(indice));
                            accesoDatos.insertarUser(user);
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

    //Metodo para comprobar el numero de favoritos
    private void hayFavoritos(){
        if(Utils.numeroFavorites == 0){
            Utils.numeroFavorites = miUserList.size();
        }
    }
/*
    private void lanzarConversacion(int position){
        String userKey = miUserList.get(position).getUserKey();
        ConversationsSqlite accesoDatosConversations = new ConversationsSqlite(miContext);
        String conversationKey = accesoDatosConversations.existeConversacionDeUsuario(userKey);
        String brandName = miUserList.get(position).getUserName();
        //Creamos el intent a lista mensajes
        Intent miListMessagesIntent = new Intent(miContext, ListMessagesAcitity.class);
        if(conversationKey.isEmpty()){
            miListMessagesIntent.putExtra("elegibleFavoritesOrigin", false);
        }else{
            miListMessagesIntent.putExtra("conversationKey", conversationKey);
        }
        miListMessagesIntent.putExtra("brandName", brandName);
        miListMessagesIntent.putExtra("userkey", userKey);
        startActivity(miListMessagesIntent);
    }
    */
}
