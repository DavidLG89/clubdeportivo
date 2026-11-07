package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Repositorios.VencimientoRepo
import java.util.Date

/**
 * Data Transfer Object (DTO) que envuelve el resultado de la consulta de vencimientos de cuotas.
 *
 * @property ok Indica si la consulta fue exitosa y arrojó resultados.
 * @property mensaje Mensaje descriptivo sobre el estado de la operación.
 * @property cuotas Lista de cuotas devueltas por la consulta.
 */
data class ResultadoVencimiento(
    val ok: Boolean,
    val mensaje: String,
    val cuotas: List<CuotaDTO>
)

/**
 * Servicio encargado de la lógica de negocio asociada a la gestión y consulta de vencimientos de cuotas.
 *
 * Actúa como intermediario entre la capa de presentación (UI) y el repositorio [VencimientoRepo].
 */
class VencimientoServ(private val vencimientoRepo: VencimientoRepo) {

    /**
     * Consulta y procesa las cuotas que vencen en la fecha indicada.
     *
     * @param fecha Objeto [Date] que especifica el día de vencimiento a consultar.
     * @return Objeto [ResultadoVencimiento] con el estado de la operación y el listado de cuotas.
     */
    fun obtenerVencimientosPorFecha(fecha: Date): ResultadoVencimiento {
        // Delegación al repositorio para consultar la base de datos
        val cuotas = vencimientoRepo.obtenerVencimientosPorFecha(fecha)

        // Validación de resultado vacío
        if (cuotas.isEmpty()) {
            return ResultadoVencimiento(
                ok = false,
                mensaje = "No se encontraron cuotas vencidas para la fecha especificada",
                cuotas = emptyList()
            )
        }

        // Retorno exitoso encapsulado
        return ResultadoVencimiento(
            ok = true,
            mensaje = "Vencimientos obtenidos con éxito",
            cuotas = cuotas
        )
    }
}
