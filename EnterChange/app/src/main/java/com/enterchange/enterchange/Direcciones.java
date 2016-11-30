package com.enterchange.enterchange;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Tute on 27/11/2016.
 */

public class Direcciones implements Serializable
{
    private LatLng latLng;
    private String Direccion;

    public Direcciones(LatLng latLng, String direccion)
    {
        this.latLng = latLng;
        Direccion = direccion;
    }
    public Direcciones()
    {

    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(double lat,double lng)
    {
        LatLng latLng =new LatLng(lat,lng);

        this.latLng = latLng;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

}
