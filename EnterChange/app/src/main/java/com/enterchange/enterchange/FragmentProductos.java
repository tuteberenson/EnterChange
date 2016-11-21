package com.enterchange.enterchange;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import static com.enterchange.enterchange.ActividadPrincipal.UsuarioActual;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProductos extends Fragment {


    EditText EdTxNombreProducto, EdTxDetalleProducto,EdTxValorMinimo,EdTxValorMaximo;
    Spinner spinnerCategorias;
    Button btnOk;
    public static ListView listViewProductos;
    public static LinearLayout LinearFormulario;
    public static TextView TxVwNoHayProductos;
    Generics generics;
    Context thisContext;
    SQLiteDatabase BaseDeDatos;
    AdapterListProductos adapterListProductos;
    Boolean estoyEditando;
    Productos productoNuevo;

  public static FloatingActionButton fab_productos;

    public FragmentProductos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View vista = inflater.inflate(R.layout.fragment_productos, container, false);

        thisContext = container.getContext();

        generics = new Generics(thisContext);

        AsociasVistas(vista);

        llenarCategorias();

        estoyEditando=false;

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!ErroresIngreso())
                {
                    if (!estoyEditando)
                    {
                        llenarNuevoProducto();
                        agregarProductoBD();

                        listViewProductos.setAdapter(null);
                        adapterListProductos=new AdapterListProductos(thisContext,R.layout.list_item_producto,LeerProductos());
                        listViewProductos.setAdapter(adapterListProductos);

                        mostrarVistas(true,false);
                    }
                }
            }
        });

        fab_productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarVistas(false,true);
                estoyEditando=false;
            }
        });

        if (LeerProductos().isEmpty())
        {
           mostrarVistas(false,false);
        }
        else
        {
            mostrarVistas(true,false);
            adapterListProductos=new AdapterListProductos(thisContext,R.layout.list_item_producto,LeerProductos());
            listViewProductos.setAdapter(adapterListProductos);
        }

        return vista;
    }

    private void llenarNuevoProducto()
    {
        productoNuevo= new Productos(EdTxNombreProducto.getText().toString().trim(),
                EdTxDetalleProducto.getText().toString().trim(),
                spinnerCategorias.getSelectedItem().toString(),
                Integer.valueOf(EdTxValorMinimo.getText().toString()),
                Integer.valueOf(EdTxValorMaximo.getText().toString()));
    }

   public void llenarCategorias()
   {
       ArrayList<String> categorias=new ArrayList<>();

       categorias.add("Muebles");
       categorias.add("Electro");

       ArrayAdapter<String> Adaptador= new ArrayAdapter<String>(thisContext,android.R.layout.simple_spinner_item, categorias);

       Adaptador.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
       spinnerCategorias.setAdapter(Adaptador);
   }

    private void agregarProductoBD()
    {

        Integer ultimoId;

        ContentValues NuevoProducto = new ContentValues();

        ultimoId =  obtenerUltimoId();
        ultimoId++;

        NuevoProducto.put("idproducto",ultimoId);
        NuevoProducto.put("idusuario",UsuarioActual.getIdUsuario());
        NuevoProducto.put("nombre", productoNuevo.getNombre());
        NuevoProducto.put("detalle", productoNuevo.getDetalle());
        NuevoProducto.put("valormin",productoNuevo.getValorMinimo());
        NuevoProducto.put("valormax",productoNuevo.getValorMaximo());
        NuevoProducto.put("categoria",productoNuevo.getCategoria());

        BaseDeDatos.insert("productos",null,NuevoProducto);

        BaseDeDatos.close();

    }


    private Integer obtenerUltimoId()
    {
        Integer ultimoId = 0;

        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select idproducto from productos",null);

        if (BaseDeDatos!=null)
        {
            if (ConjuntoDeRegistros.moveToFirst())
            {
                do {
                    ultimoId = ConjuntoDeRegistros.getInt(0);
                }while (ConjuntoDeRegistros.moveToNext());
            }
        }

        return ultimoId;
    }

    private boolean ErroresIngreso()
    {
        boolean HayErrores = false;

        if (TextUtils.isEmpty(EdTxNombreProducto.getText().toString()))
        {
            EdTxNombreProducto.setError("Rellene el campo");
            EdTxNombreProducto.requestFocus();
            HayErrores=true;
        }

        if (TextUtils.isEmpty(EdTxDetalleProducto.getText().toString()))
        {
            EdTxDetalleProducto.setError("Rellene el campo");
            EdTxDetalleProducto.requestFocus();
            HayErrores=true;
        }


        if (TextUtils.isEmpty(EdTxValorMinimo.getText().toString()))
        {
            EdTxValorMinimo.setError("Rellene el campo");
            EdTxValorMinimo.requestFocus();
            HayErrores=true;
        }


        if (TextUtils.isEmpty(EdTxValorMaximo.getText().toString()))
        {
            EdTxValorMaximo.setError("Rellene el campo");
            EdTxValorMaximo.requestFocus();
            HayErrores=true;
        }
        if (!TextUtils.isEmpty(EdTxValorMaximo.getText().toString()) && !TextUtils.isEmpty(EdTxValorMinimo.getText().toString()))
        {
            if (Integer.valueOf(EdTxValorMinimo.getText().toString()) > Integer.valueOf(EdTxValorMaximo.getText().toString()))
            {
                EdTxValorMaximo.setError("Rango inválido");
                EdTxValorMinimo.setError("Rango inválido");
                EdTxValorMinimo.requestFocus();
                HayErrores=true;
            }
        }

        return HayErrores;
    }

    public void mostrarVistas(boolean hayProductos, boolean editar) {
        if (hayProductos) {

            TxVwNoHayProductos.setVisibility(View.GONE);
            listViewProductos.setVisibility(View.VISIBLE);
            LinearFormulario.setVisibility(View.GONE);
            fab_productos.setVisibility(View.VISIBLE);

        } else
        {
            if (editar) {
                TxVwNoHayProductos.setVisibility(View.GONE);
                listViewProductos.setVisibility(View.GONE);
                LinearFormulario.setVisibility(View.VISIBLE);
                fab_productos.setVisibility(View.GONE);
            } else {
                TxVwNoHayProductos.setVisibility(View.VISIBLE);
                listViewProductos.setVisibility(View.GONE);
                LinearFormulario.setVisibility(View.GONE);
                fab_productos.setVisibility(View.GONE);
            }
        }
    }

    private void AsociasVistas(View vista)
    {
        EdTxNombreProducto= (EditText)vista.findViewById(R.id.input_nombre_producto);
        EdTxDetalleProducto = (EditText)vista.findViewById(R.id.input_detalle_producto);
        EdTxValorMinimo = (EditText)vista.findViewById(R.id.input_valor1_producto);
        EdTxValorMaximo = (EditText)vista.findViewById(R.id.input_valor2_producto);

        spinnerCategorias = (Spinner)vista.findViewById(R.id.spinnerCategorias);

        btnOk=(Button) vista.findViewById(R.id.btn_ok_productos);

        listViewProductos = (ListView)vista.findViewById(R.id.listView_productos);

        LinearFormulario = (LinearLayout)vista.findViewById(R.id.linear_Formulario_productos);

        TxVwNoHayProductos= (TextView)vista.findViewById(R.id.txVwProductos);

        fab_productos = (FloatingActionButton)vista.findViewById(R.id.fab_productos);
    }

    public ArrayList<Productos> LeerProductos()
    {
        ArrayList<Productos> listaProductos=new ArrayList<>();

        Productos unProducto;

        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ResultadoProductos;

        ResultadoProductos = BaseDeDatos.rawQuery("select idproducto, idusuario, nombre, detalle, valormin, valormax, categoria from productos where idusuario ="+UsuarioActual.getIdUsuario(),null);

        if (BaseDeDatos!=null) {
            if (ResultadoProductos.moveToFirst())
            {
                do {
                    unProducto = new Productos(ResultadoProductos.getString(2),ResultadoProductos.getString(3),ResultadoProductos.getString(6),ResultadoProductos.getInt(4),ResultadoProductos.getInt(5));
                    listaProductos.add(unProducto);

                }while (ResultadoProductos.moveToNext());
            }
        }
        return listaProductos;
    }
}
