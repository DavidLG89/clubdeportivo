package com.grupo3.clubdeportivo.ui.fragmentos.reserva

import com.grupo3.clubdeportivo.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.grupo3.clubdeportivo.clases.Modelos.Programacion

class ProgramacionAdapter(private val programacion: List<Programacion>, private val onSeleccionar: (Programacion) -> Unit) : RecyclerView.Adapter<ProgramacionAdapter.ProgramacionViewHolder>() {

    class ProgramacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHora: TextView = itemView.findViewById(R.id.tvHorario)
        val txtCupo: TextView = itemView.findViewById(R.id.tvCupos)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProgramacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_programacion, parent, false)

        return ProgramacionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProgramacionViewHolder,
        position: Int
    ) {
        val programacion = programacion[position]

        holder.txtHora.text = programacion.fechaHora.toString()
        holder.txtCupo.text = programacion.cuposDisponibles.toString()

        holder.itemView.setOnClickListener {
            onSeleccionar(programacion)
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.color_boton_principal2
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return programacion.size
    }
}