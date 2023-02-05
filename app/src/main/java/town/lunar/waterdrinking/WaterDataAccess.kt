package town.lunar.waterdrinking

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import androidx.room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Instant? {
        return value?.let { Instant.ofEpochMilli(it) }
    }

    @TypeConverter
    fun dateToTimestamp(time: Instant?): Long? {
        return time?.toEpochMilli()
    }
}
@Entity(tableName = "sip")
data class Sip(
    @ColumnInfo(name="timestamp")
    val ts: Instant
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}

@Dao
interface SipDao {
    @Query("SELECT * FROM sip ORDER BY timestamp ASC")
    fun getAllSips(): Flow<List<Sip>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sip: Sip): Long

    @Delete
    suspend fun delete(sip: Sip)

    @Query("DELETE FROM sip")
    suspend fun deleteAll()
}

@Database(entities = [Sip::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
public abstract class SipDatabase : RoomDatabase() {

    abstract fun sipDao(): SipDao

    companion object {
        @Volatile
        private var INSTANCE: SipDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SipDatabase::class.java,
                    "sip_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// TODO: synchronize with a networked datastore
class SipRepository(private val dao: SipDao) {
    val allSips: Flow<List<Sip>> = dao.getAllSips()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(sip: Sip) {
        val newId = dao.insert(sip)
        sip.id = newId
    }

    @WorkerThread
    suspend fun delete(sip: Sip) {
        dao.delete(sip)
    }

    @WorkerThread
    suspend fun deleteAll() {
        dao.deleteAll()
    }
}

class SipViewModel(private val repository: SipRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<Sip>> = repository.allSips.asLiveData()

    fun insert(sip: Sip) = viewModelScope.launch {
        Log.v("inserting sip", sip.toString())
        repository.insert(sip)
    }

    fun delete(sip: Sip) = viewModelScope.launch {
        Log.v("deleting sip", sip.toString())
        repository.delete(sip)
    }
    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}

class WordViewModelFactory(private val repository: SipRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SipViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SipViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class WaterDrinkingApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { SipDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SipRepository(database.sipDao()) }
}