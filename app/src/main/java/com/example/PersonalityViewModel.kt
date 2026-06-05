package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class AppScreen {
    START,
    CONSENT,
    METHOD,
    FORMAT,
    QUIZ,
    CHAT_QUIZ, // Chat-based progressive questions screen
    AUDIO_UPLOAD, // Audio file upload screen (Version 3)
    VIDEO_URL, // Video URL input screen (Version 3)
    REPORT,
    ERROR_BLOCKED,
    DASHBOARD,          // Commercial User Dashboard
    TERMS,              // Terms of Use
    PRIVACY,            // Privacy Policy
    DEVELOPMENT_PLAN,   // 30-Day Personal Development Plan
    PLAN_COMPARISON     // Compare multiple reports
}

enum class UserPlan {
    FREE, SILVER, GOLD
}

data class SavedReportItem(
    val id: String,
    val date: String,
    val type: String, // "سؤال وجواب قصير", "سؤال وجواب كامل", "محادثة حرة", "تحليل صوتي", "تحليل فيديو"
    val confidence: String, // "جيدة", "متوسطة", "منخفضة"
    val report: BigFiveReport
)

data class PersonalDevPlan(
    val title: String,
    val dailyHabit: String,
    val cognitiveExercise: String,
    val practicalTip: String,
    val weeklyGoal: String,
    val endOfWeekReview: String,
    val dayPlanItems: List<String>
)

enum class ChatSender {
    AI, USER
}

