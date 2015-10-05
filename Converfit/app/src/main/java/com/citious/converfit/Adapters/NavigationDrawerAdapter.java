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
import android.widget.ImageView;
import android.widget.TextView;
import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.Actividades.Conversations.ListMessagesAcitity;
import com.citious.converfit.Models.NavDraweItem;
import java.util.Collections;
import java.util.List;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDraweItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    NavDraweItem current;

    public NavigationDrawerAdapter(Context context, List<NavDraweItem> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    /*
    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }
    */

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        current = data.get(position);
        if(position == 0){
            holder.cabecera.setVisibility(View.VISIBLE);
            if(current.getConectionStatus().equalsIgnoreCase("mobile")){
                holder.cabecera.setText(context.getResources().getString(R.string.citious_app));
            }
        }else {
            boolean cambiarTituloCabecera = !current.getConectionStatus().equalsIgnoreCase(data.get(position - 1).getConectionStatus());
            if(cambiarTituloCabecera){
                holder.cabecera.setVisibility(View.VISIBLE);
            }else{
                holder.cabecera.setVisibility(View.GONE);
            }
        }

        holder.userName.setText(current.getUserName());
        holder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarConversacion(position);
            }
        });

        byte[] decodedString = Base64.decode(current.getAvatar(), Base64.DEFAULT);
        Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        if(foto != null) {
            holder.iconoUser.setImageBitmap(Utils.getRoundedBitmap(foto));
        }
        holder.iconoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarConversacion(position);
            }
        });
        //holder.hora.setText(Utils.devolverTiempo(current.getHoraConectado()));
        holder.hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarConversacion(position);
            }
        });

        if(current.getConectionStatus().equalsIgnoreCase("online")){
            holder.iconoStatus.setImageResource(R.drawable.connection_status_online_50);
        }else if(current.getConectionStatus().equalsIgnoreCase("offline")){
            holder.iconoStatus.setImageResource(R.drawable.connection_status_offline_50);
        }else if(current.getConectionStatus().equalsIgnoreCase("inactive")){
            holder.iconoStatus.setImageResource(R.drawable.connection_status_inactive_50);
        }else{
            holder.iconoStatus.setImageResource(R.drawable.connection_status_mobile_quickview_50);
        }

        holder.iconoStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarConversacion(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cabecera;
        TextView userName;
        ImageView iconoUser;
        ImageView iconoStatus;
        TextView hora;

        public MyViewHolder(View itemView) {
            super(itemView);
            cabecera = (TextView) itemView.findViewById(R.id.cabecera_recycled_drawer);
            userName = (TextView) itemView.findViewById(R.id.txt_brand_name_drawer);
            iconoUser = (ImageView) itemView.findViewById(R.id.img_brand_avatar_drawer);
            iconoStatus = (ImageView)itemView.findViewById(R.id.bntimg_brand_fav_list_drawer);
            hora = (TextView) itemView.findViewById(R.id.hora_usuario_conectado_drawer);
        }
    }

    private void lanzarConversacion(int position){
        String userKey = data.get(position).getUserKey();
        ConversationsSqlite accesoDatosConversations = new ConversationsSqlite(context);
        String conversationKey = accesoDatosConversations.existeConversacionDeUsuario(userKey);
        String brandName = data.get(position).getUserName();
        //Creamos el intent a lista mensajes
        Intent miListMessagesIntent = new Intent(context, ListMessagesAcitity.class);
        if(conversationKey.isEmpty()){
            miListMessagesIntent.putExtra("elegibleFavoritesOrigin", false);
        }else{
            miListMessagesIntent.putExtra("conversationKey", conversationKey);
        }
        miListMessagesIntent.putExtra("brandName", brandName);
        miListMessagesIntent.putExtra("userkey", userKey);
        context.startActivity(miListMessagesIntent);
    }
}