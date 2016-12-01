package com.enterchange.enterchange;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.enterchange.enterchange.ActividadPrincipal.UsuarioActual;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentListaProductos extends Fragment {

    public static ListView listViewProductos;
    Generics generics;
    Context thisContext;
    SQLiteDatabase BaseDeDatos;
   public static AdapterListProductos adapterListProductos;
    private LinearLayout LinearPrincipal;
    private TextView TxVwNoHayProductos;
    public static  ArrayList<Productos> productosMostrados;

    public FragmentListaProductos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista= inflater.inflate(R.layout.fragment_lista_productos, container, false);

        thisContext= container.getContext();

        productosMostrados = new ArrayList<>();

        generics = new Generics(thisContext);

        AsociarVistas(vista);

        if (LeerProductos().isEmpty())
        {
            LinearPrincipal.setVisibility(View.GONE);
            TxVwNoHayProductos.setVisibility(View.VISIBLE);
        }
        else
        {
            LinearPrincipal.setVisibility(View.VISIBLE);
            TxVwNoHayProductos.setVisibility(View.GONE);
        }

        productosMostrados.addAll(LeerProductos());
        listViewProductos.setAdapter(null);
        adapterListProductos=new AdapterListProductos(thisContext,R.layout.list_item_producto,productosMostrados);
        listViewProductos.setAdapter(adapterListProductos);

        listViewProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent activityDetalleProducto =new Intent(thisContext,ActivityDetalleProducto.class);
                Log.d("UbicacionUserActual","Latitud: "+UsuarioActual.getDireccion().getLatLng().latitude+" - Longitud: "+ UsuarioActual.getDireccion().getLatLng().longitude);
                activityDetalleProducto.putExtra("Producto", productosMostrados.get(position));
                activityDetalleProducto.putExtra("PosicionProducto",position);
                //Toast.makeText(thisContext, "Producto tocado: "+productosMostrados.get(position).getNombre(), Toast.LENGTH_SHORT).show();
                startActivity(activityDetalleProducto);
            }
        });

        return vista;
    }


    private void AsociarVistas(View vista)
    {
        listViewProductos = (ListView)vista.findViewById(R.id.list_view_productos_al_azar);
        LinearPrincipal = (LinearLayout)vista.findViewById(R.id.linear_contenedor_lista_prods);
        TxVwNoHayProductos = (TextView)vista.findViewById(R.id.txVwNoHayProductos);
    }

    public static void updateReceiptsList(ArrayList<Productos> newlist) {
        productosMostrados.clear();
        productosMostrados.addAll(newlist);
        adapterListProductos.notifyDataSetChanged();
    }
    public static void myRemove(int position){
        productosMostrados.remove(position);
        adapterListProductos.notifyDataSetChanged();
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
                " WHERE p.idusuario !="+UsuarioActual.getIdUsuario()+" AND p.estado = 1"+
                " ORDER BY RANDOM() LIMIT 15";

        ResultadoProductos = BaseDeDatos.rawQuery(ConsultaSql,null);

        if (BaseDeDatos!=null) {

            if (ResultadoProductos.moveToFirst())
            {
                do {

                    Boolean estado=false;

                    if (ResultadoProductos.getInt(8) == 1)
                    {
                        estado= true;
                    }

                    unaCategoria = new Categorias();
                    unaCategoria.setIdCategoria(ResultadoProductos.getInt(6));
                    unaCategoria.setNombre(ResultadoProductos.getString(7));
                    unProducto = new Productos(ResultadoProductos.getString(2),ResultadoProductos.getString(3), estado, unaCategoria,ResultadoProductos.getInt(4),ResultadoProductos.getInt(5), ResultadoProductos.getInt(0), ResultadoProductos.getInt(1));
                    listaProductos.add(unProducto);

                }while (ResultadoProductos.moveToNext());
            }
        }

        return listaProductos;
    }

}
