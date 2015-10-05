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

public class PasswordMenuActivity extends ActionBarActivity {

    Context miContext;
    ListView miListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.password_menu_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        miContext = this;

        miContext = this;
        String[] datosAdapter = getResources().getStringArray(R.array.password_menu);
        miListView = (ListView) findViewById(R.id.lstPasswordMenu);
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(miContext,android.R.layout.simple_expandable_list_item_1,datosAdapter);
        miListView.setAdapter(adaptador);

        miListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent miChangePass = new Intent(miContext, ChangePasswordActivity.class);
                    startActivity(miChangePass);
                } else {
                    Intent miRecoverPass = new Intent(miContext, RecoverPassordAcitivy.class);
                    startActivity(miRecoverPass);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password_menu, menu);
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
                setResult(PasswordMenuActivity.RESULT_OK, miIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
