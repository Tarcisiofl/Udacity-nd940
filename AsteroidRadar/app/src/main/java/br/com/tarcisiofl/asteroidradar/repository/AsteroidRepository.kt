package br.com.tarcisiofl.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import br.com.tarcisiofl.asteroidradar.api.getTodaysDate
import br.com.tarcisiofl.asteroidradar.database.AsteroidsDatabase
import br.com.tarcisiofl.asteroidradar.database.asDomainModel
import br.com.tarcisiofl.asteroidradar.domain.Asteroid
import br.com.tarcisiofl.asteroidradar.network.Network
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidRepository(
    private val database: AsteroidsDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(ioDispatcher) {
            val asteroids = Network.nasa.getAsteroidsListAsync(getTodaysDate()).await()
            database.asteroidDao.insertAll(asteroids.asDatabaseModel())
        }
    }
}