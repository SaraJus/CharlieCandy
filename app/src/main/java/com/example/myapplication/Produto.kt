package com.example.myapplication

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Produto(
    @SerializedName("PRODUTO_ID") val produtoId: Int?,
    @SerializedName("PRODUTO_NOME") val produtoNome: String?,
    @SerializedName("PRODUTO_DESC") val produtoDesc: String?,
    @SerializedName("PRODUTO_PRECO") val produtoPreco: String?,
    @SerializedName("PRODUTO_DESCONTO") val produtoDesconto: String?,
    @SerializedName("CATEGORIA_ID") val categoriaId: Int?,
    @SerializedName("PRODUTO_ATIVO") val produtoAtivo: Int?,
    @SerializedName("IMAGEM_URL") val imagemUrl: String?,
    @SerializedName("QUANTIDADE_DISPONIVEL") val quantidadeDisponivel: Int?
) : Parcelable

@Parcelize
data class Pedido(
    @SerializedName("PEDIDO_ID") val pedidoId: Int,
    @SerializedName("DATA_PEDIDO") val dataPedido: String,
    @SerializedName("VALOR_TOTAL") val valorTotal: Double,
    @SerializedName("STATUS") val status: String
) : Parcelable
