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
    String assignedFname = "";
    String assignedId = "";
    String assignedLname = "";
    String userKey = "";
    String fname = "";
    String lname = "";


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

            JSONObject assignedAdminDict = aDict.getJSONObject("assigned_admin");
            this.assignedFname = assignedAdminDict.getString("fname");
            this.assignedId = assignedAdminDict.getString("id_admin");
            this.assignedLname = assignedAdminDict.getString("lname");

            JSONObject userDict = aDict.getJSONObject("user");
            this.userKey = userDict.getString("user_key");
            this.avatar = userDict.getString("avatar");

            this.fname = aDict.getString("user_fname");
            this.lname = aDict.getString("user_lname");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ConversationModel(String aConversationKey, String anAvatar, String aLastMessageCreation, boolean aFlagNewMessageUser, String aLastMessage, String aLastUpdate,
                             String anAssignedFname, String anAssignedId, String anAssignedLname, String anUserKey, String aFname, String aLname) {
        this.conversationKey = aConversationKey;
        this.avatar = anAvatar;
        this.lastMessageCreation = aLastMessageCreation;
        this.flagNewMesssageUser = aFlagNewMessageUser;
        this.lastMessage = aLastMessage;
        this.lastUpdate = aLastUpdate;
        this.assignedFname = anAssignedFname;
        this.assignedId = anAssignedId;
        this.assignedLname = anAssignedLname;
        this.userKey = anUserKey;
        this.fname = aFname;
        this.lname = aLname;
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


    public String getAssignedFname() {
        return assignedFname;
    }


    public String getAssignedId() {
        return assignedId;
    }


    public String getAssignedLname() {
        return assignedLname;
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
}
