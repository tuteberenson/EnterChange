package com.enterchange.enterchange;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.enterchange.enterchange.ActividadPrincipal.UsuarioActual;
import static com.enterchange.enterchange.ActividadPrincipal.navigationView;


public class FragmentMisDatos extends Fragment {

    SQLiteDatabase BaseDeDatos;
    Generics generics;
    Context context;
    FloatingActionButton fab;

    String  DireccionSeleccionada;
    ArrayList<Direcciones> direccionesObtenidas;
    EditText EdTxNombre_Apellido, EdTxUsername, EdTxEmail, EdTxDireccion, EdTxTelefono, EdTxApellido;
    Button btnOK, btnVerificarDireccion;
    ImageView imageViewVerificado;
    TextView TxVwHeaderNombre;
    boolean DireccionVerificada=false;

    private InterfaseModificarNavigationDrawer mInterfase;

    public void setInterfase(InterfaseModificarNavigationDrawer interfase) {
        mInterfase = interfase;
    }

    public FragmentMisDatos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_mis_datos, container, false);


        context = container.getContext();

        direccionesObtenidas = new ArrayList<>();

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

        btnVerificarDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EdTxDireccion.getText().toString().trim().equals("")) {
                    String url = "https://maps.googleapis.com/maps/api/geocode/json?address=";

                    url += EdTxDireccion.getText().toString().trim();  // Copio la direccion ingresada al final de la URL
                    url += "&components=country:AR&key=AIzaSyA0T6Xd7zuyregCBfyon2axZWcgs1CUq-A";

                    if (generics.isConnectedToInternet()) {
                        new GeolocalizacionTask().execute(url);  // Llamo a clase async con url
                    }
                    else
                    {
                        Toast.makeText(context, "Conéctese a internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DireccionVerificada)
                {
                    Toast.makeText(context, "Verifique la direccion", Toast.LENGTH_SHORT).show();
                }
                else if (!ErroresIngreso()) {
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
        mInterfase.ModificarNavigationDrawer();

        EdTxNombre_Apellido.setText(UsuarioActual.getNombre()+" "+UsuarioActual.getApellido());
    }

    private void ModificarEnBD()
    {
        BaseDeDatos =generics.AbroBaseDatos();

        ContentValues registroAModificar = new ContentValues();

        Log.d("ModificarEnBD",EdTxNombre_Apellido.getText().toString());
        registroAModificar.put("nombre",EdTxNombre_Apellido.getText().toString());
        registroAModificar.put("apellido",EdTxApellido.getText().toString());

        registroAModificar.put("direccion",UsuarioActual.getDireccion().getDireccion());

        registroAModificar.put("latitud",UsuarioActual.getDireccion().getLatLng().latitude);
        registroAModificar.put("longitud",UsuarioActual.getDireccion().getLatLng().longitude);

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
        //UsuarioActual.setDireccion(EdTxDireccion.getText().toString());
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
        //
        EdTxDireccion.setText(UsuarioActual.getDireccion().getDireccion());
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

        btnVerificarDireccion = (Button)v.findViewById(R.id.btnValidar_direccion_modificar);
        imageViewVerificado = (ImageView)v.findViewById(R.id.imgVw_verificado_m);
    }

    public void activarControles(boolean mostrar)
    {

        EdTxDireccion.setEnabled(mostrar);
        EdTxUsername.setEnabled(mostrar);
        EdTxNombre_Apellido.setEnabled(mostrar);
        EdTxTelefono.setEnabled(mostrar);
        EdTxDireccion.setEnabled(mostrar);

        if (mostrar)
        {
            btnVerificarDireccion.setVisibility(View.VISIBLE);
            imageViewVerificado.setVisibility(View.VISIBLE);
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
            btnVerificarDireccion.setVisibility(View.INVISIBLE);
            imageViewVerificado.setVisibility(View.INVISIBLE);
            EdTxApellido.setVisibility(View.GONE);
            btnOK.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);
            if (EdTxTelefono.getText().toString().equals(""))
            {
                EdTxTelefono.setText("-");
            }
        }
    }

    public interface InterfaseModificarNavigationDrawer
    {
        public void ModificarNavigationDrawer();

    }

    private class GeolocalizacionTask extends AsyncTask<String, Void, ArrayList<Direcciones>> {
        private OkHttpClient client = new OkHttpClient();
        private ProgressDialog dialog = new ProgressDialog(context);

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

            if (Validacion(resultado.get(0).getDireccion())) {
                EdTxDireccion.setError("Dirección inválida");
            } else {

                if (resultado != null) {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                                imageViewVerificado.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
                                EdTxDireccion.setText(unaDireccion.getDireccion());
                                Direcciones dir=new Direcciones(unaDireccion.getLatLng(),unaDireccion.getDireccion());
                                UsuarioActual.setDireccion(dir);
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
