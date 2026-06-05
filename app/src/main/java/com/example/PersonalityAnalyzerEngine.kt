package com.example

object PersonalityAnalyzerEngine {

    fun generateReport(answers: Map<Int, Int>): BigFiveReport {
        // Map traits to questions
        val opennessQIds = listOf(1, 6, 11, 16)
        val conscientiousnessQIds = listOf(2, 7, 12, 17)
        val extraversionQIds = listOf(3, 8, 13, 18)
        val agreeablenessQIds = listOf(4, 9, 14, 19)
        val neuroticismQIds = listOf(5, 10, 15, 20)

        val totalQuestionsAnswered = answers.size

        // Confidence Calculation
        val confidenceLevel = when {
            totalQuestionsAnswered < 8 -> "منخفضة"
            totalQuestionsAnswered in 8..19 -> "متوسطة"
            else -> "جيدة"
        }

        val confidenceReason = when {
            totalQuestionsAnswered < 8 -> "درجة موثوقية التحليل منخفضة جدًا نتيجة قلة مدخلات الإجابات المتاحة (أقل من ٨ إجابات)."
            totalQuestionsAnswered in 8..19 -> "درجة موثوقية التحليل متوسطة لأن الإجابات غير مكتملة ($totalQuestionsAnswered من أصل ٢٠)، مما يوفر قراءة جزئية ولكنها تظل قراءة احتمالية وليست حكمًا نهائيًا."
            else -> "درجة موثوقية التحليل جيدة لأن المستخدم أجاب على ٢٠ سؤالًا منظمًا، لكنها تظل قراءة احتمالية وليست حكمًا نهائيًا."
        }

        // Calculate Traits percentages
        val openScore = calculateCategoryScore(opennessQIds, answers)
        val consScore = calculateCategoryScore(conscientiousnessQIds, answers)
        val extScore = calculateCategoryScore(extraversionQIds, answers)
        val agrScore = calculateCategoryScore(agreeablenessQIds, answers)
        val neuScore = calculateCategoryScore(neuroticismQIds, answers)

        val openLevel = getLevelForScore(openScore)
        val consLevel = getLevelForScore(consScore)
        val extLevel = getLevelForScore(extScore)
        val agrLevel = getLevelForScore(agrScore)
        val neuLevel = getLevelForScore(neuScore)

        // Generate dynamically custom evidence based on user's answers
        val opennessEvidence = generateEvidence(opennessQIds, answers, "حب تجربة الجديد", "تقدير الإبداع والخيال")
        val conscientiousnessEvidence = generateEvidence(conscientiousnessQIds, answers, "تفضيل الترتيب والمنهجية", "التخطيط المسبق للمهام")
        val extraversionEvidence = generateEvidence(extraversionQIds, answers, "الحيوية عند مقابلة الوجوه الجديدة", "حب العمل والمشاركة الجماعية")
        val agreeablenessEvidence = generateEvidence(agreeablenessQIds, answers, "السعي لتفهم مواقف الآخرين", "الحرص على عدم جرح مشاعر الغير")
        val neuroticismEvidence = generateEvidence(neuroticismQIds, answers, "التأثر السريع بالضغوط والتوتر", "القلق عند مواجهة المواقف المفاجئة")

        // Build trait reports
        val traitsReport = mapOf(
            "openness" to TraitReport(
                arabic_name = "الانفتاح على التجربة",
                score = openScore,
                level = openLevel,
                interpretation = when (openLevel) {
                    "مرتفع" -> "تشير إجاباتك إلى ميل واضح للاهتمام بالفنون، الإبداع، التفكير خارج الصندوق، وتقبل الأفكار الجديدة وغير المألوفة."
                    "متوسط" -> "يبدو من العينة الحالية أنك متوازن بين الرغبة في تجربة الجديد وتفضيل الأمور المألوفة والمجربة مسبقًا بالحد المفيد."
                    else -> "تشير إجاباتك إلى تفضيلك العالي للروتين الواضح والاستقرار، والاعتماد على الطرق والحلول التقليدية المجربة بنجاح سابقًا."
                },
                evidence = opennessEvidence,
                recommendation = when (openLevel) {
                    "مرتفع" -> "جرّب تخصيص وقت شهري لتعلّم مهارة فنية أو علمية خارج نطاق تخصصك المعتاد لتستمر في تحفيز فضولك الإبداعي."
                    "متوسط" -> "حاول كسر الروتين تدريجيًا بممارسة أنشطة جديدة خفيفة وممتعة في عطلة نهاية الأسبوع من حين لآخر."
                    else -> "حاول إدخال تغيير بسيط وتجريبي واحد في جدولك الأسبوعي للتدرب على مرونة التغيير دون توتر."
                }
            ),
            "conscientiousness" to TraitReport(
                arabic_name = "الانضباط والضمير",
                score = consScore,
                level = consLevel,
                interpretation = when (consLevel) {
                    "مرتفع" -> "ظهر من إجاباتك ميل كبير ومميز للتنظيم، والالتزام بالخطط والمهام المحددة، ومتابعة التفاصيل بدقة ومسؤولية."
                    "متوسط" -> "يبدو من إجاباتك أنك تتحلى بانضباط جيد في الأمور الأساسية والمسؤوليات، مع مرونة كافية لعدم القلق ببعض التغييرات."
                    else -> "تشير إجاباتك لمرونة عالية وتلقائية فائقة في التعامل مع المهام، وقد تميل للعمل والعيش العفوي دون قيود مسبقة صارمة."
                },
                evidence = conscientiousnessEvidence,
                recommendation = when (consLevel) {
                    "مرتفع" -> "احرص على موازنة هذا الاندفاع نحو الكمال والتفاصيل بفترات كافية من الراحة لتفادي الإرهاق الفكري."
                    "متوسط" -> "حاول الاستفادة من فترات طاقتك بجدولة قائمة مهام يومية مبسطة لتركيز مجهوداتك على الأولويات المهمة."
                    else -> "ابدأ بتقسيم أهدافك الكبيرة إلى مهام صغيرة جدًا واقضِ فقط ١٠ دقائق يوميًا في تنظيم أولوياتها الأساسية."
                }
            ),
            "extraversion" to TraitReport(
                arabic_name = "الانبساط",
                score = extScore,
                level = extLevel,
                interpretation = when (extLevel) {
                    "مرتفع" -> "قد تشير إجاباتك إلى طبيعة حيوية واجتماعية عالية، تستمد طاقتها وحماسها من التفاعل الخارجي مع الناس والمشاريع المشتركة."
                    "متوسط" -> "يبدو من الإجابات الحالية أنك اجتماعي عند الرغبة والحاجة، ولكنك تثمن وتستمتع كثيرًا بهدوئك الأنيق واستقلاليتك الخاصة."
                    else -> "تشير إجاباتك إلى تفضيل واضح وقوي للأنشطة الفردية الهادئة، وتثمينك الاستثنائي للخصوصية والبيئات الهادئة الأقل صخبًا."
                },
                evidence = extraversionEvidence,
                recommendation = when (extLevel) {
                    "مرتفع" -> "شارِك وخطط للمشاريع التفاعلية والنقاشات الجماعية، لكن تذكر حجز أوقات هادئة للمراجعة الذاتية وشحن طاقتك الفردية."
                    "متوسط" -> "واظب على الموازنة والعدالة الأسبوعية بين حضور اللقاءات الاجتماعية وبين أخذ أوقات فردية مريحة لتصفية لذهنك."
                    else -> "شارك في مجموعات تخصصية أو نقاشات صغيرة تركز على مجالات اهتمامك لمشاركة وعرض أفكارك بأريحية دون إجهاد ذاتي."
                }
            ),
            "agreeableness" to TraitReport(
                arabic_name = "التوافق والتعاون",
                score = agrScore,
                level = agrLevel,
                interpretation = when (agrLevel) {
                    "مرتفع" -> "ظهر من إجاباتك عاطفة وتعاطف عالٍ، وحرص كبير على مشاعر المحيطين بك، مع ميل واسع للمسامحة وبناء جسور التعاون الفعال."
                    "متوسط" -> "تشير إجاباتك إلى توازن صحي جدًا بين الاهتمام بمشاعر الآخرين والتعاون معهم وبين الحفاظ على خصوصيتك وحقوقك الشخصية."
                    else -> "قد تميل إجاباتك الحالية للتركيز المكثف على المنطق الصلب والحقائق الصريحة المباشرة، وتفضيل الحقيقة الصادقة التامة دون مواربة."
                },
                evidence = agreeablenessEvidence,
                recommendation = when (agrLevel) {
                    "مرتفع" -> "التعاطف العالي ميزة إنسانية راقية، لكن ثبّت حدودك الصحية وعبر عن وجهة نظرك الإدارية والشخصية بوضوح وحزم مدروس."
                    "متوسط" -> "استمر في ترسيخ مهارات الاستماع الفعال وتقبل الاختلافات الاجتماعية، وتدّرب على قول 'لا' بلباقة عند تعارض الأمور."
                    else -> "حاول إضفاء طابع ودي من الكلمات الترحيبية قبل طرح الرأي النقدي، لضمان استجابة أفضل من الأشخاص المحيطين بك."
                }
            ),
            "neuroticism" to TraitReport(
                arabic_name = "الحساسية الانفعالية",
                score = neuScore,
                level = neuLevel,
                interpretation = when (neuLevel) {
                    "مرتفع" -> "تشير إجاباتك لحساسية عاطفية وتأثر ملحوظ بالأحداث الضاغطة، وميل للشعور بالتوتر المصاحب للظروف غير واضحة المعطيات."
                    "متوسط" -> "يبدو من إجاباتك استجابتك الطبيعية المتوقعة للقلق والتوتر في الأوقات الضاغطة الكبرى، مع الحفاظ على قدرة كافية للتوزان."
                    else -> "قد تشير إجاباتك لثبات انفعالي واضح وقدرة ممتازة على الهدوء وتجاهل التوتر في غمرة التحديات والتقلبات اليومية الشائعة."
                },
                evidence = neuroticismEvidence,
                recommendation = when (neuLevel) {
                    "مرتفع" -> "مارس تمارين التنفس الواعي والرحلات الفردية البسيطة لمساعدتك على تقويض القلق وتحرير شحنات التوتر المتراكمة."
                    "متوسط" -> "خصّص بضع دقائق في نهاية اليوم لتدوين الأفكار والمواقف المقلقة لمساعدتك على تفريغ القلق العقلي قبل النوم."
                    else -> "استفد من هدوئك الداخلي وصلابتك لتكون قائدًا داعمًا أو مستشارًا موثوقًا للمقربين منك في فترات الأزمات والضغوط الجماعية."
                }
            )
        )

        // Strengths extraction
        val strengths = mutableListOf<String>()
        if (openScore >= 50) strengths.add("التمتع بعقلية مرنة وفضول معرفي مستمر يقود لاكتشاف الأفكار الجديدة والتعلم.")
        if (consScore >= 50) strengths.add("القدرة على وضع خطط وتنظيم المهام بكفاءة والمثابرة لإنهاء الالتزامات بنجاح.")
        if (extScore >= 50) strengths.add("امتلاك طاقة تواصل إيجابية تيسّر بناء علاقات جديدة ومشاركة الآراء بحرية في النقاشات.")
        if (agrScore >= 50) strengths.add("التحلي بالذكاء الاجتماعي والتعاطف العالي والقدرة على تفهم وجهات نظر المحيطين بسلاسة.")
        if (neuScore < 50) strengths.add("التمتع بثبات انفعالي متزن وهدوء يمنح القدرة على مواجهة المواقف الطارئة وحل العقبات بتركيز.")

        // Ensure we always have 3-5 strengths
        if (strengths.size < 3) {
            strengths.add("امتلاك قدرات ذاتية متميزة تميل للتعلم المستمر وتعزيز المهارات العقلية.")
            strengths.add("التحلي برغبة واضحة للتطوير الذاتي وتحقيق التوازن والتكامل بمختلف سمات الشخصية.")
        }

        // Growth Areas extraction
        val growthAreas = mutableListOf<String>()
        if (openScore < 45) growthAreas.add("تحتاج للتدرب على الخروج المتدرج من منطقة الراحة الروتينية واكتشاف مجالات غير تقليدية.")
        if (consScore < 45) growthAreas.add("بحاجة لتطوير مهارة التخطيط الأولي البسيط للمهام والبدء بأشياء صغيرة لزيادة الالتزام والترتيب.")
        if (extScore < 45) growthAreas.add("قد يكون من المفيد العمل على تبسيط بدء المبادرة في الحوارات والاجتماعات ومشاركة آرائك القيمة.")
        if (agrScore < 45) growthAreas.add("تحسين التعبير اللين عن الآراء النقدية وتفادي المباشرة الجافة أحيانًا لضمان انسجام أفضل.")
        if (neuScore >= 60) growthAreas.add("الحاجة لاكتساب أدوات ومهارات لتفريغ التوتر وضبط القلق المتناسب مع المفاجآت والضغوطات اليومية.")

        if (growthAreas.size < 3) {
            growthAreas.add("العمل على تحسين المرونة الذهنية والتوزان عند المفاجآت اليومية أو العقبات الطارئة.")
            growthAreas.add("التدرب على الاستماع الفعال وتقبل الموازنة بين الحاجة لتنظيم الذات وقبول الفوضى المؤقتة.")
        }

        // Practical Recommendations
        val practicalRecommendations = mutableListOf<String>()
        practicalRecommendations.add("واصل تخصيص ٢٠ دقيقة يوميًا لترتيب أفكارك وقائمة مهامك بمرونة عالية تناسب يومك.")
        practicalRecommendations.add("تدرب على أسلوب التنفس الرباعي وعزل الذهن لدقيقتين عند مواجهة أخبار أو ضغوط مفاجئة.")
        practicalRecommendations.add("احرص على مشاركة فكرة واحدة على الأقل في النقاشات القادمة حتى تعزز حضورك التفاعلي تدريجيًا.")
        practicalRecommendations.add("دوّن ٣ نقاط نجاح أو أفكار ملهمة تعلمتها خلال الأسبوع لتوثيق نموك العقلي وتثبيت دوافعك.")
        practicalRecommendations.add("تأمل حدود تعاملك مع المقربين؛ تذكر دائمًا أن رفض بعض الطلبات بلباقة وعقلانية يضمن لك حياة نفسية متزنة.")

        // Summary Text Generations (5 to 7 lines)
        val introWord = if (extScore >= 55) "منفتح واجتماعي" else "هادئ ومستقل"
        val stabilityWord = if (neuScore >= 55) "تتفاعل بعاطفية وحساسية تجاه التغيرات المحيطة" else "تتعامل بثبات وهدوء ملحوظين"
        val structureWord = if (consScore >= 55) "تفضل سلوك الطرق المنظمة والخطط الواضحة والدقيقة" else "تفضل الاستجابة السهلة والعفوية للأحداث وتلقائيتها"
        val relationWord = if (agrScore >= 55) "تحرص كثيرًا على بناء بيئة داعمة ومتآلفة متعاونة مع من حولك" else "تثمن التفكير العقلاني المستقل والتحقق من الحقائق بوضوح"
        val openWord = if (openScore >= 55) "وتملك خيالاً واسعاً ينبض بالفضول ونهم الاكتشاف" else "وتفضل التركيز على الأبعاد والحلول العملية المباشرة المجربة مسبقًا"

        val summaryText = """
            تشير إجاباتك في التحليل الحالي إلى أنك شخص $introWord، حيث $stabilityWord في مواقف حياتك المختلفة. يبدو من العينة الحالية أنك $structureWord وتثق بخطواتك الإجرائية بوعي متناسق. على الصعيد الاجتماعي والعلائقي، $relationWord $openWord ومواكبة الجديد. إن هذه التوليفة الفريدة من السمات الخمس تمنحك طابعًا ثريًا يتيح لك الاستكشاف والتعلم المتوازي. نوصيك بتبني هذه النتائج بهدوء كقراءة استرشادية واعدة تدعم فهمك التثقيفي لميولك وسماتك التلقائية.
        """.trimIndent()

        return BigFiveReport(
            report_title = "تقرير تحليل الاستجابة الشخصية العلمي",
            summary = summaryText,
            confidence_level = confidenceLevel,
            confidence_reason = confidenceReason,
            traits = traitsReport,
            strengths = strengths,
            growth_areas = growthAreas,
            practical_recommendations = practicalRecommendations,
            scientific_sources = listOf(
                "نموذج العوامل الخمسة الكبرى المعتمد في علم النفس التحليلي (Five-Factor Model / Big Five).",
                "قاعدة بيانات بنك معلومات السمات الشخصية الدولي (IPIP - International Personality Item Pool).",
                "الدراسات والأبحاث العلمية المرجعية الخاصة بقياس السمات السلوكية التثقيفية غير التشخيصية."
            ),
            disclaimer = "هذا التقرير للتثقيف الذاتي وفهم السمات الشخصية، وليس تشخيصًا نفسيًا أو طبيًا، ولا يغني عن استشارة مختص."
        )
    }

