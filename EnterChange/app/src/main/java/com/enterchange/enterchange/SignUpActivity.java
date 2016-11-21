package com.enterchange.enterchange;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends Activity {

    private Button _btnCrearCuenta;
    private TextView _TxVwLogueate;
    private EditText _InputName,_InputApellido,_InputUsername,_InputEmail,_InputPassword,_InputConfirmPassword, _InputDireccion, _InputTelefono;
    String ConfirmPassword;
    Usuarios usuario;
    public static String TAG = SignUpActivity.class.getSimpleName();
    Generics generics;
    SQLiteDatabase BaseDeDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usuario = new Usuarios();

        generics = new Generics(SignUpActivity.this);

        AsocioVistas();

        _btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                guardarEnVariables();
                if (!ErroresIngreso())
                {
                    Log.d(TAG,"Nombre: "+usuario.getNombre()+" Apellido: "+usuario.getApellido()+" Username: "+usuario.getUsername() + " Email: "+usuario.getEmail()+" Password: "
                            + usuario.getPassword()+" CPassword: "+ConfirmPassword + " Direccion: "+usuario.getDireccion()+ " Telefono: "+usuario.getTelefono());
                    guardarUsuarioEnBD();
                    Intent actividadPrincipal=new Intent(SignUpActivity.this,ActividadPrincipal.class);
                    startActivity(actividadPrincipal);
                    finish();
                }
            }
        });

        _TxVwLogueate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ActividadLogIn = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(ActividadLogIn);
                finish();
            }
        });
    }

    private void guardarUsuarioEnBD()
    {
        Integer ultimoId;

        ContentValues nuevoUsuario =new ContentValues();

        ultimoId =  obtenerUltimoId();
        ultimoId++;
        nuevoUsuario.put("idusuario",ultimoId);
        nuevoUsuario.put("nombre", usuario.getNombre());
        nuevoUsuario.put("apellido", usuario.getApellido());
        nuevoUsuario.put("username",usuario.getUsername());
        nuevoUsuario.put("email",usuario.getEmail());
        nuevoUsuario.put("password",usuario.getPassword());
        nuevoUsuario.put("direccion",usuario.getDireccion());
        nuevoUsuario.put("telefono",usuario.getTelefono());
        nuevoUsuario.put("iniciado",1);

        BaseDeDatos.insert("usuarios",null,nuevoUsuario);

        BaseDeDatos.close();

        Log.d(TAG, "Usuario agregado con id: "+ultimoId);
    }

    private Integer obtenerUltimoId()
    {
        Integer ultimoId = 0;

        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select idusuario from usuarios",null);

        if (BaseDeDatos!=null)
        {
            if (ConjuntoDeRegistros.moveToFirst())
            {
                do {
                    ultimoId = ConjuntoDeRegistros.getInt(0);
                }while (ConjuntoDeRegistros.moveToNext());
            }
        }

        Log.d(TAG,"Ultimo id: "+ultimoId);
        return ultimoId;
    }

    private void guardarEnVariables()
    {
        String telefono;

        usuario.setNombre(_InputName.getText().toString());
        usuario.setApellido(_InputApellido.getText().toString());
        usuario.setUsername(_InputUsername.getText().toString());
        usuario.setEmail( _InputEmail.getText().toString());
        usuario.setPassword(_InputPassword.getText().toString());
        ConfirmPassword = _InputConfirmPassword.getText().toString();
        usuario.setDireccion(_InputDireccion.getText().toString());
        telefono = _InputTelefono.getText().toString();
        usuario.setTelefono(0);
        if (!telefono.equals(""))
        {
            usuario.setTelefono(Integer.valueOf(telefono));
        }
    }

    private boolean ErroresIngreso()
    {
        boolean HayErrores = false;

        if (TextUtils.isEmpty(usuario.getNombre()))
        {
            _InputName.setError("Campo obligatorio");
            _InputName.requestFocus();
            HayErrores=true;
        }

        if (TextUtils.isEmpty(usuario.getApellido()))
        {
            _InputApellido.setError("Campo obligatorio");
            _InputApellido.requestFocus();
            HayErrores=true;
        }

        if (TextUtils.isEmpty(usuario.getUsername()))
        {
            _InputUsername.setError("Campo obligatorio");
            _InputUsername.requestFocus();
            HayErrores=true;
        }
        else if (existeUsuario(usuario.getUsername()))
        {
            _InputUsername.setError("Ya existe el usuario");
            _InputUsername.requestFocus();
            HayErrores=true;
        }

        if (TextUtils.isEmpty(usuario.getEmail()))
        {
            _InputEmail.setError("Campo obligatorio");
            _InputEmail.requestFocus();
            HayErrores=true;
        }
        else if (!isEmailValid(usuario.getEmail()))
        {
            _InputEmail.setError("Email inválido");
            _InputEmail.requestFocus();
            HayErrores=true;
        }
        else if (existeEmail(usuario.getEmail()))
        {
            _InputEmail.setError("Ya existe el email");
            _InputEmail.requestFocus();
            HayErrores=true;
        }

        if (TextUtils.isEmpty(usuario.getPassword()))
        {
            _InputPassword.setError("Campo obligatorio");
            _InputPassword.requestFocus();
            HayErrores=true;
        }
        else if (!isPasswordValid(usuario.getPassword()))
        {
            _InputPassword.setError("Ingrese mas de 4 caracteres");
            _InputPassword.requestFocus();
            HayErrores=true;
        }

        if (TextUtils.isEmpty(ConfirmPassword))
        {
            _InputConfirmPassword.setError("Campo obligatorio");
            _InputConfirmPassword.requestFocus();
            HayErrores=true;
        }

        if (!TextUtils.isEmpty(usuario.getPassword()) && !TextUtils.isEmpty(ConfirmPassword))
        {
            if (!ConfirmPassword.equals(usuario.getPassword()))
            {
                _InputConfirmPassword.setError("No coinciden las contraseñas");
                _InputConfirmPassword.setText("");
                HayErrores=true;
            }
        }

        if (TextUtils.isEmpty(usuario.getDireccion()))
        {
            _InputDireccion.setError("Campo obligatorio");
            _InputDireccion.requestFocus();
            HayErrores=true;
        }

        return HayErrores;
    }

    private boolean existeEmail(String email) {
        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        String query="select * from usuarios where email='"+email+"'";

        ConjuntoDeRegistros = BaseDeDatos.rawQuery(query, null);

        if (BaseDeDatos!=null)
        {
            if (ConjuntoDeRegistros.moveToFirst())
            {
               return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean existeUsuario(String username)
    {
        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        String query="select * from usuarios where username='"+username+"'";

        ConjuntoDeRegistros = BaseDeDatos.rawQuery(query, null);

        if (BaseDeDatos!=null)
        {
            if (ConjuntoDeRegistros.moveToFirst())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean isEmailValid(String email)
    {
        if (email.contains("@") && email.contains(".")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public void AsocioVistas()
    {
        Log.d(TAG,"Inicializo los EditText y los asocio");
        _InputName = (EditText)findViewById(R.id.input_nombre);
        _InputApellido = (EditText)findViewById(R.id.input_apellido);
        _InputUsername = (EditText)findViewById(R.id.input_username);
        _InputEmail = (EditText)findViewById(R.id.input_email);
        _InputPassword = (EditText)findViewById(R.id.input_password);
        _InputConfirmPassword = (EditText)findViewById(R.id.input_confirm_password);
        _InputDireccion = (EditText)findViewById(R.id.input_adress);
        _InputTelefono = (EditText)findViewById(R.id.input_telefono);

        Log.d(TAG,"Inicializo el text y lo asocio");
        _TxVwLogueate = (TextView)findViewById(R.id.link_login);

        Log.d(TAG,"Inicializo el button y lo asocio");
        _btnCrearCuenta = (Button)findViewById(R.id.btn_sign_up);
    }
}
