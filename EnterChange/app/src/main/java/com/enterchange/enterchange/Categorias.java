package com.enterchange.enterchange;

import java.io.Serializable;

/**
 * Created by Tute on 27/11/2016.
 */

public class Categorias implements Serializable
{
    private Integer idCategoria;
    private String Nombre;

    public Categorias()
    {

    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    @Override
    public String toString() {
        return this.Nombre;
    }
}
