package com.citious.converfit.AccesoDatos.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.citious.converfit.AccesoDatos.CapaAccesoDatos;
import com.citious.converfit.Models.MensajeModel;
import com.citious.converfit.Utils.Utils;
import java.util.ArrayList;
import java.util.Collections;

public class MessageSqlite {

    //Tabla MESSAGE
    public static final String MESSAGE_TABLE_NAME = "Message";
    public static final String MESSAGEKEY_MESSAGE = "messageKey";
    public static final String CONTENT_MESSAGE = "content";
    public static final String CREATED_MESSAGE = "created";
    public static final String SENDER_MESSAGE = "sender";
    public static final String TYPE_MESSAGE = "type";
    public static final String CONVERSATIONKEY_MESSAGE = "conversationKey";
    public static final String ENVIADO_MESSAGE = "enviado";
    public static final String FNAME_MESSAGE = "fname";
    public static final String LNAME_MESSAGE = "lname";

    // Instancias de la base de datos y de la clase BaseDatosHelper
    private SQLiteDatabase database;
    private CapaAccesoDatos.BaseDatosHelper dbHelper;

    public MessageSqlite(Context miContext) {
        dbHelper = new CapaAccesoDatos.BaseDatosHelper(miContext);
    }

    /*
      * Inserta el Mensaje
      * que nos llegue
  */
    public long insertarMessage(MensajeModel message) {
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = createMessageContentValues(message);
        // Insertar el registro directamente en la tabla
        numReg = database.insert(MESSAGE_TABLE_NAME, null, initialValues);
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de Mensajes
    private ContentValues createMessageContentValues(MensajeModel message) {
        ContentValues values = new ContentValues();
        values.put(MESSAGEKEY_MESSAGE, message.getMessageKey());
        values.put(CONTENT_MESSAGE, message.getContent());
        values.put(CREATED_MESSAGE, message.getCreated());
        values.put(SENDER_MESSAGE, message.getSender());
        values.put(TYPE_MESSAGE, message.getType());
        values.put(CONVERSATIONKEY_MESSAGE, message.getConversationKey());
        values.put(ENVIADO_MESSAGE, message.isEnviado());
        values.put(FNAME_MESSAGE,message.getFname());
        values.put(LNAME_MESSAGE, message.getLname());
        return values;
    }

    //Devolvemos todas los mensajes
    public ArrayList<MensajeModel> devolverMessages(String conversationKey){
        ArrayList<MensajeModel> miMessageList = new ArrayList<>();
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            String sql = "WHERE " + CONVERSATIONKEY_MESSAGE +" = '"+ conversationKey + "' ORDER BY " + CREATED_MESSAGE + " DESC" ;
            String selectQuery = "SELECT * FROM " + MESSAGE_TABLE_NAME + " " + sql;
            Cursor elCursor = database.rawQuery(selectQuery, null);
            if(elCursor.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String messageKey = elCursor.getString(0);
                    String content = elCursor.getString(1);
                    String created = elCursor.getString(2);
                    String sender = elCursor.getString(3);
                    String type = elCursor.getString(4);
                    String converKey = elCursor.getString(5);
                    boolean enviado = (elCursor.getString(6)).equals("1");
                    String fname = elCursor.getString(7);
                    String lname = elCursor.getString(8);
                    MensajeModel message = new MensajeModel(messageKey, content, created, sender, type, converKey,enviado,fname, lname);
                    miMessageList.add(message);
                } while (elCursor.moveToNext());
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }

        return miMessageList;
    }

    //Borramos los mensajes que tuvieramos
    public long borrarAllMessage(){
        long numReg;
        database = dbHelper.getWritableDatabase();
        // Elimina el registro de la tabla
        numReg = database.delete(MESSAGE_TABLE_NAME, null, null);
        // Cerrar la base de datos
        dbHelper.close();
        //Devolvemos el numero de registros afectados por la opcion borrar
        return numReg;
    }

    //Borramos los mensajes de una sola conversacion
    public long borrarMensajesConversacion(String conversationKey){
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Elimina el registro de la tabla
        numReg = database.delete(MESSAGE_TABLE_NAME, CONVERSATIONKEY_MESSAGE + "=?", new String[] { String.valueOf(conversationKey) });
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el resultado de la operación
        return numReg;
    }

    //Añadimos un mensaje
    public void addMensaje(String messageKey ,String content, String created, String sender, String type, String  converKey, boolean enviado, String fname, String lname){
        MensajeModel message = new MensajeModel(messageKey, content, created, sender, type, converKey, enviado, fname, lname);
        insertarMessage(message);
    }

    //Metodo para cambiar el tipo poll a poll_closed
    public long updatePollType(String conversationKey, String messageKey){
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = updatePollTypeContentValues();
        // Insertar el registro directamente en la tabla
        numReg = database.update(MESSAGE_TABLE_NAME, initialValues, CONVERSATIONKEY_MESSAGE + "=?" + " AND " + MESSAGEKEY_MESSAGE + "=?",
                new String[]{String.valueOf(conversationKey), String.valueOf(messageKey)});
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a cambiar del tipo de mensaje
    private ContentValues updatePollTypeContentValues() {
        ContentValues values = new ContentValues();
        values.put(TYPE_MESSAGE, "poll_closed");
        return values;
    }

    //Devolvemos el numero de mensajes
    public int devolverMessagesTotales(){
        int numeroMensajesTotales = 0;
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            Cursor elCursor = database.query(MESSAGE_TABLE_NAME, null, null, null
                    , null, null, null);
            numeroMensajesTotales = elCursor.getCount();
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }

        return numeroMensajesTotales;
    }

