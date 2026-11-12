package co.edu.uniquindio;

import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Main {
    private static final Empresa sistema = new Empresa();

    public static void main(String[] args) {
        sistema.cargarAportantesIniciales();
        int opcion;

        do {
            opcion = mostrarMenuPrincipal();
            switch (opcion) {
                case 1 -> listarAportantes();
                case 2 -> registrarNuevoAportante();
                case 3 -> crearEntrega();
                case 4 -> agregarLoteAEntrega();
                case 5 -> aceptarEntrega();
                case 6 -> consultarComprobante();
                case 7 -> consultarEtiquetas();
                case 8 -> consultarAcumuladoAportante();
                case 9 -> listarEntregas();
                case 0 -> mostrarMensaje("Salir del sistema",
                        "Empresa", JOptionPane.INFORMATION_MESSAGE);
                default -> mostrarMensaje("Opción no válida. Seleccione un número entre 0 y 9.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        } while (opcion != 0);
    }

    private static int mostrarMenuPrincipal() {
        String menu = """
                ================ MENÚ PRINCIPAL - Eco Ruta ================
                1. Listar aportantes registrados
                2. Registrar nuevo aportante
                3. Crear entrega
                4. Agregar lote a una entrega abierta
                5. Aceptar entrega y generar comprobante
                6. Consultar comprobante por código de entrega
                7. Consultar etiquetas por código de entrega
                8. Consultar valor acumulado reconocido por aportante
                9. Listar todas las entregas
                0. Salir
                ==========================================================
                Seleccione una opción:
                """;

        while (true) {
            String entrada = JOptionPane.showInputDialog(null, menu,
                    "Empresa · Recepción y Valoración", JOptionPane.QUESTION_MESSAGE);
            if (entrada == null) {
                return 0;
            }
            entrada = entrada.trim();
            if (entrada.matches("\\d+")) {
                return Integer.parseInt(entrada);
            }
            mostrarMensaje("Debe ingresar un número entero válido.",
                    "Entrada inválida", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void listarAportantes() {
        List<Aportante> lista = sistema.listarAportantes();
        if (lista.isEmpty()) {
            mostrarMensaje("No hay aportantes registrados.", "Aportantes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("--- APORTANTES REGISTRADOS ---\n\n");
        for (Aportante a : lista) {
            sb.append(String.format("Documento: %s  |  Nombre: %s  |  Correo: %s%n",
                    a.idAportante(), a.nombreAportante(), a.correoAportante()));
        }
        mostrarTextoLargo(sb.toString(), "Aportantes Registrados");
    }

    private static void registrarNuevoAportante() {
        String documento = leerTexto("Ingrese el número de documento del aportante:", "Registrar Aportante");
        if (documento == null) return;

        String nombre = leerTexto("Ingrese el nombre del aportante:", "Registrar Aportante");
        if (nombre == null) return;

        String correo = leerTexto("Ingrese el correo electrónico del aportante:", "Registrar Aportante");
        if (correo == null) return;

        Aportante nuevo = new Aportante(documento, nombre, correo);
        boolean registrado = sistema.registrarAportante(nuevo);
        if (registrado) {
            mostrarMensaje("Aportante registrado correctamente con documento: " + nuevo.idAportante(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("RECHAZADO: Datos incompletos o el número de documento ya se encuentra registrado.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void crearEntrega() {
        String documento = leerTexto("Ingrese el número de documento del aportante registrado (ej. 1001):", "Crear Entrega");
        if (documento == null) return;

        Entrega entrega = sistema.registrarEntrega(LocalDate.now(), documento);
        if (entrega != null) {
            mostrarMensaje("Entrega creada exitosamente en estado ABIERTA.\nCódigo generado: "
                            + entrega.getCodigo() + "\n" + entrega,
                    "Entrega Registrada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("RECHAZADO: El aportante no existe o el documento es inválido.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void agregarLoteAEntrega() {
        String codigoEntrega = leerTexto("Ingrese el código de la entrega abierta (ej. E01):", "Agregar Lote");
        if (codigoEntrega == null) return;

        String descripcion = leerTexto("Ingrese la descripción del material:", "Agregar Lote");
        if (descripcion == null) return;

        Integer pesoKg = leerEntero("Ingrese el peso en kilogramos (entero positivo > 0):", "Agregar Lote");
        if (pesoKg == null) return;

        String menuMaterial = """
                Seleccione el tipo de material del lote:
                1. Papel y cartón
                2. Metales (ALUMINIO, ACERO)
                3. Residuos electronicos
                """;
        Integer tipo = leerEntero(menuMaterial, "Tipo de Material");
        if (tipo == null) return;

        Lote lote = switch (tipo) {
            case 1 -> {
                String menuClasificado = """
                        ¿El material fue clasificado antes de entregarse?
                        Ingrese 1 para Sí o 2 para No:
                        """;
                Boolean clasificado = leerConfirmacionNumerica(menuClasificado, "Papel y Carton");
                if (clasificado == null) yield null;
                yield new PapelCarton(descripcion, pesoKg, clasificado);
            }
            case 2 -> {
                String menuMetal = """
                        Seleccione el tipo de metal:
                        1. ALUMINIO ($2.200/kg)
                        2. ACERO ($900/kg)
                        """;
                Integer opcionMetal = leerEntero(menuMetal, "Metales");
                if (opcionMetal == null) yield null;
                TipoMetal tipoMetal = switch (opcionMetal) {
                    case 1 -> TipoMetal.ALUMINIO;
                    case 2 -> TipoMetal.ACERO;
                    default -> null;
                };
                if (tipoMetal == null) {
                    mostrarMensaje("Opción de metal no válida. Debe ingresar 1 o 2.",
                            "Dato inválido", JOptionPane.WARNING_MESSAGE);
                    yield null;
                }
                yield new Metal(descripcion, pesoKg, tipoMetal);
            }
            case 3 -> {
                Integer equipos = leerEntero("Ingrese la cantidad de equipos completos (>= 0):", "Residuos Electrónicos");
                if (equipos == null) yield null;
                yield new ResiduoElectronico(descripcion, pesoKg, equipos);
            }
            default -> {
                mostrarMensaje("Tipo de material no válido. Debe seleccionar 1, 2 o 3.",
                        "Dato inválido", JOptionPane.WARNING_MESSAGE);
                yield null;
            }
        };

        if (lote == null) {
            return;
        }

        boolean agregado = sistema.agregarLoteAEntrega(codigoEntrega, lote);
        if (agregado) {
            mostrarMensaje("Lote agregado correctamente (Codigo asignado: " + lote.getIdLote() + "):\n" + lote,
                    "Lote Agregado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("RECHAZADO: La entrega no existe, ya está ACEPTADA o los datos del lote son inválidos.",
                    "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void aceptarEntrega() {
        String codigoEntrega = leerTexto("Ingrese el código de la entrega a aceptar (ej. E01):", "Aceptar Entrega");
        if (codigoEntrega == null) return;

        Comprobante comprobante = sistema.aceptarEntrega(codigoEntrega);
        if (comprobante != null) {
            mostrarMensaje("Entrega aceptada exitosamente.\n\nComprobante generado:\n" + comprobante,
                    "Comprobante de Entrega", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("RECHAZADO: La entrega no existe, está vacía (sin lotes) o ya fue aceptada.",
                    "No se pudo aceptar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void consultarComprobante() {
        String codigoEntrega = leerTexto("Ingrese el código de la entrega (ej. E01):", "Consultar Comprobante");
        if (codigoEntrega == null) return;

        Comprobante comprobante = sistema.consultarComprobante(codigoEntrega);
        if (comprobante != null) {
            mostrarMensaje(comprobante.toString(), "Comprobante Encontrado", JOptionPane.INFORMATION_MESSAGE);
        } else {
            mostrarMensaje("SIN RESULTADOS: La entrega no existe o aun no ha sido aceptada.",
                    "Consulta de Comprobante", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void consultarEtiquetas() {
        String codigoEntrega = leerTexto("Ingrese el código de la entrega aceptada (ej. E01):", "Consultar Etiquetas");
        if (codigoEntrega == null) return;

        List<String> etiquetas = sistema.consultarEtiquetas(codigoEntrega);
        if (etiquetas == null) {
            mostrarMensaje("SIN RESULTADOS: La entrega no existe o aún no ha sido aceptada.",
                    "Consulta de Etiquetas", JOptionPane.WARNING_MESSAGE);
        } else if (etiquetas.isEmpty()) {
            mostrarMensaje("La entrega no contiene lotes que requieran etiqueta (solo papel/cartón).",
                    "Consulta de Etiquetas", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("Etiquetas generadas (" + etiquetas.size() + "):\n\n");
            for (String e : etiquetas) {
                sb.append("• ").append(e).append("\n");
            }
            mostrarMensaje(sb.toString(), "Etiquetas de Control Interno", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static void consultarAcumuladoAportante() {
        String documento = leerTexto("Ingrese el número de documento del aportante (ej. 1001):", "Valor Acumulado");
        if (documento == null) return;

        double total = sistema.calcularValorAcumuladoAportante(documento);
        String msg = String.format("Valor acumulado reconocido para el documento '%s': $%,.0f", documento.trim(), total);
        mostrarMensaje(msg, "Consulta Acumulado por Aportante", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void listarEntregas() {
        List<Entrega> entregas = sistema.listarEntregas();
        if (entregas.isEmpty()) {
            mostrarMensaje("No hay entregas registradas en el sistema.",
                    "Listado de Entregas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("--- LISTADO DE ENTREGAS REGISTRADAS ---\n\n");
        for (Entrega e : entregas) {
            sb.append(e).append("\n");
            if (e.getLotes().isEmpty()) {
                sb.append("   (Sin lotes registrados)\n");
            } else {
                for (Lote l : e.getLotes()) {
                    sb.append("   - ").append(l).append("\n");
                }
            }
            sb.append("\n");
        }
        mostrarTextoLargo(sb.toString(), "Entregas Registradas");
    }

    private static String leerTexto(String mensaje, String titulo) {
        String valor = JOptionPane.showInputDialog(null, mensaje, titulo, JOptionPane.QUESTION_MESSAGE);
        return (valor != null) ? valor.trim() : null;
    }

    private static Integer leerEntero(String mensaje, String titulo) {
        while (true) {
            String entrada = JOptionPane.showInputDialog(null, mensaje, titulo, JOptionPane.QUESTION_MESSAGE);
            if (entrada == null) {
                return null;
            }
            entrada = entrada.trim();
            if (entrada.matches("-?\\d+")) {
                return Integer.parseInt(entrada);
            }
            mostrarMensaje("Debe ingresar un número entero válido.",
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static Boolean leerConfirmacionNumerica(String mensaje, String titulo) {
        while (true) {
            Integer opcion = leerEntero(mensaje, titulo);
            if (opcion == null) {
                return null;
            }
            switch (opcion) {
                case 1 -> {
                    return true;
                }
                case 2 -> {
                    return false;
                }
                default -> mostrarMensaje("Opción no válida. Ingrese 1 para Sí o 2 para No.",
                        "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private static void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(null, mensaje, titulo, tipoMensaje);
    }

    private static void mostrarTextoLargo(String contenido, String titulo) {
        JTextArea textArea = new JTextArea(contenido);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(720, 420));
        JOptionPane.showMessageDialog(null, scrollPane, titulo, JOptionPane.INFORMATION_MESSAGE);
    }
}