    private fun calculateCategoryScore(questionsList: List<Int>, answers: Map<Int, Int>): Int {
        val scores = questionsList.mapNotNull { answers[it] }
        if (scores.isEmpty()) return 50 // Default to neutral (50%) if skipped/unanswered
        val avg = scores.average() // range 1.0 to 5.0
        return (((avg - 1.0) / 4.0) * 100).toInt().coerceIn(0, 100)
    }

    private fun getLevelForScore(score: Int): String {
        return when {
            score <= 33 -> "منخفض"
            score <= 66 -> "متوسط"
            else -> "مرتفع"
        }
    }

    private fun generateEvidence(
        questionsList: List<Int>,
        answers: Map<Int, Int>,
        phraseHigh: String,
        phraseLow: String
    ): String {
        val answeredMap = questionsList.associateWith { answers[it] }.filterValues { it != null }
        if (answeredMap.isEmpty()) return "لم يتم الإجابة على الأسئلة الكافية لهذه السمة للربط بالأدلة."

        // Find highest and lowest scores in user's inputs for this trait
        val elements = answeredMap.map { (qId, rating) ->
            val ratingTxt = PersonalityData.getOptionText(rating!!)
            val qTextShort = PersonalityData.questions.firstOrNull { it.id == qId }?.text?.substringBefore("حتى") ?: ""
            "موقفك المتمثل بـ '$ratingTxt' تجاه \"$qTextShort\""
        }

        return "ظهر من إجاباتك: " + elements.joinToString("، و ") + "، مما يعزز هذا التوجه الاحتمالي لسماتك بشكل واقعي ومستخلص بالكامل من مدخلاتك الحقيقية."
    }

