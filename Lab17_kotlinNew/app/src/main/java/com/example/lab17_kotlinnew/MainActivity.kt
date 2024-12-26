package com.example.lab17_kotlinnew

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

private lateinit var btnQuery: Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnQuery = findViewById(R.id.btnQuery)
        btnQuery.setOnClickListener {
            btnQuery.isEnabled = false
            fetchAirQualityData()
        }


    }

    private fun displayData(myObject: MyObject?) {
        val items = myObject?.result?.records?.map { "地區：${it.SiteName}, 狀態：${it.Status}" }

        runOnUiThread {
            btnQuery.isEnabled = true
            if (items != null) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("臺北市空氣品質")
                    .setItems(items.toTypedArray(), null)
                    .show()
            }
        }
    }

    private fun fetchAirQualityData() {
        val request = Request.Builder().url("https://api.italkutalk.com/api/air").build()
        OkHttpClient().newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                btnQuery.isEnabled = true
                Toast.makeText(this@MainActivity, "查詢失敗", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body?.string()?.let {
                    val myObject = Gson().fromJson(it, MyObject::class.java)
                    displayData(myObject)
                }
            }
        })
    }
}