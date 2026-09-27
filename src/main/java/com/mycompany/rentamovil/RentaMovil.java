package com.mycompany.rentamovil;

import com.mycompany.rentamovil.modelo.Automovil;
import com.mycompany.rentamovil.modelo.CamionetaCarga;
import com.mycompany.rentamovil.modelo.GestorRentaMovil;
import com.mycompany.rentamovil.modelo.Motocicleta;
import com.mycompany.rentamovil.modelo.Vehiculo;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
  Presenta el menú de consola y coordina la interacción con el empleado.
 */
public class RentaMovil {

    private final Scanner entrada;
    private final GestorRentaMovil gestor;

    private RentaMovil() {
        entrada = new Scanner(System.in);
        gestor = new GestorRentaMovil();
        cargarDatosIniciales();
    }

    public static void main(String[] args) {
        RentaMovil aplicacion = new RentaMovil();
        aplicacion.ejecutar();
    }

    private void cargarDatosIniciales() {
        gestor.registrarVehiculo(new Automovil(
                "P001AAA", "Toyota", "Corolla",
                new BigDecimal("150.00"), 5, false));

        gestor.registrarVehiculo(new Automovil(
                "P002AAA", "Honda", "Civic",
                new BigDecimal("180.00"), 5, true));

        gestor.registrarVehiculo(new Motocicleta(
                "M001AAA", "Yamaha", "FZ25",
                new BigDecimal("80.00"), 250));

        gestor.registrarVehiculo(new Motocicleta(
                "M002AAA", "Kawasaki", "Ninja",
                new BigDecimal("120.00"), 400));

        gestor.registrarVehiculo(new CamionetaCarga(
                "C001AAA", "Hyundai", "H100",
                new BigDecimal("200.00"), new BigDecimal("1.5")));

        gestor.registrarVehiculo(new CamionetaCarga(
                "C002AAA", "Isuzu", "NPR",
                new BigDecimal("250.00"), new BigDecimal("2")));
    }

    private void ejecutar() {
        try {
            while (true) {
                mostrarMenu();

                try {
                    int opcion = leerEntero("Seleccione una opcion: ", 0, 6);

                    switch (opcion) {
                        case 1:
                            registrarVehiculo();
                            break;
                        case 2:
                            mostrarFlota();
                            break;
                        case 3:
                            cotizarAlquiler();
                            break;
                        case 4:
                            realizarAlquiler();
                            break;
                        case 5:
                            registrarDevolucion();
                            break;
                        case 6:
                            mostrarReporte();
                            break;
                        case 0:
                            System.out.println("Programa finalizado.");
                            return;
                    }
                } catch (IllegalArgumentException | IllegalStateException error) {
                    System.out.println("Error: " + error.getMessage());
                }
            }
        } catch (NoSuchElementException error) {
            System.out.println("\nEntrada finalizada. Programa cerrado.");
        } finally {
            entrada.close();
        }
    }

    private void mostrarMenu() {
        System.out.println("\nRENTA MOVIL");
        System.out.println("1. Registrar vehiculo");
        System.out.println("2. Consultar flota");
        System.out.println("3. Cotizar alquiler");
        System.out.println("4. Realizar alquiler");
        System.out.println("5. Registrar devolucion");
        System.out.println("6. Mostrar reporte general");
        System.out.println("0. Salir");
    }

    private void registrarVehiculo() {
        System.out.println("\nREGISTRAR VEHÍCULO");
        System.out.println("1. Automóvil");
        System.out.println("2. Motocicleta");
        System.out.println("3. Camioneta de carga");

        int tipo = leerEntero("Seleccione la categoría: ", 1, 3);
        String placa = leerTexto("Placa: ");
        String marca = leerTexto("Marca: ");
        String modelo = leerTexto("Modelo: ");
        BigDecimal tarifa = leerDecimalPositivo(
                "Tarifa diaria en Q (ejemplo: 150.50): ");

        Vehiculo vehiculo;

        switch (tipo) {
            case 1:
                int pasajeros = leerEntero(
                        "Cantidad de pasajeros: ", 1, Integer.MAX_VALUE);
                boolean automatica = leerSiNo("¿Transmisión automatica? (S/N): ");
                vehiculo = new Automovil(
                        placa, marca, modelo, tarifa, pasajeros, automatica);
                break;
            case 2:
                int cilindraje = leerEntero(
                        "Cilindraje en cc: ", 1, Integer.MAX_VALUE);
                vehiculo = new Motocicleta(
                        placa, marca, modelo, tarifa, cilindraje);
                break;
            case 3:
                BigDecimal capacidad = leerDecimalPositivo(
                        "Capacidad en toneladas (ejemplo: 1.5): ");
                vehiculo = new CamionetaCarga(
                        placa, marca, modelo, tarifa, capacidad);
                break;
            default:
                throw new IllegalArgumentException("Categoria no valida.");
        }

        gestor.registrarVehiculo(vehiculo);
        System.out.println("Vehiculo registrado correctamente: " + vehiculo.getPlaca());
    }

