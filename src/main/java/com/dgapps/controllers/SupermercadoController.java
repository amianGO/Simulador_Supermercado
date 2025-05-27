package com.dgapps.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.dgapps.entities.Cajera;
import com.dgapps.entities.Cliente;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class SupermercadoController {   //Esta es la clase controladora que maneja la logica de la aplicacion y la interfaz del Supermercado
    
    //Creamos los campos en variables donde se asignaran los campos
    @FXML private Spinner<Integer> spinnerCajeras;
    @FXML private Spinner<Integer> spinnerClientes;
    @FXML private Button btnIniciar;
    @FXML private Button btnDetener;
    @FXML private TextArea areaLog;
    @FXML private VBox panelEstadisticas;
    @FXML private Label lblTiempoTotal;
    @FXML private Label lblClientesTotal;
    @FXML private Label lblPromedioAtencion;

    private List<Cajera> cajeras;
    private List<Thread> hilosCajeras;
    private BlockingQueue<Cliente> colaClientes;    //Permite la fila de forma segura y sincronizada
    private long tiempoInicioSimulacion;
    private int totalClientes;
    private List<Cliente> clientesAtendidos;

    @FXML
    private void initialize() { //Este metodo se ejecuta al iniciar la aplicacion y se encarga de inicializar los componenentes de la interfaz
        spinnerCajeras.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,5,2));  //Impone el valor minimo, maximo y el inicial
        spinnerClientes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 15));  //Impone el valor minimo, maximo y el inicial

        btnDetener.setDisable(true);    //Desabilita el Boton de Detener al iniciar la aplicacion
        cajeras = new ArrayList<>();
        hilosCajeras = new ArrayList<>();
        clientesAtendidos = new ArrayList<>();
    }

    @FXML
    private void iniciarSimulacion(){   //Este metodo se ejecuta al hacer click en el boton de iniciar Simulacion en la interfaz del Supermercado
        
        limpiarSimulacion();

        int numCajeras = spinnerCajeras.getValue();
        int numClientes = spinnerClientes.getValue();
        totalClientes = numClientes;

        colaClientes = new LinkedBlockingQueue<>(); //LinkedBlockingQueue es una implementacion de BlockingQueue que permite una cola de clientes segura y sincronizada

        //Crear Clientes
        String[] nombresClientes = {"Ana", "Carlos", "María", "José", "Laura", "Pedro", "Carmen", "Luis", "Isabel", "Diego",
                                  "Patricia", "Miguel", "Rosa", "Antonio", "Silvia", "Fernando", "Gloria", "Raúl", "Mónica", "Javier"};
        Random random = new Random();

        //Este bucle se encarga de crear los clientes y añadirlos a la cola de clientes
            for (int j = 0; j < numClientes; j++) {
                String nombre = nombresClientes[random.nextInt(nombresClientes.length)];
                Cliente cliente = new Cliente(nombre + " " + (j + 1));
                colaClientes.offer(cliente);    //Añade el cliente a la cola de clientes
            }        

        //Crear Cajeras
        String[] nombresCajeras = {"Elena", "Sofía", "Andrea", "Lucía", "Valeria"};

        for (int i = 0; i < numCajeras; i++) {
            Cajera cajera = new Cajera(i + 1, nombresCajeras[i], colaClientes, this::actualizarLog);
            cajeras.add(cajera);

            Thread hilo = new Thread(cajera);   //Crea un nuevo hilo para cada cajera
            hilosCajeras.add(hilo);     //Añade el hilo a lista
            hilo.start();   
        }

        tiempoInicioSimulacion = System.currentTimeMillis();

        btnIniciar.setDisable(true);
        btnDetener.setDisable(false);
        spinnerCajeras.setDisable(true);
        spinnerClientes.setDisable(true);

        actualizarLog("Simulacion Iniciada con Exito con " + numCajeras + " cajeras y " + numClientes + " clientes.");

        new Thread(this::monitorearFinalizacion).start();
    }


    private void actualizarLog(String message){ //Este metodo se encarga de enviar un mensaje a la interfaz con el mensaje que se le pase como parametro
        Platform.runLater(() -> {
            areaLog.appendText(message + "\n");
            areaLog.setScrollTop(Double.MAX_VALUE);
        });
    }

    @FXML
    private void detenerSimulacion(){   //Este metodo se ejecuta al hacer click en el boton de detener en la interfaz del supermercado

        for(Cajera cajera : cajeras){   //Itera sobre cada cajer y las detiene
            cajera.detener();
        }

        for(Thread hilo : hilosCajeras){    //Itera sobre cada hilo de las cajeras y los detiene
            hilo.interrupt();
        }

        finalizarSimulacion();
    }

    private void monitorearFinalizacion(){  //Este metodo se encarga de monitorear la finalizacion de la simulacion

        while (!colaClientes.isEmpty() && !Thread.currentThread().isInterrupted()) {    //Mientras la cola de clientes no este vacia y el hilo actual no sea interrumpido se ejecutara el siguiente bloque de codigo
            try {
                Thread.sleep(1000); //Espera un segundo antes de volver a verificar si hay clientes en la cola
            } catch (InterruptedException e) {
                break;
            }
        }

        //Esperar a que las cajeras terminen con sus clientes actuales
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            
        }
        //Si la cola de clientes es vacia se finaliza la simulacion
        Platform.runLater(this::finalizarSimulacion);
    }


    private void finalizarSimulacion(){

        long tiempoTotal = System.currentTimeMillis() - tiempoInicioSimulacion;

        //Detener todas las cajeras
        for(Cajera cajera : cajeras){
            cajera.detener();
        }

        //Interrumpir los hilos
        for(Thread hilo : hilosCajeras){
            hilo.interrupt();
        }

        actualizarLog("Simulacion Finalizada");
        mostrarEstadisticas(tiempoTotal);

        btnIniciar.setDisable(false);
        btnDetener.setDisable(true);
        spinnerCajeras.setDisable(false);
        spinnerClientes.setDisable(false);
    }

    private void mostrarEstadisticas(long tiempoTotal) {    //Este metodo se encarga de mostrar las estadisticas obtenidas mediante la simulacion

        int clientesAtendidosTotal = cajeras.stream().mapToInt(Cajera :: getClientesAtendidos).sum();

        double promedioAtencion = clientesAtendidosTotal > 0 ? (double) tiempoTotal / clientesAtendidosTotal / 1000.0 : 0;

        lblTiempoTotal.setText(String.format("%.1f segundos", tiempoTotal / 1000.0));
        lblClientesTotal.setText(String.valueOf(clientesAtendidosTotal));
        lblPromedioAtencion.setText(String.format("%.1f segundos", promedioAtencion));

        //Mostrar las estadisticas por cajera
        StringBuilder stats = new StringBuilder();  //StringBuilder es una clase que permite construir cadenas de texto de manera eficientes
        for(Cajera cajera : cajeras){
            stats.append(String.format("Cajera %s: %d clientes\n", cajera.getNombre(), cajera.getClientesAtendidos())); //Stats.append() permite formatear cadenas de texto de manera legible
        }

        actualizarLog("\n ESTADISTICAS FINALES");
        actualizarLog(stats.toString());
    }

    private void limpiarSimulacion(){   //Esta funcion se encarga de setear todas las areas en un valor predeterminado
        areaLog.clear();
        cajeras.clear();
        hilosCajeras.clear();
        clientesAtendidos.clear();

        lblTiempoTotal.setText("0 segundos");
        lblClientesTotal.setText("0");
        lblPromedioAtencion.setText("0 segundos");
    }
}
