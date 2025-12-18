package Clinica.ui;

import Clinica.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Aplicacion principal JavaFX para el cliente de la clinica.
 * Proporciona la interfaz grafica con chat y formularios.
 */
public class ClinicaClienteApp extends Application {

    // Componentes principales
    private VBox chatContainer;
    private ScrollPane chatScrollPane;
    private StackPane formularioContainer;
    private Label estadoLabel;
    private Button btnNuevaCita;
    private Button btnCancelarCita;

    // Paneles de formularios
    private FormularioCitaPane formularioCita;
    private FormularioPagoPane formularioPago;
    private FormularioMedicoPane formularioMedico;
    private FormularioSintomasPane formularioSintomas;
    private VBox panelDiagnostico;

    // Estado actual
    private String citaActualId;

    @Override
    public void start(Stage primaryStage) {
        // Registrar esta app en el bridge
        GUIBridge.getInstance().setApp(this);

        // Crear la interfaz
        BorderPane root = crearInterfazPrincipal();

        // Configurar escena
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/Clinica/ui/styles.css").toExternalForm());

        primaryStage.setTitle("Clinica de Fisioterapia - Sistema de Citas");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        // Manejar cierre de ventana
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        // Mensaje de bienvenida
        agregarMensaje("Bienvenido a la Clinica de Fisioterapia. Para comenzar, presione 'Nueva Cita'.",
                       MensajeChat.TipoAgente.SISTEMA);
    }

    private BorderPane crearInterfazPrincipal() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-container");

        // Header
        HBox header = crearHeader();
        root.setTop(header);

        // Contenido principal (Chat + Formularios)
        HBox contenido = new HBox(15);
        contenido.setPadding(new Insets(15));
        contenido.getStyleClass().add("content-area");

        // Panel de chat (izquierda)
        VBox chatPanel = crearPanelChat();
        HBox.setHgrow(chatPanel, Priority.ALWAYS);

        // Panel de formularios (derecha)
        VBox formPanel = crearPanelFormularios();
        formPanel.setPrefWidth(400);
        formPanel.setMinWidth(350);

        contenido.getChildren().addAll(chatPanel, formPanel);
        root.setCenter(contenido);

        // Footer con botones
        HBox footer = crearFooter();
        root.setBottom(footer);

