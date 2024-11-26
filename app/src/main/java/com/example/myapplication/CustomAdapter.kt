package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CustomAdapter(private val dataSet: List<Produto>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.nomeProduto)
        val descricao: TextView = view.findViewById(R.id.descricaoProduto)
        val valor: TextView = view.findViewById(R.id.valorProduto)
        val imagem: ImageView = view.findViewById(R.id.imagem_produto)
        val btnComprar: Button = view.findViewById(R.id.btnComprar)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.activity_item_detalhe_produto, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val produto = dataSet[position]

        // Configura o nome do produto
        viewHolder.nome.text = produto.produtoNome
        viewHolder.nome.setTextColor(android.graphics.Color.parseColor("#BF0B0B"))

        // Limita a descrição do produto a 50 caracteres
        val descricaoCurta = if (produto.produtoDesc?.length!! > 50) {
            produto.produtoDesc.substring(0, 50) + "..."
        } else {
            produto.produtoDesc
        }
        viewHolder.descricao.text = descricaoCurta

        // Configura o preço do produto
        viewHolder.valor.text = produto.produtoPreco
        viewHolder.valor.setTextColor(android.graphics.Color.parseColor("#D94A4A"))

        // Configura o botão "Adicionar" com a cor de fundo e o texto
        viewHolder.btnComprar.text = "Adicionar"
        viewHolder.btnComprar.setBackgroundColor(android.graphics.Color.parseColor("#BBBF4E"))
        viewHolder.btnComprar.setTextColor(android.graphics.Color.WHITE)

        viewHolder.btnComprar.setOnClickListener {
            val intent = Intent(viewHolder.itemView.context, ProdutoDetalhesActivity::class.java)
            intent.putExtra("NOME_PRODUTO", produto.produtoNome)
            intent.putExtra("DESCRICAO_PRODUTO", produto.produtoDesc)
            intent.putExtra("ID_PRODUTO", produto.produtoId)
            intent.putExtra("IMAGEM_URL", produto.imagemUrl)
            intent.putExtra("QUANTIDADE_DISPONIVEL", produto.quantidadeDisponivel)

            viewHolder.itemView.context.startActivity(intent)
        }

        // Carrega a imagem do produto usando Glide
        Glide.with(viewHolder.itemView.context)
            .load(produto.imagemUrl)
            .placeholder(R.drawable.ic_launcher_background) // Imagem de placeholder
            .error(R.drawable.ic_launcher_foreground) // Imagem de erro
            .into(viewHolder.imagem)
    }

    override fun getItemCount() = dataSet.size
}
