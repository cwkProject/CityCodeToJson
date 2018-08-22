package com.example.citycodetojson

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        convert.setOnClickListener {
            onConvert()
        }
    }

    /**
     * 执行转换
     */
    private fun onConvert() {
        thread {

            val jsonText = resources.assets.open("adcode.txt").bufferedReader().use { it.readText() }

            val gson = GsonBuilder().create()

            val cityList = gson.fromJson<List<City>>(jsonText, object : TypeToken<List<City>>() {}.type)

            val cityMap = cityList.groupBy {
                when {
                    it.id.endsWith("0000") -> "province"
                    it.id.endsWith("00") -> "city"
                    else -> "district"
                }
            }

            zip(cityMap["city"]!!, cityMap["district"]!!, 4)

            cityMap["city"]!!.forEach {
                if (it.cityList == null) {
                    it.cityList = ArrayList()
                    it.cityList?.add(City(it.id, " "))
                }
            }

            zip(cityMap["province"]!!, cityMap["city"]!!, 2)

            val newJson = gson.toJson(cityMap["province"])

            val path = externalCacheDir.path + File.separator + "china_city_data.json"

            File(path).outputStream().bufferedWriter().use { it.write(newJson) }

            runOnUiThread {
                Toast.makeText(this, "转换完成", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 合并元素，将child添加到对应parent的cityList字段
     */
    private fun zip(parent: List<City>, child: List<City>, sampling: Int) {
        child.forEach { c ->
            parent.find { it.id.startsWith(c.id.take(sampling)) }?.let {
                if (it.cityList == null) {
                    it.cityList = ArrayList()
                }

                it.cityList?.add(c)
            }
        }
    }
}
