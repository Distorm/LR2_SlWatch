package com.sample.wewatch.view
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sample.wewatch.R
import com.sample.wewatch.databinding.ItemMovieMainBinding
import com.sample.wewatch.model.Movie
import com.sample.wewatch.network.RetrofitClient

import com.squareup.picasso.Picasso

import java.util.HashSet

class MainAdapter(internal var movieList: List<Movie>, internal var context: Context) : RecyclerView.Adapter<MainAdapter.MoviesHolder>() {

  // HashSet для отслеживания выбранных для удаления элементов
  val selectedMovies = HashSet<Movie>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesHolder {
    // Инициализация View Binding для элемента списка
    val binding = ItemMovieMainBinding.inflate(LayoutInflater.from(context), parent, false)
    return MoviesHolder(binding)
  }

  override fun onBindViewHolder(holder: MoviesHolder, position: Int) {
    // Получаем текущий фильм
    val movie = movieList[position]

    // Устанавливаем текст заголовка и даты выпуска
    holder.binding.titleTextview.text = movie.title
    holder.binding.releaseDateTextview.text = movie.releaseDate

    // Загрузка изображения постера фильма с использованием Picasso
    if (movie.posterPath?.isEmpty() == true) {
      holder.binding.movieImageview.setImageDrawable(context.getDrawable(R.drawable.ic_local_movies_gray))
    } else {
      Picasso.get().load(RetrofitClient.TMDB_IMAGEURL + movie.posterPath).into(holder.binding.movieImageview)
    }

    // Обработка нажатия на CheckBox
    holder.binding.checkbox.setOnClickListener {
      if (!selectedMovies.contains(movie)) {
        holder.binding.checkbox.isChecked = true
        selectedMovies.add(movie)
      } else {
        holder.binding.checkbox.isChecked = false
        selectedMovies.remove(movie)
      }
    }
  }

  override fun getItemCount(): Int {
    // Возвращаем количество элементов в списке
    return movieList.size
  }

  // Внутренний класс ViewHolder для адаптера
  inner class MoviesHolder(val binding: ItemMovieMainBinding) : RecyclerView.ViewHolder(binding.root)
}
