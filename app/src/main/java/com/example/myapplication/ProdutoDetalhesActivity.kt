package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class ProdutoDetalhesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagina_produto)

        val nomeProduto = intent.getStringExtra("NOME_PRODUTO") ?: "Nome não disponível"
        val descricaoProduto =
            intent.getStringExtra("DESCRICAO_PRODUTO") ?: "Descrição não disponível"
        val produtoId = intent.getIntExtra("ID_PRODUTO", 0)
        val quantidadeDisponivel = intent.getIntExtra("QUANTIDADE_DISPONIVEL", 0)
        val imagemUrl = intent.getStringExtra("IMAGEM_URL")

        findViewById<ImageView>(R.id.btnVoltar).setOnClickListener {
            finish() // Fecha a atividade atual e volta para a anterior
        }
        findViewById<TextView>(R.id.txtNomeProduto).text = nomeProduto
        findViewById<TextView>(R.id.txtDescricaoProduto).text = descricaoProduto
        findViewById<TextView>(R.id.txtQuantidadeDisponivel).text = "Estoque: $quantidadeDisponivel"

        val imgProduto = findViewById<ImageView>(R.id.imgProduto)
        if (!imagemUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagemUrl)
                .placeholder(R.drawable.ic_launcher_background) // Adicione uma imagem de placeholder
                .error(R.drawable.ic_launcher_foreground) // Adicione uma imagem de erro
                .into(imgProduto)
        } else {
            imgProduto.setImageResource(R.drawable.ic_launcher_background) // Mostra uma imagem de erro caso a URL esteja vazia
        }


        val editTextQuantidade = findViewById<EditText>(R.id.editQuantidadeDesejada)
        val btnAdicionarCarrinho = findViewById<Button>(R.id.btnAdicionarAoCarrinho)

        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        btnAdicionarCarrinho.setOnClickListener {
            val quantidadeDesejada = editTextQuantidade.text.toString().toIntOrNull() ?: 0
            adicionarAoCarrinho(userId, produtoId, quantidadeDesejada)
        }

        // Configuração do ícone do carrinho
        val btnIrCarrinho: ImageView = findViewById(R.id.btnIrCarrinho)
        btnIrCarrinho.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    private fun adicionarAoCarrinho(userId: Int, produtoId: Int, quantidade: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/CHARLIE/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.adicionarAoCarrinho(userId, produtoId, quantidade).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@ProdutoDetalhesActivity,
                        response.body() ?: "Sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@ProdutoDetalhesActivity, ListaProduto::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@ProdutoDetalhesActivity,
                        "Resposta não bem-sucedida",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(
                    this@ProdutoDetalhesActivity,
                    "Erro na API: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    interface ApiService {
        @FormUrlEncoded
        @POST("manter_produto_ao_carrinho/")
        fun adicionarAoCarrinho(
            @Field("userId") userId: Int,
            @Field("produtoId") produtoId: Int,
            @Field("quantidade") quantidade: Int
        ): Call<String>
    }
}
