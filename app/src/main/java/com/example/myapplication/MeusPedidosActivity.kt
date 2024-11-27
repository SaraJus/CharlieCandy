package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MeusPedidosActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidoAdapter: PedidoAdapter
    private val pedidos = mutableListOf<Pedido>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meus_pedidos)

        recyclerView = findViewById(R.id.recyclerViewPedidos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        pedidoAdapter = PedidoAdapter(pedidos)
        recyclerView.adapter = pedidoAdapter

        val sharedPreferences = getSharedPreferences("Dados", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        carregarPedidos(userId)
    }

    private fun carregarPedidos(userId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/") // Atualize com a URL correta
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(PedidoApiService::class.java)
        api.getPedidos(userId).enqueue(object : Callback<List<Pedido>> {
            override fun onResponse(call: Call<List<Pedido>>, response: Response<List<Pedido>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        pedidos.clear()
                        pedidos.addAll(it)
                        pedidoAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(
                        this@MeusPedidosActivity,
                        "Erro ao carregar pedidos.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Pedido>>, t: Throwable) {
                Toast.makeText(this@MeusPedidosActivity, "Falha na conexão.", Toast.LENGTH_LONG).show()
            }
        })
    }

    interface PedidoApiService {
        @GET("CHARLIE/finalizar_compras/createOrder")
        fun getPedidos(@Query("userId") userId: Int): Call<List<Pedido>>
    }
}
