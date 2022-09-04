package com.udacity.asteroidradar.work

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository

@RequiresApi(Build.VERSION_CODES.O)
class RefreshDataWorker (appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repo = AsteroidsRepository(database)

        return try {
            repo.refreshAsteroids()
            Result.success()
        }catch (ex:Exception){
            Result.retry()
        }
    }
}