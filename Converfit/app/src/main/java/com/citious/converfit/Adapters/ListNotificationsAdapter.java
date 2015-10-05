package com.citious.converfit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import com.citious.converfit.Actividades.ChatWeb.UserTimeLine;
import com.citious.converfit.Models.TimeLineModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;

public class ListNotificationsAdapter extends RecyclerView.Adapter<ListNotificationsAdapter.MyViewHolder> {
    protected Context miContext;
    protected ArrayList<TimeLineModel> postList;
    private LayoutInflater inflater;

    public ListNotificationsAdapter(Context context, ArrayList<TimeLineModel> misItems) {
        this.miContext = context;
        this.postList = misItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_time_line, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        //Obtenemos el listado de datos de una posicion
        TimeLineModel item = postList.get(position);
        //Cargamos el icono
        byte[] decodedString = Base64.decode(item.getAvatar(), Base64.DEFAULT);
        Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        if(foto != null){
            holder.iconoUser.setImageBitmap(Utils.getRoundedBitmap(foto));
        }
        holder.iconoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarUserTimeLine(position);
            }
        });
        holder.userName.setText(item.getUserNAme());
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarUserTimeLine(position);
            }
        });

        String horaTemp = Utils.devolverTiempo(item.getCreated());
        if(horaTemp.contains("seg")){
            String cadena = "segundos";
            if(horaTemp.equalsIgnoreCase("1 seg")){
                cadena = "segundo";
            }
            horaTemp = horaTemp.replace("seg", cadena);
        }else if(horaTemp.contains("min")){
            String cadena = "minutos";
            if(horaTemp.equalsIgnoreCase("1 min")){
                cadena = "minuto";
            }
            horaTemp = horaTemp.replace("min", cadena);
        }else if(horaTemp.contains("h")){
            String cadena = "horas";
            if(horaTemp.equalsIgnoreCase("1 h")){
                cadena = "hora";
            }
            horaTemp = horaTemp.replace("h", cadena);
        }
        holder.hora.setText(horaTemp);
        holder.hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarUserTimeLine(position);
            }
        });

        holder.miWebView.loadDataWithBaseURL("", item.getContent(), "text/html", "UTF-8", "");
        holder.miWebView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarUserTimeLine(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iconoUser;
        TextView userName;
        TextView hora;
        WebView miWebView;

        public MyViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.name_user_time_line);
            iconoUser = (ImageView) itemView.findViewById(R.id.avatar_time_line);
            hora = (TextView) itemView.findViewById(R.id.hora_last_update_user_time_line);
            miWebView = (WebView) itemView.findViewById(R.id.web_time_line);
        }
    }

    private void lanzarUserTimeLine(int tapPosicion){
        Intent miUserTimeLineIntent = new Intent(miContext, UserTimeLine.class);
        String userKey = postList.get(tapPosicion).getUserKey();
        miUserTimeLineIntent.putExtra("userkey", userKey);
        miContext.startActivity(miUserTimeLineIntent);
    }
}
