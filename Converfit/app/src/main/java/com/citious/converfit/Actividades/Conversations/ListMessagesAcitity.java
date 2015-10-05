package com.citious.converfit.Actividades.Conversations;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.citious.converfit.AccesoDatos.Conexion;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.AccesoDatos.Sqlite.MessageSqlite;
import com.citious.converfit.Actividades.Details.MyCustomDialog;
import com.citious.converfit.Adapters.ListMessagesAdapter;
import com.citious.converfit.Contenedores.TabContenedoraActivity;
import com.citious.converfit.Models.MensajeModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.citious.converfit.Utils.UtilidadesGCM.*;

public class ListMessagesAcitity extends ActionBarActivity {

    Context miContext;
    ListView miListView;
    EditText miEditText;
    ImageButton miCameraButton;
    ProgressDialog pd;
    CrearMensaje threadCrear;
    RecuperarMensajes thread;
    String tituloAlert = "";
    String mensajeError = "";
    int offSet = 0;
    int limit = 1000;
    ArrayList<MensajeModel> miMensajesLista = new ArrayList<>();
    ArrayList<MensajeModel> miMensajesListaPaginada = new ArrayList<>();
    boolean conversacionNueva = true;
    String conversationKey = "";
    String brandName;
    String userKey;
    boolean mensajeEnviadoOk = false;
    String tipo = "";
    String contenido = "";
    boolean esPrimeraVez = true;
    private static int TAKE_PICTURE = 1;
    private static int GALLERY_PICTURE = 2;
    ViewGroup header;
    boolean mostrarTopLista = false;
    boolean isPush = false;
    boolean fotoHecha = false;
    boolean vieneElegibleFavorites = false;
    int mostrarCargando = 0;
    ListMessagesAdapter miAdapter;
    MessageSqlite accesoDatos;
    boolean cambiarConversationFlag = false;
    boolean needUpdate = false;
    int indicePaginado = 0;
    int tempMessageKey = 0;
    String messageKeyServidor = "";
    boolean desloguear = false;
    boolean erroEnviarMensjaje = false;
    boolean mostrarGooglePlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_messages_acitity);
        miContext = this;
        //Obtenemos la clave que pasamos mediante el intent
        conversationKey = getIntent().getStringExtra("conversationKey");
        if(conversationKey != null ){
            conversacionNueva = false;
            ConversationsSqlite accesoDatosConversations = new ConversationsSqlite(miContext);
            if(accesoDatosConversations.hayMensajesSinLeer(conversationKey)) {
                new Utils().updateFlagValue(miContext, conversationKey);
                accesoDatosConversations.updateConversationFlag(conversationKey);
            }
        }
        brandName = getIntent().getStringExtra("brandName");
        userKey = getIntent().getStringExtra("userkey");


        //Comprobamos si viene de la lista de empresas o no
        vieneElegibleFavorites = getIntent().getBooleanExtra("elegibleFavoritesOrigin",false);

        //Activamos que muestre la flecha atras
        Toolbar toolbar = (Toolbar) findViewById(R.id.up_list_messages_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        establecerTituloCabecera();

        miEditText = (EditText) findViewById(R.id.edt_texto_mensaje_list_messages);
        miEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    tipo = "text";
                    contenido = miEditText.getText().toString();
                    if (contenido.trim().length() > 0) {
                        if (Conexion.isInternetAvailable(miContext)) {
                            if (esPrimeraVez) {
                                threadCrear = new CrearMensaje();
                                threadCrear.execute();
                                esPrimeraVez = false;
                            }
                        } else {
                            mensajeError = getResources().getString(R.string.conexion_error);
                            mostrarAlerta();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        miCameraButton = (ImageButton) findViewById(R.id.btnimg_camara_list_messages);
        miCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] options = getResources().getStringArray(R.array.camera_options_menu);
                AlertDialog.Builder builder = new AlertDialog.Builder(miContext);
                builder.setTitle(getResources().getString(R.string.elegir_opcion));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals(options[0])) {
                            tomarFoto();
                        } else if (options[item].equals(options[1])) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, GALLERY_PICTURE);
                        }
                    }
                });
                builder.show();
            }
        });


        miListView = (ListView)findViewById(R.id.lstListMessages);

        LayoutInflater inflater = getLayoutInflater();
        header = (ViewGroup) inflater.inflate(R.layout.header_listas, miListView, false);
        Button cargarMas = (Button) header.findViewById(R.id.btn_mas_mensajes);
        cargarMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarBotonMasMensajes(true);
            }
        });


        accesoDatos = new MessageSqlite(miContext);
        miMensajesLista = accesoDatos.devolverMessages(conversationKey);
        tempMessageKey = accesoDatos.devolverMessagesTotales();
        mostrarBotonMasMensajes(false);

        if(Conexion.isInternetAvailable(miContext)) {
            if (!conversacionNueva) {
                thread = new RecuperarMensajes();
                thread.execute();
            }
        }
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
        actividadAbierta = true;
        ocultarTEclado();
        accesoDatos = new MessageSqlite(miContext);
        miMensajesLista = accesoDatos.devolverMessages(conversationKey);
        indicePaginado = 0;
        mostrarBotonMasMensajes(false);
        miAdapter = new ListMessagesAdapter(miContext, miMensajesListaPaginada, miListView, header, miEditText, userKey);
        miListView.setAdapter(miAdapter);
        miListView.setSelection(miMensajesListaPaginada.size() - 1);

    }

    @Override
    protected void onPause() {
        super.onPause();
        actividadAbierta = false;
    }

    public void tomarFoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, TAKE_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ocultarTEclado();
        establecerTituloCabecera();
        if(requestCode == TAKE_PICTURE && resultCode == RESULT_OK){
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            Bitmap photo = BitmapFactory.decodeFile(f.getAbsolutePath(),bitmapOptions);
            enviarImagen(photo);
        }else if(requestCode == GALLERY_PICTURE && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(selectedImage);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap photo = BitmapFactory.decodeStream(bis);
                enviarImagen(photo);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarImagen(Bitmap photo){
        if(Conexion.isInternetAvailable(miContext)) {
            photo = getResizedBitmap(photo, 1200);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 85, stream);

            byte[] byteArray = stream.toByteArray();
            String imageEncode = Base64.encodeToString(byteArray, Base64.DEFAULT);
            tipo = "jpeg_base64";
            contenido = imageEncode;
            fotoHecha = true;
            threadCrear = new CrearMensaje();
            threadCrear.execute();
        }else{
            mensajeError = getResources().getString(R.string.conexion_error);
            mostrarAlerta();
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            if(width > maxSize){
                width = maxSize;
                height = (int) (width / bitmapRatio);
            }
        } else if(height > maxSize) {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_messages_acitity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                if(vieneElegibleFavorites && miMensajesListaPaginada.isEmpty()){
                    Intent miIntent = new Intent();
                    setResult(ListMessagesAcitity.RESULT_OK, miIntent);
                    finish();
                }else {
                    Intent miIntent = new Intent(miContext, TabContenedoraActivity.class);
                    startActivity(miIntent);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(vieneElegibleFavorites && miMensajesListaPaginada.isEmpty()){
                Intent miIntent = new Intent();
                setResult(ListMessagesAcitity.RESULT_OK, miIntent);
                finish();
            }else {
                Intent miIntent = new Intent(miContext, TabContenedoraActivity.class);
                startActivity(miIntent);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void ocultarTEclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void mostrarAlerta(){
        ocultarTEclado();
        MyCustomDialog miConstructor = new MyCustomDialog(miContext, tituloAlert, mensajeError);
        String tituloBoton = getResources().getString(R.string.aceptar_alert);
        mostrarGooglePlay = false;
        if(mensajeError.equalsIgnoreCase(getResources().getString(R.string.app_version_error))){
            mostrarGooglePlay = true;
            tituloBoton = getResources().getString(R.string.google_play);
        }
        // Definimos el botón y sus acciones
        AlertDialog dialog = miConstructor.setNegativeButton(tituloBoton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mensajeError = "";
                dialog.cancel();// se cancela la ventana
                if (desloguear) {
                    desloguear = false;
                    Utils.desLoguear(miContext);
                    finish();
                }else if(mostrarGooglePlay){
                    final String appPackageName = getPackageName();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + appPackageName));
                    startActivity(intent);
                }
            }
        }).show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(16);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Rojo));
    }

    public class RecuperarMensajes extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            getInfoServidor();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!conversationKey.isEmpty() && miMensajesLista.isEmpty() && !isPush) {
                DialogInterface.OnCancelListener dialogCancel = new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(miContext, getResources().getString(R.string.errorServidor), Toast.LENGTH_SHORT).show();
                    }
                };
                pd = ProgressDialog.show(miContext, getResources().getString(R.string.buscarServidor), getResources().getString(R.string.buscando), true, true, dialogCancel);
                pd.setCanceledOnTouchOutside(false);
            }
        }

        //Se ejecuta cuando se cancela el hilo
        @Override
        protected void onCancelled() {
            super.onCancelled();
            pd.cancel();
        }

        //Se ejecuta cuando se realiza la llamada publishProgress()
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }
        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            // Oculto la ventana de espera de conexión
            if(pd != null && pd.isShowing()){
                pd.dismiss();
            }
            if(mensajeError.length() > 0){
                mostrarAlerta();
            }else {
                if(cambiarConversationFlag) {
                    cambiarConversationFlag = false;
                    ConversationsSqlite accesoDatosConversations = new ConversationsSqlite(miContext);
                    new Utils().updateFlagValue(miContext, conversationKey);
                    accesoDatosConversations.updateConversationFlag(conversationKey);
                }
                if(needUpdate) {
                    miMensajesLista = accesoDatos.devolverMessages(conversationKey);
                    indicePaginado = 0;
                    mostrarBotonMasMensajes(false);
                    miAdapter = new ListMessagesAdapter(miContext, miMensajesListaPaginada, miListView, header, miEditText, userKey);
                    miListView.setAdapter(miAdapter);
                    if (!mostrarTopLista) {
                        miListView.setSelection(miMensajesListaPaginada.size() - 1);
                        mostrarTopLista = false;
                    }
                }
            }
        }
    }

    public void getInfoServidor(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("conversations");

        ConversationsSqlite accesoDatosConversation = new ConversationsSqlite(miContext);
        String lastUpdate = accesoDatosConversation.devolverLastUpdateConversacion(conversationKey);

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "list_messages"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("conversation_key", conversationKey));
        pairs.add(new BasicNameValuePair("last_update", lastUpdate));
        pairs.add(new BasicNameValuePair("offset", String.valueOf(offSet)));
        pairs.add(new BasicNameValuePair("limit", String.valueOf(limit)));
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
                    needUpdate = data.getBoolean("need_to_update");
                    if(needUpdate) {
                        accesoDatos.borrarMensajesConversacion(conversationKey);
                        String lastUp = data.getString("last_update");
                        accesoDatosConversation.modificarLastUpdateConversacion(conversationKey,lastUp);
                        JSONArray listMessage = data.getJSONArray("messages");
                        for (int indice = 0; indice < listMessage.length(); indice++) {
                            MensajeModel mensaje = new MensajeModel(listMessage.getJSONObject(indice), conversationKey);
                            accesoDatos.insertarMessage(mensaje);
                        }
                    }
                }else{
                    String codigoError = datos.getString("error_code");
                    desloguear = Utils.comprobarDesloguear(codigoError);
                    String[] error = new Utils().devolverStringError(miContext,codigoError);
                    tituloAlert = error[0];
                    mensajeError = error[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String codigoError = getResources().getString(R.string.default_error);
            String[] error = new Utils().devolverStringError(miContext,codigoError);
            tituloAlert = error[0];
            mensajeError = error[1];
        }
    }

    public class CrearMensaje extends AsyncTask<String , Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            String aContent = "";
            String aType = "";
            if(conversacionNueva){
                crearConversacion();
            }else{
                if(!erroEnviarMensjaje) {
                    String fecha = new Utils().crearFechaUnixActual();
                    String hora = accesoDatos.horaUltimoMensajeEnviado(miContext, conversationKey);
                    if (hora.length() > 0) {
                        if (Integer.parseInt(fecha) < Integer.parseInt(hora)) {
                            int fechaInt = Integer.parseInt(hora) + 3;
                            fecha = String.valueOf(fechaInt);
                        }
                    }
                    tempMessageKey += 1;
                    aContent = contenido;
                    aType = tipo;
                    accesoDatos.addMensaje(String.valueOf(tempMessageKey), contenido, fecha, "brand", tipo, conversationKey, true, Utils.obtenerFnameLogin(miContext), Utils.obtenerLnameLogin(miContext));
                }else{
                    aContent = params[0];
                    aType= params[1];
                }
                enviarMensaje();
            }
            return new String[] {aContent, aType};
        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(String[] params) {
            mostrarCargando = 1;
            offSet = 0;
            if(mensajeError.length() > 0){
                mostrarAlerta();
                miEditText.setText("");
                indicePaginado = 0;
                miMensajesLista = accesoDatos.devolverMessages(conversationKey);
                mostrarBotonMasMensajes(false);
                esPrimeraVez = true;
            }else {
                // Oculto la ventana de espera de conexión
                if (conversacionNueva) {
                    conversacionNueva = false;
                    threadCrear = new CrearMensaje();
                    threadCrear.execute();
                } else if (mensajeEnviadoOk) {
                    UpdateMessage updateThread = new UpdateMessage();
                    updateThread.execute(messageKeyServidor,params[0], params[1]);
                    miEditText.setText("");
                }
            }
        }
    }

    public void crearConversacion(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("conversations");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "add_conversation"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("user_key", userKey));
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
                    JSONObject dataResultado = datos.getJSONObject("data");
                    conversationKey = dataResultado.getString("conversation_key");
                    String conversationsLastUpdate = dataResultado.getString("conversations_last_update");
                    //Utils.guardarConversationsLastUpdate(miContext,conversationsLastUpdate);
                }else{
                    String codigoError = datos.getString("error_code");
                    desloguear = Utils.comprobarDesloguear(codigoError);
                    String[] error = new Utils().devolverStringError(miContext,codigoError);
                    tituloAlert = error[0];
                    mensajeError = error[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String codigoError = getResources().getString(R.string.default_error);
            String[] error = new Utils().devolverStringError(miContext,codigoError);
            tituloAlert = error[0];
            mensajeError = error[1];
        }
    }

    public void enviarMensaje(){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("conversations");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "add_message"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("conversation_key", conversationKey));
        pairs.add(new BasicNameValuePair("type", "premessage"));
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
                    mensajeEnviadoOk = true;
                    JSONObject dataResultado = datos.getJSONObject("data");
                    messageKeyServidor = dataResultado.getString("message_key");
                    accesoDatos.updateMessageKey(conversationKey, String.valueOf(tempMessageKey), messageKeyServidor);
                }else{
                    String codigoError = datos.getString("error_code");
                    desloguear = Utils.comprobarDesloguear(codigoError);
                    String[] error = new Utils().devolverStringError(miContext, codigoError);
                    tituloAlert = error[0];
                    mensajeError = error[1];
                    accesoDatos.updateEnviado(conversationKey, String.valueOf(tempMessageKey), false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String codigoError = getResources().getString(R.string.default_error);
            String[] error = new Utils().devolverStringError(miContext,codigoError);
            tituloAlert = error[0];
            mensajeError = error[1];
        }
    }

    public class UpdateMessage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String ... params) {
            String messageKey = params[0];
            String content = params[1];
            String type = params[2];
            updateMessage(messageKey, content, type);
            return null;
        }

        //Se ecuta al finalizar el thread
        @Override
        protected void onPostExecute(Void result) {
            // Oculto la ventana de espera de conexión
            if(mensajeError.length() > 0){
                mostrarAlerta();
            }
            indicePaginado = 0;
            miMensajesLista = accesoDatos.devolverMessages(conversationKey);
            mostrarBotonMasMensajes(false);
            if (miAdapter == null) {
                miAdapter = new ListMessagesAdapter(miContext, miMensajesListaPaginada, miListView, header, miEditText, userKey);
                miListView.setAdapter(miAdapter);
                miListView.removeHeaderView(header);
            }
            esPrimeraVez = true;
        }
    }

    public void updateMessage(String messageKey, String content, String type){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = Utils.devolverURLservidor("conversations");

        List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "update_message"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("conversation_key", conversationKey));
        pairs.add(new BasicNameValuePair("message_key", messageKey));
        pairs.add(new BasicNameValuePair("content", content));
        pairs.add(new BasicNameValuePair("type", type));
        pairs.add(new BasicNameValuePair("app", Utils.app));

        Post post = new Post();
        try {
            JSONObject datos = post.getServerData(url, pairs);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(resultado.equalsIgnoreCase("true")){
                    Utils.dbErrorContador = 0;

                }else{
                    Utils.listaMessagesKeyFallidos.add(messageKey);
                    accesoDatos.updateEnviado(conversationKey, messageKey, false);
                    String codigoError = datos.getString("error_code");
                    desloguear = Utils.comprobarDesloguear(codigoError);
                    if(desloguear) {
                        String[] error = new Utils().devolverStringError(miContext, codigoError);
                        tituloAlert = error[0];
                        mensajeError = error[1];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String codigoError = miContext.getResources().getString(R.string.default_error);
            String[] error = new Utils().devolverStringError(miContext,codigoError);
            tituloAlert = error[0];
            mensajeError = error[1];
        }
    }

    //Este es el codigo que se ejecutara cuando recibamos la notificacion push y este abierto
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!conversacionNueva) {
                if (conversationKey.equalsIgnoreCase(Utils.pushConversationKey)) {
                    cambiarConversationFlag = true;
                    offSet = 0;
                    isPush = true;
                    miMensajesLista.clear();
                    miMensajesListaPaginada.clear();
                    isPush = true;
                    thread = new RecuperarMensajes();
                    thread.execute();
                }
            }
        }
    };

    private void mostrarBotonMasMensajes(boolean conservarPosicion){
        indicePaginado += 1;
        miListView.removeHeaderView(header);
        if(miMensajesLista.size() > (indicePaginado * 20)){
            miMensajesListaPaginada.clear();
            int indiceFinal = (indicePaginado) * 20;
            if(indiceFinal < miMensajesLista.size()){
                miListView.addHeaderView(header);
                miMensajesListaPaginada = new ArrayList<>(miMensajesLista.subList(0, indiceFinal));
            }else{
                miListView.removeHeaderView(header);
                miMensajesListaPaginada = miMensajesLista;
            }
        }else{
            miListView.removeHeaderView(header);
            miMensajesListaPaginada = miMensajesLista;
        }
        Collections.reverse(miMensajesListaPaginada);
        miAdapter = new ListMessagesAdapter(miContext, miMensajesListaPaginada, miListView, header, miEditText, userKey);
        miListView.setAdapter(miAdapter);
        miAdapter.notifyDataSetChanged();
        if(!conservarPosicion) {
            miListView.setSelection(miMensajesListaPaginada.size() - 1);
        }
    }

    public void reenviarMensajes(final Context miContexto, final String converKey, String anUserKey, ListView aListView, ViewGroup miHeader, EditText edt){
        //Debemos hacer esta parte para evitar que los datos sean null
        miListView = aListView;
        header = miHeader;
        miContext = miContexto;
        miEditText = edt;
        conversationKey = converKey;
        userKey = anUserKey;
        ///////////////////
        final String[] opcionesMensajesFallidos = miContexto.getResources().getStringArray(R.array.opciones_reenviar_mensaje);
        AlertDialog.Builder miContructor = new AlertDialog.Builder(miContexto);

        miContructor.setItems(opcionesMensajesFallidos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int posicion) {
                switch (posicion) {
                    case 0:
                        retryMensajesFallidos(miContexto, converKey);
                        break;
                    case 1:
                        borrarMensajesFallidos(miContexto, converKey);
                        break;
                    default:
                        break;
                }
            }
        });

        AlertDialog miAlerta = miContructor.create();
        miAlerta.show();
    }

    //metodo para reenviar los mensajes fallidos
    private void retryMensajesFallidos(Context miContexto, String converKey){
        accesoDatos = new MessageSqlite(miContexto);
        ArrayList<MensajeModel> miListaMensajeFallidos = accesoDatos.devolverMessagesFallidos(miContexto, converKey, false);
        for (MensajeModel mensajeFallido : miListaMensajeFallidos) {
            accesoDatos.updateEnviado(converKey,mensajeFallido.getMessageKey(),true);
            String fecha = new Utils().crearFechaUnixActual();
            String hora = accesoDatos.horaUltimoMensajeEnviado(miContext, conversationKey);
            if(hora.length() > 0) {
                if (Integer.parseInt(fecha) < Integer.parseInt(hora)) {
                    int fechaInt = Integer.parseInt(hora) + 3;
                    fecha = String.valueOf(fechaInt);
                }
            }
            accesoDatos.updateMessageCreation(converKey,mensajeFallido.getMessageKey(),fecha);
            miMensajesLista = accesoDatos.devolverMessages(converKey);
            mostrarBotonMasMensajes(false);
            boolean updateMensaje = false;
            int posicion = 0;
            for(int i = 0; i < Utils.listaMessagesKeyFallidos.size(); i++){
                if(mensajeFallido.getMessageKey().equalsIgnoreCase(Utils.listaMessagesKeyFallidos.get(i))){
                    updateMensaje = true;
                    posicion = i;
                    i = Utils.listaMessagesKeyFallidos.size();
                }
            }
            //Ponemos el errorEnviarMensaje a true para que no se dupliquen los datos en SQlite
            erroEnviarMensjaje = true;
            if(updateMensaje){
                conversationKey = mensajeFallido.getConversationKey();
                UpdateMessage updThread = new UpdateMessage();
                updThread.execute(mensajeFallido.getMessageKey(), mensajeFallido.getContent(), mensajeFallido.getType());
                Utils.listaMessagesKeyFallidos.remove(posicion);
            }else{
                tipo = mensajeFallido.getType();
                if(conversationKey != null ) {
                    conversacionNueva = false;
                }
                tempMessageKey = Integer.parseInt(mensajeFallido.getMessageKey());
                CrearMensaje crearMensajeThread = new CrearMensaje();
                crearMensajeThread.execute(mensajeFallido.getContent(), mensajeFallido.getType());
                accesoDatos.addMensaje(mensajeFallido.getMessageKey(),contenido, fecha, "user", tipo, conversationKey, true, Utils.obtenerFnameLogin(miContext), Utils.obtenerLnameLogin(miContext));
            }
        }
    }

    //Metodo para borrar los mensajes fallidos
    private void borrarMensajesFallidos(Context miContexto, String converKey){
        accesoDatos = new MessageSqlite(miContexto);
        ArrayList<MensajeModel> miListaMensajeFallidos = accesoDatos.devolverMessagesFallidos(miContexto, converKey, false);
        for (MensajeModel mensajeFallido : miListaMensajeFallidos) {
           accesoDatos.borrarMensajesFallidos(mensajeFallido.getConversationKey(), mensajeFallido.getMessageKey());
        }

        miMensajesLista = accesoDatos.devolverMessages(converKey);
        mostrarBotonMasMensajes(false);
    }


    //Metodo para establecer el titulo de la cabecera
    private void establecerTituloCabecera(){
        getSupportActionBar().setTitle(brandName);
    }

    //Metodo para llamar al WS para cambiar el admin asignado
    public class CambiarAdminAsignado extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            cambiaAdmin(params[0]);
            return null;
        }

        public void cambiaAdmin(int asignado) {
            String url = Utils.devolverURLservidor("conversations");
            String sessionKey = Utils.obtenerSessionKey(miContext);

            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("action", "assign_conversation"));
            pairs.add(new BasicNameValuePair("session_key", sessionKey));
            pairs.add(new BasicNameValuePair("conversation_key", conversationKey));
            pairs.add(new BasicNameValuePair("assign", String.valueOf(asignado)));
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
}
