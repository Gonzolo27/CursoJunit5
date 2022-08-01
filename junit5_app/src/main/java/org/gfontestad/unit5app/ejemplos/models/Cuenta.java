package org.gfontestad.unit5app.ejemplos.models;

import org.gfontestad.unit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

public class Cuenta {
    private String nombrePersona;
    private BigDecimal saldo; //cuando se trabaja con dineros, es mejor usar el tipo BigDecimal, es más preciso y eficiente que int, float, doubles...

    public Cuenta(String nombrePersona, BigDecimal saldo) {
        this.nombrePersona = nombrePersona;
        this.saldo = saldo;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void debito(BigDecimal monto) {
        //this.saldo.subtract(monto); //así no funciona porque el BigDecimal es inmutable y los métodos substract y add devuelven una nueva instancia de ese BigDecimal

        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if(nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new DineroInsuficienteException("Dinero insuficiente");
        }
        else {
            this.saldo = nuevoSaldo;
        }
    }

    public void credito(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }

    @Override
    public boolean equals(Object obj) {
        boolean validacion = false;

        if(obj != null || (obj instanceof Cuenta)) {
            Cuenta c = (Cuenta) obj;
            if(this.nombrePersona != null || this.saldo != null) {
                validacion = this.nombrePersona.equals(c.getNombrePersona()) && this.saldo.equals(c.getSaldo());
            }
        }

        return validacion;
    }
}
