package com.citious.converfit.Actividades.ChatWeb;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.AccesoDatos.Sqlite.TimeLineSqlite;
import com.citious.converfit.Actividades.Conversations.ListMessagesAcitity;
import com.citious.converfit.Adapters.UserTimeLineAdapter;
import com.citious.converfit.Contenedores.TabContenedoraActivity;
import com.citious.converfit.Models.TimeLineModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserTimeLine extends ActionBarActivity {

    Context miContext;
    TimeLineSqlite accesoDatos;
    String userKey, userName,htmlResult;
    WebView miWebView;

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
            miWebView.loadDataWithBaseURL(null,htmlResult,"text/html","utf-8",null);
        }

        public void getInfoServidor() {
            String url = Utils.devolverURLservidor("brands");
            String sessionKey = Utils.obtenerSessionKey(miContext);

            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("action", "user_data"));
            pairs.add(new BasicNameValuePair("session_key", sessionKey));
            pairs.add(new BasicNameValuePair("user_key", userKey));
            pairs.add(new BasicNameValuePair("app", Utils.app));

            Post post = new Post();
            try {
                JSONObject datos = post.getServerData(url, pairs);
                if (datos != null && datos.length() > 0) {
                    // Para cada registro obtenido se extraen sus campos
                    String resultado = datos.getString("result");
                    if (resultado.equalsIgnoreCase("true")) {

                    } else {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
}
