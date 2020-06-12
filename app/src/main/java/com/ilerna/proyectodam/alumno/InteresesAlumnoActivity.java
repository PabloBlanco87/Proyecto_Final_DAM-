package com.ilerna.proyectodam.alumno;

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

//Clase utilizada para guardar los intereses que tiene cada alumno en nuestra FirebaseDB
public class InteresesAlumnoActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAnadeIntereses, btnRegistrarIntereses;
    private TextView tvItemsSeleccionados;

    private String[] arrayIntereses;
    boolean[] checkedItems;

    private ArrayList<Integer> indexItems = new ArrayList<>();
    private ArrayList<String> listaIntereses = new ArrayList<>();

    private DatabaseReference dbInteresesRef;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intereses_alumno);

        // Initializamos Firebase Auth para la autentificación de usuario en Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        //Inicializamos nuestra FirebaseDB
        dbInteresesRef = FirebaseDatabase.getInstance().getReference(Constantes.PATH_INTERESES);

        //Referenciamos los componentes del layout
        btnAnadeIntereses = (Button) findViewById(R.id.btn_anade_intereses);
        btnRegistrarIntereses = (Button) findViewById(R.id.save_intereses);
        tvItemsSeleccionados = (TextView) findViewById(R.id.tvItemSelected);

        //Obtenemos cada elemento del array desde nuestro xml de strings
        arrayIntereses = getResources().getStringArray(R.array.intereses_item);
        checkedItems = new boolean[arrayIntereses.length];

        //Iniciamos los escuchadores de los botones
        btnAnadeIntereses.setOnClickListener(this);
        btnRegistrarIntereses.setOnClickListener(this);

    }

    //Función para añadir los intereses
    public void addInterest() {
        //Cuando pulsemos el botón nos emergerá un cuadro de diálogo con los diferentes intereses a seleccionar
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(InteresesAlumnoActivity.this);
        mBuilder.setTitle(R.string.dialog_titulo_intereses);
        mBuilder.setMultiChoiceItems(arrayIntereses, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if (isChecked) {
                    indexItems.add(position);
                } else {
                    indexItems.remove((Integer.valueOf(position)));
                }
                //Una vez añadidos los intereses, se activa el botón de registrar
                btnRegistrarIntereses.setEnabled(true);
            }
        });

        //Esto nos permitirá eliminar uno a uno los intereses seleccionados
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String item = "";
                listaIntereses.clear();
                for (int i = 0; i < indexItems.size(); i++) {
                    item = item + arrayIntereses[indexItems.get(i)];
                    listaIntereses.add(arrayIntereses[indexItems.get(i)]);
                    if (i != indexItems.size() - 1) {
                        item = item + ", ";
                    }
                }
                tvItemsSeleccionados.setText(item);
                //Si no hay intereses se desactiva botón de registro
                if(item.equals("")){
                    btnRegistrarIntereses.setEnabled(false);
                }
            }
        });
        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //Esto limpiará todos los intereses seleccionados de golpe
        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    indexItems.clear();
                    tvItemsSeleccionados.setText(Constantes.EMPTY);
                }
                //Desactivamos el botón registrar, para obligar al alumno a registrar algún interés
                btnRegistrarIntereses.setEnabled(false);
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //Función para guardar los intereses del alumno en FirebaseDB
    public void saveInterest() {
        String idAhora = user.getUid();
        for (int i = 0; i < listaIntereses.size(); i++) {
            dbInteresesRef.child(arrayIntereses[indexItems.get(i)]).child(idAhora).setValue(true);
        }
        goToSearchActivity();
    }

    //Viajar a la siguiente actividad
    public void goToSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_anade_intereses:
                addInterest();
                break;
            case R.id.save_intereses:
                saveInterest();
                break;
        }
    }
}

