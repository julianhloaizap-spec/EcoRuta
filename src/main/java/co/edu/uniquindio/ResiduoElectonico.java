package co.edu.uniquindio;

public class ResiduoElectronico extends Lote implements Etiquetable {
    private static final double TARIFA_KG = 1400.0;
    private static final double TARIFA_POR_EQUIPO = 800.0;

    private final int cantidadEquipos;

    public ResiduoElectronico(String descripcion, int pesoKg, int cantidadEquipos) {
        super(descripcion, pesoKg);
        this.cantidadEquipos = cantidadEquipos;
    }

    public int getCantidadEquipos() {
        return cantidadEquipos;
    }

    @Override
    public boolean esValido() {
        return super.esValido() && cantidadEquipos >= 0;
    }

    @Override
    public Etiquetable comoEtiquetable() {
        return this;
    }

    @Override
    public double calcularValor() {
        return (getPesoKg() * TARIFA_KG) + (cantidadEquipos * TARIFA_POR_EQUIPO);
    }

    @Override
    public String generarEtiqueta() {
        return "ETIQUETA ELECTRÓNICO | Código Lote: " + getIdLote()
                + " | Equipos completos: " + cantidadEquipos;
    }

    @Override
    public String toString() {
        return super.toString() + " | Electrónicos (Equipos completos: " + cantidadEquipos + ")";
    }
}