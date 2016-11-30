package com.enterchange.enterchange;

/**
 * Created by Tute on 29/11/2016.
 */

public class Intercambios
{
    private Integer idIntercambio, idUsuarioD, idUsuarioI, idProductoD, idProductoI,Estado; // -D: Dueño - I: Interesado
    private Boolean Nuevo; //Nuevo es para saber si para el usuario que promociona el producto es nuevo o no

    public Intercambios()
    {

    }

    public Intercambios(Integer idIntercambio, Integer idUsuarioD, Integer idUsuarioI, Integer idProductoD, Integer idProductoI, Integer estado, Boolean nuevo) {
        this.idIntercambio = idIntercambio;
        this.idUsuarioD = idUsuarioD;
        this.idUsuarioI = idUsuarioI;
        this.idProductoD = idProductoD;
        this.idProductoI = idProductoI;
        Estado = estado;
        Nuevo = nuevo;
    }

    public Integer getIdIntercambio() {
        return idIntercambio;
    }

    public void setIdIntercambio(Integer idIntercambio) {
        this.idIntercambio = idIntercambio;
    }

    public Integer getIdUsuarioD() {
        return idUsuarioD;
    }

    public void setIdUsuarioD(Integer idUsuarioD) {
        this.idUsuarioD = idUsuarioD;
    }

    public Integer getIdUsuarioI() {
        return idUsuarioI;
    }

    public void setIdUsuarioI(Integer idUsuarioI) {
        this.idUsuarioI = idUsuarioI;
    }

    public Integer getIdProductoD() {
        return idProductoD;
    }

    public void setIdProductoD(Integer idProductoD) {
        this.idProductoD = idProductoD;
    }

    public Integer getIdProductoI() {
        return idProductoI;
    }

    public void setIdProductoI(Integer idProductoI) {
        this.idProductoI = idProductoI;
    }

    public Integer getEstado() {
        return Estado;
    }

    public void setEstado(Integer estado) {
        Estado = estado;
    }

    public boolean isNuevo() {
        return Nuevo;
    }

    public void setNuevo(Boolean nuevo) {
        Nuevo = nuevo;
    }

}
