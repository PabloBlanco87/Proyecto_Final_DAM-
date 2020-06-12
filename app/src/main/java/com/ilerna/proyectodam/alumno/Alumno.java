package com.ilerna.proyectodam.alumno;

import com.ilerna.proyectodam.persona.Persona;

/*Creamos la clase Alumno que hereda de la clase Persona, ya que comparte la mayor parte de sus atributos,
  con el fin  de obtener objetos Alumno en el nodo Alumno de nuestra BD en Firebase */
public class Alumno extends Persona {

    private String estudiosTerminados;

    public Alumno() {
        super();
    }

    public Alumno(String nombre) {
        super(nombre);
    }

    public Alumno(String nombre, String apellido, String id) {
        super(nombre, apellido, id);
    }

    public Alumno(String id, String nombre, String apellidos, String edad, String lugar, String correo,
                  String telefono, String infoAdicional, String estudiosTerminados) {
        super(id, nombre, apellidos, edad, lugar, correo, telefono, infoAdicional);

        //Añadimos el atributo "Estudios terminados", único de esta clase
        this.estudiosTerminados = estudiosTerminados;

    }

    //Creamos Getters y Setters
    public String getEstudiosTerminados() {
        return estudiosTerminados;
    }

    public void setEstudiosTerminados(String estudiosTerminados) {
        this.estudiosTerminados = estudiosTerminados;
    }

    //Creamos el método toString
    @Override
    public String toString() {
        return getNombre() + " " + getApellidos();
    }

}
