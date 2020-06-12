package com.ilerna.proyectodam.persona;

//Super clase de Alumno y Profesor
public class Persona {

    private String id, nombre, apellidos, edad, lugar, correo, telefono, infoAdicional;

    //Creamos contructor vacío y con todos sus atributos
    public Persona() {
    }

    public Persona(String nombre) {
        this.nombre = nombre;
    }

    public Persona(String nombre, String apellidos, String id) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.id = id;
    }

    public Persona(String id, String nombre, String apellidos, String edad, String lugar, String correo, String telefono, String infoAdicional) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
        this.lugar = lugar;
        this.correo = correo;
        this.telefono = telefono;
        this.infoAdicional = infoAdicional;
    }

    //Creamos Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getInfoAdicional() {
        return infoAdicional;
    }

    public void setInfoAdicional(String infoAdicional) {
        this.infoAdicional = infoAdicional;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    //Creamos método toString
    @Override
    public String toString() {
        return "Persona{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", lugar='" + lugar + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                ", infoAdicional='" + infoAdicional + '\'' +
                ", edad=" + edad +
                '}';
    }
}
