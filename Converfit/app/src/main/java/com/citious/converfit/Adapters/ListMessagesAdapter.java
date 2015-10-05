package com.citious.converfit.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.citious.converfit.Actividades.Conversations.ListMessagesAcitity;
import com.citious.converfit.Actividades.Conversations.MostrarImagenActivity;
import com.citious.converfit.Actividades.Details.VisorPDFActivity;
import com.citious.converfit.Models.MensajeModel;
import com.citious.converfit.R;
import com.citious.converfit.Utils.Utils;
import java.util.ArrayList;
import static com.citious.converfit.Utils.Utils.avatarString;

public class ListMessagesAdapter extends BaseAdapter {

    protected Context miContext;
    protected ArrayList<MensajeModel> messageList;
    protected ListView miListView;
    protected ViewGroup miHeader;
    protected EditText miEdt;
    protected String userKey;
    final int VIEW_TYPE_ROW_1 = 0;//Imagen Usuario
    final int VIEW_TYPE_ROW_2 = 1;//Texto Usuario
    final int VIEW_TYPE_ROW_3 = 2;//Imagen Otro Usuario
    final int VIEW_TYPE_ROW_4 = 3;//Texto Otro Usuario
    final int VIEW_TYPE_ROW_5 = 4;//poll o poll_closed
    final int VIEW_TYPE_ROW_6 = 5;//response
    final int VIEW_TYPE_ROW_7 = 6;//document_pdf
    final String PDF_ACTION = "Abrir Pdf.";

