package com.citious.converfit.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;

public class MensajeModel {

    String messageKey = "";
    String sender = "";
    String type = "";
    String content = "";
    String created = "";
    String conversationKey = "";
    boolean enviado;
    String fname = "";
    String lname = "";

    public MensajeModel(JSONObject aDict, String conversationKey) {

        try {
            this.conversationKey = conversationKey;
            this.messageKey = aDict.getString("message_key");
            this.sender = aDict.getString("sender");
            this.type = aDict.getString("type");
            String contenido = aDict.getString("content");
            if(this.type.equalsIgnoreCase("jpeg_base64")){
                this.content = redimensionarImagen(contenido);
            }else{
                this.content = contenido;
            }
            this.created = aDict.getString("created");
            enviado = true;
            JSONObject senderData = aDict.getJSONObject("sender_data");
            fname = senderData.getString("fname");
            lname = senderData.getString("lname");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public MensajeModel(String messageKey, String content, String created, String sender, String type, String conversationKey, boolean enviado, String fname, String lname) {
        this.messageKey = messageKey;
        this.content = content;
        this.created = created;
        this.sender = sender;
        this.type = type;
        this.conversationKey = conversationKey;
        this.enviado = enviado;
        this.fname = fname;
        this.lname = lname;
    }


    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated() {
        return created;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getConversationKey() {
        return conversationKey;
    }

    public String redimensionarImagen(String content){
        byte[] decodedString = Base64.decode(content, Base64.DEFAULT);
        Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        foto = getResizedBitmap(foto, 1200);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.JPEG, 85, stream);

        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            if(width > maxSize){
                width = maxSize;
                height = (int) (width / bitmapRatio);
            }
        } else if(height > maxSize) {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public boolean isEnviado() {
        return enviado;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

}
