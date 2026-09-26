package com.mycompany.rentamovil.modelo;

import java.math.BigDecimal;

/**
  Representa una motocicleta y aplica un recargo único si supera los 250 cc.
 */
public class Motocicleta extends Vehiculo {

    private final int cilindraje;

    public Motocicleta(String placa, String marca, String modelo,
                       BigDecimal tarifaDiaria, int cilindraje) {
        super(placa, marca, modelo, tarifaDiaria);

        if (cilindraje <= 0) {
            throw new IllegalArgumentException(
                    "El cilindraje debe ser mayor que cero.");
        }

        this.cilindraje = cilindraje;
    }

    @Override
    public String getCategoria() {
        return "Motocicleta";
    }

    @Override
    protected String getDetalleEspecifico() {
        return "Cilindraje: " + cilindraje + " cc";
    }

    @Override
    protected BigDecimal calcularRecargo(int dias) {
        // El recargo es único por alquiler, independientemente de los días.
        if (cilindraje > 250) {
            return BigDecimal.valueOf(75);
        }

        return BigDecimal.ZERO;
    }
}