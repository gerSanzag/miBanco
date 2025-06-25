package com.mibanco.controlador;

/**
 * Interfaz del controlador para la entidad Cuenta.
 * Define los métodos para coordinar la interacción entre la vista y el servicio de cuentas.
 */
public interface CuentaControlador {

    /**
     * Coordina la creación de una nueva cuenta.
     * Solicita datos a la vista, los procesa y los envía al servicio.
     */
    void crearCuenta();

    /**
     * Coordina la búsqueda de una cuenta por su número.
     * Solicita el número a la vista, busca en el servicio y muestra el resultado.
     */
    void buscarCuentaPorNumero();

    /**
     * Coordina la obtención de todas las cuentas.
     * Obtiene la lista del servicio y la muestra a través de la vista.
     */
    void listarTodasLasCuentas();

    /**
     * Coordina la actualización de una cuenta.
     * Solicita el número, busca la cuenta, solicita modificaciones y actualiza.
     */
    void actualizarCuenta();

    /**
     * Coordina la eliminación de una cuenta.
     * Solicita el número, busca la cuenta, confirma y elimina.
     */
    void eliminarCuenta();

    /**
     * Muestra el menú principal de gestión de cuentas.
     * Maneja el bucle de opciones y la navegación del menú.
     */
    void mostrarMenuPrincipal();

    /**
     * Coordina la búsqueda de cuentas por titular.
     * Solicita el ID del titular a la vista, busca en el servicio y muestra los resultados.
     */
    void buscarCuentasPorTitular();

    /**
     * Coordina la búsqueda de cuentas por tipo.
     * Solicita el tipo de cuenta a la vista, busca en el servicio y muestra los resultados.
     */
    void buscarCuentasPorTipo();

    /**
     * Coordina la obtención de cuentas activas.
     * Obtiene la lista de cuentas activas del servicio y la muestra a través de la vista.
     */
    void listarCuentasActivas();

    /**
     * Coordina la obtención de cuentas eliminadas.
     * Obtiene la lista de cuentas eliminadas del servicio y la muestra a través de la vista.
     */
    void mostrarCuentasEliminadas();

    /**
     * Coordina la restauración de una cuenta eliminada.
     * Solicita el número de cuenta, busca en eliminadas, confirma y restaura.
     */
    void restaurarCuenta();

    /**
     * Coordina el conteo de cuentas.
     * Obtiene el total de cuentas del servicio y lo muestra a través de la vista.
     */
    void contarCuentas();

    /**
     * Coordina la consulta del saldo de una cuenta.
     * Solicita el número de cuenta, busca en el servicio y muestra el saldo.
     */
    void consultarSaldoCuenta();
} 