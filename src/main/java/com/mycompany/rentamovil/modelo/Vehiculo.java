package com.mycompany.rentamovil.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/*
  Define los datos comunes y el cálculo general de un alquiler.
  Cada subclase proporciona su categoría, sus detalles y su recargo.
 */
public abstract class Vehiculo {

    private final String placa;
    private final String marca;
    private final String modelo;
    private final BigDecimal tarifaDiaria;
    private boolean disponible;

    protected Vehiculo(String placa, String marca, String modelo,
                       BigDecimal tarifaDiaria) {
        this.placa = normalizarPlaca(placa);

        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La marca no puede estar vacia.");
        }

        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El modelo no puede estar vacio.");
        }

        if (tarifaDiaria == null
                || tarifaDiaria.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "La tarifa diaria debe ser mayor que cero.");
        }

        this.marca = marca.trim();
        this.modelo = modelo.trim();
        this.tarifaDiaria = tarifaDiaria;
        this.disponible = true;
    }

    // Acceso de paquete: también lo utilizará GestorRentaMovil.
    static String normalizarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La placa no puede estar vacía.");
        }

        return placa.trim().toUpperCase(Locale.ROOT);
    }

    public String getPlaca() {
        return placa;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public abstract String getCategoria();

    public final String getDescripcion() {
        return "Categoria: " + getCategoria()
                + "\nPlaca: " + placa
                + "\nMarca: " + marca
                + "\nModelo: " + modelo
                + "\nTarifa diaria: Q"
                + tarifaDiaria.setScale(2, RoundingMode.HALF_UP).toPlainString()
                + "\nEstado: " + (disponible ? "Disponible" : "Alquilado")
                + "\n" + getDetalleEspecifico();
    }

    public final BigDecimal calcularCosto(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException(
                    "Los dias de alquiler deben ser mayores que cero.");
        }

        BigDecimal costoBase = tarifaDiaria.multiply(BigDecimal.valueOf(dias));
        BigDecimal recargo = calcularRecargo(dias);
        BigDecimal total = costoBase.add(recargo);

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    protected abstract String getDetalleEspecifico();

    protected abstract BigDecimal calcularRecargo(int dias);

    // El gestor coordina estas transiciones después de validar la operación.
    void ocupar() {
        if (!disponible) {
            throw new IllegalStateException(
                    "El vehiculo con placa " + placa + " ya esta alquilado.");
        }

        disponible = false;
    }

    void liberar() {
        if (disponible) {
            throw new IllegalStateException(
                    "El vehiculo con placa " + placa + " ya esta disponible.");
        }

        disponible = true;
    }
}