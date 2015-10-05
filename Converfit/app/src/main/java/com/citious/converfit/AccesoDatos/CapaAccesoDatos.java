package com.citious.converfit.AccesoDatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.citious.converfit.Utils.Utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CapaAccesoDatos {

    // Base de datos
    public static final String DB_NAME = "BBDD.sqlite";
    public static final int DATABASE_VERSION = 1;

    // Instancias de la base de datos y de la clase BaseDatosHelper
    private SQLiteDatabase database;
    private BaseDatosHelper dbHelper;

    // Constructor de la clase que da valor a la instancia dbHelper
    public CapaAccesoDatos(Context context) {
        dbHelper = new BaseDatosHelper(context);
    }

    /*
    * Se incluye la clase que extiende de SQLiteOpenHelper, necesaria para la
    * creación, actualización y manejo de la base de datos
    */
    public static class BaseDatosHelper extends SQLiteOpenHelper {
        private Context miContext;

        // Constructor de la clase
        public BaseDatosHelper(Context context) {
            super(context, CapaAccesoDatos.DB_NAME, null,CapaAccesoDatos.DATABASE_VERSION);
            miContext = context;
            //Copiar la base de datos del fichero *.sqlite a la ruta interna del dispositivo
            copiarBD();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Utils.borrarLastUpdates(miContext);
            miContext.deleteDatabase(DB_NAME);
            copiarBD();
        }

        // Devuelve el nombre de la base de datos actual
        public String getDatabaseName() {
            return CapaAccesoDatos.DB_NAME;
        }

        /*
        * Copia la base de datos en la carpeta /data/data/paquete/databases del
        * dispositivo para poder ser utilizada por la app
        */
        private void copiarBD() {
            //Averigua el path del dispositivo donde se almacenan las bases de datos
            File pathFile = miContext.getDatabasePath(CapaAccesoDatos.DB_NAME);

            //Averigua si la base de datos existe
            boolean dbExist = checkDataBase(pathFile.getAbsolutePath());
            // boolean dbExist = checkDataBase(miContext.getDatabasePath(CapaAccesoDatos.DB_NAME).getAbsolutePath());
            //Si si la base de datos no existe, se ejecuta el proceso de creación y copia de bd
            if (!dbExist) {
                //Crea una base de datos vacía sobre la que se copiará la nueva
                //getReadableDatabase() comprueba si la bd existe, y sino crea un fichero bd nuevo y vacío
                //También Llama al método onCreate(), aunque este no contiene ninguna operación
                this.getReadableDatabase();
                // Copiar la base de datos de /assets a la bd vacía y recién creada
                try {
                    InputStream in = miContext.getAssets().open(CapaAccesoDatos.DB_NAME);
                    OutputStream salida = new FileOutputStream(pathFile);
                    byte[] buffer = new byte[1024];
                    int tam;
                    while ((tam = in.read(buffer)) > 0) {
                        salida.write(buffer, 0, tam);
                    }
                    salida.flush();
                    salida.close();
                    in.close();
                    this.close();
                }catch(Exception e){
                    //Excepcion de cobiarBD
                    this.close();
                }

            }else{
                //Existe el fichero
            }
        }

        //Chequea si la base de datos ya existe o no
        private static boolean checkDataBase(String path) {
            SQLiteDatabase checkDB = null;
            try {
                checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            } catch(Exception e){
                //No encuentra la checkDB
            }
            if(checkDB != null) {
                checkDB.close();
                //checkDb es diferente de null
            }
            return checkDB != null;
        }
    }
}
