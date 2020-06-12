package com.ilerna.proyectodam.alumno;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ilerna.proyectodam.chat.MisChats;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.constantes.Constantes;

//Clase que nos permitirá consultar la información/el perfil de cada alumno
public class PerfilAlumno extends AppCompatActivity {

    private TextView tvNombre, tvApellidos, tvEdad, tvProvincia, tvCorreo, tvTelefono, tvEstudios, tvInfoAdicional;

    private DatabaseReference mDataBase;

    private static String idUsuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alumno_perfil);

        //Inicializamos Database
        mDataBase = FirebaseDatabase.getInstance().getReference();

        //Asignamos ids del layout
        tvNombre = (TextView) findViewById(R.id.consulta_nombre);
        tvApellidos = (TextView) findViewById(R.id.consulta_apellido);
        tvEdad = (TextView) findViewById(R.id.consulta_edad);
        tvProvincia = (TextView) findViewById(R.id.consulta_provincia);
        tvCorreo = (TextView) findViewById(R.id.consulta_correo);
        tvTelefono = (TextView) findViewById(R.id.consulta_telefono);
        tvEstudios = (TextView) findViewById(R.id.consulta_estudios);
        tvInfoAdicional = (TextView) findViewById(R.id.consulta_infoAdicional);

        //Recuperamos la información del id de usuario que nos han mandado desde ShowAlumnosActivity
        idUsuario = getIntent().getStringExtra(Constantes.PASA_INFO);
        //Utilizamos ese Uid (PK) que hemos recibido en el Intent para buscar en el nodo Alumnos, el alumnos seleccionado
        mDataBase.child(Constantes.PATH_ALUMNOS).child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child(Constantes.PATH_NOMBRE).getValue().toString();
                    String apellidos = dataSnapshot.child(Constantes.PATH_APELLIDOS).getValue().toString();
                    String edad = dataSnapshot.child(Constantes.PATH_EDAD).getValue().toString();
                    String provincia = dataSnapshot.child(Constantes.PATH_LUGAR).getValue().toString();
                    String correo = dataSnapshot.child(Constantes.PATH_CORREO).getValue().toString();
                    String telefono = dataSnapshot.child(Constantes.PATH_TELEFONO).getValue().toString();
                    String estudios = dataSnapshot.child(Constantes.PATH_ESTUDIOS_TERMINADOS).getValue().toString();
                    String infoAdicional = dataSnapshot.child(Constantes.PATH_INFO_ADICIONAL).getValue().toString();

                    //Una vez recuperada la información, la mostramos en los EditText
                    tvNombre.setText(nombre);
                    tvApellidos.setText(apellidos);
                    tvEdad.setText(getString(R.string.show_edad) + edad + getString(R.string.show_years));
                    tvProvincia.setText(getString(R.string.show_estoy_en) + provincia);
                    tvCorreo.setText(getString(R.string.show_email) + correo);
                    tvTelefono.setText(getString(R.string.show_telefono) + telefono);
                    tvEstudios.setText(getString(R.string.show_estudios) + estudios);
                    tvInfoAdicional.setText(getString(R.string.show_about_me) + infoAdicional);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Función para iniciar chat
    public void goToChatear(View view) {
        Intent intent = new Intent(this, MisChats.class);
        intent.putExtra(Constantes.PASA_INFO, idUsuario);
        startActivity(intent);
    }
}
