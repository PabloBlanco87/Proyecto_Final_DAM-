package com.ilerna.proyectodam.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilerna.proyectodam.adaptersChat.ChatAlumnoAdapter;
import com.ilerna.proyectodam.adaptersChat.ChatProfesorAdapter;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.alumno.Alumno;
import com.ilerna.proyectodam.constantes.Constantes;
import com.ilerna.proyectodam.profesor.Profesor;

import java.util.ArrayList;
import java.util.List;

//Clase creada para mostrar en un RecyclerView las conversaciones que tenemos abiertas
public class MisContactosChats extends AppCompatActivity {

    private RecyclerView recyclerViewAlumnos, recyclerViewProfesores;
    private ChatAlumnoAdapter alumnoAdapter;
    private ChatProfesorAdapter profesorAdapter;
    private List<Alumno> alumnoList;
    private List<Profesor> profesoresList;

    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference mDatabaseRef;
    private static DatabaseReference mDatabaseRefStudents;
    private static DatabaseReference mDatabaseRefAlumnos;
    private static DatabaseReference mDatabaseRefTeachers;
    private static DatabaseReference mDatabaseRefProfesores;
    private static FirebaseUser mAuth;

    private static String user;
    private ArrayList<String> chatAlumno = new ArrayList<>();
    private ArrayList<String> chatProfesor = new ArrayList<>();

