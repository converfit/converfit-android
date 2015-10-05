package com.citious.converfit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.citious.converfit.R;
import java.util.ArrayList;

public class ListMenuSettingsAdapters extends BaseAdapter {

    protected Context miContext;
    private ArrayList<String> titleOpcionMenu;

    public ListMenuSettingsAdapters(Context miContext, ArrayList<String> opciones){
        this.miContext = miContext;
        this.titleOpcionMenu = opciones;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return titleOpcionMenu.get(position);
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
            vi = inflater.inflate(R.layout.celda_menu_settings, null);
        }

        TextView opcion = (TextView) vi.findViewById(R.id.txt_opcion_menu_settings);
        if(position == 0){
            opcion.setText(titleOpcionMenu.get(0));
        }else{
            opcion.setText(titleOpcionMenu.get(1));
        }

        return vi;
    }
}
