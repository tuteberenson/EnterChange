package com.enterchange.enterchange;

/**
 * Created by Tute on 21/11/2016.
 */

public class Productos
{
    private String Nombre, Detalle, Categoria;
    private Integer ValorMinimo, ValorMaximo;

    public  Productos()
    {

    }
    public Productos(String nombre, String detalle, String categoria, Integer valorMinimo, Integer valorMaximo)
    {
        this.Nombre = nombre;
        this.Detalle = detalle;
        this.Categoria = categoria;
        this.ValorMinimo = valorMinimo;
        this.ValorMaximo = valorMaximo;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDetalle() {
        return Detalle;
    }

    public void setDetalle(String detalle) {
        Detalle = detalle;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public Integer getValorMinimo() {
        return ValorMinimo;
    }

    public void setValorMinimo(Integer valorMinimo) {
        ValorMinimo = valorMinimo;
    }
    public Integer getValorMaximo() {
        return ValorMaximo;
    }

    public void setValorMaximo(Integer valorMaximo) {
        ValorMaximo = valorMaximo;
    }
}
