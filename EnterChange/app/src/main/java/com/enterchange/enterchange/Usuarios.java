package com.enterchange.enterchange;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Tute on 19/11/2016.
 */

public class Usuarios
{
    private Integer idUsuario;
    private String Nombre, Apellido, Username, Email, Password;
    private Direcciones Direccion;
    private Integer Telefono;

    public Usuarios()
    {

    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public Integer getTelefono() {
        return Telefono;
    }

    public void setTelefono(Integer telefono) {
        Telefono = telefono;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Direcciones getDireccion() {
        return Direccion;
    }

    public void setDireccion(Direcciones direccion) {
        Direccion = direccion;
    }
}
