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
import android.widget.TextView;

public class ActividadPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SQLiteDatabase BaseDeDatos;
    public static Usuarios UsuarioActual;
    Generics generics;
    MenuItem itemEditarDatos;
    private TextView TxVwHeaderNombre, TxVwHeaderEmail;
    public static NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UsuarioActual = new Usuarios();

        generics = new Generics(ActividadPrincipal.this);

        ObtengoUsuarioActual();

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

    private void ObtengoUsuarioActual()
    {
        BaseDeDatos =generics.AbroBaseDatos();

        Cursor ConjuntoDeRegistros;

        ConjuntoDeRegistros = BaseDeDatos.rawQuery("select idusuario, nombre, apellido, username, email, password, direccion, telefono from usuarios where iniciado = 1",null);

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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actividad_principal, menu);
        itemEditarDatos = menu.findItem(R.id.action_editar_datos);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_editar_datos)
        {

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            fragment = new FragmentMisDatos();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_actividad_principal, fragment).commit();


        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
