package com.citious.converfit.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import com.citious.converfit.AccesoDatos.Post;
import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.AccesoDatos.Sqlite.MessageSqlite;
import com.citious.converfit.AccesoDatos.Sqlite.TimeLineSqlite;
import com.citious.converfit.AccesoDatos.Sqlite.UserSqlite;
import com.citious.converfit.Actividades.UserAcces.WelcomeActivity;
import com.citious.converfit.R;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    static public String  avatarString = "";
    static public String appVersion = "1.0.0";
    static public final String app = "converfit";
    static public int numeroFavorites = 0;
    static public final String SISTEMA_STRING = "android";
    static public final String NOMBREPREF_STRING = "NombrePref";
    static public final String SESSIONKEY_STRING = "sessionKey";
    static public final String LASTUPDATE_STRING = "last_update";
    static public final String FAVORITOLASTUPDATE_STRING = "last_update_favorito";
    static public final String CONVERSATIONSLASTUPDATE_STRING = "conversations_last_update";
    static public final String DEVICETOKENKEY_STRING = "deviceToken";
    static public final String IDLOGIN_STRING = "id_login";
    static public final String FNAMELOGIN_STRING = "fname";
    static public final String LNAMELOGIN_STRING = "lname";
    static public final String GROUPSUBBRAND_STRING = "group_subbrands";
    static public final String BRAND_NOTIFICATIONS_LAST_UPDATE = "brands_notifications_last_update";
    static public final String WEBCHAT_STATUS = "web_chat_status";
    static public String pushConversationKey = "";
    static public String errorCheckSession = "";
    static public boolean bloquearSistema = false;
    static public int dbErrorContador = 0;
    static public boolean favoritesPrimeraVez = true;
    static public boolean conversationsPrimeraVez = true;
    static public boolean vieneUserTimeLine = false;
    static public ArrayList<String> listaMessagesKeyFallidos  = new ArrayList<>();

    private static final String PATTERN_EMAIL = "^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    //Metodo que comprueba si el formato de email es correcto
    public boolean isValidEmail(String email){
        boolean isValid = false;
        if(email.length() < 155){
            // Compiles the given regular expression into a pattern.
            Pattern pattern = Pattern.compile(PATTERN_EMAIL);
            // Match the given input against this pattern
            Matcher matcher = pattern.matcher(email);
            isValid = matcher.matches();
        }
        return isValid;
    }

    public String crearFechaUnixActual(){

        //el dia, el mes y el año de la fecha actual
        long unixTime = System.currentTimeMillis() / 1000L;

        return String.valueOf(unixTime);
    }

    public String devolverFecha(Context miContexto, String fechaUnix){
        String fechaRetorno;

        Long fechaLong = Long.parseLong(fechaUnix);

        Calendar mydate = Calendar.getInstance();
        mydate.setTimeInMillis(fechaLong * 1000);

        int minutos, horas,dia, mes, year;

        //Extraemos la hora, los minutos, el dia, el mes y el año de la fecha que nos pasaron
        minutos = mydate.get(Calendar.MINUTE);
        horas = mydate.get(Calendar.HOUR_OF_DAY);
        dia = mydate.get(Calendar.DAY_OF_MONTH);
        mes = mydate.get(Calendar.MONTH) + 1;
        year = mydate.get(Calendar.YEAR);

        //el dia, el mes y el año de la fecha actual
        int diaActual, mesActual, yearActual;
        Calendar now = Calendar.getInstance();
        yearActual = now.get(Calendar.YEAR);
        mesActual = now.get(Calendar.MONTH) + 1; // Note: zero based!
        diaActual = now.get(Calendar.DAY_OF_MONTH);

        if((diaActual == dia) && (mesActual == mes) && (yearActual == year)){
            if(minutos <= 9){
                fechaRetorno = String.valueOf(horas).concat(":").concat("0").concat(String.valueOf(minutos));
            }else {
                fechaRetorno = String.valueOf(horas).concat(":").concat(String.valueOf(minutos));
            }
        }else if((diaActual - dia == 1) && (mesActual == mes) && (yearActual == year) ){
            fechaRetorno = miContexto.getString(R.string.ayer);
        }else{
            fechaRetorno = String.valueOf(dia).concat("/").concat(String.valueOf(mes)).concat("/").concat(String.valueOf(year));
        }
        return fechaRetorno;
    }
    //Metodo que nos devolvera un String con el texo del error corresponiente al codigo de error recibido
    public String[] devolverStringError(Context miContexto, String codigoError){
        String mensaje;
        String titulo = miContexto.getString(R.string.error);
        switch (codigoError){
            case "system_closed":
                mensaje = miContexto.getString(R.string.system_closed);
                break;
            case "db_connection_error":
                mensaje = miContexto.getString(R.string.db_connection_error);
                break;
            case "session_key_not_valid":
                mensaje = miContexto.getString(R.string.session_key_not_valid);
                break;
            case "input_data_missing":
                mensaje = miContexto.getString(R.string.input_data_missing);
                break;
            case "email_in_use":
                mensaje = miContexto.getString(R.string.email_in_use);
                break;
            case "email_not_in_use":
                mensaje = miContexto.getString(R.string.email_not_in_use);
                break;
            case "email_not_valid":
                mensaje = miContexto.getString(R.string.email_not_valid);
                break;
            case "email_or_password_not_valid":
                titulo = miContexto.getString(R.string.email_password_not_valid);
                mensaje = miContexto.getString(R.string.email_or_password_not_valid);
                break;
            case "old_password_not_valid":
                mensaje = miContexto.getString(R.string.old_password_not_valid);
                break;
            case "list_conversations_empty":
                mensaje = miContexto.getString(R.string.list_conversations_empty);
                break;
            case "list_brands_empty":
                mensaje = miContexto.getString(R.string.list_brands_empty);
                break;
            case "list_users_empty":
                mensaje = miContexto.getString(R.string.list_favorites_empty);
                break;
            case "brand_username_not_valid":
                mensaje = miContexto.getString(R.string.brand_username_not_valid);
                break;
            case "brand_not_active":
                mensaje = miContexto.getString(R.string.brand_not_active);
                break;
            case "brand_in_user_favorites":
                mensaje = miContexto.getString(R.string.brand_in_user_favorites);
                break;
            case "brand_not_in_user_favorites":
                mensaje = miContexto.getString(R.string.brand_not_in_user_favorites);
                break;
            case "user_not_active":
                mensaje = miContexto.getString(R.string.user_not_active);
                break;
            case "list_messages_empty":
                mensaje = miContexto.getString(R.string.list_messages_empty);
                break;
            case "conversation_permission_denied":
                mensaje = miContexto.getString(R.string.conversation_permission_denied);
                break;
            case "conversation_not_active":
                mensaje = miContexto.getString(R.string.conversation_not_active);
                break;
            case "campos_vacios":
                mensaje = miContexto.getString(R.string.campos_vacios);
                break;
            case "formato_email":
                mensaje = miContexto.getString(R.string.formato_email);
                break;
            case "formato_nombre":
                mensaje = miContexto.getString(R.string.formato_nombre);
                break;
            case "formato_apellidos":
                mensaje = miContexto.getString(R.string.formato_apellidos);
                break;
            case "formato_contraseña":
                mensaje = miContexto.getString(R.string.formato_contraseña);
                break;
            case "pass_actual":
                mensaje = miContexto.getString(R.string.pass_actual);
                break;
            case "pass_nueva":
                mensaje = miContexto.getString(R.string.pass_nueva);
                break;
            case "errorLLamada":
                mensaje = miContexto.getString(R.string.errorLLamada);
                break;
            case "guardarOk":
                mensaje = miContexto.getString(R.string.cambios_realizados);
                break;
            case "version_not_valid":
                titulo = miContexto.getString(R.string.app_update_title);
                mensaje = miContexto.getString(R.string.app_version_error);
                break;
            case "user_blocked":
                mensaje = miContexto.getString(R.string.brand_user_blocked);
                break;
            case "conversation_closed":
                mensaje = miContexto.getString(R.string.conversation_closed);
                break;
            case "conversation_cancelled":
                mensaje = miContexto.getString(R.string.conversation_cancelled);
                break;
            case "admin_not_active":
                mensaje = miContexto.getString(R.string.admin_not_active);
                break;
            case "user_key_not_valid":
                mensaje = miContexto.getString(R.string.user_key_not_valid);
                break;
            default:
                titulo = miContexto.getString(R.string.conexion_error_title);
                mensaje = miContexto.getString(R.string.default_error);
                break;
        }

        return new String[]{titulo, mensaje};
    }

    //Metodo para guardar el sessionKey
    static public void guardarSessionKey(Context miContext, String sessionKey){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(SESSIONKEY_STRING, sessionKey);
        miEditor.apply();
    }

    //Metodo para obtener el sessionKey
    static public String obtenerSessionKey(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(SESSIONKEY_STRING,"asdf");
    }

    //Metodo para borrar el sessionKey
    static public void borrarSessionKey(Context miContext){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.remove(SESSIONKEY_STRING);
        miEditor.apply();
    }

    //Metodo para borrar todos los last updates
    static public void borrarLastUpdates(Context miContext){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.remove(LASTUPDATE_STRING);
        miEditor.remove(FAVORITOLASTUPDATE_STRING);
        miEditor.remove(CONVERSATIONSLASTUPDATE_STRING);
        miEditor.apply();
    }

    //Metodo para guardar el lastUpdate
    static public void guardarLastUpdate(Context miContext, String lastUpdate){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(LASTUPDATE_STRING, lastUpdate);
        miEditor.apply();
    }

    //Metodo para obtener el lastUpdate
    static public String obtenerLastUpdate(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(LASTUPDATE_STRING, "0");
    }

    //Metodo para guardar el favorito_lastUpdate
    static public void guardarFavoritosLastUpdate(Context miContext, String favoritosLastUpdate){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(FAVORITOLASTUPDATE_STRING, favoritosLastUpdate);
        miEditor.apply();
    }

    //Metodo para obtener el favorito_lastUpdate
    static public String obtenerFavoritoLastUpdate(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(FAVORITOLASTUPDATE_STRING, "0");
    }

    //Metodo para guardar el conversations_lastUpdate
    static public void guardarConversationsLastUpdate(Context miContext, String conversationsLastUpdate){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(CONVERSATIONSLASTUPDATE_STRING, conversationsLastUpdate);
        miEditor.apply();
    }

    //Metodo para obtener el converstations_lastUpdate
    static public String obtenerConversationsLastUpdate(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(CONVERSATIONSLASTUPDATE_STRING, "0");
    }


    //Metodo para guardar el deviceKey
    static public void guardarDeviceKey(Context miContext, String deviceKey){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(DEVICETOKENKEY_STRING, deviceKey);
        miEditor.apply();
    }

    //Metodo para obtener el deviceKey
    static public String obtenerDeviceKey(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(DEVICETOKENKEY_STRING, "dont_allow");
    }

    //Funcion para guardar el id del usuario logado
    static public void guardarIdLogin(Context miContext, String id){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(IDLOGIN_STRING, id);
        miEditor.apply();
    }

    //Metodo para obtener el id del usuario logado
    static public String obtenerIdLogin(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(IDLOGIN_STRING, "0");
    }

    //Funcion para guardar el id del usuario logado
    static public void guardarFnameLogin(Context miContext, String fname){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(FNAMELOGIN_STRING, fname);
        miEditor.apply();
    }

    //Metodo para obtener el id del usuario logado
    static public String obtenerFnameLogin(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(FNAMELOGIN_STRING, "");
    }

    //Funcion para guardar el id del usuario logado
    static public void guardarBrandNotificationsLastUpdate(Context miContext, String brandNotificationsLastUpdate){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(BRAND_NOTIFICATIONS_LAST_UPDATE, brandNotificationsLastUpdate);
        miEditor.apply();
    }

    //Metodo para obtener el id del usuario logado
    static public String obtenerBrandNotificationsLastUpdate(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(BRAND_NOTIFICATIONS_LAST_UPDATE, "0");
    }

    //Metodo para guardar si es group_subBrands o no
    static public void guardarGroupSubBrands(Context miContext, String tipoBrand){
        boolean isGroupSubBrand = false;
        if(tipoBrand.equalsIgnoreCase(GROUPSUBBRAND_STRING)){
            isGroupSubBrand = true;
        }
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putBoolean(GROUPSUBBRAND_STRING, isGroupSubBrand);
        miEditor.apply();
    }

    //Metodo para obtener si es un group_subbrand o no
    static public boolean obtenerIsGroupSubBrand(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getBoolean(GROUPSUBBRAND_STRING, false);
    }

    //Funcion para guardar el id del usuario logado
    static public void guardarLnameLogin(Context miContext, String lname){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(LNAMELOGIN_STRING, lname);
        miEditor.apply();
    }

    //Metodo para obtener el id del usuario logado
    static public String obtenerLnameLogin(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(LNAMELOGIN_STRING, "");
    }

    //Funcion para guardar el id del usuario logado
    static public void guardarWebChatStatus(Context miContext, String status){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.putString(WEBCHAT_STATUS, status);
        miEditor.apply();
    }

    //Metodo para obtener el id del usuario logado
    static public String obtenerWebChatStatus(Context miContext){
        SharedPreferences settings = miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        return settings.getString(WEBCHAT_STATUS, "0");
    }

    //Metodo para borrar los datos del usuario logado
    static public void borrarUserLogin(Context miContext){
        SharedPreferences prefCompartidas=miContext.getSharedPreferences(NOMBREPREF_STRING, Context.MODE_PRIVATE);
        SharedPreferences.Editor miEditor= prefCompartidas.edit();
        miEditor.remove(IDLOGIN_STRING);
        miEditor.remove(FNAMELOGIN_STRING);
        miEditor.remove(LNAMELOGIN_STRING);
        miEditor.remove(BRAND_NOTIFICATIONS_LAST_UPDATE);
        //Borramos tb el group_subbrand
        miEditor.remove(GROUPSUBBRAND_STRING);
        miEditor.apply();
    }

    //Metodo para borrar Sqlite
    static public void borrarSqlite(Context miContext){
        UserSqlite accesoDatosUser = new UserSqlite(miContext);
        accesoDatosUser.borrarAllUsers();
        ConversationsSqlite accesoDatosConversations = new ConversationsSqlite(miContext);
        accesoDatosConversations.borrarAllConversations();
        MessageSqlite accesoDatosMessage = new MessageSqlite(miContext);
        accesoDatosMessage.borrarAllMessage();
        TimeLineSqlite accesoDatosTimeLine = new TimeLineSqlite(miContext);
        accesoDatosTimeLine.borrarAllPosts();
    }

    //Metodo que se llama cuando nos desLogueamos
    static public void desLoguear(Context miContext){
        Utils.borrarSessionKey(miContext);
        Utils.borrarLastUpdates(miContext);
        borrarSqlite(miContext);
        //Reseteamos los valores de las primeraVez
        conversationsPrimeraVez = true;
        favoritesPrimeraVez = true;
        ////////////////////////////////////////
        Intent miWelcomeIntent = new Intent(miContext,WelcomeActivity.class);
        miContext.startActivity(miWelcomeIntent);
    }

    //Metodo para comprobar si deslogueamos o no segun el errorCode
    static public boolean comprobarDesloguear(String errorCode){
        boolean desloguear = false;

        switch (errorCode){
            case "session_key_not_valid":
                errorCheckSession = errorCode;
                desloguear = true;
                break;
            case "system_closed":
                errorCheckSession = errorCode;
                desloguear = true;
                bloquearSistema = true;
                break;
            case "version_not_valid":
                bloquearSistema = true;
                errorCheckSession = errorCode;
                desloguear = true;
                break;
            case "db_connection_error":
                dbErrorContador += 1;
                if(dbErrorContador == 5){
                    desloguear = true;
                }
                break;
            case "admin_not_active":
                errorCheckSession = errorCode;
                desloguear = true;
                break;
        }

        return desloguear;
    }

    //Metodo para cambiar el valor de la flag
    public void updateFlagValue(Context miContext, String converstaionKey){
        UpdateConversationFlag thread = new UpdateConversationFlag();
        thread.execute(miContext,converstaionKey);
    }

    //Metodo para updatear el Conversation flag
    public class UpdateConversationFlag extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            Context miContext = (Context) params[0];
            String conversationKey = (String) params[1];
            updateFlag(miContext, conversationKey);
            return null;
        }
    }

    public void updateFlag(Context miContext, String conversationKey){
        String sessionKey = Utils.obtenerSessionKey(miContext);
        String url = devolverURLservidor("conversations");

        /*List<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair("action", "update_conversation_flag"));
        pairs.add(new BasicNameValuePair("session_key", sessionKey));
        pairs.add(new BasicNameValuePair("conversation_key", conversationKey));
        pairs.add(new BasicNameValuePair("new_message_flag", "0"));
        pairs.add(new BasicNameValuePair("app", Utils.app));

        Post post = new Post();
        try {
            JSONObject datos = post.getServerData(url, pairs);*/
        try {
            Map<String, Object> stringMap = new HashMap<>();
            stringMap.put("action", "update_conversation_flag");
            stringMap.put("session_key", sessionKey);
            stringMap.put("conversation_key", conversationKey);
            stringMap.put("new_message_flag", "0");
            stringMap.put("app", Utils.app);
            JSONObject datos = Post.getServerData(stringMap,"POST",url);
            if (datos != null && datos.length() > 0) {
                // Para cada registro obtenido se extraen sus campos
                String resultado = datos.getString("result");
                if(resultado.equalsIgnoreCase("true")){
                    JSONObject data = datos.getJSONObject("data");
                    String lastUpdate = data.getString("last_update");
                    Utils.guardarLastUpdate(miContext,lastUpdate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void comprobarPrimerasVeces(Context miContext){
        //si tenemos lasUpdate en conversaciones no cambiamos el valor de su primera vez
        String conversationsLastUpdate = Utils.obtenerConversationsLastUpdate(miContext);
        if(!conversationsLastUpdate.equalsIgnoreCase("0")){
            Utils.conversationsPrimeraVez = false;
        }

        //si tenemos lasUpdate en favorites no cambiamos el valor de su primera vez
        String favoritesLastUpdate = Utils.obtenerFavoritoLastUpdate(miContext);
        if(!favoritesLastUpdate.equalsIgnoreCase("0")){
            Utils.favoritesPrimeraVez = false;
        }
    }

    //funcion para obtener las url del servidor de los WS entre pruebas y produccion
    public static String devolverURLservidor(String modelo){
        String url = "";
        final String accesoServidor = "pruebas";    // "pruebas" o "produccion"

        switch (accesoServidor){
            case "pruebas":
                final String rutaServidorPruebas = "http://server.converfit.com/";
                if(modelo.equalsIgnoreCase("access")){
                    url = rutaServidorPruebas.concat("android/1.0.0/models/access/model.php");
                }else if(modelo.equalsIgnoreCase("conversations")){
                    url = rutaServidorPruebas.concat("android/1.0.0/models/conversations/model.php");
                }else if(modelo.equalsIgnoreCase("brand_notifications")){
                    url = rutaServidorPruebas.concat("android/1.0.0/models/brand_notifications/model.php");
                }else if(modelo.equalsIgnoreCase("brands")) {
                    url = rutaServidorPruebas.concat("android/1.0.0/models/users/model.php");
                }else if(modelo.equalsIgnoreCase("webchat")){
                    url = rutaServidorPruebas.concat("android/1.0.0/models/brands/model.php");
                }else if(modelo.equalsIgnoreCase("pdf")){
                    url = rutaServidorPruebas.concat("resources/message_files/");
                }
                break;
            default:
                final String rutaServidorProduccion = "http://server.converfit.com/";
                if(modelo.equalsIgnoreCase("access")){
                    url = rutaServidorProduccion.concat("android/1.0.0/models/access/model.php");
                }else if(modelo.equalsIgnoreCase("conversations")){
                    url = rutaServidorProduccion.concat("android/1.0.0/models/conversations/model.php");
                }else if(modelo.equalsIgnoreCase("brands")) {
                    url = rutaServidorProduccion.concat("android/1.0.0/models/users/model.php");
                }else if(modelo.equalsIgnoreCase("webchat")){
                    url = rutaServidorProduccion.concat("android/1.0.0/models/brands/model.php");
                }else if(modelo.equalsIgnoreCase("pdf")) {
                    url = rutaServidorProduccion.concat("resources/message_files/");
                }
                break;
        }

        return url;
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);


        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public static String devolverTiempo(String fechaUnix){
        long unixTimeActual = System.currentTimeMillis() / 1000L;
        long fechaUnixRecibida = Long.parseLong(fechaUnix);
        long fromNow = unixTimeActual - fechaUnixRecibida;
        long d = 0;
        if(fromNow < 1){
            return "Ahora";
        }else{
            String timeStamp_to_str = "";
            d = fromNow;
            if(d >= 1){
                timeStamp_to_str += d;
                if(d > 1){
                    timeStamp_to_str += " seg";
                }else{
                    timeStamp_to_str += " seg";
                }
            }
            //Calculamos los minutos
            d = fromNow / 60;
            if( d >= 1){
                timeStamp_to_str = "";
                timeStamp_to_str += d;
                if(d > 1){
                    timeStamp_to_str += " min";
                }else{
                    timeStamp_to_str += " min";
                }
            }
            //Calculamos los horas
            d = fromNow / (60*60);
            if(d >= 1){
                timeStamp_to_str = "";
                timeStamp_to_str += d;
                if(d > 1){
                    timeStamp_to_str += " h";
                }else{
                    timeStamp_to_str += " h";
                }
            }
            //Calculamos los dia
            d = fromNow / (24*60*60);
            if(d >= 1){
                timeStamp_to_str = "";
                timeStamp_to_str += d;
                if(d > 1){
                    timeStamp_to_str += " días";
                }else{
                    timeStamp_to_str = " Ayer";
                }
            }
            //Calculamos los dia
            d = fromNow / (30*24*60*60);
            if(d >= 1){
                timeStamp_to_str = "";
                timeStamp_to_str += d;
                if(d > 1){
                    timeStamp_to_str += " meses";
                }else{
                    timeStamp_to_str += " mes";
                }
            }
            //Calculamos los años
            d = fromNow / (365*24*60*60);
            if(d >= 1){
                timeStamp_to_str = "";
                timeStamp_to_str += d;
                if(d > 1){
                    timeStamp_to_str += " años";
                }else{
                    timeStamp_to_str += " año";
                }
            }
            return timeStamp_to_str;
        }
    }
}
