package com.citious.converfit.Actividades.Settings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.citious.converfit.R;

public class ProfileMenuActivity extends ActionBarActivity {

    Context miContext;
    ListView miListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_menu);

        //Activamos que muestre la flecha atras
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_menu_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        miContext = this;

        String[] datosAdapter = getResources().getStringArray(R.array.profile_menu);
        miListView = (ListView) findViewById(R.id.lstProfileMenu);
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(miContext,android.R.layout.simple_expandable_list_item_1,datosAdapter);
        miListView.setAdapter(adaptador);

        miListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent miPersonalDataIntent = new Intent(miContext,PersonalDataActivity.class);
                    startActivity(miPersonalDataIntent);
                } else if (position == 1) {
                    Intent miPasswordIntent = new Intent(miContext, PasswordMenuActivity.class);
                    startActivity(miPasswordIntent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent miIntent = new Intent();
                setResult(ProfileMenuActivity.RESULT_OK, miIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
