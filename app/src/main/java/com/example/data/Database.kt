package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- entities ---

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "competition_scores")
data class CompetitionScore(
    @PrimaryKey val id: Int = 1,
    val myScore: Int = 0,
    val friendScore: Int = 0,
    val friendName: String = "زميل الدراسة"
)

@Entity(tableName = "pharmacy_questions")
data class PharmacyQuestion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val questionText: String,
    val choicesJson: String = "", // comma-separated or custom formatting
    val correctAnswer: String,    // standard keyword or text
    val difficulty: String,       // "سهل" (Easy) or "صعب" (Hard) or "متوسط" (Medium)
    val hintForUser: String,
    val hintForFriend: String,
    val referenceSource: String   // FDA, MedlinePlus, etc.
)

// --- daos ---

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)
}

@Dao
interface CompetitionScoresDao {
    @Query("SELECT * FROM competition_scores WHERE id = 1 LIMIT 1")
    fun getScores(): Flow<CompetitionScore?>

    @Query("SELECT * FROM competition_scores WHERE id = 1 LIMIT 1")
    suspend fun getScoresSync(): CompetitionScore?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScores(scores: CompetitionScore)
}

@Dao
interface PharmacyQuestionsDao {
    @Query("SELECT * FROM pharmacy_questions")
    fun getAllQuestionsFlow(): Flow<List<PharmacyQuestion>>

    @Query("SELECT * FROM pharmacy_questions")
    suspend fun getAllQuestions(): List<PharmacyQuestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<PharmacyQuestion>)

    @Query("SELECT COUNT(*) FROM pharmacy_questions")
    suspend fun getCount(): Int
}

// --- database ---

@Database(
    entities = [UserProfile::class, CompetitionScore::class, PharmacyQuestion::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun competitionScoresDao(): CompetitionScoresDao
    abstract fun pharmacyQuestionsDao(): PharmacyQuestionsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aon_pharma_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
