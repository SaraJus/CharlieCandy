package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class ListaProduto : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_produto)

        // Configurar RecyclerView com GridLayoutManager de 2 colunas
        recyclerView = findViewById(R.id.recyclerViewProdutos)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Configuração do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/CHARLIE/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Chamada da API para obter produtos
        apiService.getProdutos().enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                Log.d("Response produtos", response.toString())
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    adapter = CustomAdapter(produtos)
                    recyclerView.adapter = adapter
                } else {
                    Log.e("API Error", "Erro ao buscar produtos")
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Log.e("API Failure", "Erro ao buscar produtos", t)
            }
        })

        // Configurar o BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    Toast.makeText(this, "Home selecionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.produtos -> {
                    Toast.makeText(this, "Produtos selecionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.pedidos -> {
                    Toast.makeText(this, "Pedidos selecionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.carrinho -> {
                    Toast.makeText(this, "Carrinho selecionado", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.eu -> {
                    Toast.makeText(this, "Perfil selecionado", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    interface ApiService {
        @GET("lista_de_produtos")
        fun getProdutos(): Call<List<Produto>>
    }
}