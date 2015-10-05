package com.citious.converfit.AccesoDatos.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.citious.converfit.AccesoDatos.CapaAccesoDatos;
import com.citious.converfit.Models.ConversationModel;
import com.citious.converfit.Utils.Utils;
import java.util.ArrayList;

public class ConversationsSqlite {

    //Tabla CONVERSATION
    public static final String CONVERSATION_TABLE_NAME = "Conversation";
    public static final String CONVERSATIONKEY_CONVERSATION = "conversationKey";
    public static final String AVATAR_CONVERSATION = "avatar";
    public static final String CREATIONLASTMESSAGE_CONVERSATION = "creationLastMessage";
    public static final String FLAGNEWUSERMESSAGE_CONVERSATION = "flagNewuserMessage";
    public static final String LASTMESSAGE_CONVERSATION = "lastMessage";
    public static final String LASTUPDATE_CONVERSATION = "lastUpdate";
    public static final String USERKEY_CONVERSATION = "userKey";
    public static final String FNAME_CONVERSATION = "fname";
    public static final String LNAME_CONVERSATION = "lname";
    public static final String CONECTIONSTATUS_CONVERSATION = "conectionStatus";

    // Instancias de la base de datos y de la clase BaseDatosHelper
    private SQLiteDatabase database;
    private CapaAccesoDatos.BaseDatosHelper dbHelper;

    public ConversationsSqlite(Context miContext) {
        dbHelper = new CapaAccesoDatos.BaseDatosHelper(miContext);
    }

    /*
       * Inserta la Brand
       * que nos llegue
   */
    public long insertarConversation(ConversationModel conversation) {
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = createConversationsContentValues(conversation);
        // Insertar el registro directamente en la tabla
        numReg = database.insert(CONVERSATION_TABLE_NAME, null, initialValues);
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de Politicas
    private ContentValues createConversationsContentValues(ConversationModel conversation) {
        ContentValues values = new ContentValues();
        values.put(CONVERSATIONKEY_CONVERSATION, conversation.getConversationKey());
        values.put(AVATAR_CONVERSATION, conversation.getAvatar());
        values.put(CREATIONLASTMESSAGE_CONVERSATION, conversation.getLastMessageCreation());
        values.put(FLAGNEWUSERMESSAGE_CONVERSATION, conversation.isFlagNewMesssageUser());
        values.put(LASTMESSAGE_CONVERSATION, conversation.getLastMessage());
        values.put(LASTUPDATE_CONVERSATION, conversation.getLastUpdate());
        values.put(USERKEY_CONVERSATION, conversation.getUserKey());
        values.put(FNAME_CONVERSATION, conversation.getFname());
        values.put(LNAME_CONVERSATION, conversation.getLname());
        values.put(CONECTIONSTATUS_CONVERSATION, conversation.getConectionStatus());
        return values;
    }

    //Devolvemos todas las brands
    public ArrayList<ConversationModel> devolverConversations(Context miContext){
        ArrayList<ConversationModel> miConversationList = new ArrayList<>();
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            String sql = "ORDER BY " + CREATIONLASTMESSAGE_CONVERSATION + " DESC" ;
            String selectQuery = "SELECT * FROM " + CONVERSATION_TABLE_NAME + " " + sql;
            Cursor elCursor = database.rawQuery(selectQuery,null);
            if(elCursor.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String conversationKey = elCursor.getString(0);
                    String avatar = elCursor.getString(1);
                    String fechaUnix = elCursor.getString(2);
                    String creationLastMessage = Utils.devolverTiempo(fechaUnix);
                            //new Utils().devolverFecha(miContext, fechaUnix);
                    boolean flagNewuserMessage = (elCursor.getString(3)).equals("1");
                    String lastMessage = elCursor.getString(4);
                    String lastUpdate = elCursor.getString(5);
                    String userKey = elCursor.getString(6);
                    String fname = elCursor.getString(7);
                    String lname = elCursor.getString(8);
                    String conectionStatus = elCursor.getString(9);
                    ConversationModel conversation = new ConversationModel(conversationKey, avatar, creationLastMessage, flagNewuserMessage, lastMessage, lastUpdate,
                            userKey, fname, lname,conectionStatus);
                    miConversationList.add(conversation);
                } while (elCursor.moveToNext());
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }
        return miConversationList;
    }

