package com.mycompany.rentamovil.modelo;

import java.math.BigDecimal;

/**
  Representa una camioneta de carga y aplica un recargo por tonelada y día.
 */
public class CamionetaCarga extends Vehiculo {

    private final BigDecimal capacidadToneladas;

    public CamionetaCarga(String placa, String marca, String modelo,
                         BigDecimal tarifaDiaria, BigDecimal capacidadToneladas) {
        super(placa, marca, modelo, tarifaDiaria);

        if (capacidadToneladas == null
                || capacidadToneladas.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "La capacidad en toneladas debe ser mayor que cero.");
        }

        this.capacidadToneladas = capacidadToneladas;
    }

    @Override
    public String getCategoria() {
        return "Camioneta de carga";
    }

    @Override
    protected String getDetalleEspecifico() {
        return "Capacidad de carga: " + capacidadToneladas.toPlainString()
                + " toneladas";
    }

    @Override
    protected BigDecimal calcularRecargo(int dias) {
        return BigDecimal.valueOf(100)
                .multiply(capacidadToneladas)
                .multiply(BigDecimal.valueOf(dias));
    }
}
