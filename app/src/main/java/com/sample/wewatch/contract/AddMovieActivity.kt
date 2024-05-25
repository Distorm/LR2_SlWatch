package com.sample.wewatch.contract

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sample.wewatch.databinding.ActivityAddMovieBinding
import com.sample.wewatch.model.LocalDataSource
import com.sample.wewatch.model.Movie
import com.sample.wewatch.view.SearchActivity

open class AddMovieActivity : AppCompatActivity() {

  // Инициализация переменной для View Binding
  private lateinit var binding: ActivityAddMovieBinding
  private lateinit var dataSource: LocalDataSource

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Инициализация View Binding
    binding = ActivityAddMovieBinding.inflate(layoutInflater)

    // Установка содержимого активности с использованием корневого элемента из binding
    setContentView(binding.root)

    dataSource = LocalDataSource(application)

    setupViews()
  }

  // Метод для инициализации представлений
  private fun setupViews() {
    // Назначение onClickListener для кнопки поиска
    binding.searchButton.setOnClickListener { goToSearchMovieActivity() }

    // Назначение onClickListener для кнопки добавления фильма
    binding.addMovieButton.setOnClickListener { onClickAddMovie() }
  }

  // Обработка нажатия на кнопку поиска
  private fun goToSearchMovieActivity() {
    val title = binding.movieTitle.text.toString()
    val intent = Intent(this@AddMovieActivity, SearchActivity::class.java)
    intent.putExtra(SearchActivity.SEARCH_QUERY, title)
    startActivityForResult(intent, SEARCH_MOVIE_ACTIVITY_REQUEST_CODE)
  }

  // Обработка нажатия на кнопку добавления фильма
  private fun onClickAddMovie() {
    if (TextUtils.isEmpty(binding.movieTitle.text)) {
      showToast("Название фильма не может быть пустым")
    } else {
      val title = binding.movieTitle.text.toString()
      val releaseDate = binding.movieReleaseDate.text.toString()
      val posterPath = if (binding.movieImageview.tag != null) binding.movieImageview.tag.toString() else ""

      val movie = Movie(title = title, releaseDate = releaseDate, posterPath = posterPath)
      dataSource.insert(movie)

      setResult(Activity.RESULT_OK)
      finish()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    this@AddMovieActivity.runOnUiThread {
      binding.movieTitle.setText(data?.getStringExtra(SearchActivity.EXTRA_TITLE))
      binding.movieReleaseDate.setText(data?.getStringExtra(SearchActivity.EXTRA_RELEASE_DATE))
      binding.movieImageview.tag = data?.getStringExtra(SearchActivity.EXTRA_POSTER_PATH)
    }
  }

  // Показ Toast сообщения
  private fun showToast(string: String) {
    Toast.makeText(this@AddMovieActivity, string, Toast.LENGTH_LONG).show()
  }

  companion object {
    const val SEARCH_MOVIE_ACTIVITY_REQUEST_CODE = 2
  }
}
