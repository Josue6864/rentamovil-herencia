package com.mycompany.rentamovil.modelo;

import java.math.BigDecimal;

/**
  Representa un automóvil y aplica el recargo diario por transmisión automática.
 */
public class Automovil extends Vehiculo {

    private final int cantidadPasajeros;
    private final boolean automatica;

    public Automovil(String placa, String marca, String modelo,
                     BigDecimal tarifaDiaria, int cantidadPasajeros,
                     boolean automatica) {
        super(placa, marca, modelo, tarifaDiaria);

        if (cantidadPasajeros <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de pasajeros debe ser mayor que cero.");
        }

        this.cantidadPasajeros = cantidadPasajeros;
        this.automatica = automatica;
    }

    @Override
    public String getCategoria() {
        return "Automovil";
    }

    @Override
    protected String getDetalleEspecifico() {
        return "Cantidad de pasajeros: " + cantidadPasajeros
                + "\nTransmision: " + (automatica ? "Automatica" : "Manual");
    }

    @Override
    protected BigDecimal calcularRecargo(int dias) {
        if (automatica) {
            return BigDecimal.valueOf(50).multiply(BigDecimal.valueOf(dias));
        }

        return BigDecimal.ZERO;
    }
}
