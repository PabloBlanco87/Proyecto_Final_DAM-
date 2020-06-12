package com.ilerna.proyectodam.alumno;

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
import com.ilerna.proyectodam.adaptersUsuarios.AlumnoAdapter;
import com.ilerna.proyectodam.constantes.Constantes;

import java.util.ArrayList;
import java.util.List;

//Esta clase está creada para mostrar la lista de alumnos, ya sea por intereses y provincias, como por nombre
public class ShowAlumnosActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private AlumnoAdapter adapter;
    private List<Alumno> alumnoList;
    private Button btnBuscaProvincia, btnBuscaPIntereses;
    private ImageButton btnBusca;
    private EditText etNombreBusca;
    private TextView tvProvinciaSeleccionada, tvInteresesSeleccionados;
    private String[] arrayProvincias;
    private String[] arrayIntereses;
    private static String provincia = Constantes.EMPTY;

    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference mDatabaseRef;
    private static DatabaseReference mDatabaseRefStudents;
    private static DatabaseReference mDatabaseRefInterest;

    private String tipoInteres;
    private ArrayList<String> interestIdStudents = new ArrayList<>();

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_alumnos);

        //Inicializamos Database
        getInstanceDatabaseReference();

        //Asignamos ids del layout
        recyclerView = findViewById(R.id.rv_alumnos);
        btnBuscaProvincia = findViewById(R.id.search_provincia);
        btnBuscaPIntereses = findViewById(R.id.btnSearchInterest);
        btnBusca = findViewById(R.id.btnSearchAlumno);
        tvProvinciaSeleccionada = findViewById(R.id.tv_provincia_seleccionada);
        tvInteresesSeleccionados = findViewById(R.id.tv_interes_seleccionado);
        etNombreBusca = findViewById(R.id.tv_busca_nombre);

        //Comportamiento de los botones
        btnBuscaPIntereses.setOnClickListener(this);
        btnBuscaProvincia.setOnClickListener(this);
        btnBusca.setOnClickListener(this);


        //Definimos comportamiento del RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Inicializamos el ArrayList y el AlumnoAdapter
        alumnoList = new ArrayList<>();
        adapter = new AlumnoAdapter(this, alumnoList);
        recyclerView.setAdapter(adapter);

        //Obtenemos los recursos del archivo Strings.xml para llenar los arrays
        arrayProvincias = getResources().getStringArray(R.array.array_provincias);
        arrayIntereses = getResources().getStringArray(R.array.intereses_item);

        //Para acceder al RecyclerView seleccionado le añadimos el onClick
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowAlumnosActivity.this, getString(R.string.show_seleccion) +
                        alumnoList.get(recyclerView.getChildAdapterPosition(v)).
                                getNombre(), Toast.LENGTH_SHORT).show();
                /*Recuperamos el id del alumno para acceder a sus datos cuando hacemos click en el
                  elemento del RecyclerView */
                String idAlumno = alumnoList.get(recyclerView.getChildAdapterPosition(v)).getId();
                Intent intent = new Intent(ShowAlumnosActivity.this, PerfilAlumno.class);
                intent.putExtra(Constantes.PASA_INFO, idAlumno);
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

    //Función para obtener la Database con ruta "Intereses"/"Interés seleccionado"
    public static DatabaseReference getDatabaseRefenceInterest(String tipoInteres) {
        mDatabaseRefInterest = getInstanceDatabaseReference()
                .child(Constantes.PATH_INTERESES).child(tipoInteres);
        return mDatabaseRefInterest;
    }

    // Creamos función para leer los datos de nuestra Firebase Database
    private ValueEventListener getValueEventListener(final Boolean searchStudentsInterestID,
                                                     final Boolean searchIDInterest,
                                                     final Boolean searchAlumno) {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Si vamos a buscar un alumno, borra la lista de alumnos creada previamente
                if (searchAlumno)
                    alumnoList.clear();

                //Si se ha buscado un interés previamente, borrar la lista de datos
                if (searchIDInterest)
                    interestIdStudents.clear();

                //Si se ha guardado algo en el DataSnapshot, añádelo a la lista
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (searchAlumno) {
                            alumnoList.clear();
                            Alumno alumno = snapshot.getValue(Alumno.class);
                            alumnoList.add(alumno);
                        }
                        //Guarda todos los alumnos con el interés y la provincia seleccionada
                        if (searchStudentsInterestID) {
                            Alumno alumno = snapshot.getValue(Alumno.class);
                            String lugar = alumno.getLugar();
                            if (lugar.equals(provincia)) {
                                alumnoList.add(alumno);
                            }
                            btnBuscaPIntereses.setEnabled(false);
                        }
                        //Guarda todos los Ids de los alumnos con un interés
                        if (searchIDInterest) {
                            interestIdStudents.add(snapshot.getKey());
                        }
                    }
                }

                if (searchIDInterest)
                    //Función para mostrar todos los alumnos con los Ids que hemos conseguido en el interés seleccionado
                    getStudentsByIdInterest();

                if (searchStudentsInterestID || searchAlumno || searchIDInterest)
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        return valueEventListener;
    }

    //Para guardar todos los Ids de los alumnos
    private void getStudentsById() {
        Query query = getDatabaseRefenceStudents()
                .orderByChild(Constantes.PATH_ID);
        query.addListenerForSingleValueEvent(getValueEventListener
                (false, true, false));
    }

    //Función para obtener el interés seleccionado
    private void getInterest() {
        Query query = getDatabaseRefenceInterest(tipoInteres)
                .orderByKey();
        query.addListenerForSingleValueEvent(getValueEventListener
                (false, true, false));
    }

    //Función para mostrar todos los alumnos con los Ids que hemos conseguido en el interés seleccionado
    private void getStudentsByIdInterest() {
        for (String getIdStudents : interestIdStudents) {
            Query query = getDatabaseRefenceStudents()
                    .orderByChild(Constantes.PATH_ID)
                    .equalTo(getIdStudents);
            query.addListenerForSingleValueEvent(getValueEventListener
                    (true, false, false));
            alumnoList.clear();
        }
    }

    //Función para el botón de buscar por interés
    private void getSearchInterest() {
        //Cuando pulsemos el botón emergerá un dialog con todos los intereses, del que solo se podrá seleccionar uno de ellos
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShowAlumnosActivity.this);
        mBuilder.setTitle(R.string.dialog_intereses_buscados);
        mBuilder.setSingleChoiceItems(arrayIntereses, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvInteresesSeleccionados.setText(arrayIntereses[i]);
                dialogInterface.dismiss();
                //Recuperamos el interés seleccionado y lo almacenamos en el siguiente String
                tipoInteres = tvInteresesSeleccionados.getText().toString();
                //Llamamos a la función
                getInterest();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //Función para el botón de buscar por provincia
    private void getSearchProvincia() {
        /*Cuando pulsemos el botón emergerá un dialog con todas las provincias, del que solo
          se podrá seleccionar una de ellas */
        alumnoList.clear();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShowAlumnosActivity.this);
        mBuilder.setTitle(R.string.dialog_provincia_buscada);
        mBuilder.setSingleChoiceItems(arrayProvincias, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvProvinciaSeleccionada.setText(arrayProvincias[i]);
                dialogInterface.dismiss();
                //Recuperamos la provincia seleccionada y la almacenamos en el siguiente String
                provincia = tvProvinciaSeleccionada.getText().toString();
                //Activamos el botón de buscar intereses
                btnBuscaPIntereses.setEnabled(true);
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //Función para buscar un alumno en función de su nombre
    private void getSearchAlumno() {
        String nombre = etNombreBusca.getText().toString();
        String introduzcaNombre = (String) getText(R.string.show_introduzca_nombre);
        if (nombre.equals(Constantes.EMPTY)) {
            etNombreBusca.setError(introduzcaNombre);
        } else {
            Query query = getDatabaseRefenceStudents()
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
            case R.id.btnSearchInterest:
                getSearchInterest();
                break;
            case R.id.search_provincia:
                getSearchProvincia();
                break;
            case R.id.btnSearchAlumno:
                getSearchAlumno();
                break;
            default:
                break;
        }
    }
}
