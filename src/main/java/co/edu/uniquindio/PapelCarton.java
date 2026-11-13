package co.edu.uniquindio;

public class PapelCarton extends Lote {

    private boolean clasificado;

    public PapelCarton(String idLote, String descripcionLote, int pesoKilogramos, boolean clasificado) {
        super(idLote, descripcionLote, pesoKilogramos);
        this.clasificado = clasificado;
    }

    public PapelCarton(String descripcionLote, int pesoKilogramos, boolean clasificado) {
        super(null, descripcionLote, pesoKilogramos);
        this.clasificado = clasificado;
    }

    @Override
    public double calcularValor() {
        double tarifaPorKilogramo = 500.0;
        if (clasificado) {
            tarifaPorKilogramo += 100.0;
        }
        return getPesoKilogramos() * tarifaPorKilogramo;
    }

    public boolean isClasificado() {
        return clasificado;
    }

    public void setClasificado(boolean clasificado) {
        this.clasificado = clasificado;
    }

    @Override
    public String toString() {
        String textoClasificado = clasificado ? "Si" : "No";
        return "Papel y Carton" + super.toString() + " \nClasificado: " + textoClasificado;
    }
}