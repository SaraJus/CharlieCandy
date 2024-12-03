package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

class CartAdapter(
    private val items: MutableList<Produto>,
    private val context: Context,
    private val updateTotal: () -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.productNameTextView)
        val productPrice: TextView = view.findViewById(R.id.productPriceTextView)
        val productQuantity: TextView = view.findViewById(R.id.productQuantityTextView)
        val productImage: ImageView = view.findViewById(R.id.productImageView)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detalhe_carrinho, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.productName.text = item.produtoNome
        holder.productPrice.text = String.format("R$%.2f", item.produtoPreco?.toDoubleOrNull() ?: 0.0)
        holder.productQuantity.text = "Qtd: ${item.quantidadeDisponivel ?: 1}"
        Glide.with(context).load(item.imagemUrl).into(holder.productImage)


        holder.deleteButton.setOnClickListener {
            removeItemFromCart(item, position)
        }
    }

    private fun removeItemFromCart(item: Produto, position: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/CHARLIE/carrinho_de_compras/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(CartApiService::class.java)


        val sharedPreferences = context.getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        api.deleteCartItem(item.produtoId!!, userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, items.size)
                    updateTotal()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Tratamento de erro ao remover o item
            }
        })
    }

    override fun getItemCount(): Int = items.size
}

interface CartApiService {
    @GET("getCartItems")
    fun getCartItems(@Query("userId") userId: Int): Call<List<Produto>>

    @DELETE("deleteCartItem")
    fun deleteCartItem(@Query("produtoId") produtoId: Int, @Query("userId") userId: Int): Call<Void>
}