package com.udacity.asteroidradar.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class AsteroidsRepository(private val database: AsteroidDatabase) {

    private val current = LocalDate.now()
    private val formatter = DateTimeFormatter.ofPattern(Constants.API_QUERY_DATE_FORMAT)

    private val today = current.format(formatter)
    private val week = current.plusDays(7).format(formatter)

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    val todayAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getToday(today)) {
            it.asDomainModel()
        }

    val lastWeekAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getWeek(today,week)) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                database.asteroidDao.deleteOldAsteroids(today)
                val str = AsteroidApi.retrofitService.getAsteroid()
                val asteroids = parseAsteroidsJsonResult(JSONObject(str))
                database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}