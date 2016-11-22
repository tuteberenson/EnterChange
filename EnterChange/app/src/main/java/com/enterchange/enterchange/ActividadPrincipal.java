package com.enterchange.enterchange;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;


public class ActividadPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentMisDatos.InterfaseModificarNavigationDrawer{

    SessionManager session;
    SQLiteDatabase BaseDeDatos;
    public static Usuarios UsuarioActual;
    Generics generics;
    MenuItem itemEditarDatos, itemAgregarProductos;
    private TextView TxVwHeaderNombre, TxVwHeaderEmail;
    public static NavigationView navigationView;
    FragmentProductos fragmentProductos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UsuarioActual = new Usuarios();

        generics = new Generics(ActividadPrincipal.this);

        session = new SessionManager(ActividadPrincipal.this);


        fragmentProductos = new FragmentProductos();

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String password = user.get(SessionManager.KEY_PASSWORD);

        // email
        String email = user.get(SessionManager.KEY_EMAIL);

        ObtengoUsuarioActual(email);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TxVwHeaderNombre=(TextView)navigationView.getHeaderView(0).findViewById(R.id.text_header1);
        TxVwHeaderEmail=(TextView)navigationView.getHeaderView(0).findViewById(R.id.text_header2);

        TxVwHeaderNombre.setText(UsuarioActual.getNombre()+" "+UsuarioActual.getApellido());
        TxVwHeaderEmail.setText(UsuarioActual.getEmail());
    }

    private void ObtengoUsuarioActual(String email)
    {
        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select idusuario, nombre, apellido, username, email, password, direccion, telefono from usuarios where email ='"+ email+"'",null);

        if (BaseDeDatos!=null)
        {
            if (ConjuntoDeRegistros.moveToFirst())
            {
                do {
                    UsuarioActual.setIdUsuario(ConjuntoDeRegistros.getInt(0));
                    UsuarioActual.setNombre(ConjuntoDeRegistros.getString(1));
                    UsuarioActual.setApellido(ConjuntoDeRegistros.getString(2));
                    UsuarioActual.setUsername(ConjuntoDeRegistros.getString(3));
                    UsuarioActual.setEmail(ConjuntoDeRegistros.getString(4));
                    UsuarioActual.setPassword(ConjuntoDeRegistros.getString(5));
                    UsuarioActual.setDireccion(ConjuntoDeRegistros.getString(6));
                    UsuarioActual.setTelefono(ConjuntoDeRegistros.getInt(7));
                }while (ConjuntoDeRegistros.moveToNext());
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actividad_principal, menu);
        itemEditarDatos = menu.findItem(R.id.action_editar_datos);
        itemAgregarProductos = menu.findItem(R.id.action_agregar_producto);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_agregar_producto)
        {
            fragmentProductos.agregarProductoMenuItem();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_productos) {
            
            fragmentProductos = new FragmentProductos();
            itemAgregarProductos.setVisible(true);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_actividad_principal, fragmentProductos).commit();

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_datos) {
            fragment = new FragmentMisDatos();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_actividad_principal, fragment)
                    .commit();


        } else if (id == R.id.nav_about_me) {

        }
        else if (id == R.id.nav_salir)
        {
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
}
