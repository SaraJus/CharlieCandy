package com.example.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.google.gson.GsonBuilder

class PaymentActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        sharedPreferences = getSharedPreferences("Dados", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", 0)

        val totalValue = intent.getStringExtra("TOTAL")?.toDoubleOrNull()
        val productList = intent.getParcelableArrayListExtra<Produto>("PRODUCT_LIST")

        findViewById<TextView>(R.id.totalValueText).text = "Total: $totalValue"

        val cardNumberInput: EditText = findViewById(R.id.cardNumberInput)
        val cardExpirationInput: EditText = findViewById(R.id.cardExpirationInput)
        val cardCVCInput: EditText = findViewById(R.id.cardCVCInput)
        val finishPaymentButton: Button = findViewById(R.id.finishPaymentButton)

        radioGroup = findViewById(R.id.addressRadioGroup)

        // Carregar endereÃƒÂ§os
        loadUserAddresses(userId)

        // Configurar botÃ£o para finalizar pagamento
        finishPaymentButton.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedAddressId = selectedRadioButton.tag.toString().toInt()
                enviaOrdem(userId, totalValue ?: 0.0, productList, selectedAddressId)
            } else {
                Toast.makeText(this, "Por favor, selecione um endereÃƒÂ§o", Toast.LENGTH_LONG)
                    .show()
            }
        }

        findViewById<ImageView>(R.id.btnVoltar).setOnClickListener {
            finish() // Fecha a atividade atual e volta para a anterior
        }
    }

    private fun loadUserAddresses(userId: Int) {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/") // Atualize com a URL correta
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val api = retrofit.create(OrderApiService::class.java)
        val call = api.getUserAddresses(userId)
        call.enqueue(object : Callback<List<Endereco>> {
            override fun onResponse(
                call: Call<List<Endereco>>,
                response: Response<List<Endereco>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { addresses ->
                        populateAddressRadioButtons(addresses)
                    }
                } else {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Erro ao carregar endereços.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Endereco>>, t: Throwable) {
                Toast.makeText(this@PaymentActivity, "Falha na conexão.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun populateAddressRadioButtons(addresses: List<Endereco>) {
        addresses.forEach { address ->
            val radioButton = RadioButton(this)
            radioButton.text =
                "${address.ENDERECO_LOGRADOURO}, ${address.ENDERECO_NUMERO} - ${address.ENDERECO_COMPLEMENTO}, ${address.ENDERECO_CIDADE} - ${address.ENDERECO_ESTADO}, ${address.ENDERECO_CEP}"
            radioButton.tag = address.ENDERECO_ID
            radioGroup.addView(radioButton)
        }
    }

    private fun enviaOrdem(
        userId: Int,
        total: Double,
        products: ArrayList<Produto>?,
        addressId: Int
    ) {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.thyagoquintas.com.br/") // Atualize com a URL correta
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val api = retrofit.create(OrderApiService::class.java)
        val orderRequest = OrderRequest(userId, total, products ?: arrayListOf(), addressId)
        val call = api.createOrder(orderRequest)
        call.enqueue(object : Callback<ResponseCompra> {
            override fun onResponse(
                call: Call<ResponseCompra>,
                response: Response<ResponseCompra>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Pedido realizado com sucesso!",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@PaymentActivity, SucessActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Erro ao realizar pedido",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseCompra>, t: Throwable) {
                Toast.makeText(this@PaymentActivity, "Falha na conexão", Toast.LENGTH_LONG).show()
            }
        })
    }

    interface OrderApiService {
        @GET("CHARLIE/finalizar_compra/getUserAddresses")
        fun getUserAddresses(@Query("userId") userId: Int): Call<List<Endereco>>

        @POST("CHARLIE/finalizar_compra/createOrder/index.php")
        fun createOrder(@Body orderRequest: OrderRequest): Call<ResponseCompra>
    }

    data class Endereco(
        val ENDERECO_ID: Int,
        val ENDERECO_LOGRADOURO: String,
        val ENDERECO_NUMERO: String,
        val ENDERECO_COMPLEMENTO: String,
        val ENDERECO_CIDADE: String,
        val ENDERECO_ESTADO: String,
        val ENDERECO_CEP: String
    )

    data class OrderRequest(
        val userId: Int,
        val total: Double,
        val products: ArrayList<Produto>,
        val addressId: Int
    )

    data class ResponseCompra(
        val status: String,
        val code: Int,
        val message: String
    )
}
