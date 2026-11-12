package co.edu.uniquindio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Empresa {
    private final ArrayList<Aportante> aportantes;
    private final ArrayList<Entrega> entregas;

    public Empresa() {
        this.aportantes = new ArrayList<>();
        this.entregas = new ArrayList<>();
    }

    public void cargarAportantesIniciales() {
        registrarAportante(new Aportante("1001", "Hogar Familia Torres", "torres@correo.com"));
        registrarAportante(new Aportante("2002", "Comercio Supermercado El Sol", "elsol@comercio.com"));
        registrarAportante(new Aportante("3003", "Institución Educativa San José", "contacto@sanjose.edu.co"));
    }

    public boolean registrarAportante(Aportante aportante) {
        if (aportante == null
                || aportante.idAportante() == null || aportante.idAportante().isBlank()
                || aportante.nombreAportante() == null || aportante.nombreAportante().isBlank()
                || aportante.correoAportante() == null || aportante.correoAportante().isBlank()) {
            return false;
        }
        if (buscarAportante(aportante.idAportante()) != null) {
            return false;
        }
        aportantes.add(new Aportante(
                aportante.idAportante().trim(),
                aportante.nombreAportante().trim(),
                aportante.correoAportante().trim()
        ));
        return true;
    }

    public Aportante buscarAportante(String identificacion) {
        if (identificacion == null || identificacion.isBlank()) {
            return null;
        }
        String idBuscado = identificacion.trim();
        for (Aportante aportante : aportantes) {
            if (aportante.idAportante().equalsIgnoreCase(idBuscado)) {
                return aportante;
            }
        }
        return null;
    }

    public ArrayList<Aportante> listarAportantes() {
        return new ArrayList<>(aportantes);
    }

    public Entrega registrarEntrega(LocalDate fecha, String identificacionAportante) {
        if (fecha == null) {
            return null;
        }
        Aportante aportante = buscarAportante(identificacionAportante);
        if (aportante == null) {
            return null;
        }
        Entrega nuevaEntrega = new Entrega(fecha, aportante);
        if (buscarEntrega(nuevaEntrega.getCodigo()) != null) {
            return null;
        }
        entregas.add(nuevaEntrega);
        return nuevaEntrega;
    }

    public Entrega buscarEntrega(String codigoEntrega) {
        if (codigoEntrega == null || codigoEntrega.isBlank()) {
            return null;
        }
        String codigoBuscado = codigoEntrega.trim();
        for (Entrega entrega : entregas) {
            if (entrega.getCodigo().equalsIgnoreCase(codigoBuscado)) {
                return entrega;
            }
        }
        return null;
    }

    public ArrayList<Entrega> listarEntregas() {
        return new ArrayList<>(entregas);
    }

    public boolean agregarLoteAEntrega(String codigoEntrega, Lote lote) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return false;
        }
        return entrega.agregarLote(lote);
    }

    public Comprobante aceptarEntrega(String codigoEntrega) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return null;
        }
        return entrega.aceptar();
    }

    public Comprobante consultarComprobante(String codigoEntrega) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null || entrega.getEstado() != EstadoEntrega.ACEPTADA) {
            return null;
        }
        return entrega.getComprobante();
    }

    public List<String> consultarEtiquetas(String codigoEntrega) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null || entrega.getEstado() != EstadoEntrega.ACEPTADA) {
            return null;
        }
        return entrega.obtenerEtiquetas();
    }

    public double calcularValorAcumuladoAportante(String identificacionAportante) {
        if (identificacionAportante == null || identificacionAportante.isBlank()) {
            return 0.0;
        }
        String idBuscado = identificacionAportante.trim();
        double acumulado = 0.0;
        for (Entrega entrega : entregas) {
            if (entrega.getEstado() == EstadoEntrega.ACEPTADA
                    && entrega.getComprobante() != null
                    && entrega.getAportante().idAportante().equalsIgnoreCase(idBuscado)) {
                acumulado += entrega.getComprobante().valorTotalReconocido();
            }
        }
        return acumulado;
    }
}