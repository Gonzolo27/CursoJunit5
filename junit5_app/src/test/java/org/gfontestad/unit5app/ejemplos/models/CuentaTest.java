package org.gfontestad.unit5app.ejemplos.models;

import org.gfontestad.unit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Al poner esto, podemos quitar el static de los métodos de before y after all, se crea una sola referencia del test
class CuentaTest {

    String nombre = "Gonzalo Fontestad";
    BigDecimal saldo = new BigDecimal("1000.12345");
    Cuenta cuenta;
    TestInfo testInfo;
    TestReporter testReporter;

    @BeforeAll
//    static void beforeAll() {
    void beforeAll() {
        System.out.println("Inicializando los tests");
    }

    @BeforeEach
    void initMethodTest(TestInfo testInfo, TestReporter testReporter) {
        testReporter.publishEntry("Ejecutando " + testInfo.getTestMethod().get().getName() + " " + testInfo.getDisplayName() + " con las tags: " +
                testInfo.getTags());
        this.cuenta = new Cuenta(nombre, saldo);
        this.testInfo = testInfo;
        this.testReporter = testReporter;
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el test");
    }

    @AfterAll
//   static void afterAll() {
    void afterAll() {
        System.out.println("Tests finalizados");
    }


    @Tag("cuenta")
    @Tag("error")
    @Disabled
    @Test
    void dineroInsuficienteException() {
        fail("Error simulado"); //Simulamos un error
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1500"));
        });
        String real = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, real);
    }


    @Test
    void testRelacionBancoCuentas() {
        Banco banco = new Banco("BBVA");

        Cuenta cuenta1 = new Cuenta(nombre, new BigDecimal("1500"));
        Cuenta cuenta2 = new Cuenta("John Papa", new BigDecimal("2500"));

        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

        assertAll(
                () -> {
                    assertEquals(2, banco.getCuentas().size(), () -> "El banco no tiene las cuentas esperadas: " );
                },
                () -> {
                    assertEquals("BBVA", cuenta1.getBanco().getNombre(), () -> "El nombre del banco no es el esperado");
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getNombrePersona().equals("John Papa")));
                });


        //3 maneras de comprobar que un elemento está en una lista:

        //### 1 ###
        /*assertEquals("John Papa", banco.getCuentas().stream()
                .filter(c -> c.getNombrePersona().equals("John Papa"))
                .findFirst()
                .get()
                .getNombrePersona());*/

        //### 2 ###
        /*assertTrue(banco.getCuentas().stream()
                .filter(c -> c.getNombrePersona().equals("John Papa"))
                .findFirst().isPresent());*/

        //### 3 ###
        /*assertTrue(banco.getCuentas().stream()
                .anyMatch(c -> c.getNombrePersona().equals("John Papa")));*/
    }

    @Test
    void testSaldoCuentaDev() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(isDev); //Si isDev == false, el test se deshabilita

        assertNotNull(cuenta.getSaldo(), () ->"El saldo de la cuenta es nulo");
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
    }

    @Test
    void testSaldoCuentaDev2() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(isDev, () -> {
            assertNotNull(cuenta.getSaldo(), () ->"El saldo de la cuenta es nulo");
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        }); //Si isDev == false, no se ejecutará el código de la expresión lamda pero el test continúa habilitado
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("Probando Nombre y saldos cuentas")
    class CuentaTestNombreYSaldo {
        @Test
        @DisplayName("Test nombre de la cuenta")
        void testNombreCuenta() {

            String esperado = nombre;
            String real = cuenta.getNombrePersona();

            assertNotNull(real, () ->"La cuenta no puede ser nula"); //Al usar una expresión lamda, ese string no se instanciará a no ser que falle, por tanto es más eficiente que pasar simplemente el string
            assertEquals(esperado, real, () ->"El nombre de la cuenta no era el esperado");
        }

        @Test
        void testSaldoCuenta() {
            assertNotNull(cuenta.getSaldo(), () ->"El saldo de la cuenta es nulo");
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        }

        @Test
        void testReferenciaCuenta() {
            Cuenta john_papa = new Cuenta("John Papa", new BigDecimal("8900.9997"));
            Cuenta john_papaFrita = new Cuenta("John Papa", new BigDecimal("8900.9997"));

            assertEquals(john_papaFrita, john_papa);
        }
    }

    @Nested
    @DisplayName("Probando operaciones cuentas")
    class CuentaOperacionesTest {
        @Tag("cuenta")
        @Test
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal("100"));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals(900.12345, cuenta.getSaldo().doubleValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal("100"));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals(1100.12345, cuenta.getSaldo().doubleValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("banco")
        @Tag("cuenta")
        @Test
        void testTransferirDineroCuentas() {
            Banco banco = new Banco("BBVA");

            Cuenta cuenta1 = new Cuenta(nombre, new BigDecimal("1500"));
            Cuenta cuenta2 = new Cuenta("John Papa", new BigDecimal("2500"));

            banco.transferir(cuenta2, cuenta1, new BigDecimal("500"));

            assertEquals("2000", cuenta1.getSaldo().toPlainString());
            assertEquals("2000", cuenta2.getSaldo().toPlainString());
        }
    }

    @DisplayName("Probando debito cuenta en repeticion")
    @RepeatedTest(value = 3, name = "{displayName}: Repeticion numero: {currentRepetition} de {totalRepetitions}")
    void testDebitoCuenta(RepetitionInfo info) {
        if(info.getCurrentRepetition() == 2) {
            System.out.println("HOLA");
        }
        cuenta.debito(new BigDecimal("100"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals(900.12345, cuenta.getSaldo().doubleValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("params")
    @Nested
    class PruebasParametrizadasTest {
        @DisplayName("Probando debito cuenta parametrizado")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.12345", "1100"})
        void testDebitoCuentaValueSource(String monto) {

            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @DisplayName("Probando debito cuenta parametrizado")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345", "7,1100"})
        void testDebitoCuentaCsvSource(String index, String monto) {

            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @DisplayName("Probando debito cuenta parametrizado")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,John,Gonzalo Fontestad", "250,200,Pepe,Pepe", "300,300,Pepe,Pepe", "500,500,Pepe,Pepe", "750,700,Pepe,Pepe", "1000.12345,1000.12345,Pepe,Pepe"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {

            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.setNombrePersona(actual);
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getNombrePersona());

            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Probando debito cuenta parametrizado")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {

            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        @DisplayName("Probando debito cuenta parametrizado")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {

            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.setNombrePersona(actual);
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getNombrePersona());

            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @DisplayName("Probando debito cuenta parametrizado")
        @ParameterizedTest(name = "Numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @MethodSource("montoListMethod")
        void testDebitoCuentaMethodSource(String monto) {

            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) >= 0);
        }

        private static List<String> montoListMethod() {
            return Arrays.asList("100", "200", "300", "500", "700", "1000.12345", "1100");
        }
    }

    @Tag("timeout")
    @Nested
    class timeoutTests {
        @Test
        @Timeout(5) //segundos
        void testTimeOut() throws InterruptedException {
            TimeUnit.SECONDS.sleep(6);
        }

        @Test
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS) //segundos
        void testTimeOut2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        void testTimeOutAssertions() throws InterruptedException {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.SECONDS.sleep(6);
            });
        }
    }

    @Nested
    @DisplayName("Probando variables de ambiente")
    class VariableAmbienteTest {
        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach( (key, value)-> {
                System.out.println(key + ": " + value);
            });
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-18.0.2.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "16")
        void testProcessors() {
        }
    }

    @Nested
    @DisplayName("Probando propiedades del sistema")
    class SystemPropertiesTest {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach( (key, value) -> {
                System.out.println(key + ": " + value);
            });
        }

        @Test
        //@EnabledIfSystemProperty(named = "java.version", matches = "18.0.1") //No lo ejecuta
        @EnabledIfSystemProperty(named = "java.version", matches = ".*18.*")   //Sí lo ejecuta
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testArquitecturaNO32() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testArquitectura32() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev") //Por sí sola, ENV no existe, hay que añadirla en Edit configurations de la clase CuentaTest
            //Donde pone -ea añadimos -DENV ese -D indica que vamos a configurar un system property
            //También en esa ventana se pueden crear variables de entorno tal que así: EnvName=EnvValue
        void testDev() {
        }
    }

    @Nested
    @DisplayName("Probando versiones de java")
    class JavaVersionTest {
        @EnabledOnJre(JRE.JAVA_18)
        @Test
        void testJava18() { }

        @EnabledOnJre(JRE.JAVA_8)
        @Test
        void testJava8() { }

        @DisabledOnJre(JRE.JAVA_18)
        @Test
        void testNoJava18() { }
    }

    @Nested
    @DisplayName("Probando sistemas operativos")
    class SistemaOperativoTest {
        @EnabledOnOs(OS.WINDOWS)
        @Test
        void testSoloWindows() { }

        @EnabledOnOs({OS.MAC, OS.LINUX})
        @Test
        void testSoloMacLinux() { }

        @DisabledOnOs(OS.WINDOWS)
        @Test
        void testNoWindows() { }
    }
}