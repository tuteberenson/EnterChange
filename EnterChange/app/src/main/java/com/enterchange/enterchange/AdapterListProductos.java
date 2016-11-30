package com.enterchange.enterchange;

import android.content.Context;
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
 * Created by Tute on 21/11/2016.
 */

public class AdapterListProductos extends ArrayAdapter<Productos> {

    private ArrayList<Productos> productos;
    private Context context;

    public AdapterListProductos(Context context, int resource, ArrayList<Productos> productos) {
        super(context, resource);
        this.productos = productos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Nullable
    @Override
    public Productos getItem(int position) {
        return productos.get(position);
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
        vistaADevolver=infladorDeLayouts.inflate(R.layout.list_item_producto,GrupoActual,false);

        TextView TxVwNombre,TxVwDetalle,TxVwCategoria,TxVwValor;
        LinearLayout unItem;

        TxVwNombre=(TextView)vistaADevolver.findViewById(R.id.nombre_producto);
        TxVwDetalle=(TextView)vistaADevolver.findViewById(R.id.detalle_producto);
        TxVwValor=(TextView)vistaADevolver.findViewById(R.id.valor_producto);
        TxVwCategoria=(TextView)vistaADevolver.findViewById(R.id.categoriaProducto);

        unItem = (LinearLayout)vistaADevolver.findViewById(R.id.unItemListView);

        Productos producto=new Productos();

        producto=getItem(posicion);

        TxVwNombre.setText(producto.getNombre());
        TxVwValor.setText("$"+producto.getValorMinimo()+" - "+"$"+producto.getValorMaximo());
        TxVwDetalle.setText(producto.getDetalle());
        TxVwCategoria.setText(producto.getCategorias().getNombre());

        if (!producto.getEstado()) //Si entra quiere decir que está en intercambio
        {
            unItem.setBackgroundColor(Color.rgb(244, 80, 66));
        }

        return vistaADevolver;

    }
}
