package com.citious.converfit.Actividades.ChatWeb;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.citious.converfit.AccesoDatos.Sqlite.TimeLineSqlite;
import com.citious.converfit.Adapters.UserTimeLineAdapter;
import com.citious.converfit.Contenedores.TabContenedoraActivity;
import com.citious.converfit.Models.TimeLineModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;

import java.util.ArrayList;

public class UserTimeLine extends ActionBarActivity {

    Context miContext;
    TimeLineSqlite accesoDatos;
    ArrayList<TimeLineModel> miUserPostList = new ArrayList<>();

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

        String userKey = getIntent().getStringExtra("userkey");
        miUserPostList = accesoDatos.devolverAllUserPost(userKey);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_time_line_recycled);
        UserTimeLineAdapter adapter = new UserTimeLineAdapter(miContext, miUserPostList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(miContext));
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
        }
        return super.onOptionsItemSelected(item);
    }
}
