package com.ilerna.proyectodam.adaptersUsuarios;

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

//Esta clase la creamos para la actividad donde nos aparecen/haremos búsqueda de los profesores
public class ProfesorAdapter extends RecyclerView.Adapter<ProfesorAdapter.ProfesorViewHolder>
        implements View.OnClickListener {

    private Context contextoProfesores;
    private List<Profesor> profesorList;
    private View.OnClickListener listener;

    public ProfesorAdapter(Context contextoProfesores, List<Profesor> profesorList) {
        this.contextoProfesores = contextoProfesores;
        this.profesorList = profesorList;
    }

    @NonNull
    @Override
    public ProfesorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contextoProfesores).inflate(R.layout.recyclerview_profesores,
                parent, false);
        view.setOnClickListener(this);
        return new ProfesorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfesorViewHolder holder, int position) {
        //Holder para enviar nombre, apellido, estudios terminados, provincia y edad de los profesores
        Profesor profesor = profesorList.get(position);
        holder.textViewName.setText(profesor.getNombre());
        holder.textViewApellido.setText(profesor.getApellidos());
        holder.textViewGenre.setText(contextoProfesores.getString(R.string.show_experiencia) + profesor.getAniosExperiencia());
        holder.textViewAge.setText(contextoProfesores.getString(R.string.show_edad) + profesor.getEdad());
        holder.textViewCountry.setText(contextoProfesores.getString(R.string.show_provincia)  + profesor.getLugar());
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


    class ProfesorViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName, textViewGenre, textViewAge, textViewCountry, textViewApellido;

        public ProfesorViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name_profesor);
            textViewGenre = itemView.findViewById(R.id.text_view_experiencia_profesor);
            textViewAge = itemView.findViewById(R.id.text_view_age_profesor);
            textViewCountry = itemView.findViewById(R.id.text_view_country_profesor);
            textViewApellido = itemView.findViewById(R.id.text_view_apellido_profesor);
        }
    }
}