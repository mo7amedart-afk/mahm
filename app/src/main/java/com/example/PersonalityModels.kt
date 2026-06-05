package com.example

data class TraitReport(
    val arabic_name: String,
    val score: Int,
    val level: String, // منخفض، متوسط، مرتفع
    val interpretation: String,
    val evidence: String,
    val recommendation: String
) {
    fun toJsonString(): String {
        return """{
      "arabic_name": "${escapeJson(arabic_name)}",
      "score": $score,
      "level": "${escapeJson(level)}",
      "interpretation": "${escapeJson(interpretation)}",
      "evidence": "${escapeJson(evidence)}",
      "recommendation": "${escapeJson(recommendation)}"
    }"""
    }
}

data class BigFiveReport(
    val report_title: String,
    val summary: String,
    val confidence_level: String, // جيدة، متوسطة، منخفضة
    val confidence_reason: String,
    val traits: Map<String, TraitReport>, // openness, conscientiousness, extraversion, agreeableness, neuroticism
    val strengths: List<String>,
    val growth_areas: List<String>,
    val practical_recommendations: List<String>,
    val scientific_sources: List<String>,
    val disclaimer: String
) {
    fun generateSpeechNarrative(): String {
        val builder = java.lang.StringBuilder()
        builder.append("هذا تحليل تعليمي مبسط لسماتك الشخصية بناءً على إجاباتك الحالية. ")
        builder.append("عنوان تقريرك الطبيعي المعتمد هو: ${report_title}. ")
        builder.append("${summary} ")
        builder.append("درجة موثوقية التحليل الحالية تعتبر ${confidence_level}. ")
        
        builder.append("تفاصيل أبعادك الخمسة الكبرى هي كما يلي: ")
        traits.values.forEach { trait ->
            builder.append("بالنسبة لـ ${trait.arabic_name}، درجتك المئوية هي ${trait.score} بالمئة، وهي تعتبر في مستوى ${trait.level}. ${trait.interpretation} ")
        }
        
        if (strengths.isNotEmpty()) {
            builder.append("أبرز نقاط قوتك وتميزك تشمل: ")
            strengths.take(3).forEach { strength ->
                builder.append("${strength} ")
            }
        }
        
        if (growth_areas.isNotEmpty()) {
            builder.append("ومن الجوانب القابلة للتطوير الشخصي في حياتك: ")
            growth_areas.take(3).forEach { area ->
                builder.append("${area} ")
            }
        }
        
        builder.append("تذكير مهم: هذا التحليل ليس تشخيصًا نفسيًا أو طبيًا، بل قراءة احتمالية تساعدك على فهم نفسك بشكل أفضل.")
        return builder.toString()
    }

    fun toJsonString(): String {
        val traitsJson = traits.entries.joinToString(",\n    ") { (key, trait) ->
            "\"$key\": ${trait.toJsonString()}"
        }
        val strengthsJson = strengths.joinToString(", ") { "\"${escapeJson(it)}\"" }
        val growthAreasJson = growth_areas.joinToString(", ") { "\"${escapeJson(it)}\"" }
        val recsJson = practical_recommendations.joinToString(", ") { "\"${escapeJson(it)}\"" }
        val sourcesJson = scientific_sources.joinToString(", ") { "\"${escapeJson(it)}\"" }

        return """{
  "report_title": "${escapeJson(report_title)}",
  "summary": "${escapeJson(summary)}",
  "confidence_level": "${escapeJson(confidence_level)}",
  "confidence_reason": "${escapeJson(confidence_reason)}",
  "traits": {
    $traitsJson
  },
  "strengths": [$strengthsJson],
  "growth_areas": [$growthAreasJson],
  "practical_recommendations": [$recsJson],
  "scientific_sources": [$sourcesJson],
  "disclaimer": "${escapeJson(disclaimer)}"
}"""
    }
}

fun escapeJson(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}

data class PersonalityQuestion(
    val id: Int,
    val text: String,
    val category: String // openness, conscientiousness, extraversion, agreeableness, neuroticism
)

object PersonalityData {
    val questions = listOf(
        PersonalityQuestion(1, "أحب تجربة أشياء جديدة حتى لو لم أكن متأكدًا من النتيجة", "openness"),
        PersonalityQuestion(2, "أحب ترتيب أعمالي قبل البدء", "conscientiousness"),
        PersonalityQuestion(3, "أشعر بالنشاط عند مقابلة أشخاص جدد", "extraversion"),
        PersonalityQuestion(4, "أحاول تفهم وجهات نظر الآخرين", "agreeableness"),
        PersonalityQuestion(5, "أتأثر بسرعة عند حدوث ضغط أو مشكلة", "neuroticism"),
        PersonalityQuestion(6, "أحب التعلم واكتشاف أفكار جديدة", "openness"),
        PersonalityQuestion(7, "ألتزم غالبًا بإنهاء ما أبدأه", "conscientiousness"),
        PersonalityQuestion(8, "أفضل العمل مع الآخرين بدلًا من العمل وحدي دائمًا", "extraversion"),
        PersonalityQuestion(9, "أحرص على تجنب إيذاء مشاعر الآخرين", "agreeableness"),
        PersonalityQuestion(10, "أقلق كثيرًا عندما لا تسير الأمور كما توقعت", "neuroticism"),
        PersonalityQuestion(11, "أحب الخيال والإبداع", "openness"),
        PersonalityQuestion(12, "أهتم بالتفاصيل الصغيرة", "conscientiousness"),
        PersonalityQuestion(13, "أجد سهولة في بدء الحديث مع أشخاص جدد", "extraversion"),
        PersonalityQuestion(14, "أسامح الآخرين غالبًا بعد الخلاف", "agreeableness"),
        PersonalityQuestion(15, "أتوتر عند مواجهة مواقف غير واضحة", "neuroticism"),
        PersonalityQuestion(16, "أحب الأفكار غير التقليدية", "openness"),
        PersonalityQuestion(17, "أضع خططًا واضحة قبل تنفيذ المهام", "conscientiousness"),
        PersonalityQuestion(18, "أحب المشاركة في النقاشات الجماعية", "extraversion"),
        PersonalityQuestion(19, "أتعامل بلطف حتى مع من يختلف معي", "agreeableness"),
        PersonalityQuestion(20, "أحتاج وقتًا طويلًا حتى أهدأ بعد موقف مزعج", "neuroticism")
    )

    fun getOptionText(score: Int): String {
        return when (score) {
            1 -> "لا أوافق أبدًا"
            2 -> "لا أوافق"
            3 -> "محايد"
            4 -> "أوافق"
            5 -> "أوافق جدًا"
            else -> "غير معروف"
        }
    }
}
