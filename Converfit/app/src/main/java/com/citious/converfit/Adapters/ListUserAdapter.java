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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.citious.converfit.AccesoDatos.Sqlite.ConversationsSqlite;
import com.citious.converfit.Actividades.Conversations.ListMessagesAcitity;
import com.citious.converfit.Models.UserModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.MyViewHolder> {

    List<UserModel> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context miContext;
    UserModel current;

    public ListUserAdapter(Context aContext, ArrayList<UserModel> misItems) {
        this.miContext = aContext;
        inflater = LayoutInflater.from(miContext);
        this.data = misItems;
    }

    @Override
    public ListUserAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.list_favorites_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListUserAdapter.MyViewHolder myViewHolder, final int position) {
        current = data.get(position);
        if(position == 0){
            myViewHolder.cabecera.setVisibility(View.VISIBLE);
            if(current.getConectionStatus().equalsIgnoreCase("mobile")){
                myViewHolder.cabecera.setText(miContext.getResources().getString(R.string.citious_app));
            }
        }else {
            boolean cambiarTituloCabecera = !current.getConectionStatus().equalsIgnoreCase(data.get(position - 1).getConectionStatus());
            if(cambiarTituloCabecera){
                myViewHolder.cabecera.setVisibility(View.VISIBLE);
            }else{
                myViewHolder.cabecera.setVisibility(View.GONE);
            }
        }

        //Cargamos el icono
        byte[] decodedString = Base64.decode(current.getAvatar(), Base64.DEFAULT);
        Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        if(foto != null) {
            myViewHolder.iconoUser.setImageBitmap(Utils.getRoundedBitmap(foto));
            myViewHolder.iconoUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lanzarConversacion(position);
                }
            });
        }

        //Cargamos el nombre del usuario
        String nombreUsuarioCompuesto = current.getUserName();
        myViewHolder.userName.setText(nombreUsuarioCompuesto);
        myViewHolder.userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarConversacion(position);
            }
        });

        if(current.getConectionStatus().equalsIgnoreCase("online")){
            myViewHolder.iconoStatus.setImageResource(R.drawable.connection_status_online_50);
        }else if(current.getConectionStatus().equalsIgnoreCase("offline")){
            myViewHolder.iconoStatus.setImageResource(R.drawable.connection_status_offline_50);
        }else if(current.getConectionStatus().equalsIgnoreCase("inactive")){
            myViewHolder.iconoStatus.setImageResource(R.drawable.connection_status_inactive_50);
        }else{
            myViewHolder.iconoStatus.setImageResource(R.drawable.connection_status_mobile_50);
        }
        myViewHolder.iconoStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarConversacion(position);
            }
        });

        myViewHolder.hora.setText(Utils.devolverTiempo(current.getHoraConectado()));
        myViewHolder.hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarConversacion(position);
            }
        });
        myViewHolder.ultimaActividad.setText(current.getLast_page_title());
        myViewHolder.ultimaActividad.setOnClickListener(new View.OnClickListener() {
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
        ImageView iconoUser;
        TextView userName;
        TextView ultimaActividad;
        ImageView iconoStatus;
        TextView hora;

        public MyViewHolder(View itemView) {
            super(itemView);
            cabecera = (TextView) itemView.findViewById(R.id.cabecera_list_users);
            iconoUser = (ImageView) itemView.findViewById(R.id.img_brand_avatar_list_favorites_layout);
            userName = (TextView) itemView.findViewById(R.id.txt_brand_name_list_favorites_layout);
            ultimaActividad = (TextView) itemView.findViewById(R.id.txt_last_page_User);
            iconoStatus = (ImageView)itemView.findViewById(R.id.bntimg_brand_fav_list_favorites_layout);
            hora = (TextView) itemView.findViewById(R.id.hora_usuario_conectado_chat_tab);
        }
    }

    private void lanzarConversacion(int position){
        String userKey = data.get(position).getUserKey();
        ConversationsSqlite accesoDatosConversations = new ConversationsSqlite(miContext);
        String conversationKey = accesoDatosConversations.existeConversacionDeUsuario(userKey);
        String brandName = data.get(position).getUserName();
        //Creamos el intent a lista mensajes
        Intent miListMessagesIntent = new Intent(miContext, ListMessagesAcitity.class);
        if(conversationKey.isEmpty()){
            miListMessagesIntent.putExtra("elegibleFavoritesOrigin", true);
        }else{
            miListMessagesIntent.putExtra("conversationKey", conversationKey);
        }
        miListMessagesIntent.putExtra("brandName", brandName);
        miListMessagesIntent.putExtra("userkey", userKey);
        miContext.startActivity(miListMessagesIntent);
    }
}
