package co.edu.uniquindio;

public record Comprobante(
        String idEntrega,
        String idAportante,
        int pesoTotalKg,
        double valorTotalReconocido
) {
    @Override
    public String toString() {
        return String.format(
                "Comprobante[Entrega=%s, Aportante=%s, PesoTotal=%d kg, ValorTotal=$%,.0f]",
                idEntrega, idAportante, pesoTotalKg, valorTotalReconocido
        );
    }
}
