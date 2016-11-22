package com.enterchange.enterchange;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import static com.enterchange.enterchange.ActividadPrincipal.UsuarioActual;
import static com.enterchange.enterchange.ActividadPrincipal.navigationView;


public class FragmentMisDatos extends Fragment {

    SQLiteDatabase BaseDeDatos;
    Generics generics;
    Context context;
    FloatingActionButton fab;

    TextView TxVwHeaderNombre;

    public FragmentMisDatos() {
        // Required empty public constructor
    }


    EditText EdTxNombre_Apellido, EdTxUsername, EdTxEmail, EdTxDireccion, EdTxTelefono, EdTxApellido;
    Button btnOK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_mis_datos, container, false);


        context = container.getContext();

        AsociarVistas(v);
        AsignarValores();

        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activarControles(true);
            }
        });

        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_manage));
        generics= new Generics(context);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (!ErroresIngreso())
                    {
                        ActualizarDatos();
                    }
            }
        });

        return v;
    }

    private boolean ErroresIngreso()
    {
        boolean HayErrores = false;

        if (TextUtils.isEmpty(EdTxNombre_Apellido.getText().toString()))
        {
            EdTxNombre_Apellido.setError("Rellene el campo");
            EdTxNombre_Apellido.requestFocus();
            HayErrores=true;
        }

        if (TextUtils.isEmpty(EdTxApellido.getText().toString()))
        {
            EdTxApellido.setError("Rellene el campo");
            EdTxApellido.requestFocus();
            HayErrores=true;
        }


        if (TextUtils.isEmpty(EdTxEmail.getText().toString()))
        {
            EdTxEmail.setError("Rellene el campo");
            EdTxEmail.requestFocus();
            HayErrores=true;
        }
        else if (!isEmailValid(EdTxEmail.getText().toString()))
        {
            EdTxEmail.setError("Email inválido");
            EdTxEmail.requestFocus();
            HayErrores=true;
        }
        else if (existeUsuario(EdTxUsername.getText().toString()) && !EdTxUsername.getText().toString().equals(UsuarioActual.getUsername()))
        {
            EdTxUsername.setError("Ya existe el username");
            EdTxUsername.requestFocus();
            HayErrores=true;
        }

        if (TextUtils.isEmpty(EdTxDireccion.getText().toString()))
        {
            EdTxDireccion.setError("Rellene el campo");
            EdTxDireccion.requestFocus();
            HayErrores=true;
        }

        return HayErrores;
    }

    private boolean isEmailValid(String email)
    {
        if (email.contains("@") && email.contains(".")) {
            return true;
        } else {
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

    public void ActualizarDatos()
    {
        ModificarUserActual();
        activarControles(false);
        ModificarEnBD();

        TxVwHeaderNombre.setText(UsuarioActual.getNombre()+" "+UsuarioActual.getApellido());
        EdTxNombre_Apellido.setText(UsuarioActual.getNombre()+" "+UsuarioActual.getApellido());
    }

    private void ModificarEnBD()
    {
        BaseDeDatos =generics.AbroBaseDatos();

        ContentValues registroAModificar = new ContentValues();

        Log.d("ModificarEnBD",EdTxNombre_Apellido.getText().toString());
        registroAModificar.put("nombre",EdTxNombre_Apellido.getText().toString());
        registroAModificar.put("apellido",EdTxApellido.getText().toString());
        registroAModificar.put("direccion",EdTxDireccion.getText().toString());

        if (!EdTxTelefono.getText().toString().equals("") && !EdTxTelefono.getText().toString().equals("-"))
        {
            registroAModificar.put("telefono", Integer.valueOf(EdTxTelefono.getText().toString()));
        }
        else
        {
            registroAModificar.put("telefono",0);
        }

        registroAModificar.put("username",EdTxUsername.getText().toString());


        if (BaseDeDatos!=null)
        {
            BaseDeDatos.update("usuarios",registroAModificar,"idusuario="+UsuarioActual.getIdUsuario(),null);
        }

    }

    public void ModificarUserActual()
    {
        String telefono;

        UsuarioActual.setNombre(EdTxNombre_Apellido.getText().toString());
        UsuarioActual.setApellido(EdTxApellido.getText().toString());
        UsuarioActual.setDireccion(EdTxDireccion.getText().toString());
        UsuarioActual.setUsername(EdTxUsername.getText().toString());
        UsuarioActual.setTelefono(0);

        telefono = EdTxTelefono.getText().toString();

        if (!telefono.equals("") && !telefono.equals("-"))
        {
            UsuarioActual.setTelefono(Integer.valueOf(EdTxTelefono.getText().toString()));
        }
    }

    private void AsignarValores()
    {
        EdTxNombre_Apellido.setText(UsuarioActual.getNombre()+" "+UsuarioActual.getApellido());
        EdTxApellido.setText(UsuarioActual.getApellido());
        EdTxUsername.setText(UsuarioActual.getUsername());
        EdTxEmail.setText(UsuarioActual.getEmail());
        EdTxDireccion.setText(UsuarioActual.getDireccion());
        EdTxTelefono.setText("-");

        if (!UsuarioActual.getTelefono().equals(0)) {
            EdTxTelefono.setText(UsuarioActual.getTelefono().toString());
        }
    }

    private void AsociarVistas(View v)
    {
        TxVwHeaderNombre=(TextView)navigationView.getHeaderView(0).findViewById(R.id.text_header1);
        EdTxNombre_Apellido =(EditText)v.findViewById(R.id.txt_nombre_Apellido);
        EdTxUsername =(EditText)v.findViewById(R.id.txt_username);
        EdTxEmail =(EditText)v.findViewById(R.id.txt_email);
        EdTxDireccion =(EditText)v.findViewById(R.id.txt_direccion);
        EdTxTelefono =(EditText)v.findViewById(R.id.txt_telefono);
        EdTxApellido=(EditText) v.findViewById(R.id.txt_Apellido);
        btnOK =(Button)v.findViewById(R.id.btn_ok_editar_datos);
    }

    public void activarControles(boolean mostrar)
    {

        EdTxDireccion.setEnabled(mostrar);
        EdTxUsername.setEnabled(mostrar);
        EdTxNombre_Apellido.setEnabled(mostrar);
        EdTxTelefono.setEnabled(mostrar);
        EdTxDireccion.setEnabled(mostrar);

        if (mostrar) {
            EdTxApellido.setVisibility(View.VISIBLE);
            btnOK.setVisibility(View.VISIBLE);
            EdTxNombre_Apellido.setText(UsuarioActual.getNombre());
            EdTxNombre_Apellido.requestFocus();
            fab.setVisibility(View.INVISIBLE);
            if (EdTxTelefono.getText().toString().equals("-"))
            {
                EdTxTelefono.setText("");
            }
        }
        else
        {
            EdTxApellido.setVisibility(View.GONE);
            btnOK.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);
            if (EdTxTelefono.getText().toString().equals(""))
            {
                EdTxTelefono.setText("-");
            }
        }
    }

}
