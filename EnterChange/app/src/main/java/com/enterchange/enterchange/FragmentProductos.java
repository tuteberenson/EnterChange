package com.enterchange.enterchange;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    ListView listViewProductos;
    LinearLayout LinearFormulario;
    TextView TxVwNoHayProductos;
    Generics generics;
    Context thisContext;
    SQLiteDatabase BaseDeDatos;
    AdapterListProductos adapterListProductos;
    Boolean estoyEditando;
    Productos productoNuevo;
    Integer posicionProductoAModificar = 0;
    View InfladorLayout;

    /*public FragmentProductos() {
        // Required empty public constructor
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View vista = inflater.inflate(R.layout.fragment_productos, container, false);

        thisContext = container.getContext();

        generics = new Generics(thisContext);

        InfladorLayout=vista;

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
                        limpiarFormulario();
                        listViewProductos.setAdapter(null);
                        adapterListProductos=new AdapterListProductos(thisContext,R.layout.list_item_producto,LeerProductos());
                        listViewProductos.setAdapter(adapterListProductos);
                            mostrarVistas(true, false);

                    }
                    else
                    {
                        actualizarProductoBD(adapterListProductos.getItem(posicionProductoAModificar).getIdProducto());
                        adapterListProductos=new AdapterListProductos(thisContext,R.layout.list_item_producto, LeerProductos());
                        listViewProductos.setAdapter(adapterListProductos);
                            mostrarVistas(true, false);

                    }
                }
            }
        });

        listViewProductos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                new AlertDialog.Builder(thisContext)
                        .setTitle("¿Editar o Eliminar?")
                        .setMessage("Producto: "+adapterListProductos.getItem(position).getNombre())
                        .setPositiveButton("Editar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                posicionProductoAModificar = position;
                                modificarProducto(adapterListProductos.getItem(position));
                                estoyEditando=true;
                                mostrarVistas(false,true);
                            }

                        })
                        .setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(thisContext)
                                        .setTitle("Eliminar")
                                        .setMessage("¿Desea eliminar el producto: "+adapterListProductos.getItem(position).getNombre()+"?")
                                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                eliminarProductoBD(adapterListProductos.getItem(position).getIdProducto());
                                                adapterListProductos=new AdapterListProductos(thisContext,R.layout.list_item_producto, LeerProductos());
                                                listViewProductos.setAdapter(adapterListProductos);
                                                if (!LeerProductos().isEmpty()) {
                                                    mostrarVistas(true, false);
                                                }
                                                else {
                                                    mostrarVistas(false,false);
                                                }
                                            }
                                        })
                                        .setNegativeButton("No",null)
                                        .setCancelable(false)
                                        .show();
                            }
                        })
                        .setCancelable(true)
                        .show();
                return true;
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

    private void actualizarProductoBD(Integer idProducto)
    {
        BaseDeDatos =generics.AbroBaseDatos();

        ContentValues registroAModificar = new ContentValues();

        registroAModificar.put("nombre",EdTxNombreProducto.getText().toString());
        registroAModificar.put("detalle",EdTxDetalleProducto.getText().toString());
        registroAModificar.put("valormin",Integer.valueOf(EdTxValorMinimo.getText().toString()));
        registroAModificar.put("valormax",Integer.valueOf(EdTxValorMaximo.getText().toString()));
        registroAModificar.put("categoria", spinnerCategorias.getSelectedItem().toString());

        if (BaseDeDatos!=null)
        {
            BaseDeDatos.update("productos",registroAModificar,"idproducto="+idProducto,null);
        }
    }

    private void limpiarFormulario()
    {
        EdTxNombreProducto.setText("");
        EdTxDetalleProducto.setText("");
        EdTxValorMinimo.setText("");
        EdTxValorMaximo.setText("");
    }

    private void modificarProducto(Productos item)
    {
        EdTxNombreProducto.setText(item.getNombre());
        EdTxDetalleProducto.setText(item.getDetalle());
        EdTxValorMinimo.setText(item.getValorMinimo().toString());
        EdTxValorMaximo.setText(item.getValorMaximo().toString());
    }


    private void llenarNuevoProducto()
    {
        Integer ultimoId;
        ultimoId =  obtenerUltimoId();
        ultimoId++;

        productoNuevo= new Productos(EdTxNombreProducto.getText().toString().trim(),
                EdTxDetalleProducto.getText().toString().trim(),
                spinnerCategorias.getSelectedItem().toString(),
                Integer.valueOf(EdTxValorMinimo.getText().toString()),
                Integer.valueOf(EdTxValorMaximo.getText().toString()), ultimoId);
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
        BaseDeDatos =generics.AbroBaseDatos();
        ContentValues NuevoProducto = new ContentValues();

        NuevoProducto.put("idproducto",productoNuevo.getIdProducto());
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

    public void eliminarProductoBD(Integer idProducto)
    {
        BaseDeDatos = generics.AbroBaseDatos();

        if (BaseDeDatos!=null)
        {
            BaseDeDatos.delete("productos","idproducto="+idProducto,null);
        }

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

        } else
        {
            if (editar) {
                TxVwNoHayProductos.setVisibility(View.GONE);
                listViewProductos.setVisibility(View.GONE);
                LinearFormulario.setVisibility(View.VISIBLE);

            } else {
                TxVwNoHayProductos.setVisibility(View.VISIBLE);
                listViewProductos.setVisibility(View.GONE);
                LinearFormulario.setVisibility(View.GONE);

            }
        }
    }

    public void agregarProductoMenuItem()
    {
        AsociasVistas(InfladorLayout);
        LinearFormulario.setVisibility(View.VISIBLE);
        TxVwNoHayProductos.setVisibility(View.GONE);
        listViewProductos.setVisibility(View.GONE);
        estoyEditando=false;
        EdTxNombreProducto.setText("");
        EdTxDetalleProducto.setText("");
        EdTxValorMinimo.setText("");
        EdTxValorMaximo.setText("");
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
                    unProducto = new Productos(ResultadoProductos.getString(2),ResultadoProductos.getString(3),ResultadoProductos.getString(6),ResultadoProductos.getInt(4),ResultadoProductos.getInt(5), ResultadoProductos.getInt(0));
                    listaProductos.add(unProducto);

                }while (ResultadoProductos.moveToNext());
            }
        }
        return listaProductos;
    }
}
