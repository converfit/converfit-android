package com.citious.converfit.Actividades.Conversations;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.UserSqlite;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.Adapters.ListUserAdapter;
import com.citious.converfit.Contenedores.TabContenedoraActivity;
import com.citious.converfit.Models.UserModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListElegibleFavoritesAcitivy extends AppCompatActivity {

    Context miContext;
    private RecyclerView recyclerView;
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

        accesoDatos = new UserSqlite(miContext);
        miUserList = accesoDatos.devolverUsers();
        ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
        recyclerView = (RecyclerView) findViewById(R.id.lstListElegibleFavctivity);
        recyclerView.setAdapter(miAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(miContext));

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
                recyclerView = (RecyclerView) findViewById(R.id.lstListElegibleFavctivity);
                recyclerView.setAdapter(miAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ((newText.equalsIgnoreCase("") || newText.isEmpty()) && textoBuscado.isEmpty()) {
                    miUserList = accesoDatos.devolverUsers();
                    ListUserAdapter miAdapter = new ListUserAdapter(miContext, miUserList);
                    recyclerView = (RecyclerView) findViewById(R.id.lstListElegibleFavctivity);
                    recyclerView.setAdapter(miAdapter);
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
                recyclerView = (RecyclerView) findViewById(R.id.lstListElegibleFavctivity);
                recyclerView.setAdapter(miAdapter);
            }
        }
    }

    public void getInfoServidor(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("brands");

        try {
            Map<String, Object> stringMap = new HashMap<>();
            stringMap.put("action", "list_users");
            stringMap.put("session_key", sessionKey);
            stringMap.put("users_last_update", String.valueOf(Utils.obtenerFavoritoLastUpdate(miContext)));
            stringMap.put("offset", String.valueOf(offSet));
            stringMap.put("limit", String.valueOf(limit));
            stringMap.put("app_version", Utils.appVersion);
            stringMap.put("app", Utils.app);
            JSONObject datos = Post.getServerData(stringMap,"POST",url);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(resultado.equalsIgnoreCase("true")){
                    Utils.dbErrorContador = 0;
                    JSONObject data = datos.getJSONObject("data");
                    String favoritesLastUpdate = data.getString("users_last_update");
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
}