    //Borramos las politicas que tuvieramos
    public long borrarAllConversations(){
        long numReg;
        database = dbHelper.getWritableDatabase();
        // Elimina el registro de la tabla
        numReg = database.delete(CONVERSATION_TABLE_NAME, null, null);
        // Cerrar la base de datos
        dbHelper.close();
        //Devolvemos el numero de registros afectados por la opcion borrar
        return numReg;
    }

    //Modificamos el lastUpdate de una conversacion determinada
    public long modificarLastUpdateConversacion(String conversationKey, String lastUpdate){
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = updateLastUpdateContentValues(lastUpdate);
        // Insertar el registro directamente en la tabla
        numReg = database.update(CONVERSATION_TABLE_NAME, initialValues, CONVERSATIONKEY_CONVERSATION + "=?",
                new String[]{String.valueOf(conversationKey)});
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de Politicas
    private ContentValues updateLastUpdateContentValues(String lastUpdate) {
        ContentValues values = new ContentValues();
        values.put(LASTUPDATE_CONVERSATION, lastUpdate);
        return values;
    }

    //Devolvemos las cookies o la privacidad o los temirnos dependiendo cual queramos
    public String devolverLastUpdateConversacion(String conversationKey){
        String aConversationKey = "0";
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            Cursor elCursor = database.query(CONVERSATION_TABLE_NAME,null, CONVERSATIONKEY_CONVERSATION + "=?", new String[] { conversationKey }
                    ,null,null,null);
            if(elCursor.moveToFirst()){
                aConversationKey = elCursor.getString(7);
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }
        return aConversationKey;
    }

    //Borrar una conversacion con un conversationKey determinado
    public long borrarConversation(String conversationKey){
        long numReg;
        database = dbHelper.getWritableDatabase();
        // Elimina el registro de la tabla
        numReg = database.delete(CONVERSATION_TABLE_NAME, CONVERSATIONKEY_CONVERSATION + "=?", new String[]{String.valueOf(conversationKey)});
        // Cerrar la base de datos
        dbHelper.close();
        //Devolvemos el numero de registros afectados por la opcion borrar
        return numReg;
    }

    //Metodo para cambiar el valor del conversation flag a false porque no hay mensajes pendientes
    public long updateConversationFlag(String conversationKey){
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = updateConversationFlagContentValues();
        // Insertar el registro directamente en la tabla
        numReg = database.update(CONVERSATION_TABLE_NAME, initialValues, CONVERSATIONKEY_CONVERSATION + "=?",
                new String[]{String.valueOf(conversationKey)});
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de Politicas
    private ContentValues updateConversationFlagContentValues() {
        ContentValues values = new ContentValues();
        values.put(FLAGNEWUSERMESSAGE_CONVERSATION, false);
        return values;
    }

    //Metodo para devolver el numero de mensajes sin leer
    public int obtenerNumeroMensajesSinLeer(){
        int numeroMensajes = 0;
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            Cursor elCursor = database.query(CONVERSATION_TABLE_NAME,null, FLAGNEWUSERMESSAGE_CONVERSATION + "=?", new String[] { "1" }
                    ,null,null,null);
            numeroMensajes = elCursor.getCount();
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }

        return numeroMensajes;
    }

    //Metodo que devuelve si una conversacion tiene mensajes sin leer
    public boolean hayMensajesSinLeer(String conversationKey){
        boolean hayMensajesSinLeer = false;

        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            Cursor elCursor = database.query(CONVERSATION_TABLE_NAME,null, CONVERSATIONKEY_CONVERSATION + "=?", new String[] {conversationKey}
                    ,null,null,null);
            if(elCursor.moveToFirst()){
                hayMensajesSinLeer = (elCursor.getString(3)).equals("1");
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }

        return hayMensajesSinLeer;
    }

    public String existeConversacionDeUsuario(String userKey){
        String conversationKey = "";
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            Cursor elCursor = database.query(CONVERSATION_TABLE_NAME,null, USERKEY_CONVERSATION + "=?", new String[] { userKey }
                    ,null,null,null);
            if(elCursor.moveToFirst()){
                conversationKey = elCursor.getString(0);
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }
        return conversationKey;
    }
}
