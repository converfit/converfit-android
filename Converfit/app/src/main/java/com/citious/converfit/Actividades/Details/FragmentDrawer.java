package com.citious.converfit.Actividades.Details;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.UserSqlite;
import com.citious.converfit.Adapters.NavigationDrawerAdapter;
import com.citious.converfit.Contenedores.SlidingTabLayout;
import com.citious.converfit.Models.NavDraweItem;
import com.citious.converfit.Models.UserModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentDrawer extends Fragment {

    private RecyclerView recyclerView;
    private ImageView miBotonAjustes;
    private TextView miTxtActivadoDesactivado;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    public static ArrayList<UserModel> usersList;
    private FragmentDrawerListener drawerListener;
    private float lastTranslate = 0.0f;
    Context miContext;
    UserSqlite accesoDatos;
    String tituloAlert = "";
    String mensajeError = "";
    boolean desloguear = false;
    boolean needUpdate = false;
    boolean mostrarGooglePlay = false;
    TextView txtEstadoChat;
    String accionActivarDesactivar = "";
    Handler customHandler;
    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public List<NavDraweItem> getData() {
        List<NavDraweItem> data = new ArrayList<>();
        // preparing navigation drawer items
        for (int i = 0; i < usersList.size(); i++) {
            NavDraweItem navItem = new NavDraweItem();
            navItem.setUserName(usersList.get(i).getUserName());
            navItem.setAvatar(usersList.get(i).getAvatar());
            navItem.setConectionStatus(usersList.get(i).getConectionStatus());
            navItem.setUserKey(usersList.get(i).getUserKey());
            navItem.setHoraConectado(usersList.get(i).getHoraConectado());
            data.add(navItem);
        }
        return data;
    }

    private Runnable updateTimerThread = new Runnable()
    {
        public void run()
        {
            RecuperarUsuarios recuperarUsersThread = new RecuperarUsuarios();
            recuperarUsersThread.execute();
            customHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        miContext = getActivity();
        accesoDatos = new UserSqlite(miContext);
        usersList = accesoDatos.devolverUsers();
        RecuperarEstadoChat estadoThread = new RecuperarEstadoChat();
        estadoThread.execute();
        //if(usersList.isEmpty()){
            RecuperarUsuarios recuperarUsersThread = new RecuperarUsuarios();
            recuperarUsersThread.execute();
            customHandler = new android.os.Handler();
            customHandler.postDelayed(updateTimerThread, 0);
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        final View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        txtEstadoChat = (TextView) layout.findViewById(R.id.status_chat_drawer);
        RecuperarEstadoChat estadoThread = new RecuperarEstadoChat();
        estadoThread.execute();

        miBotonAjustes = (ImageView) layout.findViewById(R.id.ajustes_button_drawer);
        miBotonAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarAlertSheet();
            }
        });

        miTxtActivadoDesactivado = (TextView) layout.findViewById(R.id.status_chat_drawer);
        miTxtActivadoDesactivado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarAlertSheet();
            }
        });

        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new NavigationDrawerAdapter(miContext, getData());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
                mDrawerLayout.closeDrawer(containerView);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }


    public void setUp(int fragmentId, final DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
                adapter = new NavigationDrawerAdapter(miContext, getData());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float moveFactor = (drawerView.getWidth() * slideOffset);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    //frame.setTranslationX(moveFactor);
                    View pager = drawerLayout.findViewById(R.id.pager);
                    pager.setTranslationX(moveFactor);
                    toolbar.setTranslationX(moveFactor);
                    SlidingTabLayout tabs = (SlidingTabLayout)drawerLayout.findViewById(R.id.tabs);
                    tabs.setTranslationX(moveFactor);
                }
                else
                {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    View pager = drawerLayout.findViewById(R.id.pager);
                    pager.setTranslationX(moveFactor);
                    toolbar.setTranslationX(moveFactor);
                    SlidingTabLayout tabs = (SlidingTabLayout)drawerLayout.findViewById(R.id.tabs);
                    tabs.setTranslationX(moveFactor);
                    lastTranslate = moveFactor;
                }
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.quickview_icon);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDrawerLayout.isDrawerVisible(GravityCompat.START)){
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    public class RecuperarUsuarios extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getInfoServidor();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //Se ejecuta cuando se cancela el hilo
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        //Se ejecuta cuando se realiza la llamada publishProgress()
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            if (mensajeError.length() > 0) {
                mostrarAlerta();
            }else if (needUpdate) {
                usersList = accesoDatos.devolverUsers();
                adapter = new NavigationDrawerAdapter(miContext, getData());
                recyclerView.setAdapter(adapter);
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
            stringMap.put("offset", "0");
            stringMap.put("limit", "1000");
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
                    String codigoError = datos.getString("error_code");
                    desloguear = Utils.comprobarDesloguear(codigoError);
                    String[] error = new Utils().devolverStringError(miContext, codigoError);
                    tituloAlert = error[0];
                    mensajeError = error[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class RecuperarEstadoChat extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getestadoChat();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //Se ejecuta cuando se cancela el hilo
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        //Se ejecuta cuando se realiza la llamada publishProgress()
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            String estadoChat = Utils.obtenerWebChatStatus(miContext);
            if(estadoChat.equalsIgnoreCase("0")){
                txtEstadoChat.setText(miContext.getResources().getString(R.string.chat_desactivado_drawer));
            }else{
                txtEstadoChat.setText(miContext.getResources().getString(R.string.chat_activado_drawer));
            }
        }
    }

    public void getestadoChat(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("webchat");

        try {
            Map<String, Object> stringMap = new HashMap<>();
            stringMap.put("action", "brand_webchat_status");
            stringMap.put("session_key", sessionKey);
            JSONObject datos = Post.getServerData(stringMap,"POST",url);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(resultado.equalsIgnoreCase("true")){
                    Utils.dbErrorContador = 0;
                    JSONObject data = datos.getJSONObject("data");
                    String brandWebChatStatus = data.getString("brand_webchat_status");
                    Utils.guardarWebChatStatus(miContext, brandWebChatStatus);
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
        }
    }

    private void lanzarAlertSheet(){
        final MyCustomAlertSheet alertSheet = new MyCustomAlertSheet(miContext);
        final AlertDialog ad = alertSheet.show();
        ad.getWindow().setGravity(Gravity.BOTTOM);

        String estatusChat = Utils.obtenerWebChatStatus(miContext);
        if(estatusChat.equalsIgnoreCase("0")){
            alertSheet.miBtnDesactivar.setText(miContext.getResources().getString(R.string.alert_sheet_activar_chat));
            accionActivarDesactivar = "1";
        }else{
            alertSheet.miBtnDesactivar.setText(miContext.getResources().getString(R.string.alert_sheet_desactivar_chat));
            accionActivarDesactivar = "0";
        }

        alertSheet.miBtnDesactivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
                ChangeEstadoChat changeThread = new ChangeEstadoChat();
                changeThread.execute();
            }
        });

        alertSheet.miBtnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });
    }

    private void mostrarAlerta(){
        customHandler.removeCallbacks(updateTimerThread);
        if(isVisible() && isAdded()) {
            MyCustomDialog miConstructor = new MyCustomDialog(miContext, tituloAlert, mensajeError);
            String tituloBoton = miContext.getResources().getString(R.string.aceptar_alert);
            mostrarGooglePlay = false;
            if (mensajeError.equalsIgnoreCase(miContext.getResources().getString(R.string.app_version_error))) {
                mostrarGooglePlay = true;
                tituloBoton = miContext.getResources().getString(R.string.google_play);
            }
            // Definimos el botón y sus acciones
            AlertDialog dialog = miConstructor.setNegativeButton(tituloBoton, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (!mensajeError.equalsIgnoreCase(miContext.getResources().getString(R.string.session_key_not_valid))) {
                        customHandler.postDelayed(updateTimerThread, 0);
                    }
                    mensajeError = "";
                    dialog.cancel();// se cancela la ventana
                    if (desloguear) {
                        desloguear = false;
                        Utils.desLoguear(miContext);
                        getActivity().finish();
                    } else if (mostrarGooglePlay) {
                        final String appPackageName = miContext.getPackageName();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=" + appPackageName));
                        startActivity(intent);
                    }
                }
            }).show();
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(miContext.getResources().getColor(R.color.Rojo));
        }
    }

    public class ChangeEstadoChat extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            cambiarEstadoChat();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //Se ejecuta cuando se cancela el hilo
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        //Se ejecuta cuando se realiza la llamada publishProgress()
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            String estadoChat = Utils.obtenerWebChatStatus(miContext);
            if(estadoChat.equalsIgnoreCase("0")){
                txtEstadoChat.setText(miContext.getResources().getString(R.string.chat_desactivado_drawer));
            }else{
                txtEstadoChat.setText(miContext.getResources().getString(R.string.chat_activado_drawer));
            }
        }
    }

    public void cambiarEstadoChat(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("webchat");

        try {
            Map<String, Object> stringMap = new HashMap<>();
            stringMap.put("action", "update_brand_webchat_status");
            stringMap.put("session_key", sessionKey);
            stringMap.put("webchat_status", accionActivarDesactivar);
            JSONObject datos = Post.getServerData(stringMap,"POST",url);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(resultado.equalsIgnoreCase("true")){
                    Utils.dbErrorContador = 0;
                    Utils.guardarWebChatStatus(miContext, accionActivarDesactivar);
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
        }
    }
}
