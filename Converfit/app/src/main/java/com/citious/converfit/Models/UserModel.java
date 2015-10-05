package com.citious.converfit.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class UserModel {
    String userKey = "";
    String avatar = "";
    String email = "";
    String phone = "";
    String fname = "";
    String lname = "";
    boolean userBlocked = false;
    String conectionStatus = "";
    String horaConectado = "";

    //Constructor a partir del diccionario que nos llega
    public UserModel(JSONObject aDict){
        try {
            this.userKey = aDict.getString("user_key");
            this.avatar = aDict.getString("avatar");
            this.email = aDict.getString("email");
            this.phone = aDict.getString("phone");
            this.fname = aDict.getString("fname");
            this.lname = aDict.getString("lname");
            String blocked = aDict.getString("blocked");
            if(blocked.equalsIgnoreCase("0")){
                userBlocked = false;
            }else{
                userBlocked = true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserModel(String anUserKey, String anAvatar, String anEmail, String aPhone, String aFname, String aLname, boolean blocked, String aConectionStatus, String anHoraConectado) {
        this.userKey = anUserKey;
        this.avatar = anAvatar;
        this.email = anEmail;
        this.phone = aPhone;
        this.fname = aFname;
        this.lname = aLname;
        this.userBlocked = blocked;
        this.conectionStatus = aConectionStatus;
        this.horaConectado = anHoraConectado;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }


    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public boolean isUserBlocked() {
        return userBlocked;
    }

    public String getConectionStatus() {
        return conectionStatus;
    }

    public String getHoraConectado() {
        return horaConectado;
    }
}
