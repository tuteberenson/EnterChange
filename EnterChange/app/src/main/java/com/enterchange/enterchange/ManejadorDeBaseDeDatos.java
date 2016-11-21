package com.enterchange.enterchange;

import android.content.Context;
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

        String sqlCrearTablaUsuarios;

        sqlCrearTablaUsuarios="create table usuarios (idusuario integer autoincremental, nombre text, apellido text, username text, email text, password text, direccion text, telefono integer, iniciado integer)";
        baseDeDatos.execSQL(sqlCrearTablaUsuarios);
    }
    @Override
    public void onUpgrade(SQLiteDatabase baseDeDatos, int versionVieja, int versionNueva) {

    }
}
