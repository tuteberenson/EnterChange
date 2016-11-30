package com.enterchange.enterchange;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.RuntimeRemoteException;

import java.util.ArrayList;
import java.util.HashMap;


public class ActividadPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentMisDatos.InterfaseModificarNavigationDrawer{

    SessionManager session;
    SQLiteDatabase BaseDeDatos;
    public static Usuarios UsuarioActual;
    Generics generics;
    MenuItem itemEditarDatos, itemAgregarProductos, itemBuscarProductos;
    private TextView TxVwHeaderNombre, TxVwHeaderEmail;
    public static NavigationView navigationView;
    FragmentProductos fragmentProductos;
    FragmentBuscarProductos fragmentBuscarProductos;
    boolean doubleBackToExitPressedOnce = false;
    boolean existenCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UsuarioActual = new Usuarios();

        generics = new Generics(ActividadPrincipal.this);

        session = new SessionManager(ActividadPrincipal.this);

        fragmentBuscarProductos =new FragmentBuscarProductos();

        fragmentProductos = new FragmentProductos();

        if (session.checkLogin()) {

            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // name
            String password = user.get(SessionManager.KEY_PASSWORD);

            // email
            String email = user.get(SessionManager.KEY_EMAIL);

            ObtengoUsuarioActual(email);

            Notificacion();
            existenCategorias = chequerCategorias();

            if (!existenCategorias) {
                llenarCategoriasBD();
            } else {
                borrarCategorias();
            }

            String AbrirFragment="";

            try {
                AbrirFragment = getIntent().getStringExtra("Fragment");

                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                // Cancelamos la Notificacion que hemos comenzado
                nm.cancel(getIntent().getExtras().getInt("notificationID"));
            }
            catch (Exception e)
            {
                Log.d("Error",e+"");
            }

            //Asocio la vista del drawer layout que cotiene el menú desplegable
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            //Asocia ahora el navigation view que es el menú desplegable
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            //Asocia los text view que tiene el menú en la parte de arriba para despues modificarlos
            /**
             * TxVwHeaderNombre=(TextView)navigationView.getHeaderView(0).findViewById(R.id.text_header1);
             * En esta linea le asigno a la variable global TxVwHeaderNombre la vista del textview del navigation view:
             * (TextView)navigationView.getHeaderView(0) --> le digo que el textview que quiero asociar está en el navigation view anteriormente asociado
             */
            TxVwHeaderNombre = (TextView) navigationView.getHeaderView(0).findViewById(R.id.text_header1);
            TxVwHeaderEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.text_header2);

            //Una vez asociados les asigno el texto que quiero
            TxVwHeaderNombre.setText(UsuarioActual.getNombre() + " " + UsuarioActual.getApellido());
            TxVwHeaderEmail.setText(UsuarioActual.getEmail());

         if (AbrirFragment != null)
            {
                setFragmentIntercambios();
            }

