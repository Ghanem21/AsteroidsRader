package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM databaseAsteroid ORDER BY closeApproachDate DESC")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)


    @Query("SELECT * FROM databaseAsteroid WHERE closeApproachDate = :today ORDER BY closeApproachDate DESC")
    fun getToday(today: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseAsteroid WHERE closeApproachDate BETWEEN :today AND :week ORDER BY closeApproachDate DESC")
    fun getWeek(today: String, week: String): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE FROM databaseAsteroid WHERE closeApproachDate < :today")
    fun deleteOldAsteroids(today: String)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

@Volatile
private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    if (!::INSTANCE.isInitialized) {
        synchronized(AsteroidDatabase::class.java) {
            if (!::INSTANCE.isInitialized) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AsteroidDatabase::class.java,
                    "asteroids"
                ).build()
            }
        }
    }
    return INSTANCE
}

