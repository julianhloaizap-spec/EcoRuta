package co.edu.uniquindio;

public class Metal extends Lote implements IEtiquetable {

    private TipoMetal tipoMetal;

    public Metal(String idLote, String descripcionLote, int pesoKilogramos, TipoMetal tipoMetal) {
        super(idLote, descripcionLote, pesoKilogramos);
        this.tipoMetal = tipoMetal;
    }

    public Metal(String descripcionLote, int pesoKilogramos, TipoMetal tipoMetal) {
        super(null, descripcionLote, pesoKilogramos);
        this.tipoMetal = tipoMetal;
    }

    @Override
    public double calcularValor() {
        double tarifaPorKilogramo = 0.0;
        if (tipoMetal == TipoMetal.ALUMINIO) {
            tarifaPorKilogramo = 2200.0;
        } else if (tipoMetal == TipoMetal.ACERO) {
            tarifaPorKilogramo = 900.0;
        }
        return getPesoKilogramos() * tarifaPorKilogramo;
    }

    @Override
    public String generarEtiqueta() {
        return "ETIQUETA METAL -> Id Lote: " + getIdLote() + "  Tipo de Metal: " + tipoMetal + "Peso Kg: " + getPesoKilogramos();
    }

    public TipoMetal getTipoMetal() {
        return tipoMetal;
    }

    public void setTipoMetal(TipoMetal tipoMetal) {
        this.tipoMetal = tipoMetal;
    }

    @Override
    public String toString() {
        return "Metal: " + super.toString() + "  Tipo: " + tipoMetal;
    }
}