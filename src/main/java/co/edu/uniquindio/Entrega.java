package co.edu.uniquindio;

import java.util.ArrayList;

public class Entrega {

    private String idEntrega;
    private String fechaEntrega;
    private Aportante aportante;
    private ArrayList<Lote> listaLotes;
    private EstadoEntrega estadoEntrega;
    private Comprobante comprobante;
    private int contadorLotes;

    public Entrega(String idEntrega, String fechaEntrega, Aportante aportante) {
        this.idEntrega = idEntrega;
        this.fechaEntrega = fechaEntrega;
        this.aportante = aportante;
        this.listaLotes = new ArrayList<Lote>();
        this.estadoEntrega = EstadoEntrega.ABIERTA;
        this.comprobante = null;
        this.contadorLotes = 1;
    }

    private String generarIdLoteAutomatico() {
        String idGenerado = "";
        if (contadorLotes < 10) {
            idGenerado = "L0" + contadorLotes;
        } else {
            idGenerado = "L" + contadorLotes;
        }
        contadorLotes++;
        return idGenerado;
    }

    public boolean existeIdLote(String idLoteBuscado) {
        for (int indice = 0; indice < listaLotes.size(); indice++) {
            Lote loteActual = listaLotes.get(indice);
            if (loteActual.getIdLote().equalsIgnoreCase(idLoteBuscado)) {
                return true;
            }
        }
        return false;
    }

    public boolean agregarLote(Lote nuevoLote) {
        if (estadoEntrega != EstadoEntrega.ABIERTA) {
            return false;
        }
        if (nuevoLote == null) {
            return false;
        }
        if (nuevoLote.getPesoKilogramos() <= 0) {
            return false;
        }
        if (nuevoLote instanceof ResiduoElectronico) {
            ResiduoElectronico loteElectronico = (ResiduoElectronico) nuevoLote;
            if (loteElectronico.getCantidadEquiposCompletos() < 0) {
                return false;
            }
        }

        if (nuevoLote.getIdLote() == null || nuevoLote.getIdLote().trim().isEmpty()) {
            String idAutomatico = generarIdLoteAutomatico();
            while (existeIdLote(idAutomatico)) {
                idAutomatico = generarIdLoteAutomatico();
            }
            nuevoLote.setIdLote(idAutomatico);
        } else if (existeIdLote(nuevoLote.getIdLote())) {
            return false;
        }

        listaLotes.add(nuevoLote);
        return true;
    }

    public boolean aceptarEntrega() {
        if (estadoEntrega != EstadoEntrega.ABIERTA) {
            return false;
        }
        if (listaLotes.isEmpty()) {
            return false;
        }

        int pesoTotalCalculado = 0;
        double valorTotalCalculado = 0.0;

        for (int indice = 0; indice < listaLotes.size(); indice++) {
            Lote loteActual = listaLotes.get(indice);
            pesoTotalCalculado += loteActual.getPesoKilogramos();
            valorTotalCalculado += loteActual.calcularValor();
        }

        estadoEntrega = EstadoEntrega.ACEPTADA;
        comprobante = new Comprobante(
                idEntrega,
                aportante.idAportante(),
                pesoTotalCalculado,
                valorTotalCalculado
        );
        return true;
    }

    public ArrayList<String> obtenerEtiquetas() {
        ArrayList<String> listaEtiquetas = new ArrayList<String>();
        for (int indice = 0; indice < listaLotes.size(); indice++) {
            Lote loteActual = listaLotes.get(indice);
            if (loteActual instanceof IEtiquetable) {
                IEtiquetable loteEtiquetable = (IEtiquetable) loteActual;
                listaEtiquetas.add(loteEtiquetable.generarEtiqueta());
            }
        }
        return listaEtiquetas;
    }

    public String getIdEntrega() {
        return idEntrega;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public Aportante getAportante() {
        return aportante;
    }

    public ArrayList<Lote> getListaLotes() {
        return listaLotes;
    }

    public EstadoEntrega getEstadoEntrega() {
        return estadoEntrega;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    @Override
    public String toString() {
        String detalle = "Entrega Id: " + idEntrega +
                "\nFecha: " + fechaEntrega +
                "\nAportante: " + aportante.nombreAportante() + " (Id: " + aportante.idAportante() + ")" +
                "\nEstado: " + estadoEntrega +
                "\nCantidad de lotes: " + listaLotes.size();

        if (!listaLotes.isEmpty()) {
            detalle += "\n\nLotes registrados:";
            for (int indice = 0; indice < listaLotes.size(); indice++) {
                detalle += "\n  " + (indice + 1) + ". " + listaLotes.get(indice).toString();
            }
        }

        if (comprobante != null) {
            detalle += "\n\nComprobante generado:" +
                    "\n  Peso total: " + comprobante.pesoTotal() + " kg" +
                    "\n  Valor total reconocido: $" + comprobante.valorTotalReconocido();
        }
        return detalle;
    }
}