    public ListMessagesAdapter(Context aContext, ArrayList<MensajeModel> misItems, ListView miListView, ViewGroup miHeader, EditText miEdt, String userKey) {
        this.miContext = aContext;
        this.messageList = misItems;
        this.miListView = miListView;
        this.miHeader = miHeader;
        this.miEdt = miEdt;
        this.userKey = userKey;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        if (!messageList.get(position).getSender().equalsIgnoreCase("user")) {
            if (messageList.get(position).getType().equalsIgnoreCase("jpeg_base64")) {
                return VIEW_TYPE_ROW_1; //Imagen Usuario
            } else if (messageList.get(position).getType().equalsIgnoreCase("text")) {
                return VIEW_TYPE_ROW_2; //Texto Usuario
            } else if (messageList.get(position).getType().equalsIgnoreCase("poll") || messageList.get(position).getType().equalsIgnoreCase("poll_closed")) {
                return VIEW_TYPE_ROW_5;     //poll brand
            } else {
                return VIEW_TYPE_ROW_7;
            }
        } else {
            if (messageList.get(position).getType().equalsIgnoreCase("jpeg_base64")) {
                return VIEW_TYPE_ROW_3;     //Imagen Otro Usuario
            } else if (messageList.get(position).getType().equalsIgnoreCase("text")) {
                return VIEW_TYPE_ROW_4;     //Texto Otro usuario
            } else {
                return VIEW_TYPE_ROW_6;     //poll response
            }
        }
}

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) miContext.getSystemService(miContext.LAYOUT_INFLATER_SERVICE);
        final MensajeModel item = messageList.get(position);

        int type = getItemViewType(position);

        if (type == VIEW_TYPE_ROW_1) {          //Imagen Usuario
            if(convertView == null){
                convertView = inflater.inflate(R.layout.imagen_usuario,null);
            }
            ImageView miImage = (ImageView) convertView.findViewById(R.id.img_imagen_usuario);
            byte[] decodedString = Base64.decode(item.getContent(), Base64.DEFAULT);
            Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            miImage.setImageBitmap(foto);
            miImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent miDetalleImagenInten = new Intent(miContext, MostrarImagenActivity.class);
                    avatarString = item.getContent();
                    miContext.startActivity(miDetalleImagenInten);
                }
            });

            //Boton reenviar texto
            final Button miBtnReenviarImagen = (Button) convertView.findViewById(R.id.btn_reenviar_mensaje_imagen);
            miBtnReenviarImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ListMessagesAcitity().reenviarMensajes(miContext, item.getConversationKey(), userKey, miListView, miHeader, miEdt);
                }
            });

            if(item.isEnviado()){
                miBtnReenviarImagen.setVisibility(View.GONE);
            }else{
                miBtnReenviarImagen.setVisibility(View.VISIBLE);
            }
        }else if (type == VIEW_TYPE_ROW_2) {    //Texto Usuario
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.texto_mensaje_usuario, null);
            }
            TextView mensaje = (TextView) convertView.findViewById(R.id.txt_mensaje_usuario);
            mensaje.setText(item.getContent());
            TextView hora = (TextView) convertView.findViewById(R.id.txt_hora_mensaje_usuario);
            hora.setText(Utils.devolverTiempo(item.getCreated()));

            //Boton reenviar texto
            final Button miBtnReenviarTexto = (Button) convertView.findViewById(R.id.btn_reenviar_mensaje_texto);
            miBtnReenviarTexto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ListMessagesAcitity().reenviarMensajes(miContext, item.getConversationKey(), userKey, miListView, miHeader, miEdt);
                }
            });

            if(item.isEnviado()){
                miBtnReenviarTexto.setVisibility(View.GONE);
            }else{
                miBtnReenviarTexto.setVisibility(View.VISIBLE);
            }

            TextView adminSender = (TextView) convertView.findViewById(R.id.admin_sender_mensaje_usuario);
            adminSender.setText(item.getFname() + " " + item.getLname());
        }else if (type == VIEW_TYPE_ROW_3){     //Imagen Otro Usuario
            if(convertView == null){
                convertView = inflater.inflate(R.layout.imagen_otro_usuario,null);
            }
            ImageView miImage = (ImageView) convertView.findViewById(R.id.img_imagen_otro_usuario);
            byte[] decodedString = Base64.decode(messageList.get(position).getContent(), Base64.DEFAULT);
            Bitmap foto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            miImage.setImageBitmap(foto);
            miImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent miDetalleImagenInten = new Intent(miContext, MostrarImagenActivity.class);
                    avatarString = item.getContent();
                    miContext.startActivity(miDetalleImagenInten);
                }
            });

        }else if (type == VIEW_TYPE_ROW_4){     //Texto Otro usuario
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.texto_mensaje_otro_usuario, null);
            }
            TextView mensaje = (TextView) convertView.findViewById(R.id.txt_mensaje_otro_usuario);
            mensaje.setText(item.getContent());
            TextView hora = (TextView) convertView.findViewById(R.id.txt_hora_mensaje_otro_usuario_usuario);
            hora.setText(Utils.devolverTiempo(item.getCreated()));
        }else if(type == VIEW_TYPE_ROW_5 ){//Poll y poll_closed
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.texto_mensaje_usuario, null);
            }
            String tituloEncuesta = "<b><small>" + miContext.getResources().getString(R.string.encuesta_enviada) + "</small></b><br>";
            TextView mensaje = (TextView) convertView.findViewById(R.id.txt_mensaje_usuario);
            mensaje.setText(Html.fromHtml("<body>" + tituloEncuesta + "</br>" + item.getContent() + "</body>"));
            TextView hora = (TextView) convertView.findViewById(R.id.txt_hora_mensaje_usuario);
            hora.setText(Utils.devolverTiempo(item.getCreated()));
            TextView adminSender = (TextView) convertView.findViewById(R.id.admin_sender_mensaje_usuario);
            adminSender.setText(item.getFname() + " " + item.getLname());
            //Boton reenviar texto
            final Button miBtnReenviarTexto = (Button) convertView.findViewById(R.id.btn_reenviar_mensaje_texto);
            miBtnReenviarTexto.setVisibility(View.GONE);

        }else if( type == VIEW_TYPE_ROW_6){//poll_response
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.texto_mensaje_otro_usuario, null);
            }
            String[] contenidoArray = item.getContent().split("::");
            String tituloEncueseta = "<b><small>" + miContext.getResources().getString(R.string.encuesta_enviada) + "</small></b><br>";
            String tituloRespuesta = "<b><small>" + miContext.getResources().getString(R.string.encuesta_recibida) + " (" + contenidoArray[1] + "/5):</small></b><br>";
            String textoFomateado ="<body>" + tituloEncueseta + contenidoArray[0] + "<br><br>" + tituloRespuesta + contenidoArray[2] + "</body>";
            TextView mensaje = (TextView) convertView.findViewById(R.id.txt_mensaje_otro_usuario);
            mensaje.setText(Html.fromHtml(textoFomateado));
            TextView hora = (TextView) convertView.findViewById(R.id.txt_hora_mensaje_otro_usuario_usuario);
            hora.setText(Utils.devolverTiempo(item.getCreated()));

        } else if (type == VIEW_TYPE_ROW_7){
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.encuesta_pdf, null);
            }
            ImageView imagen = (ImageView)convertView.findViewById(R.id.img_encuesta_pdf);
            TextView titulo = (TextView) convertView.findViewById(R.id.txt_titulo_encuesta_pdf);
            titulo.setText(item.getContent());
            TextView accion = (TextView) convertView.findViewById(R.id.txt_accion_encuesta_pdf);
            TextView hora = (TextView) convertView.findViewById(R.id.txt_hora_mensaje_encuesta_pdf);
            hora.setText(Utils.devolverTiempo(item.getCreated()));
            accion.setText(PDF_ACTION);
            imagen.setImageResource(R.drawable.icon_pdf);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pdfIntent = new Intent(miContext, VisorPDFActivity.class);
                    String tituloPdf = item.getContent();
                    pdfIntent.putExtra("titulo", tituloPdf);
                    String messageKey = item.getMessageKey();
                    pdfIntent.putExtra("messageKey", messageKey);
                    miContext.startActivity(pdfIntent);
                }
            });
            }
        return convertView;
    }
}
