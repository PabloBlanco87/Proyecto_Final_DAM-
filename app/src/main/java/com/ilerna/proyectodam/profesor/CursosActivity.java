package com.ilerna.proyectodam.profesor;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.clasesPrincipales.SearchActivity;
import com.ilerna.proyectodam.constantes.Constantes;

import java.util.ArrayList;

//Clase utilizada para guardar las materias que puede dar cada profesor y guardarlo en nuestra FirebaseDB
public class CursosActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAnadeCursos, btnRegistrarCursos;
    private TextView tvItemsSeleccionados;
    private String[] arrayItems;
    private boolean[] checkedItems;
    private ArrayList<Integer> indexItems = new ArrayList<>();
    private ArrayList<String> listaMaterias = new ArrayList<>();
    private DatabaseReference dbMateriasRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        // Initializamos Firebase Auth para la autentificación de usuario en Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        //Inicializamos nuestra FirebaseDB
        dbMateriasRef = FirebaseDatabase.getInstance().getReference(Constantes.PATH_MATERIAS);

        //Referenciamos los componentes del layout
        btnAnadeCursos = (Button) findViewById(R.id.btn_anade_cursos);
        btnRegistrarCursos = (Button) findViewById(R.id.save_cursos);
        tvItemsSeleccionados = (TextView) findViewById(R.id.tvItemSelected_cursos);

        //Obtenemos cada elemento del array desde nuestro xml de strings
        arrayItems = getResources().getStringArray(R.array.array_materias);
        checkedItems = new boolean[arrayItems.length];

        //Iniciamos los escuchadores de los botones
        btnAnadeCursos.setOnClickListener(this);
        btnRegistrarCursos.setOnClickListener(this);
    }

    public void goToSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        finish();
    }

    public void registraMaterias() {
        String idAhora = user.getUid();

        for (int i = 0; i < listaMaterias.size(); i++) {
            dbMateriasRef.child(arrayItems[indexItems.get(i)]).child(idAhora).setValue(true);
        }
        goToSearchActivity();
    }

    //Función para añadir las materias
    public void addMaterias() {
        //Cuando pulsemos el botón nos emergerá un cuadro de diálogo con los diferentes materias a seleccionar
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CursosActivity.this);
        mBuilder.setTitle(R.string.dialog_aniade_materia);
        mBuilder.setMultiChoiceItems(arrayItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    indexItems.add(position);
                } else {
                    indexItems.remove((Integer.valueOf(position)));
                }
                btnRegistrarCursos.setEnabled(true);
            }
        });

        //Esto nos permitirá eliminar uno a uno las materias seleccionadas
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = Constantes.EMPTY;
                listaMaterias.clear();
                for (int i = 0; i < indexItems.size(); i++) {
                    item = item + arrayItems[indexItems.get(i)];
                    listaMaterias.add(arrayItems[indexItems.get(i)]);
                    if (i != indexItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                tvItemsSeleccionados.setText(item);
                //Si no hay intereses se desactiva botón de registro
                if (item.equals("")) {
                    btnRegistrarCursos.setEnabled(false);
                }
            }
        });

        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //Esto limpiará todos las materias seleccionadas de golpe
        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    indexItems.clear();
                    tvItemsSeleccionados.setText(Constantes.EMPTY);
                }
                //Desactivamos el botón registrar, para obligar al alumno a registrar algún interés
                btnRegistrarCursos.setEnabled(false);
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_cursos:
                registraMaterias();
                break;
            case R.id.btn_anade_cursos:
                addMaterias();
                break;
        }

    }
}

