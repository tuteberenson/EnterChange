package com.enterchange.enterchange;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.enterchange.enterchange.ActividadPrincipal.UsuarioActual;
import static com.enterchange.enterchange.FragmentListaProductos.adapterListProductos;
import static com.enterchange.enterchange.FragmentListaProductos.listViewProductos;
import static com.enterchange.enterchange.FragmentListaProductos.myRemove;
import static com.enterchange.enterchange.FragmentListaProductos.productosMostrados;
import static com.enterchange.enterchange.FragmentListaProductos.updateReceiptsList;

public class ActivityDetalleProducto extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView TxVwNombreProducto, TxVwCategoria, TxVwRango, TxVwDetalle;
    Generics generics;
    SQLiteDatabase BaseDeDatos;
    private Button btn_Intercambiar;
    Productos productoRecibido;
    String ProductoSeleccionadoParaIntercambio;
    Integer IdProductoSeleccionadoParaIntercambio, PosicionProductoEnLista; //IdProductoSeleccionado para intercambio es el id del producto que ofrece el usuario actual
    boolean IntercambioExitoso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_detalle_producto);
        mapFragment.getMapAsync(this);

        AsociarVistas();

        generics = new Generics(ActivityDetalleProducto.this);

        productoRecibido =new Productos();

        productoRecibido = (Productos)getIntent().getSerializableExtra("Producto");
        PosicionProductoEnLista = getIntent().getIntExtra("PosicionProducto",-1);

        setVistasProducto(productoRecibido);

        btn_Intercambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialogoElejirProducto = onCreateDialogSeleccionarProducto();
                if (dialogoElejirProducto == null)
                {
                    Toast.makeText(ActivityDetalleProducto.this, "No tiene productos para intercambiar", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    dialogoElejirProducto.show();
                }
            }
        });
    }

    private void setVistasProducto(Productos producto)
    {
        TxVwNombreProducto.setText(producto.getNombre());
        TxVwRango.setText("$"+producto.getValorMinimo()+" - $"+producto.getValorMaximo());
        TxVwDetalle.setText(producto.getDetalle());
        TxVwCategoria.setText(getCategoriaById(producto.getCategorias().getIdCategoria()));
    }

    private String getCategoriaById(Integer id) {
        String resultado = "";

        BaseDeDatos = generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select nombre from categorias where idcategoria =" + id, null);

        if (BaseDeDatos != null) {
            if (ConjuntoDeRegistros.moveToFirst()) {
                        resultado=ConjuntoDeRegistros.getString(0);
            }
        }
        return resultado;
    }


    private void AsociarVistas()
    {
        TxVwNombreProducto = (TextView) findViewById(R.id.act_nombre_producto);
        TxVwCategoria = (TextView)findViewById(R.id.act_categoria_producto);
        TxVwDetalle = (TextView) findViewById(R.id.act_detalle_producto);
        TxVwRango = (TextView)findViewById(R.id.act_rango_producto);

        btn_Intercambiar = (Button)findViewById(R.id.act_btn_intercambiar_producto);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(false); // Habilita +/- para hacer zoom
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);    // Selecciona tipo de mapa satelital

        Direcciones direccionUsuarioProducto =new Direcciones();

        direccionUsuarioProducto = traerDireccionUsuarioById(productoRecibido.getIdUsuario());

        if (mMap!=null)
        {
            CameraUpdate posUsuario = CameraUpdateFactory.newLatLngZoom(new LatLng(
                    direccionUsuarioProducto.getLatLng().latitude,
                    direccionUsuarioProducto.getLatLng().longitude), 15);
            // CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
            mMap.moveCamera(posUsuario);

            addMark(direccionUsuarioProducto.getLatLng(),"Usuario",false);
           // LatLng sydney = new LatLng(-34, 151);
            Log.d("UbicacionUserActual","Latitud: "+UsuarioActual.getDireccion().getLatLng().latitude+" - Longitud: "+ UsuarioActual.getDireccion().getLatLng().longitude);

            addMark(UsuarioActual.getDireccion().getLatLng(),"Acá estoy yo",true);

        }
    }
    public void addMark(LatLng latLng, String titulo, boolean usuarioActual)
    {
        if (usuarioActual) {
            MarkerOptions mo = new MarkerOptions()
                    .position(latLng)
                    .title(titulo)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.usermarker));
            mMap.addMarker(mo);
        }
        else {
            MarkerOptions mo = new MarkerOptions()
                    .position(latLng)
                    .title(titulo);
            mMap.addMarker(mo);
        }

    }

    public Direcciones traerDireccionUsuarioById(int id)
    {
        Direcciones DireccionUnUsuario =new Direcciones();

        Cursor UsuarioTraido;

        BaseDeDatos = generics.AbroBaseDatos();

        UsuarioTraido = BaseDeDatos.rawQuery("select idusuario, nombre , apellido , username , email  , direccion , telefono , latitud , longitud  from usuarios where idusuario =" + id, null);

        if (BaseDeDatos != null)
        {
            if (UsuarioTraido.moveToFirst())
            {
                DireccionUnUsuario.setDireccion(UsuarioTraido.getString(5));
                DireccionUnUsuario.setLatLng(Double.valueOf(UsuarioTraido.getString(7)),Double.valueOf(UsuarioTraido.getString(8)));
            }
        }

        return DireccionUnUsuario;
    }

    public Dialog onCreateDialogSeleccionarProducto() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetalleProducto.this);

        ArrayList<Productos> productosUserInteresado = new ArrayList<>();

        productosUserInteresado.addAll(LeerProductos());

        if (productosUserInteresado.isEmpty()) {
            return null;
        }
        else
        {
            final String[] array = new String[productosUserInteresado.size()];
            final Integer[] arrayIds = new Integer[productosUserInteresado.size()];

            int index = 0;

            for (Productos value : productosUserInteresado) {
                array[index] = value.getNombre();
                arrayIds[index] = value.getIdProducto();
                index++;
            }

            final List<String> optionsList = Arrays.asList(array);

            ProductoSeleccionadoParaIntercambio = optionsList.get(0);
            IdProductoSeleccionadoParaIntercambio = arrayIds[0];
            builder.setTitle("Elija un producto: ")

                    .setSingleChoiceItems(array, 0, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ProductoSeleccionadoParaIntercambio = optionsList.get(which);
                            IdProductoSeleccionadoParaIntercambio = arrayIds[which];
                            Log.d("idProductoUserActual",IdProductoSeleccionadoParaIntercambio+"");

                            Log.d("currentItem", ProductoSeleccionadoParaIntercambio);
                        }
                    })

                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                           DialogConfirmarIntercambio();
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
    public void DialogConfirmarIntercambio()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetalleProducto.this);

        builder.setTitle("Confirmación")
                .setMessage("¿Cambiar: "+ProductoSeleccionadoParaIntercambio + " x "+productoRecibido.getNombre()+"?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realizarIntercambioBD();
                        Toast.makeText(ActivityDetalleProducto.this, "Intercambio confirmado", Toast.LENGTH_SHORT).show();
                        IntercambioExitoso =true;
                        btn_Intercambiar.setEnabled(false);
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (IntercambioExitoso)
        {
            myRemove(PosicionProductoEnLista);
        }
        super.onBackPressed();
    }

    private void realizarIntercambioBD()
    {
        Log.d("idProductoUserActual",IdProductoSeleccionadoParaIntercambio+"");

        BaseDeDatos = generics.AbroBaseDatos();

        Integer ultimoId;
        ultimoId =  obtenerUltimoId();
        ultimoId++;

        ContentValues NuevoIntercambio = new ContentValues(), modificarEstadoProducto= new ContentValues();

        NuevoIntercambio.put("idintercambio",ultimoId);
        NuevoIntercambio.put("idusuarioduenio",productoRecibido.getIdUsuario());
        NuevoIntercambio.put("idusuariointeresado", UsuarioActual.getIdUsuario());
        NuevoIntercambio.put("idproductoduenio", productoRecibido.getIdProducto());
        NuevoIntercambio.put("idproductointeresado",IdProductoSeleccionadoParaIntercambio);
        NuevoIntercambio.put("estado",1); //Estado 1 es pendiente , 0 Rechazado y 2 aceptado
        NuevoIntercambio.put("nuevo", 1);

        BaseDeDatos.insert("intercambios",null,NuevoIntercambio);

        modificarEstadoProducto.put("estado",0);

        if (BaseDeDatos!=null)
        {
            BaseDeDatos.update("productos",modificarEstadoProducto,"idproducto = "+IdProductoSeleccionadoParaIntercambio+" AND idusuario = "+UsuarioActual.getIdUsuario(),null);
            BaseDeDatos.update("productos",modificarEstadoProducto,"idproducto = "+productoRecibido.getIdProducto()+" AND idusuario = "+productoRecibido.getIdUsuario(),null);
        }

        BaseDeDatos.close();

    }
    private Integer obtenerUltimoId()
    {
        Integer ultimoId = 0;

        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select idintercambio from intercambios",null);

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
                " WHERE p.idusuario ="+UsuarioActual.getIdUsuario()+ " AND estado = 1";

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
}
