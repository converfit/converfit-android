package com.citious.converfit.Actividades.Settings;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.Adapters.ListMenuSettingsAdapters;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsMenuFragment extends Fragment {

    Context miContext;
    ListView miListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_settings_menu, container, false);

        setHasOptionsMenu(true);
        miContext = getActivity();

        String[] datosAdapter = getResources().getStringArray(R.array.settings_menu);
        ArrayList<String> misOpcionesMenu = new ArrayList<>();
        misOpcionesMenu.add(datosAdapter[0]);
        misOpcionesMenu.add(datosAdapter[1]);


        miListView = (ListView) v.findViewById(R.id.lstSettingsMenu);
        //ArrayAdapter<String> adaptador = new ArrayAdapter<>(miContext,android.R.layout.simple_expandable_list_item_1,datosAdapter);
        ListMenuSettingsAdapters miAdapter = new ListMenuSettingsAdapters(miContext, misOpcionesMenu);
        miListView.setAdapter(miAdapter);

        miListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent miProfileMenu = new Intent(miContext,ProfileMenuActivity.class);
                    startActivity(miProfileMenu);
                } else {
                    DesLoguear thread = new DesLoguear();
                    thread.execute();
                    Utils.desLoguear(miContext);
                    getActivity().finish();
                }
            }
        });

        return v;
    }

    public class DesLoguear extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getInfoServidor();
            return null;
        }

        public void getInfoServidor() {
            String url = Utils.devolverURLservidor("access");
            String sessionKey = Utils.obtenerSessionKey(miContext);

            try {
                Map<String, Object> stringMap = new HashMap<>();
                stringMap.put("action", "logout");
                stringMap.put("session_key", sessionKey);
                stringMap.put("app", Utils.app);
                JSONObject datos = Post.getServerData(stringMap,"POST",url);
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
}
