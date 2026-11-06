package com.grupo3.clubdeportivo.clases.Servicios

import com.grupo3.clubdeportivo.clases.DTOS.CuotaDTO
import com.grupo3.clubdeportivo.clases.Repositorios.VencimientoRepo
import java.util.Date

data class ResultadoVencimiento(
    val ok: Boolean,
    val mensaje: String,
    val cuotas: List<CuotaDTO>
)

class VencimientoServ(private val vencimientoRepo: VencimientoRepo) {

    fun obtenerVencimientosPorFecha(fecha: Date): ResultadoVencimiento {
        val cuotas = vencimientoRepo.obtenerVencimientosPorFecha(fecha)

        if (cuotas.isEmpty()) {
            return ResultadoVencimiento(
                ok = false,
                mensaje = "No se encontraron cuotas vencidas para la fecha especificada",
                cuotas = emptyList()
            )
        }

        return ResultadoVencimiento(
            ok = true,
            mensaje = "Vencimientos obtenidos con éxito",
            cuotas = cuotas
        )
    }
}
