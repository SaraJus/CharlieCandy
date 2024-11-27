package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.text.toDouble

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalTextView: TextView
    private lateinit var goToPaymentButton: Button
    private var total: Double = 0.0
    private lateinit var cartAdapter: CartAdapter
    private var cartItems: MutableList<Produto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalTextView = findViewById(R.id.totalTextView)
        goToPaymentButton = findViewById(R.id.goToPaymentButton)

        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchCartItems()

        goToPaymentButton.setOnClickListener {
            val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("id", 0)
            val intent = Intent(this, PaymentActivity::class.java).apply {
                putExtra("TOTAL", total.toString())
                putExtra("USER", userId)
                putParcelableArrayListExtra("PRODUCT_LIST", ArrayList(cartItems))
            }
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.btnVoltar).setOnClickListener {
            finish() // Fecha a atividade atual e volta para a anterior
        }

        // Configurar o BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.produtos -> {
                    Toast.makeText(this, "Produtos selecionado", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(
                            this,
                            ListaProduto::class.java
                        )
                    ) // Navega para ProductsActivity
                    true
                }

                R.id.pedidos -> {
                    Toast.makeText(this, "Pedidos selecionado", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(
                            this,
                            MeusPedidosActivity::class.java
                        )
                    ) // Navega para Pedidos
                    true
                }

                R.id.carrinho -> {
                    Toast.makeText(this, "Carrinho selecionado", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(
                            this,
                            CartActivity::class.java
                        )
                    ) // Navega para CartActivity
                    true
                }

                else -> false
            }
        }
    }

    private fun fetchCartItems() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/CHARLIE/carrinho_de_compras/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val sharedPreferences = getSharedPreferences("Dados", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        val api = retrofit.create(CartApiService::class.java)
        api.getCartItems(userId).enqueue(object : Callback<List<Produto>> {
            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
                if (response.isSuccessful) {
                    cartItems = response.body()?.toMutableList() ?: mutableListOf()
                    setupAdapter()
                    updateTotal()
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                Toast.makeText(
                    this@CartActivity,
                    "Falha ao buscar itens do carinho. Tente novamente mais tarde.",
                    Toast.LENGTH_LONG
                ).show()

            }
        })
    }


    private fun setupAdapter() {
        cartAdapter = CartAdapter(cartItems, this) { updateTotal() }
        recyclerView.adapter = cartAdapter
    }

    private fun updateTotal() {
        total = cartItems.sumOf { cartItem ->
            val price = cartItem.produtoPreco?.toDouble() ?: 0.0
            val quantity = cartItem.quantidadeDisponivel ?: 1
            price * quantity
        }
        totalTextView.text = "Total: R$${String.format("%.2f", total)}"
    }
}
