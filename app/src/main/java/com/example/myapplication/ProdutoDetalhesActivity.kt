package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
        val descricaoProduto = intent.getStringExtra("DESCRICAO_PRODUTO") ?: "Descrição não disponível"
        val precoProduto = intent.getStringExtra("PRECO_PRODUTO") ?: "0,00"
        val descontoProduto = intent.getStringExtra("DESCONTO_PRODUTO") ?: "0,00"
        val produtoId = intent.getIntExtra("ID_PRODUTO", 0)
        val quantidadeDisponivel = intent.getIntExtra("QUANTIDADE_DISPONIVEL", 0)
        val imagemUrl = intent.getStringExtra("IMAGEM_URL")

        // Configurar elementos da interface
        findViewById<TextView>(R.id.txtNomeProduto).text = nomeProduto
        findViewById<TextView>(R.id.txtDescricaoProduto).text = descricaoProduto
        findViewById<TextView>(R.id.txtPrecoProduto).text = "Preço: R$ $precoProduto"
        findViewById<TextView>(R.id.txtDescontoProduto).text = "Desconto: $descontoProduto"
        findViewById<TextView>(R.id.txtQuantidadeDisponivel).text = "Estoque: $quantidadeDisponivel"

        val imgProduto = findViewById<ImageView>(R.id.imgProduto)
        if (!imagemUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagemUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(imgProduto)
        }

        findViewById<ImageView>(R.id.btnVoltar).setOnClickListener {
            finish() // Fecha a atividade atual e volta para a anterior
        }

        // Configurar botões de quantidade
        val btnIncrease = findViewById<ImageView>(R.id.btnIncrease)
        val btnDecrease = findViewById<ImageView>(R.id.btnDecrease)
        val txtQuantity = findViewById<TextView>(R.id.txtQuantity)

        btnIncrease.setOnClickListener {
            val currentQuantity = txtQuantity.text.toString().toInt()
            if (currentQuantity < quantidadeDisponivel) {
                txtQuantity.text = (currentQuantity + 1).toString()
            } else {
                Toast.makeText(this, "Quantidade máxima disponível no estoque alcançada!", Toast.LENGTH_SHORT).show()
            }
        }

        btnDecrease.setOnClickListener {
            val currentQuantity = txtQuantity.text.toString().toInt()
            if (currentQuantity > 1) {
                txtQuantity.text = (currentQuantity - 1).toString()
            } else {
                Toast.makeText(this, "A quantidade mínima é 1!", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar botão "Adicionar ao Carrinho"
        val btnAdicionarCarrinho = findViewById<Button>(R.id.btnAdicionarAoCarrinho)
        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        btnAdicionarCarrinho.setOnClickListener {
            val quantidadeDesejada = txtQuantity.text.toString().toInt()
            if (quantidadeDesejada > 0 && quantidadeDesejada <= quantidadeDisponivel) {
                adicionarAoCarrinho(userId, produtoId, quantidadeDesejada)
            } else {
                Toast.makeText(this, "Insira uma quantidade válida antes de adicionar ao carrinho!", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar botão "Ir para o Carrinho"
        findViewById<ImageView>(R.id.btnIrCarrinho).setOnClickListener {
            if (userId != 0) {
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("userId", userId)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            }
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
                    Toast.makeText(this@ProdutoDetalhesActivity, response.body() ?: "Sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ProdutoDetalhesActivity, "Resposta não bem-sucedida", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@ProdutoDetalhesActivity, "Erro na API: ${t.message}", Toast.LENGTH_SHORT).show()
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
