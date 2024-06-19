package com.example.weatherappcanada

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.android.gms.common.api.Status
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherappcanada.databinding.ActivityMainBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var placesClient: PlacesClient
    var city = "toronto"
    val api = "" // add API from the https://openweathermap.org/api
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


        Places.initialize(applicationContext, "YOUR_API_KEY")

        // Initialize the Places Client
        val placesClient = Places.createClient(this)

        // Set up Autocomplete for the city search field
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.actv_city_search) as? AutocompleteSupportFragment
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS))
            autocompleteFragment.setCountries("CA") // Restrict search to Canada
            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    // Handle the selected city
                    binding.actvCitySearch.setText(place.name)
                }

                override fun onError(status: Status) {
                    // Handle the error
                    Log.e("PlacesAPI", "An error occurred: $status")
                }
            })

            // Set the default city to Toronto, Canada
            autocompleteFragment.setText("Toronto, Canada")
        } else {
            // Handle the case where the AutocompleteSupportFragment is not found
            Log.e("PlacesAPI", "Unable to find AutocompleteSupportFragment")
        }










        binding.btnNewCity.setOnClickListener {
         // city  = binding.actvcitysearch.text.toString().toLowerCase().trim()
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
                val updated = jsonObject.getLong("dt")
                val updatedText = "Updated At " + SimpleDateFormat("dd/MM/YYYY hh:mm a", Locale.ENGLISH).format(
                    Date(updated*1000)
                )
                val temp = "" + kotlin.math.floor(main.getString("temp").toFloat()) + "° C"
                val mintemp = "Min " + kotlin.math.ceil(main.getString("temp_min").toFloat()) +  "° C"
                val feelLike = "Feels Like " + main.getString("feels_like") + "° C"
                val maxtemp = "Max " + kotlin.math.ceil(main.getString("temp_max").toFloat()) +  "° C"


                binding.status.text  =  weatherStatus.capitalize()
                binding.temp.text = temp
                binding.updatedAddress.text = updatedText
                binding.mintemp.text = mintemp
                binding.maxtemp.text = maxtemp
                binding.address.text = cityName + "," + syss.getString("country")
                binding.feelslike.text = feelLike
                binding.loader.visibility = View.GONE
                binding.mainLauout.visibility = View.VISIBLE
                binding.addContainer.visibility = View.VISIBLE
            }
            catch (e : Exception){
                binding.errorTxt.visibility = View.VISIBLE
                binding.loader.visibility = View.GONE
                binding.addContainer.visibility = View.INVISIBLE
                binding.mainLauout.visibility = View.VISIBLE
                println(e)
            }
        }
        }

    }


