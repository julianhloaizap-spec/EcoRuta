package co.edu.uniquindio;

public class PapelCarton extends Lote {
    private static final double TARIFA_BASE_KG = 500.0;
    private static final double BONO_CLASIFICADO_KG = 100.0;

    private final boolean clasificado;

    public PapelCarton(String descripcion, int pesoKg, boolean clasificado) {
        super(descripcion, pesoKg);
        this.clasificado = clasificado;
    }

    public boolean isClasificado() {
        return clasificado;
    }

    @Override
    public double calcularValor() {
        double tarifaFinal = TARIFA_BASE_KG + (clasificado ? BONO_CLASIFICADO_KG : 0.0);
        return getPesoKg() * tarifaFinal;
    }

    @Override
    public String toString() {
        return super.toString() + " | Papel/Cartón (Clasificado: " + (clasificado ? "Sí" : "No") + ")";
    }
}