package com.example.moviesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: MovieAdapter
    private lateinit var navigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        progressBar = findViewById(R.id.progressBar)
        adapter = MovieAdapter(mutableListOf()) { movie -> showMovieDetails(movie) }

        navigationBar = findViewById(R.id.bottom_bar)
        val fragmentController = findNavController(R.id.fragmentContainerView)
        navigationBar.setupWithNavController(fragmentController)


        getMovieData()
    }

    private fun getMovieData() {
        val apiService = APIRetrofit.getInstance().create(APIServices::class.java)
        apiService.getPopularMovies(page = 1)
            .enqueue(object : Callback<MovieResponse> {

                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>
                ) {
                    if (response.isSuccessful) {
                        progressBar.visibility = View.GONE
                        recyclerView.adapter =
                            response.body()?.movies?.let {
                                MovieAdapter(
                                    it as MutableList<Movie>,
                                    onMovieClick = { movie -> showMovieDetails(movie) })
                            }
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    val error = t.message
                    println(error)
                }
            })
    }

    private fun showMovieDetails(movie: Movie) {
        val intent = Intent(this, MovieDetails::class.java)
        intent.putExtra("MOVIE", movie)
        startActivity(intent)
    }


}

