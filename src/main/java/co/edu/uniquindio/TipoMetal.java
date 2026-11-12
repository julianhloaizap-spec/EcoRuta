package co.edu.uniquindio;

public enum TipoMetal {
    ALUMINIO(2200.0),
    ACERO(900.0);

    private final double tarifaPorKg;

    TipoMetal(double tarifaPorKg) {
        this.tarifaPorKg = tarifaPorKg;
    }

    public double getTarifaPorKg() {
        return tarifaPorKg;
    }
}