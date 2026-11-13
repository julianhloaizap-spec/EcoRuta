package co.edu.uniquindio;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Empresa empresaEcoRuta = new Empresa("123456789", "Bienvenido a EcoRuta");
        empresaEcoRuta.cargarAportantesIniciales();

        int Menu = 0;

        while (Menu != 10) {
            String textoMenu = "=== " + empresaEcoRuta.getNombreEmpresa() + " ===\n" +
                    "NIT: " + empresaEcoRuta.getNitEmpresa() + "\n\n" +
                    "Seleccione una opcion digitando su numero:\n\n" +
                    "1. Registrar entrega\n" +
                    "2. Agregar lote\n" +
                    "3. Aceptar entrega\n" +
                    "4. Registrar aportante\n" +
                    "5. Ver aportantes registrados\n" +
                    "6. Consultar etiqueta\n" +
                    "7. Consultar valor a pagar\n" +
                    "8. Consultar entrega\n" +
                    "9. Consultar comprobante\n" +
                    "10. Salir\n";

            String entradaOpcion = JOptionPane.showInputDialog(null, textoMenu, "Menu Principal EcoRuta", JOptionPane.QUESTION_MESSAGE);

            if (entradaOpcion == null) {
                Menu = 10;
            } else if (!esNumeroEnteroNoNegativo(entradaOpcion.trim())) {
                mostrarMensaje(" Debe ingresar un numero valido del 1 al 10");
                Menu = 0;
            } else {
                Menu = Integer.parseInt(entradaOpcion.trim());
            }

            switch (Menu) {
                case 1:
                    registrarEntrega(empresaEcoRuta);
                    break;
                case 2:
                    agregarLote(empresaEcoRuta);
                    break;
                case 3:
                    aceptarEntrega(empresaEcoRuta);
                    break;
                case 4:
                    registrarAportante(empresaEcoRuta);
                    break;
                case 5:
                    verAportantesRegistrados(empresaEcoRuta);
                    break;
                case 6:
                    consultarEtiquetas(empresaEcoRuta);
                    break;
                case 7:
                    consultarValorAPagar(empresaEcoRuta);
                    break;
                case 8:
                    consultarEntrega(empresaEcoRuta);
                    break;
                case 9:
                    consultarComprobante(empresaEcoRuta);
                    break;
                case 10:
                    mostrarMensaje("Gracias por utilizar el sistema EcoRuta");
                    break;
                default:
                    if (Menu != 0) {
                        mostrarMensaje("Opcion no reconocida.\nPor favor digite un numero entre 1 y 10.");
                    }
                    break;
            }
        }
    }

    private static void registrarAportante(Empresa empresaEcoRuta) {
        String idAportante = JOptionPane.showInputDialog(null,
                "REGISTRAR APORTANTE\n\nIngrese el documento del aportante :");
        if (idAportante == null || idAportante.trim().isEmpty()) {
            mostrarMensaje("Operacion cancelada o id invalido.");
            return;
        }

        String nombreAportante = JOptionPane.showInputDialog(null,
                "REGISTRAR APORTANTE\n\nIngrese el nombre completo o razon social:");
        if (nombreAportante == null || nombreAportante.trim().isEmpty()) {
            mostrarMensaje("Operacion cancelada o nombre invalido.");
            return;
        }

        String correoAportante = JOptionPane.showInputDialog(null,
                "REGISTRAR APORTANTE\n\nIngrese el correo electronico:");
        if (correoAportante == null || correoAportante.trim().isEmpty()) {
            mostrarMensaje("Operacion cancelada o correo invalido.");
            return;
        }

        Aportante nuevoAportante = new Aportante(
                idAportante.trim(),
                nombreAportante.trim(),
                correoAportante.trim()
        );

        boolean aportanteRegistrado = empresaEcoRuta.registrarAportante(nuevoAportante);
        if (aportanteRegistrado) {
            mostrarMensaje("Aportante registrado exitosamente.\n\n" +
                    "Id: " + nuevoAportante.idAportante() + "\n" +
                    "Nombre: " + nuevoAportante.nombreAportante() + "\n" +
                    "Correo: " + nuevoAportante.correoAportante());
        } else {
            mostrarMensaje("Ya existe un aportante registrado con el docuemnto: " + idAportante.trim());
        }
    }

    private static void registrarEntrega(Empresa empresaEcoRuta) {
        String idAportante = JOptionPane.showInputDialog(null,
                "REGISTRAR NUEVA ENTREGA\n\nIngrese el docuemnto del aportante :");
        if (idAportante == null) {
            return;
        }

        String fechaActual = LocalDate.now().toString();
        Entrega entregaCreada = empresaEcoRuta.registrarEntregaAutomatica(fechaActual, idAportante);

        if (entregaCreada != null) {
            mostrarMensaje("Entrega registrada exitosamente.\n\n" +
                    "Id Entrega generado: " + entregaCreada.getIdEntrega() + "\n" +
                    "Fecha: " + entregaCreada.getFechaEntrega() + "\n" +
                    "Documento Aportante: " + entregaCreada.getAportante().idAportante() + "\n" +
                    "Nombre Aportante: " + entregaCreada.getAportante().nombreAportante() + "\n" +
                    "Estado inicial: " + entregaCreada.getEstadoEntrega());
        } else {
            mostrarMensaje("El documento ingresado no corresponde a un aportante registrado.");
        }
    }

    private static void agregarLote(Empresa empresaEcoRuta) {
        String idEntrega = JOptionPane.showInputDialog(null,
                "AGREGAR LOTE\n\nIngrese el id de la entrega abierta:");
        if (idEntrega == null) {
            return;
        }

        Entrega entregaEncontrada = empresaEcoRuta.buscarEntregaPorId(idEntrega);
        if (entregaEncontrada == null) {
            mostrarMensaje("No existe ninguna entrega con el id: " + idEntrega);
            return;
        }
        if (entregaEncontrada.getEstadoEntrega() != EstadoEntrega.ABIERTA) {
            mostrarMensaje("Operacion rechazada.\nLa entrega " + idEntrega + " ya se encuentra aceptada y cerrada.");
            return;
        }

        String menuTipoLote = "SELECCIONE EL TIPO DE LOTE (Digite el numero)\n\n" +
                "1. Papel y Carton\n" +
                "2. Metal\n" +
                "3. Residuo Electronico\n";

        String entradaTipo = JOptionPane.showInputDialog(null, menuTipoLote);
        if (entradaTipo == null || !esNumeroEnteroNoNegativo(entradaTipo.trim())) {
            mostrarMensaje("Operacion cancelada o numero de tipo de lote invalido.");
            return;
        }

        int opcionTipoLote = Integer.parseInt(entradaTipo.trim());
        if (opcionTipoLote < 1 || opcionTipoLote > 3) {
            mostrarMensaje("Opcion invalida. Debe digitar 1, 2 o 3.");
            return;
        }

        String descripcionLote = JOptionPane.showInputDialog(null,
                "Ingrese la descripcion del lote:");
        if (descripcionLote == null || descripcionLote.trim().isEmpty()) {
            mostrarMensaje("Debe ingresar una descripcion valida.");
            return;
        }

        String entradaPeso = JOptionPane.showInputDialog(null,
                "Ingrese el peso en Kg:");
        if (entradaPeso == null || !esNumeroEnteroNoNegativo(entradaPeso.trim())) {
            mostrarMensaje("El peso debe ser mayor a cero.");
            return;
        }
        int pesoKilogramos = Integer.parseInt(entradaPeso.trim());

        Lote nuevoLote = null;

        switch (opcionTipoLote) {
            case 1:
                String entradaClasificado = JOptionPane.showInputDialog(null,
                        "El papel o carton fue clasificado antes de entregarse?\n\n" +
                                "Digite el numero:\n" +
                                "1. Si\n" +
                                "2. No\n");
                if (entradaClasificado == null || !esNumeroEnteroNoNegativo(entradaClasificado.trim())) {
                    mostrarMensaje("Operacion rechazada. Debe digitar 1 o 2.");
                    return;
                }
                int opcionClasificado = Integer.parseInt(entradaClasificado.trim());
                if (opcionClasificado != 1 && opcionClasificado != 2) {
                    mostrarMensaje("Operacion rechazada. Debe digitar 1 o 2.");
                    return;
                }
                boolean estaClasificado = (opcionClasificado == 1);
                nuevoLote = new PapelCarton(descripcionLote.trim(), pesoKilogramos, estaClasificado);
                break;

            case 2:
                String entradaTipoMetal = JOptionPane.showInputDialog(null,
                        "Seleccione el tipo de metal digitando su numero:\n\n" +
                                "1. ALUMINIO\n" +
                                "2. ACERO\n");
                if (entradaTipoMetal == null || !esNumeroEnteroNoNegativo(entradaTipoMetal.trim())) {
                    mostrarMensaje("Operacion rechazada. Debe digitar 1 o 2.");
                    return;
                }
                int opcionMetal = Integer.parseInt(entradaTipoMetal.trim());
                TipoMetal tipoMetalSeleccionado = null;
                if (opcionMetal == 1) {
                    tipoMetalSeleccionado = TipoMetal.ALUMINIO;
                } else if (opcionMetal == 2) {
                    tipoMetalSeleccionado = TipoMetal.ACERO;
                } else {
                    mostrarMensaje("Operacion rechazada. Debe digitar 1 o 2.");
                    return;
                }
                nuevoLote = new Metal(descripcionLote.trim(), pesoKilogramos, tipoMetalSeleccionado);
                break;

            case 3:
                String entradaEquipos = JOptionPane.showInputDialog(null,
                        "Ingrese la cantidad de equipos completos :");
                if (entradaEquipos == null || !esNumeroEnteroNoNegativo(entradaEquipos.trim())) {
                    mostrarMensaje("La cantidad de equipos debe ser un numero mayor o igual a cero.");
                    return;
                }
                int cantidadEquipos = Integer.parseInt(entradaEquipos.trim());
                nuevoLote = new ResiduoElectronico(descripcionLote.trim(), pesoKilogramos, cantidadEquipos);
                break;

            default:
                break;
        }

        boolean loteAgregado = empresaEcoRuta.agregarLoteAEntrega(idEntrega, nuevoLote);
        if (loteAgregado) {
            mostrarMensaje("Lote agregado correctamente a la entrega " + entregaEncontrada.getIdEntrega() + ".\n\n" +
                    nuevoLote.toString());
        } else {
            mostrarMensaje("Verifique que el peso sea mayor a 0 kg.");
        }
    }

    private static void aceptarEntrega(Empresa empresaEcoRuta) {
        String idEntrega = JOptionPane.showInputDialog(null,
                "ACEPTAR ENTREGA\n\nIngrese el id de la entrega que desea aceptar:");
        if (idEntrega == null) {
            return;
        }

        boolean entregaAceptada = empresaEcoRuta.aceptarEntrega(idEntrega);
        if (entregaAceptada) {
            Comprobante comprobanteGenerado = empresaEcoRuta.consultarComprobantePorEntrega(idEntrega);
            mostrarMensaje("Entrega aceptada exitosamente.\n\n" +
                    "=== COMPROBANTE GENERADO ===\n" +
                    "Id de entrega: " + comprobanteGenerado.idEntrega() + "\n" +
                    "Documento del aportante: " + comprobanteGenerado.idAportante() + "\n" +
                    "Peso total: " + comprobanteGenerado.pesoTotal() + " kg\n" +
                    "Valor total reconocido: $" + comprobanteGenerado.valorTotalReconocido());
        } else {
            mostrarMensaje("Operacion rechazada.\n\n" +
                    "No se pudo aceptar la entrega porque no existe, no tiene lotes registrados\n" +
                    "o ya fue aceptada.");
        }
    }

    private static void verAportantesRegistrados(Empresa empresaEcoRuta) {
        ArrayList<Aportante> listaAportantes = empresaEcoRuta.getListaAportantes();
        String textoSalida = "=== APORTANTES REGISTRADOS ===\n\n";

        if (listaAportantes.isEmpty()) {
            textoSalida += "No hay aportantes registrados.";
        } else {
            for (int indice = 0; indice < listaAportantes.size(); indice++) {
                Aportante aportanteActual = listaAportantes.get(indice);
                textoSalida += (indice + 1) + ". Id: " + aportanteActual.idAportante() + "\n" +
                        "   Nombre: " + aportanteActual.nombreAportante() + "\n" +
                        "   Correo: " + aportanteActual.correoAportante() + "\n\n";
            }
        }
        mostrarMensaje(textoSalida);
    }

    private static void consultarEtiquetas(Empresa empresaEcoRuta) {
        String idEntrega = JOptionPane.showInputDialog(null,
                "CONSULTAR ETIQUETA\n\nIngrese el id de la entrega:");
        if (idEntrega == null) {
            return;
        }

        Entrega entregaEncontrada = empresaEcoRuta.buscarEntregaPorId(idEntrega);
        if (entregaEncontrada == null) {
            mostrarMensaje("No existe ninguna entrega registrada con el id: " + idEntrega);
            return;
        }

        ArrayList<String> etiquetas = empresaEcoRuta.consultarEtiquetasPorEntrega(idEntrega);
        String textoEtiquetas = "=== ETIQUETAS DE CONTROL INTERNO ===\n" +
                "Entrega: " + entregaEncontrada.getIdEntrega() + "\n\n";

        if (etiquetas.isEmpty()) {
            textoEtiquetas += "Esta entrega no contiene lotes que requieran etiqueta.";
        } else {
            for (int indice = 0; indice < etiquetas.size(); indice++) {
                textoEtiquetas += (indice + 1) + ". " + etiquetas.get(indice) + "\n";
            }
        }
        mostrarMensaje(textoEtiquetas);
    }

    private static void consultarValorAPagar(Empresa empresaEcoRuta) {
        String idAportante = JOptionPane.showInputDialog(null,
                "CONSULTAR VALOR A PAGAR\n\nIngrese el documento del aportante:");
        if (idAportante == null) {
            return;
        }

        double valorAPagar = empresaEcoRuta.consultarValorAPagarPorAportante(idAportante);
        Aportante aportanteEncontrado = empresaEcoRuta.buscarAportantePorId(idAportante);

        String nombreMostrar = (aportanteEncontrado != null) ? aportanteEncontrado.nombreAportante() : "No registrado";

        mostrarMensaje("=== CONSULTA DE VALOR A PAGAR ===\n\n" +
                "Documento del Aportante: " + idAportante.trim() + "\n" +
                "Aportante: " + nombreMostrar + "\n" +
                "Total a pagar : $" + valorAPagar);
    }

    private static void consultarEntrega(Empresa empresaEcoRuta) {
        String idEntrega = JOptionPane.showInputDialog(null,
                "CONSULTAR ENTREGA\n\nIngrese el id de la entrega:");
        if (idEntrega == null) {
            return;
        }

        Entrega entregaEncontrada = empresaEcoRuta.buscarEntregaPorId(idEntrega);
        if (entregaEncontrada == null) {
            mostrarMensaje("No existe ninguna entrega registrada con el id: " + idEntrega);
            return;
        }

        String estadoTexto = "";
        if (entregaEncontrada.getEstadoEntrega() == EstadoEntrega.ABIERTA) {
            estadoTexto = "ABIERTA ";
        } else {
            estadoTexto = "ACEPTADA ";
        }

        String detalleEntrega = "=== DETALLE DE LA ENTREGA ===\n\n" +
                "Id Entrega: " + entregaEncontrada.getIdEntrega() + "\n" +
                "Fecha: " + entregaEncontrada.getFechaEntrega() + "\n" +
                "Aportante: " + entregaEncontrada.getAportante().nombreAportante() +
                " (Id: " + entregaEncontrada.getAportante().idAportante() + ")\n" +
                "Estado: " + estadoTexto + "\n" +
                "Cantidad de lotes: " + entregaEncontrada.getListaLotes().size() + "\n\n";

        ArrayList<Lote> listaLotes = entregaEncontrada.getListaLotes();
        if (listaLotes.isEmpty()) {
            detalleEntrega += "Esta entrega aun no tiene lotes registrados.";
        } else {
            detalleEntrega += "Lotes registrados en la entrega:\n";
            for (int indice = 0; indice < listaLotes.size(); indice++) {
                Lote loteActual = listaLotes.get(indice);
                detalleEntrega += (indice + 1) + ". " + loteActual.toString() + "\n";
            }
        }

        mostrarMensaje(detalleEntrega);
    }

    private static void consultarComprobante(Empresa empresaEcoRuta) {
        String idEntrega = JOptionPane.showInputDialog(null,
                "CONSULTAR COMPROBANTE\n\nIngrese el id de la entrega:");
        if (idEntrega == null) {
            return;
        }

        Entrega entregaEncontrada = empresaEcoRuta.buscarEntregaPorId(idEntrega);
        if (entregaEncontrada == null) {
            mostrarMensaje("No existe ninguna entrega registrada con el id: " + idEntrega);
            return;
        }

        Comprobante comprobante = entregaEncontrada.getComprobante();
        if (comprobante == null) {
            mostrarMensaje("=== COMPROBANTE NO DISPONIBLE ===\n\n" +
                    "La entrega " + entregaEncontrada.getIdEntrega() + " se encuentra en estado ABIERTA.\n" +
                    "Debe aceptarla primero para generar el comprobante.");
        } else {
            mostrarMensaje("=== COMPROBANTE DE ENTREGA ===\n\n" +
                    "Id Entrega: " + comprobante.idEntrega() + "\n" +
                    "Documento del Aportante: " + comprobante.idAportante() + "\n" +
                    "Peso total: " + comprobante.pesoTotal() + " kg\n" +
                    "Valor total reconocido: $" + comprobante.valorTotalReconocido());
        }
    }

    private static boolean esNumeroEnteroNoNegativo(String textoEvaluar) {
        if (textoEvaluar == null || textoEvaluar.isEmpty() || textoEvaluar.length() > 9) {
            return false;
        }
        String digitosValidos = "0123456789";
        for (int indice = 0; indice < textoEvaluar.length(); indice++) {
            String digitoActual = textoEvaluar.substring(indice, indice + 1);
            if (!digitosValidos.contains(digitoActual)) {
                return false;
            }
        }
        return true;
    }

    private static void mostrarMensaje(String mensajeMostrar) {
        JOptionPane.showMessageDialog(null, mensajeMostrar, "EcoRuta", JOptionPane.INFORMATION_MESSAGE);
    }
}