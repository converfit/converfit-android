package com.citious.converfit.AccesoDatos.Sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.citious.converfit.AccesoDatos.CapaAccesoDatos;
import com.citious.converfit.Models.TimeLineModel;
import java.util.ArrayList;

public class TimeLineSqlite {

    //Tabla User
    public static final String TIMELINE_TABLE_NAME = "timeline";
    public static final String USERKEY_TIMELINE = "user_key";
    public static final String AVATAR_TIMELINE  = "user_avatar";
    public static final String USERNAME_TIMELINE  = "user_name";
    public static final String CREATED_TIMELINE  = "created";
    public static final String CONTENT_TIMELINE  = "content";

    // Instancias de la base de datos y de la clase BaseDatosHelper
    private SQLiteDatabase database;
    private CapaAccesoDatos.BaseDatosHelper dbHelper;

    public TimeLineSqlite(Context miContext){
        dbHelper = new CapaAccesoDatos.BaseDatosHelper(miContext);
    }

    public long insertarNotification(TimeLineModel notificacion) {
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = createUserContentValues(notificacion);
        // Insertar el registro directamente en la tabla
        numReg = database.insert(TIMELINE_TABLE_NAME, null, initialValues);
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de timeline
    private ContentValues createUserContentValues(TimeLineModel notificacion) {
        ContentValues values = new ContentValues();
        values.put(USERKEY_TIMELINE, notificacion.getUserKey());
        values.put(AVATAR_TIMELINE, notificacion.getAvatar());
        values.put(USERNAME_TIMELINE, notificacion.getUserNAme());
        values.put(CREATED_TIMELINE, notificacion.getCreated());
        values.put(CONTENT_TIMELINE, notificacion.getContent());
        return values;
    }

    //Devolvemos todos los Post
    public ArrayList<TimeLineModel> devolverAllPost(){
        ArrayList<TimeLineModel> miPostList = new ArrayList<>();
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            String sql = " ORDER BY " + CREATED_TIMELINE + " DESC" ;
            String selectQuery = "SELECT * FROM " + TIMELINE_TABLE_NAME + " " + sql;
            Cursor elCursor = database.rawQuery(selectQuery, null);
            if(elCursor.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String userKey = elCursor.getString(0);
                    String avatar = elCursor.getString(1);
                    String userName = elCursor.getString(2);
                    String created = elCursor.getString(3);
                    String content = elCursor.getString(4);
                    TimeLineModel post = new TimeLineModel(userKey, avatar, userName, created, content);
                    miPostList.add(post);
                } while (elCursor.moveToNext());
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de devolverAllPost es " + e.toString());
            // Devolver el cursor
        }
        return miPostList;
    }

    //Borramos los post que tuvieramos
    public long borrarAllPosts(){
        long numReg;
        database = dbHelper.getWritableDatabase();
        // Elimina el registro de la tabla
        numReg = database.delete(TIMELINE_TABLE_NAME, null, null);
        // Cerrar la base de datos
        dbHelper.close();
        //Devolvemos el numero de registros afectados por la opcion borrar
        return numReg;
    }

    //Devolver el time line de un usuario dado por su userKey
    public ArrayList<TimeLineModel> devolverAllUserPost(String anUserKey){
        ArrayList<TimeLineModel> miPostList = new ArrayList<>();
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            String sql = "WHERE " + USERKEY_TIMELINE +" = '"+ anUserKey + "' ORDER BY " + CREATED_TIMELINE + " DESC" ;
            String selectQuery = "SELECT * FROM " + TIMELINE_TABLE_NAME + " " + sql;
            Cursor elCursor = database.rawQuery(selectQuery, null);
            if(elCursor.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String userKey = elCursor.getString(0);
                    String avatar = elCursor.getString(1);
                    String userName = elCursor.getString(2);
                    String created = elCursor.getString(3);
                    String content = elCursor.getString(4);
                    TimeLineModel post = new TimeLineModel(userKey, avatar, userName, created, content);
                    miPostList.add(post);
                } while (elCursor.moveToNext());
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de devolverAllPost es " + e.toString());
            // Devolver el cursor
        }
        return miPostList;
    }
}
