package com.citious.converfit.Actividades.Details;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.UserSqlite;
import com.citious.converfit.Adapters.NavigationDrawerAdapter;
import com.citious.converfit.Contenedores.SlidingTabLayout;
import com.citious.converfit.Models.NavDraweItem;
import com.citious.converfit.Models.UserModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

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
            navItem.setUserName(usersList.get(i).getFname() + " " + usersList.get(i).getLname());
            navItem.setAvatar(usersList.get(i).getAvatar());
            navItem.setConectionStatus("");
            navItem.setUserKey(usersList.get(i).getUserKey());
            //navItem.setHoraConectado(usersList.get(i).getHoraConectado());
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        miContext = getActivity();
        accesoDatos = new UserSqlite(miContext);
        usersList = accesoDatos.devolverUsers();
        if(usersList.isEmpty()){
            RecuperarUsuarios recuperarUsersThread = new RecuperarUsuarios();
            recuperarUsersThread.execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout
        final View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

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
            usersList = accesoDatos.devolverUsers();
            adapter = new NavigationDrawerAdapter(miContext, getData());
            recyclerView.setAdapter(adapter);
        }
    }

    public void getInfoServidor(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("brands");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "list_brand_users"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("users_last_update", String.valueOf(Utils.obtenerFavoritoLastUpdate(miContext))));
        pairs.add(new BasicNameValuePair("offset", "0"));
        pairs.add(new BasicNameValuePair("limit", "1000"));
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
                    Utils.guardarFavoritosLastUpdate(miContext,favoritesLastUpdate);
                    boolean needUpdate = data.getBoolean("need_to_update");
                    if(needUpdate) {
                        accesoDatos.borrarAllUsers();
                        JSONArray brands = data.getJSONArray("users");
                        for (int indice = 0; indice < brands.length(); indice++) {
                            UserModel user = new UserModel(brands.getJSONObject(indice));
                            accesoDatos.insertarUser(user);
                        }
                    }
                }else{
                    //codigoError = datos.getString("error_code");
                    //desloguear = Utils.comprobarDesloguear(codigoError);
                    //mensajeError = new Utils().devolverStringError(miContext,codigoError);
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

        alertSheet.miBtnDesactivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
                Toast.makeText(miContext, "Desactivar", Toast.LENGTH_SHORT).show();
            }
        });

        alertSheet.miBtnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });
    }
}
