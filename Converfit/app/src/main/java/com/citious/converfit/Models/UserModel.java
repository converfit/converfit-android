package com.citious.converfit.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {
    String userKey = "";
    String avatar = "";
    String userName = "";
    String last_page_title = "" ;
    String conectionStatus = "";
    String horaConectado = "";

    //Constructor a partir del diccionario que nos llega
    public UserModel(JSONObject aDict){
        try {
            this.userKey = aDict.getString("user_key");
            this.avatar = aDict.getString("user_avatar");
            this.userName = aDict.getString("user_name");
            this.last_page_title = aDict.getString("last_page_title");
            this.conectionStatus = aDict.getString("connection-status");
            this.horaConectado = aDict.getString("last_connection");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserModel(String anUserKey, String anAvatar, String userName, String lastPage, String aConectionStatus, String anHoraConectado) {
        this.userKey = anUserKey;
        this.avatar = anAvatar;
        this.userName = userName;
        this.last_page_title = lastPage;
        this.conectionStatus = aConectionStatus;
        this.horaConectado = anHoraConectado;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getConectionStatus() {
        return conectionStatus;
    }

    public String getHoraConectado() {
        return horaConectado;
    }

    public String getUserName() {
        return userName;
    }

    public String getLast_page_title() {
        return last_page_title;
    }
}
