package org.gfontestad.unit5app.ejemplos.models;

import org.gfontestad.unit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    String nombre = "Gonzalo Fontestad";
    BigDecimal saldo = new BigDecimal("1000.12345");


    @Test
    @DisplayName("Test nombre de la cuenta")
    void testNombreCuenta() {

        Cuenta cuenta = new Cuenta(nombre, saldo);

        String esperado = nombre;
        String real = cuenta.getNombrePersona();

        assertEquals(esperado, real);
    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta(nombre, saldo);
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta john_papa = new Cuenta("John Papa", new BigDecimal("8900.9997"));
        Cuenta john_papaFrita = new Cuenta("John Papa", new BigDecimal("8900.9997"));

        assertEquals(john_papaFrita, john_papa);
    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta(nombre, saldo);
        cuenta.debito(new BigDecimal("100"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals(900.12345, cuenta.getSaldo().doubleValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta(nombre, saldo);
        cuenta.credito(new BigDecimal("100"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals(1100.12345, cuenta.getSaldo().doubleValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void dineroInsuficienteException() {
        Cuenta cuenta = new Cuenta(nombre, saldo);
        Exception exception = assertThrows(DineroInsuficienteException.class, ()-> {
           cuenta.debito(new BigDecimal("1500"));
        });
        String real = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals(esperado, real);
    }
}