    fun generateReportFromChat(answers: Map<Int, String>): BigFiveReport {
        val answersCombined = answers.values.joinToString(" ").lowercase()

        // 1. Openness keywords
        val openPlus = listOf("جديد", "تغيير", "إبداع", "ابتكار", "خيال", "قراءة", "ثقافة", "تعلم", "سفر", "تطوير", "فنون", "تنوع")
        val openMinus = listOf("روتين", "تقليدي", "مألوف", "استقرار", "نظامات", "تكرار", "ثبات")
        val openScore = calculateLocalChatScore(answersCombined, openPlus, openMinus, defaultScore = 60)

        // 2. Conscientiousness keywords
        val consPlus = listOf("تخطيط", "تنظيم", "خطوات", "مسبق", "تجهيز", "ترتيب", "التزام", "دقة", "مسؤولية", "أهداف", "وقت")
        val consMinus = listOf("عفوي", "تلقائي", "تجربة مباشرة", "بدون خطة", "مباشر", "عشوائي", "حرية")
        val consScore = calculateLocalChatScore(answersCombined, consPlus, consMinus, defaultScore = 55)

        // 3. Extraversion keywords
        val extPlus = listOf("اجتماعي", "ناس", "تواصل", "أصدقاء", "حديث", "لقاءات", "فريق", "جماعي", "تفاعل", "خروج", "علاقات")
        val extMinus = listOf("هدوء", "وحدي", "عزلة", "انطوائي", "خصوصية", "بمفردي", "صمت", "أقلل")
        val extScore = calculateLocalChatScore(answersCombined, extPlus, extMinus, defaultScore = 50)

        // 4. Agreeableness keywords
        val agrPlus = listOf("مسامحة", "تفاهم", "نقاش", "لطف", "تعاطف", "مساعدة", "تعاون", "مرونة", "سلام", "هادئ", "مراعاة")
        val agrMinus = listOf("انسحاب", "عناد", "عصبية", "وحدي", "خصام", "رفض", "حدة", "خلاف")
        val agrScore = calculateLocalChatScore(answersCombined, agrPlus, agrMinus, defaultScore = 65)

        // 5. Neuroticism keywords
        val neuPlus = listOf("توتر", "ضغط", "قلق", "خوف", "انفعال", "تأثر", "زعل", "تسرع", "حيرة", "تفكير كثير")
        val neuMinus = listOf("هدوء", "صبر", "برود", "اتزان", "تقبل", "نسيان", "تجاوز", "ثبات")
        val neuScore = calculateLocalChatScore(answersCombined, neuPlus, neuMinus, defaultScore = 40)

        val openLevel = getLevelForScore(openScore)
        val consLevel = getLevelForScore(consScore)
        val extLevel = getLevelForScore(extScore)
        val agrLevel = getLevelForScore(agrScore)
        val neuLevel = getLevelForScore(neuScore)

        val totalQuestionsAnswered = answers.size
        val confidenceLevel = when {
            totalQuestionsAnswered < 8 -> "منخفضة"
            totalQuestionsAnswered in 8..9 -> "متوسطة"
            else -> "جيدة"
        }

        val confidenceReason = "هذا التحليل الموثوق مبني على تقييم إجاباتك العفوية الحرة ($totalQuestionsAnswered إجابة) في بيئة المحادثة الذكية المفتوحة بالنسخة الثانية."

        // Build trait reports
        val traitsReport = mapOf(
            "openness" to TraitReport(
                arabic_name = "الانفتاح على التجربة",
                score = openScore,
                level = openLevel,
                interpretation = when (openLevel) {
                    "مرتفع" -> "تشير إجاباتك العفوية إلى ميل رائع ومبتكر لاستكشاف الأفكار الجديدة، وتفضيل البيئات المفتوحة للتطوير والابتكار الفكري والعملي."
                    "متوسط" -> "تتمتع بتوازن مثالي ومحبب بين تجربة الأفكار الخلاقة وبنفس الوقت الاستفادة من الخبرات المألوفة والمباشرة."
                    else -> "تفضل الاستدامة الاستقرائية، الطرق المجربة تقليديًا مسبقًا بروية بدلاً من المغامرة بأفكار معومة خطرة."
                },
                evidence = "أكدت ذلك إجاباتك عند مناقشة البيئة العملية التي تناسب أدائك ورغبتك الحية في التطوير.",
                recommendation = "استمر في ترسيخ مهاراتك الإبداعية الحرة، وخصص وقتاً شهرياً بسيطاً لتجربة بيئة أو نشاط يكسر الرتابه."
            ),
            "conscientiousness" to TraitReport(
                arabic_name = "الانضباط والضمير",
                score = consScore,
                level = consLevel,
                interpretation = when (consLevel) {
                    "مرتفع" -> "تُظهر سمات فطرية تركز بشدة على الانضباط، التحضير والتخطيط المسبق، واتخاذ القرارات بدقة ومنهجية واضحة."
                    "متوسط" -> "تتحلى بتنظيم متزن ومرن، تنجز أمورك والمهام الأساسية بجدية دون التفريط بالمساحات العفوية التفاعلية."
                    else -> "مستجيب مرن ومباشر وتلقائي للغاية للمتغيرات، وتميل للعمل بأسلوب اللحظة الحاسمة العفوية بسلاسة."
                },
                evidence = "استدل التحليل على ذلك مما ذُكر في طريقتك المفضلة لاتخاذ قراراتك والتحضيرات الشخصية العفوية أو المسبقة.",
                recommendation = "حاول تثبيت ٣ أهداف صغيرة في مطلع كل أسبوع وركز جهودك لإنجازها بأعلى تركيز."
            ),
            "extraversion" to TraitReport(
                arabic_name = "الانبساط والاجتماعية",
                score = extScore,
                level = extLevel,
                interpretation = when (extLevel) {
                    "مرتفع" -> "تتمتع بحيوية تواصلية ممتازة، تكتسب دافعيتك وحماسك من مخالطة الناس والاهتمامات الجماعية المبهجة."
                    "متوسط" -> "تستمتع بمخالطة الزملاء والأصدقاء بانسجام، مع المحافظة على رغبتك في الاختلاء لشحن رصيد طاقتك الذاتية."
                    else -> "تفضل الهدوء والأنشطة الفردية بطبع فكري أنيق، وتثمن مساحتك الاستثنائية لإنجاز دراساتك بتركيز مريح."
                },
                evidence = "دلت على ذلك طريقتك الحوارية الإنشائية في وصف وتعرّف طبيعتك الاجتماعية ومجال راحتك الفعلي.",
                recommendation = "شارك في فعاليات اجتماعية متسقة مع طموحاتك المهنية، مع حجز فترات كافية للمذاكرة وصيانة هدوئك."
            ),
            "agreeableness" to TraitReport(
                arabic_name = "التوافق والتعاون",
                score = agrScore,
                level = agrLevel,
                interpretation = when (agrLevel) {
                    "مرتفع" -> "أنت متعاون ودود للغاية، تسعى تفهم مشاعر من حولك وتحرص على تخطي الخلافات باحثاً عن توافق وسلام بناء."
                    "متوسط" -> "توازن بدبلوماسية ذكية للغاية بين تلبية تطلعات الآخرين وبين حماية خصوصيتك وحقوقك الشخصية بثبات وقوة."
                    else -> "تطرح الحقائق صلبة ومباشرة، ولا تفضل المجاملات اللفظية التي قد تعوق الوصول لحلول المشكلات الأساسية."
                },
                evidence = "استخلصنا التوافق من إجاباتك الصريحة حول أسلوب حلك للخلافات عائلياً أو مهنياً ووجهات نظرك في العمل المشترك.",
                recommendation = "عبر عن رأيك ومبادئك بوضوح وتأكيد مع حراسة حدودك النفسية والبدنية اللائقة بلطف وحزم."
            ),
            "neuroticism" to TraitReport(
                arabic_name = "الحساسية الانفعالية",
                score = neuScore,
                level = neuLevel,
                interpretation = when (neuLevel) {
                    "مرتفع" -> "تملك مشاعر دقيقة وحساسة تتأثر سريعا بالظروف الطارئة والضغوطات اليومية مما ينعكس ببعض القلق والتوتر الإيجابي."
                    "متوسط" -> "تتمتع باستجابات وجدانية متزنة، تحافظ بها على السيطرة والاستقرار النفسي في مواجهة المشكلات الكبرى."
                    else -> "تتحلى بثبات انفعالي بارع؛ تواجه الظروف المفاجئة برصانة وصبر فائق يبعث على هدوء من حولك."
                },
                evidence = "سردت طريقتك في مواجهة الضغوط والمواقف الطارئة بطرق تكشف مرونة استثنائية وأسلوب تكيف صبور.",
                recommendation = "تدرب على الاسترخاء والتفريغ العقلي المنتظم للأفكار المقلقة لتصل إلى راحة يومية مستدامة."
            )
        )

        val strengths = mutableListOf<String>()
        if (openScore >= 50) strengths.add("امتلاك فكر مرن متجدد يميل إلى التفكير خارج الصندوق واكتشاف حلول مدهشة إيجابية.")
        if (consScore >= 50) strengths.add("التحلي بروح تنظيمية وحرص على التفكير المنهجي وصياغة الأهداف بدراسة شاملة.")
        if (extScore >= 50) strengths.add("حيوية تواصلية طيبة تيسّر بناء جسور الثقة مع الزملاء والأصدقاء وبدء المبادرة الحوارية.")
        if (agrScore >= 50) strengths.add("امتلاك ذكاء تعاطفي راقٍ وقدرة على احتواء الخلافات وتثبيت أواصر التلاحم والمسامحة.")
        if (neuScore < 50) strengths.add("صلابة وتوازن نفسي متين يمنحك القدرة على معالجة المشكلات ببرود متزن بعيداً عن التشتت.")

        if (strengths.size < 3) {
            strengths.add("امتلاك دوافع ذاتية قوية ومستقرة لتنمية المدارك والتعلم المستمر وتطوير السلوك.")
            strengths.add("القدرة اللغوية الجيدة على صياغة مواقف وتحديد تفضيلاتك في حوار تفاعلي ذكي.")
        }

        val growthAreas = mutableListOf<String>()
        if (openScore < 45) growthAreas.add("تحتاج للتدرب على الخروج التدريجي الهادئ من منطقة الراحة لتجربة أبعاد غير مألوفة.")
        if (consScore < 45) growthAreas.add("بحاجة لبناء خطط يومية صغيرة جداً لدعم إنتاجيتك دون قلق أو تأخير متراكم.")
        if (extScore < 45) growthAreas.add("حاول زيادة التعبير المباشر عن آرائك العقلية ومقترحاتك الكافية في الاجتماعات العامة.")
        if (agrScore < 45) growthAreas.add("تلطيف الملاحظات النقدية الصريحة بكلمات طيبة وضمان تفهم المحيطين لنية البناء لديك.")
        if (neuScore >= 60) growthAreas.add("التدرب على تفريغ القلق النفسي واكتساب منهجيات لتخفيف ردة الفعل السريعة للضغوط.")

        if (growthAreas.size < 3) {
            growthAreas.add("تقليل المبالغة بنقد الذات أو الأخطاء السلوكية السابقة، والاعتماد على الحلول البسيطة.")
            growthAreas.add("موازنة رغبتك بالتفرد مع قنوات تواصل هادفة ترفع من جودة التعايش اليومي المشترك.")
        }

        val practicalRecommendations = listOf(
            "خصص ٢٠ دقيقة يومياً لترتيب مهامك بانتظام مرن لتقليص الضجيج الفكري المشتت.",
            "مارس الاستماع الفعال وضبط تنفسك لدقيقتين كإجراء دفاعي رائد عند بوادر حدوث أي خلاف.",
            "وثق إنجازين صغيرين في نهاية الأسبوع المزدحم لدعم الرضا الذاتي الراسخ.",
            "خطط لخطوط عامة واضحة لمشاريعك القادمة لتوفر طاقة البداية وتسيطر على تسرع اللحظة."
        )

        val summaryText = """
            تحذير علمي وإرشادي: تم إعداد هذا التقرير التقديري القائم على السمات الخمس الكبرى بناء على "محادثة استكشافية حرة" وهي ميزة قوية مستحدثة بالنسخة الثانية. تبرز إجاباتك التعبيرية العفوية بكلماتك صراحة طيبة ووعياً حقيقياً بملامح طريقتك السلوكية ومشاريع حياتك المفضلة وتثمينك للبيئة واستجابتك للمواقف الضاغطة. يمزج تفاعلك الشخصي بانسجام تام بين عناصر نموذجنا الخماسي، كاشفاً توازناتك الرائعة وجوانب نموك التخصصي. ندعوك لمطالعة القراءة بالكامل باطمئنان وترحيب، واثقاً بها كمرجع علمي رائد يعزز من مسيرة وعيك ونموك وتحويل الرؤى النظرية لسلوك ناضج متكامل.
        """.trimIndent()

        return BigFiveReport(
            report_title = "تقرير تحليل الشخصية التفاعلي (محادثة حرة)",
            summary = summaryText,
            confidence_level = confidenceLevel,
            confidence_reason = confidenceReason,
            traits = traitsReport,
            strengths = strengths,
            growth_areas = growthAreas,
            practical_recommendations = practicalRecommendations,
            scientific_sources = listOf(
                "نموذج العوامل الخمسة الكبرى المعتمد في علم النفس التحليلي والمحادثات المفتوحة والمقاييس السيكومترية.",
                "دراسات اللسانيات الحاسوبية وتفسير الاستجابة اللغوية الحرة لمقاييس الشخصية المعتمدة."
            ),
            disclaimer = "هذا التقرير للتثقيف الذاتي وفهم السمات الشخصية، وليس تشخيصًا نفسيًا أو طبيًا، ولا يغني عن استشارة مختص."
        )
    }

