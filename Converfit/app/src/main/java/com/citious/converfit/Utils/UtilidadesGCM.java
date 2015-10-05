package com.citious.converfit.Utils;

import android.content.Context;
import android.content.Intent;

public class UtilidadesGCM {

    static public boolean actividadAbierta = false;
    static public final String DISPLAY_MESSAGE_ACTION = "com.citious.DISPLAY_MESSAGE";

    public static void mostrarMensaje(Context miContext, String mensaje){
        Intent miIntent = new Intent(DISPLAY_MESSAGE_ACTION);
        miIntent.putExtra("mensaje",mensaje);
        miContext.sendBroadcast(miIntent);
    }
}
