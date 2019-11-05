package kr.co.gadaga.official.data.db

/*
@Database(entities = [], version = 1)
abstract class GadagaDatabase: RoomDatabase() {

    companion object {
        @Volatile private var instance: GadagaDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: Room.databaseBuilder(context.applicationContext, GadagaDatabase::class.java, "gadaga.db").build()
                .also { instance = it }
        }
    }
}
*/