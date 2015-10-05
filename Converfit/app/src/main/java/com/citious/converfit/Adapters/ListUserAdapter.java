package com.citious.converfit.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class ListUserAdapter extends BaseAdapter {

    protected Context miContext;
    protected ArrayList<UserModel> userList;

    public ListUserAdapter(Context aContext, ArrayList<UserModel> misItems) {
        this.miContext = aContext;
        this.userList = misItems;
    }

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
}
