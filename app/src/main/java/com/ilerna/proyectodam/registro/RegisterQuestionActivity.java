package com.ilerna.proyectodam.registro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.registro.RegisterActivityAlumno;
import com.ilerna.proyectodam.registro.RegisterActivityProfesor;

/*Esta clase tiene la finalidad de bifurcar el registro del usuario en dos caminos; registro como
  profesor o como alumno */
public class RegisterQuestionActivity extends AppCompatActivity {

    public static final String USUARIO = "names";
    private Button btnProfesor, btnAlumno;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_register);
        final String user = getIntent().getStringExtra(USUARIO);
        btnAlumno = (Button) findViewById(R.id.btn_reg_estudiante);
        btnProfesor = (Button) findViewById(R.id.btn_registro_profesor);
        btnAlumno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegisterAlumno(user);
            }
        });
        btnProfesor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegisterProfesor(user);
            }
        });
    }

    //Funciones asociadas a los dos botones; mediante Intents nos conducirán a una actividad u a otra
    public void goToRegisterAlumno(String usuario) {
        Intent intent = new Intent(this, RegisterActivityAlumno.class);
        intent.putExtra(RegisterActivityAlumno.USUARIO, usuario);
        startActivity(intent);
        finish();
    }

    public void goToRegisterProfesor(String usuario) {
        Intent intent = new Intent(this, RegisterActivityProfesor.class);
        intent.putExtra(RegisterActivityProfesor.USUARIO, usuario);
        startActivity(intent);
        finish();
    }

}
