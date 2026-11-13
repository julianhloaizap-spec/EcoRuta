package co.edu.uniquindio;

public class ResiduoElectronico extends Lote implements IEtiquetable {

    private int cantidadEquiposCompletos;

    public ResiduoElectronico(String idLote, String descripcionLote, int pesoKilogramos, int cantidadEquiposCompletos) {
        super(idLote, descripcionLote, pesoKilogramos);
        this.cantidadEquiposCompletos = cantidadEquiposCompletos;
    }

    public ResiduoElectronico(String descripcionLote, int pesoKilogramos, int cantidadEquiposCompletos) {
        super(null, descripcionLote, pesoKilogramos);
        this.cantidadEquiposCompletos = cantidadEquiposCompletos;
    }

    @Override
    public double calcularValor() {
        double valorPorPeso = getPesoKilogramos() * 1400.0;
        double valorPorEquipos = cantidadEquiposCompletos * 800.0;
        return valorPorPeso + valorPorEquipos;
    }

    @Override
    public String generarEtiqueta() {
        return "ETIQUETA ELECTRONICO -> Id Lote: " + getIdLote() +
                "  Equipos completos: " + cantidadEquiposCompletos + "  Peso Kg: " + getPesoKilogramos();
    }

    public int getCantidadEquiposCompletos() {
        return cantidadEquiposCompletos;
    }

    public void setCantidadEquiposCompletos(int cantidadEquiposCompletos) {
        this.cantidadEquiposCompletos = cantidadEquiposCompletos;
    }

    @Override
    public String toString() {
        return "Residuo Electronico: " + super.toString() + "  Equipos completos: " + cantidadEquiposCompletos;
    }
}