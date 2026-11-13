package co.edu.uniquindio;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class EmpresaTest {

    private static final Logger LOG = Logger.getLogger(EmpresaTest.class.getName());
    private Empresa empresa;

    @DisplayName("Prueba 1: Registro de una entrega")
    @Test
    void testRegistroEntrega() {
        LOG.info("Iniciando prueba Empresa...");

        String datosEntrada = "Datos de entrada: Aportante registrado id '1094001', entrega 'E01'; " +
                "luego entrega repetida 'E01' y entrega 'E02' con aportante desconocido '9999999'.";
        String resultadoEsperado = "Resultado esperado: Entrega 'E01' creada en estado ABIERTA, sin lotes y sin comprobante; " +
                "ambos registros posteriores rechazados y 'E01' conservada.";

        boolean registroExitoso = empresa.registrarEntrega("E01", "2026-11-12", "1094001");
        Entrega entregaRegistrada = empresa.buscarEntregaPorId("E01");

        assertTrue(registroExitoso);
        assertNotNull(entregaRegistrada);
        assertEquals(EstadoEntrega.ABIERTA, entregaRegistrada.getEstadoEntrega());
        assertEquals(0, entregaRegistrada.getListaLotes().size());
        assertNull(entregaRegistrada.getComprobante());

        boolean registroRepetido = empresa.registrarEntrega("E01", "2026-11-12", "1094001");
        boolean registroAportanteDesconocido = empresa.registrarEntrega("E02", "2026-11-12", "9999999");

        assertFalse(registroRepetido);
        assertFalse(registroAportanteDesconocido);
        assertNotNull(empresa.buscarEntregaPorId("E01"));
        assertNull(empresa.buscarEntregaPorId("E02"));
        assertEquals(1, empresa.getListaEntregas().size());

        String resultadoObtenido = "Resultado obtenido: Registro E01=" + registroExitoso +
                ", Estado=" + entregaRegistrada.getEstadoEntrega() +
                ", Lotes=" + entregaRegistrada.getListaLotes().size() +
                ", Comprobante=" + entregaRegistrada.getComprobante() +
                ", Rechazo duplicado=" + (!registroRepetido) +
                ", Rechazo aportante desconocido=" + (!registroAportanteDesconocido);

        LOG.info(datosEntrada);
        LOG.info(resultadoEsperado);
        LOG.info(resultadoObtenido);
        LOG.info(entregaRegistrada.toString());
        LOG.info("Finalizando prueba Empresa...");
    }

    @DisplayName("Prueba 2: Validacion de lotes")
    @Test
    void testValidacionLotes() {
        LOG.info("Iniciando prueba Empresa...");

        String datosEntrada = "Datos de entrada: Entrega abierta 'E01', lote 'L01' con 5 kg; " +
                "luego lote repetido 'L01' con 3 kg y lote 'L02' con 0 kg.";
        String resultadoEsperado = "Resultado esperado: Ambas operaciones rechazadas y la entrega conserva unicamente el primer lote ('L01' de 5 kg).";

        empresa.registrarEntrega("E01", "2026-11-12", "1094001");

        boolean primerLoteAgregado = empresa.agregarLoteAEntrega("E01", new PapelCarton("L01", "Cajas de carton", 5, false));
        boolean segundoLoteRepetido = empresa.agregarLoteAEntrega("E01", new Metal("L01", "Latas de aluminio", 3, TipoMetal.ALUMINIO));
        boolean tercerLotePesoCero = empresa.agregarLoteAEntrega("E01", new Metal("L02", "Varillas de acero", 0, TipoMetal.ACERO));

        Entrega entregaActual = empresa.buscarEntregaPorId("E01");

        assertTrue(primerLoteAgregado);
        assertFalse(segundoLoteRepetido);
        assertFalse(tercerLotePesoCero);
        assertEquals(1, entregaActual.getListaLotes().size());
        assertEquals("L01", entregaActual.getListaLotes().get(0).getIdLote());
        assertEquals(5, entregaActual.getListaLotes().get(0).getPesoKilogramos());

        String resultadoObtenido = "Resultado obtenido: Primer lote=" + primerLoteAgregado +
                ", Lote repetido rechazado=" + (!segundoLoteRepetido) +
                ", Lote peso 0 kg rechazado=" + (!tercerLotePesoCero) +
                ", Cantidad lotes conservados=" + entregaActual.getListaLotes().size();

        LOG.info(datosEntrada);
        LOG.info(resultadoEsperado);
        LOG.info(resultadoObtenido);
        LOG.info(entregaActual.toString());
        LOG.info("Finalizando prueba Empresa...");
    }

    @DisplayName("Prueba 3: Rechazo de una entrega vacia")
    @Test
    void testRechazoEntregaVacia() {
        LOG.info("Iniciando prueba Empresa...");

        String datosEntrada = "Datos de entrada: Entrega 'E01' registrada sin lotes, intento de aceptacion.";
        String resultadoEsperado = "Resultado esperado: Operacion rechazada, estado mantenido en ABIERTA y comprobante null.";

        empresa.registrarEntrega("E01", "2026-11-12", "1094001");
        boolean aceptacionRealizada = empresa.aceptarEntrega("E01");
        Entrega entregaActual = empresa.buscarEntregaPorId("E01");

        assertFalse(aceptacionRealizada);
        assertEquals(EstadoEntrega.ABIERTA, entregaActual.getEstadoEntrega());
        assertNotEquals(EstadoEntrega.ACEPTADA, entregaActual.getEstadoEntrega());
        assertNull(entregaActual.getComprobante());
        assertEquals(0.0, empresa.consultarValorAPagarPorAportante("1094001"));

        String resultadoObtenido = "Resultado obtenido: Aceptacion=" + aceptacionRealizada +
                ", Estado=" + entregaActual.getEstadoEntrega() +
                ", Comprobante=" + entregaActual.getComprobante();

        LOG.info(datosEntrada);
        LOG.info(resultadoEsperado);
        LOG.info(resultadoObtenido);
        LOG.info(entregaActual.toString());
        LOG.info("Finalizando prueba Empresa...");
    }

    @DisplayName("Prueba 4: Aceptacion y calculo con los tres tipos de lote")
    @Test
    void testAceptacionYCalculoTresTiposLote() {
        LOG.info("Iniciando prueba Empresa...");

        String datosEntrada = "Datos de entrada: Entrega 'E01' con 10 kg PapelCarton clasificado, " +
                "5 kg Metal ALUMINIO y 4 kg ResiduoElectronico con 2 equipos completos.";
        String resultadoEsperado = "Resultado esperado: Estado ACEPTADA, comprobante con 19 kg y $24.200 " +
                "(valores por lote: $6.000, $11.000 y $7.200).";

        empresa.registrarEntrega("E01", "2026-11-12", "1094001");

        Lote lotePapelCarton = new PapelCarton("LP01", "Archivo clasificado", 10, true);
        Lote loteMetal = new Metal("LM01", "Perfiles de aluminio", 5, TipoMetal.ALUMINIO);
        Lote loteElectronico = new ResiduoElectronico("LE01", "Computadores completos", 4, 2);

        double valorPapel = lotePapelCarton.calcularValor();
        double valorMetal = loteMetal.calcularValor();
        double valorElectronico = loteElectronico.calcularValor();

        assertEquals(6000.0, valorPapel);
        assertEquals(11000.0, valorMetal);
        assertEquals(7200.0, valorElectronico);

        empresa.agregarLoteAEntrega("E01", lotePapelCarton);
        empresa.agregarLoteAEntrega("E01", loteMetal);
        empresa.agregarLoteAEntrega("E01", loteElectronico);

        boolean aceptacionRealizada = empresa.aceptarEntrega("E01");
        Entrega entregaActual = empresa.buscarEntregaPorId("E01");
        Comprobante comprobanteGenerado = empresa.consultarComprobantePorEntrega("E01");

        assertTrue(aceptacionRealizada);
        assertEquals(EstadoEntrega.ACEPTADA, entregaActual.getEstadoEntrega());
        assertNotNull(comprobanteGenerado);
        assertEquals(19, comprobanteGenerado.pesoTotal());
        assertEquals(24200.0, comprobanteGenerado.valorTotalReconocido());
        assertEquals(24200.0, empresa.consultarValorAPagarPorAportante("1094001"));

        String resultadoObtenido = "Resultado obtenido: Estado=" + entregaActual.getEstadoEntrega() +
                ", Peso total=" + comprobanteGenerado.pesoTotal() + " kg" +
                ", Valor total=$" + comprobanteGenerado.valorTotalReconocido() +
                " (Papel=$" + valorPapel + ", Metal=$" + valorMetal + ", Electronico=$" + valorElectronico + ")";

        LOG.info(datosEntrada);
        LOG.info(resultadoEsperado);
        LOG.info(resultadoObtenido);
        LOG.info(comprobanteGenerado.toString());
        LOG.info("Finalizando prueba Empresa...");
    }

    @DisplayName("Prueba 5: Consulta de etiquetas")
    @Test
    void testConsultaEtiquetas() {
        LOG.info("Iniciando prueba Empresa...");

        String datosEntrada = "Datos de entrada: Consulta de etiquetas de entrega 'E01' aceptada con PapelCarton ('LP01'), " +
                "Metal ALUMINIO ('LM01') y ResiduoElectronico ('LE01', 2 equipos completos).";
        String resultadoEsperado = "Resultado esperado: Exactamente 2 etiquetas; etiqueta de metal con 'LM01' y 'ALUMINIO', " +
                "etiqueta electronica con 'LE01' y '2'.";

        empresa.registrarEntrega("E01", "2026-11-12", "1094001");
        empresa.agregarLoteAEntrega("E01", new PapelCarton("LP01", "Archivo clasificado", 10, true));
        empresa.agregarLoteAEntrega("E01", new Metal("LM01", "Perfiles de aluminio", 5, TipoMetal.ALUMINIO));
        empresa.agregarLoteAEntrega("E01", new ResiduoElectronico("LE01", "Computadores completos", 4, 2));
        empresa.aceptarEntrega("E01");

        ArrayList<String> etiquetasObtenidas = empresa.consultarEtiquetasPorEntrega("E01");

        assertEquals(2, etiquetasObtenidas.size());

        String etiquetaMetal = etiquetasObtenidas.get(0);
        String etiquetaElectronico = etiquetasObtenidas.get(1);

        assertTrue(etiquetaMetal.contains("LM01"));
        assertTrue(etiquetaMetal.contains("ALUMINIO"));
        assertFalse(etiquetaMetal.contains("LP01"));

        assertTrue(etiquetaElectronico.contains("LE01"));
        assertTrue(etiquetaElectronico.contains("2"));

        String resultadoObtenido = "Resultado obtenido: Cantidad etiquetas=" + etiquetasObtenidas.size() +
                " -> [1] " + etiquetaMetal + " | [2] " + etiquetaElectronico;

        LOG.info(datosEntrada);
        LOG.info(resultadoEsperado);
        LOG.info(resultadoObtenido);
        LOG.info(etiquetasObtenidas.toString());
        LOG.info("Finalizando prueba Empresa...");
    }

    @BeforeEach
    void ejecutarAntes() {
        LOG.warning("Ejecucion del BeforeEach");
        empresa = new Empresa("123456789", "EcoRuta");
        empresa.cargarAportantesIniciales();

    }

    @AfterEach
    void ejecutarDespues() {
        LOG.warning("Ejecucion del AfterEach");
    }
}