    //Devolvemos todos los mensajes fallidos
    public ArrayList<MensajeModel> devolverMessagesFallidos(Context miContext, String conversationKey, boolean anEnviado){
        ArrayList<MensajeModel> miMessageListFallidos = new ArrayList<>();
        try {
            int enviadoInt = 0;
            if(anEnviado){
                enviadoInt = 1;
            }
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            String sql = "WHERE " + CONVERSATIONKEY_MESSAGE +" = '"+ conversationKey + "' AND " + ENVIADO_MESSAGE + " = " + enviadoInt + " ORDER BY " + CREATED_MESSAGE + " ASC" ;
            String selectQuery = "SELECT * FROM " + MESSAGE_TABLE_NAME + " " + sql;
            Cursor elCursor = database.rawQuery(selectQuery, null);
            if(elCursor.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String messageKey = elCursor.getString(0);
                    String content = elCursor.getString(1);
                    String fechaUnix = elCursor.getString(2);
                    String created = new Utils().devolverFecha(miContext, fechaUnix);
                    String sender = elCursor.getString(3);
                    String type = elCursor.getString(4);
                    String converKey = elCursor.getString(5);
                    boolean enviado = (elCursor.getString(6)).equals("1");
                    String fname = elCursor.getString(7);
                    String lname = elCursor.getString(8);
                    MensajeModel message = new MensajeModel(messageKey, content, created, sender, type, converKey,enviado, fname, lname);
                    miMessageListFallidos.add(message);
                } while (elCursor.moveToNext());
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }

        return miMessageListFallidos;
    }

    //Devolvemos el ultimo enviado a true
    public MensajeModel devolverUltimoMensajeEnviadoOk(Context miContext, String conversationKey){
        MensajeModel ultimoMensajeEnviado;

        ArrayList<MensajeModel> listaMensajesEnviadoOk = devolverMessagesFallidos(miContext, conversationKey, true);

        ultimoMensajeEnviado = listaMensajesEnviadoOk.get(listaMensajesEnviadoOk.size() - 1);

        return ultimoMensajeEnviado;
    }

    //Cambiamos el valor de si es enviado un mensaje  o no al valor que nos llega
    public long updateEnviado(String conversationKey, String messageKey, boolean enviado){
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = updateEnviadoContentValues(enviado);
        // Insertar el registro directamente en la tabla
        numReg = database.update(MESSAGE_TABLE_NAME, initialValues, CONVERSATIONKEY_MESSAGE + "=?" + " AND " + MESSAGEKEY_MESSAGE + "=?",
                new String[]{String.valueOf(conversationKey), String.valueOf(messageKey)});
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de Politicas
    private ContentValues updateEnviadoContentValues(boolean enviado) {
        ContentValues values = new ContentValues();
        values.put(ENVIADO_MESSAGE, enviado);
        return values;
    }

    //Cambiamos el valor de si es enviado un mensaje  o no al valor que nos llega
    public long updateMessageKey(String conversationKey, String messageKey, String messageKeyServidor){
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = updateMessageKeyContentValues(messageKeyServidor);
        // Insertar el registro directamente en la tabla
        numReg = database.update(MESSAGE_TABLE_NAME, initialValues, CONVERSATIONKEY_MESSAGE + "=?" + " AND " + MESSAGEKEY_MESSAGE + "=?",
                new String[]{String.valueOf(conversationKey), String.valueOf(messageKey)});
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de Politicas
    private ContentValues updateMessageKeyContentValues(String messageKeyServidor) {
        ContentValues values = new ContentValues();
        values.put(MESSAGEKEY_MESSAGE, messageKeyServidor);
        return values;
    }

    public String horaUltimoMensajeEnviado(Context miContext, String conversationKey){
        ArrayList<MensajeModel> listaMensajes = devolverMessages(conversationKey);
        Collections.reverse(listaMensajes);
        String hora = "";
        if(listaMensajes.size() > 0){
            hora = listaMensajes.get(listaMensajes.size() - 1).getCreated();
        }
        return hora;
    }

    //Cambiamos el valor de si es enviado un mensaje  o no al valor que nos llega
    public long updateMessageCreation(String conversationKey, String messageKey, String hora){
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = updateMessageCreationContentValues(hora);
        // Insertar el registro directamente en la tabla
        numReg = database.update(MESSAGE_TABLE_NAME, initialValues, CONVERSATIONKEY_MESSAGE + "=?" + " AND " + MESSAGEKEY_MESSAGE + "=?",
                new String[]{String.valueOf(conversationKey), String.valueOf(messageKey)});
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de Politicas
    private ContentValues updateMessageCreationContentValues(String hora) {
        ContentValues values = new ContentValues();
        values.put(CREATED_MESSAGE, hora);
        return values;
    }

    //Borramos los mensajes de una sola conversacion
    public long borrarMensajesFallidos(String conversationKey, String messageKey){
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Elimina el registro de la tabla
        numReg = database.delete(MESSAGE_TABLE_NAME, CONVERSATIONKEY_MESSAGE + "=?" + " AND " + MESSAGEKEY_MESSAGE + "=?",
                new String[]{String.valueOf(conversationKey), String.valueOf(messageKey)});
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el resultado de la operación
        return numReg;
    }
}