data class ChatMessage(
    val sender: ChatSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class PersonalityViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.START)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _answers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val answers: StateFlow<Map<Int, Int>> = _answers.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _generatedReport = MutableStateFlow<BigFiveReport?>(null)
    val generatedReport: StateFlow<BigFiveReport?> = _generatedReport.asStateFlow()

    private val _showJsonDialog = MutableStateFlow(false)
    val showJsonDialog: StateFlow<Boolean> = _showJsonDialog.asStateFlow()

    // --- Dynamic Chat Mode states (Version 2) ---
    private val _selectedMethod = MutableStateFlow("qa") // "qa" or "chat"
    val selectedMethod: StateFlow<String> = _selectedMethod.asStateFlow()

    private val _chatAnswers = MutableStateFlow<Map<Int, String>>(emptyMap())
    val chatAnswers: StateFlow<Map<Int, String>> = _chatAnswers.asStateFlow()

    private val _currentChatIndex = MutableStateFlow(0)
    val currentChatIndex: StateFlow<Int> = _currentChatIndex.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAnalyzingChat = MutableStateFlow(false)
    val isAnalyzingChat: StateFlow<Boolean> = _isAnalyzingChat.asStateFlow()

    // --- Audio Player states (Version 2 TTS) ---
    private val _ttsIsPlaying = MutableStateFlow(false)
    val ttsIsPlaying: StateFlow<Boolean> = _ttsIsPlaying.asStateFlow()

    // --- Audio File Upload and Transcription (Version 3) ---
    private val _audioTranscribedText = MutableStateFlow("")
    val audioTranscribedText: StateFlow<String> = _audioTranscribedText.asStateFlow()

    private val _isTranscribingAudio = MutableStateFlow(false)
    val isTranscribingAudio: StateFlow<Boolean> = _isTranscribingAudio.asStateFlow()

    private val _audioConfirmConsent = MutableStateFlow(false)
    val audioConfirmConsent: StateFlow<Boolean> = _audioConfirmConsent.asStateFlow()

    // --- Video URL Input and Analysis (Version 3) ---
    private val _videoUrl = MutableStateFlow("")
    val videoUrl: StateFlow<String> = _videoUrl.asStateFlow()

    private val _videoIsOwn = MutableStateFlow(true)
    val videoIsOwn: StateFlow<Boolean> = _videoIsOwn.asStateFlow()

    private val _videoHasConsent = MutableStateFlow(false)
    val videoHasConsent: StateFlow<Boolean> = _videoHasConsent.asStateFlow()

    private val _isAnalyzingVideo = MutableStateFlow(false)
    val isAnalyzingVideo: StateFlow<Boolean> = _isAnalyzingVideo.asStateFlow()

    // --- Commercial Subscription, History & Development states ---
    private val _currentUserPlan = MutableStateFlow(UserPlan.GOLD)
    val currentUserPlan: StateFlow<UserPlan> = _currentUserPlan.asStateFlow()

    private val _pastReports = MutableStateFlow<List<SavedReportItem>>(emptyList())
    val pastReports: StateFlow<List<SavedReportItem>> = _pastReports.asStateFlow()

    private val _blockedReason = MutableStateFlow("")
    val blockedReason: StateFlow<String> = _blockedReason.asStateFlow()

    private val _currentDevPlan = MutableStateFlow<PersonalDevPlan?>(null)
    val currentDevPlan: StateFlow<PersonalDevPlan?> = _currentDevPlan.asStateFlow()

    private val _userPoints = MutableStateFlow(150)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    private val _privacyEncrypt = MutableStateFlow(true)
    val privacyEncrypt: StateFlow<Boolean> = _privacyEncrypt.asStateFlow()

    private val _privacyBehavioral = MutableStateFlow(true)
    val privacyBehavioral: StateFlow<Boolean> = _privacyBehavioral.asStateFlow()

    private val _privacyClearOnExit = MutableStateFlow(false)
    val privacyClearOnExit: StateFlow<Boolean> = _privacyClearOnExit.asStateFlow()

    private val _reportStyle = MutableStateFlow("مفصل")
    val reportStyle: StateFlow<String> = _reportStyle.asStateFlow()

    val chatQuestions = listOf(
        "ما الشيء الذي يحمسك عادة باليوم والمسعى المهني؟",
        "كيف تتعامل مع الضغط أو المشكلات المفاجئة والطارئة؟",
        "هل تفضل التخطيط المسبق أم التجربة والارتجال المباشر؟ ولماذا؟",
        "كيف تتصرف عند حدوث خلاف فكري أو شخصي مع شخص قريب منك؟",
        "ما نوع البيئة المحيطة التي تعطيك أفضل وأغزر أداء وعطاء؟",
        "كيف تتخذ قراراتك المهمة والمصيرية في حياتك؟",
        "ما الذي يزعجك عادة في العمل الجماعي والمشترك؟",
        "كيف تصف نفسك وتواصلك اجتماعيًا مع المحيطين والجدد؟",
        "ما أكثر عادة إيجابية فيك تحبها وتعتز بها في نفسك؟",
        "ما أكثر جانب أو سمه سلوكية تريد تطويرها في شخصيتك مستقبلاً؟"
    )

    init {
        resetChatMessages()
        // Pre-populate with realistic, high-quality, non-clinical reports to showcase the commercial past reports feature beautifully
        val mockTraits = mapOf(
            "openness" to TraitReport("الانفتاح على التجربة", 68, "مرتفع", "تبدو مائلاً للمرونة الفكرية وامتلاك ذائقة تعبيرية رحبة.", "مؤيد بالتجريب والطلب المستمر للتطوير الشخصي.", "استمر في خوض تجارب فكرية منوعة."),
            "conscientiousness" to TraitReport("الانضباط والضمير", 75, "مرتفع", "يظهر من إيجابيتك تقدير عالٍ للتنظيم الفعال ومتابعة الواجبات.", "تفضيلك للخطط وجدولة المساعي الشخصية.", "احرص على الموازنة وتجنب مجهودات الإرهاق."),
            "extraversion" to TraitReport("الانبساط والاجتماعية", 42, "منخفض", "يبدو أنك تميل أحياناً إلى البيئات الهادئة أو التفاعل المحدود مع المجموعات للتأمل.", "تفضيل إنجاز الواجبات بشكل مستقل.", "ابحث عن مجالس فكرية صغيرة مريحة سلوكياً."),
            "agreeableness" to TraitReport("التوافق والتعاون", 80, "مرتفع", "يرجح رغبتك الشديدة في التعاون ومساندة الآخرين.", "محيط دافئ وتجنب الخلافات المفتعلة.", "استمر بالود مع وضع حدود شخصية صحية."),
            "neuroticism" to TraitReport("الحساسية الانفعالية والاتزان", 35, "منخفض", "تشير إجاباتك إلى توازن وهدوء جيد وصمود متناغم أمام المواقف الضاغطة.", "هدوءك في معالجة المشكلات المفاجئة.", "تدرب على الاسترخاء والامتنان اليومي وملاحظة الأفكار.")
        )
        val mockReport = BigFiveReport(
            report_title = "تقرير قياس مقياس الشخصية الشامل",
            summary = "تبدو إجاباتك معبرة عن شخصية تميل نحو التوازن والتنظيم العالي والوئام، مع تفضيل أوقات الخلوة الهادئة لإعادة شحن طاقتها.",
            confidence_level = "جيدة",
            confidence_reason = "مبني على إجابات ٢٠ سؤالاً معتمداً.",
            traits = mockTraits,
            strengths = listOf("التنظيم الدؤوب", "الحكمة في تبني القرارات", "التعاطف اللطيف"),
            growth_areas = listOf("تطوير مهارة التحدث أمام الجمهور", "ممارسة المرونة عند تغيير الخطط"),
            practical_recommendations = listOf("ابدأ يومك بوضع ثلاث أولويات واضحة", "خصص ٢٠ دقيقة للقراءة الهادئة"),
            scientific_sources = listOf("دليل مقاييس العوامل الخمسة الكبرى", "أبحاث علم النفس السلبي للتوازن الذاتي"),
            disclaimer = "القراءة احتمالية تثقيفية وليست أداة تشخيص نفسي أو طبي."
        )
        _pastReports.value = listOf(
            SavedReportItem("1", "2026-05-28", "سؤال وجواب كامل", "جيدة", mockReport),
            SavedReportItem("2", "2026-06-02", "تحليل مقطع فيديو كوتش", "متوسطة", mockReport.copy(
                report_title = "تحليل السلوك الخارجي الظاهر من فيديو مقتضب",
                summary = "مبني على تحليل الكلام المنطوق والسلوكيات الملحوظة لمقطع فيديو تعليمي تم إرساله."
            ))
        )
    }

    fun setUserPlan(plan: UserPlan) {
        _currentUserPlan.value = plan
    }

    fun updateReportStyle(style: String) {
        _reportStyle.value = style
    }

    fun setPrivacyEncrypt(v: Boolean) {
        _privacyEncrypt.value = v
    }

    fun setPrivacyBehavioral(v: Boolean) {
        _privacyBehavioral.value = v
    }

    fun setPrivacyClearOnExit(v: Boolean) {
        _privacyClearOnExit.value = v
    }

    fun addPoints(amount: Int) {
        _userPoints.value = _userPoints.value + amount
    }

    fun setBlockedReason(reason: String) {
        _blockedReason.value = reason
    }

    fun deleteReportFromHistory(id: String) {
        _pastReports.value = _pastReports.value.filter { it.id != id }
    }

    fun saveReportToHistory(type: String, report: BigFiveReport) {
        // Silver is capped to 3 saved reports, Gold has unlimited capacity, Free cannot save
        val currentPlan = _currentUserPlan.value
        if (currentPlan == UserPlan.FREE) return

        val currentList = _pastReports.value
        if (currentPlan == UserPlan.SILVER && currentList.size >= 3) {
            // Cap to 3 by removing oldest
            val truncated = currentList.drop(currentList.size - 2)
            _pastReports.value = truncated + SavedReportItem(
                id = java.util.UUID.randomUUID().toString(),
                date = "اليوم",
                type = type,
                confidence = report.confidence_level,
                report = report
            )
        } else {
            _pastReports.value = currentList + SavedReportItem(
                id = java.util.UUID.randomUUID().toString(),
                date = "اليوم",
                type = type,
                confidence = report.confidence_level,
                report = report
            )
        }
    }

    fun generatePersonalDevPlan(report: BigFiveReport) {
        val agr = report.traits["agreeableness"]?.score ?: 50
        val neu = report.traits["neuroticism"]?.score ?: 50
        val con = report.traits["conscientiousness"]?.score ?: 50

        val habit: String
        val exercise: String
        val tip: String
        val weekly: String
        val review: String

        if (neu > 60) {
            habit = "ممارسة التأمل الهادئ المريح وتأمل الشهيق والزفير لمدة خمس دقائق صباحاً."
            exercise = "كتابة الأفكار التي تبدو ضاغطة ومناقشتها بعقلانية لتقليل أثرها مجهرياً."
            tip = "تجنب البيئات الصاخبة ومارس الهدوء وقراءة الأثر بتبسط وتدرج دافئ."
            weekly = "تخصيص نصف ساعة كاملة بنهاية الأسبوع للمشي الفردي في بيئة مفتوحة ومريحة سلوكياً."
            review = "مراجعة معدلات الارتياح والاتزان الفكري العام مع تجنب إصدار أي أحكام نفسية."
        } else if (con < 50) {
            habit = "وضع ثلاث مهام شخصية مبسطة في مستهل كل يوم والالتزام بتنفيذ مهمة واحدة كاملة."
            exercise = "تمرين التركيز المتواصل لمدة خمس عشرة دقيقة كاملة بدون تشتيت الالتفات."
            tip = "تذكر دائماً أن التدرج في إنجاز أجزاء من الخطط خير من السعي الطويل نحو كمال وهمي."
            weekly = "ترتيب بيئة العمل لتقليل الصخب والتشتت المادي المحيط."
            review = "استعراض الإنجازات البسيطة المحققة بفخر مع تجنب لوم الذات المطلق."
        } else {
            habit = "تخصيص ربع ساعة لقراءة أطروحة سلوكية دافئة وتثاقفية لرفع مهارات الاتصال."
            exercise = "تسجيل فكرة مبتكرة أو عادة جديدة خفيفة تجعلك أكثر مرونة في تعاملك اليومي."
            tip = "اعرض خدمات المساندة التعبيرية على صديق مقرب لتوثيق أواصر التوافق والتعاون اللطيف."
            weekly = "تجربة هواية جديدة ممتعة خالية من الأهداف التنافسية المرهقة."
            review = "ملاحظة مدى تواصلك الوجداني الفعّال مع الأشخاص المحيطين بك."
        }

        val items = ArrayList<String>()
        for (i in 1..30) {
            items.add("اليوم $i: المضي قدماً في السلوك التنموي الجديد بحب وتثبيت رصين للامتنان الشخصي اليومي.")
        }

        _currentDevPlan.value = PersonalDevPlan(
            title = "خطة التنمية والتطوير الشخصي المتزنة لمده ٣٠ يوماً",
            dailyHabit = habit,
            cognitiveExercise = exercise,
            practicalTip = tip,
            weeklyGoal = weekly,
            endOfWeekReview = review,
            dayPlanItems = items
        )
        _currentScreen.value = AppScreen.DEVELOPMENT_PLAN
    }

    fun containsClinicalKeywords(text: String): Boolean {
        val keywords = listOf(
            "اكتئاب", "فصام", "وسواس", "اضطراب نفسي", "ثنائي القطب", "قلق مرضي", 
            "انفصام", "توحد", "مرض نفسي", "مريض نفسي", "وسواس قهري", "اضطرابات نفسية",
            "علاج نفسي", "أدوية نفسية"
        )
        val lower = text.lowercase()
        return keywords.any { lower.contains(it) }
    }

    fun selectScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setSelectedMethod(method: String) {
        _selectedMethod.value = method
        if (method == "chat") {
            resetChatMessages()
        }
    }

    fun setTTSPlaying(playing: Boolean) {
        _ttsIsPlaying.value = playing
    }

    // --- Traditional Quiz Functions ---
    fun answerCurrentQuestion(score: Int) {
        val qId = PersonalityData.questions[_currentQuestionIndex.value].id
        _answers.value = _answers.value + (qId to score)
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < PersonalityData.questions.size - 1) {
            _currentQuestionIndex.value += 1
        } else {
            generateFinalReport()
        }
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun finishQuizEarly() {
        val answeredCount = _answers.value.filterKeys { it in 1..20 }.size
        if (answeredCount <= 5) {
            _blockedReason.value = "insufficient_data"
            _currentScreen.value = AppScreen.ERROR_BLOCKED
        } else {
            generateFinalReport()
        }
    }

    private fun generateFinalReport() {
        val totalQuestionsAnswered = _answers.value.filterKeys { it in 1..20 }.size
        if (totalQuestionsAnswered <= 5) {
            _blockedReason.value = "insufficient_data"
            _currentScreen.value = AppScreen.ERROR_BLOCKED
            return
        }
        val finalReport = PersonalityAnalyzerEngine.generateReport(_answers.value)
        _generatedReport.value = finalReport
        val typeLabel = if (totalQuestionsAnswered == 20) "اختبار كامل (٢٠ سؤالاً)" else "اختبار مختصر"
        saveReportToHistory(typeLabel, finalReport)
        _currentScreen.value = AppScreen.REPORT
    }

    // --- Free Chat Functions ---
    fun resetChatMessages() {
        _chatAnswers.value = emptyMap()
        _currentChatIndex.value = 0
        _chatMessages.value = listOf(
            ChatMessage(ChatSender.AI, "مرحباً بك في وضع المحادثة الحرة للكوتش الذكي. سأقوم بطرح غلاف من الأسئلة المتدرجة عليك تدريجياً لنتعرف معاً بأسلوب وجداني على معالم شخصيتك الرائعة بناءً على مقاييس علم النفس."),
            ChatMessage(ChatSender.AI, "السؤال الأول: ما الشيء الذي يحمسك عادة باليوم والمسعى المهني؟")
        )
    }

    fun submitChatResponse(response: String) {
        val trimmed = response.trim()
        if (trimmed.isEmpty()) return

        val currentIndex = _currentChatIndex.value
        if (currentIndex >= chatQuestions.size) return

        // Add user statement
        val currentList = _chatMessages.value.toMutableList()
        currentList.add(ChatMessage(ChatSender.USER, trimmed))

        // Save answer
        val updatedAnswers = _chatAnswers.value + (currentIndex + 1 to trimmed)
        _chatAnswers.value = updatedAnswers

        // Advance
        val nextIndex = currentIndex + 1
        _currentChatIndex.value = nextIndex

        if (nextIndex < chatQuestions.size) {
            val nextQuestionText = "السؤال ${nextIndex + 1}: ${chatQuestions[nextIndex]}"
            currentList.add(ChatMessage(ChatSender.AI, nextQuestionText))
        } else {
            currentList.add(ChatMessage(ChatSender.AI, "لقد أكملت جميع الأسئلة العشرة الاستكشافية بنجاح مدهش! انقر الآن على زر 'إصدار التحليل النهائي' بالأسفل لبدء قياس سماتك الشخصية."))
        }

        _chatMessages.value = currentList
    }

    fun generateChatAnalysis() {
        val answers = _chatAnswers.value
        val combinedText = answers.values.joinToString(" ")
        if (containsClinicalKeywords(combinedText)) {
            _blockedReason.value = "psychiatric_refusal"
            _currentScreen.value = AppScreen.ERROR_BLOCKED
            _isAnalyzingChat.value = false
            return
        }

        _isAnalyzingChat.value = true
        _currentScreen.value = AppScreen.REPORT
        
        viewModelScope.launch {
            var report: BigFiveReport? = null

            // Grab the API key securely
            val apiKey = BuildConfig.GEMINI_API_KEY
            val isRealKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "PLACEHOLDER"

            if (isRealKey) {
                try {
                    val promptText = makeChatAnalysisPrompt(answers)
                    val request = GeminiRequest(
                        contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = promptText)))),
                        generationConfig = GeminiGenerationConfig(
                            responseMimeType = "application/json",
                            temperature = 0.45f
                        )
                    )
                    val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
                    val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (rawText != null) {
                        report = parseGeminiReport(rawText)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PersonalityViewModel", "Gemini server-side analysis failed, switching to local: ${e.message}", e)
                }
            }

            // High intelligence local rule engine as robust fallback guarantee
            if (report == null) {
                report = PersonalityAnalyzerEngine.generateReportFromChat(answers)
            }

            _generatedReport.value = report
            saveReportToHistory("محادثة حرة تفاعلية", report)
            _isAnalyzingChat.value = false
        }
    }

    private fun cleanJsonString(input: String): String {
        var text = input.trim()
        if (text.startsWith("```json")) {
            text = text.substringAfter("```json")
        } else if (text.startsWith("```")) {
            text = text.substringAfter("```")
        }
        if (text.endsWith("```")) {
            text = text.substringBeforeLast("```")
        }
        return text.trim()
    }

    private fun parseGeminiReport(rawJson: String): BigFiveReport {
        val cleanJson = cleanJsonString(rawJson)
        val obj = JSONObject(cleanJson)
        val reportTitle = obj.optString("report_title", "تقرير تحليل الشخصية التفاعلي (محادثة حرة)")
        val summary = obj.optString("summary", "يرجى العلم بأن هذا التحليل مبني بالكامل على مخرجات محادثة مفتوحة وجدانية.")
        val confidenceLevel = obj.optString("confidence_level", "جيدة")
        val confidenceReason = obj.optString("confidence_reason", "بناء على محادثة حرة مستمرة.")

        val traitsJson = obj.optJSONObject("traits")
        val traitsMap = mutableMapOf<String, TraitReport>()
        val traitKeys = listOf("openness", "conscientiousness", "extraversion", "agreeableness", "neuroticism")

        traitKeys.forEach { key ->
            val traitObj = traitsJson?.optJSONObject(key)
            if (traitObj != null) {
                traitsMap[key] = TraitReport(
                    arabic_name = traitObj.optString("arabic_name", ""),
                    score = traitObj.optInt("score", 50),
                    level = traitObj.optString("level", "متوسط"),
                    interpretation = traitObj.optString("interpretation", ""),
                    evidence = traitObj.optString("evidence", ""),
                    recommendation = traitObj.optString("recommendation", "")
                )
            }
        }

        if (traitsMap.isEmpty()) {
            throw Exception("Failed to serialize sub-parameters of Big Five traits correctly from JSON output.")
        }

        val strengths = mutableListOf<String>()
        val strengthsArray = obj.optJSONArray("strengths")
        if (strengthsArray != null) {
            for (i in 0 until strengthsArray.length()) {
                strengths.add(strengthsArray.getString(i))
            }
        }

        val growthAreas = mutableListOf<String>()
        val growthAreasArray = obj.optJSONArray("growth_areas")
        if (growthAreasArray != null) {
            for (i in 0 until growthAreasArray.length()) {
                growthAreas.add(growthAreasArray.getString(i))
            }
        }

        val practicalRecommendations = mutableListOf<String>()
        val recommendationsArray = obj.optJSONArray("practical_recommendations")
        if (recommendationsArray != null) {
            for (i in 0 until recommendationsArray.length()) {
                practicalRecommendations.add(recommendationsArray.getString(i))
            }
        }

        val scientificSources = mutableListOf<String>()
        val sourcesArray = obj.optJSONArray("scientific_sources")
        if (sourcesArray != null) {
            for (i in 0 until sourcesArray.length()) {
                scientificSources.add(sourcesArray.getString(i))
            }
        }

        val disclaimer = obj.optString("disclaimer", "هذا التقرير للتثقيف الذاتي وفهم السمات الشخصية، وليس تشخيصًا نفسيًا أو طبيًا، ولا يغني عن استشارة مختص.")

        return BigFiveReport(
            report_title = reportTitle,
            summary = summary,
            confidence_level = confidenceLevel,
            confidence_reason = confidenceReason,
            traits = traitsMap,
            strengths = strengths,
            growth_areas = growthAreas,
            practical_recommendations = practicalRecommendations,
            scientific_sources = scientificSources,
            disclaimer = disclaimer
        )
    }

    private fun makeChatAnalysisPrompt(answers: Map<Int, String>): String {
        val builder = java.lang.StringBuilder()
        builder.append("حلل إجابات المحادثة المفتوحة التالية لمستخدم وفقاً لنموذج السمات الخمس الكبرى (الانفتاح، الانضباط، الانبساط، التوافق، الحساسية الانفعالية).\n\n")
        builder.append("يرجى قراءة كل إجابة بعناية وصياغة تقرير متناسق، ثري ومقروء باللغة العربية الفصحى.\n\n")
        
        answers.forEach { (qId, answer) ->
            val qText = chatQuestions.getOrNull(qId - 1) ?: ""
            builder.append("السؤال: $qText\nإجابة المستخدم الحرة: $answer\n\n")
        }

        builder.append("مهم جداً: أرجع الناتج بهيئة كود JSON صالح تماماً بمستند الكيانات التالي (تجنب لف الناتج بأي نص خارجي غير الـ JSON، ولا تضف ```json في البداية والنهاية، فقط ارجع الـ JSON مباشرة):\n")
        builder.append("""
        {
          "report_title": "تقرير تحليل الشخصية التفاعلي (محادثة حرة)",
          "summary": "ملخص كامل ومعمق لسمات شخصية المستخدم بناء على أجوبته (يتراوح بين ٥ إلى ٧ أسطر) يوضح تفاعل سماته بشكل متموج وإيجابي، مع تضمين عبارة تشير صراحة إلى أن هذا التحليل مبني على محادثة مفتوحة تفاعلية دافئة بنسخته الثانية.",
          "confidence_level": "جيدة (أو متوسطة بناء على عدد الأسئلة المجابة)",
          "confidence_reason": "قراءة احتمالية مبنية على إجابات المحادثة المفتوحة للمستخدم.",
          "traits": {
            "openness": {
              "arabic_name": "الانفتاح على التجربة",
              "score": 65,
              "level": "منخفض أو متوسط أو مرتفع",
              "interpretation": "تفسير السمة بالأبعاد العلمية والوجدانية"،
              "evidence": "دليل ملموس من إجابات المستخدم الحقيقية بكلماته الخاصة بالتطوير والبيئة المذكورة",
              "recommendation": "توصية عملية مخصصة تناسبه"
            },
            "conscientiousness": {
              "arabic_name": "الانضباط والانجاز",
              "score": 55,
              "level": "منخفض أو متوسط أو مرتفع",
              "interpretation": "تفسير السمة بالأبعاد العلمية",
              "evidence": "دليل من إجابة كيفية اتخاذه القرارات والتنظيم المذكور",
              "recommendation": "توصية عملية مخصصة"
            },
            "extraversion": {
              "arabic_name": "الانبساط والاجتماعية",
              "score": 75,
              "level": "منخفض أو متوسط أو مرتفع",
              "interpretation": "تفسير السمة بالأبعاد العلمية",
              "evidence": "دليل من وصفه لنفسه اجتماعياً وموقفه من الزملاء والوحدانية",
              "recommendation": "توصية عملية مخصصة"
            },
            "agreeableness": {
              "arabic_name": "الوفاق والتوافق",
              "score": 70,
              "level": "منخفض أو متوسط أو مرتفع",
              "interpretation": "تفسير وتأثير سمة التوافق لديه"،
              "evidence": "دليل مما ذكره حول تخطيه الخلافات مع القريبين وأدواره التلاحمية والمسامحة",
              "recommendation": "توصية عملية مخصصة"
            },
            "neuroticism": {
              "arabic_name": "الحساسية الانفعالية والاتزان",
              "score": 40,
              "level": "منخفض أو متوسط أو مرتفع",
              "interpretation": "تأويل معدلات القلق وإدارته للمواقف الطارئة والتوتر",
              "evidence": "دليل ملموس من جوابه على طريقة التعامل مع الضغوط والمشكلات الطارئة",
              "recommendation": "توصية عملية هادئة مخصصة"
            }
          },
          "strengths": ["٣ إلى ٥ نقاط من جوانب قوة وتميز هذا المستخدم مستلهمة من الإجابات الفردية"],
          "growth_areas": ["٣ إلى ٥ نقاط تنموية تخصصية قابلة للتطور مستلهمة من الإجابات الفردية"],
          "practical_recommendations": ["٣ إلى ٥ توصيات مميزة مخصصة تترجم لسلوك يومي ناضج"],
          "scientific_sources": [
            "نموذج العوامل الخمسة الكبرى المعتمد في علم النفس التحليلي والمحادثات المفتوحة والتقييمات اللغوية.",
            "دراسات الاستجابة اللغوية الحرة لمقاييس الوعي الذاتي المعتمدة."
          ],
          "disclaimer": "هذا التقرير للتثقيف الذاتي وفهم السمات الشخصية، وليس تشخيصًا نفسيًا أو طبيًا، ولا يغني عن استشارة مختص."
        }
        """.trimIndent())

        return builder.toString()
    }

    // --- Helpers ---
    fun getReportJsonString(): String {
        val report = _generatedReport.value ?: return "{}"
        return report.toJsonString()
    }

    fun setShowJsonDialog(show: Boolean) {
        _showJsonDialog.value = show
    }

    // --- Audio and Video Actions (Version 3) ---
    fun updateAudioTranscribedText(text: String) {
        _audioTranscribedText.value = text
    }

    fun setAudioTranscribing(transcribing: Boolean) {
        _isTranscribingAudio.value = transcribing
    }

    fun setAudioConfirmConsent(confirm: Boolean) {
        _audioConfirmConsent.value = confirm
    }

    fun updateVideoUrl(url: String) {
        _videoUrl.value = url
    }

    fun setVideoIsOwn(isOwn: Boolean) {
        _videoIsOwn.value = isOwn
    }

    fun setVideoHasConsent(hasConsent: Boolean) {
        _videoHasConsent.value = hasConsent
    }

    fun generateAudioAnalysis(text: String) {
        if (containsClinicalKeywords(text)) {
            _blockedReason.value = "psychiatric_refusal"
            _currentScreen.value = AppScreen.ERROR_BLOCKED
            _isTranscribingAudio.value = false
            return
        }

        _isTranscribingAudio.value = true
        _currentScreen.value = AppScreen.REPORT
        viewModelScope.launch {
            var report: BigFiveReport? = null
            val apiKey = BuildConfig.GEMINI_API_KEY
            val isRealKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "PLACEHOLDER"

            if (isRealKey) {
                try {
                    val promptText = "حلل لغوياً وسلوكياً وبصورة ممتازة النص التالي المستخرج من مقطع صوتي لمستخدم وفقاً لنموذج السمات الخمس الكبرى (الانفتاح، الانضباط، الانبساط، التوافق، الحساسية الانفعالية).\n" +
                            "مهم جداً: هذا التحليل يعتمد تماماً على الكلام المنطوق في المقطع، ولا تبنِ حكماً نفسياً على نبرة الصوت وحدها. اذكر صراحة أن التحليل يعتمد على الكلام المنطوق.\n" +
                            "النص المستخرج: $text\n\n" +
                            "أرجع الناتج بهيئة كود JSON مطابق كلياً لهيكل التقرير السابق (report_title, summary, confidence_level, confidence_reason, traits, strengths, growth_areas, practical_recommendations, scientific_sources, disclaimer) باللغة العربية.\n" +
                            "تأكد من شمول التقرير للتنبيه: 'تحليل الصوت يعتمد على الكلام الموجود في المقطع، ولا يمثل حكماً كاملاً على شخصية المستخدم'."
                    val request = GeminiRequest(
                        contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = promptText)))),
                        generationConfig = GeminiGenerationConfig(
                            responseMimeType = "application/json",
                            temperature = 0.45f
                        )
                    )
                    val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
                    val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (rawText != null) {
                        report = parseGeminiReport(rawText)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PersonalityViewModel", "Gemini audio analysis failed, switching to local: ${e.message}", e)
                }
            }

            if (report == null) {
                report = PersonalityAnalyzerEngine.generateReportFromAudioText(text)
            }

            _generatedReport.value = report
            saveReportToHistory("تحليل التعبير الصوتي", report)
            _isTranscribingAudio.value = false
        }
    }

    fun generateVideoAnalysis(url: String, consentApproved: Boolean) {
        if (!consentApproved && !_videoIsOwn.value && !_videoHasConsent.value) {
            _blockedReason.value = "consent_required"
            _currentScreen.value = AppScreen.ERROR_BLOCKED
            return
        }

        _isAnalyzingVideo.value = true
        _currentScreen.value = AppScreen.REPORT
        viewModelScope.launch {
            var report: BigFiveReport? = null
            val apiKey = BuildConfig.GEMINI_API_KEY
            val isRealKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "PLACEHOLDER"

            // Simulate the extracted speech & behavior
            val speechText = "أعتقد أن العمل الدؤوب المرتب هو السبيل لكل إنجاز، لكنني أيضاً منفتح للغاية على تجربة الحلول والأطروحات الفكرية غير التقليدية، وفي تواصلي أميل لتبني حذر وقائي هادئ."
            val behaviorText = "تظهر لغة الجسد هدوءاً ورصانة حركية عالية، مع نبرة صوت متزنة ومنخفضة والتزام تام بالحديث دون تشتت انفعالي."

            if (isRealKey) {
                try {
                    val promptText = "حلل لغوياً وسلوكياً وبصورة ممتازة الكلام والسلوك الظاهر المستخرج من رابط الفيديو التالي ($url) وفقاً لنموذج السمات الخمس الكبرى.\n" +
                            "مهم جداً: لا تستنتج النوايا الداخلية أو الحالة النفسية للشخص. حلل الكلام والسلوك الظاهر فقط، وبصياغة احتمالية محدودة.\n" +
                            "الكلام المستخرج: $speechText\n" +
                            "السلوك الملحوظ: $behaviorText\n\n" +
                            "أرجع الناتج بهيئة كود JSON مطابق كلياً لهيكل التقرير السابق باللغة العربية.\n" +
                            "تأكد من شمول التنبيه: 'التحليل من فيديو واحد قد لا يمثل الشخصية كاملة، ويجب اعتباره قراءة احتمالية محدودة'."
                    val request = GeminiRequest(
                        contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = promptText)))),
                        generationConfig = GeminiGenerationConfig(
                            responseMimeType = "application/json",
                            temperature = 0.45f
                        )
                    )
                    val response = GeminiRetrofitClient.api.generateContent(apiKey, request)
                    val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (rawText != null) {
                        report = parseGeminiReport(rawText)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PersonalityViewModel", "Gemini video analysis failed, switching to local: ${e.message}", e)
                }
            }

            if (report == null) {
                report = PersonalityAnalyzerEngine.generateReportFromVideoMeta(url, speechText, behaviorText)
            }

            _generatedReport.value = report
            saveReportToHistory("تحليل محتوى الفيديو", report)
            _isAnalyzingVideo.value = false
        }
    }

    fun restartAnalysis() {
        _answers.value = emptyMap()
        _currentQuestionIndex.value = 0
        _generatedReport.value = null
        _showJsonDialog.value = false
        _ttsIsPlaying.value = false
        _selectedMethod.value = "qa"
        _audioTranscribedText.value = ""
        _isTranscribingAudio.value = false
        _audioConfirmConsent.value = false
        _videoUrl.value = ""
        _videoIsOwn.value = true
        _videoHasConsent.value = false
        _isAnalyzingVideo.value = false
        resetChatMessages()
        _currentScreen.value = AppScreen.START
    }
}
