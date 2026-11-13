package co.edu.uniquindio;

public record Comprobante(
        String idEntrega,
        String idAportante,
        int pesoTotal,
        double valorTotalReconocido
) {
}