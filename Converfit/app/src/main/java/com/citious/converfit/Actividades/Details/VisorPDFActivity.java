package com.citious.converfit.Actividades.Details;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;

public class VisorPDFActivity extends ActionBarActivity {

    Context miContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visor_pdf);
        miContext = this;

        String titulo = getIntent().getStringExtra("titulo");
        String messageKey = getIntent().getStringExtra("messageKey");
        String urlPdf = Utils.devolverURLservidor("pdf").concat(messageKey).concat(".pdf");

        //Activamos que muestre la flecha atras
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_visor_pdf_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(titulo);

        WebView webView=new WebView(miContext);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //---you need this to prevent the webview from
        // launching another browser when a url
        // redirection occurs---
        webView.setWebViewClient(new Callback());

        webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + urlPdf);

        setContentView(webView);

    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_visor_pd, menu);
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
                setResult(VisorPDFActivity.RESULT_OK, miIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