    private void mostrarFlota() {
        List<Vehiculo> flota = gestor.getVehiculos();

        if (flota.isEmpty()) {
            System.out.println("No hay vehiculos registrados.");
            return;
        }

        System.out.println("\nFLOTA REGISTRADA");
        for (Vehiculo vehiculo : flota) {
            System.out.println("\n" + vehiculo.getDescripcion());
        }
    }

    private void cotizarAlquiler() {
        String placa = leerTexto("Placa del vehiculo: ");
        int dias = leerEntero("Dias de alquiler: ", 1, Integer.MAX_VALUE);

        Vehiculo vehiculo = gestor.buscarVehiculo(placa);
        BigDecimal total = gestor.cotizar(placa, dias);

        System.out.println("\n" + vehiculo.getDescripcion());
        System.out.println("Total cotizado: Q" + total.toPlainString());

        if (!vehiculo.isDisponible()) {
            System.out.println(
                    "El vehiculo esta alquilado. Podra alquilarse despues de su devolucion.");
        }
    }

    private void realizarAlquiler() {
        String placa = leerTexto("Placa del vehiculo: ");
        Vehiculo vehiculo = gestor.buscarVehiculo(placa);

        if (!vehiculo.isDisponible()) {
            throw new IllegalStateException(
                    "El vehiculo con placa " + vehiculo.getPlaca() + " ya está alquilado.");
        }

        int dias = leerEntero("Dias de alquiler: ", 1, Integer.MAX_VALUE);
        BigDecimal total = gestor.cotizar(placa, dias);

        System.out.println("\n" + vehiculo.getDescripcion());
        System.out.println("Total por cobrar: Q" + total.toPlainString());

        if (!leerSiNo("¿Confirma el alquiler? (S/N): ")) {
            System.out.println("Alquiler cancelado. No se realizo ningun cobro.");
            return;
        }

        BigDecimal cobrado = gestor.confirmarAlquiler(placa, dias);
        System.out.println("Alquiler confirmado. Cobro registrado: Q" + cobrado.toPlainString());
    }

    private void registrarDevolucion() {
        String placa = leerTexto("Placa del vehiculo a devolver: ");
        gestor.devolverVehiculo(placa);
        System.out.println("Devolucion registrada. El vehiculo vuelve a estar disponible.");
    }

    private void mostrarReporte() {
        System.out.println("\n" + gestor.generarReporte());
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();

            if (!texto.isEmpty()) {
                return texto;
            }

            System.out.println("El dato no puede estar vacio.");
        }
    }

    private int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            String texto = leerTexto(mensaje);

            try {
                int valor = Integer.parseInt(texto);

                if (valor >= minimo && valor <= maximo) {
                    return valor;
                }

                System.out.println("El valor debe estar entre " + minimo + " y " + maximo + ".");
            } catch (NumberFormatException error) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }

    private BigDecimal leerDecimalPositivo(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);

            // Acepta números como 150 o 150.50, sin separadores de miles.
            if (!texto.matches("[0-9]+(\\.[0-9]+)?")) {
                System.out.println(
                        "Use un numero positivo con punto decimal, por ejemplo 150.50.");
                continue;
            }

            try {
                BigDecimal valor = new BigDecimal(texto);

                if (valor.compareTo(BigDecimal.ZERO) > 0) {
                    return valor;
                }

                System.out.println("El valor debe ser mayor que cero.");
            } catch (NumberFormatException error) {
                System.out.println("Ingrese un numero decimal válido.");
            }
        }
    }

    private boolean leerSiNo(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje);

            if (respuesta.equalsIgnoreCase("S")) {
                return true;
            }

            if (respuesta.equalsIgnoreCase("N")) {
                return false;
            }

            System.out.println("Ingrese S o N.");
        }
    }
}

