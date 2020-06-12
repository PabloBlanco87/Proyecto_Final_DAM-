package com.ilerna.proyectodam.adaptersChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.chat.MensajeRecibir;
import com.ilerna.proyectodam.constantes.Constantes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Esta clase la creamos para la actividad donde nos aparece el chat con la conversación actual
public class AdapterMensajes extends RecyclerView.Adapter<HolderMensaje> {

    private List<MensajeRecibir> listMensaje = new ArrayList<>();
    private Context contextoMensajes;
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public AdapterMensajes(Context contextoMensajes) {
        this.contextoMensajes = contextoMensajes;
    }

    public void addMensaje(MensajeRecibir m) {
        listMensaje.add(m);
        notifyItemInserted(listMensaje.size());
    }

    @NonNull
    @Override
    public HolderMensaje onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //En getItemViewType(línea 70) bifurcamos la apariencia del cardview
        if (viewType == 1) {
            view = LayoutInflater.from(contextoMensajes).inflate(R.layout.cardview_mensajes_emisor,
                    parent,
                    false);
        } else {
            view = LayoutInflater.from(contextoMensajes).inflate(R.layout.cardview_mensajes_receptor,
                    parent,
                    false);
        }
        return new HolderMensaje(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMensaje holder, int position) {
        //Holder para enviar los datos: nombre, mensaje y hora
        holder.getNombreUsuario().setText(listMensaje.get(position).getNombre());
        holder.getMensajeUsuario().setText(listMensaje.get(position).getMensaje());
        Long codigoHora = listMensaje.get(position).getHora();
        Date d = new Date(codigoHora);
        SimpleDateFormat sdf = new SimpleDateFormat(Constantes.FORMATO_HORA);
        holder.getHoraMensaje().setText(sdf.format(d));
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

    @Override
    public int getItemViewType(int position) {
        //Condición para bifurcar mensajes enviados, o recibidos
        String usuario = user.getUid();
        if (listMensaje.get(position).getId().equals(usuario)) {
            return 1;
        } else {
            return -1;
        }
    }
}
