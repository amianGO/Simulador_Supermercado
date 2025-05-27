package com.dgapps.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Esta es la clase Cliente que representa un Cliente de una tienda
public class Cliente {
    
    private static int contadorId = 1; //Contador estatico que sirve para asignar ids unicos a los clientes
    private int id;
    private String nombre;
    private List<Producto> carrito;
    private double totalCompra;
    private long tiempoInicioCompra;
    private long tiempoFinCompra;

    //Constructor que recibe un nombre como argumento y genera un carrito Aleatorio
    public Cliente(String nombre){
        this.id = contadorId++;
        this.nombre = nombre;
        this.carrito = new ArrayList<>();
        generarCarritoAleatorio();
        calcularTotal();
    }

    private void generarCarritoAleatorio(){ //Esta es una funcion que genera un carrito de compras aleatorio

        Random random = new Random();   //Se crea un objeto de Tipo Ramdom que se usa para generar numero aleatorios

        String[] productos = {"Pan", "Leche", "Huevos", "Arroz", "Aceite", "Azúcar", "Sal", "Pasta", "Tomate", "Cebolla"};
        double[] precios = {2.50, 3.20, 4.80, 1.80, 5.50, 2.10, 0.90, 1.60, 2.80, 1.40};
        int[] tiempos = {500, 800, 600, 400, 700, 300, 200, 450, 550, 350};

        int cantidadProductos = random.nextInt(5) + 3; //Genera un numero aleatorio entre 3 y 7 para la cantidad de productos en el carrito

        for (int i = 0; i < cantidadProductos; i++) { // Iteramos sobre la cantidad de productos

            int index = random.nextInt(productos.length); //Guardamos en un index de cada iteracion un numero ramdom dentro del rango de la lista
            carrito.add(new Producto(productos[index], precios[index], tiempos[index])); //Se añade al carrito un Producto en las posicione scorrespondientes al numero aleatorio
        }
    }

    private void calcularTotal(){   //Creamos un metodo para calcular el total de la compra
        totalCompra = carrito.stream().mapToDouble(Producto::getPrecio).sum();  //Funciona de la siguiente manera: carrito.stream() crea un flujo de datos a traves de la lista de carritos, mapToDouble mapea todos los Productos sacandoles su precio y sum() suma todos los precios obtenidos
    }


    //Creamos los getters y Setters
    public static int getContadorId() {
        return contadorId;
    }

    public static void setContadorId(int contadorId) {
        Cliente.contadorId = contadorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Producto> getCarrito() {
        return carrito;
    }

    public void setCarrito(List<Producto> carrito) {
        this.carrito = carrito;
    }

    public double getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(double totalCompra) {
        this.totalCompra = totalCompra;
    }

    public long getTiempoInicioCompra() {
        return tiempoInicioCompra;
    }

    public void setTiempoInicioCompra(long tiempoInicioCompra) {
        this.tiempoInicioCompra = tiempoInicioCompra;
    }

    public long getTiempoFinCompra() {
        return tiempoFinCompra;
    }

    public void setTiempoFinCompra(long tiempoFinCompra) {
        this.tiempoFinCompra = tiempoFinCompra;
    }

    public long getTiempoTotalCompra(){
        return tiempoFinCompra - tiempoInicioCompra;
    }

    @Override
    public String toString(){   //Esto funciona como un metodo para mostrar la informacion de manera legible
        return String.format("Cliente %d - %s (%.2f)", id, nombre, totalCompra);    //hace que el id se muestre como un numero, el nombre como una cadena de texto y el totalCompra como un numero con 2 decimales
    }
}
