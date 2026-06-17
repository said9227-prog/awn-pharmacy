package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    /**
     * Checks if the Gemini API Key is available and non-empty.
     */
    fun isApiKeyAvailable(): Boolean {
        val key = getApiKey()
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY" && !key.startsWith("placeholder", ignoreCase = true)
    }

    /**
     * Generates a freeform text response (used for the advanced chat).
     */
    suspend fun generateChatResponse(
        userName: String,
        conversationHistory: List<Pair<String, String>>, // list of role to content
        newPrompt: String
    ): String = withContext(Dispatchers.IO) {
        if (!isApiKeyAvailable()) {
            return@withContext "عذراً، لم يتم العثور على مفتاح API الخاص بـ Gemini في إعدادات التطبيق. يرجى إضافته عبر لوحة الأسرار (Secrets) لتفعيل الدردشة الحية."
        }

        try {
            val endpoint = "$BASE_URL/v1beta/models/$MODEL_NAME:generateContent?key=${getApiKey()}"
            val requestJson = JSONObject()

            // System Instruction to force the requested Arabic/English medical terminology formatting
            val systemInstruction = """
                أنت مساعد صيدلاني ذكي وأمين لتطبيق اسمه "عون فرما" (Aon Pharma). 
                اسم المستخدم الذي تخاطبه هو: '$userName'. يجب أن ترحب به باسمه دائماً بعبارات طبية تشجيعية دافئة.
                قواعد هامة جداً جداً للإجابة والمظهر:
                1. يجب أن تكون جميع إجاباتك طبية موثوقة وعلمية 100% ومستقاة من المصادر العالمية المعتمدة مثل FDA و EMA و PubMed و MedlinePlus.
                2. ممنوع التزييف أو التأليف الطبي. إن لم تتوفر المعلومة، قل بوضوح: "هذه المعلومة غير متوفرة في المصادر الطبية الموثوقة لدينا".
                3. لغة التطبيق هي العربية بالكامل. ولكن هناك شرط حاسم جداً:
                   أية مصطلحات طبية أو أسماء العلاجات والمواد الفعّالة كافّة، يجب كتابتها بالصيغة التالية:
                   بين قوسين يُكتب المصطلح بالإنجليزية أولاً، متبوعاً فوراً بالنطق أو الاسم باللغة العربية مع إدراج الحركات الإعرابية والتشكيل (الضم والفتح والكسر والشدة) بدقة وحرص شديدين لكي يتمكن القارئ من قراءتها بشكل ممتاز.
                   مثال: (Paracetamol) بَارَاسِيتَامُول، أو (Antibiotic) مُضَادٌّ حَيَوِيٌّ.
                4. اجعل الإجابات منسقة وجميلة ومنظمة بنقاط بسيطة دون لغو زائد. لا تقدم نصائح دوائية خطيرة دون تنبيه المستخدم لضرورة استشارة طبيبه المختص.
            """.trimIndent()

            val contentsArray = JSONArray()

            // Add history
            for (turn in conversationHistory) {
                val turnObj = JSONObject()
                turnObj.put("role", if (turn.first == "user") "user" else "model")
                val partsObj = JSONObject()
                partsObj.put("text", turn.second)
                val partsArray = JSONArray().apply { put(partsObj) }
                turnObj.put("parts", partsArray)
                contentsArray.put(turnObj)
            }

            // Add current prompt
            val promptObj = JSONObject()
            promptObj.put("role", "user")
            val partsObj = JSONObject()
            partsObj.put("text", newPrompt)
            val partsArray = JSONArray().apply { put(partsObj) }
            promptObj.put("parts", partsArray)
            contentsArray.put(promptObj)

            requestJson.put("contents", contentsArray)

            // Add system instruction
            val systemInstructionObj = JSONObject()
            val sysPartsObj = JSONObject()
            sysPartsObj.put("text", systemInstruction)
            val sysPartsArray = JSONArray().apply { put(sysPartsObj) }
            systemInstructionObj.put("parts", sysPartsArray)
            requestJson.put("systemInstruction", systemInstructionObj)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext "خطأ في الشبكة أو رمز الاستجابة: ${response.code}. تأكد من اتصالك بالإنترنت وصحة مفتاح API."
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            return@withContext text ?: "لم يتم العثور على رد مناسب."
        } catch (e: Exception) {
            Log.e(TAG, "Error generating chat content", e)
            return@withContext "حدث خطأ غير متوقع أثناء الاتصال بـ Gemini: ${e.localizedMessage}. تفقد اتصال الإنترنت الخاص بك."
        }
    }

    /**
     * Searches for a medication and returns structured information matching the Drug Card requirements.
     */
    suspend fun searchMedication(medicationQuery: String): MedicationData? = withContext(Dispatchers.IO) {
        if (!isApiKeyAvailable()) {
            return@withContext null
        }

        try {
            val endpoint = "$BASE_URL/v1beta/models/$MODEL_NAME:generateContent?key=${getApiKey()}"
            val requestJson = JSONObject()

            val prompt = """
                ابحث عن الدواء المسمى: "$medicationQuery" وصغ إجابة علمية دقيقة وموثوقة عنه مأخوذة من أعلى المراجع الطبية (FDA, EMA, DailyMed).
                يجب أن ترجع النتيجة كـ JSON كائن واحد متوافق تماماً مع هذا الهيكل الحرفي:
                {
                  "nameAr": "اسم العلاج باللغة العربية مع مراعاة دقة الضبط والتشكيل والضم والفتح مثل: بَارَاسِيتَامُول",
                  "nameEn": "اسم العلاج بالإنجليزية مثل: Paracetamol",
                  "drugClassAr": "مثال: مُسَكِّن لِلآلَام وَمُخَفِّض لِلْحَرَارَة",
                  "drugClassEn": "مثال: Analgesic and Antipyretic",
                  "mechanismOfActionAr": "شرح مبسط لآلية عمل الدواء بالعربية مشكل ومضبوط",
                  "mechanismOfActionEn": "Mechanism of Action in English",
                  "indicationsAr": "دواعي الاستعمال المعتمدة بالعربية",
                  "indicationsEn": "Approved indications in English",
                  "dosageInfantsAr": "الجرعة للرضع بالعربية وإذا غير مسموح وضح ذلك",
                  "dosageInfantsEn": "Dosage for infants in English",
                  "dosageChildrenAr": "الجرعة للأطفال بالعربية",
                  "dosageChildrenEn": "Dosage for children in English",
                  "dosageAdultsAr": "الجرعة للبالغين بالعربية",
                  "dosageAdultsEn": "Dosage for adults in English",
                  "maxDoseToxicityAr": "الجرعة القصوى اليومية وأعراض التسمم بالعربية",
                  "maxDoseToxicityEn": "Maximum dose and toxicity symptoms in English",
                  "contraindicationsAr": "موانع الاستعمال بالتفصيل خاصة الحمل، الرضاعة، الضغط، السكري، أمراض الكلى والكبد بالعربية",
                  "contraindicationsEn": "Contraindications in English",
                  "antidoteAr": "المضاد في حال جرعة زائدة (مع ذكر أنها تعطى في المستشفى فقط) وإذا لم يوجد وضح ذلك",
                  "antidoteEn": "Antidote details if exists in English",
                  "interactionsAr": "التداخلات الدوائية مقسمة إلى: خطرة (Major)، متوسطة (Moderate)، بسيطة (Minor) مع توضيح التأثير بالعربية",
                  "interactionsEn": "Drug interactions categorized in English",
                  "receptorsAr": "المستقبلات أو الأنظمة البيولوجية التي يستهدفها الدواء بالعربية",
                  "receptorsEn": "Biological receptors / target site in English",
                  "sideEffectsCommonAr": "الأعراض الجانبية الشائعة بالعربية",
                  "sideEffectsCommonEn": "Common side effects in English",
                  "sideEffectsSeriousAr": "الأعراض الجانبية الخطيرة بالعربية",
                  "sideEffectsSeriousEn": "Serious side effects in English",
                  "specialWarningsAr": "تحذيرات هامة وتعديلات كبار السن أومرضى الفشل الكلوي والكبدي بالعربية",
                  "specialWarningsEn": "Special clinical warnings in English",
                  "allergyTestingAr": "تفاصيل اختبار الحساسية إذا كان مطلوباً، أو اكتب 'غير مطلوب' إذا لم يكن الدواء يتطلبه بالعربية",
                  "allergyTestingEn": "Allergy testing protocol if required, otherwise 'Not required'",
                  "sourcesUsedEn": "قائمة بالمواقع المستخدمة للفحص مثل (https://dailymed.nlm.nih.gov)"
                }
                
                هام جداً جداً جداً:
                - يجب كتابة المصطلحات الطبية كلها في النصوص كالتالي: (المصطلح بالانجليزي) المصطلح بالعربي مع الحركات والضبط والتشكيل.
                - يجب كتابة وتنسيق الإجابة الطبية بمهنية وموضوعية مطلقة وخلوها تماماً من أي تخمين أو افتراض خيالي.
                - لا ترجع أي نصوص أخرى خارج الـ JSON. ابدأ الاستجابة بـ { وانتهِ بـ }.
            """.trimIndent()

            val contentsArray = JSONArray()
            val textObj = JSONObject().apply { put("text", prompt) }
            val partsArray = JSONArray().apply { put(textObj) }
            val contentObj = JSONObject().apply { put("parts", partsArray) }
            contentsArray.put(contentObj)

            requestJson.put("contents", contentsArray)

            // We can specify JSON MIME type in generationConfig so Gemini returns clean JSON
            val responseFormatText = JSONObject().apply { put("mimeType", "application/json") }
            val responseFormat = JSONObject().apply { put("text", responseFormatText) }
            val generationConfig = JSONObject().apply { put("responseFormat", responseFormat) }
            requestJson.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            var rawText = parts?.optJSONObject(0)?.optString("text")?.trim() ?: return@withContext null

            // Clean markdown blocks if Gemini added them despite the format
            if (rawText.startsWith("```json")) {
                rawText = rawText.substringAfter("```json").substringBeforeLast("```").trim()
            } else if (rawText.startsWith("```")) {
                rawText = rawText.substringAfter("```").substringBeforeLast("```").trim()
            }

            val dataObj = JSONObject(rawText)
            return@withContext MedicationData(
                nameAr = dataObj.optString("nameAr"),
                nameEn = dataObj.optString("nameEn"),
                drugClassAr = dataObj.optString("drugClassAr"),
                drugClassEn = dataObj.optString("drugClassEn"),
                mechanismOfActionAr = dataObj.optString("mechanismOfActionAr"),
                mechanismOfActionEn = dataObj.optString("mechanismOfActionEn"),
                indicationsAr = dataObj.optString("indicationsAr"),
                indicationsEn = dataObj.optString("indicationsEn"),
                dosageInfantsAr = dataObj.optString("dosageInfantsAr"),
                dosageInfantsEn = dataObj.optString("dosageInfantsEn"),
                dosageChildrenAr = dataObj.optString("dosageChildrenAr"),
                dosageChildrenEn = dataObj.optString("dosageChildrenEn"),
                dosageAdultsAr = dataObj.optString("dosageAdultsAr"),
                dosageAdultsEn = dataObj.optString("dosageAdultsEn"),
                dosageWarningAr = "الجرعة تعتمد على الحالة الطبية ووزن المريض.",
                dosageWarningEn = "Dosage depends on clinical condition and body weight.",
                maxDoseToxicityAr = dataObj.optString("maxDoseToxicityAr"),
                maxDoseToxicityEn = dataObj.optString("maxDoseToxicityEn"),
                contraindicationsAr = dataObj.optString("contraindicationsAr"),
                contraindicationsEn = dataObj.optString("contraindicationsEn"),
                antidoteAr = dataObj.optString("antidoteAr"),
                antidoteEn = dataObj.optString("antidoteEn"),
                interactionsAr = dataObj.optString("interactionsAr"),
                interactionsEn = dataObj.optString("interactionsEn"),
                receptorsAr = dataObj.optString("receptorsAr"),
                receptorsEn = dataObj.optString("receptorsEn"),
                sideEffectsCommonAr = dataObj.optString("sideEffectsCommonAr"),
                sideEffectsCommonEn = dataObj.optString("sideEffectsCommonEn"),
                sideEffectsSeriousAr = dataObj.optString("sideEffectsSeriousAr"),
                sideEffectsSeriousEn = dataObj.optString("sideEffectsSeriousEn"),
                specialWarningsAr = dataObj.optString("specialWarningsAr"),
                specialWarningsEn = dataObj.optString("specialWarningsEn"),
                allergyTestingAr = dataObj.optString("allergyTestingAr"),
                allergyTestingEn = dataObj.optString("allergyTestingEn"),
                sourcesUsedEn = dataObj.optString("sourcesUsedEn")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error searching medication through Gemini", e)
            return@withContext null
        }
    }

    /**
     * Let Gemini evaluate user's and friend's answers for a pharmacy question.
     */
    suspend fun evaluateAnswers(
        questionText: String,
        correctAnswer: String,
        userAnswer: String,
        friendAnswer: String,
        userName: String,
        friendName: String
    ): EvaluationResult = withContext(Dispatchers.IO) {
        if (!isApiKeyAvailable()) {
            // Local simple check if API is not configured
            val userCorrect = userAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true) || 
                              correctAnswer.trim().contains(userAnswer.trim(), ignoreCase = true)
            val friendCorrect = friendAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true) ||
                                correctAnswer.trim().contains(friendAnswer.trim(), ignoreCase = true)

            return@withContext EvaluationResult(
                userIsCorrect = userCorrect,
                friendIsCorrect = friendCorrect,
                correctAnswerDetailed = "الإجابة الصحيحة المسجلة في بنك الأسئلة هي: $correctAnswer",
                explanation = "تنبيه: تعمل آلية التحقق محلياً نظراً لعدم توفر مفتاح Gemini API. لم يقم الذكاء الاصطناعي بتحليل الإجابات النصية العميقة.",
                encouragementMessage = "العلم يحتاج صبر واجتهاد! استمرا في التنافس والتعلم الدائم."
            )
        }

        try {
            val endpoint = "$BASE_URL/v1beta/models/$MODEL_NAME:generateContent?key=${getApiKey()}"
            val requestJson = JSONObject()

            val prompt = """
                أنت مصحح مسابقات صيدلانية دقيق في تطبيق "عون فرما".
                السؤال المطروح كان: "$questionText"
                الإجابة الصحيحة والنموذجية المخزنة لدينا هي: "$correctAnswer"
                
                إجابة المستخدم ($userName) هي: "$userAnswer"
                إجابة صديقه ($friendName) هي: "$friendAnswer"
                
                مهمتك هي مقارنة إجابة كل منهما بالإجابة الصحيحة بشكل منطقي ومرن (إذا كانت تعبر عن نفس المعنى العلمي والمادة الفعالة والعائلة الدوائية اعتبرها صحيحة).
                يجب أن ترجع النتيجة كـ JSON كائن واحد متوافق حرفياً تماماً مع هذا الهيكل:
                {
                  "userIsCorrect": true/false,
                  "friendIsCorrect": true/false,
                  "correctAnswerDetailedAr": "تفصيل الإجابة الصحيحة باللغة العربية مع إقران المصطلحات بالإنجليزية بين قوسين مع التشكيل والحركات، مثلا باراسيتامول (Paracetamol)",
                  "explanationAr": "شرح علمي مبسط ومختصر جداً لسبب صواب أو خطأ الإجابات وتوضيح الفكرة الطبية",
                  "encouragementAr": "عبارة تشجيعية طبية تحفيزية للشخص المخطئ، ويجب حتماً أن تتضمن جملة: 'العلم يحتاج صبر واجتهاد' إذا كان أحدهما أو كلاهما مخطئاً"
                }
                لا تكتب أية نصوص أخرى خارج الـ JSON.
            """.trimIndent()

            val contentsArray = JSONArray()
            val textObj = JSONObject().apply { put("text", prompt) }
            val partsArray = JSONArray().apply { put(textObj) }
            val contentObj = JSONObject().apply { put("parts", partsArray) }
            contentsArray.put(contentObj)

            requestJson.put("contents", contentsArray)

            val responseFormatText = JSONObject().apply { put("mimeType", "application/json") }
            val responseFormat = JSONObject().apply { put("text", responseFormatText) }
            val generationConfig = JSONObject().apply { put("responseFormat", responseFormat) }
            requestJson.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext EvaluationResult(
                    userIsCorrect = false,
                    friendIsCorrect = false,
                    correctAnswerDetailed = correctAnswer,
                    explanation = "فشل التحقق عبر الذكاء الاصطناعي بسبب خطأ في الاستجابة.",
                    encouragementMessage = "العلم يحتاج صبر واجتهاد! حاولوا مرة أخرى."
                )
            }

            val responseBody = response.body?.string() ?: ""
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            var rawText = parts?.optJSONObject(0)?.optString("text")?.trim() ?: ""

            if (rawText.startsWith("```json")) {
                rawText = rawText.substringAfter("```json").substringBeforeLast("```").trim()
            } else if (rawText.startsWith("```")) {
                rawText = rawText.substringAfter("```").substringBeforeLast("```").trim()
            }

            val dataObj = JSONObject(rawText)
            return@withContext EvaluationResult(
                userIsCorrect = dataObj.optBoolean("userIsCorrect"),
                friendIsCorrect = dataObj.optBoolean("friendIsCorrect"),
                correctAnswerDetailed = dataObj.optString("correctAnswerDetailedAr"),
                explanation = dataObj.optString("explanationAr"),
                encouragementMessage = dataObj.optString("encouragementAr")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error evaluating answers via Gemini", e)
            val userCorrect = userAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true) || 
                              correctAnswer.trim().contains(userAnswer.trim(), ignoreCase = true)
            val friendCorrect = friendAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true) ||
                                correctAnswer.trim().contains(friendAnswer.trim(), ignoreCase = true)

            return@withContext EvaluationResult(
                userIsCorrect = userCorrect,
                friendIsCorrect = friendCorrect,
                correctAnswerDetailed = correctAnswer,
                explanation = "حدث خطأ أثناء الاتصال بالذكاء الاصطناعي للتحقق: ${e.localizedMessage}",
                encouragementMessage = "العلم يحتاج صبر واجتهاد! استمرا في التنافس والتعلم."
            )
        }
    }
}

