package com.mibanco.vista;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.ClienteDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interfaz para la vista de la entidad Cuenta.
 * Define los métodos para interactuar con el usuario en lo que respecta a la gestión de cuentas.
 */
public interface CuentaVista {

    /**
     * Muestra el menú principal de opciones para la gestión de cuentas.
     */
    void mostrarMenuPrincipal();

    /**
     * Lee la opción numérica que el usuario introduce desde el teclado.
     *
     * @return un Optional con el número de la opción seleccionada, o un Optional vacío si la entrada no es un número válido.
     */
    Optional<Integer> obtenerOpcion();

    /**
     * Solicita al usuario los datos necesarios para crear una nueva cuenta.
     *
     * @return un Map con los datos crudos de la cuenta (ej: "numeroCuenta" -> "1234567890").
     */
    Map<String, String> solicitarDatosParaCrear();

    /**
     * Solicita al usuario el número de cuenta.
     *
     * @return un Optional con el número de cuenta, o un Optional vacío si la entrada no es válida.
     */
    Optional<String> solicitarNumeroCuenta();

    /**
     * Muestra los datos modificables de una cuenta y solicita los nuevos valores.
     * Solo muestra y permite modificar saldo y estado activo.
     *
     * @param cuentaActual el DTO de la cuenta con los datos actuales para mostrarlos como referencia.
     * @return un Map con los nuevos datos introducidos por el usuario (solo los campos modificados).
     */
    Map<String, String> solicitarDatosParaActualizar(CuentaDTO cuentaActual);

    /**
     * Muestra los detalles de una cuenta específica.
     * Si el Optional está vacío, informa al usuario que la cuenta no fue encontrada.
     *
     * @param cuenta el Optional que contiene el DTO de la cuenta a mostrar.
     */
    void mostrarCuenta(Optional<CuentaDTO> cuenta);

    /**
     * Muestra una lista de todas las cuentas registradas.
     *
     * @param cuentas la lista de DTOs de cuentas a mostrar.
     */
    void mostrarTodasLasCuentas(List<CuentaDTO> cuentas);

    /**
     * Muestra un mensaje genérico en la consola.
     *
     * @param mensaje el mensaje a mostrar.
     */
    void mostrarMensaje(String mensaje);

    /**
     * Muestra los datos de una cuenta y solicita confirmación para una acción específica.
     *
     * @param cuenta el DTO de la cuenta sobre la cual se realizará la acción.
     * @param titulo el título que se mostrará en la cabecera.
     * @param mensaje el mensaje de confirmación que se mostrará al usuario.
     * @return true si el usuario confirma la acción, false en caso contrario.
     */
    boolean confirmarAccion(CuentaDTO cuenta, String titulo, String mensaje);

    /**
     * Solicita al usuario el ID del titular para asociar a la cuenta.
     *
     * @return un Optional con el ID del titular, o un Optional vacío si la entrada no es un número válido.
     */
    Optional<Long> solicitarIdTitular();

    /**
     * Solicita al usuario el tipo de cuenta.
     *
     * @return un Optional con el tipo de cuenta seleccionado, o un Optional vacío si la entrada no es válida.
     */
    Optional<String> solicitarTipoCuenta();

    /**
     * Solicita al usuario el saldo inicial de la cuenta.
     *
     * @return un Optional con el saldo inicial, o un Optional vacío si la entrada no es un número válido.
     */
    Optional<BigDecimal> solicitarSaldoInicial();
} 