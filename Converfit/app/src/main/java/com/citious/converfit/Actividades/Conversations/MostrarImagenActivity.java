package com.citious.converfit.Actividades.Conversations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import com.citious.converfit.R;

public class MostrarImagenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_imagen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mostrar_imagen_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        WebView miWebView = (WebView) findViewById(R.id.photo_webView);
        miWebView.getSettings().setSupportZoom(true);
        miWebView.getSettings().setBuiltInZoomControls(true);
        miWebView.getSettings().setDisplayZoomControls(false);
        miWebView.getSettings().setUseWideViewPort(true);
        miWebView.setInitialScale(1);
        miWebView.getSettings().setLoadWithOverviewMode(true);

        String html="<html><body class='text-align:center; margin:10px'>" +
                "<img style='width:100%;' src='{IMAGE_URL}' />" +
                "</body></html>";
        String urlImagen = getIntent().getStringExtra("url_imagen");

        // Use image for the img src parameter in your html and load to webview
        html = html.replace("{IMAGE_URL}", urlImagen);
        miWebView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mostrar_imagen, menu);
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
                setResult(MostrarImagenActivity.RESULT_OK, miIntent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
