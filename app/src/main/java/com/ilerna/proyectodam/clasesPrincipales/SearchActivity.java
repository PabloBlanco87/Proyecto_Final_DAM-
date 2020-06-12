package com.ilerna.proyectodam.clasesPrincipales;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.alumno.ModificarPerfilAlumno;
import com.ilerna.proyectodam.alumno.ShowAlumnosActivity;
import com.ilerna.proyectodam.chat.MisContactosChats;
import com.ilerna.proyectodam.constantes.Constantes;
import com.ilerna.proyectodam.profesor.ModificarPerfilProfesor;
import com.ilerna.proyectodam.profesor.ShowProfesoresActivity;

/*Clase principal de la App, creada con el objetivo de ser el nexo entre las actividades de la
aplicación. En ella, podremos buscar alumnos o profesores, acceder a nuestros chats abiertos e incluso
modificar o borrar nuestro perfil. */
public class SearchActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference databaseReference, dbIntereses, dbMaterias;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Initializamos Firebase Auth para la autentificación de usuario en Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        //Inicializamos nuestra FirebaseDB
        databaseReference = FirebaseDatabase.getInstance().getReference();
        /*Inicializamos otra instancia de Firebase con el objetivo de eliminar todos los intereses/materias
        añadidos previamente*/
        dbIntereses = FirebaseDatabase.getInstance().getReference(Constantes.PATH_INTERESES);
        dbMaterias = FirebaseDatabase.getInstance().getReference(Constantes.PATH_MATERIAS);
    }

    //Creamos un menú para la ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile: {
                modificarUsuario();
                break;
            }
            case R.id.icon_about: {
                lanzarAcercaDe();
                break;
            }
            case R.id.icon_delete: {
                borrarUsuario();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //Función para ir a la actividad ShowAlumnos
    public void goToShowAlumnos(View view) {
        Intent intent = new Intent(this, ShowAlumnosActivity.class);
        startActivity(intent);
    }

    //Función para ir a la actividad AcercaDe
    public void lanzarAcercaDe() {
        Intent i = new Intent(this, AcercaDe.class);
        startActivity(i);
    }

    //Función para ir a la actividad ShowProfesores
    public void goToShowProfesores(View view) {
        Intent intent = new Intent(this, ShowProfesoresActivity.class);
        startActivity(intent);
    }

    ////Función para borrar el usuario
    public void borrarUsuario() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SearchActivity.this);
        builder1.setMessage(R.string.show_estas_seguro_borrar)
                .setPositiveButton(R.string.show_seguro, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String idUsuario = user.getUid();
                        databaseReference.child(Constantes.PATH_ALUMNOS).child(idUsuario).removeValue();
                        databaseReference.child(Constantes.PATH_PROFESORES).child(idUsuario).removeValue();
                        //Obtenemos el array desde res/values/strings
                        String[] borrarIntereses = getResources().getStringArray(R.array.intereses_item);
                        //Eliminamos cada interés guardado previamente por cada alumno
                        for (int i = 0; i < borrarIntereses.length; i++) {
                            dbIntereses.child(borrarIntereses[i]).child(user.getUid()).removeValue();
                        }
                        //Obtenemos el array desde res/values/strings
                        borrarIntereses = getResources().getStringArray(R.array.array_materias);
                        //Eliminamos cada materia guardada previamente por cada profesor
                        for (int i = 0; i < borrarIntereses.length; i++) {
                            dbMaterias.child(borrarIntereses[i]).child(user.getUid()).removeValue();

                        }
                        user.delete();
                        finishAffinity();
                    }
                })
                .setNegativeButton(R.string.show_no_seguro, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog titulo = builder1.create();
        titulo.setTitle(R.string.show_atencion);
        titulo.show();
    }

    //Función para modificar nuestros datos de usuario
    public void modificarUsuario() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SearchActivity.this);
        builder1.setMessage(R.string.show_estas_seguro_modificar)
                .setPositiveButton(R.string.show_seguro, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
          /*Hago una comprobación; hago una búsqueda en el nodo "Alumnos" con el nodo hijo (UID actual)
          Si encuentra algo en la búsqueda, entonces irá a la actividad de modificar alumno. Si no
          encuentra nada, entonces irá a la actividad de modificar profesor */
                        String idUsuario = user.getUid();
                        databaseReference.child(Constantes.PATH_ALUMNOS).child(idUsuario).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Intent intent1 = new Intent(SearchActivity.this, ModificarPerfilAlumno.class);
                                    startActivity(intent1);
                                    finish();
                                } else {
                                    Intent intent = new Intent(SearchActivity.this, ModificarPerfilProfesor.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }

                })
                .setNegativeButton(R.string.show_no_seguro, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog titulo = builder1.create();
        titulo.setTitle(R.string.show_atencion);
        titulo.show();

    }


    //Función para ir a la actividad que muestra nuestras conversaciones abiertas
    public void goToMyChat(View view) {
        Intent intent1 = new Intent(SearchActivity.this, MisContactosChats.class);
        startActivity(intent1);
    }
}
