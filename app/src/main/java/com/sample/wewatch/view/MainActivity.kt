package com.sample.wewatch.view
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.wewatch.R
import com.sample.wewatch.contract.AddMovieActivity
import com.sample.wewatch.databinding.ActivityMainBinding
import com.sample.wewatch.model.LocalDataSource
import com.sample.wewatch.model.Movie
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

  // Инициализация переменной для View Binding
  private lateinit var binding: ActivityMainBinding
  private var adapter: MainAdapter? = null

  private lateinit var dataSource: LocalDataSource
  private val compositeDisposable = CompositeDisposable()

  private val TAG = "MainActivity"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Инициализация View Binding
    binding = ActivityMainBinding.inflate(layoutInflater)

    // Установка содержимого активности с использованием корневого элемента из binding
    setContentView(binding.root)

    setupViews()
  }

  override fun onStart() {
    super.onStart()
    dataSource = LocalDataSource(application)
    getMyMoviesList()
  }

  override fun onStop() {
    super.onStop()
    compositeDisposable.clear()
  }

  private fun setupViews() {
    // Использование View Binding для доступа к элементам макета
    binding.moviesRecyclerview.layoutManager = LinearLayoutManager(this)
    binding.fab.setOnClickListener { goToAddMovieActivity(it) }
    supportActionBar?.title = "Фильмы для просмотра"
  }

  private fun getMyMoviesList() {
    val myMoviesDisposable = myMoviesObservable
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeWith(observer)

    compositeDisposable.add(myMoviesDisposable)
  }

  private val myMoviesObservable: Observable<List<Movie>>
    get() = dataSource.allMovies

  private val observer: DisposableObserver<List<Movie>>
    get() = object : DisposableObserver<List<Movie>>() {

      override fun onNext(movieList: List<Movie>) {
        displayMovies(movieList)
      }

      override fun onError(@NonNull e: Throwable) {
        Log.d(TAG, "Ошибка $e")
        e.printStackTrace()
        displayError("Ошибка при получении списка фильмов")
      }

      override fun onComplete() {
        Log.d(TAG, "Завершено")
      }
    }

  fun displayMovies(movieList: List<Movie>?) {
    if (movieList == null || movieList.isEmpty()) {
      Log.d(TAG, "Нет фильмов для отображения")
      binding.moviesRecyclerview.visibility = INVISIBLE
      binding.noMoviesLayout.visibility = VISIBLE
    } else {
      adapter = MainAdapter(movieList, this@MainActivity)
      binding.moviesRecyclerview.adapter = adapter

      binding.moviesRecyclerview.visibility = VISIBLE
      binding.noMoviesLayout.visibility = INVISIBLE
    }
  }

  fun goToAddMovieActivity(v: View) {
    val myIntent = Intent(this@MainActivity, AddMovieActivity::class.java)
    startActivityForResult(myIntent, ADD_MOVIE_ACTIVITY_REQUEST_CODE)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ADD_MOVIE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      showToast("Фильм успешно добавлен.")
    } else {
      displayError("Не удалось добавить фильм.")
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.deleteMenuItem) {
      val adapter = this.adapter
      if (adapter != null) {
        for (movie in adapter.selectedMovies) {
          dataSource.delete(movie)
        }
        if (adapter.selectedMovies.size == 1) {
          showToast("Фильм удален")
        } else if (adapter.selectedMovies.size > 1) {
          showToast("Фильмы удалены")
        }
      }
    }
    return super.onOptionsItemSelected(item)
  }

  fun showToast(str: String) {
    Toast.makeText(this@MainActivity, str, Toast.LENGTH_LONG).show()
  }

  fun displayError(e: String) {
    showToast(e)
  }

  companion object {
    const val ADD_MOVIE_ACTIVITY_REQUEST_CODE = 1
  }
}