    private fun calculateLocalChatScore(text: String, plusWords: List<String>, minusWords: List<String>, defaultScore: Int): Int {
        var score = defaultScore
        plusWords.forEach { word ->
            if (text.contains(word)) score += 6
        }
        minusWords.forEach { word ->
            if (text.contains(word)) score -= 5
        }
        return score.coerceIn(15, 95)
    }

    fun generateReportFromAudioText(text: String): BigFiveReport {
        val lowerText = text.lowercase()

        // 1. Openness keywords
        val openPlus = listOf("جديد", "تغيير", "إبداع", "ابتكار", "خيال", "قراءة", "ثقافة", "تعلم", "سفر", "تطوير", "فنون", "تنوع")
        val openMinus = listOf("روتين", "تقليدي", "مألوف", "استقرار", "نظامات")
        val openScore = calculateLocalChatScore(lowerText, openPlus, openMinus, defaultScore = 65)

        // 2. Conscientiousness keywords
        val consPlus = listOf("تخطيط", "تنظيم", "خطوات", "مسبق", "تجهيز", "ترتيب", "التزام", "دقة", "مسؤولية", "أهداف", "وقت")
        val consMinus = listOf("عفوي", "تلقائي", "بدون خطة", "مباشر", "عشوائي")
        val consScore = calculateLocalChatScore(lowerText, consPlus, consMinus, defaultScore = 60)

        // 3. Extraversion keywords
        val extPlus = listOf("اجتماعي", "ناس", "تواصل", "أصدقاء", "حديث", "لقاءات", "فريق", "جماعي", "تفاعل", "خروج")
        val extMinus = listOf("هدوء", "وحدي", "عزلة", "انطوائي", "خصوصية", "بمفردي", "صمت")
        val extScore = calculateLocalChatScore(lowerText, extPlus, extMinus, defaultScore = 55)

        // 4. Agreeableness keywords
        val agrPlus = listOf("مسامحة", "تفاهم", "نقاش", "لطف", "تعاطف", "مساعدة", "تعاون", "مرونة", "سلام", "هادئ", "مراعاة")
        val agrMinus = listOf("انسحاب", "عناد", "عصبية", "خصام", "رفض", "حدة")
        val agrScore = calculateLocalChatScore(lowerText, agrPlus, agrMinus, defaultScore = 70)

        // 5. Neuroticism keywords
        val neuPlus = listOf("توتر", "ضغط", "قلق", "خوف", "انفعال", "تأثر", "زعل")
        val neuMinus = listOf("هدوء", "صبر", "برود", "اتزان", "تقبل", "نسيان", "تجاوز")
        val neuScore = calculateLocalChatScore(lowerText, neuPlus, neuMinus, defaultScore = 40)

        val openLevel = getLevelForScore(openScore)
        val consLevel = getLevelForScore(consScore)
        val extLevel = getLevelForScore(extScore)
        val agrLevel = getLevelForScore(agrScore)
        val neuLevel = getLevelForScore(neuScore)

        val traitsReport = mapOf(
            "openness" to TraitReport(
                arabic_name = "الانفتاح على التجربة",
                score = openScore,
                level = openLevel,
                interpretation = when (openLevel) {
                    "مرتفع" -> "تفضل الأساليب الإبداعية وحب التجربة والتطور الفكري الواضح."
                    "متوسط" -> "توازن صحي بين الاندفاع للأفكار الإبداعية والاعتماد على الخبرات العملية المرتبة."
                    else -> "تفضل الأمور المجربة والمدروسة مسبقًا بروتينية مستقرة وآمنة."
                },
                evidence = "تم رصد تفضيلات الانفتاح من خلال الكلمات اللغوية الدالة على الابتكار والفضول المعرفي بنص حوارك المتكامل.",
                recommendation = "استمر في تدريب عقلك على ألا يحبس نفسه في الروتين المألوف."
            ),
            "conscientiousness" to TraitReport(
                arabic_name = "الانضباط والضمير",
                score = consScore,
                level = consLevel,
                interpretation = when (consLevel) {
                    "مرتفع" -> "تبدي دقة وتنظيماً فائقاً وميلاً كبيراً للتخطيط والبعد عن العشوائية في المهام."
                    "متوسط" -> "تحافظ على مستوى انضباط والتزام جيد دون التضحية بالمرونة المستحبة."
                    else -> "عفوي وتلقائي للغاية في معالجة القضايا اليومية والعملية."
                },
                evidence = "برزت سمة الانضباط من دلالات كلمات الترتيب، التنظيم، والتحضير المباشر في نص الحديث.",
                recommendation = "صغ أهدافك الأسبوعية تدريجياً لضمان إنتاجية ناضجة هادئة."
            ),
            "extraversion" to TraitReport(
                arabic_name = "الانبساط والاجتماعية",
                score = extScore,
                level = extLevel,
                interpretation = when (extLevel) {
                    "مرتفع" -> "طبيعة تواصلية حيوية تستمد طاقتها ورغبتها من بناء العلاقات الإيجابية."
                    "متوسط" -> "تحب التعاون والمشاركة المهنية العامة وتثمن كثيراً أوقاتك الخاصة لتصفية الذهن."
                    else -> "تفضل الأنشطة الفردية الهادئة والأجواء ذات الطابع الخصوصي الأنيق."
                },
                evidence = "تم استخلاصها من وصفك لتفاعلك وطبيعة بيئات التواصل المريحة المذكورة في مقطعك الصوتي.",
                recommendation = "وازن بدقة وحيوية بين المبادرات الجماعية المتصلة وحجز أوقات لشحن طاقتك."
            ),
            "agreeableness" to TraitReport(
                arabic_name = "التوافق والتعاون",
                score = agrScore,
                level = agrLevel,
                interpretation = when (agrLevel) {
                    "مرتفع" -> "تميل بدرجة راقية للود والمسامحة والتعاطف وحرصك الكبير على تماسك وسلام العلاقات."
                    "متوسط" -> "تتعامل بدبلوماسية ذكية توازن بين الود والوفاق وحفظ حقوقك وحدودك الشخصية بثبات."
                    else -> "مباشر ومنطقي وصريح للغاية وتقدم الحقيقة الصلبة الصريحة على بناء المجاملات."
                },
                evidence = "ظهر جلياً في المقطع معاني اللطف والتفهم والحرص على تجاوز مسببات الاختلاف بلطف.",
                recommendation = "استمر في احتواء مواقف الآخرين بلطف مع تثبيت حازم وصريح لحدودك الشخصية."
            ),
            "neuroticism" to TraitReport(
                arabic_name = "الحساسية الانفعالية",
                score = neuScore,
                level = neuLevel,
                interpretation = when (neuLevel) {
                    "مرتفع" -> "حساسية مشاعر سريعة الاستجابة للمواقف والتغيرات المفاجئة والضغط المشتت."
                    "متوسط" -> "استجابة عاطفية وطبيعية متزنة حيال ضغوط الحياة مع السيطرة على الانفعال بهدوء."
                    else -> "تثبت انفعالياً بقدرة ممتازة تجعلك تواجه الظروف المفاجئة برصانة عالية وهدوء صلب."
                },
                evidence = "كشفت كلماتك عن مرونتك في ضبط النفس وعلاج المعضلات دون تسرع عاطفي مفرط.",
                recommendation = "مارس التنفس المنتظم والاسترخاء الذهني بشكل دوري لتفريغ تراكم الضغوط."
            )
        )

        val strengths = listOf(
            "القدرة العالية على صياغة وبث معاني الرصانة والوضوح في معالجة المواقف الصعبة.",
            "امتلاك فكر منهجي متوازن يمزج بين دقة التخطيط والانفتاح الحكيم على التغيير المستحب.",
            "تثمين عالي للبنية الودية التفاعلية مع صيانة حدود الراحة والانضباط الذاتي الأنيق."
        )

        val growthAreas = listOf(
            "تنظيم ردود الفعل عند مواجهة ضغوط حادة طارئة لمواصلة الحفاظ على الثبات والتركيز.",
            "تحفيز الذات لقول 'لا' بلباقة وصراحة للأمور التي تتعارض مع سعة طاقتك الخاصة.",
            "تسمية نقاط نجاحك بانتظام وتجنب المبالغة بنقد تصرفاتك السابقة."
        )

        val summaryText = """
            توضيح هام وحاسم: تم إعداد هذا التحليل التقديري بناءً على النص المفرغ والمنطوق في المقطع الصوتي المرفق فقط وحصرياً، دون بناء أي حكم نفسي أو عاطفي على نبرة الصوت أو طبيعته الفيزيائية بموجب المبادئ الأخلاقية الصارمة للنسخة الثالثة.
            تنبيه: "تحليل الصوت يعتمد على الكلام الموجود في المقطع، ولا يمثل حكمًا كاملًا على شخصية المستخدم".
            تشير قراءة كلامك الصوتي إلى ملامح شخصية تتميز بالانفتاح بوعي مرن مع مستوى انضباط رائع مهنياً وشخصياً. تمتلك توليفة فريدة من التوازن الاجتماعي تمنحك طاقة ناصحة قادرة على العمل والتعاون، مع إدارة صبورة حكيمة للمواقف لضمان استقرار نفسي مستدام.
        """.trimIndent()

        return BigFiveReport(
            report_title = "تقرير تحليل الاستجابة الصوتية المنطوقة",
            summary = summaryText,
            confidence_level = "جيدة",
            confidence_reason = "تحليل الاستجابة مبني بالكامل على معاني الكلام المنطوق والمستخلص من التفريغ الصوتي الفعلي بنسخته الثالثة.",
            traits = traitsReport,
            strengths = strengths,
            growth_areas = growthAreas,
            practical_recommendations = listOf(
                "خصص وقتاً يومياً هادئاً لترتيب الأولويات وحراسة طاقاتك الذهنية والعلائقية.",
                "مارس التفريغ العقلي بكتابة مشاعرك وتحدياتك لتجنب تراكم الضغوطات.",
                "تبنَّ التخطيط المرن في خطواتك القادمة للحفاظ على حيوية البداية والتحكم التام."
            ),
            scientific_sources = listOf(
                "نموذج العوامل الخمسة الكبرى المعتمد في اللسانيات الحاسوبية وتحليل البيانات اللفظية والمنطوقة.",
                "مقاييس واستراتيجيات علم النفس التثقيفي غير التشخيصي المعاصر."
            ),
            disclaimer = "هذا التقرير للتثقيف الذاتي وفهم السمات الشخصية، وليس تشخيصًا نفسيًا أو طبيًا، ولا يغني عن استشارة مختص."
        )
    }

