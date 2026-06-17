package com.example.ui

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainActivity
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AonPharmaViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "AonPharmaViewModel"

    private val database = AppDatabase.getDatabase(application)
    private val userProfileDao = database.userProfileDao()
    private val scoresDao = database.competitionScoresDao()
    private val questionsDao = database.pharmacyQuestionsDao()

    // --- State Observables ---
    val userProfile: StateFlow<UserProfile?> = userProfileDao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val competitionScores: StateFlow<CompetitionScore?> = scoresDao.getScores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val questionsList: StateFlow<List<PharmacyQuestion>> = questionsDao.getAllQuestionsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Current UI Screen ---
    private val _currentScreen = MutableStateFlow<String>("welcome_check")
    val currentScreen: StateFlow<String> = _currentScreen

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    // --- Connectivity State ---
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline

    // --- SMS Action States ---
    private val _isSmsPermissionGranted = MutableStateFlow(false)
    val isSmsPermissionGranted: StateFlow<Boolean> = _isSmsPermissionGranted

    fun setSmsPermissionGranted(granted: Boolean) {
        _isSmsPermissionGranted.value = granted
    }

    // --- Search Tab States ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _searchedMedication = MutableStateFlow<MedicationData?>(null)
    val searchedMedication: StateFlow<MedicationData?> = _searchedMedication

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError

    // --- Chat Tab States ---
    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput

    private val _chatHistory = MutableStateFlow<List<Pair<String, String>>>(emptyList()) // role to message
    val chatHistory: StateFlow<List<Pair<String, String>>> = _chatHistory

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading

    // --- Competition States ---
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex

    private val _userAnswerInput = MutableStateFlow("")
    val userAnswerInput: StateFlow<String> = _userAnswerInput

    private val _friendAnswerInput = MutableStateFlow("")
    val friendAnswerInput: StateFlow<String> = _friendAnswerInput

    private val _friendNameInput = MutableStateFlow("زميل الدراسة")
    val friendNameInput: StateFlow<String> = _friendNameInput

    private val _showHintForUser = MutableStateFlow(false)
    val showHintForUser: StateFlow<Boolean> = _showHintForUser

    private val _showHintForFriend = MutableStateFlow(false)
    val showHintForFriend: StateFlow<Boolean> = _showHintForFriend

    // checking progress animation state
    private val _isCheckingAnswer = MutableStateFlow(false)
    val isCheckingAnswer: StateFlow<Boolean> = _isCheckingAnswer

    private val _checkingProgress = MutableStateFlow(10) // 10% to 100%
    val checkingProgress: StateFlow<Int> = _checkingProgress

    private val _evaluationResult = MutableStateFlow<EvaluationResult?>(null)
    val evaluationResult: StateFlow<EvaluationResult?> = _evaluationResult

    // Registration Temp State
    val regNameInput = MutableStateFlow("")

    val medicalQuotesAr = listOf(
        "غذاءك دواؤك، ودواؤك في علمك.",
        "الوقاية خير من قنطار علاج، والتعلم الدائم يحميك.",
        "الصيدلة علم الأمان والشفاء بإذن الله.",
        "دواؤك آمن عندما تفهم تركيبه وخصائصه.",
        "العلم النافع يشفي العقول كما تشفي الأدوية الأبدان."
    )

    val medicalQuotesEn = listOf(
        "Let food be thy medicine and medicine thy food.",
        "An ounce of prevention is worth a pound of cure.",
        "Pharmacy is the art and science of healing.",
        "Your medicine is safe when you fully comprehend its profile.",
        "Knowledge cures minds just as drugs cure bodies."
    )

    init {
        createNotificationChannel()
        startNetworkMonitoring()
        prepopulateDatabaseIfEmpty()
        loadRegistrationStatus()
    }

    private fun loadRegistrationStatus() {
        viewModelScope.launch {
            userProfile.collectLatest { profile ->
                if (profile != null) {
                    _currentScreen.value = "main"
                } else {
                    _currentScreen.value = "registration"
                }
            }
        }
    }

    private fun prepopulateDatabaseIfEmpty() {
        viewModelScope.launch {
            val count = questionsDao.getCount()
            if (count == 0) {
                questionsDao.insertAll(DefaultQuestions.list)
                Log.d(TAG, "Successfully prepopulated Room database with default pharmaceutical questions.")
            }
            // Ensure scores entity is initialized
            val scores = scoresDao.getScoresSync()
            if (scores == null) {
                scoresDao.insertScores(CompetitionScore(1, 0, 0, "زميلي العزيز"))
            }
        }
    }

    // --- Search Actions ---
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun performMedicationSearch() {
        val query = _searchQuery.value.trim()
        if (query.isEmpty()) return

        _isSearching.value = true
        _searchError.value = null
        _searchedMedication.value = null

        viewModelScope.launch {
            try {
                // First: Check if the user is online, otherwise fallback to local search or local mocked data cards
                if (!_isOnline.value) {
                    // Try to simulate offline retrieval for common queried drugs to avoid dead-ends
                    val fallback = lookUpOfflineMedication(query)
                    if (fallback != null) {
                        _searchedMedication.value = fallback
                    } else {
                        _searchError.value = "عذراً، أنت أوفلاين ولم نجد معلومات هذا الدواء مخزنة محلياً. يرجى الاتصال بالإنترنت للبحث الفوري."
                    }
                    _isSearching.value = false
                    return@launch
                }

                // Call Gemini Service for structured response
                val data = GeminiService.searchMedication(query)
                if (data != null) {
                    _searchedMedication.value = data
                } else {
                    // Fallback to offline databases
                    val fallback = lookUpOfflineMedication(query)
                    if (fallback != null) {
                        _searchedMedication.value = fallback
                    } else {
                        _searchError.value = "لم يتم العثور على معلومات دقيقة وموثوقة لـ '$query' في مصادرنا المعتمدة."
                    }
                }
            } catch (e: Exception) {
                _searchError.value = "فشل البحث: ${e.localizedMessage}"
            } finally {
                _isSearching.value = false
            }
        }
    }

    private fun lookUpOfflineMedication(query: String): MedicationData? {
        val lower = query.lowercase()
        return if (lower.contains("paracetamol") || lower.contains("باراسيتامول")) {
            MedicationData(
                nameAr = "بَارَاسِيتَامُول",
                nameEn = "Paracetamol",
                drugClassAr = "مسكن للآلام ومخفض للحرارة (Analgesic & Antipyretic)",
                drugClassEn = "Analgesic and Antipyretic",
                mechanismOfActionAr = "يعمل بشكل رئيسي على تثبيط تصنيع البروستاجلاندين (Prostaglandin) في الجهاز العصبي المركزي (CNS).",
                mechanismOfActionEn = "Inhibits prostaglandin synthesis in the Central Nervous System",
                indicationsAr = "تسكين الآلام الخفيفة إلى المتوسطة (Pain relief) وخفض الحرارة (Fever reduction).",
                indicationsEn = "Mild to moderate pain, reduction of fever",
                dosageInfantsAr = "الجرعة تحسب كالتالي: (10-15 mg/kg) لكل جرعة ولا ينصح بمستحضرات الكبار.",
                dosageInfantsEn = "10 to 15 mg/kg per dose under medical supervision",
                dosageChildrenAr = "جرعة الأطفال هي (10-15 mg/kg) كل 4 إلى 6 ساعات عند الحاجة.",
                dosageChildrenEn = "10-15 mg/kg body weight every 4 to 6 hours",
                dosageAdultsAr = "جرعة البالغين هي (500 mg - 1000 mg) كل 4 إلى 6 ساعات.",
                dosageAdultsEn = "500 mg to 1000 mg every 4-6 hours as needed",
                dosageWarningAr = "يجب الحذر الشديد من تجاوز الجرعة اليومية القصوى.",
                dosageWarningEn = "Avoid exceeding maximum daily dose strictly.",
                maxDoseToxicityAr = "الجرعة القصوى هي 4 جرام (4000 mg) يومياً تفادياً للـ (Hepatotoxicity) تسمم الكبد.",
                maxDoseToxicityEn = "4000 mg daily limit. Excess risks severe Hepatotoxicity",
                contraindicationsAr = "الفشل الكبدي والكلوي الحاد والحساسية المفرطة للمادة الفعالة.",
                contraindicationsEn = "Severe liver failure, hypersensitivity to acetaminophen",
                antidoteAr = "الترياق هو أسيتيل سيستيين (N-acetylcysteine) ويعطى في المستشفى فقط للحالات الطارئة.",
                antidoteEn = "N-acetylcysteine (NAC) administered exclusively in hospital settings",
                interactionsAr = "تداخل كبدي خطير مع الكحول (Alcohol). ويزيد خطر النزيف بتداخل بسيط مع الوارفارين (Warfarin).",
                interactionsEn = "Major: Alcohol. Moderate: Warfarin",
                receptorsAr = "يعمل بشكل رئيسي عبر مستقبلات البروستاجلاندين والمسارات المركزية لإنزيمات الأكسدة الحلقية.",
                receptorsEn = "Central Cyclooxygenase (COX-3 / COX-2) pathways",
                sideEffectsCommonAr = "نادرة ولكن قد تشمل غثيان طفيف وطفح جلدي بسيط.",
                sideEffectsCommonEn = "Nausea, rash (rare)",
                sideEffectsSeriousAr = "قد يسبب متلازمات جلدية حادة مهددة للحياة مثل مستشفى (Stevens-Johnson syndrome).",
                sideEffectsSeriousEn = "Severe skin reactions including Stevens-Johnson Syndrome",
                specialWarningsAr = "يجب حتماً تعديل الجرعات لمرضى القصور الكبدي والكلوي.",
                specialWarningsEn = "Reduce dosage in hepatic or renal impairment",
                allergyTestingAr = "غير مطلوب فحص الحساسية كإجراء روتيني.",
                allergyTestingEn = "Not required",
                sourcesUsedEn = "MedlinePlus, DailyMed, FDA"
            )
        } else if (lower.contains("amoxicillin") || lower.contains("أموكسيسيلين")) {
            MedicationData(
                nameAr = "أَمُوكْسِيسِيلِين",
                nameEn = "Amoxicillin",
                drugClassAr = "مضاد حيوي من عائلة البنسلينات (Penicillin Antibiotic)",
                drugClassEn = "Penicillin Antibiotic / Beta-Lactam",
                mechanismOfActionAr = "يثبط تصنيع الجدار الخلوي البكتيري (Bacterial cell wall synthesis) مما يؤدي لموت البكتيريا البنيوية.",
                mechanismOfActionEn = "Binds to penicillin-binding proteins, inhibiting cell wall assembly",
                indicationsAr = "التهابات الجهاز التنفسي العلوي والسفلي، التهاب الأذن الوسطى، والمسالك البولية والجلد.",
                indicationsEn = "Respiratory tract infections, otitis media, skin and UTI",
                dosageInfantsAr = "يحسب بالكامل بناء على الوزن تحت إشراف طبيب الأطفال المختص.",
                dosageInfantsEn = "Based on weight and severity, pediatric consult mandatory",
                dosageChildrenAr = "عادة من (20-45 mg/kg) يومياً مقسمة على جرعات كل 12 أو 8 ساعات.",
                dosageChildrenEn = "20 to 150 mg/kg/day split into 2 or 3 doses",
                dosageAdultsAr = "الجرعة الشائعة هي (500 mg - 875 mg) كل 8 أو 12 ساعة حسب شدة العدوى.",
                dosageAdultsEn = "250 mg to 875 mg every 8 to 12 hours",
                dosageWarningAr = "يجب إكمال الوصفة كاملة لعدم تمكن البكتيريا من بناء مقاومة.",
                dosageWarningEn = "Always complete the antibiotic course fully.",
                maxDoseToxicityAr = "الجرعة اليومية تعتمد على شدة الالتهاب. التسمم يسبب تشنجات وفشل كلوي حاد.",
                maxDoseToxicityEn = "Toxicity risks central nervous system excitation and seizures",
                contraindicationsAr = "فرط الحساسية لعائلة البنسلينات أو السيفالوسبورينات (Penicillin allergy).",
                contraindicationsEn = "History of hypersensitivity to beta-lactam antibiotics",
                antidoteAr = "لا يوجد ترياق نوعي مخبري، يعالج عبر غسيل الكلى (Hemodialysis) في الطوارئ ودعم العلامات الحيوية.",
                antidoteEn = "No specific antidote. Can be cleared via hemodialysis if severe",
                interactionsAr = "يتداخل بشكل متوسط مع حبوب منع الحمل الفموية (Oral contraceptives) ومع حبوب النقرس ألوبرينول.",
                interactionsEn = "Moderate: Allopurinol (rash risk), Oral birth control (reduced efficacy)",
                receptorsAr = "يستهدف بروتينات ربط البنسلين (Penicillin-Binding Proteins - PBPs) على الخلية البكتيرية.",
                receptorsEn = "Penicillin-Binding Proteins (PBPs)",
                sideEffectsCommonAr = "إسهال (Diarrhea)، غثيان (Nausea)، طفح جلدي خفيف.",
                sideEffectsCommonEn = "Diarrhea, nausea, stomach pain, rash",
                sideEffectsSeriousAr = "صدمة الحساسية المفرطة (Anaphylaxis)، والتهاب القولون الغشائي الكاذب.",
                sideEffectsSeriousEn = "Anaphylactoid reactions, pseudomembranous colitis",
                specialWarningsAr = "يجب خفض الجرعة بدقة لمرضى الفشل الكلوي الحاد (Renal impairment).",
                specialWarningsEn = "Adjust dosage in severe renal impairment",
                allergyTestingAr = "مطلوب إجراء فحص حساسية الجلد للبنسلين للتأكد من أمان تعاطيه للمريض.",
                allergyTestingEn = "Skin prick testing for penicillin allergy is recommended if history suggests allergy.",
                sourcesUsedEn = "FDA, DailyMed, EMA"
            )
        } else {
            null
        }
    }

    // --- Chat Actions ---
    fun updateChatInput(text: String) {
        _chatInput.value = text
    }

    fun sendChatMessage() {
        val message = _chatInput.value.trim()
        if (message.isEmpty() || _isChatLoading.value) return

        val userLabel = "user"
        val updatedHistory = _chatHistory.value + Pair(userLabel, message)
        _chatHistory.value = updatedHistory
        _chatInput.value = ""
        _isChatLoading.value = true

        viewModelScope.launch {
            val name = userProfile.value?.name ?: "مستخدم عون"
            val response = GeminiService.generateChatResponse(
                userName = name,
                conversationHistory = updatedHistory.dropLast(1),
                newPrompt = message
            )
            _chatHistory.value = _chatHistory.value + Pair("model", response)
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        _chatHistory.value = emptyList()
    }

    // --- Competition / Quiz Actions ---
    fun updateUserAnswerInput(text: String) {
        _userAnswerInput.value = text
    }

    fun updateFriendAnswerInput(text: String) {
        _friendAnswerInput.value = text
    }

    fun updateFriendNameInput(text: String) {
        _friendNameInput.value = text
        viewModelScope.launch {
            val current = scoresDao.getScoresSync() ?: CompetitionScore()
            scoresDao.insertScores(current.copy(friendName = text))
        }
    }

    fun toggleHintUser() {
        _showHintForUser.value = !_showHintForUser.value
    }

    fun toggleHintFriend() {
        _showHintForFriend.value = !_showHintForFriend.value
    }

    fun checkQuizAnswers() {
        if (_isCheckingAnswer.value) return
        val questions = questionsList.value
        if (questions.isEmpty()) return

        val question = questions[_currentQuestionIndex.value]
        val userAns = _userAnswerInput.value.trim()
        val friendAns = _friendAnswerInput.value.trim()

        if (userAns.isEmpty() || friendAns.isEmpty()) {
            return
        }

        _isCheckingAnswer.value = true
        _checkingProgress.value = 10
        _evaluationResult.value = null

        viewModelScope.launch {
            // Simulated 10% to 100% magnifying lens counting progress
            for (p in 10..100 step 15) {
                _checkingProgress.value = p
                delay(150)
            }
            _checkingProgress.value = 100
            delay(100)

            val userName = userProfile.value?.name ?: "المستخدم"
            val friendName = _friendNameInput.value

            val result = GeminiService.evaluateAnswers(
                questionText = question.questionText,
                correctAnswer = question.correctAnswer,
                userAnswer = userAns,
                friendAnswer = friendAns,
                userName = userName,
                friendName = friendName
            )

            _evaluationResult.value = result

            // If the user answered correctly, award points!
            if (result.userIsCorrect) {
                val currentScores = scoresDao.getScoresSync() ?: CompetitionScore()
                scoresDao.insertScores(
                    currentScores.copy(
                        myScore = currentScores.myScore + 10,
                        friendName = friendName
                    )
                )
            }

            // If friend answered correctly and user didn't or friend simply got it right, award points to friend too
            if (result.friendIsCorrect) {
                val currentScores = scoresDao.getScoresSync() ?: CompetitionScore()
                scoresDao.insertScores(
                    currentScores.copy(
                        friendScore = currentScores.friendScore + 10,
                        friendName = friendName
                    )
                )
            }

            _isCheckingAnswer.value = false
        }
    }

    fun loadNextQuestion() {
        val size = questionsList.value.size
        if (size > 0) {
            // Cycle or pick another index. Let's make it advance sequentially or randomly as requested
            // Progressively changing levels: easy -> hard -> hard -> easy
            _currentQuestionIndex.value = (_currentQuestionIndex.value + 1) % size
            // Reset state
            _userAnswerInput.value = ""
            _friendAnswerInput.value = ""
            _showHintForUser.value = false
            _showHintForFriend.value = false
            _evaluationResult.value = null
        }
    }

    // --- Profile / Registration Actions ---
    fun registerUser() {
        val name = regNameInput.value.trim()
        if (name.isEmpty()) return

        viewModelScope.launch {
            userProfileDao.insertUserProfile(UserProfile(name = name))
            _currentScreen.value = "main"
        }
    }

    // --- SMS Action Helpers ---
    fun shareTextToSMS(context: Context, text: String, phoneNumber: String = "") {
        try {
            if (_isSmsPermissionGranted.value && phoneNumber.isNotBlank()) {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                smsManager.sendTextMessage(phoneNumber, null, text, null, null)
                Toast.makeText(context, "تم إرسال الرسالة النصية بنجاح لمصداقية التنافس!", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Sent SMS directly to $phoneNumber")
            } else {
                // Fallback to SMS Intent (Zero-dead-ends)
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$phoneNumber")
                    putExtra("sms_body", text)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                Log.d(TAG, "Opened SMS Intent fallback safely")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed sending SMS", e)
            Toast.makeText(context, "عذراً، فشل إرسال SMS تلقائياً، جاري فتح تطبيق الرسائل...", Toast.LENGTH_SHORT).show()
            // Open fallback SMS intent anyway
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    fun shareQuestionWithFriend(context: Context) {
        val questions = questionsList.value
        if (questions.isEmpty()) return
        val question = questions[_currentQuestionIndex.value]
        val message = """
            مرحباً يا صديقي! يسرني دعوتك لتحدي صيدلاني مشوق في تطبيق (Aon Pharma) عون فرما!
            سؤال اليوم: ${question.questionText}
            أجبني بسرعة لنرى من الأذكى! انشر العلم لتعم الفائدة واختبر معلوماتك!
        """.trimIndent()
        shareTextToSMS(context, message)
    }

    fun shareAdviceFromChat(context: Context, adviceText: String) {
        val userName = userProfile.value?.name ?: "مستخدم عون"
        val message = """
            نصيحة طبية قيِّمة من تطبيق عون فرما (Aon Pharma) شاركها '$userName':
            $adviceText
            انشر العلم لصديقك لتعم الفائدة وتختبر معلوماته الطبية!
        """.trimIndent()
        shareTextToSMS(context, message)
    }

    fun shareResultWithFriend(context: Context) {
        val eval = _evaluationResult.value ?: return
        val friend = _friendNameInput.value
        val questions = questionsList.value
        val question = questions.getOrNull(_currentQuestionIndex.value)?.questionText ?: "السؤال الصيدلاني"

        val myResult = if (eval.userIsCorrect) "أجبتُ أنا بشكل صحيح! 🎉" else "أخطأتُ أنا في الإجابة 😢"
        val friendResult = if (eval.friendIsCorrect) "أجبتَ أنت بشكل صحيح رائع! 🌟" else "أخطأتَ أنت في الإجابة."

        val message = """
            نتيجة تحدينا الطبي في عون فرما حول سؤال: '$question'
            - صديقي $friend: $friendResult
            - أنا: $myResult
            - الإجابة الصحيحة هي: ${eval.correctAnswerDetailed}
            - الشرح الطبي: ${eval.explanation}
            قال التطبيق لنا: "${eval.encouragementMessage}"
        """.trimIndent()

        shareTextToSMS(context, message)
    }

    // Opens Google Translate web page/app (zero-dead-end feature)
    fun openGoogleTranslate(context: Context, term: String) {
        try {
            val cleanTerm = term.replace(Regex("[()#\\\\[\\\\]]"), "").trim()
            val uri = Uri.parse("https://translate.google.com/?sl=en&tl=ar&text=${Uri.encode(cleanTerm)}&op=translate")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "فشل فتح مترجم جوجل.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Connectivity Monitoring ---
    private fun startNetworkMonitoring() {
        val conManager = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        // Initial state
        val activeNetwork = conManager.activeNetwork
        val caps = conManager.getNetworkCapabilities(activeNetwork)
        _isOnline.value = caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        conManager.registerNetworkCallback(
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    _isOnline.value = true
                    Log.d(TAG, "Network is ONLINE. Triggering pharmaceutical question notification.")
                    triggerConnectivityNotification()
                }

                override fun onLost(network: Network) {
                    _isOnline.value = false
                }
            }
        )
    }

    // Trigger local persistent notification on connection as requested
    private fun triggerConnectivityNotification() {
        val context = getApplication<Application>()
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("open_tab", "competition")
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "AonPharmaChannelID")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("💊 تحدي عون فرما اليومي")
            .setContentText("ما هي الفئة الدوائية لعلاج (Amoxicillin) أَمُوكْسِيسِيلِين؟ اضغط للمنافسة وحصد النقاط!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("💊 تحدي عون فرما اليومي:\nما هي الفئة الدوائية لعلاج (Amoxicillin) أَمُوكْسِيسِيلِين؟ اضغط للدخول مباشرة لصفحة المنافسات واختبر معلوماتك مع صديقك!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = getApplication<Application>()
            val name = "عون فرما - سؤال اليوم"
            val descriptionText = "إشعارات الأسئلة والتنافسات الصيدلانية لزيادة العلم والمنفعة"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("AonPharmaChannelID", name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
