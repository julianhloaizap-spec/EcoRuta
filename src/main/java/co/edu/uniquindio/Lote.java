package co.edu.uniquindio;

public abstract class Lote {

    private String idLote;
    private String descripcionLote;
    private int pesoKilogramos;

    public Lote(String idLote, String descripcionLote, int pesoKilogramos) {
        this.idLote = idLote;
        this.descripcionLote = descripcionLote;
        this.pesoKilogramos = pesoKilogramos;
    }

    public abstract double calcularValor();

    public String getIdLote() {
        return idLote;
    }

    public void setIdLote(String idLote) {
        this.idLote = idLote;
    }

    public String getDescripcionLote() {
        return descripcionLote;
    }

    public void setDescripcionLote(String descripcionLote) {
        this.descripcionLote = descripcionLote;
    }

    public int getPesoKilogramos() {
        return pesoKilogramos;
    }

    public void setPesoKilogramos(int pesoKilogramos) {
        this.pesoKilogramos = pesoKilogramos;
    }

    @Override
    public String toString() {
        return "\nId Lote: " + idLote +"\n"+
                "Descripcion: " + descripcionLote + "\n"+
                "Peso: " + pesoKilogramos + " kg" +"\n"+
                "Valor: $" + calcularValor();
    }
}