package co.edu.uniquindio;

import java.util.ArrayList;

public class Empresa {

    private String nitEmpresa;
    private String nombreEmpresa;
    private ArrayList<Aportante> listaAportantes;
    private ArrayList<Entrega> listaEntregas;
    private int contadorEntregas;

    public Empresa(String nitEmpresa, String nombreEmpresa) {
        this.nitEmpresa = nitEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.listaAportantes = new ArrayList<Aportante>();
        this.listaEntregas = new ArrayList<Entrega>();
        this.contadorEntregas = 1;
    }

    public void cargarAportantesIniciales() {
        registrarAportante(new Aportante("1094001", "Julian Loaiza", "julian.loaiza@hotmail.com"));
        registrarAportante(new Aportante("1094002", "Yair Yampuezan", "yair.yampuezan@gmail.com"));
        registrarAportante(new Aportante("1094003", "Mariana Mendez", "mariana.mendez@uqvirtual.edu.co"));
    }

    private String generarIdEntregaAutomatico() {
        String idGenerado = "";
        if (contadorEntregas < 10) {
            idGenerado = "E0" + contadorEntregas;
        } else {
            idGenerado = "E" + contadorEntregas;
        }
        contadorEntregas++;
        return idGenerado;
    }

    public boolean registrarAportante(Aportante nuevoAportante) {
        if (nuevoAportante == null) {
            return false;
        }
        if (nuevoAportante.idAportante() == null || nuevoAportante.idAportante().trim().isEmpty()) {
            return false;
        }
        if (buscarAportantePorId(nuevoAportante.idAportante()) != null) {
            return false;
        }
        listaAportantes.add(nuevoAportante);
        return true;
    }

    public Aportante buscarAportantePorId(String idBuscado) {
        if (idBuscado == null) {
            return null;
        }
        for (int indice = 0; indice < listaAportantes.size(); indice++) {
            Aportante aportanteActual = listaAportantes.get(indice);
            if (aportanteActual.idAportante().equalsIgnoreCase(idBuscado.trim())) {
                return aportanteActual;
            }
        }
        return null;
    }

    public Entrega registrarEntregaAutomatica(String fechaEntrega, String idAportante) {
        Aportante aportanteEncontrado = buscarAportantePorId(idAportante);
        if (aportanteEncontrado == null) {
            return null;
        }
        String idGenerado = generarIdEntregaAutomatico();
        while (buscarEntregaPorId(idGenerado) != null) {
            idGenerado = generarIdEntregaAutomatico();
        }
        Entrega nuevaEntrega = new Entrega(idGenerado, fechaEntrega, aportanteEncontrado);
        listaEntregas.add(nuevaEntrega);
        return nuevaEntrega;
    }

    public boolean registrarEntrega(String idEntrega, String fechaEntrega, String idAportante) {
        if (idEntrega == null || idEntrega.trim().isEmpty()) {
            return false;
        }
        if (buscarEntregaPorId(idEntrega) != null) {
            return false;
        }
        Aportante aportanteEncontrado = buscarAportantePorId(idAportante);
        if (aportanteEncontrado == null) {
            return false;
        }
        Entrega nuevaEntrega = new Entrega(idEntrega.trim(), fechaEntrega, aportanteEncontrado);
        listaEntregas.add(nuevaEntrega);
        return true;
    }

    public Entrega buscarEntregaPorId(String idEntregaBuscado) {
        if (idEntregaBuscado == null) {
            return null;
        }
        for (int indice = 0; indice < listaEntregas.size(); indice++) {
            Entrega entregaActual = listaEntregas.get(indice);
            if (entregaActual.getIdEntrega().equalsIgnoreCase(idEntregaBuscado.trim())) {
                return entregaActual;
            }
        }
        return null;
    }

    public boolean agregarLoteAEntrega(String idEntrega, Lote nuevoLote) {
        Entrega entregaEncontrada = buscarEntregaPorId(idEntrega);
        if (entregaEncontrada == null) {
            return false;
        }
        return entregaEncontrada.agregarLote(nuevoLote);
    }

    public boolean aceptarEntrega(String idEntrega) {
        Entrega entregaEncontrada = buscarEntregaPorId(idEntrega);
        if (entregaEncontrada == null) {
            return false;
        }
        return entregaEncontrada.aceptarEntrega();
    }

    public Comprobante consultarComprobantePorEntrega(String idEntrega) {
        Entrega entregaEncontrada = buscarEntregaPorId(idEntrega);
        if (entregaEncontrada == null) {
            return null;
        }
        return entregaEncontrada.getComprobante();
    }

    public ArrayList<String> consultarEtiquetasPorEntrega(String idEntrega) {
        Entrega entregaEncontrada = buscarEntregaPorId(idEntrega);
        if (entregaEncontrada == null) {
            return new ArrayList<String>();
        }
        return entregaEncontrada.obtenerEtiquetas();
    }

    public double consultarValorAPagarPorAportante(String idAportante) {
        if (idAportante == null || idAportante.trim().isEmpty()) {
            return 0.0;
        }
        double totalAcumulado = 0.0;
        for (int indice = 0; indice < listaEntregas.size(); indice++) {
            Entrega entregaActual = listaEntregas.get(indice);
            if (entregaActual.getEstadoEntrega() == EstadoEntrega.ACEPTADA && entregaActual.getComprobante() != null) {
                Comprobante comprobanteActual = entregaActual.getComprobante();
                if (comprobanteActual.idAportante().equalsIgnoreCase(idAportante.trim())) {
                    totalAcumulado += comprobanteActual.valorTotalReconocido();
                }
            }
        }
        return totalAcumulado;
    }

    public String getNitEmpresa() {
        return nitEmpresa;
    }

    public void setNitEmpresa(String nitEmpresa) {
        this.nitEmpresa = nitEmpresa;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public ArrayList<Aportante> getListaAportantes() {
        return listaAportantes;
    }

    public ArrayList<Entrega> getListaEntregas() {
        return listaEntregas;
    }
}