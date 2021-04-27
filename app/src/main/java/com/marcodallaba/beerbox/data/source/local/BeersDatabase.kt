package com.marcodallaba.beerbox.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marcodallaba.beerbox.data.Beer

@Database(entities = [Beer::class], version = 1, exportSchema = false)
abstract class BeersDatabase : RoomDatabase() {

    abstract fun beersDao(): BeersDao

}