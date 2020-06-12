package com.ilerna.proyectodam.clasesPrincipales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.ilerna.proyectodam.R;
import com.ilerna.proyectodam.constantes.Constantes;
import com.ilerna.proyectodam.registro.RegisterActivity;

//Clase inicial para loguearse y poder acceder a la app
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText textEmail, textPassword;
    private Button btnLogin, btnRegistrar;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializamos Firebase Auth para la autentificación de usuario en Firebase
        mAuth = FirebaseAuth.getInstance();

        //Referenciamos los componentes del layout
        textEmail = (EditText) findViewById(R.id.et_email);
        textPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_loguear);
        btnRegistrar = (Button) findViewById(R.id.btn_registro);

        //Creamos una barra de progreso, para cuando pinchemos en loguear usuario
        progressDialog = new ProgressDialog(this);

        //Iniciamos los escuchadores de los botones
        btnLogin.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);
    }

    //Función para hacer el loguin
    private void loguearUsuario() {
        //Obtenemos el mail y contraseña desde las cajas de texto
        final String email = textEmail.getText().toString().trim();
        String password = textPassword.getText().toString().trim();

        //Verificamos que las cajas de texto no estén vacías, si lo están, saldrá una alerta
        String campoRequerido = (String) getText(R.string.show_campo_requerido);

        if (TextUtils.isEmpty(email)) {
            textEmail.setError(campoRequerido);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            textPassword.setError(campoRequerido);
            return;
        }

        //Mostramos este mensaje en la barra de progreso
        String loguin = (String) getText(R.string.show_accediendo);
        progressDialog.setMessage(loguin);
        progressDialog.show();

        //Logueamos al usuario
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            int posicion = email.indexOf(Constantes.ARROBA);
                            String usuario = email.substring(0, posicion);
                            Toast.makeText(LoginActivity.this, getString(R.string.show_accediendo_ok), Toast.LENGTH_LONG).show();

                            //Hacemos el intent para que envíe el nombre del usuario a SearchActivity
                            Intent intentSearch = new Intent(getApplication(), SearchActivity.class);
                            startActivity(intentSearch);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.show_problema_loguin), Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    //Función para ir la actividad de registro de usuario
    public void goToRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //Función para cuando hagamos click en los diferentes botones y los discrimine
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_registro:
                goToRegisterActivity();
                break;
            case R.id.btn_loguear:
                loguearUsuario();
        }
    }
}