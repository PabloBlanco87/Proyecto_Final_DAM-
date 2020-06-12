package com.ilerna.proyectodam.alumno;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.constantes.Constantes;

import java.util.Calendar;

//Clase utilizada para modificar el perfil, en este caso del Alumno
public class ModificarPerfilAlumno extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner spCCAA, spProvincia, spEstudios;
    private EditText textNombre, textApellidos, textTelefono, textEmail, textInfoAlumno;
    private TextView textEdad;
    private Button btnRegistroAlumno, btnEdad;

    private DatabaseReference dbAlumno, dbIntereses;
    private FirebaseUser user;

    private int aa, ma, anio, mes, dia = 0;
    private static String edadActual;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_alumno);

        // Initializamos Firebase Auth para la autentificación de usuario en Firebase
        user = FirebaseAuth.getInstance().getCurrentUser();
        //Inicializamos nuestra FirebaseDB
        dbAlumno = FirebaseDatabase.getInstance().getReference();
        /*Inicializamos otra instancia de Firebase con el objetivo de eliminar todos los intereses
        añadidos previamente*/
        dbIntereses = FirebaseDatabase.getInstance().getReference(Constantes.PATH_INTERESES);
        //Obtenemos el array desde res/values/strings
        String[] borrarIntereses = getResources().getStringArray(R.array.intereses_item);
        //Eliminamos cada interés guardado previamente por cada alumno
        for (int i = 0; i < borrarIntereses.length; i++) {
            dbIntereses.child(borrarIntereses[i]).child(user.getUid()).removeValue();
        }
        //Referenciamos los componentes del layout
        textNombre = (EditText) findViewById(R.id.et_nombre_perfil);
        textApellidos = (EditText) findViewById(R.id.et_apellido_perfil);
        textEdad = (TextView) findViewById(R.id.et_edad_perfil);
        textTelefono = (EditText) findViewById(R.id.et_telefono_perfil);
        textEmail = (EditText) findViewById(R.id.et_email_perfil);
        textInfoAlumno = (EditText) findViewById(R.id.et_info_alumno_perfil);
        spProvincia = (Spinner) findViewById(R.id.sp_provincia_perfil);
        spCCAA = (Spinner) findViewById(R.id.sp_ccaa_perfil);
        spEstudios = (Spinner) findViewById(R.id.sp_estudios_perfil);
        btnRegistroAlumno = (Button) findViewById(R.id.btn_modifica_alumno_perfil);
        btnEdad = (Button) findViewById(R.id.btn_edad_perfil);

        //Iniciamos el Spinner de CCAA que actualizará el Spinner de Provincias para no sobrecargar tanto la búsqueda
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource
                (this, R.array.array_CCAA, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCCAA.setAdapter(adapter);
        spCCAA.setOnItemSelectedListener(this);

        //Iniciamos el Spinner de Estudios terminados
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource
                (this, R.array.array_estudios_terminados, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstudios.setAdapter(adapter1);

        //Recuperamos en un String el Uid del usuario que nos proporciona Firebase en el registro
        String idUsuario = user.getUid();
        //Utilizamos ese Uid para buscar en el nodo Alumnos, el alumno actual, ya que tenemos el Uid como Clave Primaria
        dbAlumno.child(Constantes.PATH_ALUMNOS).child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child(Constantes.PATH_NOMBRE).getValue().toString();
                    String apellidos = dataSnapshot.child(Constantes.PATH_APELLIDOS).getValue().toString();
                    String correo = dataSnapshot.child(Constantes.PATH_CORREO).getValue().toString();
                    String telefono = dataSnapshot.child(Constantes.PATH_TELEFONO).getValue().toString();
                    String infoAdicional = dataSnapshot.child(Constantes.PATH_INFO_ADICIONAL).getValue().toString();

                    //Una vez recuperada la información, la mostramos en los EditText
                    textNombre.setText(nombre);
                    textApellidos.setText(apellidos);
                    textEmail.setText(correo);
                    textTelefono.setText(telefono);
                    textInfoAlumno.setText(infoAdicional);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Botones con el mismo listener
        btnRegistroAlumno.setOnClickListener(this);
        btnEdad.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Cuando seleccionemos la CCAA, se cargará el sub-array correspondiente de Provincias
        int[] provinciaAlumno = {R.array.array_andalucia, R.array.array_aragon, R.array.array_asturias,
                R.array.array_baleares, R.array.array_canarias, R.array.array_cantabria,
                R.array.array_lamancha, R.array.array_leon, R.array.array_catalunya,
                R.array.array_extremadura, R.array.array_galicia, R.array.array_rioja,
                R.array.array_madrid, R.array.array_murcia, R.array.array_navarra, R.array.array_vasco,
                R.array.array_valencia};
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                provinciaAlumno[position],
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvincia.setAdapter(adapter);
    }

    //Función obligatoria cuando cargamos un Spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //Función para que cuando pulsemos el botón registre al alumno en nuestra Firebase DB
    public void registrarAlumno() {
        /*Obtenemos el UID del usuario registrado, este paso es MUY IMPORTANTE, ya que gracias a él,
          nuestra Firebase DB obtendrá mucha más consistencia, puesto que todos nuestros comportamientos
          en la aplicación irán relacionados a esta PK que nos ofrece Firebase */
        String idAhora = user.getUid();
        //Obtenemos los datos de los EditText
        String nombre = textNombre.getText().toString();
        String apellidos = textApellidos.getText().toString();
        String telefono = textTelefono.getText().toString();
        String email = textEmail.getText().toString();
        String provincia = spProvincia.getSelectedItem().toString().trim();
        String infoAlumno = textInfoAlumno.getText().toString();
        String edad = textEdad.getText().toString();
        String estudiosTerminados = spEstudios.getSelectedItem().toString().trim();

        //Obligamos al usuario a rellenar los campos, en el caso que no lo rellene, saldrá una alerta
        String campoRequerido= (String) getText(R.string.show_campo_requerido);
        if (TextUtils.isEmpty(nombre)) {
            textNombre.setError(campoRequerido);
        } else if (TextUtils.isEmpty(apellidos)) {
            textApellidos.setError(campoRequerido);
        } else if (TextUtils.isEmpty(telefono)) {
            textTelefono.setError(campoRequerido);
        } else if (TextUtils.isEmpty(email)) {
            textEmail.setError(campoRequerido);
        } else if (TextUtils.isEmpty(infoAlumno)) {
            textInfoAlumno.setError(campoRequerido);
        } else if (TextUtils.isEmpty(edad)) {
            textEdad.setError(campoRequerido);
        } else {
            /*Creamos el nuevo objeto Alumno con los datos obtenidos en los EditText y los añadimos a Firebase DB
              y como PK el UID que nos ha generado Firebase en la autentificación de usuario */
            Alumno alumno = new Alumno(idAhora, nombre, apellidos, edadActual, provincia, email, telefono, infoAlumno, estudiosTerminados);
            this.dbAlumno.child(Constantes.PATH_ALUMNOS).child(idAhora).setValue(alumno);
            Toast.makeText(this, getString(R.string.show_alumno_guardado), Toast.LENGTH_SHORT).show();
            //Por último, vamos a la siguiente actividad
            goToInteresesActivity();
        }
    }

    //Función para extraer la fecha que seleccione el usuario en el DatePicker
    public void calcularFecha() {
        Calendar ca = Calendar.getInstance();
        anio = ca.get(Calendar.YEAR);
        mes = ca.get(Calendar.MONTH);
        dia = ca.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                String diaFormateado = (dayOfMonth < 10) ? "0" + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10) ? "0" + String.valueOf(mesActual) : String.valueOf(mesActual);
                aa = year;
                ma = Integer.parseInt(mesFormateado);
                textEdad.setText(diaFormateado + "/" + mesFormateado + "/" + year);

                //Utilizamos este String "static" para que mande a Firebase el cálculo de la edad
                edadActual = calculaEdad(anio, (mes + 1), aa, ma);
            }
        }, anio, mes, dia);
        recogerFecha.show();
    }

    //Función para calcular la edad que tiene el usuario, utilizando su fecha de nacimiento
    public String calculaEdad(int a, int m, int aa, int ma) {
        int anios = 0;
        if (ma <= m) {
            anios = a - aa;
        } else {
            anios = a - aa - 1;
        }
        String resultado = String.valueOf(anios);
        return resultado;
    }

    //Función para viajar a la siguiente actividad
    public void goToInteresesActivity() {
        Intent i = new Intent(ModificarPerfilAlumno.this, InteresesAlumnoActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modifica_alumno_perfil:
                registrarAlumno();
                break;
            case R.id.btn_edad_perfil:
                calcularFecha();
                break;
            default:
                break;
        }
    }
}
