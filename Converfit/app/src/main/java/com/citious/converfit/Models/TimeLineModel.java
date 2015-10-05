package com.citious.converfit.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class TimeLineModel {

    String userKey = "";
    String avatar = "";
    String userNAme = "";
    String created = "";
    String content = "";

    //Constructor a partir del diccionario que nos llega
    public TimeLineModel(JSONObject aDict) {
        try {
            this.userKey = aDict.getString("user_key");
            this.avatar = aDict.getString("user_avatar");
            this.userNAme = aDict.getString("user_name");
            this.created = aDict.getString("created");
            this.content = aDict.getString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public TimeLineModel(String anUserKey, String anAvatar, String anUserName, String created, String content){
        this.userKey = anUserKey;
        this.avatar = anAvatar;
        this.userNAme = anUserName;
        this.created = created;
        this.content = content;
    }
    public String getUserKey() {
        return userKey;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUserNAme() {
        return userNAme;
    }

    public String getCreated() {
        return created;
    }

    public String getContent() {
        return content;
    }
}
