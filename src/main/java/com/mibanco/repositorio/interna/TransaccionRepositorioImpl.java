package com.mibanco.repositorio.interna;

import com.mibanco.dto.TransaccionDTO;
import com.mibanco.dto.mapeador.TransaccionMapeador;
import com.mibanco.modelo.Transaccion;
import com.mibanco.modelo.enums.TipoOperacionTransaccion;
import com.mibanco.modelo.enums.TipoTransaccion;
import com.mibanco.repositorio.TransaccionRepositorio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementación del repositorio de Transacciones
 * Visibilidad restringida al paquete internal
 */
class TransaccionRepositorioImpl extends BaseRepositorioImpl<Transaccion, Long, TipoOperacionTransaccion> implements TransaccionRepositorio {
    
    /**
     * Constructor con visibilidad de paquete
     */
    TransaccionRepositorioImpl() {
        super();
    }
   
    @Override
    public Optional<List<Transaccion>> buscarPorCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            buscarTodosPorPredicado(transaccion -> transaccion.getNumeroCuenta().equals(numero))
        );
    }
    
    @Override
    public Optional<List<Transaccion>> buscarPorTipo(Optional<TipoTransaccion> tipo) {
        return tipo.flatMap(t -> 
            buscarTodosPorPredicado(transaccion -> transaccion.getTipo() == t)
        );
    }
    
    @Override
    public Optional<List<Transaccion>> buscarPorFecha(Optional<LocalDate> fecha) {
        return fecha.flatMap(f -> 
            buscarTodosPorPredicado(transaccion -> 
                transaccion.getFecha().toLocalDate().equals(f))
        );
    }
    
    @Override
    public Optional<List<Transaccion>> buscarPorRangoFechas(Optional<LocalDate> fechaInicio, Optional<LocalDate> fechaFin) {
        return fechaInicio.flatMap(inicio -> 
            fechaFin.flatMap(fin -> 
                buscarTodosPorPredicado(transaccion -> {
                    LocalDateTime fechaTransaccion = transaccion.getFecha();
                    return !fechaTransaccion.toLocalDate().isBefore(inicio) && 
                           !fechaTransaccion.toLocalDate().isAfter(fin);
                })
            )
        );
    }
    
    @Override
    public Optional<List<Transaccion>> buscarTodas() {
        return buscarTodos();
    }
    
    @Override
    public Optional<Transaccion> eliminarPorId(Optional<Long> id) {
        return eliminarPorId(id, TipoOperacionTransaccion.ANULAR);
    }
    
    /**
     * ✅ Obtiene la configuración para este repositorio
     * @return Map con la configuración (ruta, clase, extractor de ID)
     */
    @Override
    protected Map<String, Object> obtenerConfiguracion() {
        Map<String, Object> config = new HashMap<>();
        config.put("rutaArchivo", "src/main/resources/data/transaccion.json");
        config.put("tipoClase", Transaccion.class);
        config.put("extractorId", (Function<Transaccion, Long>) Transaccion::getId);
        return config;
    }
    
    /**
     * ✅ Crea una nueva transacción con ID automático
     * Usa DTOs para mantener la inmutabilidad de la entidad
     * Enfoque funcional puro con Optional
     * @param transaccion Transacción a crear
     * @return Transacción creada con nuevo ID
     */
    @Override
    protected Transaccion crearConNuevoId(Transaccion transaccion) {
        TransaccionMapeador mapeador = new TransaccionMapeador();
        
        return mapeador.aDtoDirecto(transaccion)
            .map(dto -> dto.toBuilder()
                .id(idContador.incrementAndGet())
                .build())
            .flatMap(mapeador::aEntidadDirecta)
            .orElseThrow(() -> new IllegalStateException("No se pudo procesar la entidad Transaccion"));
    }
} 