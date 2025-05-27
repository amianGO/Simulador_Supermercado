package com.dgapps.entities;


public class Producto {                              // Creamos la clase Producto que representa un Producto en un Supermercado
    private String nombre;
    private double precio;
    private int tiempoProcesamiento;

    public Producto(String nombre, double precio, int tiempoProcesamiento) { // Este es el constructor que inicializa los atributos de la clase
        this.nombre = nombre;
        this.precio = precio;
        this.tiempoProcesamiento = tiempoProcesamiento;
    }

    //Creamos los getters para cada atributo de nuestra clase Producto
    public Producto() {
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getTiempoProcesamiento() {
        return tiempoProcesamiento;
    }

    // Esto funciona como un metodo para mostrar la informacion de manera legible
    @Override
    public String toString(){
        return String.format("%s - $%.2f (%.1fs)", nombre, precio, tiempoProcesamiento / 1000.0); //%s - $%.2f (%.1fs) funciona como un formato de cadena el cual hace que el nombre se muestre como una cadena, el precio se muestre como un numero con 2 decimales y el tiempoProcesamiento como un numero con 1 decimal
    }
    

    
}
