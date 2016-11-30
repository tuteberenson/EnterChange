package com.enterchange.enterchange;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
                    InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputMethodManager.hideSoftInputFromWindow(EdTxNombreProducto.getWindowToken(), 0);
                    inputMethodManager.hideSoftInputFromWindow(EdTxValorMaximo.getWindowToken(), 0);
                    inputMethodManager.hideSoftInputFromWindow(EdTxValorMinimo.getWindowToken(), 0);
                    inputMethodManager.hideSoftInputFromWindow(EdTxDetalleProducto.getWindowToken(), 0);
                    if (!estoyEditando)
                    {
                        llenarNuevoProducto();
                        agregarProductoBD();
                        limpiarFormulario();
                        listViewProductos.setAdapter(null);
                        adapterListProductos=new AdapterListProductos(thisContext,R.layout.list_item_producto,LeerProductos());
                        listViewProductos.setAdapter(adapterListProductos);
                        mostrarVistas(true, false);
                        Toast.makeText(thisContext, "Producto registrado", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        actualizarProductoBD(adapterListProductos.getItem(posicionProductoAModificar).getIdProducto());
                        adapterListProductos=new AdapterListProductos(thisContext,R.layout.list_item_producto, LeerProductos());
                        listViewProductos.setAdapter(adapterListProductos);
                            mostrarVistas(true, false);
                        Toast.makeText(thisContext, "Producto modificado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        listViewProductos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
            {
                if (adapterListProductos.getItem(position).getEstado()) {
                    new AlertDialog.Builder(thisContext)
                            .setTitle("¿Editar o Eliminar?")
                            .setMessage("Producto: " + adapterListProductos.getItem(position).getNombre())
                            .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    posicionProductoAModificar = position;
                                    modificarProducto(adapterListProductos.getItem(position));
                                    estoyEditando = true;
                                    mostrarVistas(false, true);
                                }

                            })
                            .setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AlertDialog.Builder(thisContext)
                                            .setTitle("Eliminar")
                                            .setMessage("¿Desea eliminar el producto: " + adapterListProductos.getItem(position).getNombre() + "?")
                                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    eliminarProducto(position);
                                                }
                                            })
                                            .setNegativeButton("No", null)
                                            .setCancelable(false)
                                            .show();
                                }
                            })
                            .setCancelable(true)
                            .show();
                }
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

    public void eliminarProducto(int posicionProducto)
    {
        eliminarProductoBD(adapterListProductos.getItem(posicionProducto).getIdProducto());
        adapterListProductos=new AdapterListProductos(thisContext,R.layout.list_item_producto, LeerProductos());
        listViewProductos.setAdapter(adapterListProductos);
        if (!LeerProductos().isEmpty()) {
            mostrarVistas(true, false);
        }
        else {
            mostrarVistas(false,false);
        }
        Toast.makeText(thisContext, "Producto eliminado", Toast.LENGTH_SHORT).show();
    }
    private void actualizarProductoBD(Integer idProducto)
    {
        BaseDeDatos =generics.AbroBaseDatos();

        ContentValues registroAModificar = new ContentValues();

        Categorias unaCat=new Categorias();

        unaCat=(Categorias) spinnerCategorias.getSelectedItem();

        registroAModificar.put("nombre",EdTxNombreProducto.getText().toString());
        registroAModificar.put("detalle",EdTxDetalleProducto.getText().toString());
        registroAModificar.put("valormin",Integer.valueOf(EdTxValorMinimo.getText().toString()));
        registroAModificar.put("valormax",Integer.valueOf(EdTxValorMaximo.getText().toString()));
        registroAModificar.put("idcategoria",unaCat.getIdCategoria());

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
        spinnerCategorias.setSelection(0);
    }

    private void modificarProducto(Productos item)
    {
        EdTxNombreProducto.setText(item.getNombre());
        EdTxDetalleProducto.setText(item.getDetalle());
        EdTxValorMinimo.setText(item.getValorMinimo().toString());
        EdTxValorMaximo.setText(item.getValorMaximo().toString());
        spinnerCategorias.setSelection(item.getCategorias().getIdCategoria()-1);
    }


    private void llenarNuevoProducto()
    {
        Integer ultimoId;
        ultimoId =  obtenerUltimoId();
        ultimoId++;

        productoNuevo= new Productos(EdTxNombreProducto.getText().toString().trim(),
                EdTxDetalleProducto.getText().toString().trim(),
                true, (Categorias)spinnerCategorias.getSelectedItem(),
                Integer.valueOf(EdTxValorMinimo.getText().toString()),
                Integer.valueOf(EdTxValorMaximo.getText().toString()), ultimoId, UsuarioActual.getIdUsuario());
    }

   public void llenarCategorias()
   {
       ArrayList<Categorias> categorias=new ArrayList<>();

       categorias.addAll(leerCategorias());

       ArrayAdapter<Categorias> Adaptador= new ArrayAdapter<Categorias>(thisContext,android.R.layout.simple_spinner_item, categorias);

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
        NuevoProducto.put("idcategoria",productoNuevo.getCategorias().getIdCategoria());
        NuevoProducto.put("estado",1);

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
                EdTxValorMaximo.setText("");
                EdTxValorMinimo.setText("");
                Toast.makeText(thisContext, "Rango inválido", Toast.LENGTH_SHORT).show();
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

    public Integer getCountProductos()
    {
        return LeerProductos().size();
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
        spinnerCategorias.setSelection(0);
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

        Categorias unaCategoria;


        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ResultadoProductos;

        String ConsultaSql ="SELECT p.idproducto, p.idusuario, p.nombre, p.detalle, p.valormin, p.valormax, p.idcategoria, c.nombre, p.estado" +
                " FROM productos p " +
                " INNER JOIN  categorias c" +
                " ON p.idcategoria = c.idcategoria " +
                " WHERE p.idusuario ="+UsuarioActual.getIdUsuario();

        ResultadoProductos = BaseDeDatos.rawQuery(ConsultaSql,null);

        if (BaseDeDatos!=null) {

            if (ResultadoProductos.moveToFirst())
            {
                Log.d("LeerProductos","Lee productos");
                do {
                    Boolean estado=false;
                    if (ResultadoProductos.getInt(8) == 1)
                    {
                        estado= true;
                    }
                    unaCategoria = new Categorias();
                    unaCategoria.setIdCategoria(ResultadoProductos.getInt(6));
                    unaCategoria.setNombre(ResultadoProductos.getString(7));
                    unProducto = new Productos(ResultadoProductos.getString(2),ResultadoProductos.getString(3),estado, unaCategoria,ResultadoProductos.getInt(4),ResultadoProductos.getInt(5), ResultadoProductos.getInt(0), ResultadoProductos.getInt(1));
                    listaProductos.add(unProducto);

                }while (ResultadoProductos.moveToNext());
            }
        }
        for (Productos p: listaProductos)
        {
            Log.d("ListaProductos", p.getNombre()+" "+p.getDetalle()+""+p.getCategorias().getNombre() +"" +p.getCategorias().getIdCategoria());
        }
        return listaProductos;
    }

    public ArrayList<Categorias> leerCategorias()
    {

        ArrayList<Categorias> listaCategorias=new ArrayList<>();

        Categorias unaCategoria = new Categorias();

        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ResultadoCategorias;

        ResultadoCategorias = BaseDeDatos.rawQuery("select idcategoria, nombre from categorias",null);

        if (BaseDeDatos!=null) {
            if (ResultadoCategorias.moveToFirst())
            {
                do {
                    unaCategoria = new Categorias();
                    unaCategoria.setIdCategoria(ResultadoCategorias.getInt(0));
                    unaCategoria.setNombre(ResultadoCategorias.getString(1));
                    listaCategorias.add(unaCategoria);
                }while (ResultadoCategorias.moveToNext());
            }
        }
        return listaCategorias;
    }

}