            setFragmentPrincipal();
        }
    }

    private void Notificacion()
    {
        if (!LeerSolicitudes().isEmpty())
        {
            int notificationID = 1;

            Intent i = new Intent(this, ActividadPrincipal.class);
            i.putExtra("notificationID", notificationID);
            i.putExtra("Fragment","Abrir");

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            CharSequence ticker ="Tenés solicitudes de intercambio!";
            CharSequence contentTitle = "Tenés: "+LeerSolicitudes().size()+" solicitudes";
            CharSequence contentText = "Mirá las nuevas solicitudes!";
            Notification noti = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setTicker(ticker)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .addAction(R.mipmap.ic_launcher, ticker, pendingIntent)
                    .setVibrate(new long[] {100, 250, 100, 500})
                    .build();
            nm.notify(notificationID, noti);

            modificarNuevoIntercambioBD();
        }
    }

    private void modificarNuevoIntercambioBD()
    {
        BaseDeDatos =generics.AbroBaseDatos();

        ContentValues registroAModificar = new ContentValues();

        registroAModificar.put("nuevo",0);

        if (BaseDeDatos!=null)
        {
            BaseDeDatos.update("intercambios",registroAModificar,"idusuarioduenio="+UsuarioActual.getIdUsuario(),null);
        }
    }


    private void borrarCategorias()
    {
        BaseDeDatos = generics.AbroBaseDatos();

        if (BaseDeDatos!=null)
        {
            BaseDeDatos.delete("categorias","",null);
        }

        llenarCategoriasBD();
    }

    private boolean chequerCategorias()
    {
        BaseDeDatos = generics.AbroBaseDatos();

        Cursor ResultadoCategorias;

        ResultadoCategorias = BaseDeDatos.rawQuery("select idcategoria, nombre from categorias", null);

        if (BaseDeDatos != null) {
            if (ResultadoCategorias.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    private void ObtengoUsuarioActual(String email)
    {
        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select idusuario, nombre, apellido, username, email, password, direccion, telefono,latitud,longitud from usuarios where email ='"+ email+"'",null);

        if (BaseDeDatos!=null)
        {
            if (ConjuntoDeRegistros.moveToFirst())
            {
                do {
                    Direcciones direccion =new Direcciones();

                    direccion.setDireccion(ConjuntoDeRegistros.getString(6));
                    direccion.setLatLng((double) ConjuntoDeRegistros.getInt(7),(double)ConjuntoDeRegistros.getInt(8));

                    UsuarioActual.setIdUsuario(ConjuntoDeRegistros.getInt(0));
                    UsuarioActual.setNombre(ConjuntoDeRegistros.getString(1));
                    UsuarioActual.setApellido(ConjuntoDeRegistros.getString(2));
                    UsuarioActual.setUsername(ConjuntoDeRegistros.getString(3));
                    UsuarioActual.setEmail(ConjuntoDeRegistros.getString(4));
                    UsuarioActual.setPassword(ConjuntoDeRegistros.getString(5));
                    UsuarioActual.setDireccion(direccion);
                    UsuarioActual.setTelefono(ConjuntoDeRegistros.getInt(7));
                }while (ConjuntoDeRegistros.moveToNext());
            }
        }
    }


    @Override
    public void onBackPressed() {
        //Entra a este método cuando el usuario toca la flecha del celular para volver para atrás
        //Primero Asocio nuevamente el menú esplegable
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Pregunto si el menú está desplegado, en el caso de que esté abierto se cierra
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        //Si el menú está cerrado pregunta si una variable global declarada como false, está en false
         else if (!doubleBackToExitPressedOnce) {
            //Está como false, la cambio a true
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,"Presione nuevamente para salir", Toast.LENGTH_SHORT).show();
            //Si quiere salir de la aplicacion deberá tocar el boton de atrás 2 veces continuas
            //Comienzo un timer que si pasan dos segundos vuelve la variable a false
            //Esto lo que hace es que si tocaste una vez para atrás la variable global va a pasar a false y si volviste atocar
            //Antes de los 2 segundos, no va a entrar acá y vaa a ejecutar el super.onBackPressed(); que sale de la app
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
           finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //Asocio los iconos que aparecen arriba a la derecha y  están declarados como globales para después poder modificarlos, ocultandolos por ejemplo.
        getMenuInflater().inflate(R.menu.actividad_principal, menu);
        itemEditarDatos = menu.findItem(R.id.action_editar_datos);
        itemAgregarProductos = menu.findItem(R.id.action_agregar_producto);
        itemBuscarProductos = menu.findItem(R.id.action_buscar_producto);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Llega a esta función un parametro que es el item que tocó, esta sirve para manejar las acciones de estos iconos
        //obtiene el id del icono (Toda vista en android tiene un id por el que se asocia)

        //Para manejar las acciones pregunto cual id coincide con el id que me llegó
        if (id == R.id.action_agregar_producto)
        {
            if (fragmentProductos.getCountProductos()<10) {
                fragmentProductos.agregarProductoMenuItem();
            }else {
                Toast.makeText(this, "No se pueden agregar más productos", Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.action_buscar_producto)
        {
            Dialog dialog = fragmentBuscarProductos.onCreateDialogTipoDeBusqueda();
            dialog.show();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        setDefaultFragment();

        //Esta funcion funciona similar al onOptionsItemSelected pero en vez de manejar los iconos de arriba a la derecha
        //Maneja los íconos del menú


        if (id == R.id.nav_productos) {
            //Muestro un fragment
          setFragmentProductos();

        } else if (id == R.id.nav_lotes) {
            itemBuscarProductos.setVisible(false);
            itemAgregarProductos.setVisible(false);

        } else if (id == R.id.nav_intercambios) {
            setFragmentIntercambios();
            itemAgregarProductos.setVisible(false);
            itemBuscarProductos.setVisible(false);

        } else if (id == R.id.nav_donaciones) {
            itemBuscarProductos.setVisible(false);
            itemAgregarProductos.setVisible(false);
        }
        else if (id == R.id.nav_buscar)
        {
         setFragmentBuscarProductos();
        }
        else if (id == R.id.nav_datos) {
            setFragmentDatos();
        } else if (id == R.id.nav_about_me) {
            itemBuscarProductos.setVisible(false);
            itemAgregarProductos.setVisible(false);
        }
        else if (id == R.id.nav_settings) {
            itemBuscarProductos.setVisible(false);
            itemAgregarProductos.setVisible(false);
        }
        else if (id == R.id.nav_salir) {
            session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void ModificarNavigationDrawer()
    {
        String NombreApellido = UsuarioActual.getNombre() + " "+UsuarioActual.getApellido();
        TxVwHeaderNombre.setText(NombreApellido);
    }

    public void setFragmentBuscarProductos()
    {
        itemBuscarProductos.setVisible(true);
        itemAgregarProductos.setVisible(false);
       fragmentBuscarProductos =new FragmentBuscarProductos();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_actividad_principal, fragmentBuscarProductos)
                .commit();
    }

    public void setFragmentProductos()
    {
        //Oculto o muestro los iconos de arriba a la derecha que quiro que parezcan en el fragmebnt
        itemAgregarProductos.setVisible(true);
        itemBuscarProductos.setVisible(false);

        //Instancio el fragment que quiero mostrar
        fragmentProductos = new FragmentProductos();
        //Obtengo el manejador de fragments
        FragmentManager fragmentManager = getFragmentManager();
        //Reemplazo el layout del fragment por el del contenedor de la activity principal
        fragmentManager.beginTransaction().replace(R.id.content_actividad_principal, fragmentProductos)
                .commit();
    }

    public void setFragmentDatos()
    {
        FragmentMisDatos fragmentMisDatos = new FragmentMisDatos();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_actividad_principal, fragmentMisDatos)
                .commit();
        fragmentMisDatos.setInterfase(this);
        itemAgregarProductos.setVisible(false);
        itemBuscarProductos.setVisible(false);
    }
    public void setFragmentIntercambios()
    {
        FragmentIntercambios fragmentIntercambios = new FragmentIntercambios();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_actividad_principal, fragmentIntercambios)
                .commit();

    }

    public void setDefaultFragment()
    {
        Fragment defaultFragment = new FragmentEnConstruccion();
        FragmentManager fragmentManagerDefault = getFragmentManager();
        fragmentManagerDefault.beginTransaction().replace(R.id.content_actividad_principal, defaultFragment)
                .commit();
    }

    public void llenarCategoriasBD()
    {
        BaseDeDatos =generics.AbroBaseDatos();

        ContentValues NuevaCategoria = new ContentValues();
        NuevaCategoria.put("nombre","Jardín");
        NuevaCategoria.put("idcategoria",1);
        BaseDeDatos.insert("categorias",null,NuevaCategoria);

        NuevaCategoria = new ContentValues();
        NuevaCategoria.put("nombre","Bazar y Hogar");
        NuevaCategoria.put("idcategoria",2);
        BaseDeDatos.insert("categorias",null,NuevaCategoria);

        NuevaCategoria = new ContentValues();
        NuevaCategoria.put("nombre","Iluminación");
        NuevaCategoria.put("idcategoria",3);
        BaseDeDatos.insert("categorias",null,NuevaCategoria);

        NuevaCategoria = new ContentValues();
        NuevaCategoria.put("nombre","Electrodomésticos");
        NuevaCategoria.put("idcategoria",4);
        BaseDeDatos.insert("categorias",null,NuevaCategoria);

        NuevaCategoria = new ContentValues();
        NuevaCategoria.put("nombre","Ferreteria");
        NuevaCategoria.put("idcategoria",5);
        BaseDeDatos.insert("categorias",null,NuevaCategoria);

        NuevaCategoria = new ContentValues();
        NuevaCategoria.put("nombre","Plomería");
        NuevaCategoria.put("idcategoria",6);
        BaseDeDatos.insert("categorias",null,NuevaCategoria);

        NuevaCategoria = new ContentValues();
        NuevaCategoria.put("nombre","Baños y Cocinas");
        NuevaCategoria.put("idcategoria",7);
        BaseDeDatos.insert("categorias",null,NuevaCategoria);

        BaseDeDatos.close();
    }

    private void setFragmentPrincipal()
    {
        FragmentListaProductos fragmentListaProductos = new FragmentListaProductos();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_actividad_principal, fragmentListaProductos)
                .commit();
    }
    public ArrayList<Intercambios> LeerSolicitudes()
    {
        BaseDeDatos = generics.AbroBaseDatos();

        ArrayList<Intercambios> listaIntercambios = new ArrayList<>();

        Intercambios unIntercambio;

        Cursor conjuntoDeIntercambios;
        String ConsultaSql ="SELECT idintercambio,idusuarioduenio, idusuariointeresado ,idproductoduenio, idproductointeresado, estado, nuevo FROM intercambios WHERE idusuarioduenio = "+ UsuarioActual.getIdUsuario()+ " AND nuevo = 1";

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
