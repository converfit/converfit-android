package com.citious.converfit.AccesoDatos;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Post {

    private InputStream is = null;
    private String respuesta = "";

    //Recoge los parámetros adecuados para la ejecución del script indicado en la URL
    public JSONObject getServerData(String URL, List<NameValuePair> params)
            throws Exception {
        JSONObject jObject = null;
        try {
            conectaPost(URL, params);
            getRespuestaPost();
            jObject = getJsonArray();
            return jObject;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void conectaPost(String URL, List<NameValuePair> params) throws Exception {
        HttpClient httpclient = null;
        HttpPost httppost = null;
        HttpResponse response = null;
        HttpEntity entity = null;
        try {
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(URL);
            try{
                httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                response = httpclient.execute(httppost);
            }catch (Exception e){
            }
            if (response != null) {
                entity = response.getEntity();
            }
            if (entity != null) {
                is = entity.getContent();
            }
        } catch (Exception e) {
            throw new Exception("Error al conectar con el servidor. ");
        } finally {
            if (entity != null) {
                entity = null;
            }
            if (response != null) {
                response = null;
            }
            if (httppost != null) {
                httppost = null;
            }
            if (httpclient != null) {
                httpclient = null;
            }
        }
    }

    //Para obtener la respuesta enviada por el script PHP, es llamado desde esta clase
    private void getRespuestaPost() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            respuesta = sb.toString();
        } catch (Exception e) {
            throw new Exception("Error al recuperar las imágenes del servidor. ");
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    //Para recuperar los datos JSON enviados por el script, es llamado desde esta clase
    @SuppressWarnings("finally")
    private JSONObject getJsonArray() throws Exception {
        JSONObject jObjet = null;
        try {
            jObjet = new JSONObject(respuesta);
        } catch (Exception e) {
            throw new Exception("Error al convertir a JSonArray. ");
        } finally {
            return jObjet;
        }
    }

}
