package co.edu.uniquindio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Entrega {
    private static int contador = 1;

    private final String codigo;
    private final LocalDate fecha;
    private final Aportante aportante;
    private EstadoEntrega estado;
    private final ArrayList<Lote> lotes;
    private Comprobante comprobante;

    public Entrega(LocalDate fecha, Aportante aportante) {
        this.codigo = generarSiguienteCodigo();
        this.fecha = fecha;
        this.aportante = aportante;
        this.estado = EstadoEntrega.ABIERTA;
        this.lotes = new ArrayList<>();
        this.comprobante = null;
    }

    private static String generarSiguienteCodigo() {
        return String.format("E%02d", contador++);
    }

    public static void reiniciarContador() {
        contador = 1;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Aportante getAportante() {
        return aportante;
    }

    public EstadoEntrega getEstado() {
        return estado;
    }

    public List<Lote> getLotes() {
        return Collections.unmodifiableList(lotes);
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public boolean agregarLote(Lote lote) {
        if (estado != EstadoEntrega.ABIERTA || lote == null || !lote.esValido()) {
            return false;
        }
        for (Lote existente : lotes) {
            if (existente.getIdLote().equalsIgnoreCase(lote.getIdLote())) {
                return false;
            }
        }
        lotes.add(lote);
        return true;
    }

    public int calcularPesoTotal() {
        int pesoTotal = 0;
        for (Lote lote : lotes) {
            pesoTotal += lote.getPesoKg();
        }
        return pesoTotal;
    }

    public double calcularValorTotal() {
        double valorTotal = 0.0;
        for (Lote lote : lotes) {
            valorTotal += lote.calcularValor();
        }
        return valorTotal;
    }

    public Comprobante aceptar() {
        if (estado != EstadoEntrega.ABIERTA || lotes.isEmpty()) {
            return null;
        }
        int pesoTotal = calcularPesoTotal();
        double valorTotal = calcularValorTotal();

        this.estado = EstadoEntrega.ACEPTADA;
        this.comprobante = new Comprobante(codigo, aportante.idAportante(), pesoTotal, valorTotal);
        return this.comprobante;
    }

    public List<String> obtenerEtiquetas() {
        ArrayList<String> etiquetas = new ArrayList<>();
        for (Lote lote : lotes) {
            IEtiquetable etiquetable = lote.comoEtiquetable();
            if (etiquetable != null) {
                etiquetas.add(etiquetable.generarEtiqueta());
            }
        }
        return Collections.unmodifiableList(etiquetas);
    }

    @Override
    public String toString() {
        return String.format("Entrega[Código=%s, Fecha=%s, Aportante=%s (Doc: %s), Estado=%s, Lotes=%d, Comprobante=%s]",
                codigo, fecha, aportante.nombreAportante(), aportante.idAportante(),
                estado, lotes.size(), (comprobante != null ? "Sí" : "No"));
    }
}