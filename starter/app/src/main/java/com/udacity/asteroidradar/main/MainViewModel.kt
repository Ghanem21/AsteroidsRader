package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

enum class AsteroidFilters {
    TODAY,
    WEEK,
    ALL
}

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(application: Application) : ViewModel() {

    private val database = getDatabase(application)
    private val repo = AsteroidsRepository(database)

    private val _filter = MutableLiveData(AsteroidFilters.ALL)

    fun updateFilter(filter: AsteroidFilters) {
        _filter.value = filter
    }

    val asteroids: LiveData<List<Asteroid>> = Transformations.switchMap(_filter) {
        when (it) {
            AsteroidFilters.TODAY -> repo.todayAsteroids
            AsteroidFilters.WEEK -> repo.lastWeekAsteroids
            else -> repo.asteroids
        }
    }

    init {
        viewModelScope.launch {
            try {
                repo.refreshAsteroids()
                getTodayPicture()

            } catch (ex: Exception) {
                Timber.d(ex.message)
            }
        }
    }


    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private suspend fun getTodayPicture() {
        withContext(Dispatchers.IO) {
            try {
                val pictureOfDay = AsteroidApi.retrofitService.getPictureOfTheDay()
                _pictureOfDay.postValue(pictureOfDay)
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }
    }


    private val _navigateToAsteroidDetails = MutableLiveData<Asteroid?>()
    val navigateToAsteroidDetails
        get() = _navigateToAsteroidDetails

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToAsteroidDetails.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToAsteroidDetails.value = null
    }


}