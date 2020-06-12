package com.ilerna.proyectodam.profesor;

import com.ilerna.proyectodam.persona.Persona;

/*Creamos la clase Profesor que hereda de la clase Persona, ya que comparte la mayor parte de sus atributos,
  con el fin  de obtener objetos Profesor en el nodo Profesor de nuestra BD en Firebase */
public class Profesor extends Persona {

    private String aniosExperiencia;

    public Profesor() {
        super();
    }

    public Profesor(String id, String nombre, String apellidos, String edad, String provincia,
                    String correo, String telefono, String infoAdicional, String aniosExperiencia) {
        super(id, nombre, apellidos, edad, provincia, correo, telefono, infoAdicional);

        //Añadimos el atributo "Años de experiencia", único de esta clase
        this.aniosExperiencia = aniosExperiencia;
    }

    //Creamos Getters y Setters
    public String getAniosExperiencia() {
        return aniosExperiencia;
    }

    public void setAniosExperiencia(String aniosExperiencia) {
        this.aniosExperiencia = aniosExperiencia;
    }

    //Creamos el método toString
    @Override
    public String toString() {
        return "Profesor{" +
                "aniosExperiencia='" + aniosExperiencia + '\'' +
                '}';
    }
}
