package com.ilerna.proyectodam.adaptersChat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ilerna.proyectodam.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderMensaje extends RecyclerView.ViewHolder {

    private TextView nombreUsuario, mensajeUsuario, horaMensaje;
    private CircleImageView fotoMensaje;

    public HolderMensaje(@NonNull View itemView) {
        super(itemView);
        nombreUsuario =(TextView)itemView.findViewById(R.id.nombre_mensaje);
        mensajeUsuario =(TextView)itemView.findViewById(R.id.mensaje_mensaje);
        horaMensaje =(TextView)itemView.findViewById(R.id.hora_mensaje);
        fotoMensaje=(CircleImageView)itemView.findViewById(R.id.foto_perfil_mensaje);
    }

    public TextView getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(TextView nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public TextView getMensajeUsuario() {
        return mensajeUsuario;
    }

    public void setMensajeUsuario(TextView mensajeUsuario) {
        this.mensajeUsuario = mensajeUsuario;
    }

    public TextView getHoraMensaje() {
        return horaMensaje;
    }

    public void setHoraMensaje(TextView horaMensaje) {
        this.horaMensaje = horaMensaje;
    }

    public CircleImageView getFotoMensaje() {
        return fotoMensaje;
    }

    public void setFotoMensaje(CircleImageView fotoMensaje) {
        this.fotoMensaje = fotoMensaje;
    }
}
