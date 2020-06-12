package com.ilerna.proyectodam.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.ilerna.proyectodam.adaptersChat.AdapterMensajes;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.alumno.Alumno;
import com.ilerna.proyectodam.clasesPrincipales.SearchActivity;
import com.ilerna.proyectodam.constantes.Constantes;
import com.ilerna.proyectodam.profesor.Profesor;

import de.hdodenhof.circleimageview.CircleImageView;

//Esta clase está creada para la actividad de chatear, ya sea entre Alumno/Alumno, Alumno/Profesor, Profesor/Profesor
public class MisChats extends AppCompatActivity {

    private CircleImageView fotoPerfil;
    private TextView tvNombre;
    private RecyclerView recyclerView;
    private EditText txtMensaje;
    private Button btnEnviar;
    private ImageButton btnHome;

    private AdapterMensajes adaptador;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference, dbEmisor, dbReceptor;
    private FirebaseAuth user;

    private String nombreUsuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        //Recibimos el uid del usuario que recibe el mensaje
        String keyUsuarioReceptor = getIntent().getStringExtra(Constantes.PASA_INFO);

        //Referenciamos los componentes del layout
        fotoPerfil = (CircleImageView) findViewById(R.id.foto_perfil);
        tvNombre = (TextView) findViewById(R.id.nombre_chat);
        recyclerView = (RecyclerView) findViewById(R.id.rv_mensaje);
        txtMensaje = (EditText) findViewById(R.id.txt_mensaje);
        btnEnviar = (Button) findViewById(R.id.btn_enviar_mensaje);
        btnHome = (ImageButton) findViewById(R.id.goHome);

        //Initializamos Firebase Auth para la autentificación de usuario en Firebase
        user = FirebaseAuth.getInstance();
        //Almacenamos esa uid en un String
        final String usuarioActual = user.getUid();
        //Inicializamos nuestra FirebaseDB
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(Constantes.PATH_CHATS);
        //Creamos dos DatabaseReference, una para el emisor del mensaje y otra para el receptor
        dbEmisor = databaseReference.child(usuarioActual).child(keyUsuarioReceptor);
        dbReceptor = databaseReference.child(keyUsuarioReceptor).child(usuarioActual);

        //Ponemos el adapter para que lo reciba el RecyclerView
        adaptador = new AdapterMensajes(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adaptador);

        /*Cuando pulsamos el botón se crean dos nodos en FirebaseDB, uno para el emisor y otro para
        el receptor */
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbEmisor.push().setValue(new MensajeEnviar(
                        txtMensaje.getText().toString(),
                        tvNombre.getText().toString(),
                        usuarioActual,
                        ServerValue.TIMESTAMP));
                dbReceptor.push().setValue(new MensajeEnviar(
                        txtMensaje.getText().toString(),
                        tvNombre.getText().toString(),
                        usuarioActual,
                        ServerValue.TIMESTAMP));
                txtMensaje.setText(Constantes.EMPTY);
            }
        });

        //Cuando pulsamos el botón regresa a SearchActivity
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MisChats.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Cuando haya cambios en el recyclerview y se necesite por tamaño, se podrá navegar con Scroll (línea 140)
        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollBar();
            }
        });

        //Para recibir los datos buscados de nuestra FirebaseDB
        dbEmisor.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Recibiremos de nuestra FirebaseDB el mensaje del chat
                MensajeRecibir mensaje = dataSnapshot.getValue(MensajeRecibir.class);
                adaptador.addMensaje(mensaje);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    //Asignará al Scroll del RecyclerView el número de elementos
    private void setScrollBar() {
        recyclerView.scrollToPosition(adaptador.getItemCount() - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Obtenemos el usuario actual
        FirebaseUser usuarioActual = user.getCurrentUser();
        //Inicializamos nuestra FirebaseDB con el path que queremos obtener datos
        DatabaseReference reference = database.getReference(
                Constantes.PATH_ALUMNOS_HIJO + usuarioActual.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Recibimos el nombre del usuario y su imagen como perfil de Alumno
                    Alumno alumno = dataSnapshot.getValue(Alumno.class);
                    nombreUsuario = alumno.getNombre();
                    tvNombre.setText(nombreUsuario);
                    fotoPerfil.setImageResource(R.drawable.iconsstudent);
                } else {
                    FirebaseUser usuarioActual = user.getCurrentUser();
                    DatabaseReference referenceProfesor = database.getReference(
                            Constantes.PATH_PROFESORES_HIJO + usuarioActual.getUid());
                    referenceProfesor.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //Recibimos el nombre del usuario y su imagen como perfil de Profesor
                            Profesor profesor = dataSnapshot.getValue(Profesor.class);
                            nombreUsuario = profesor.getNombre();
                            tvNombre.setText(nombreUsuario);
                            fotoPerfil.setImageResource(R.drawable.iconteacher);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
