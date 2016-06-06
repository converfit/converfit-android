package com.citious.converfit.AccesoDatos;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Post {

    public static JSONObject getServerData(Map<String, Object> paramsMap, String method, String apiAddress) throws Exception {
        String requestBody = buildPostPutParameters(paramsMap);
        try {
            String responseServerString = makeRequest(method, apiAddress, null, "application/x-www-form-urlencoded",requestBody);
            return returnJsonServer(responseServerString);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static String buildPostPutParameters(Object content){
        String output = null;
        if ((content instanceof String) ||
                (content instanceof JSONObject) ||
                (content instanceof JSONArray)) {
            output = content.toString();
        } else if (content instanceof Map) {
            Uri.Builder builder = new Uri.Builder();
            HashMap hashMap = (HashMap) content;
            if (hashMap != null) {
                Iterator entries = hashMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry) entries.next();
                    builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
                    entries.remove(); // avoids a ConcurrentModificationException
                }
                output = builder.build().getEncodedQuery();
            }
        }
        return output;
    }

    public static String makeRequest(String method, String apiAddress, String accessToken, String mimeType, String requestBody) throws Exception {
        try {
            URL url = new URL(apiAddress);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(!method.equals("GET"));
            urlConnection.setRequestMethod(method);

            //urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken);

            urlConnection.setRequestProperty("Content-Type", mimeType);
            OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
            writer.write(requestBody);
            writer.flush();
            writer.close();
            outputStream.close();

            urlConnection.connect();

            //////////////////////////////
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            StringBuilder responseOutput = new StringBuilder();
            System.out.println("output===============" + br);
            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }
            br.close();

            urlConnection.disconnect();
            return responseOutput.toString();
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("Error al conectar con el servidor. ");
        }
    }

    public static JSONObject returnJsonServer(String responseServer) throws Exception {
        try {
            return new JSONObject(responseServer);
        } catch (Exception e) {
            throw new Exception("Error al convertir a JSonArray. ");
        }
    }
}