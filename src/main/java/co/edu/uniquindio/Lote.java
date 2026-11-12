package co.edu.uniquindio;

public abstract class Lote {
    private static int contador = 1;

    private final String idLote;
    private final String descripcion;
    private final int pesoKg;

    protected Lote(String descripcion, int pesoKg) {
        this.idLote = generarSiguienteCodigo();
        this.descripcion = (descripcion != null) ? descripcion.trim() : "";
        this.pesoKg = pesoKg;
    }

    private static String generarSiguienteCodigo() {
        return String.format("L%02d", contador++);
    }

    public static void reiniciarContador() {
        contador = 1;
    }

    public String getIdLote() {
        return idLote;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getPesoKg() {
        return pesoKg;
    }

    public boolean esValido() {
        return !idLote.isBlank() && !descripcion.isBlank() && pesoKg > 0;
    }

    public Etiquetable comoEtiquetable() {
        return null;
    }

    public abstract double calcularValor();

    @Override
    public String toString() {
        return String.format("[%s] %s (%d kg) -> Valor: $%,.0f",
                idLote, descripcion, pesoKg, calcularValor());
    }
}