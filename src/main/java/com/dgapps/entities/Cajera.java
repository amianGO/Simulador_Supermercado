package com.dgapps.entities;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class Cajera extends Task<Void>{ //Creamos la clase Cajera que representa un Cajero de un supermercado extiende de Task para poder ejecutarlo en un hilo separado usa void como tipo de retorno ya que no devuelve nada al finalizar la ejecucion
    
    private int id;
    private String nombre;
    private BlockingQueue<Cliente> colaClientes; //Aqui se define la cola de clientes que esta atendiendo la cajera, y se usa BlockingQueue para permitir que varios hilos pueda acceder a la cola de manera segura y sincronizada
    private Consumer<String> actualizadoUI; //Este es Costumer que se utiliza para actualizar la interfaz de usuario, Costumer se utiliza para enviar mensajes a la interfaz cuando la cajera esta atendiendo a un cliente o cuando finaliza de atenderlo
    private boolean activa;
    private int clientesAtendidos;

    //Este es el constructor que inicializa los atributos de la clase cajera
    public Cajera(int id, String nombre, BlockingQueue<Cliente> colaClientes, Consumer<String> actualizadoUI) {
        this.id = id;
        this.nombre = nombre;
        this.colaClientes = colaClientes;
        this.actualizadoUI = actualizadoUI;
        this.activa = true;
        this.clientesAtendidos = 0;
    }

    protected Void call() throws Exception { //Este es el metodo call que se ejecuta cuando se inicia la tarea, y el que permite trabajar con hilos separados

        while (activa && !Thread.currentThread().isInterrupted()) { //Mientas la cajera este activa y el hilo actual no sea interrumpoido se ejecutara el siguiente bloque de codigo
            try {

                Cliente cliente = colaClientes.take(); //Se obtiene un cliente de la cola de clientes
                if (cliente == null) break; //Si no existe un cliente se detiene la aplicacion hasta que haya uno nuevo

                atenderCliente(cliente);    //llamamos a la funcion atender cliente asignandole el cliente a atender
                clientesAtendidos++;    //se le suma al contador un 1 por cliente atendido

            } catch (Exception e) {
                Thread.currentThread().interrupt(); //Si ocurre un Excepcion se interrumple el Hilo actual
                break;
            }
        }
        return null; //Retorna null ya que al finalizar la ejecucion no devulve nada
    }

    public void atenderCliente(Cliente cliente) throws InterruptedException{    //Creamos una funcion para atender clientes que recibe un objeto cliente

        cliente.setTiempoInicioCompra(System.currentTimeMillis());  //Marca el inicio de tiempo de compra del cliente

        //Se usa Platform.runlater para actualizar la interfaz del hilo actual del usuario mostrando el nombre de la cajera y el del cliente
        Platform.runLater(() -> 
            actualizadoUI.accept(String.format("Cajera %s esta atendiendo a %s", nombre, cliente.getNombre()))
        );


        for(Producto producto : cliente.getCarrito()){ //Iteramos sobre la lista de carritos del cliennte

            Thread.sleep(producto.getTiempoProcesamiento());    //Simula la espera de tiempo de procesamiento de un producto, Thread.Sleep() funciona como un alto y espere

            //Se usa Platform.runlater para actualizar la interfaz del hilo actual
            Platform.runLater(() -> 
                actualizadoUI.accept(String.format(" -> Procesando: %s", producto.toString() ))
            );
        }

        cliente.setTiempoFinCompra(System.currentTimeMillis()); // Marca el fin del tiempo de compra del cliente

        // Calcularemos la compra total, el tiempo total e imprimiremos en la interfaz el nombre del cajero, el del cliente, la compra total y el tiempo total
        Platform.runLater(() -> 
            actualizadoUI.accept(String.format(" %s termino con %s - total: $%.2f - Tiempo: %.1fs" , nombre, cliente.getNombre(), cliente.getTotalCompra(), cliente.getTiempoTotalCompra() /1000.0))
        );
    }

    public void detener(){  //Creamos un Metodo para detener la aplicacion
        this.activa = false;
        this.cancel();
    }

    //Getters de la Entidad
    public int getId() {return id;}
    public String getNombre() {return nombre;}
    public int getClientesAtendidos() {return clientesAtendidos;}
    
}
