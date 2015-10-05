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
    public static final String EMAIL_USER = "email";
    public static final String PHONE_USER = "phone";
    public static final String FNAME_USER = "fname";
    public static final String LNAME_USER = "lname";
    public static final String USERBLOCKED_USER = "user_blocked";
    public static final String CONECTIONSTATUS_USER = "conection_status";
    public static final String HORACONECTADO_USER = "hora_conectado";

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
        values.put(EMAIL_USER, user.getEmail());
        values.put(PHONE_USER, user.getPhone());
        values.put(FNAME_USER, user.getFname());
        values.put(LNAME_USER, user.getLname());
        values.put(USERBLOCKED_USER, user.isUserBlocked());
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
            String sql = "ORDER BY UPPER(" + FNAME_USER + ") ASC" ;
            String selectQuery = "SELECT * FROM " + USER_TABLE_NAME + " " + sql;
            Cursor elCursor = database.rawQuery(selectQuery,null);
            if(elCursor.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String userKey = elCursor.getString(0);
                    String avatar = elCursor.getString(1);
                    String email = elCursor.getString(2);
                    String phone = elCursor.getString(3);
                    String fname = elCursor.getString(4);
                    String lname = elCursor.getString(5);
                    boolean userBlocked = (elCursor.getString(6)).equals("1");
                    String conectionStatus = elCursor.getString(7);
                    String horaConectado = elCursor.getString(8);
                    UserModel user = new UserModel(userKey, avatar, email, phone, fname, lname, userBlocked, conectionStatus, horaConectado);
                    miUserList.add(user);
                } while (elCursor.moveToNext());
            }
            elCursor.close();
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

            String sql = "WHERE " + FNAME_USER + " LIKE '%" + textoBuscado + "%' OR " + LNAME_USER + " LIKE '%" + textoBuscado + "%'";
            String selectQuery = "SELECT * FROM " + USER_TABLE_NAME + " " + sql;
            // Crear un Cursor con todos los elementos de la tabla
            Cursor elCursor = database.rawQuery(selectQuery, null);
            if(elCursor.moveToFirst()) {
                do {
                    //Se recogen los datos del registro actual, solo recogemos el refran
                    String userKey = elCursor.getString(0);
                    String avatar = elCursor.getString(1);
                    String email = elCursor.getString(2);
                    String phone = elCursor.getString(3);
                    String fname = elCursor.getString(4);
                    String lname = elCursor.getString(5);
                    boolean userBlocked = (elCursor.getString(6)).equals("1");
                    String conectionStatus = elCursor.getString(7);
                    String horaConectado = elCursor.getString(8);
                    UserModel user = new UserModel(userKey, avatar, email, phone, fname, lname, userBlocked, conectionStatus, horaConectado);
                    miUserList.add(user);
                } while (elCursor.moveToNext());
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }
        return miUserList;
    }

    //Obtenemos una brand a partir de su username
    public UserModel obtenerUser(String anUserKey){
        UserModel user = null;
        try {
            // Abrir la base de datos para lectura
            database = dbHelper.getReadableDatabase();
            // Crear un Cursor con todos los elementos de la tabla
            Cursor elCursor = database.query(USER_TABLE_NAME, null, USERKEY_USER + "=?", new String[]{anUserKey}
                    , null, null, null);
            if(elCursor.moveToFirst()){
                //Se recogen los datos del registro actual, solo recogemos el refran
                String userKey = elCursor.getString(0);
                String avatar = elCursor.getString(1);
                String email = elCursor.getString(2);
                String phone = elCursor.getString(3);
                String fname = elCursor.getString(4);
                String lname = elCursor.getString(5);
                boolean userBlocked = (elCursor.getString(6)).equals("1");
                String conectionStatus = elCursor.getString(7);
                String horaConectado = elCursor.getString(8);
                user = new UserModel(userKey, avatar, email, phone, fname, lname, userBlocked, conectionStatus, horaConectado);
            }
            elCursor.close();
        } catch (Exception e) {
            Log.e("MIO", "La exception de todosUser es " + e.toString());
            // Devolver el cursor
        }
        return user;
    }

    //Cambiamos el valor de si es enviado un mensaje  o no al valor que nos llega
    public long updateBloqueado(String userKey, boolean bloqueado){
        long numReg;
        // Abrir la base de datos para escritura
        database = dbHelper.getWritableDatabase();
        // Crear el objeto ContentValues con los nombres de los campos del
        // registro a insertar
        ContentValues initialValues = updateBloqueadoContentValues(bloqueado);
        // Insertar el registro directamente en la tabla
        numReg = database.update(USER_TABLE_NAME, initialValues, USERKEY_USER + "=?",
                new String[]{String.valueOf(userKey)});
        // Cerrar la base de datos
        dbHelper.close();
        // Devolver el número de registro insertado o -1 si hubo algún error
        return numReg;
    }

    // Crear el objeto ContentValues con los datos a insertar en la tabla de Politicas
    private ContentValues updateBloqueadoContentValues(boolean bloqueado) {
        ContentValues values = new ContentValues();
        values.put(USERBLOCKED_USER, bloqueado);
        return values;
    }
}
