package co.edu.uniquindio;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmpresaTest {

    private Empresa sistema;

    @BeforeEach
    void setUp() {
        Entrega.reiniciarContador();
        Lote.reiniciarContador();

        sistema = new Empresa();
        sistema.cargarAportantesIniciales(); // Registra documentos: "1001", "2002", "3003"
    }

    @Test
    @DisplayName("1. Registro de entrega con ID automático y rechazo de documento desconocido")
    void prueba1_RegistroDeUnaEntrega() {
        // Datos de entrada
        String docAportanteValido = "1001";
        String docAportanteDesconocido = "9999";
        LocalDate fecha = LocalDate.of(2026, 11, 11);

        // Ejecución
        Entrega entregaE01 = sistema.registrarEntrega(fecha, docAportanteValido);
        Entrega intentoAportanteDesconocido = sistema.registrarEntrega(fecha, docAportanteDesconocido);
        Entrega entregaE02 = sistema.registrarEntrega(fecha, docAportanteValido);
        Entrega e01Conservada = sistema.buscarEntrega("E01");

        // Registro de evidencia en consola
        registrarEvidencia(
                "PRUEBA 1: Registro de una entrega",
                "Aportante documento '1001', crear entrega (auto 'E01'); probar documento desconocido '9999' y otra entrega (auto 'E02').",
                "'E01' en estado ABIERTA, 0 lotes, sin comprobante (null); desconocido rechazado (null); siguiente entrega única 'E02'; 'E01' conservada.",
                "CodigoGenerado1=" + entregaE01.getCodigo()
                        + ", Estado=" + entregaE01.getEstado()
                        + ", Lotes=" + entregaE01.getLotes().size()
                        + ", Comprobante=" + entregaE01.getComprobante()
                        + ", RechazoDesconocido=" + (intentoAportanteDesconocido == null)
                        + ", CodigoGenerado2=" + entregaE02.getCodigo()
                        + ", E01Conservada=" + (e01Conservada != null)
        );

        // Aserciones JUnit
        assertNotNull(entregaE01, "La primera entrega debió crearse correctamente.");
        assertEquals("E01", entregaE01.getCodigo(), "El código generado automáticamente debe ser E01.");
        assertEquals(EstadoEntrega.ABIERTA, entregaE01.getEstado(), "La entrega debe iniciar en estado ABIERTA.");
        assertTrue(entregaE01.getLotes().isEmpty(), "La entrega debe iniciar sin lotes.");
        assertNull(entregaE01.getComprobante(), "La entrega abierta no debe tener comprobante.");

        assertNull(intentoAportanteDesconocido, "Debe rechazarse una entrega con documento desconocido.");
        assertNotNull(entregaE02);
        assertEquals("E02", entregaE02.getCodigo(), "La segunda entrega debe generar un código único automático (E02).");
        assertSame(entregaE01, e01Conservada, "La entrega E01 original debe conservarse intacta.");
    }

    @Test
    @DisplayName("2. Validación de lotes: rechazo de código duplicado y peso cero")
    void prueba2_ValidacionDeLotes() {
        // Datos de entrada
        sistema.registrarEntrega(LocalDate.of(2026, 11, 11), "1001"); // E01
        Lote loteValido = new PapelCarton("Cartón corrugado", 5, false); // L01
        Lote lotePesoCero = new PapelCarton("Papel periódico", 0, false); // L02 (inválido por peso 0)

        // Ejecución
        boolean agregadoL01 = sistema.agregarLoteAEntrega("E01", loteValido);
        boolean agregadoCodigoRepetido = sistema.agregarLoteAEntrega("E01", loteValido); // Mismo código L01
        boolean agregadoPesoCero = sistema.agregarLoteAEntrega("E01", lotePesoCero);
        Entrega e01 = sistema.buscarEntrega("E01");

        // Registro de evidencia en consola
        registrarEvidencia(
                "PRUEBA 2: Validación de lotes",
                "En entrega 'E01', agregar lote auto-generado 'L01' (5 kg); intentar repetir 'L01' y agregar 'L02' con 0 kg.",
                "Se acepta 'L01' (5 kg), se rechazan el código repetido y el peso 0 kg (false), y se conserva únicamente 'L01'.",
                "AgregadoL01=" + agregadoL01
                        + ", RechazoRepetido=" + (!agregadoCodigoRepetido)
                        + ", RechazoPesoCero=" + (!agregadoPesoCero)
                        + ", LotesConservados=" + e01.getLotes().size()
                        + " (" + e01.getLotes().get(0).getIdLote() + ", " + e01.getLotes().get(0).getPesoKg() + " kg)"
        );

        // Aserciones JUnit
        assertTrue(agregadoL01, "El primer lote L01 con 5 kg debe agregarse.");
        assertFalse(agregadoCodigoRepetido, "Debe rechazarse un lote con código L01 ya existente en la entrega.");
        assertFalse(agregadoPesoCero, "Debe rechazarse un lote con peso de 0 kg.");

        assertEquals(1, e01.getLotes().size(), "La entrega debe conservar únicamente el primer lote.");
        assertEquals("L01", e01.getLotes().get(0).getIdLote());
        assertEquals(5, e01.getLotes().get(0).getPesoKg());
    }

    @Test
    @DisplayName("3. Rechazo de aceptación de una entrega vacía")
    void prueba3_RechazoEntregaVacia() {
        // Datos de entrada
        Entrega entregaVacia = sistema.registrarEntrega(LocalDate.of(2026, 11, 11), "1001"); // E01

        // Ejecución
        Comprobante comprobante = sistema.aceptarEntrega(entregaVacia.getCodigo());

        // Registro de evidencia en consola
        registrarEvidencia(
                "PRUEBA 3: Rechazo de una entrega vacía",
                "Entrega '" + entregaVacia.getCodigo() + "' registrada sin lotes; invocar aceptarEntrega.",
                "Operación rechazada (null), estado permanece ABIERTA y sin comprobante (null).",
                "RechazoVacia=" + (comprobante == null)
                        + ", Estado=" + entregaVacia.getEstado()
                        + ", Comprobante=" + entregaVacia.getComprobante()
        );

        // Aserciones JUnit
        assertNull(comprobante, "Aceptar una entrega sin lotes debe retornar null.");
        assertEquals(EstadoEntrega.ABIERTA, entregaVacia.getEstado(), "La entrega debe permanecer en estado ABIERTA.");
        assertNull(entregaVacia.getComprobante(), "La entrega debe quedar sin comprobante.");
    }

    @Test
    @DisplayName("4. Aceptación y cálculo polimórfico con los tres tipos de lote")
    void prueba4_AceptacionYCalculoTresTiposDeLote() {
        // Datos de entrada
        Entrega entrega = sistema.registrarEntrega(LocalDate.of(2026, 11, 11), "2002"); // E01
        Lote lotePapel = new PapelCarton("Papel y cartón clasificado", 10, true); // L01
        Lote loteMetal = new Metal("Perfiles de aluminio", 5, TipoMetal.ALUMINIO); // L02
        Lote loteElectronico = new ResiduoElectronico("Computadores portátiles", 4, 2); // L03

        sistema.agregarLoteAEntrega(entrega.getCodigo(), lotePapel);
        sistema.agregarLoteAEntrega(entrega.getCodigo(), loteMetal);
        sistema.agregarLoteAEntrega(entrega.getCodigo(), loteElectronico);

        // Ejecución
        Comprobante comprobante = sistema.aceptarEntrega(entrega.getCodigo());
        Entrega e01 = sistema.buscarEntrega(entrega.getCodigo());

        // Registro de evidencia en consola
        registrarEvidencia(
                "PRUEBA 4: Aceptación y cálculo con los tres tipos de lote",
                "Entrega 'E01' (Aportante doc '2002'): 10 kg papel clasificado (L01), 5 kg aluminio (L02), 4 kg electrónicos con 2 equipos (L03).",
                "Valores por lote: $6.000, $11.000 y $7.200; Estado=ACEPTADA; Comprobante: 19 kg y $24.200.",
                String.format("ValoresLotes=[$%,.0f, $%,.0f, $%,.0f], Estado=%s, PesoTotal=%d kg, ValorTotal=$%,.0f",
                        lotePapel.calcularValor(), loteMetal.calcularValor(), loteElectronico.calcularValor(),
                        e01.getEstado(), comprobante.pesoTotalKg(), comprobante.valorTotalReconocido())
        );

        // Aserciones JUnit
        assertEquals(6000.0, lotePapel.calcularValor(), 0.001, "Papel clasificado (10 kg) debe valer $6.000.");
        assertEquals(11000.0, loteMetal.calcularValor(), 0.001, "Aluminio (5 kg) debe valer $11.000.");
        assertEquals(7200.0, loteElectronico.calcularValor(), 0.001, "Electrónicos (4 kg + 2 equipos) debe valer $7.200.");

        assertEquals(EstadoEntrega.ACEPTADA, e01.getEstado(), "La entrega debe pasar a estado ACEPTADA.");
        assertNotNull(comprobante, "Debe generarse un comprobante al aceptar la entrega.");
        assertEquals("E01", comprobante.idEntrega());
        assertEquals("2002", comprobante.idAportante());
        assertEquals(19, comprobante.pesoTotalKg(), "El peso total en el comprobante debe ser 19 kg.");
        assertEquals(24200.0, comprobante.valorTotalReconocido(), 0.001, "El valor total reconocido debe ser $24.200.");
    }

    @Test
    @DisplayName("5. Consulta de etiquetas de una entrega aceptada")
    void prueba5_ConsultaDeEtiquetas() {
        // Datos de entrada
        Entrega entrega = sistema.registrarEntrega(LocalDate.of(2026, 11, 11), "2002"); // E01
        sistema.agregarLoteAEntrega(entrega.getCodigo(), new PapelCarton("Papel y cartón clasificado", 10, true)); // L01
        sistema.agregarLoteAEntrega(entrega.getCodigo(), new Metal("Perfiles de aluminio", 5, TipoMetal.ALUMINIO)); // L02
        sistema.agregarLoteAEntrega(entrega.getCodigo(), new ResiduoElectronico("Computadores portátiles", 4, 2)); // L03
        sistema.aceptarEntrega(entrega.getCodigo());

        // Ejecución
        List<String> etiquetas = sistema.consultarEtiquetas(entrega.getCodigo());

        // Registro de evidencia en consola
        registrarEvidencia(
                "PRUEBA 5: Consulta de etiquetas",
                "Consultar etiquetas de la entrega aceptada 'E01' (papel L01, aluminio L02 y electrónicos L03 con 2 equipos).",
                "Exactamente 2 etiquetas: metal con 'L02' y 'ALUMINIO'; electrónico con 'L03' y '2'.",
                "CantidadEtiquetas=" + etiquetas.size() + " -> " + etiquetas
        );

        // Aserciones JUnit
        assertNotNull(etiquetas, "La lista de etiquetas no debe ser nula para una entrega aceptada.");
        assertEquals(2, etiquetas.size(), "Deben generarse exactamente 2 etiquetas (papel y cartón no lleva etiqueta).");

        String etiquetaMetal = etiquetas.get(0);
        String etiquetaElectronico = etiquetas.get(1);

        assertTrue(etiquetaMetal.contains("L02") && etiquetaMetal.contains("ALUMINIO"),
                "La etiqueta de metal debe incluir el código L02 y el tipo ALUMINIO.");
        assertTrue(etiquetaElectronico.contains("L03") && etiquetaElectronico.contains("2"),
                "La etiqueta electrónica debe incluir el código L03 y la cantidad de equipos completos (2).");
    }

    private void registrarEvidencia(String titulo, String entrada, String esperado, String obtenido) {
        System.out.println("=================================================================");
        System.out.println(titulo);
        System.out.println("  * Datos de entrada  : " + entrada);
        System.out.println("  * Resultado esperado: " + esperado);
        System.out.println("  * Resultado obtenido: " + obtenido);
        System.out.println("=================================================================\n");
    }
}