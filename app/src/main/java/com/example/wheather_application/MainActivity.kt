package com.example.wheather_application

import ApiInterface
import WeatherApp
import android.os.Bundle
import android.service.notification.Condition
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.wheather_application.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.SimpleFormatter


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("mumbai" )
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }


    private fun fetchWeatherData(cityName : String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
//add api key from the home.openweathermap.org
        val response = retrofit.getWeather(cityName , "{Add your API Key}" , "matrix")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call : Call<WeatherApp>, response : Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp
                    val temperatureCelsius = temperature - 273.15
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    binding.temp.text = String.format("%.1f °C", temperatureCelsius)
                    binding.wheather.text = condition
                    binding.MaxTemp.text = String.format("Max: %.1f °C", maxTemp - 273.15)
                    binding.MinTemp.text = String.format("Min: %.1f °C", minTemp - 273.15)
                    binding.humidity.text = "$humidity"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.subset.text = "${time(sunSet)}"
                    binding.sea.text = "$sealevel hPa"
                    binding.conditions.text = "$condition"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.cityName.text = "$cityName"

                    // Log.d("TAG", "onResponse: $temperature")
                    chaneImagesAccordingToCondition(condition)
                }

            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun chaneImagesAccordingToCondition(condition: String) {
        when (condition){
            "Foggy" , "Mist" ,"Overcast" ,"Clouds", "Partially Clouds" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear Sky" ,"Sunny" , "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain" , "Drizzle" ,"Moderate Rain" , "Showers" ,"Heavy Rain" ,"Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow" , "Moderate Snow" ,"Heavy Snow","Snow" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName (timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun date ():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    fun time (timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

}
