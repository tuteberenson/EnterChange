package com.enterchange.enterchange;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tute on 30/11/2016.
 */

public class AdapterListIntercambio extends ArrayAdapter<Intercambios> {
    private ArrayList<Intercambios> intercambios;
    private Context context;
    Generics generics;
    SQLiteDatabase BaseDeDatos;


    public AdapterListIntercambio(Context context, int resource, ArrayList<Intercambios> intercambios) {
        super(context, resource);
        this.intercambios = intercambios;
        this.context = context;
        generics =new Generics(context);
    }

    @Override
    public int getCount() {
        return intercambios.size();
    }

    @Nullable
    @Override
    public Intercambios getItem(int position) {
        return intercambios.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int posicion, View VistaActual, ViewGroup GrupoActual) {
        View vistaADevolver;
        vistaADevolver = null;

        LayoutInflater infladorDeLayouts;

        Log.d("getViewAdpater", "Inicializo el inflador");
        infladorDeLayouts = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Log.d("getViewAdapter", "Inflo la View");
        vistaADevolver=infladorDeLayouts.inflate(R.layout.list_item_intercambio,GrupoActual,false);

        TextView TxVwProd1,TxVwProd2,TxVwUser1,TxVwUser2, TxVwEstado;
        LinearLayout unItem;

        TxVwProd1=(TextView)vistaADevolver.findViewById(R.id.inter_producto_1);
        TxVwProd2=(TextView)vistaADevolver.findViewById(R.id.inter_producto_2);
        TxVwUser2=(TextView)vistaADevolver.findViewById(R.id.inter_usuario_2);
        TxVwUser1=(TextView)vistaADevolver.findViewById(R.id.inter_usuario_1);
        TxVwEstado = (TextView)vistaADevolver.findViewById(R.id.inter_estado);

        unItem = (LinearLayout)vistaADevolver.findViewById(R.id.unItemListViewIntercambio);

        Intercambios intercambio=new Intercambios();

        intercambio = getItem(posicion);

        TxVwProd1.setText(getNombreProductoById(intercambio.getIdProductoD()));
        TxVwProd2.setText(getNombreProductoById(intercambio.getIdProductoI()));
        TxVwUser2.setText(getUsernameUsuarioById(intercambio.getIdUsuarioI()));
        TxVwUser1.setText(getUsernameUsuarioById(intercambio.getIdUsuarioD()));

        switch (intercambio.getEstado())
        {
            case 0:
                    TxVwEstado.setText("Rechazado");
                    unItem.setBackgroundColor(Color.rgb(244, 80, 66));
                break;
            case 1:
                TxVwEstado.setText("Pendiente");
                break;
            case 2:
                TxVwEstado.setText("Aceptado");
                break;
        }

        return vistaADevolver;
    }

    private String getNombreProductoById(Integer idProducto)
    {
        String resultado="";

        Cursor registros;

        BaseDeDatos = generics.AbroBaseDatos();

        String consultaSql="SELECT nombre FROM productos WHERE idProducto = "+idProducto;

        registros = BaseDeDatos.rawQuery(consultaSql,null);

        if(BaseDeDatos!=null)
        {
            if (registros.moveToFirst())
            {
                resultado = registros.getString(0);
            }
        }

        return resultado;
    }
    private String getUsernameUsuarioById(Integer idUsuario)
    {
        String resultado="";

        Cursor registros;

        BaseDeDatos = generics.AbroBaseDatos();

        String consultaSql="SELECT username FROM usuarios WHERE idUsuario = "+idUsuario;

        registros = BaseDeDatos.rawQuery(consultaSql,null);

        if(BaseDeDatos!=null)
        {
            if (registros.moveToFirst())
            {
                resultado = registros.getString(0);
            }
        }

        return resultado;
    }
}
