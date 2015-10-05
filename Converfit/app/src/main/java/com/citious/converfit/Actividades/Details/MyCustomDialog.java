package com.citious.converfit.Actividades.Details;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.citious.converfit.R;

public class MyCustomDialog extends AlertDialog.Builder {

    public MyCustomDialog(Context context,String title,String message) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View viewDialog = inflater.inflate(R.layout.custom_alert_layout, null, false);

        TextView titleTextView = (TextView)viewDialog.findViewById(R.id.custom_alert_tittle);
        titleTextView.setText(title);
        TextView messageTextView = (TextView)viewDialog.findViewById(R.id.custom_alert_message);
        messageTextView.setText(message);

        this.setCancelable(false);

        this.setView(viewDialog);

    }
}