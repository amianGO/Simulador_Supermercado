package com.dgapps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App  extends Application{ // Esta es la aplicacion principal que extiende de Application para poder utilizaar JavaFX
    
    //Este es el metodo principal que se encarga de cargar la interfaz, se inicia al iniciar la aplicacion
    @Override
    public void start (Stage primaryStage) throws Exception {   //Stage es la ventana principal de la aplicacion y se le pasa como parametro al metodo start

        FXMLLoader loader = new FXMLLoader(getClass().getResource("supermercado.fxml"));    //Cargamos el archivo FXML que vamos a utilizar como interfaz

        Parent root = loader.load();    //Parent funciona como un nodo raiz de la Escena y se carga la el Archivo FXML

        primaryStage.setTitle("Simulacion de Supermercado");
        primaryStage.setScene(new Scene(root, 800, 600));   //Creamos una nueva Escena con el nodo raiz, asignandole un ancho y un alto
        primaryStage.setResizable(false);   //Evita que la ventana se pueda Redimensionar
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);   //Este es el metodo main que se encarga de iniciar la aplicacion , se inciiar la aplicacion con el metodo launch()
    }
}
