package com.enterchange.enterchange;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import static com.enterchange.enterchange.ActividadPrincipal.UsuarioActual;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentIntercambios extends Fragment {


    Generics generics;
    SQLiteDatabase BaseDeDatos;
    Context thisContext;
    ListView listView_MisIntercambios, listView_Solicitudes;
    ArrayList<Intercambios> MisIntercambios, Solicitudes;
    AdapterListIntercambio adaptador;

    public FragmentIntercambios() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_intercambios, container, false);

        thisContext = container.getContext();

        generics = new Generics(thisContext);

        AsociarVistas(vista);

        MisIntercambios= new ArrayList<>();
        Solicitudes = new ArrayList<>();

        MisIntercambios.addAll(LeerMisIntercambios());
        Solicitudes.addAll(LeerSolicitudes());

        listView_Solicitudes.setAdapter(null);
        listView_MisIntercambios.setAdapter(null);

        adaptador = new AdapterListIntercambio(thisContext,R.layout.list_item_intercambio,MisIntercambios);
        listView_MisIntercambios.setAdapter(adaptador);

        adaptador = new AdapterListIntercambio(thisContext,R.layout.list_item_intercambio,Solicitudes);
        listView_Solicitudes.setAdapter(adaptador);

        return vista;
    }

    private void AsociarVistas(View vista)
    {
        listView_MisIntercambios = (ListView)vista.findViewById(R.id.listView_misIntercambios);
        listView_Solicitudes = (ListView)vista.findViewById(R.id.listView_Solicitudes);
    }

    public ArrayList<Intercambios> LeerMisIntercambios()
    {
        BaseDeDatos = generics.AbroBaseDatos();

        ArrayList<Intercambios> listaIntercambios = new ArrayList<>();

        Intercambios unIntercambio;

        Cursor conjuntoDeIntercambios;
        String ConsultaSql ="SELECT idintercambio,idusuarioduenio, idusuariointeresado ,idproductoduenio, idproductointeresado, estado, nuevo FROM intercambios WHERE idusuariointeresado = "+ UsuarioActual.getIdUsuario();

        conjuntoDeIntercambios = BaseDeDatos.rawQuery(ConsultaSql,null);

        if (BaseDeDatos!=null)
        {
            if (conjuntoDeIntercambios.moveToFirst())
            {
                do {
                    Boolean nuevo = true;
                    if (conjuntoDeIntercambios.getInt(6) == 0)
                    {
                        nuevo = false;
                    }
                    unIntercambio = new Intercambios(conjuntoDeIntercambios.getInt(0),
                                                    conjuntoDeIntercambios.getInt(1),
                                                    conjuntoDeIntercambios.getInt(2),
                                                    conjuntoDeIntercambios.getInt(3),
                                                    conjuntoDeIntercambios.getInt(4),
                                                    conjuntoDeIntercambios.getInt(5),
                                                    nuevo);
                    listaIntercambios.add(unIntercambio);
                }while (conjuntoDeIntercambios.moveToNext());
            }
        }

        return listaIntercambios;
    }
    public ArrayList<Intercambios> LeerSolicitudes()
    {
        BaseDeDatos = generics.AbroBaseDatos();

        ArrayList<Intercambios> listaIntercambios = new ArrayList<>();

        Intercambios unIntercambio;

        Cursor conjuntoDeIntercambios;
        String ConsultaSql ="SELECT idintercambio,idusuarioduenio, idusuariointeresado ,idproductoduenio, idproductointeresado, estado, nuevo FROM intercambios WHERE idusuarioduenio = "+ UsuarioActual.getIdUsuario();

        conjuntoDeIntercambios = BaseDeDatos.rawQuery(ConsultaSql,null);

        if (BaseDeDatos!=null)
        {
            if (conjuntoDeIntercambios.moveToFirst())
            {
                do {
                    Boolean nuevo = true;
                    if (conjuntoDeIntercambios.getInt(6) == 0)
                    {
                        nuevo = false;
                    }
                    unIntercambio = new Intercambios(conjuntoDeIntercambios.getInt(0),
                            conjuntoDeIntercambios.getInt(1),
                            conjuntoDeIntercambios.getInt(2),
                            conjuntoDeIntercambios.getInt(3),
                            conjuntoDeIntercambios.getInt(4),
                            conjuntoDeIntercambios.getInt(5),
                            nuevo);
                    listaIntercambios.add(unIntercambio);
                }while (conjuntoDeIntercambios.moveToNext());
            }
        }

        return listaIntercambios;
    }
}
