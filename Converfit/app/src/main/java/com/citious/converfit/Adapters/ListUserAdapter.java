package com.citious.converfit.Adapters;

import android.content.Context;
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
    public void onBindViewHolder(ListUserAdapter.MyViewHolder myViewHolder, int position) {
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
        }

        //Cargamos el nombre del usuario
        String nombreUsuarioCompuesto = current.getUserName();
        myViewHolder.userName.setText(nombreUsuarioCompuesto);

        if(current.getConectionStatus().equalsIgnoreCase("online")){
            myViewHolder.iconoStatus.setImageResource(R.drawable.connection_status_online_50);
        }else if(current.getConectionStatus().equalsIgnoreCase("offline")){
            myViewHolder.iconoStatus.setImageResource(R.drawable.connection_status_offline_50);
        }else if(current.getConectionStatus().equalsIgnoreCase("inactive")){
            myViewHolder.iconoStatus.setImageResource(R.drawable.connection_status_inactive_50);
        }else{
            myViewHolder.iconoStatus.setImageResource(R.drawable.connection_status_mobile_50);
        }

        myViewHolder.hora.setText(Utils.devolverTiempo(current.getHoraConectado()));
        myViewHolder.ultimaActividad.setText(current.getLast_page_title());
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

    /*
    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) miContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.list_favorites_layout, null);
        }

        //Obtenemos el listado de datos de una posicion
        UserModel item = userList.get(position);

        //Cargamos el icono
        ImageView icono = (ImageView) vi.findViewById(R.id.img_brand_avatar_list_favorites_layout);
        byte[] decodedString = Base64.decode(item.getAvatar(), Base64.DEFAULT);
        Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        //Cargamos el nombre del usuario
        String nombreUsuarioCompuesto = item.getFname() + " " + item.getLname();

        TextView name = (TextView) vi.findViewById(R.id.txt_brand_name_list_favorites_layout);
        name.setText(nombreUsuarioCompuesto);

        if(item.isUserBlocked()){
            if(foto != null) {
                icono.setImageBitmap(Utils.getRoundedBitmap(foto));
            }
            vi.setAlpha(0.5f);
        }else{
            if(foto != null) {
                icono.setImageBitmap(Utils.getRoundedBitmap(foto));
            }
            vi.setAlpha(1f);
        }

        ImageView statusImage = (ImageView) vi.findViewById(R.id.bntimg_brand_fav_list_favorites_layout);
        if(item.getConectionStatus().equalsIgnoreCase("online")){
            statusImage.setImageResource(R.drawable.connection_status_online_50);
        }else if(item.getConectionStatus().equalsIgnoreCase("offline")){
            statusImage.setImageResource(R.drawable.connection_status_offline_50);
        }else if(item.getConectionStatus().equalsIgnoreCase("inactive")){
            statusImage.setImageResource(R.drawable.connection_status_inactive_50);
        }else{
            statusImage.setImageResource(R.drawable.connection_status_mobile_50);
        }

        TextView horaUsuario = (TextView) vi.findViewById(R.id.hora_usuario_conectado_chat_tab);
        //horaUsuario.setText(Utils.devolverTiempo(item.getHoraConectado()));
        return vi;
    }
    */
}
