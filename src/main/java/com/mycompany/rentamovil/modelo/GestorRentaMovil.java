package com.mycompany.rentamovil.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
  Administra la flota y coordina las operaciones de alquiler.
 */
public class GestorRentaMovil {

    private final Map<String, Vehiculo> vehiculos;
    private BigDecimal ingresosAcumulados;

    public GestorRentaMovil() {
        vehiculos = new LinkedHashMap<>();
        ingresosAcumulados = BigDecimal.ZERO.setScale(2);
    }

    public void registrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            throw new IllegalArgumentException(
                    "El vehiculo no puede ser nulo.");
        }

        String placa = Vehiculo.normalizarPlaca(vehiculo.getPlaca());

        if (vehiculos.containsKey(placa)) {
            throw new IllegalArgumentException(
                    "Ya existe un vehiculo con la placa " + placa + ".");
        }

        if (!vehiculo.isDisponible()) {
            throw new IllegalStateException(
                    "Solo se pueden registrar vehiculos disponibles.");
        }

        vehiculos.put(placa, vehiculo);
    }

    public Vehiculo buscarVehiculo(String placa) {
        String placaNormalizada = Vehiculo.normalizarPlaca(placa);
        Vehiculo vehiculo = vehiculos.get(placaNormalizada);

        if (vehiculo == null) {
            throw new IllegalArgumentException(
                    "No existe un vehiculo con la placa "
                    + placaNormalizada + ".");
        }

        return vehiculo;
    }

    public List<Vehiculo> getVehiculos() {
        return Collections.unmodifiableList(
                new ArrayList<>(vehiculos.values()));
    }

    public BigDecimal cotizar(String placa, int dias) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        return vehiculo.calcularCosto(dias);
    }

    public BigDecimal confirmarAlquiler(String placa, int dias) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        BigDecimal total = vehiculo.calcularCosto(dias);

        if (!vehiculo.isDisponible()) {
            throw new IllegalStateException(
                    "El vehiculo con placa " + vehiculo.getPlaca()
                    + " ya esta alquilado.");
        }

        // Preparar los importes antes de modificar el estado.
        BigDecimal nuevosIngresos = ingresosAcumulados.add(total);

        vehiculo.ocupar();
        ingresosAcumulados = nuevosIngresos;

        return total;
    }

    public void devolverVehiculo(String placa) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        vehiculo.liberar();
    }

    public BigDecimal getIngresosAcumulados() {
        return ingresosAcumulados;
    }

    public String generarReporte() {
        // Cada arreglo contiene: [registrados, disponibles, alquilados].
        Map<String, int[]> resumenCategorias = new LinkedHashMap<>();
        int totalDisponibles = 0;

        for (Vehiculo vehiculo : vehiculos.values()) {
            String categoria = vehiculo.getCategoria();
            int[] cantidades = resumenCategorias.get(categoria);

            if (cantidades == null) {
                cantidades = new int[3];
                resumenCategorias.put(categoria, cantidades);
            }

            cantidades[0]++;

            if (vehiculo.isDisponible()) {
                cantidades[1]++;
                totalDisponibles++;
            } else {
                cantidades[2]++;
            }
        }

        StringBuilder reporte = new StringBuilder("REPORTE DE FLOTA\n");

        if (vehiculos.isEmpty()) {
            reporte.append("No hay vehiculos registrados.\n");
        }

        for (Map.Entry<String, int[]> entrada : resumenCategorias.entrySet()) {
            int[] cantidades = entrada.getValue();

            reporte.append("\n").append(entrada.getKey()).append("\n")
                    .append("Registrados: ").append(cantidades[0])
                    .append(" | Disponibles: ").append(cantidades[1])
                    .append(" | Alquilados: ").append(cantidades[2])
                    .append("\n");
        }

        int totalRegistrados = vehiculos.size();
        int totalAlquilados = totalRegistrados - totalDisponibles;

        reporte.append("\nTOTALES DE LA FLOTA\n")
                .append("Registrados: ").append(totalRegistrados)
                .append(" | Disponibles: ").append(totalDisponibles)
                .append(" | Alquilados: ").append(totalAlquilados)
                .append("\nIngresos acumulados: Q")
                .append(ingresosAcumulados.toPlainString());

        return reporte.toString();
    }
}
