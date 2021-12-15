package com.example.weatherapp

import android.graphics.Color
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.parseColor("#1C1D5C")

        val lat = intent.getStringExtra("lat")
        var long = intent.getStringExtra("long")

        if (lat != null && long != null) {
            getJsonData(lat, long)
        }
    }

    private fun getJsonData(lat: String, long: String) {
        val API_KEY = "7dca1674a10aeeddb54e09b7e638a0ff"
        val queue = Volley.newRequestQueue(this)
        val cityName = getCityName(lat.toDouble(), long.toDouble())
//        call API with lat and long
//        val url =
//            "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&appid=${API_KEY}"

//        call API with city name
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=${cityName}&appid=${API_KEY}"

        try {
            val jsonRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    setValues(response)
                },
                Response.ErrorListener {
                    Toast.makeText(
                        this,
                        "Please turn on internet connection",
                        Toast.LENGTH_LONG
                    ).show()
                })


            queue.add(jsonRequest)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "ERROR" + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setValues(response: JSONObject) {
        var country = response.getJSONObject("sys").getString("country")
        address.text = response.getString("name") + ", " + country

        updated_at.text = dateFormat(response.getString("dt").toLong()).toString()

        weather.text = response.getJSONArray("weather").getJSONObject(0).getString("description")

        var tempr = response.getJSONObject("main").getString("temp")
        temp.text = "${farToCel(tempr.toFloat()).toString()}°C"

        var mintemp = response.getJSONObject("main").getString("temp_min")
        temp_min.text = "Min Temp: ${farToCel(mintemp.toFloat()).toString()}°C"

        var maxtemp = response.getJSONObject("main").getString("temp_max")
        temp_max.text = "Max Temp:  ${farToCel(maxtemp.toFloat()).toString()}°C"

        var sunsetTime = response.getJSONObject("sys").getString("sunset").toLong()
        sunset.text = timeFormat(sunsetTime)

        var sunriseTime = response.getJSONObject("sys").getString("sunrise").toLong()
        sunrise.text = timeFormat(sunriseTime)

        pressure.text = response.getJSONObject("main").getString("pressure") + " hPa"
        humidity.text = response.getJSONObject("main").getString("humidity") + "%"
        wind.text = response.getJSONObject("wind").getString("speed")
        sea_level.text = response.getJSONObject("main").getString("sea_level") + " hPa"
    }

    private fun getCityName(lat: Double, long: Double): String {
        var geoCoder = Geocoder(this, Locale.getDefault())
        var adress = geoCoder.getFromLocation(lat, long, 3)

        return adress.get(0).locality
    }

    private fun farToCel(far: Float): Int {
        return (far - 273.15).toInt()
    }

    private fun dateFormat(milisecond: Long): String {
        return SimpleDateFormat(
            "EEE, d MMM yyyy hh:mm:ss a",
            Locale.ENGLISH
        ).format(Date(milisecond * 1000))
    }

    private fun timeFormat(time: Long): String {
        val formatter: DateFormat = SimpleDateFormat("hh:mm a", Locale.US)
        return formatter.format(Date(time * 1000)).toString()
    }
}