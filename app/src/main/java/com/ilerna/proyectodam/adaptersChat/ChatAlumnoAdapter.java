package com.ilerna.proyectodam.adaptersChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.alumno.Alumno;

import java.util.List;

//Esta clase la creamos para la actividad donde nos aparecen todos los chats abiertos y nos vuelque los chats con los alumnos
public class ChatAlumnoAdapter extends RecyclerView.Adapter<ChatAlumnoAdapter.ChatAlumnoViewHolder>
        implements View.OnClickListener {

    private Context contextoChatAlumno;
    private List<Alumno> alumnoList;
    private View.OnClickListener listener;

    public ChatAlumnoAdapter(Context contextoChatAlumno, List<Alumno> alumnoList) {
        this.contextoChatAlumno = contextoChatAlumno;
        this.alumnoList = alumnoList;
    }

    @NonNull
    @Override
    public ChatAlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contextoChatAlumno).inflate(R.layout.recyclerview_chat_alumnos,
                parent, false);
        view.setOnClickListener(this);
        return new ChatAlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAlumnoViewHolder holder, int position) {
        //Holder para enviar el dato: nombre
        Alumno alumno = alumnoList.get(position);
        holder.textViewNameAlumno.setText(alumno.getNombre());
    }

    @Override
    public int getItemCount() {
        return alumnoList.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    //Función para poder hacer click en el Recyclerview
    @Override
    public void onClick(View v) {
        if (listener != null)
            listener.onClick(v);
    }

    class ChatAlumnoViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNameAlumno;

        public ChatAlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNameAlumno = itemView.findViewById(R.id.text_chat_name_alumno);
        }
    }
}
