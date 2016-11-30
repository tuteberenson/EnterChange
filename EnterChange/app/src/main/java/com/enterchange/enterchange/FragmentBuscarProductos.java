package com.enterchange.enterchange;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import static com.enterchange.enterchange.ActividadPrincipal.UsuarioActual;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBuscarProductos extends Fragment {

    Generics generics;
    SQLiteDatabase BaseDeDatos;
    private Context thisContext;
    private EditText EdTxValorMin, EdTxValorMax, EdTxUsername;
    private Spinner spnCategorias;
    String TipoDeBusqueda;
    private AutoCompleteTextView autoCompleteProductos;

    /*public FragmentBuscarProductos() {
        // Required empty public constructor
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista=  inflater.inflate(R.layout.fragment_buscar_productos, container, false);

        thisContext=container.getContext();

        generics = new Generics(thisContext);

        AsociarVistas(vista);

        String[] productos = getProducts();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(thisContext, R.layout.list_item_auto_complete, productos);
        autoCompleteProductos.setAdapter(adapter);

        return vista;
    }

    private void AsociarVistas(View vista)
    {
        autoCompleteProductos = (AutoCompleteTextView) vista.findViewById(R.id.buscar_por_nombre);
        EdTxUsername =(EditText)vista.findViewById(R.id.buscar_por_username);
        EdTxValorMin = (EditText)vista.findViewById(R.id.buscar_valor1_producto);
        EdTxValorMax = (EditText)vista.findViewById(R.id.buscar_valor2_producto);
        spnCategorias =(Spinner)vista.findViewById(R.id.spinner_buscar_categorias);
    }


    public String[] getProducts()
    {
        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select idproducto  ,idusuario  ,nombre , detalle , valormin , valormax , idcategoria  from productos where idusuario !="+UsuarioActual.getIdUsuario(),null);

        if(ConjuntoDeRegistros.getCount() >0)
        {
            String[] str = new String[ConjuntoDeRegistros.getCount()];
            int i = 0;

            while (ConjuntoDeRegistros.moveToNext())
            {
                str[i] = ConjuntoDeRegistros.getString(2);
                i++;
            }
            return str;
        }
        else
        {
            return new String[] {};
        }
    }

    public Dialog onCreateDialogTipoDeBusqueda() {

        //Initialize the Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
        //Source of the data in the DIalog
        String[] array = {"Nombre", "Username", "Precio","Categoria"};

        TipoDeBusqueda = "Nombre";

        final List<String> optionsList = Arrays.asList(array);
        // Set the dialog title
        builder.setTitle("Buscar por: ")
        // Specify the list array, the items to be selected by default (null for none),
        // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        TipoDeBusqueda = optionsList.get(which);

                        Log.d("currentItem", TipoDeBusqueda);
                    }
                })

                // Set the action buttons
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                            mostrarControles();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    private void mostrarControles()
    {
        switch (TipoDeBusqueda)
        {
            case "Nombre":
                autoCompleteProductos.setVisibility(View.VISIBLE);
                EdTxUsername.setVisibility(View.GONE);
                EdTxValorMax.setVisibility(View.GONE);
                EdTxValorMin.setVisibility(View.GONE);
                spnCategorias.setVisibility(View.GONE);
                break;
            case "Username":
                autoCompleteProductos.setVisibility(View.GONE);
                EdTxUsername.setVisibility(View.VISIBLE);
                EdTxValorMax.setVisibility(View.GONE);
                EdTxValorMin.setVisibility(View.GONE);
                spnCategorias.setVisibility(View.GONE);
                break;
            case "Precio":
                autoCompleteProductos.setVisibility(View.GONE);
                EdTxUsername.setVisibility(View.GONE);
                EdTxValorMax.setVisibility(View.VISIBLE);
                EdTxValorMin.setVisibility(View.VISIBLE);
                spnCategorias.setVisibility(View.GONE);
                break;
            case "Categoria":
                autoCompleteProductos.setVisibility(View.GONE);
                EdTxUsername.setVisibility(View.GONE);
                EdTxValorMax.setVisibility(View.GONE);
                EdTxValorMin.setVisibility(View.GONE);
                spnCategorias.setVisibility(View.VISIBLE);
                break;
        }
    }
}