    private ValueEventListener valueEventListenerAlumnos, valueEventListenerProfesores;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats_abiertos);

        //Inicializamos Database
        getInstanceDatabaseReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        user = mAuth.getUid();

        //Asignamos ids del layout
        recyclerViewAlumnos = findViewById(R.id.rv_alumnos_chat);
        recyclerViewProfesores = findViewById(R.id.rv_profesores_chat);

        //Definimos comportamiento de los RecyclerView
        recyclerViewAlumnos.setHasFixedSize(true);
        recyclerViewAlumnos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProfesores.setHasFixedSize(true);
        recyclerViewProfesores.setLayoutManager(new LinearLayoutManager(this));

        //Inicializamos el ArrayList y el AlumnoAdapter
        alumnoList = new ArrayList<>();
        alumnoAdapter = new ChatAlumnoAdapter(this, alumnoList);
        recyclerViewAlumnos.setAdapter(alumnoAdapter);
        //Inicializamos el ArrayList y el ProfesorAdapter
        profesoresList = new ArrayList<>();
        profesorAdapter = new ChatProfesorAdapter(this, profesoresList);
        recyclerViewProfesores.setAdapter(profesorAdapter);

        //Funciones para obtener los alumnos y los profesores con los que hemos chateado
        getChatsAlumnos();
        getChatsProfesores();

        //Para acceder al RecyclerView seleccionado le añadimos el onClick
        alumnoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MisContactosChats.this, getString(R.string.show_seleccion) +
                        alumnoList.get(recyclerViewAlumnos.getChildAdapterPosition(v)).
                                getNombre(), Toast.LENGTH_SHORT).show();

                /*Recuperamos el id del alumno para acceder a sus datos cuando hacemos click en el
                  elemento del RecyclerView */
                String idUsuario = alumnoList.get(recyclerViewAlumnos.getChildAdapterPosition(v)).getId();
                Intent intent = new Intent(MisContactosChats.this, MisChats.class);
                intent.putExtra(Constantes.PASA_INFO, idUsuario);
                startActivity(intent);
            }
        });

        //Para acceder al RecyclerView seleccionado le añadimos el onClick
        profesorAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MisContactosChats.this, getString(R.string.show_seleccion) +
                        profesoresList.get(recyclerViewProfesores.getChildAdapterPosition(v)).
                                getNombre(), Toast.LENGTH_SHORT).show();

                /*Recuperamos el id del alumno para acceder a sus datos cuando hacemos click en el
                  elemento del RecyclerView */
                String idUsuario = profesoresList.get(recyclerViewProfesores.getChildAdapterPosition(v)).getId();
                Intent intent = new Intent(MisContactosChats.this, MisChats.class);
                intent.putExtra(Constantes.PASA_INFO, idUsuario);
                startActivity(intent);
            }
        });
    }

    //Función para inicializar el Database
    public static DatabaseReference getInstanceDatabaseReference() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = firebaseDatabase.getReference();
        return mDatabaseRef;
    }

    //Función para obtener la Database con ruta de "Alumnos"
    public static DatabaseReference getDatabaseRefenceStudents() {
        mDatabaseRefStudents = firebaseDatabase.getReference(Constantes.PATH_ALUMNOS);
        return mDatabaseRefStudents;
    }

    //Función para obtener la Database con ruta de "Profesores"
    public static DatabaseReference getDatabaseRefenceProfesores() {
        mDatabaseRefTeachers = firebaseDatabase.getReference(Constantes.PATH_PROFESORES);
        return mDatabaseRefTeachers;
    }

    //Función para obtener la Database con ruta "Chats/UID" para el RecyclerView de Alumnos
    public static DatabaseReference getDatabaseRefenceChatsAlumno(String uid) {
        mDatabaseRefAlumnos = getInstanceDatabaseReference()
                .child(Constantes.PATH_CHATS).child(uid);
        return mDatabaseRefAlumnos;
    }

    //Función para obtener la Database con ruta "Chats/UID" para el RecyclerView de Profesores
    public static DatabaseReference getDatabaseRefenceChatProfesor(String uid) {
        mDatabaseRefProfesores = getInstanceDatabaseReference()
                .child(Constantes.PATH_CHATS).child(uid);
        return mDatabaseRefProfesores;
    }

    //Función para obtener los datos de los alumnos que tienen conversación abierta, ulizamos nuestra uid
    private void getChatsAlumnos() {
        Query query = getDatabaseRefenceChatsAlumno(user)
                .orderByKey();
        query.addListenerForSingleValueEvent(getValueEventListenerAlumnos
                (false, true));
    }

    //Función para mostrar todos los alumnos con los Ids que hemos conseguido en el chat
    private void getStudentsByIdChats() {
        for (String getIdStudents : chatAlumno) {
            Query query = getDatabaseRefenceStudents()
                    .orderByChild(Constantes.PATH_ID)
                    .equalTo(getIdStudents);
            query.addListenerForSingleValueEvent(getValueEventListenerAlumnos
                    (true, false));
            alumnoList.clear();
        }
    }

    //Función para obtener los datos de los profesores que tienen conversación abierta, ulizamos nuestra uid
    private void getChatsProfesores() {
        Query query = getDatabaseRefenceChatProfesor(user)
                .orderByKey();
        query.addListenerForSingleValueEvent(getValueEventListenerProfesores
                (false, true));
    }

    //Función para mostrar todos los profesores con los Ids que hemos conseguido en el chat
    private void getProfesoresByIdChat() {
        for (String getIdStudents : chatProfesor) {
            Query query = getDatabaseRefenceProfesores()
                    .orderByChild(Constantes.PATH_ID)
                    .equalTo(getIdStudents);
            query.addListenerForSingleValueEvent(getValueEventListenerProfesores
                    (true, false));
            profesoresList.clear();
        }
    }

    // Creamos función para leer los datos de nuestra Firebase Database referentes a Alumnos
    private ValueEventListener getValueEventListenerAlumnos(final Boolean searchStudentsChatID,
                                                            final Boolean searchID) {
        valueEventListenerAlumnos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (searchID)
                    chatAlumno.clear();

                //Si se ha guardado algo en el DataSnapshot, añádelo a la lista
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Guarda todos los alumnos con con chat abierto
                        if (searchStudentsChatID) {
                            Alumno alumno = snapshot.getValue(Alumno.class);
                            alumnoList.add(alumno);
                        }
                        //Guarda todos los Ids de los alumnos
                        if (searchID) {
                            chatAlumno.add(snapshot.getKey());
                        }
                    }
                }

                if (searchID)
                    //Función para mostrar todos los alumnos con los Ids que hemos conseguido en el chat
                    getStudentsByIdChats();

                if (searchStudentsChatID)
                    alumnoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return valueEventListenerAlumnos;
    }

    //Creamos función para leer los datos de nuestra Firebase Database referentes a Profesores
    private ValueEventListener getValueEventListenerProfesores(final Boolean searchProfesorChatID,
                                                               final Boolean searchID) {
        valueEventListenerProfesores = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (searchID)
                    chatProfesor.clear();

                //Si se ha guardado algo en el DataSnapshot, añádelo a la lista
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        //Guarda todos los profesores con un chat abierto
                        if (searchProfesorChatID) {
                            Profesor profesor = snapshot.getValue(Profesor.class);
                            profesoresList.add(profesor);
                        }
                        //Guarda todos los Ids de los profesores
                        if (searchID) {
                            chatProfesor.add(snapshot.getKey());
                        }
                    }
                }

                if (searchID)
                    //Función para mostrar todos los profesores con los Ids que hemos conseguido en el chat
                    getProfesoresByIdChat();

                if (searchProfesorChatID)
                    profesorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        return valueEventListenerProfesores;
    }

}
