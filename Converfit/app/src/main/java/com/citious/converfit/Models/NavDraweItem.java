package com.citious.converfit.Models;


public class NavDraweItem {
    private String userKey = "";
    private String userName = "";
    private String avatar = "";
    private String conectionStatus = "";
    private String horaConectado = "";


    public NavDraweItem() {

    }

    public NavDraweItem(String userKey, String userName, String avatar, String conectionStatus, String horaConectado) {
        this.userKey = userKey;
        this.userName = userName;
        this.avatar = avatar;
        this.conectionStatus = conectionStatus;
        this.horaConectado = horaConectado;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setConectionStatus(String conectionStatus) {
        this.conectionStatus = conectionStatus;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setHoraConectado(String horaConectado) {
        this.horaConectado = horaConectado;
    }
}
