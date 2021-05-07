/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.tarcisiofl.trackmysleepquality

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.tarcisiofl.trackmysleepquality.database.SleepDatabase
import br.com.tarcisiofl.trackmysleepquality.database.SleepDatabaseDao
import br.com.tarcisiofl.trackmysleepquality.database.SleepNight
import br.com.tarcisiofl.trackmysleepquality.util.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.io.IOException

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() = runBlocking {
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        assertEquals(tonight?.sleepQuality, -1)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetNight() = runBlocking {
        val night = SleepNight()
        sleepDao.insert(night)

        val tonight = sleepDao.getTonight()
        tonight?.let {
            it.sleepQuality = 5
            sleepDao.update(it)
        }
        assertEquals(tonight?.sleepQuality, 5)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAllNight() = runBlocking {
        val nights: List<SleepNight> = listOf(SleepNight(), SleepNight(), SleepNight(), SleepNight())
        nights.forEach { night -> sleepDao.insert(night) }
        val dataBaseNights = sleepDao.getAllNights().getOrAwaitValue()
        assertEquals(dataBaseNights.size, 4)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGet() = runBlocking {
        var night = SleepNight()
        sleepDao.insert(night)
        night = sleepDao.getTonight() ?: night

        val lastInsert = sleepDao.get(night.nightId)
        assertEquals(night.nightId, lastInsert?.nightId)
    }


    @Test
    @Throws(Exception::class)
    fun clear() = runBlocking {
        val nights: List<SleepNight> = listOf(SleepNight(), SleepNight(), SleepNight(), SleepNight())
        nights.forEach { night -> sleepDao.insert(night) }
        sleepDao.clear()
        val emptyTable = sleepDao.getAllNights().getOrAwaitValue()
        assertEquals(emptyTable.size, 0)
    }
}

