package com.citious.converfit.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import com.citious.converfit.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class AddFilesToDisk {


    static public String crearFichero(String messageKey, String tipo, String contenido, Context miContext){
        String pathArchivo = "";
        File filepath = Environment.getExternalStorageDirectory();
        // Si no existe el directorio de Citious lo creamos
        File dir = new File(filepath.getAbsolutePath() + "/" + miContext.getResources().getString(R.string.app_name) + "/");
        if(!dir.exists()){
            dir.mkdirs();
        }
        if(tipo.equalsIgnoreCase("jpeg_base64")) {
            //Comprobamos si existe la imagen guardada y sino la guardamos
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + miContext.getResources().getString(R.string.app_name) + "/"+ messageKey + ".jpg");
            if(!f.exists()){
                Bitmap miImagen = redimensionarImagen(contenido);
                // Creamos el fichero
                File file = new File(dir, messageKey + ".jpg" );
                try {
                    OutputStream output = new FileOutputStream(file);

                    // Compress into png format image from 0% - 100%
                    miImagen.compress(Bitmap.CompressFormat.JPEG, 100, output);
                    output.flush();
                    output.close();
                    addImageToGallery(file.getAbsolutePath(), miContext);
                    pathArchivo = file.getAbsolutePath();
                }

                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                pathArchivo = f.getAbsolutePath();
            }
        }
        return  pathArchivo;
    }

    public static String addImageToGallery(final String filePath, final Context context) {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values).toString();
    }

    static public Bitmap redimensionarImagen(String content) {
        byte[] decodedString = Base64.decode(content, Base64.DEFAULT);
        Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.JPEG, 85, stream);

        return foto;
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inTempStorage = new byte[16*1024];
        options.inPurgeable = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16*1024];
        options.inPurgeable = true;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    //Metodo para renombrar la foto temporal con el messageKey devuelto por el servidor
    public static String renombrarTempMessageKeyFile(String tempMessageKey, String messageKeyServidor, String type, Context miContext){
        String tipo = "jpeg_base64";
        String extension = "";
        String pathArchivo = "";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + miContext.getResources().getString(R.string.app_name) + "/";
        if(tipo.equalsIgnoreCase(type)){
            extension = ".jpg";
            File photo = new File(filePath, tempMessageKey + extension);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap miImagen = BitmapFactory.decodeFile(photo.getAbsolutePath(), options);
            File file = new File(filePath, messageKeyServidor + extension );
            try {
                OutputStream output = new FileOutputStream(file);

                // Compress into png format image from 0% - 100%
                miImagen.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.flush();
                output.close();
                addImageToGallery(file.getAbsolutePath(), miContext);
                pathArchivo = file.getAbsolutePath();
                miContext.getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.ImageColumns.DATA + "=?" , new String[]{ photo.getAbsolutePath() });
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return pathArchivo;
    }
}