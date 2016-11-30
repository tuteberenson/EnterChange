package com.enterchange.enterchange;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tute on 19/11/2016.
 */

public class ManejadorDeBaseDeDatos extends SQLiteOpenHelper
{
    public ManejadorDeBaseDeDatos(Context contexto, String nombre, SQLiteDatabase.CursorFactory fabrica, int version) {
        super(contexto, nombre, fabrica, version);
    }

    @Override
    public void onCreate(SQLiteDatabase baseDeDatos) {

        String sqlCrearTablaUsuarios, sqlCrearTablaProductos, sqlCrearTablaCategorias, sqlCrearTablaIntercambios;

        sqlCrearTablaUsuarios="create table usuarios (idusuario integer autoincremental, nombre text, apellido text, username text, email text, password text, direccion text, telefono integer, latitud integer, longitud integer)";
        baseDeDatos.execSQL(sqlCrearTablaUsuarios);

        sqlCrearTablaProductos="create table productos (idproducto integer autoincremental,idusuario integer ,nombre text, detalle text, valormin integer, valormax integer, idcategoria integer, estado integer)";
        baseDeDatos.execSQL(sqlCrearTablaProductos);

        sqlCrearTablaCategorias="create table categorias (idcategoria integer autoincremental,nombre text)";
        baseDeDatos.execSQL(sqlCrearTablaCategorias);

        sqlCrearTablaIntercambios="create table intercambios (idintercambio integer autoincremental,idusuarioduenio integer, idusuariointeresado integer, idproductoduenio integer, idproductointeresado integer, estado integer, nuevo integer)";
        baseDeDatos.execSQL(sqlCrearTablaIntercambios);
        //Estado 1 es pendiente , 0 Rechazado y 2 aceptado

    }
    @Override
    public void onUpgrade(SQLiteDatabase baseDeDatos, int versionVieja, int versionNueva) {
        String sql = "DROP TABLE IF EXISTS usuarios, productos, categorias, intercambios";
        baseDeDatos.execSQL(sql);

        onCreate(baseDeDatos);
    }

}
