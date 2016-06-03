package com.citious.converfit.Contenedores;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.Actividades.Details.FragmentDrawer;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import static com.citious.converfit.Utils.UtilidadesGCM.DISPLAY_MESSAGE_ACTION;
import static com.citious.converfit.Utils.UtilidadesGCM.actividadAbierta;

public class TabContenedoraActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    // Declaring Your View and Variables
    Context miContext;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Online" , "Usuarios","Chats, Settings"};
    int Numboftabs =4;
    String numeroMensajesSinLeer = "0";
    boolean primeraNotificacion = true;
    private FragmentDrawer drawerFragment;
    DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        miContext = this;
        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, mDrawerLayout, toolbar);
        drawerFragment.setDrawerListener(this);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        if(!Utils.vieneUserTimeLine) {
            pager.setCurrentItem(2);//Seleccionamos el chat como default
        }else{
            Utils.vieneUserTimeLine = false;
            pager.setCurrentItem(0);
        }


        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setCustomTabView(R.layout.tab_layout, 0);
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.RojoCabecera);
            }
        });

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Toast.makeText(miContext,"onPageScrolled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(miContext,"onPageSelected",Toast.LENGTH_SHORT).show();
                primeraNotificacion = true;
                actividadAbierta = true;
                ConversationsSqlite accesoDatosConversation = new ConversationsSqlite(miContext);
                numeroMensajesSinLeer = String.valueOf(accesoDatosConversation.obtenerNumeroMensajesSinLeer());
                //badge = new BadgeView(miContext, tabs);
                //badge.setText(numeroMensajesSinLeer);
                if (!numeroMensajesSinLeer.equalsIgnoreCase("0")) {
                  //  badge.show();
                }
                //badge = new BadgeView(miContext, tabs);
                //badge.setText(numeroMensajesSinLeer);
                if (!numeroMensajesSinLeer.equalsIgnoreCase("0")) {
                  //  badge.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHandleMessageReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        primeraNotificacion = true;
        actividadAbierta = true;
        ConversationsSqlite accesoDatosConversation = new ConversationsSqlite(miContext);
        numeroMensajesSinLeer = String.valueOf(accesoDatosConversation.obtenerNumeroMensajesSinLeer());
        //badge = new BadgeView(miContext, tabs);
        //badge.setText(numeroMensajesSinLeer);
        if(!numeroMensajesSinLeer.equalsIgnoreCase("0")){
          //  badge.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        actividadAbierta = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_contenedora, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        //Toast.makeText(miContext, String.valueOf(position),Toast.LENGTH_SHORT).show();
    }



    public class RecuperarNumeroMensajesSinLeer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //getNumeroMensajes();
            return null;
        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            // Oculto la ventana de espera de conexión
            if(!numeroMensajesSinLeer.equalsIgnoreCase("0")){
                //badge = new BadgeView(miContext, tabs);
                //badge.setText(numeroMensajesSinLeer);
                //badge.show();
                if(primeraNotificacion){
                    primeraNotificacion = false;
                    //thread = new RecuperarNumeroMensajesSinLeer();
                    //thread.execute();
                }
            }
        }
    }

    public void getNumeroMensajes(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("conversations");

        try {
            Map<String, Object> stringMap = new HashMap<>();
            stringMap.put("action", "get_new_message_flags_count");
            stringMap.put("session_key", sessionKey);
            stringMap.put("app", Utils.app);
            JSONObject datos = Post.getServerData(stringMap,"POST",url);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(resultado.equalsIgnoreCase("true")){
                    JSONObject data = datos.getJSONObject("data");
                    numeroMensajesSinLeer = data.getString("count");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Este es el codigo que se ejecutara cuando recibamos la notificacion push y este abierto
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            primeraNotificacion = true;
            //thread = new RecuperarNumeroMensajesSinLeer();
            //thread.execute();
        }
    };

    public void llamarRecuperarMensajes(){
        Intent refresh = new Intent(miContext, TabContenedoraActivity.class);
        startActivity(refresh);//Start the same Activity
        finish(); //finis
    }
}