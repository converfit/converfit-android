package com.citious.converfit.Actividades.Details;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.citious.converfit.R;

public class MyCustomAlertSheet extends AlertDialog.Builder {
    Button miBtnDesactivar;
    Button miBtnCancelar;
    public MyCustomAlertSheet(final Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View viewDialog = inflater.inflate(R.layout.custom_alert_sheet_layout, null, false);

        miBtnDesactivar = (Button) viewDialog.findViewById(R.id.btn_desactivar_drawer_alert_sheet);

        miBtnCancelar = (Button) viewDialog.findViewById(R.id.btn_cancelar_drawer_alert_sheet);

        this.setCancelable(false);

        this.setView(viewDialog);
    }
}