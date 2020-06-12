package com.ilerna.proyectodam.adaptersChat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.profesor.Profesor;

import java.util.List;

//Esta clase la creamos para la actividad donde nos aparecen todos los chats abiertos y nos vuelque los chats con los profesores
public class ChatProfesorAdapter extends RecyclerView.Adapter<ChatProfesorAdapter.ChatProfesorViewHolder>
        implements View.OnClickListener {

    private Context contextoChatProfesor;
    private List<Profesor> profesorList;
    private View.OnClickListener listener;

    public ChatProfesorAdapter(Context contextoChatProfesor, List<Profesor> profesorList) {
        this.contextoChatProfesor = contextoChatProfesor;
        this.profesorList = profesorList;
    }

    @NonNull
    @Override
    public ChatProfesorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contextoChatProfesor).inflate(R.layout.recyclerview_chat_profesores,
                parent, false);
        view.setOnClickListener(this);
        return new ChatProfesorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatProfesorViewHolder holder, int position) {
        //Holder para enviar el dato: nombre
        Profesor profesor = profesorList.get(position);
        holder.textViewName.setText(profesor.getNombre());
    }

    @Override
    public int getItemCount() {
        return profesorList.size();
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


    class ChatProfesorViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;

        public ChatProfesorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_chat_name_profesor);
        }
    }
}