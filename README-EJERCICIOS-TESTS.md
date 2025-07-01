# Ejercicios de Tests en Java - miBanco

## Propósito de esta rama

Esta rama (`feature/ejercicios-tests-java`) está dedicada al desarrollo y aprendizaje de técnicas de testing en Java, utilizando el proyecto miBanco como base para los ejercicios prácticos.

## Objetivos de aprendizaje

- **JUnit 5**: Aprender a escribir tests unitarios efectivos
- **Mockito**: Practicar el uso de mocks para aislar unidades de código
- **Test Driven Development (TDD)**: Desarrollar código siguiendo la metodología TDD
- **Cobertura de código**: Aprender a medir y mejorar la cobertura de tests
- **Tests de integración**: Practicar tests que involucren múltiples componentes
- **Assertions avanzadas**: Usar diferentes tipos de assertions para validar resultados

## Estructura de ejercicios

### 1. Tests Unitarios Básicos
- Tests para clases del modelo (Cliente, Cuenta, Tarjeta, Transaccion)
- Tests para servicios básicos
- Tests para mapeadores DTO

### 2. Tests con Mocks
- Tests de controladores usando mocks de servicios
- Tests de servicios usando mocks de repositorios
- Tests de casos edge y excepciones

### 3. Tests de Integración
- Tests que involucren múltiples capas
- Tests de flujos completos de negocio
- Tests de persistencia

### 4. Tests de Rendimiento
- Tests de carga básicos
- Tests de concurrencia

## Tecnologías a utilizar

- **JUnit 5**: Framework principal de testing
- **Mockito**: Para crear mocks y stubs
- **AssertJ**: Para assertions más legibles
- **Maven Surefire**: Para ejecutar tests
- **JaCoCo**: Para medir cobertura de código

## Convenciones de nomenclatura

- Clases de test: `[ClaseAProbar]Test.java`
- Métodos de test: `should[ComportamientoEsperado]_when[Condicion]()`
- Paquetes de test: Misma estructura que el código principal, pero en `src/test/java`

## Estado actual del proyecto

El proyecto miBanco ya tiene una estructura básica con:
- Modelos de dominio (Cliente, Cuenta, Tarjeta, Transaccion)
- Servicios de negocio
- Controladores
- Repositorios
- DTOs y mapeadores

Esta base nos permitirá crear ejercicios de testing realistas y prácticos.

## Próximos pasos

1. Configurar dependencias de testing en `pom.xml`
2. Crear estructura de directorios para tests
3. Comenzar con ejercicios básicos de JUnit 5
4. Implementar tests progresivamente más complejos
5. Documentar mejores prácticas aprendidas

---

**Nota**: Esta rama es para fines educativos y de aprendizaje. Los cambios aquí no afectarán la funcionalidad principal del proyecto miBanco. 