        return root;
    }

    private HBox crearHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setSpacing(20);
        header.getStyleClass().add("header");

        // Titulo
        Label titulo = new Label("Clinica de Fisioterapia");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        titulo.setTextFill(Color.WHITE);

        // Separador flexible
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Estado
        HBox estadoBox = new HBox(10);
        estadoBox.setAlignment(Pos.CENTER_RIGHT);
        Label estadoTitulo = new Label("Estado:");
        estadoTitulo.setTextFill(Color.WHITE);
        estadoLabel = new Label("Listo");
        estadoLabel.getStyleClass().add("estado-label");

        estadoBox.getChildren().addAll(estadoTitulo, estadoLabel);

        header.getChildren().addAll(titulo, spacer, estadoBox);
        return header;
    }

    private VBox crearPanelChat() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("chat-panel");

        // Titulo del chat
        Label titulo = new Label("Conversacion con Agentes");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        titulo.getStyleClass().add("panel-title");

        // Contenedor de mensajes
        chatContainer = new VBox(10);
        chatContainer.setPadding(new Insets(10));
        chatContainer.getStyleClass().add("chat-container");

        chatScrollPane = new ScrollPane(chatContainer);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.getStyleClass().add("chat-scroll");
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);

        panel.getChildren().addAll(titulo, chatScrollPane);
        return panel;
    }

    private VBox crearPanelFormularios() {
        VBox panel = new VBox(10);
        panel.getStyleClass().add("form-panel");

        // Titulo del panel
        Label titulo = new Label("Formularios");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        titulo.getStyleClass().add("panel-title");

        // Contenedor para formularios (uno a la vez)
        formularioContainer = new StackPane();
        formularioContainer.getStyleClass().add("formulario-container");
        VBox.setVgrow(formularioContainer, Priority.ALWAYS);

        // Crear todos los formularios
        formularioCita = new FormularioCitaPane(this::onCitaEnviada);
        formularioPago = new FormularioPagoPane(this::onPagoEnviado);
        formularioMedico = new FormularioMedicoPane(this::onDatosMedicosEnviados);
        formularioSintomas = new FormularioSintomasPane(this::onSintomasEnviados);

        // Mensaje inicial
        Label mensajeInicial = new Label("Presione 'Nueva Cita' para comenzar");
        mensajeInicial.getStyleClass().add("mensaje-inicial");
        mensajeInicial.setWrapText(true);
        formularioContainer.getChildren().add(mensajeInicial);

        panel.getChildren().addAll(titulo, formularioContainer);
        return panel;
    }

    private HBox crearFooter() {
        HBox footer = new HBox(15);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.getStyleClass().add("footer");

        btnNuevaCita = new Button("Nueva Cita");
        btnNuevaCita.getStyleClass().addAll("btn", "btn-primary");
        btnNuevaCita.setOnAction(e -> iniciarNuevaCita());

        btnCancelarCita = new Button("Cancelar Cita");
        btnCancelarCita.getStyleClass().addAll("btn", "btn-danger");
        btnCancelarCita.setDisable(true);
        btnCancelarCita.setOnAction(e -> cancelarCita());

        footer.getChildren().addAll(btnNuevaCita, btnCancelarCita);
        return footer;
    }

    // Metodo para agregar mensajes al chat
    public void agregarMensaje(String contenido, MensajeChat.TipoAgente agente) {
        MensajeChat mensaje = new MensajeChat(contenido, agente);

        VBox burbuja = new VBox(5);
        burbuja.getStyleClass().add("mensaje-burbuja");
        burbuja.setMaxWidth(400);

        // Header del mensaje con nombre del agente
        HBox headerMsg = new HBox(10);
        Label nombreAgente = new Label(mensaje.getNombreAgente());
        nombreAgente.setFont(Font.font("System", FontWeight.BOLD, 12));
        nombreAgente.setStyle("-fx-text-fill: " + mensaje.getColorAgente() + ";");

        Label tiempo = new Label(mensaje.getTimestampFormateado());
        tiempo.getStyleClass().add("mensaje-tiempo");

        headerMsg.getChildren().addAll(nombreAgente, tiempo);

        // Contenido del mensaje
        Label textoMensaje = new Label(contenido);
        textoMensaje.setWrapText(true);
        textoMensaje.getStyleClass().add("mensaje-texto");

        burbuja.getChildren().addAll(headerMsg, textoMensaje);

        // Alinear segun el agente
        HBox wrapper = new HBox();
        wrapper.setPadding(new Insets(5, 10, 5, 10));

        switch (agente) {
            case RECEPCIONISTA:
                wrapper.setAlignment(Pos.CENTER_RIGHT);
                burbuja.getStyleClass().add("mensaje-recepcionista");
                break;
            case FISIOTERAPEUTA:
                wrapper.setAlignment(Pos.CENTER_LEFT);
                burbuja.getStyleClass().add("mensaje-fisioterapeuta");
                break;
            case AYUDANTE:
                wrapper.setAlignment(Pos.CENTER_LEFT);
                burbuja.getStyleClass().add("mensaje-ayudante");
                break;
            case SISTEMA:
                wrapper.setAlignment(Pos.CENTER);
                burbuja.getStyleClass().add("mensaje-sistema");
                break;
        }

        wrapper.getChildren().add(burbuja);
        chatContainer.getChildren().add(wrapper);

        // Auto-scroll al final
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    // Metodos para mostrar formularios
    public void mostrarFormularioCita() {
        formularioContainer.getChildren().clear();
        formularioCita.limpiar();
        formularioContainer.getChildren().add(formularioCita);
    }

    public void mostrarFormularioPago(String idCita, double monto) {
        this.citaActualId = idCita;
        formularioContainer.getChildren().clear();
        formularioPago.setMonto(monto);
        formularioPago.limpiar();
        formularioContainer.getChildren().add(formularioPago);
    }

    public void mostrarFormularioMedico(String idCita) {
        this.citaActualId = idCita;
        formularioContainer.getChildren().clear();
        formularioMedico.limpiar();
        formularioContainer.getChildren().add(formularioMedico);
    }

    public void mostrarFormularioSintomas(String idCita, Paciente paciente) {
        this.citaActualId = idCita;
        formularioContainer.getChildren().clear();
        formularioSintomas.setPaciente(paciente);
        formularioSintomas.limpiar();
        formularioContainer.getChildren().add(formularioSintomas);
    }

    public void mostrarDiagnostico(Consulta consulta) {
        formularioContainer.getChildren().clear();

        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.getStyleClass().add("diagnostico-panel");

        Label titulo = new Label("Diagnostico y Tratamiento");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.getStyleClass().add("diagnostico-titulo");

        // Procedimiento
        VBox procBox = crearSeccionDiagnostico("Procedimiento:", consulta.getProcedimiento());

        // Sesiones
        VBox sesionesBox = crearSeccionDiagnostico("Sesiones Recomendadas:",
            String.valueOf(consulta.getSesionesRecomendadas()) + " sesiones");

        // Medicamentos
        VBox medBox = crearSeccionDiagnostico("Medicamentos:",
            consulta.getMedicamentos() != null ? consulta.getMedicamentos() : "Ninguno");

        // Ejercicios
        VBox ejBox = crearSeccionDiagnostico("Ejercicios para Casa:",
            consulta.getEjercicios() != null ? consulta.getEjercicios() : "Ninguno");

        Button btnFinalizar = new Button("Finalizar Consulta");
        btnFinalizar.getStyleClass().addAll("btn", "btn-success");
        btnFinalizar.setOnAction(e -> {
            reiniciarInterfaz();
            agregarMensaje("Consulta finalizada. Gracias por su visita.", MensajeChat.TipoAgente.SISTEMA);
        });

        panel.getChildren().addAll(titulo, procBox, sesionesBox, medBox, ejBox, btnFinalizar);
        formularioContainer.getChildren().add(panel);
    }

    private VBox crearSeccionDiagnostico(String titulo, String contenido) {
        VBox box = new VBox(5);
        Label lblTitulo = new Label(titulo);
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 12));
        lblTitulo.getStyleClass().add("diagnostico-seccion-titulo");

        Label lblContenido = new Label(contenido);
        lblContenido.setWrapText(true);
        lblContenido.getStyleClass().add("diagnostico-seccion-contenido");

        box.getChildren().addAll(lblTitulo, lblContenido);
        return box;
    }

    // Callbacks de formularios
    private void onCitaEnviada(Cita cita) {
        this.citaActualId = cita.getId();
        btnCancelarCita.setDisable(false);
        btnNuevaCita.setDisable(true);

        agregarMensaje("Solicitando cita para " + cita.getNombrePaciente() +
                       " a las " + cita.getHora() +
                       " - Terapia: " + cita.getTipoTerapia(),
                       MensajeChat.TipoAgente.RECEPCIONISTA);

        // Notificar al agente
        GUIBridge.getInstance().notificarCitaCreada(cita);
    }

    private void onPagoEnviado(Pago pago) {
        String tipoPago = pago.getTipoPago().equals(Pago.EFECTIVO) ? "efectivo" : "tarjeta";
        agregarMensaje("Pago realizado con " + tipoPago + " por $" + pago.getMonto(),
                       MensajeChat.TipoAgente.RECEPCIONISTA);
        GUIBridge.getInstance().enviarPago(pago);
    }

    private void onDatosMedicosEnviados(Paciente paciente) {
        agregarMensaje("Datos medicos enviados - Altura: " + paciente.getAltura() +
                       "m, Peso: " + paciente.getPeso() + "kg",
                       MensajeChat.TipoAgente.RECEPCIONISTA);
        GUIBridge.getInstance().enviarDatosMedicos(paciente);
    }

    private void onSintomasEnviados(Consulta consulta) {
        agregarMensaje("Sintomas reportados - Zona: " + consulta.getZonaDolor() +
                       ", Nivel de dolor: " + consulta.getNivelDolor() + "/10",
                       MensajeChat.TipoAgente.RECEPCIONISTA);
        GUIBridge.getInstance().enviarSintomas(consulta);
    }

    // Acciones de botones principales
    private void iniciarNuevaCita() {
        agregarMensaje("Iniciando proceso de nueva cita...", MensajeChat.TipoAgente.SISTEMA);
        mostrarFormularioCita();
        actualizarEstado("Creando cita");
    }

    private void cancelarCita() {
        if (citaActualId != null) {
            agregarMensaje("Solicitando cancelacion de cita...", MensajeChat.TipoAgente.RECEPCIONISTA);
            GUIBridge.getInstance().notificarCitaCancelada(citaActualId);
        }
    }

    // Metodos publicos para control desde el bridge
    public void actualizarEstado(String estado) {
        estadoLabel.setText(estado);
    }

    public void habilitarNuevaCita(boolean habilitar) {
        btnNuevaCita.setDisable(!habilitar);
        btnCancelarCita.setDisable(habilitar);
    }

    public void reiniciarInterfaz() {
        citaActualId = null;
        btnNuevaCita.setDisable(false);
        btnCancelarCita.setDisable(true);
        actualizarEstado("Listo");

        formularioContainer.getChildren().clear();
        Label mensajeInicial = new Label("Presione 'Nueva Cita' para comenzar");
        mensajeInicial.getStyleClass().add("mensaje-inicial");
        mensajeInicial.setWrapText(true);
        formularioContainer.getChildren().add(mensajeInicial);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
