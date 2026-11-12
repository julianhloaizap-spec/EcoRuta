package co.edu.uniquindio;

public class Metal extends Lote implements Etiquetable {
    private final TipoMetal tipoMetal;

    public Metal(String descripcion, int pesoKg, TipoMetal tipoMetal) {
        super(descripcion, pesoKg);
        this.tipoMetal = tipoMetal;
    }

    public TipoMetal getTipoMetal() {
        return tipoMetal;
    }

    @Override
    public boolean esValido() {
        return super.esValido() && tipoMetal != null;
    }

    @Override
    public Etiquetable comoEtiquetable() {
        return this;
    }

    @Override
    public double calcularValor() {
        if (tipoMetal == null) {
            return 0.0;
        }
        return getPesoKg() * tipoMetal.getTarifaPorKg();
    }

    @Override
    public String generarEtiqueta() {
        return "ETIQUETA METAL | Código Lote: " + getIdLote()
                + " | Tipo de Metal: " + (tipoMetal != null ? tipoMetal.name() : "N/A");
    }

    @Override
    public String toString() {
        return super.toString() + " | Metal: " + (tipoMetal != null ? tipoMetal.name() : "N/A");
    }
}