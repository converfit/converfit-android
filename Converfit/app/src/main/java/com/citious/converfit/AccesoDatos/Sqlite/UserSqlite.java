package com.citious.converfit.AccesoDatos.Sqlite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.citious.converfit.AccesoDatos.CapaAccesoDatos;
import com.citious.converfit.Models.UserModel;
import java.util.ArrayList;

public class UserSqlite {

    //Tabla User
    public static final String USER_TABLE_NAME = "User";
    public static final String USERKEY_USER = "userKey";
    public static final String AVATAR_USER = "avatar";
    public static final String USERNAME_USER = "userNAme";
    public static final String LASTPAGETITE_USER = "lastPageTitle";
    public static final String CONECTIONSTATUS_USER = "connectionStatus";
    public static final String HORACONECTADO_USER = "horaConectado";

    // Instancias de la base de datos y de la clase BaseDatosHelper
    private SQLiteDatabase database;
    private CapaAccesoDatos.BaseDatosHelper dbHelper;

    public UserSqlite(Context miContext){
        dbHelper = new CapaAccesoDatos.BaseDatosHelper(miContext);
    }

    public long insertarUser(UserModel user) {
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = createUserContentValues(user);
        // Insertar el registro directamente en la tabla
        numReg = database.insert(USER_TABLE_NAME, null, initialValues);
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de Politicas
    private ContentValues createUserContentValues(UserModel user) {
        ContentValues values = new ContentValues();
        values.put(USERKEY_USER, user.getUserKey());
        values.put(AVATAR_USER, user.getAvatar());
        values.put(USERNAME_USER, user.getUserName());
        values.put(LASTPAGETITE_USER, user.getLast_page_title());
        values.put(CONECTIONSTATUS_USER, user.getConectionStatus());
        values.put(HORACONECTADO_USER, user.getHoraConectado());
        return values;
    }

    //Devolvemos todas las brands
    public ArrayList<UserModel> devolverUsers(){
        ArrayList<UserModel> miUserList = new ArrayList<>();
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            String sql = "WHERE " + CONECTIONSTATUS_USER +" <> '"+ "mobile" + "' ORDER BY " + HORACONECTADO_USER + " DESC" ;
            String selectQuery = "SELECT * FROM " + USER_TABLE_NAME + " " + sql;//Obtenemos los usuarios que no esten desde el mobil
            Cursor elCursor = database.rawQuery(selectQuery,null);
            if(elCursor.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String userKey = elCursor.getString(0);
                    String avatar = elCursor.getString(1);
                    String userName = elCursor.getString(2);
                    String lastPageTitle = elCursor.getString(3);
                    String conectionStatus = elCursor.getString(4);
                    String horaConectado = elCursor.getString(5);
                    UserModel user = new UserModel(userKey, avatar, userName,lastPageTitle, conectionStatus, horaConectado);
                    miUserList.add(user);
                } while (elCursor.moveToNext());
            }
            elCursor.close();
            String sqlMobil = "WHERE " + CONECTIONSTATUS_USER +" = '"+ "mobile" + "' ORDER BY " + HORACONECTADO_USER + " DESC" ;
            String selectQeryMobile = "SELECT * FROM " + USER_TABLE_NAME + " " + sqlMobil;//Obtenemos los usuarios que esten desde el mobil
            Cursor elCursorMobil = database.rawQuery(selectQeryMobile,null);
            if(elCursorMobil.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String userKey = elCursorMobil.getString(0);
                    String avatar = elCursorMobil.getString(1);
                    String userName = elCursorMobil.getString(2);
                    String lastPageTitle = elCursorMobil.getString(3);
                    String conectionStatus = elCursorMobil.getString(4);
                    String horaConectado = elCursorMobil.getString(5);
                    UserModel user = new UserModel(userKey, avatar, userName,lastPageTitle, conectionStatus, horaConectado);
                    miUserList.add(user);
                } while (elCursorMobil.moveToNext());
            }
            elCursorMobil.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }
        return miUserList;
    }

    //Borramos las politicas que tuvieramos
    public long borrarAllUsers(){
        long numReg;
        database = dbHelper.getWritableDatabase();
        // Elimina el registro de la tabla
        numReg = database.delete(USER_TABLE_NAME, null, null);
        // Cerrar la base de datos
        dbHelper.close();
        //Devolvemos el numero de registros afectados por la opcion borrar
        return numReg;
    }

    //Busca una brand favorita a partir de un texto
    //Devolvemos todas las brands
    public ArrayList<UserModel> devolverUserBuscado(String textoBuscado){
        ArrayList<UserModel> miUserList = new ArrayList<>();
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();

            //String sql = "WHERE " + USERNAME_USER + " LIKE '%" + textoBuscado + "%'";
            String sql = "WHERE " + CONECTIONSTATUS_USER +" <> '"+ "mobile" + "'"+ " AND " + USERNAME_USER + " LIKE '%" + textoBuscado + "%'"+" ORDER BY " + HORACONECTADO_USER + " DESC";
            String selectQuery = "SELECT * FROM " + USER_TABLE_NAME + " " + sql;
            // Crear un Cursor con todos los elementos de la tabla
            Cursor elCursor = database.rawQuery(selectQuery, null);
            if(elCursor.moveToFirst()) {
                do {
                    String userKey = elCursor.getString(0);
                    String avatar = elCursor.getString(1);
                    String userName = elCursor.getString(2);
                    String lastPageTitle = elCursor.getString(3);
                    String conectionStatus = elCursor.getString(4);
                    String horaConectado = elCursor.getString(5);
                    UserModel user = new UserModel(userKey, avatar, userName,lastPageTitle, conectionStatus, horaConectado);
                    miUserList.add(user);
                } while (elCursor.moveToNext());
            }
            elCursor.close();
            String sqlMobil = "WHERE " + CONECTIONSTATUS_USER +" = '"+ "mobile" + "'"+ " AND " + USERNAME_USER + " LIKE '%" + textoBuscado + "%'"+" ORDER BY " + HORACONECTADO_USER + " DESC";
            String selectQeryMobile = "SELECT * FROM " + USER_TABLE_NAME + " " + sqlMobil;//Obtenemos los usuarios que esten desde el mobil
            Cursor elCursorMobil = database.rawQuery(selectQeryMobile,null);
            if(elCursorMobil.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String userKey = elCursorMobil.getString(0);
                    String avatar = elCursorMobil.getString(1);
                    String userName = elCursorMobil.getString(2);
                    String lastPageTitle = elCursorMobil.getString(3);
                    String conectionStatus = elCursorMobil.getString(4);
                    String horaConectado = elCursorMobil.getString(5);
                    UserModel user = new UserModel(userKey, avatar, userName,lastPageTitle, conectionStatus, horaConectado);
                    miUserList.add(user);
                } while (elCursorMobil.moveToNext());
            }
            elCursorMobil.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }
        return miUserList;
    }
}
