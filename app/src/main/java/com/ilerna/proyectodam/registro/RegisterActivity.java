package com.ilerna.proyectodam.registro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.constantes.Constantes;

public class RegisterActivity extends AppCompatActivity {

    private EditText textEmail, textPassword, textPasswordRepetida;
    private Button btnRegistro;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro1);

        /* Initializamos Firebase Auth para la autentificación de usuario en Firebase; lo utilizaremos
           más tarde en la creación de un nuevo usuario*/
        mAuth = FirebaseAuth.getInstance();

        //Referenciamos los componentes del layout
        textEmail = (EditText) findViewById(R.id.et_email_registro);
        textPassword = (EditText) findViewById(R.id.et_password_registro);
        textPasswordRepetida = (EditText) findViewById(R.id.et_password_coincide);
        btnRegistro = (Button) findViewById(R.id.btn_registrar_registro);

        //Creamos una barra de progreso, para cuando clickemos en registrar usuario
        progressDialog = new ProgressDialog(this);

        //Añadimos el comportamiento del botón
        btnRegistro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });
    }

    //Función para registrar el usuario con el correo electrónico
    public void registrarUsuario() {
        //Obtenemos el mail y contraseña de los EditText
        final String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString().trim();
        String repeatPassword = textPasswordRepetida.getText().toString().trim();

        //Verificamos que las cajas de texto no estén vacías, si lo están, saldrá una alerta
        String campoRequerido = (String) getText(R.string.show_campo_requerido);
        String minCaracteres = (String) getText(R.string.min_caracteres);
        String noCoincide = (String) getText(R.string.no_coincide);
        if (TextUtils.isEmpty(email)) {
            textEmail.setError(campoRequerido);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            textPassword.setError(campoRequerido);
            return;
        }
        if (TextUtils.isEmpty(repeatPassword)) {
            textPasswordRepetida.setError(campoRequerido);
            return;
        }
        //Verificamos que la contraseña tenga entre 6 y 8 dígitos
        if (password.length() < 6 || password.length() > 8) {
            textPassword.setError(minCaracteres);
            return;
        }
        if (!password.equals(repeatPassword)) {
            textPasswordRepetida.setError(noCoincide);
            return;
        }


        //Mostramos este mensaje en la barra de progreso
        String mensaje = (String) getText(R.string.show_registrando);
        progressDialog.setMessage(mensaje);
        progressDialog.show();

        //Creamos un nuevo usuario
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, getString(R.string.show_usuario_registrado), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, RegisterQuestionActivity.class);
                            intent.putExtra(RegisterQuestionActivity.USUARIO, email);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.show_usuario_existente), Toast.LENGTH_LONG).show();
                                textPassword.setText(Constantes.EMPTY);
                                textPasswordRepetida.setText(Constantes.EMPTY);
                            } else {
                                Toast.makeText(RegisterActivity.this, getString(R.string.show_no_registrado), Toast.LENGTH_LONG).show();
                                textPassword.setText(Constantes.EMPTY);
                                textPasswordRepetida.setText(Constantes.EMPTY);
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}

