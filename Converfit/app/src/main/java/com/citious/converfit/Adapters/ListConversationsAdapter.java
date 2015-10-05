package com.citious.converfit.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.citious.converfit.Models.ConversationModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import java.util.ArrayList;

public class ListConversationsAdapter extends BaseAdapter {

    protected Context miContext;
    protected ArrayList<ConversationModel> conversationsList;

    public ListConversationsAdapter(Context aContext, ArrayList<ConversationModel> misItems){
        this.miContext = aContext;
        this.conversationsList = misItems;
    }

    @Override
    public int getCount() {
        return conversationsList.size();
    }

    @Override
    public Object getItem(int position) {
        return conversationsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) miContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.list_conversations_layout,null);
        }

        //Obtenemos el listado de datos de una posicion
        ConversationModel item = conversationsList.get(position);

        //Cargamos el icono
        ImageView icono = (ImageView)vi.findViewById(R.id.img_brand_avatar_list_conversations_layout);
        byte[] decodedString = Base64.decode(conversationsList.get(position).getAvatar(), Base64.DEFAULT);
        Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        if(foto != null){
            icono.setImageBitmap(Utils.getRoundedBitmap(foto));
        }

        //Cargamos el nombre del usuario
        TextView name = (TextView) vi.findViewById(R.id.txt_brand_name_list_conversations_layout);
        name.setText(item.getFname() + " " + item.getLname());

        //Cargamos el ultimo mensaje
        TextView lastMessage = (TextView) vi.findViewById(R.id.txt_last_message_list_conversations_layout);
        String mensaje = item.getLastMessage();
        if(mensaje.length() > 30){
            mensaje = mensaje.substring(0,27).concat("...");

        }
        lastMessage.setText(mensaje);
        //Cargamos la fecha del ultimo mensaje
        TextView lastMessageDate = (TextView) vi.findViewById(R.id.txt_last_update_list__conversations_layout);
        lastMessageDate.setText(item.getLastMessageCreation());

        //Si es un mensaje nuevo cambiamos los colores
        if(item.isFlagNewMesssageUser()){
            name.setTypeface(null, Typeface.BOLD);
            name.setTextColor(miContext.getResources().getColor(R.color.Negro_nuevo_mensjae));
            lastMessage.setTypeface(null, Typeface.BOLD);
            lastMessage.setTextColor(miContext.getResources().getColor(R.color.Negro_nuevo_mensjae));
            lastMessageDate.setTextColor(miContext.getResources().getColor(R.color.Negro_nuevo_mensjae));
        }else{
            name.setTypeface(null, Typeface.NORMAL);
            name.setTextColor(miContext.getResources().getColor(R.color.nombres_chat_tab));
            lastMessage.setTypeface(null, Typeface.NORMAL);
            lastMessage.setTextColor(miContext.getResources().getColor(R.color.GrisMensajeAlert));
            lastMessageDate.setTextColor(miContext.getResources().getColor(R.color.GrisMensajeAlert));
        }

        ImageView conectionStatus = (ImageView) vi.findViewById(R.id.bntimg_brand_fav_list_conversations_layout);
        if(item.getConectionStatus().equalsIgnoreCase("online")){
            conectionStatus.setImageResource(R.drawable.connection_status_online_50);
        }else if(item.getConectionStatus().equalsIgnoreCase("offline")){
            conectionStatus.setImageResource(R.drawable.connection_status_offline_50);
        }else if(item.getConectionStatus().equalsIgnoreCase("inactive")){
            conectionStatus.setImageResource(R.drawable.connection_status_inactive_50);
        }else{
            conectionStatus.setImageResource(R.drawable.connection_status_mobile_50);
        }



        return vi;
    }
}
