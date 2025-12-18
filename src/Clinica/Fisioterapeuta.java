package Clinica;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Agente Fisioterapeuta - Se ejecuta en el servidor (Computadora A).
 * Gestiona las citas, coordina con el Ayudante y genera diagnosticos.
 */
public class Fisioterapeuta extends Agent {

    private Codec codec = new SLCodec();
    private Ontology ontologia = ClinicaOntology.getInstance();

    // Almacen de citas activas
    private Map<String, Cita> citasActivas = new HashMap<>();
    private Map<String, Paciente> datosPacientes = new HashMap<>();

    @Override
    protected void setup() {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);

        System.out.println("[FISIOTERAPEUTA] Listo y esperando citas...");

        // Comportamiento para gestionar mensajes entrantes
        addBehaviour(new GestionarMensajesBehaviour());
    }

    /**
     * Comportamiento principal para gestionar mensajes de otros agentes.
     */
    class GestionarMensajesBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = receive();

            if (msg != null) {
                try {
                    procesarMensaje(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("[FISIOTERAPEUTA] Error procesando mensaje: " + e.getMessage());
                }
            } else {
                block();
            }
        }

        private void procesarMensaje(ACLMessage msg) throws Exception {
            int performative = msg.getPerformative();

            // Verificar si tiene contenido estructurado
            if (msg.getLanguage() != null && msg.getLanguage().equals(codec.getName())) {
                ContentElement ce = getContentManager().extractContent(msg);

                if (ce instanceof Agendar) {
                    procesarSolicitudCita((Agendar) ce, msg);
                } else if (ce instanceof EnviarSintomas) {
                    procesarSintomas((EnviarSintomas) ce, msg);
                } else if (ce instanceof CancelarCita) {
                    procesarCancelacion((CancelarCita) ce, msg);
                } else if (ce instanceof EnviarDatosMedicos) {
                    // Datos medicos recibidos del Ayudante
                    procesarDatosDelAyudante((EnviarDatosMedicos) ce, msg);
                }
            } else if (performative == ACLMessage.CANCEL) {
                // Mensaje de cancelacion sin contenido estructurado
                System.out.println("[FISIOTERAPEUTA] Recibida solicitud de cancelacion");
            }
        }

        private void procesarSolicitudCita(Agendar agendar, ACLMessage msg) {
            Cita cita = agendar.getCita();
            String idCita = cita.getId();

            System.out.println("\n[FISIOTERAPEUTA] Nueva solicitud de cita recibida:");
            System.out.println("  - Paciente: " + cita.getNombrePaciente());
            System.out.println("  - Hora: " + cita.getHora());
            System.out.println("  - Terapia: " + cita.getTipoTerapia());

            // Almacenar la cita
            cita.setEstado(Cita.ESTADO_CONFIRMADA);
            citasActivas.put(idCita, cita);

            // Confirmar la cita al Recepcionista
            enviarConfirmacion(msg, idCita, true,
                "Su cita para " + cita.getTipoTerapia() + " ha sido agendada para las " + cita.getHora());

            // Notificar al Ayudante para que prepare la sala y solicite datos
            notificarAyudante(cita);
        }

        private void procesarSintomas(EnviarSintomas enviarSintomas, ACLMessage msg) {
            Consulta consulta = enviarSintomas.getConsulta();
            String idCita = enviarSintomas.getIdCita();

            System.out.println("\n[FISIOTERAPEUTA] Sintomas recibidos para cita " + idCita);
            System.out.println("  - Zona de dolor: " + consulta.getZonaDolor());
            System.out.println("  - Nivel de dolor: " + consulta.getNivelDolor() + "/10");
            System.out.println("  - Descripcion: " + consulta.getDescripcionSintomas());

            // Obtener datos del paciente
            Paciente paciente = datosPacientes.get(idCita);
            Cita cita = citasActivas.get(idCita);

            // Generar diagnostico basado en sintomas y datos del paciente
            Consulta diagnostico = generarDiagnostico(consulta, paciente, cita);

            // Enviar diagnostico al Recepcionista
            enviarDiagnostico(msg, idCita, diagnostico);

            System.out.println("[FISIOTERAPEUTA] Diagnostico enviado al paciente");
        }

        private void procesarDatosDelAyudante(EnviarDatosMedicos enviarDatos, ACLMessage msg) {
            Paciente paciente = enviarDatos.getPaciente();
            String idCita = enviarDatos.getIdCita();

            System.out.println("\n[FISIOTERAPEUTA] Datos del paciente recibidos del Ayudante");

            // Almacenar datos del paciente
            datosPacientes.put(idCita, paciente);

            // Solicitar sintomas al paciente
            solicitarSintomas(idCita, paciente);
        }

        private void procesarCancelacion(CancelarCita cancelar, ACLMessage msg) {
            String idCita = cancelar.getIdCita();

            System.out.println("\n[FISIOTERAPEUTA] Solicitud de cancelacion para cita " + idCita);

            if (citasActivas.containsKey(idCita)) {
                Cita cita = citasActivas.get(idCita);
                cita.setEstado(Cita.ESTADO_CANCELADA);
                citasActivas.remove(idCita);
                datosPacientes.remove(idCita);

                // Confirmar cancelacion
                enviarConfirmacion(msg, idCita, true, "Su cita ha sido cancelada exitosamente.");

                // Notificar al Ayudante
                notificarCancelacionAyudante(idCita);

                System.out.println("[FISIOTERAPEUTA] Cita cancelada");
            } else {
                enviarConfirmacion(msg, idCita, false, "No se encontro la cita especificada.");
            }
        }
    }

    // Enviar confirmacion de cita al Recepcionista
    private void enviarConfirmacion(ACLMessage originalMsg, String idCita, boolean confirmada, String mensaje) {
        try {
            ConfirmarCita confirmacion = new ConfirmarCita();
            confirmacion.setIdCita(idCita);
            confirmacion.setConfirmada(confirmada);
            confirmacion.setMensaje(mensaje);

            ACLMessage reply = originalMsg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setLanguage(codec.getName());
            reply.setOntology(ontologia.getName());

            getContentManager().fillContent(reply, confirmacion);
            send(reply);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Notificar al Ayudante sobre nueva cita
    private void notificarAyudante(Cita cita) {
        try {
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID("Ayudante", AID.ISLOCALNAME));
            msg.setLanguage(codec.getName());
            msg.setOntology(ontologia.getName());

            // Enviar la cita completa al ayudante
            Agendar agendar = new Agendar();
            agendar.setCita(cita);

            getContentManager().fillContent(msg, agendar);
            send(msg);

            System.out.println("[FISIOTERAPEUTA] Ayudante notificado para preparar sala");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Notificar cancelacion al Ayudante
    private void notificarCancelacionAyudante(String idCita) {
        try {
            ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
            msg.addReceiver(new AID("Ayudante", AID.ISLOCALNAME));
            msg.setContent("Cita cancelada: " + idCita);
            send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Recibir datos del paciente desde el Ayudante y solicitar sintomas
    public void recibirDatosPaciente(String idCita, Paciente paciente) {
        datosPacientes.put(idCita, paciente);

        System.out.println("[FISIOTERAPEUTA] Datos del paciente recibidos:");
        System.out.println("  - Altura: " + paciente.getAltura() + "m");
        System.out.println("  - Peso: " + paciente.getPeso() + "kg");
        System.out.println("  - IMC: " + String.format("%.1f", paciente.calcularIMC()));
        System.out.println("  - Diabetes: " + (paciente.isDiabetes() ? "Si" : "No"));
        System.out.println("  - Alergias: " + paciente.getAlergias());

        // Solicitar sintomas al Recepcionista
        solicitarSintomas(idCita, paciente);
    }

    // Solicitar sintomas al paciente via Recepcionista
    private void solicitarSintomas(String idCita, Paciente paciente) {
        try {
            ConsultarSintomas consultar = new ConsultarSintomas();
            consultar.setIdCita(idCita);
            consultar.setPaciente(paciente);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new AID("Recepcionista", AID.ISLOCALNAME));
            msg.setLanguage(codec.getName());
            msg.setOntology(ontologia.getName());

            getContentManager().fillContent(msg, consultar);
            send(msg);

            System.out.println("[FISIOTERAPEUTA] Solicitud de sintomas enviada al Recepcionista");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Enviar diagnostico al Recepcionista
    private void enviarDiagnostico(ACLMessage originalMsg, String idCita, Consulta diagnostico) {
        try {
            EnviarDiagnostico enviar = new EnviarDiagnostico();
            enviar.setIdCita(idCita);
            enviar.setConsulta(diagnostico);

            ACLMessage reply = originalMsg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setLanguage(codec.getName());
            reply.setOntology(ontologia.getName());

            getContentManager().fillContent(reply, enviar);
            send(reply);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Genera un diagnostico basado en los sintomas, datos del paciente y tipo de terapia.
     */
    private Consulta generarDiagnostico(Consulta sintomas, Paciente paciente, Cita cita) {
        Consulta diagnostico = new Consulta();

        // Copiar datos de sintomas
        diagnostico.setZonaDolor(sintomas.getZonaDolor());
        diagnostico.setNivelDolor(sintomas.getNivelDolor());
        diagnostico.setDescripcionSintomas(sintomas.getDescripcionSintomas());

        String zona = sintomas.getZonaDolor();
        int nivelDolor = sintomas.getNivelDolor();
        String tipoTerapia = cita != null ? cita.getTipoTerapia() : "General";

        // Determinar procedimiento basado en zona y nivel de dolor
        String procedimiento = determinarProcedimiento(zona, nivelDolor, tipoTerapia);
        diagnostico.setProcedimiento(procedimiento);

        // Determinar numero de sesiones
        int sesiones = determinarSesiones(nivelDolor, zona);
        diagnostico.setSesionesRecomendadas(sesiones);

        // Determinar medicamentos (considerando alergias y diabetes)
        String medicamentos = determinarMedicamentos(nivelDolor, paciente);
        diagnostico.setMedicamentos(medicamentos);

        // Determinar ejercicios para casa
        String ejercicios = determinarEjercicios(zona);
        diagnostico.setEjercicios(ejercicios);

        return diagnostico;
    }

    private String determinarProcedimiento(String zona, int nivelDolor, String tipoTerapia) {
        StringBuilder proc = new StringBuilder();

        // Procedimiento base segun tipo de terapia
        switch (tipoTerapia) {
            case "Masaje Terapeutico":
                proc.append("Masaje descontracturante de 45 minutos");
                break;
            case "Rehabilitacion Fisica":
                proc.append("Programa de rehabilitacion con ejercicios progresivos");
                break;
            case "Electroterapia":
                proc.append("Sesion de TENS y ultrasonido terapeutico");
                break;
            case "Hidroterapia":
                proc.append("Terapia en piscina con ejercicios acuaticos");
                break;
            case "Terapia Manual":
                proc.append("Manipulacion articular y movilizacion de tejidos");
                break;
            case "Ejercicios Terapeuticos":
                proc.append("Programa de ejercicios especificos supervisados");
                break;
            default:
                proc.append("Evaluacion y tratamiento personalizado");
        }

        // Agregar detalles segun zona
        if (zona.contains("Cervical") || zona.contains("Cuello")) {
            proc.append(" con enfasis en region cervical y trapecios");
        } else if (zona.contains("Lumbar") || zona.contains("Espalda Baja")) {
            proc.append(" con enfasis en region lumbar y musculatura paravertebral");
        } else if (zona.contains("Rodillas")) {
            proc.append(" con fortalecimiento de cuadriceps y estiramiento de isquiotibiales");
        } else if (zona.contains("Hombros")) {
            proc.append(" con trabajo en manguito rotador y movilidad glenohumeral");
        }

        // Agregar intensidad segun nivel de dolor
        if (nivelDolor >= 7) {
            proc.append(". Iniciar con intensidad baja y progresar gradualmente.");
        } else if (nivelDolor >= 4) {
            proc.append(". Intensidad moderada con monitoreo constante.");
        } else {
            proc.append(". Intensidad normal con ejercicios de mantenimiento.");
        }

        return proc.toString();
    }

    private int determinarSesiones(int nivelDolor, String zona) {
        int sesionesBase;

        // Sesiones base segun nivel de dolor
        if (nivelDolor >= 8) {
            sesionesBase = 12;
        } else if (nivelDolor >= 6) {
            sesionesBase = 8;
        } else if (nivelDolor >= 4) {
            sesionesBase = 6;
        } else {
            sesionesBase = 4;
        }

        // Ajustar segun zona (algunas zonas requieren mas sesiones)
        if (zona.contains("Lumbar") || zona.contains("Cervical")) {
            sesionesBase += 2;
        } else if (zona.contains("Rodillas") || zona.contains("Cadera")) {
            sesionesBase += 1;
        }

        return sesionesBase;
    }

    private String determinarMedicamentos(int nivelDolor, Paciente paciente) {
        StringBuilder meds = new StringBuilder();

        // Verificar alergias
        String alergias = paciente != null ? paciente.getAlergias() : "";
        boolean alergicoAINEs = alergias != null &&
            (alergias.toLowerCase().contains("ibuprofeno") ||
             alergias.toLowerCase().contains("naproxeno") ||
             alergias.toLowerCase().contains("aines"));

        if (nivelDolor >= 7) {
            if (!alergicoAINEs) {
                meds.append("Ibuprofeno 400mg cada 8 horas por 5 dias (con alimentos)");
            } else {
                meds.append("Paracetamol 500mg cada 6 horas por 5 dias");
            }
            meds.append(", Gel antiinflamatorio topico aplicar 3 veces al dia");
        } else if (nivelDolor >= 4) {
            meds.append("Gel antiinflamatorio topico aplicar 2-3 veces al dia");
            if (!alergicoAINEs) {
                meds.append(", Naproxeno 250mg cada 12 horas si el dolor persiste");
            }
        } else {
            meds.append("Gel de arnica para uso topico segun necesidad");
        }

        // Consideracion para diabeticos
        if (paciente != null && paciente.isDiabetes()) {
            meds.append(". NOTA: Por su condicion de diabetes, evite antiinflamatorios prolongados y " +
                       "consulte con su medico antes de tomar cualquier medicamento");
        }

        return meds.toString();
    }

    private String determinarEjercicios(String zona) {
        StringBuilder ejercicios = new StringBuilder();

        if (zona.contains("Cervical") || zona.contains("Cuello")) {
            ejercicios.append("1. Rotaciones suaves de cuello (10 rep cada lado)\n");
            ejercicios.append("2. Flexion y extension de cuello (10 rep)\n");
            ejercicios.append("3. Retraccion cervical (menton hacia atras, 15 rep)\n");
            ejercicios.append("4. Estiramiento de trapecios (30 seg cada lado)");
        } else if (zona.contains("Lumbar") || zona.contains("Espalda Baja")) {
            ejercicios.append("1. Estiramiento de gato-camello (15 rep)\n");
            ejercicios.append("2. Rodillas al pecho alternadas (10 rep cada lado)\n");
            ejercicios.append("3. Puente de gluteos (15 rep)\n");
            ejercicios.append("4. Plancha abdominal (30 seg, 3 series)");
        } else if (zona.contains("Hombros")) {
            ejercicios.append("1. Circulos de hombros (10 rep cada direccion)\n");
            ejercicios.append("2. Pendulares de Codman (1 minuto)\n");
            ejercicios.append("3. Rotacion externa con banda elastica (15 rep)\n");
            ejercicios.append("4. Estiramiento de pectoral en esquina (30 seg)");
        } else if (zona.contains("Rodillas")) {
            ejercicios.append("1. Extensiones de rodilla sentado (15 rep)\n");
            ejercicios.append("2. Sentadillas parciales (10 rep)\n");
            ejercicios.append("3. Elevacion de pierna recta (15 rep cada lado)\n");
            ejercicios.append("4. Estiramiento de cuadriceps e isquiotibiales (30 seg cada uno)");
        } else if (zona.contains("Espalda Alta") || zona.contains("Dorsal")) {
            ejercicios.append("1. Estiramiento de romboides (30 seg)\n");
            ejercicios.append("2. Retraccion escapular (15 rep)\n");
            ejercicios.append("3. Estiramiento de pectoral (30 seg cada lado)\n");
            ejercicios.append("4. Rowing con banda elastica (15 rep)");
        } else {
            ejercicios.append("1. Estiramiento general de la zona afectada (30 seg)\n");
            ejercicios.append("2. Movilidad articular suave (10 rep)\n");
            ejercicios.append("3. Fortalecimiento isometrico (10 seg, 5 rep)\n");
            ejercicios.append("4. Caminar 20-30 minutos diarios");
        }

        ejercicios.append("\n\nRealizar los ejercicios 2 veces al dia. ");
        ejercicios.append("Detener si aumenta el dolor.");

        return ejercicios.toString();
    }

    @Override
    protected void takeDown() {
        System.out.println("[FISIOTERAPEUTA] Terminando servicio.");
    }
}
