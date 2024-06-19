package com.example.weatherappcanada

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherappcanada.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var city = "toronto"
    val api = "0fd0b8e47a6bb2794abfef589eac6408"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide the status bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        // Hide the action bar (if present)
        supportActionBar?.hide()

        // For Android versions below 4.1 (API level 16)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addContainer.visibility = View.INVISIBLE


        binding.btnNewCity.setOnClickListener {

            city  = binding.newCity.text.toString().toLowerCase().trim()
            println("test " + city)
            weatherTask().execute()
        }



    }

    override fun onStart() {
        super.onStart()
        weatherTask().execute()
    }
    inner class weatherTask() : AsyncTask<String,Void,String>(){

        override fun onPreExecute() {
            super.onPreExecute()
            binding.loader.visibility = View.VISIBLE

            binding.mainLauout.visibility = View.INVISIBLE
            binding.errorTxt.visibility = View.GONE

        }

        override fun doInBackground(vararg params: String?): String? {
            var response : String?
            try {
                response = (URL("https://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&appid=$api").readText(Charsets.UTF_8)).toString()

                // Hide the status bar
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

                // Hide the action bar (if present)
                supportActionBar?.hide()


               // onUpdate(response)

            }
            catch (e : Exception)
            {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                println("test " + result)
                val jsonObject = JSONObject(result)
                val main = jsonObject.getJSONObject("main")
                val syss = jsonObject.getJSONObject("sys")
                val wind = jsonObject.getJSONObject("wind")
                val cityName = jsonObject.getString("name")
                val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
                val weatherStatus = weather.getString("description")
                val updated= jsonObject.getLong("dt")
                val updatedText = SimpleDateFormat("dd/MM/YYYY hh:mm a", Locale.ENGLISH).format(
                    Date(updated*1000)
                )

                val sunrise =  SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(syss.getLong("sunrise")*1000)
                )
                val sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                    Date(syss.getLong("sunset")*1000)
                )

                val windtxt = wind.getString("speed")
                val temp = "" + kotlin.math.floor(main.getString("temp").toFloat()) + "° C"
                val mintemp = "Min " + kotlin.math.ceil(main.getString("temp_min").toFloat()) +  "° C"
                val feelLike = "Feels Like " + main.getString("feels_like") + "° C"
                val maxtemp = "Max " + kotlin.math.ceil(main.getString("temp_max").toFloat()) +  "° C"
                val humidity = main.getString("humidity")
                val pressure =  main.getString("pressure")


                binding.status.text  =  weatherStatus.capitalize()
                binding.temp.text = temp
                binding.updatedAddress.text = updatedText
                binding.mintemp.text = mintemp
                binding.maxtemp.text = maxtemp
                binding.address.text = cityName + "," + syss.getString("country")
                binding.feelslike.text = feelLike
                binding.humidityTxt.text = humidity
                binding.uvTxt.text = pressure
                binding.windTxt.text = String.format("%.2f", windtxt.toFloat() * 3.6)    + "  km/h"
                binding.sunsetTimeTxt.text = sunset
                binding.sunriseTimeTxt.text = sunrise



                binding.loader.visibility = View.GONE
                binding.mainLauout.visibility = View.VISIBLE
                binding.addContainer.visibility = View.VISIBLE
            }
            catch (e : Exception){
                binding.errorTxt.visibility = View.VISIBLE
                binding.loader.visibility = View.GONE
                binding.mainLauout.visibility = View.VISIBLE
                binding.addContainer.visibility = View.INVISIBLE

                println(e)
            }
        }
        }

    }


