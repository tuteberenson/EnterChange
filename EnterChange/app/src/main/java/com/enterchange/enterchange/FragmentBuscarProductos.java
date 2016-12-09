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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

import java.util.ArrayList;
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
    private EditText /*EdTxValorMin, EdTxValorMax,*/ EdTxUsername;
    RelativeLayout relativeLayoutRango;
    private Spinner spnCategorias;
    String TipoDeBusqueda;
    ListView listViewProductos;
    private AutoCompleteTextView autoCompleteProductos;
    AdapterListProductos adapterListProductos;
    ArrayList<Productos> ProductosObtenidos;
    Button btn_Buscar;
    //CrystalRangeSeekbar es una barra que parece en pantalla que se va a usar para determinar el rango de precios
    CrystalRangeSeekbar rangoDePrecios;
    //Los textviews que van debajo del rango de precios
    TextView tvMin, tvMax;

    Integer valorMax = 10000, valorMin=100;

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

        ProductosObtenidos = new ArrayList<>();

        AsociarVistas(vista);

        String[] productos = getProducts();

        llenarCategorias();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(thisContext, R.layout.list_item_auto_complete, productos);
        autoCompleteProductos.setAdapter(adapter);

        btn_Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductosObtenidos.clear();
                ProductosObtenidos.addAll(buscarProductos(TipoDeBusqueda));
                if (!ProductosObtenidos.isEmpty()) {
                    listViewProductos.setAdapter(null);
                    adapterListProductos = new AdapterListProductos(thisContext, R.layout.list_item_producto, ProductosObtenidos);
                    listViewProductos.setAdapter(adapterListProductos);
                }
                else
                {
                    listViewProductos.setAdapter(null);
                    Toast.makeText(thisContext, "No se obtuvieron productos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set listener
        rangoDePrecios.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText(String.valueOf(minValue)+"$");
                tvMax.setText(String.valueOf(maxValue)+"$");
            }
        });

