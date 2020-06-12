package com.ilerna.proyectodam.profesor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.adaptersUsuarios.ProfesorAdapter;
import com.ilerna.proyectodam.constantes.Constantes;

import java.util.ArrayList;
import java.util.List;

//Esta clase está creada para mostrar la lista de profesores, ya sea por intereses y provincias, como por nombre
public class ShowProfesoresActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private ProfesorAdapter profesorAdapter;
    private List<Profesor> profesoresList;
    private Button btnBuscaProvincia, btnBuscaMaterias;
    private ImageButton btnBusca;
    private EditText etNombreBusca;
    private TextView tvProvinciaSeleccionada, tvMateriaSeleccionada;
    private String[] arrayProvincias;
    private String[] arrayMaterias;
    private static String provincia = Constantes.EMPTY;

    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference mDatabaseRef;
    private static DatabaseReference mDatabaseRefProfesores;
    private static DatabaseReference mDatabaseRefMaterias;

    private String tipoMateria;
    private ArrayList<String> materiasIdProfesores = new ArrayList<>();

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profesores);

        //Inicializamos Database
        getInstanceDatabaseReference();

        //Asignamos ids del layout
        recyclerView = findViewById(R.id.rv_profesores);
        btnBuscaProvincia = findViewById(R.id.search_provincia_profesor);
        btnBuscaMaterias = findViewById(R.id.btn_search_materias);
        btnBusca = findViewById(R.id.btn_search_profesor);
        tvProvinciaSeleccionada = findViewById(R.id.tv_provincia_seleccionada_profesor);
        tvMateriaSeleccionada = findViewById(R.id.tv_materia_seleccionada);
        etNombreBusca = findViewById(R.id.tv_busca_nombre_profesor);

        //Comportamiento de los botones
        btnBuscaMaterias.setOnClickListener(this);
        btnBuscaProvincia.setOnClickListener(this);
        btnBusca.setOnClickListener(this);


        //Definimos comportamiento del RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Inicializamos el ArrayList y el ProfesorAdapter
        profesoresList = new ArrayList<>();
        profesorAdapter = new ProfesorAdapter(this, profesoresList);
        recyclerView.setAdapter(profesorAdapter);

        //Obtenemos los recursos del archivo Strings.xml para llenar los arrays
        arrayProvincias = getResources().getStringArray(R.array.array_provincias);
        arrayMaterias = getResources().getStringArray(R.array.array_materias);

        //Para acceder al RecyclerView seleccionado le añadimos el onClick
        profesorAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowProfesoresActivity.this, getString(R.string.show_seleccion) +
                        profesoresList.get(recyclerView.getChildAdapterPosition(v)).
                                getNombre(), Toast.LENGTH_SHORT).show();

                /*Recuperamos el id del alumno para acceder a sus datos cuando hacemos click en el
                  elemento del RecyclerView */
                String idProfesor = profesoresList.get(recyclerView.getChildAdapterPosition(v)).getId();
                Intent intent = new Intent(ShowProfesoresActivity.this, PerfilProfesor.class);
                intent.putExtra(Constantes.PASA_INFO, idProfesor);
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

    //Función para obtener la Database con ruta de "Profesores"
    public static DatabaseReference getDatabaseRefenceProfesores() {
        mDatabaseRefProfesores = firebaseDatabase.getReference(Constantes.PATH_PROFESORES);
        return mDatabaseRefProfesores;
    }

    //Función para obtener la Database con ruta "Materias"/"materia seleccionada"
    public static DatabaseReference getDatabaseRefenceMaterias(String tipoMateria) {
        mDatabaseRefMaterias = getInstanceDatabaseReference()
                .child(Constantes.PATH_MATERIAS).child(tipoMateria);
        return mDatabaseRefMaterias;
    }

    // Creamos función para leer los datos de nuestra Firebase Database
    private ValueEventListener getValueEventListener(final Boolean searchProfesorMateriaID,
                                                     final Boolean searchIDInterest,
                                                     final Boolean searchProfesor) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (searchProfesor)
                    profesoresList.clear();

                //Si se ha buscado una materia previamente, borrar la lista de datos
                if (searchIDInterest)
                    materiasIdProfesores.clear();

                //Si se ha guardado algo en el DataSnapshot, añádelo a la lista
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        if (searchProfesor) {
                            profesoresList.clear();
                            Profesor profesor = snapshot.getValue(Profesor.class);
                            profesoresList.add(profesor);
                        }

                        //Guarda todos los profesores con la materia  y la provincia seleccionada
                        if (searchProfesorMateriaID) {
                            Profesor profesor = snapshot.getValue(Profesor.class);
                            String lugar = profesor.getLugar();
                            if (lugar.equals(provincia)) {
                                profesoresList.add(profesor);
                            }
                            btnBuscaMaterias.setEnabled(false);
                        }

                        //Guarda todos los Ids de los profesores con un esa materia
                        if (searchIDInterest) {
                            materiasIdProfesores.add(snapshot.getKey());
                        }
                    }
                }

                if (searchIDInterest)
                    //Función para mostrar todos los profesores con los Ids que hemos conseguido en la materia seleccionada
                    getProfesoresByIdMaterias();

                if (searchProfesorMateriaID || searchProfesor||searchIDInterest)
                    profesorAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        return valueEventListener;
    }

    //Para guardar todos los Ids de los profesores
    private void getProfesoresById() {
        Query query = getDatabaseRefenceProfesores()
                .orderByChild(Constantes.PATH_ID);
        query.addListenerForSingleValueEvent(getValueEventListener
                (false, true, false));
    }

    //Función para obtener la materia seleccionada
    private void getAsignaturas() {
        Query query = getDatabaseRefenceMaterias(tipoMateria)
                .orderByKey();
        query.addListenerForSingleValueEvent(getValueEventListener
                (false, true, false));
    }

    //Función para mostrar todos los profesores con los Ids que hemos conseguido en la materia seleccionada
    private void getProfesoresByIdMaterias() {
        for (String getIdProfesores : materiasIdProfesores) {
            Query query = getDatabaseRefenceProfesores()
                    .orderByChild(Constantes.PATH_ID)
                    .equalTo(getIdProfesores);
            query.addListenerForSingleValueEvent(getValueEventListener
                    (true, false, false));
            profesoresList.clear();
        }
    }

    //Función para el botón de buscar por materia
    private void getSearchMateria() {
        //Cuando pulsemos el botón emergerá un dialog con todas las materias, del que solo se podrá seleccionar uno de ellos
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShowProfesoresActivity.this);
        mBuilder.setTitle(R.string.dialog_materia_buscada);
        mBuilder.setSingleChoiceItems(arrayMaterias, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvMateriaSeleccionada.setText(arrayMaterias[i]);
                dialogInterface.dismiss();
                //Recuperamos la materia seleccionada y lo almacenamos en el siguiente String
                tipoMateria = tvMateriaSeleccionada.getText().toString();
                //Llamamos a la función
                getAsignaturas();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //Función para el botón de buscar por provincia
    private void getSearchProvincia() {
        /*Cuando pulsemos el botón emergerá un dialog con todas las provincias, del que solo
          se podrá seleccionar una de ellas */
        profesoresList.clear();
        AlertDialog.Builder mBuilderProfesor = new AlertDialog.Builder(ShowProfesoresActivity.this);
        mBuilderProfesor.setTitle(R.string.dialog_provincia_buscada);
        mBuilderProfesor.setSingleChoiceItems(arrayProvincias, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvProvinciaSeleccionada.setText(arrayProvincias[i]);
                dialogInterface.dismiss();
                //Recuperamos la provincia seleccionada y la almacenamos en el siguiente String
                provincia = tvProvinciaSeleccionada.getText().toString();
                //Activamos el botón de buscar intereses
                btnBuscaMaterias.setEnabled(true);
            }
        });

        AlertDialog mDialog = mBuilderProfesor.create();
        mDialog.show();
    }

    //Función para buscar un profesor en función de su nombre
    private void getSearchProfesor() {
        String nombre = etNombreBusca.getText().toString();
        String introduzcaNombre = (String) getText(R.string.show_introduzca_nombre);

        if (nombre.equals(Constantes.EMPTY)) {
            etNombreBusca.setError(introduzcaNombre);
        } else {
            Query query = getDatabaseRefenceProfesores()
                    .orderByChild(Constantes.PATH_NOMBRE)
                    .startAt(nombre)
                    .endAt(nombre + Constantes.UF8);
            query.addListenerForSingleValueEvent(getValueEventListener
                    (false, false, true));
            etNombreBusca.setText(Constantes.EMPTY);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search_materias:
                getSearchMateria();
                break;
            case R.id.search_provincia_profesor:
                getSearchProvincia();
                break;
            case R.id.btn_search_profesor:
                getSearchProfesor();
                break;
            default:
                break;
        }
    }


}

