package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PedidoAdapter(private val pedidos: List<Pedido>) :
    RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPedidoId: TextView = itemView.findViewById(R.id.txtPedidoId)
        val txtDataPedido: TextView = itemView.findViewById(R.id.txtDataPedido)
        val txtValorTotal: TextView = itemView.findViewById(R.id.txtValorTotal)
        val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.txtPedidoId.text = "Pedido: ${pedido.pedidoId}"
        holder.txtDataPedido.text = "Data: ${pedido.dataPedido}"
        holder.txtValorTotal.text = "Valor: R$ ${pedido.valorTotal}"
        holder.txtStatus.text = "Status: ${pedido.status}"
    }

    override fun getItemCount(): Int = pedidos.size
}
