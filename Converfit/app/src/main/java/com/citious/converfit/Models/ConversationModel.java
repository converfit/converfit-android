package com.citious.converfit.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class ConversationModel {

    String conversationKey = "";
    String avatar = "";
    String lastMessageCreation = "";
    boolean flagNewMesssageUser = false;
    String lastMessage = "";
    String lastUpdate = "0";
    String userKey = "";
    String fname = "";
    String lname = "";
    String conectionStatus = "";


    public ConversationModel(JSONObject aDict) {
        try {
            this.conversationKey = aDict.getString("conversation_key");
            this.lastMessageCreation = aDict.getString("last_update");
            String aux = aDict.getString("flag_new_message_brand");
            if(aux.equalsIgnoreCase("1")) {
                this.flagNewMesssageUser = true;
            }else{
                this.flagNewMesssageUser = false;
            }
            String aLastMessage = aDict.getString("last_message");
            if(aLastMessage.equals("[::image]")) {
                this.lastMessage = "\uD83D\uDCF7";
            }else if(aLastMessage.equals("[::document]")) {
                this.lastMessage = "\uD83D\uDCCE";
            }else if(aLastMessage.equals("[::poll]")){
                this.lastMessage = "\uD83D\uDCCB";
            }else if (aLastMessage.equalsIgnoreCase("[::video]")){
                this.lastMessage = "\uD83D\uDCF9";
            }else {
                this.lastMessage = aLastMessage;
            }
            this.lastUpdate = "0";

            JSONObject userDict = aDict.getJSONObject("user");
            this.userKey = userDict.getString("user_key");
            this.avatar = userDict.getString("avatar");
            conectionStatus = userDict.getString("connection-status");

            this.fname = aDict.getString("user_fname");
            this.lname = aDict.getString("user_lname");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ConversationModel(String aConversationKey, String anAvatar, String aLastMessageCreation, boolean aFlagNewMessageUser, String aLastMessage, String aLastUpdate,
                             String anUserKey, String aFname, String aLname, String conectionStatus) {
        this.conversationKey = aConversationKey;
        this.avatar = anAvatar;
        this.lastMessageCreation = aLastMessageCreation;
        this.flagNewMesssageUser = aFlagNewMessageUser;
        this.lastMessage = aLastMessage;
        this.lastUpdate = aLastUpdate;
        this.userKey = anUserKey;
        this.fname = aFname;
        this.lname = aLname;
        this.conectionStatus = conectionStatus;
    }

    public String getConversationKey() {
        return conversationKey;
    }

    public void setConversationKey(String conversationKey) {
        this.conversationKey = conversationKey;
    }

    public String getAvatar() {
        return avatar;
    }


    public String getLastMessageCreation() {
        return lastMessageCreation;
    }


    public boolean isFlagNewMesssageUser() {
        return flagNewMesssageUser;
    }


    public String getLastMessage() {
        return lastMessage;
    }


    public String getLastUpdate() {
        return lastUpdate;
    }


    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getFname() {
        return fname;
    }


    public String getLname() {
        return lname;
    }

    public String getConectionStatus() {
        return conectionStatus;
    }
}
