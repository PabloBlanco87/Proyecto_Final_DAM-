package com.ilerna.proyectodam.adaptersUsuarios;

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

import static android.provider.Settings.System.getString;

//Esta clase la creamos para la actividad donde nos aparecen/haremos búsqueda de los alumnos
public class AlumnoAdapter extends RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>
        implements View.OnClickListener {

    private Context contextoAlumno;
    private List<Alumno> alumnoList;
    private View.OnClickListener listener;

    public AlumnoAdapter(Context contextoAlumno, List<Alumno> alumnoList) {
        this.contextoAlumno = contextoAlumno;
        this.alumnoList = alumnoList;
    }

    @NonNull
    @Override
    public AlumnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contextoAlumno).inflate(R.layout.recyclerview_alumnos,
                parent, false);
        view.setOnClickListener(this);
        return new AlumnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlumnoViewHolder holder, int position) {
        //Holder para enviar nombre, apellido, estudios terminados, provincia y edad de los alumnos
        Alumno alumno = alumnoList.get(position);
        holder.textViewName.setText(alumno.getNombre());
        holder.textViewApellido.setText(alumno.getApellidos());
        holder.textViewGenre.setText(contextoAlumno.getString(R.string.show_estudios) + alumno.getEstudiosTerminados());
        holder.textViewAge.setText(contextoAlumno.getString(R.string.show_edad) + alumno.getEdad());
        holder.textViewCountry.setText(contextoAlumno.getString(R.string.show_provincia) + alumno.getLugar());
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


    class AlumnoViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName, textViewGenre, textViewAge, textViewCountry, textViewApellido;

        public AlumnoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewGenre = itemView.findViewById(R.id.text_view_genre);
            textViewAge = itemView.findViewById(R.id.text_view_age);
            textViewCountry = itemView.findViewById(R.id.text_view_country);
            textViewApellido = itemView.findViewById(R.id.text_view_apellido);
        }
    }
}