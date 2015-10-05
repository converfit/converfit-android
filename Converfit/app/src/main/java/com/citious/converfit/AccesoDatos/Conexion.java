package com.citious.converfit.AccesoDatos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Conexion {

    public static boolean isInternetAvailable(Context miContexto){
        boolean hayConexion = false;
        ConnectivityManager conMgr = (ConnectivityManager) miContexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        try{
            NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED){
                hayConexion = true;
            }else{
                hayConexion = false;
            }
        }catch (Exception e){
         //Excepcion
        }

        return hayConexion;
    }
}