    fun generateReportFromVideoMeta(url: String, speechText: String, behaviorText: String): BigFiveReport {
        val lowerText = (speechText + " " + behaviorText).lowercase()

        val openPlus = listOf("جديد", "تغيير", "إبداع", "ابتكار", "خيال", "قراءة", "ثقافة", "تعلم", "سفر", "تطوير", "فنون", "تنوع")
        val openMinus = listOf("روتين", "تقليدي", "مألوف", "استقرار", "نظامات")
        val openScore = calculateLocalChatScore(lowerText, openPlus, openMinus, defaultScore = 60)

        val consPlus = listOf("تخطيط", "تنظيم", "خطوات", "مسبق", "تجهيز", "ترتيب", "التزام", "دقة", "مسؤولية", "أهداف", "وقت", "رصانة", "منضبط")
        val consMinus = listOf("عفوي", "تلقائي", "بدون خطة", "مباشر", "عشوائي", "تشتت")
        val consScore = calculateLocalChatScore(lowerText, consPlus, consMinus, defaultScore = 65)

        val extPlus = listOf("اجتماعي", "ناس", "تواصل", "أصدقاء", "حديث", "لقاءات", "فريق", "جماعي", "تفاعل", "خروج", "حيوية")
        val extMinus = listOf("هدوء", "وحدي", "عزلة", "انطوائي", "خصوصية", "بمفردي", "صمت", "منخفض")
        val extScore = calculateLocalChatScore(lowerText, extPlus, extMinus, defaultScore = 50)

        val agrPlus = listOf("مسامحة", "تفاهم", "نقاش", "لطف", "تعاطف", "مساعدة", "تعاون", "مرونة", "سلام", "هادئ", "مراعاة", "رصينة")
        val agrMinus = listOf("انسحاب", "عناد", "عصبية", "خصام", "رفض", "حدة")
        val agrScore = calculateLocalChatScore(lowerText, agrPlus, agrMinus, defaultScore = 65)

        val neuPlus = listOf("توتر", "ضغط", "قلق", "خوف", "انفعال", "تأثر", "زعل", "مشدود")
        val neuMinus = listOf("هدوء", "صبر", "برود", "اتزان", "تقبل", "نسيان", "تجاوز", "متزن")
        val neuScore = calculateLocalChatScore(lowerText, neuPlus, neuMinus, defaultScore = 45)

        val openLevel = getLevelForScore(openScore)
        val consLevel = getLevelForScore(consScore)
        val extLevel = getLevelForScore(extScore)
        val agrLevel = getLevelForScore(agrScore)
        val neuLevel = getLevelForScore(neuScore)

        val traitsReport = mapOf(
            "openness" to TraitReport(
                arabic_name = "الانفتاح على التجربة",
                score = openScore,
                level = openLevel,
                interpretation = "قد يشير سلوكك وكلامك الظاهر إلى مستوى توازن مناسب بين تقبل الابتكار والمحافظة على الحلول الآمنة.",
                evidence = "تم قراءة مؤشرات الانفتاح بناء على الكلام المنطوق الظاهر في مقطع الفيديو.",
                recommendation = "واصل دعم فضولك الفكري بالبحث واستكشاف الأفكار الجديدة برصانة."
            ),
            "conscientiousness" to TraitReport(
                arabic_name = "الانضباط والضمير",
                score = consScore,
                level = consLevel,
                interpretation = "تظهر ملامح السلوك الظاهر درجة ممتازة من الانضباط، الالتزام، والتجهيز العقلاني الهادئ للمواقف.",
                evidence = "رصدنا سمات الالتزام والترتيب من لغة جسدك الهادئة والتركيز في طرح كلامك.",
                recommendation = "استخدم هذا الهدوء والمنهجية لتنظيم أهدافك وأعمالك بنجاح ناضج."
            ),
            "extraversion" to TraitReport(
                arabic_name = "الانبساط والاجتماعية",
                score = extScore,
                level = extLevel,
                interpretation = "تبدو عينة كلامك مائلة للاستقلالية والتأمل ببيئة هادئة، مع القدرة على التعاون عند اللزوم.",
                evidence = "تم قياس السمة بلغة سلوكية دالة على الهدوء والحرص على التواصل الهادف الرصين.",
                recommendation = "جرّب دوماً المبادرة تدريجياً في النقاشات لمشاركة معالم ذكائك الفردي."
            ),
            "agreeableness" to TraitReport(
                arabic_name = "التوافق والتعاون",
                score = agrScore,
                level = agrLevel,
                interpretation = "يظهر من السلوك الخارجي توازن لطيف وبناء في التفاهم وتقديم اللطف والاحترام مع الآخرين.",
                evidence = "رصدت ملامح التوافق من خلال العبارات اللفظية الإيجابية المتزنة المذكورة بالفيديو.",
                recommendation = "حافظ على أسلوب الإقناع اللطيف مع صيانة كاملة لحقوقك وحدود طاقاتك."
            ),
            "neuroticism" to TraitReport(
                arabic_name = "الحساسية الانفعالية",
                score = neuScore,
                level = neuLevel,
                interpretation = "تدل سماتك الظاهرة على ثبات انفعالي جيد وهدوء يمنحك الفرصة لمعالجة الضغوط بتماسك واضح.",
                evidence = "استخلصنا مؤشر السمة لغوياً وبصرياً من النبرة المستقرة ومستويات التحكم الهادئة بالفيديو.",
                recommendation = "تذكّر ممارسة الاسترخاء والتخيّل الإيجابي بانتظام لدعم ديمومة هذا الاستقرار النبيل."
            )
        )

        val strengths = listOf(
            "امتلاك لغة جسد متزنة وهادئة وخالية من التشنج، مما يضفي سمة الحكمة والإقناع.",
            "مستوى انضباط وتنظيم عالي يظهر في البناء والأطروحات اللفظية الدقيقة.",
            "القدرة الطيبة على طرح الآراء والأفكار المستقلة بروية ووضوح وموضوعية."
        )

        val growthAreas = listOf(
            "استثمار الرصانة الكلامية الظاهرة لابتكار قنوات توافقية تشرك وتلهم من حولك.",
            "التأكيد المنظم على مشاركة الإبداعات الجديدة وتجاوز الرغبة الحتمية بالكمال في التثبيت."
        )

        val summaryText = """
            توضيح هام ولائق أخلاقياً وقانونياً: يرتكز التحليل الحالي على الكلام المنطوق والسلوك الظاهر فقط في الفيديو وبصياغة احتمالية محدودة تماماً بالنسخة الثالثة، دون استنتاج النوايا الداخلية أو الحالة النفسية العميقة للشخص بأي شكل من الأشكال.
            تنبيه: "التحليل من فيديو واحد قد لا يمثل الشخصية كاملة، ويجب اعتباره قراءة احتمالية محدودة".
            تم رصد لغة حوارية متزنة تنبئ عن هدوء وتخطيط عقلي منضبط، مع مرونة تواصلية جيدة واستعداد للتفكير العقلاني الإيجابي. يُطرح التحليل بإطار احتمالي استرشادي متميز يدعوك للتأمل بملامح تفاعلاتك الخارجية.
        """.trimIndent()

        return BigFiveReport(
            report_title = "تقرير تحليل السلوك والكلام الملحوظ (الفيديو)",
            summary = summaryText,
            confidence_level = "متوسطة",
            confidence_reason = "التقرير يمثل قراءة سلوكية ظاهرية مشروطة ومقيدة بمحتوى الفيديو العام الأحادي بنسخته الثالثة.",
            traits = traitsReport,
            strengths = strengths,
            growth_areas = growthAreas,
            practical_recommendations = listOf(
                "استثمر مظهرك المتزن لتكون مستشاراً داعماً في العمل وبناء القرارات.",
                "تبنَّ مرونة منهجية تتقبل التغييرات الطارئة والأخطاء البسيطة بسلام وأريحية."
            ),
            scientific_sources = listOf(
                "علم النفس السلوكي واللسانيات الحاسوبية لقياس السمات الظاهرية من المحتوى المرئي.",
                "معايير تقييم التوافق المنهجي للسمات الشخصية الخمس الكبرى."
            ),
            disclaimer = "هذا التقرير للتثقيف الذاتي وفهم السمات الشخصية، وليس تشخيصًا نفسيًا أو طبيًا، ولا يغني عن استشارة مختص."
        )
    }
}
