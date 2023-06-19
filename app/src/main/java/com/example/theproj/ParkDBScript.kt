package com.example.theproj

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Entity
data class ParkDB(
    @PrimaryKey val code : Int,
    val Name : String,
    var y : Float,
    var x : Float,
    var fac : String,
    var loc : String
)

@Dao
interface ParkDBInterface {

    @Query("SELECT fac FROM ParkDB")
    suspend fun getFac() : String

    @Query("SELECT loc FROM ParkDB")
    suspend fun getLoc() : String

    @Query("SELECT Name FROM ParkDB where Name like '%' ||  :i || '%'")
    suspend fun getNN(i : String) : List<String>

    @Query("SELECT Name FROM ParkDB where loc = :i and fac like '%' || :k || '%'")
    suspend fun getNamebyLF(i : String, k : String) : List<String>

    @Query("SELECT fac FROM ParkDB where Name = :i")
    suspend fun getfacbyName(i : String) : String


    @Query("SELECT Name FROM ParkDB where loc = :i")
    suspend fun getNamebyLoc(i : String) : List<String>


    @Query("SELECT * FROM ParkDB")
    suspend fun getAll(): List<ParkDB>

    @Query("SELECT x FROM ParkDB")
    suspend fun getX() : List<Float>

    @Query("SELECT y FROM ParkDB")
    suspend fun getY() : List<Float>

    @Query("SELECT Name FROM ParkDB")
    suspend fun getName() : List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(parkDB: ParkDB)

    @Query("DELETE FROM ParkDB")
    suspend fun deleteAll()

}

@Database(entities = [ParkDB::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun parkDBInterface() : ParkDBInterface
}





