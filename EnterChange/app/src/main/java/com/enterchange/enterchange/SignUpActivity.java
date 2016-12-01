package com.enterchange.enterchange;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends Activity {

    private Button _btnCrearCuenta, btnVerificarDireccion;
    private ImageView imageViewDireccionVerificada;
    private TextView _TxVwLogueate;
    private EditText _InputName,_InputApellido,_InputUsername,_InputEmail,_InputPassword,_InputConfirmPassword, _InputDireccion, _InputTelefono;
    String ConfirmPassword, DireccionSeleccionada;
    ArrayList<Direcciones> direccionesObtenidas;
    Usuarios usuario;
    public static String TAG = SignUpActivity.class.getSimpleName();
    Generics generics;
    SQLiteDatabase BaseDeDatos;
    SessionManager session;
    boolean DireccionVerificada=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        session =new SessionManager(SignUpActivity.this);

        usuario = new Usuarios();

        generics = new Generics(SignUpActivity.this);

        AsocioVistas();

        direccionesObtenidas= new ArrayList<>();

        _btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                guardarEnVariables();
                if (!DireccionVerificada) {
                    Toast.makeText(SignUpActivity.this, "Verifique la direccion", Toast.LENGTH_SHORT).show();
                }
                if (!ErroresIngreso())
                {
                    session.createLoginSession(usuario.getEmail(),usuario.getPassword());
                    Log.d(TAG,"Nombre: "+usuario.getNombre()+" Apellido: "+usuario.getApellido()+" Username: "+usuario.getUsername() + " Email: "+usuario.getEmail()+" Password: "
                            + usuario.getPassword()+" CPassword: "+ConfirmPassword + " Direccion: "+usuario.getDireccion()+ " Telefono: "+usuario.getTelefono());
                    guardarUsuarioEnBD();
                    Intent actividadPrincipal=new Intent(SignUpActivity.this,ActividadPrincipal.class);
                    startActivity(actividadPrincipal);
                    finish();
                }
            }
        });

         btnVerificarDireccion.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (!_InputDireccion.getText().toString().trim().equals("")) {

                     String url = "https://maps.googleapis.com/maps/api/geocode/json?address=";

                     url += _InputDireccion.getText().toString().trim();  // Copio la direccion ingresada al final de la URL
                     url += "&components=administrative_area:CABA|country:AR&key=AIzaSyA0T6Xd7zuyregCBfyon2axZWcgs1CUq-A";
                     if (generics.isConnectedToInternet()) {
                         new GeolocalizacionTask().execute(url);  // Llamo a clase async con url
                     }
                     else
                     {
                         Toast.makeText(SignUpActivity.this, "Conéctese a internet", Toast.LENGTH_SHORT).show();
                     }
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
        nuevoUsuario.put("direccion",usuario.getDireccion().getDireccion());
        nuevoUsuario.put("latitud",usuario.getDireccion().getLatLng().latitude);
        nuevoUsuario.put("longitud",usuario.getDireccion().getLatLng().longitude);
        nuevoUsuario.put("telefono",usuario.getTelefono());
        //nuevoUsuario.put("iniciado",1);

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

        usuario.setNombre(_InputName.getText().toString().trim());
        usuario.setApellido(_InputApellido.getText().toString().trim());
        usuario.setUsername(_InputUsername.getText().toString().trim());
        usuario.setEmail( _InputEmail.getText().toString().trim().toLowerCase());
        usuario.setPassword(_InputPassword.getText().toString().trim());
        ConfirmPassword = _InputConfirmPassword.getText().toString().trim();

        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=";

        if (TextUtils.isEmpty(_InputDireccion.getText().toString().trim()))
        {
            Direcciones dir= new Direcciones();
            dir.setDireccion("");
            dir.setLatLng(0.0,0.0);
            usuario.setDireccion(dir);
        }



        telefono = _InputTelefono.getText().toString().trim();
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

        if (TextUtils.isEmpty(usuario.getDireccion().getDireccion()))
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

        imageViewDireccionVerificada = (ImageView)findViewById(R.id.imageView_validado);

        Log.d(TAG,"Inicializo el text y lo asocio");
        _TxVwLogueate = (TextView)findViewById(R.id.link_login);

        Log.d(TAG,"Inicializo el button y lo asocio");
        _btnCrearCuenta = (Button)findViewById(R.id.btn_sign_up);
        btnVerificarDireccion = (Button)findViewById(R.id.btn_validar_direccion);
    }


    private class GeolocalizacionTask extends AsyncTask<String, Void, ArrayList<Direcciones>> {
        private OkHttpClient client = new OkHttpClient();
        private ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Espere por favor");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }
        @Override
        protected void onPostExecute(ArrayList<Direcciones> resultado) {
            super.onPostExecute(resultado);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (resultado != null) {

            if (Validacion(resultado.get(0).getDireccion())) {
                _InputDireccion.setError("Dirección inválida");
            } else {


                    direccionesObtenidas.clear();
                    direccionesObtenidas.addAll(resultado);

                    Dialog dialog = onCreateDialogSingleChoice(direccionesObtenidas);
                    dialog.show();


                }
            }

        }



        @Override
        protected ArrayList<Direcciones> doInBackground(String... params) {
            String url = params[0];

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {

                ArrayList<Direcciones> listaDirecciones;
                Response response = client.newCall(request).execute();  // Llamado al Google API
                listaDirecciones = parsearResultado(response.body().string());      // Convierto el resultado en ArrayList<Direcciones>


                return listaDirecciones;

            } catch (IOException | JSONException e) {
                Log.d("Error", e.getMessage());                          // Error de Network o al parsear JSON
                return null;
            }
        }


        // Convierte un JSON en un ArrayList de Direccion
        ArrayList<Direcciones> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Direcciones> direcciones = new ArrayList<>();
            JSONObject json = new JSONObject(JSONstr);                 // Convierto el String recibido a JSONObject
            JSONArray jsonDirecciones = json.getJSONArray("results");  // Array - una busqueda puede retornar varios resultados
            for (int i = 0; i < jsonDirecciones.length(); i++) {
                // Recorro los resultados recibidos
                JSONObject jsonResultado = jsonDirecciones.getJSONObject(i);
                String jsonAddress = jsonResultado.getString("formatted_address");  // Obtiene la direccion formateada

                JSONObject jsonGeometry = jsonResultado.getJSONObject("geometry");
                JSONObject jsonLocation = jsonGeometry.getJSONObject("location");
                double jsonLat = jsonLocation.getDouble("lat");                     // Obtiene latitud
                double jsonLng = jsonLocation.getDouble("lng");                     // Obtiene longitud
                LatLng latLng=new LatLng(jsonLat,jsonLng);

                String coord = jsonLat + "," + jsonLng;

                Direcciones d = new Direcciones(latLng, jsonAddress);                    // Creo nueva instancia de direccion
                direcciones.add(d);                                                 // Agrego objeto d al array list
                Log.d("Direccion:", d.getDireccion() + " " + coord);
            }
            return direcciones;
        }
    }
    public boolean Validacion(String direccion) {
        int varSubstring = 0;
        String palabraNueva = "", caracter;
        boolean esNumero, resul = false;
        for (int i = 0; i < direccion.length(); i++) {

            if (varSubstring < direccion.length()) {
                varSubstring = i + 1;
            }
            caracter = direccion.substring(i, varSubstring);

            esNumero = isNumeric(caracter);

            if (caracter.compareTo(" ") != 0 && caracter.compareTo(".") != 0 && !esNumero && caracter.compareTo("-") != 0) {
                palabraNueva += caracter;
            }
            if (esNumero || caracter.compareTo("-") == 0) {
                i = direccion.length();
            }
        }
        if (palabraNueva.compareTo("Argentina") == 0) {
            resul = true;
        }
        return resul;
    }

    public  boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public Dialog onCreateDialogSingleChoice(ArrayList<Direcciones> listaDirecciones) {

//Initialize the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
//Source of the data in the DIalog

        String[] array = new String[listaDirecciones.size()];
        int index = 0;
        for (Direcciones value : listaDirecciones) {
            array[index] = value.getDireccion();
            index++;
        }

        final List<String> optionsList = Arrays.asList(array);
// Set the dialog title
        DireccionSeleccionada = optionsList.get(0);
        builder.setTitle("Elija su dirección: ")
                .setCancelable(false)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DireccionSeleccionada = optionsList.get(which);

                        Log.d("currentItem", DireccionSeleccionada);

                    }
                })

                // Set the action buttons
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                        for (Direcciones unaDireccion:direccionesObtenidas)
                        {
                            if (unaDireccion.getDireccion().equals(DireccionSeleccionada))
                            {
                                DireccionVerificada=true;
                                imageViewDireccionVerificada.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
                                _InputDireccion.setText(unaDireccion.getDireccion());
                                Direcciones dir=new Direcciones(unaDireccion.getLatLng(),unaDireccion.getDireccion());
                                 usuario.setDireccion(dir);
                            }
                        }

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
}