data class MedicationData(
    val nameAr: String,
    val nameEn: String,
    val drugClassAr: String,
    val drugClassEn: String,
    val mechanismOfActionAr: String,
    val mechanismOfActionEn: String,
    val indicationsAr: String,
    val indicationsEn: String,
    val dosageInfantsAr: String,
    val dosageInfantsEn: String,
    val dosageChildrenAr: String,
    val dosageChildrenEn: String,
    val dosageAdultsAr: String,
    val dosageAdultsEn: String,
    val dosageWarningAr: String,
    val dosageWarningEn: String,
    val maxDoseToxicityAr: String,
    val maxDoseToxicityEn: String,
    val contraindicationsAr: String,
    val contraindicationsEn: String,
    val antidoteAr: String,
    val antidoteEn: String,
    val interactionsAr: String,
    val interactionsEn: String,
    val receptorsAr: String,
    val receptorsEn: String,
    val sideEffectsCommonAr: String,
    val sideEffectsCommonEn: String,
    val sideEffectsSeriousAr: String,
    val sideEffectsSeriousEn: String,
    val specialWarningsAr: String,
    val specialWarningsEn: String,
    val allergyTestingAr: String,
    val allergyTestingEn: String,
    val sourcesUsedEn: String
)

data class EvaluationResult(
    val userIsCorrect: Boolean,
    val friendIsCorrect: Boolean,
    val correctAnswerDetailed: String,
    val explanation: String,
    val encouragementMessage: String
)
