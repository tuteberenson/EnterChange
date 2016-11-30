package com.enterchange.enterchange;

import java.io.Serializable;

/**
 * Created by Tute on 21/11/2016.
 */

public class Productos implements Serializable
{
    private String Nombre, Detalle;
    private Categorias Categorias;
    private Integer ValorMinimo, ValorMaximo, IdProducto, IdUsuario;
    private Boolean Estado;

    public  Productos()
    {

    }
    public Productos(String nombre, String detalle, Boolean estado, Categorias categoria, Integer valorMinimo, Integer valorMaximo, Integer idProducto, Integer idUsuario)
    {
        this.Nombre = nombre;
        this.Detalle = detalle;
        Estado = estado;
        this.Categorias = categoria;
        this.ValorMinimo = valorMinimo;
        this.ValorMaximo = valorMaximo;
        IdProducto = idProducto;
        IdUsuario = idUsuario;
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

    public Integer getIdProducto() {
        return IdProducto;
    }

    public void setIdProducto(Integer idProducto) {
        IdProducto = idProducto;
    }

    public Categorias getCategorias() {
        return Categorias;
    }

    public void setCategorias(com.enterchange.enterchange.Categorias categorias) {
        Categorias = categorias;
    }

    public Boolean getEstado() {
        return Estado;
    }

    public void setEstado(Boolean estado) {
        Estado = estado;
    } //Si es true, esta para mostrar, si es false no está para mostrar

    public Integer getIdUsuario() {
        return IdUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        IdUsuario = idUsuario;
    }
}