// set final value listener
        rangoDePrecios.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue)
            {
                valorMax=maxValue.intValue();
                valorMin = minValue.intValue();
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });

        return vista;
    }

    private void AsociarVistas(View vista)
    {
        // get seekbar from view
        rangoDePrecios = (CrystalRangeSeekbar) vista.findViewById(R.id.rangeSeekbar1);

        // get min and max text view
        tvMin = (TextView) vista.findViewById(R.id.textMin1);
        tvMax = (TextView) vista.findViewById(R.id.textMax1);

        btn_Buscar = (Button)vista.findViewById(R.id.btn_buscarProducto);
        listViewProductos = (ListView)vista.findViewById(R.id.listView_BuscarProductos);
        autoCompleteProductos = (AutoCompleteTextView) vista.findViewById(R.id.buscar_por_nombre);
        EdTxUsername =(EditText)vista.findViewById(R.id.buscar_por_username);

      //  EdTxValorMin = (EditText)vista.findViewById(R.id.buscar_valor1_producto);
      //  EdTxValorMax = (EditText)vista.findViewById(R.id.buscar_valor2_producto);

        spnCategorias =(Spinner)vista.findViewById(R.id.spinner_buscar_categorias);
        relativeLayoutRango = (RelativeLayout) vista.findViewById(R.id.relativeLayoutRango);
    }


    public String[] getProducts()
    {
        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select idproducto  ,idusuario  ,nombre , detalle , valormin , valormax , idcategoria  from productos where idusuario !="+UsuarioActual.getIdUsuario()+" AND estado = 1",null);

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
                        btn_Buscar.setEnabled(true);
                        listViewProductos.setAdapter(null);
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
                relativeLayoutRango.setVisibility(View.GONE);
                spnCategorias.setVisibility(View.GONE);
                break;
            case "Username":
                autoCompleteProductos.setVisibility(View.GONE);
                EdTxUsername.setVisibility(View.VISIBLE);
                relativeLayoutRango.setVisibility(View.GONE);
                spnCategorias.setVisibility(View.GONE);
                break;
            case "Precio":
                autoCompleteProductos.setVisibility(View.GONE);
                EdTxUsername.setVisibility(View.GONE);
                relativeLayoutRango.setVisibility(View.VISIBLE);
                spnCategorias.setVisibility(View.GONE);
                break;
            case "Categoria":
                autoCompleteProductos.setVisibility(View.GONE);
                EdTxUsername.setVisibility(View.GONE);
                relativeLayoutRango.setVisibility(View.GONE);
                spnCategorias.setVisibility(View.VISIBLE);
                break;
        }
    }

    public ArrayList<Productos> buscarProductos(String parametro)
    {
        BaseDeDatos = generics.AbroBaseDatos();

        ArrayList<Productos> listaProductos = new ArrayList<>();

        Categorias unaCategoria;

        Cursor registrosProductos;

        Productos unProducto;

        String consultaSql = "";

            switch (parametro)
            {
                case "Nombre":
                    consultaSql= "SELECT p.idproducto, p.idusuario, p.nombre, p.detalle, p.valormin, p.valormax, p.idcategoria, c.nombre, p.estado" +
                            " FROM productos p " +
                            " INNER JOIN  categorias c" +
                            " ON p.idcategoria = c.idcategoria " +
                            " WHERE p.idusuario !="+UsuarioActual.getIdUsuario()+" AND p.estado = 1 AND p.nombre LIKE '"+autoCompleteProductos.getText()+"%'";
                    break;
                case "Username":
                    Integer idUsuarioBuscado=getIdUsuarioByUsername(EdTxUsername.getText().toString().trim());

                       consultaSql= "SELECT p.idproducto, p.idusuario, p.nombre, p.detalle, p.valormin, p.valormax, p.idcategoria, c.nombre, p.estado" +
                            " FROM productos p " +
                            " INNER JOIN  categorias c" +
                            " ON p.idcategoria = c.idcategoria " +
                            " WHERE p.estado = 1 AND p.idusuario = "+idUsuarioBuscado;
                    break;
                case "Precio":
                    consultaSql= "SELECT p.idproducto, p.idusuario, p.nombre, p.detalle, p.valormin, p.valormax, p.idcategoria, c.nombre, p.estado" +
                            " FROM productos p " +
                            " INNER JOIN  categorias c" +
                            " ON p.idcategoria = c.idcategoria " +
                            " WHERE p.idusuario !="+UsuarioActual.getIdUsuario()+" AND p.estado = 1 AND p.valorMin >"+valorMin +" AND p.valorMax <"+valorMax;
                    break;
                case "Categoria":
                    consultaSql= "SELECT p.idproducto, p.idusuario, p.nombre, p.detalle, p.valormin, p.valormax, p.idcategoria, c.nombre, p.estado" +
                            " FROM productos p " +
                            " INNER JOIN  categorias c" +
                            " ON p.idcategoria = c.idcategoria " +
                            " WHERE p.idusuario !="+UsuarioActual.getIdUsuario()+" AND p.estado = 1 AND c.nombre='"+spnCategorias.getSelectedItem().toString()+"'";
                    break;
            }

            Log.d("ConsultaSql",consultaSql);

            if (!consultaSql.equals(""))
            {
                Log.d("ConsultaSql",consultaSql);
                registrosProductos = BaseDeDatos.rawQuery(consultaSql, null);

                if (BaseDeDatos!=null) {

                    if (registrosProductos.moveToFirst())
                    {
                        do {

                            Boolean estado=false;

                            if (registrosProductos.getInt(8) == 1)
                            {
                                estado= true;
                            }

                            unaCategoria = new Categorias();
                            unaCategoria.setIdCategoria(registrosProductos.getInt(6));
                            unaCategoria.setNombre(registrosProductos.getString(7));
                            unProducto = new Productos(registrosProductos.getString(2),registrosProductos.getString(3), estado, unaCategoria,registrosProductos.getInt(4),registrosProductos.getInt(5), registrosProductos.getInt(0), registrosProductos.getInt(1));
                            listaProductos.add(unProducto);

                        }while (registrosProductos.moveToNext());
                    }
                }
            }


        return listaProductos;
    }

    private Integer getIdUsuarioByUsername(String username)
    {
        Integer id = 0;

        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select idusuario from usuarios WHERE username ='"+username+"'",null);

        if (BaseDeDatos!=null)
        {
            if (ConjuntoDeRegistros.moveToFirst())
            {
                do {
                    id = ConjuntoDeRegistros.getInt(0);
                }while (ConjuntoDeRegistros.moveToNext());
            }
        }

        return id;
    }

    public void llenarCategorias()
    {
        ArrayList<Categorias> categorias=new ArrayList<>();

        categorias.addAll(leerCategorias());

        ArrayAdapter<Categorias> Adaptador= new ArrayAdapter<Categorias>(thisContext,android.R.layout.simple_spinner_item, categorias);

        Adaptador.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnCategorias.setAdapter(Adaptador);
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
