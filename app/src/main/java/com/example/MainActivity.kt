package com.example
 
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Enforce RTL direction for full Arabic support
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        MainContent(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    viewModel: PersonalityViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val answers by viewModel.answers.collectAsState()
    val currentQIdx by viewModel.currentQuestionIndex.collectAsState()
    val report by viewModel.generatedReport.collectAsState()
    val showJsonDialog by viewModel.showJsonDialog.collectAsState()

    // Gather Live Chat details (Version 2)
    val selectedMethod by viewModel.selectedMethod.collectAsState()
    val chatAnswers by viewModel.chatAnswers.collectAsState()
    val currentChatIndex by viewModel.currentChatIndex.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAnalyzingChat by viewModel.isAnalyzingChat.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Glowing artistic aura background element of Elegant Dark purple
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-150).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), // Elegant purple glow
                            Color.Transparent
                        )
                    )
                )
        )

        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                AppScreen.START -> StartScreen(
                    onStartClick = { viewModel.selectScreen(AppScreen.CONSENT) },
                    onDashboardClick = { viewModel.selectScreen(AppScreen.DASHBOARD) }
                )
                AppScreen.CONSENT -> ConsentScreen(
                    onAgree = { viewModel.selectScreen(AppScreen.METHOD) },
                    onDisagree = { viewModel.selectScreen(AppScreen.START) }
                )
                AppScreen.METHOD -> MethodSelectionScreen(
                    onContinue = { method ->
                        viewModel.setSelectedMethod(method)
                        viewModel.selectScreen(AppScreen.FORMAT)
                    }
                )
                AppScreen.FORMAT -> ResultsFormatScreen(
                    selectedMethod = selectedMethod,
                    onStartQuiz = {
                        when (selectedMethod) {
                            "chat" -> viewModel.selectScreen(AppScreen.CHAT_QUIZ)
                            "audio" -> viewModel.selectScreen(AppScreen.AUDIO_UPLOAD)
                            "video" -> viewModel.selectScreen(AppScreen.VIDEO_URL)
                            else -> viewModel.selectScreen(AppScreen.QUIZ)
                        }
                    }
                )
                AppScreen.QUIZ -> QuizScreen(
                    currentQuestionIndex = currentQIdx,
                    answers = answers,
                    onSelectAnswer = { score -> viewModel.answerCurrentQuestion(score) },
                    onNext = { viewModel.nextQuestion() },
                    onPrev = { viewModel.prevQuestion() },
                    onFinishEarly = { viewModel.finishQuizEarly() }
                )
                AppScreen.CHAT_QUIZ -> ChatQuizScreen(
                    chatMessages = chatMessages,
                    currentChatIndex = currentChatIndex,
                    isAnalyzingChat = isAnalyzingChat,
                    totalQuestions = viewModel.chatQuestions.size,
                    chatAnswersCount = chatAnswers.size,
                    onBackClick = { viewModel.selectScreen(AppScreen.METHOD) },
                    onSendAnswer = { text -> viewModel.submitChatResponse(text) },
                    onGenerateReport = { viewModel.generateChatAnalysis() }
                )
                AppScreen.AUDIO_UPLOAD -> AudioUploadScreen(viewModel = viewModel)
                AppScreen.VIDEO_URL -> VideoUrlScreen(viewModel = viewModel)
                AppScreen.REPORT -> ReportDashboardScreen(
                    report = report,
                    viewModel = viewModel,
                    onRestart = { viewModel.restartAnalysis() },
                    onViewJson = { viewModel.setShowJsonDialog(true) }
                )
                AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                AppScreen.TERMS -> TermsScreen(onBack = { viewModel.selectScreen(AppScreen.DASHBOARD) })
                AppScreen.PRIVACY -> PrivacyScreen(onBack = { viewModel.selectScreen(AppScreen.DASHBOARD) })
                AppScreen.DEVELOPMENT_PLAN -> DevelopmentPlanScreen(viewModel = viewModel)
                AppScreen.PLAN_COMPARISON -> PlanComparisonScreen(viewModel = viewModel)
                AppScreen.ERROR_BLOCKED -> ErrorBlockedScreen(
                    viewModel = viewModel,
                    onBackToQuestions = { viewModel.selectScreen(AppScreen.QUIZ) }
                )
            }
        }

        // Structured JSON Code Viewer Dialog
        if (showJsonDialog && report != null) {
            JsonReportDialog(
                jsonText = viewModel.getReportJsonString(),
                onDismiss = { viewModel.setShowJsonDialog(false) }
            )
        }
    }
}

// 1. Shasht Bedaya (Start screen)
@Composable
fun StartScreen(
    onStartClick: () -> Unit,
    onDashboardClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Brain/Science Atmospheric Illustration Icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "أيقونة التحليل العلمي",
                modifier = Modifier.size(54.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "تحليل الشخصية العلمي",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "افهم ميولك وسماتك الشخصية من خلال أسئلة منظمة أو محادثة، مع تقرير مبسط مدعوم بمصادر علمية دقيقة وموثوقة.",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 54.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("start_analysis_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ابدأ التحليل",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowBack, // Will flip normally in RTL
                    contentDescription = "السهم",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onDashboardClick,
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 54.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("commercial_dashboard_btn")
        ) {
            Text(
                text = "لوحة التحكم والاشتراكات (النسخة التجارية)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// 2. Shashat Mowafaqah (Consent screen)
@Composable
fun ConsentScreen(
    onAgree: () -> Unit,
    onDisagree: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "تنبيه هام",
                modifier = Modifier.size(44.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "موافقة والتزام علمي وطبي",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "هذا التحليل مخصص للتثقيف الذاتي ومساعدتك على فهم سماتك الشخصية العامة فقط. هو يقوم بقراءة احتمالية تعليمية بناءً على دراسات Big Five ولا يُصنف كتنبؤ طبي أو نفسي عكاري، كما أنه ليس تشخيصًا نفسيًا أو طبيًا على الإطلاق، ولا يغني بأي شكل من الأشكال عن استشارة الأخصائيين والخبراء المعتمدين.",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Justify
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAgree,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .testTag("agree_button")
        ) {
            Text(
                text = "أوافق وأبدأ",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onDisagree,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .testTag("disagree_button")
        ) {
            Text(
                text = "لا أوافق (الرجوع)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 3. Selection of analysis method screen
@Composable
fun MethodSelectionScreen(
    onContinue: (String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("qa") } // qa, chat, audio, video

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "اختر طريقة التحليل المفضلة",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "حدد الوسيلة التي ترغب بتقديم سمات حياتك من خلالها",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Options
        MethodItemCard(
            title = "سؤال وجواب",
            description = "أجب على ٢٠ سؤالاً علمياً مصمماً بعناية لقياس أبعاد الشخصية الكبرى.",
            icon = Icons.Default.Edit,
            enabled = true,
            selected = selectedMethod == "qa",
            onClick = { selectedMethod = "qa" }
        )

        MethodItemCard(
            title = "محادثة حرة الكوتش الذكي",
            description = "تحدث بحرية مطلقة مع الكوتش الذكي ليرسم أبعاد وسمات شخصيتك الخمس.",
            icon = Icons.Default.Favorite,
            enabled = true,
            selected = selectedMethod == "chat",
            onClick = { selectedMethod = "chat" }
        )

        MethodItemCard(
            title = "تحليل مقطع صوتي",
            description = "ارفع مقطعاً صوتياً لتفريغ الكلام وتحليل السمات بناءً على دلالات الألفاظ والعبارات المنطوقة.",
            icon = Icons.Default.PlayArrow,
            enabled = true,
            selected = selectedMethod == "audio",
            onClick = { selectedMethod = "audio" }
        )

        MethodItemCard(
            title = "تحليل رابط فيديو",
            description = "مشاركة رابط فيديو عام لقراءة الكلام المسرد والسلوك الظاهري بصياغة احتمالية آمنة ومدروسة.",
            icon = Icons.Default.PlayArrow,
            enabled = true,
            selected = selectedMethod == "video",
            onClick = { selectedMethod = "video" }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onContinue(selectedMethod) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .testTag("continue_button")
        ) {
            Text(
                text = "استمرار",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MethodItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    enabled: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                enabled -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            }
        ),
        border = BorderStroke(
            if (selected) 1.5.dp else 1.dp,
            when {
                selected -> MaterialTheme.colorScheme.primary
                enabled -> MaterialTheme.colorScheme.outline
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!enabled) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "قريباً",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            if (!enabled) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "مغلق",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
            } else if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "تم الاختيار",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// 4. Choosing presentation format of recommendations screen
@Composable
fun ResultsFormatScreen(
    selectedMethod: String,
    onStartQuiz: () -> Unit
) {
    var selectedFormat by remember { mutableStateOf("text") } // text, audio, both

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "اختر طريقة عرض التقرير النظري",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "كيف تفضل استلام وقراءة قراءة علم النفس لسماتك؟",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        MethodItemCard(
            title = "تقرير كتابي مفصل (نصي)",
            description = "تقرير نصي مرئي كامل بصيغة بطاقات إيضاحية مجهزة بقابلية عرض الكود JSON.",
            icon = Icons.Default.Edit,
            enabled = true,
            selected = selectedFormat == "text",
            onClick = { selectedFormat = "text" }
        )

        MethodItemCard(
            title = "تقرير صوتي تفاعلي (بودكاست)",
            description = "ميزة قادمة - استمع للتحليل بصوت مجسّد تفاعلي يشرح لك سماتك بانسجام.",
            icon = Icons.Default.PlayArrow,
            enabled = false,
            selected = selectedFormat == "audio",
            onClick = {}
        )

        MethodItemCard(
            title = "نصي وصوتي معاً",
            description = "ميزة قادمة - تجمع بين القراءة النصية والاستماع الموجه لسهولة التوافق والوعي.",
            icon = Icons.Default.PlayArrow,
            enabled = false,
            selected = selectedFormat == "both",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onStartQuiz,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .testTag("start_quiz_button")
        ) {
            val buttonText = when (selectedMethod) {
                "chat" -> "بدء المحادثة الاستكشافية الحرة"
                "audio" -> "الانتقال لرفع مقطع صوتي"
                "video" -> "الانتقال لتحليل فيديو عام"
                else -> "بدء الاختبار والاستبيان"
            }
            Text(
                text = buttonText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 5. Quiz wizard
@Composable
fun QuizScreen(
    currentQuestionIndex: Int,
    answers: Map<Int, Int>,
    onSelectAnswer: (Int) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onFinishEarly: () -> Unit
) {
    val question = PersonalityData.questions[currentQuestionIndex]
    val selectedRating = answers[question.id]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Progress indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تقدّم استمارة السمات الشخصية",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "السؤال ${currentQuestionIndex + 1} من ٢٠",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { (currentQuestionIndex + 1) / 20f },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Question display card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = question.text,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "حدد مستوى التوافق الشخصي معك:",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 5 Rating vertical choices cards
        val ratingOptions = listOf(
            5 to "أوافق جدًا",
            4 to "أوافق",
            3 to "محايد",
            2 to "لا أوافق",
            1 to "لا أوافق أبدًا"
        )

        ratingOptions.forEach { (rating, label) ->
            val isSelected = selectedRating == rating
            val optionTag = "rate_${rating}_button"

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    if (isSelected) 1.5.dp else 1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { onSelectAnswer(rating) }
                    .testTag(optionTag)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$rating - $label",
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )

                    RadioButton(
                        selected = isSelected,
                        onClick = { onSelectAnswer(rating) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Navigation controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Previous button
            OutlinedButton(
                onClick = onPrev,
                enabled = currentQuestionIndex > 0,
                border = BorderStroke(1.dp, if (currentQuestionIndex > 0) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .weight(0.48f)
                    .testTag("prev_question_button")
            ) {
                Text(text = "السابق", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Next button
            Button(
                onClick = onNext,
                enabled = selectedRating != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .weight(0.48f)
                    .testTag("next_question_button")
            ) {
                Text(text = if (currentQuestionIndex == 19) "عرض النتيجة" else "التالي", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Finish early button
        TextButton(
            onClick = onFinishEarly,
            modifier = Modifier.testTag("show_results_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "إنهاء مبكر",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "إنهاء مبكر وعرض النتيجة الحالية",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// 6. Report dashboard
@Composable
fun ReportDashboardScreen(
    report: BigFiveReport?,
    viewModel: PersonalityViewModel,
    onRestart: () -> Unit,
    onViewJson: () -> Unit
) {
    if (report == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App header inside report page
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "التقرير جاهز",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = report.report_title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Arabic Natural TTS Audio Player Card (Version 2)
        item {
            val isPlaying by viewModel.ttsIsPlaying.collectAsState()
            var hasAskedTTS by remember { mutableStateOf(false) }
            val context = LocalContext.current
            val speechText = remember(report) { report.generateSpeechNarrative() }
            
            var ttsManager by remember { mutableStateOf<PersonalityTTS?>(null) }
            
            DisposableEffect(context) {
                val manager = PersonalityTTS(context) { _ -> }
                ttsManager = manager
                onDispose {
                    manager.shutdown()
                    viewModel.setTTSPlaying(false)
                }
            }

            if (!hasAskedTTS && !isPlaying) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "قراءة صوتية",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "هل تريد سماع التقرير صوتيًا؟",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Text(
                            text = "احصل الآن على سرد هادئ وواضح وخلفي يشرح سماتك خمسية الأبعاد بأسلوب السرد التفاعلي الدافئ دون قراءة الروابط أو البيانات الجداول حرفياً.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            lineHeight = 22.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { hasAskedTTS = true }) {
                                Text("تخطي الآن", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    hasAskedTTS = true
                                    viewModel.setTTSPlaying(true)
                                    ttsManager?.speak(speechText)
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "بدء تشغيل الصوت",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نعم، شغّل الصوت", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlaying) Color(0xFF10B981) else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPlaying) "جاري تشغيل القراءة الصوتية الطبيعية..." else "جاهز لإعادة القراءة الصوتية",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            if (isPlaying) {
                                IconButton(
                                    onClick = {
                                        ttsManager?.stop()
                                        viewModel.setTTSPlaying(false)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "إيقاف الصوت",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isPlaying) {
                            AnimatedSpeechWaveform()
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.height(32.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(5) {
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    if (isPlaying) {
                                        ttsManager?.stop()
                                        viewModel.setTTSPlaying(false)
                                    } else {
                                        viewModel.setTTSPlaying(true)
                                        ttsManager?.speak(speechText)
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "إقفال" else "تشغيل",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPlaying) "إيقاف مؤقت للقراءة" else "استمع للتقرير كاملاً",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Disclaimer notice at starting (Cherry error/warning block)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ErrorBg),
                border = BorderStroke(1.dp, ErrorBorder),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "تحذير أخلاقي",
                        tint = ErrorText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "هذا التقرير للتثقيف الذاتي وفهم السمات الشخصية، وليس تشخيصاً نفسياً أو طبياً، ولا يغني عن استشارة مختص.",
                        color = ErrorText,
                        fontSize = 11.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Confidence badge card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "موثوقية التحليل العلمي:",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        val badgeColor = when (report.confidence_level) {
                            "جيدة" -> Color(0xFF10B981)
                            "متوسطة" -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        }
                        Box(
                            modifier = Modifier
                                .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                .border(1.dp, badgeColor, RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = report.confidence_level,
                                color = badgeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = report.confidence_reason,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Summary statement (5 to 7 lines) (Styled like Elegant Dark summary section)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ملخص التقرير",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = report.summary,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        lineHeight = 25.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }

        // Five Traits Title Item
        item {
            Text(
                text = "تحليل الأبعاد والسمات الخمس الكبرى",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        // Rendering each of the 5 traits dynamically from Map
        items(report.traits.values.toList()) { trait ->
            TraitAnalysisCard(trait)
        }

        // Strengths Section (Solid DarkSurface card, soft green accent details)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "مميّز",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "جوانب القوة والتميز",
                            color = Color(0xFF34D399),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    report.strengths.forEach { strength ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", color = Color(0xFF34D399), fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
                            Text(text = strength, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }

        // Growth Areas Section (Solid DarkSurface card, soft purple accent details)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "فرص تنموية",
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "جوانب تخصصية قابلة للتطوير",
                            color = Color(0xFFA78BFA),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    report.growth_areas.forEach { area ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", color = Color(0xFFA78BFA), fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
                            Text(text = area, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }

        // Custom Behavior & General Practical Recommendations Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "توصيات شخصية",
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "التوصيات العامة المقترحة",
                            color = Color(0xFF60A5FA),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    report.practical_recommendations.forEachIndexed { i, rec ->
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${i + 1}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = rec, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }

        // Toggle for comparative cultural reading (Optional Advanced Feature)
        item {
            var enableCulturalReading by remember { mutableStateOf(false) }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "قراءة ثقافية",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "قراءة ثقافية مقارنة (اختياري/متقدم)",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ربط السمات الشخصية المعاصرة بالثقافة والتاريخ الإنساني.",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = enableCulturalReading,
                        onCheckedChange = { enableCulturalReading = it },
                        modifier = Modifier.testTag("cultural_reading_toggle")
                    )
                }

                if (enableCulturalReading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Explicit disclaimer block as requested
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ErrorBg.copy(alpha = 0.6f)),
                        border = BorderStroke(1.dp, ErrorBorder.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "هذه القراءة الثقافية للتأمل والمقارنة فقط، وليست أساس الدرجة العلمية في التقرير.",
                            color = ErrorText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.0.dp).fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "توضيح علمي حاسم:",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "يتم قياس وحساب درجات السمات المئوية في هذا التقرير حصرياً بناءً على نموذج العوامل الخمسة الكبرى (Big Five)، وهو الأساس العلمي المعتمد في علم النفس الحسابي المعاصر. تُعرض مادة الطبائع الأربعة والفراسة بالأسفل كخيار تاريخي تثقيفي للمقارنة والتوجّه الفلسفي، ولا صلة لها بحساب الدرجات الفعلية للمستخدم.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Extract openness, conscientiousness, extraversion, agreeableness, neuroticism values
                    val openScore = report.traits["openness"]?.score ?: 50
                    val consScore = report.traits["conscientiousness"]?.score ?: 50
                    val extScore = report.traits["extraversion"]?.score ?: 50
                    val agrScore = report.traits["agreeableness"]?.score ?: 50
                    val neuScore = report.traits["neuroticism"]?.score ?: 50

                    // 1. Four Humors mapping
                    val humorsResult = when {
                        extScore >= 55 && neuScore < 45 -> "المزاج الدموي (Sanguine) - يرمز تاريخياً للحيوية والهواء والحرارة والرطوبة."
                        neuScore >= 55 && extScore < 45 -> "المزاج السوداوي (Melancholic) - يرمز تاريخياً للتأمل والتراب والبرودة والجفاف."
                        consScore >= 55 && neuScore >= 50 -> "المزاج الصفراوي (Choleric) - يرمز تاريخياً للحزم والنار والحرارة والجفاف."
                        extScore < 45 && neuScore < 45 -> "المزاج البلغمي (Phlegmatic) - يرمز تاريخياً للهدوء والماء والبرودة والرطوبة."
                        else -> "مزيج متكامل الأبعاد ومتساوي النفس من الطبائع الأربعة التراثية القديمة للتأمل الفلسفي."
                    }

                    Text(
                        text = "أولاً: الطبائع الأربعة في التراث اليوناني القديم",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = humorsResult,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Arabic Physiognomy mapping (Firasa)
                    val firasaResult = when {
                        openScore >= 60 -> "أنت تميل لـ \"فراسة التفكير والحكمة\". تاريخياً، كان يُربط حب المعرفة واستكشاف الأسرار بفراسة تتبع الأثر وقياس العواقب والتأمل الذهني الشامل."
                        consScore >= 60 -> "أنت تميل لـ \"فراسة الحزم والتدبير\". تاريخياً، كان يُربط الالتزام الشديد والتجهيز الدقيق بفراسة الإمارة والقيادة وحسن الولاية المالية والعملية."
                        extScore >= 60 -> "أنت تميل لـ \"فراسة البلاغة والبيان\". تاريخياً، كان يُربط التواصل البشري الواسع بفراسة الخطابه والفصاحه ودبلوماسية الوجهاء الاجتماعية."
                        agrScore >= 60 -> "أنت تميل لـ \"فراسة الألفة والصلح\". تاريخياً، كان يُربط الود التام وحفظ العهود بفراسة تطييب النفوس وتقريب القلوب العشائرية."
                        neuScore >= 60 -> "أنت تميل لـ \"فراسة التوقي والتحرز\". تاريخياً، كان يُربط شدة التأثر والحذر بوعي الفطن الحريص الذي يستشعر مواضع الخطر ويتحصن بالوقاية."
                        else -> "أنت تميل لـ \"فراسة الاعتدال السلوكي\". توازن تام في خامات التفرس والمكاسب السلوكية والاعتدال بشتى الجوانب."
                    }

                    Text(
                        text = "ثانياً: علم الفراسة في التراث العربي كمادة تاريخية",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = firasaResult,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. General Cultures (Yin & Yang)
                    val asianResult = if (extScore < 50) {
                        "توازن الين واليانغ (Yin & Yang) في الثقافة الشرق آسيوية القديمة: ميلك الهدوء والتأمل ينسجم مع قوى الين (Yin) التي تمثل السكون، والاحتفاظ بمخزون الطاقة وعميق التفكير."
                    } else {
                        "توازن الين واليانغ (Yin & Yang) في الثقافة الشرق آسيوية القديمة: ميلك للنشاط والتواصل الخارجي ينسجم مع قوى اليانغ (Yang) التي تمثل الحركة والديناميكية والإشعاع التفاعلي."
                    }

                    Text(
                        text = "ثالثاً: تصنيفات المزاج والتوازن من ثقافات مختلفة",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = asianResult,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Scientific Sources list
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "المراجع",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "مصادر ومراجعة التحليل العلمي",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    report.scientific_sources.forEach { source ->
                        Text(
                            text = "• $source",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Export JSON option Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الحصول على التقرير بهيئة كود JSON برمجى",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onViewJson,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .testTag("view_json_button")
                    ) {
                        Icon(imageVector = Icons.Default.Build, contentDescription = "كود")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "عرض الرمز البرمجي JSON", fontSize = 13.sp)
                    }
                }
            }
        }

        // Final Ethical alert disclaimer block
        item {
            Text(
                text = "تنبيه نهائي: ${report.disclaimer}",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 11.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        // Export buttons & Control buttons row
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Enabled restart analysis button
                val currentPlan by viewModel.currentUserPlan.collectAsState()
                
                Button(
                    onClick = {
                        if (currentPlan == UserPlan.GOLD) {
                            viewModel.generatePersonalDevPlan(report)
                        } else {
                            viewModel.setBlockedReason("gold_required_blocked")
                            viewModel.selectScreen(AppScreen.ERROR_BLOCKED)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentPlan == UserPlan.GOLD) SoftGreen else MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("activate_dev_plan_button")
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "نجمة")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "توليد خطة التنمية والتطوير الذاتي (٣٠ يوماً) ⭐", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.selectScreen(AppScreen.DASHBOARD) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("back_to_dashboard_from_report_btn")
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "لوحة")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "الرجوع للوحة التحكم والمشتركات", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .testTag("restart_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "إعادة")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "إعادة إجراء التحليل المبدئي", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Disabled other feature placeholders
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ReportActionPlaceholderButton(label = "حفظ السجل محلياً", tag = "save_button")
                    Spacer(modifier = Modifier.width(6.dp))
                    ReportActionPlaceholderButton(label = "تعديل النمط لـ PDF", tag = "download_button")
                    Spacer(modifier = Modifier.width(6.dp))
                    ReportActionPlaceholderButton(label = "طمس التقرير", tag = "delete_button")
                }
            }
        }
    }
}

@Composable
fun RowScope.ReportActionPlaceholderButton(
    label: String,
    tag: String
) {
    // Hidden action with capsule style matching user intent and designer requirements
    Box(
        modifier = Modifier
            .weight(1f)
            .height(44.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "قريباً",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                fontSize = 8.sp
            )
        }
    }
}

@Composable
fun TraitAnalysisCard(trait: TraitReport) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Trait Title and Badge level
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when (trait.level) {
                                    "مرتفع" -> Color(0xFF10B981)
                                    "متوسط" -> Color(0xFFF59E0B)
                                    else -> Color(0xFFEF4444)
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = trait.arabic_name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "المستوى: ${trait.level}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score with Progress Indicator bar (Uniform accent color matching design HTML!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الدرجة: ${trait.score}%",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(80.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    LinearProgressIndicator(
                        progress = { trait.score / 100f },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .height(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interpretation Section
            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                Text(
                    text = "التفسير المرجعي الاحتمالي:",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trait.interpretation,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)

            // Evidence bound section (Dlil Men Ejabatek)
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "دليل واقعي من إجاباتك الحالية:",
                    color = Color(0xFF34D399),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trait.evidence,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)

            // Recommendation
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = "توصية عملية مخصصة:",
                    color = Color(0xFFF59E0B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trait.recommendation,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// 7. Error Block page
data class Quintet<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

@Composable
fun ErrorBlockedScreen(
    viewModel: PersonalityViewModel,
    onBackToQuestions: () -> Unit
) {
    val blockedReason by viewModel.blockedReason.collectAsState()
    val answers by viewModel.answers.collectAsState()
    val answersCount = answers.size

    val (titleText, bodyText, iconVector, buttonText, onBtnClick) = when (blockedReason) {
        "psychiatric_refusal" -> {
            Quintet(
                "تنبيه فحص أخلاقي وإرشادي",
                "تم كشف كلمات أو تساؤلات تتعلق بطلب تشخيص طبي أو علاج نفسي صريح. يرجى العلم بأن هذا التطبيق مصمم لغرض تثقيفي وتنموي سلوكي عام (Big Five)، ولا يقدم تشخيصًا نفسيًّا أو طبيًّا للمسائل السريرية ولا يغني إطلاقاً عن زيارة الأخصائي أو المعالج النفسي المرخص.",
                Icons.Default.Warning,
                "العودة إلى لوحة التحكم",
                { viewModel.selectScreen(AppScreen.DASHBOARD) }
            )
        }
        "consent_required" -> {
            Quintet(
                "احترام خصوصية وموافقة الآخرين",
                "تم رفض التحليل لعدم توفر الموافقة الصريحة. يرجى العلم بأن التطبيق يتبنى مبدأ ‘احترام خصوصية الآخرين’، ويُمنع منعاً باتاً محاولة استقصاء أو تحليل سمات شخص آخر دون إذنه وموافقته المعلنة صراحة ومكتوبة.",
                Icons.Default.Lock,
                "العودة لتأكيد الموافقة",
                { viewModel.selectScreen(AppScreen.DASHBOARD) }
            )
        }
        "free_plan_blocked" -> {
            Quintet(
                "الميزة تتطلب باقة أعلى",
                "نعتذر، محادثات الكوتش الحرة التفاعلية غير متوفرة في الباقة المجانية. يرجى الذهاب للوحة التحكم وترقية باقتك إلى الفضية أو الذهبية للاستمتاع بالمزايا اللامحدودة.",
                Icons.Default.Star,
                "ترقية الباقة الآن",
                { viewModel.selectScreen(AppScreen.DASHBOARD) }
            )
        }
        "gold_required_blocked" -> {
            Quintet(
                "رصيد الباقة الذهبية مطلوب",
                "التحليل الصوتي، تحليل الفيديو، والحصول على اللوائح التنموية لـ ٣٠ يوماً تتطلب ترقية حسابك للباقة الذهبية الممتازة.",
                Icons.Default.Star,
                "الذهاب للباقات",
                { viewModel.selectScreen(AppScreen.DASHBOARD) }
            )
        }
        else -> {
            Quintet(
                "الإجابات الحالية غير كافية لموثوقية التقرير",
                "لقد قمت بالإجابة على 5 أسئلة فقط حتى الآن. النظام يتطلب إجابة 6 أسئلة كحد أدنى لتجنب توليد نتائج مضللة أو غير كاملة.",
                Icons.Default.Warning,
                "العودة للأسئلة واستكمال الاختبار",
                onBackToQuestions
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("error_indicator_view"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(ErrorBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = titleText,
                tint = ErrorText,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = titleText,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = bodyText,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontSize = 15.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onBtnClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(vertical = 14.dp, horizontal = 28.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("go_back_to_questions_button")
        ) {
            Text(
                text = buttonText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Structured scientific JSON container modal dialog Component
@Composable
fun JsonReportDialog(
    jsonText: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(vertical = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جسم التقرير بصيغة JSON",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "اغلاق", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable content showing JSON
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    val scroll = rememberScrollState()
                    Text(
                        text = jsonText,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .verticalScroll(scroll)
                            .horizontalScroll(rememberScrollState())
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "إغلاق النافذة")
                }
            }
        }
    }
}

@Composable
fun AnimatedSpeechWaveform() {
    val infiniteTransition = rememberInfiniteTransition(label = "speech_wave")
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.height(44.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val heights = listOf(0.4f, 0.8f, 0.5f, 0.9f, 0.6f)
        heights.forEachIndexed { index, weight ->
            val heightFraction by infiniteTransition.animateFloat(
                initialValue = 0.15f * weight,
                targetValue = weight,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 400 + index * 100, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )
            
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight(heightFraction)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun ChatQuizScreen(
    chatMessages: List<ChatMessage>,
    currentChatIndex: Int,
    isAnalyzingChat: Boolean,
    totalQuestions: Int,
    chatAnswersCount: Int,
    onBackClick: () -> Unit,
    onSendAnswer: (String) -> Unit,
    onGenerateReport: () -> Unit
) {
    var textState by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    
    // Auto-scroll to bottom of conversation
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "السابق",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text = "محادثة الكوتش الذكي",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {}, enabled = false) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Indicator
                val progress = chatAnswersCount.toFloat() / totalQuestions.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "التقدم المنجز المباشر",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$chatAnswersCount من أصل $totalQuestions أسئلة",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isAnalyzingChat) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "جاري تدوير لغة الحديث وقراءة سماتك الخمس...",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "نقوم بمعالجة الحوار عبر ملقن الذكاء الاصطناعي لاستخلاص التفكير والسلوكيات الإيجابية والتوافقية بدقة.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(chatMessages) { msg ->
                        val isAi = msg.sender == ChatSender.AI
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                        ) {
                            Card(
                                shape = RoundedCornerShape(
                                    topStart = 20.dp,
                                    topEnd = 20.dp,
                                    bottomStart = if (isAi) 4.dp else 20.dp,
                                    bottomEnd = if (isAi) 20.dp else 4.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isAi) {
                                        MaterialTheme.colorScheme.surface
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                ),
                                border = if (isAi) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
                                modifier = Modifier.widthIn(max = 295.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (isAi) "الكوتش الذكي" else "أنت",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAi) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = msg.text,
                                        fontSize = 14.sp,
                                        lineHeight = 22.sp,
                                        color = if (isAi) {
                                            MaterialTheme.colorScheme.onSurface
                                        } else {
                                            MaterialTheme.colorScheme.onPrimary
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Threshold notification for early analysis
                if (chatAnswersCount in 8 until totalQuestions) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "لقد أجبت على $chatAnswersCount أسئلة، يمكنك الآن اختيار إنهاء المحادثة وإصدار التقرير مبكراً أو مواصلة الإجابة.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Conditional Finish/Generate Report Button
                if (chatAnswersCount >= 8) {
                    Button(
                        onClick = onGenerateReport,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .testTag("chat_generate_report_button")
                    ) {
                        Text(
                            text = "إصدار التقرير النهائي الكلي للسمات",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Chat Input Field Panel
                if (currentChatIndex < totalQuestions) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = textState,
                                onValueChange = { textState = it },
                                placeholder = { Text("اكتب إجابتك الصريحة هنا...", fontSize = 13.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_field"),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(24.dp),
                                maxLines = 4
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (textState.trim().isNotEmpty()) {
                                        onSendAnswer(textState)
                                        textState = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                    .testTag("chat_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "إرسال الجواب",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// 9. Audio upload Screen (Version 3)
@Composable
fun AudioUploadScreen(
    viewModel: PersonalityViewModel
) {
    val transcribedText by viewModel.audioTranscribedText.collectAsState()
    val isTranscribing by viewModel.isTranscribingAudio.collectAsState()
    val hasConsent by viewModel.audioConfirmConsent.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Text(
            text = "تحليل الاستجابة اللفظية الصوتية (النسخة الثالثة)",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Warning Alert Disclaimer Box
        Card(
            colors = CardDefaults.cardColors(containerColor = ErrorBg),
            border = BorderStroke(1.dp, ErrorBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "تنبيه أخلاقي",
                        tint = ErrorText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تنبيه حاسم للخصوصية والأخلاقيات",
                        color = ErrorText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "تحليل الصوت يعتمد على الكلام الموجود في المقطع، ولا يمثل حكمًا كاملًا على شخصية المستخدم.",
                    color = ErrorText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ملاحظة: هذا الفحص لا يتبنى أي أحكام نفسية بناءً على خامة أو نبرة الصوت المجرّدة، وإنما يقوم على معاني الكلمات والألفاظ المنطوقة والمافوق لغويا والمفرغة علمياً.",
                    color = ErrorText.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isTranscribing) {
            // Processing status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "جاري تصفية الصوت وتفريغه إلى نص مكتوب...",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AnimatedSpeechWaveform()
                }
            }
        } else if (transcribedText.isEmpty()) {
            // Upload / Drag screen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        scope.launch {
                            viewModel.setAudioTranscribing(true)
                            kotlinx.coroutines.delay(2000)
                            viewModel.updateAudioTranscribedText(
                                "أنا من الأشخاص الذين يعشقون التغيير وخوض التجارب الجديدة والابتكار الفكري المتواصل، وأحب تخطيط يومي وأهتم بالتفاصيل لضمان الالتزام والدقة التامة دون قلق أو عشوائية. أعتبر نفسي ودوداً في بناء الألفة وتفهم مشاعر المحيطين لحفظ الود وتجاوز الخلافات، كما أحافظ عادة على سكينة وبرود داخلي ممتاز عند تطور الضغوط أو حدوث المشاكل الطارئة."
                            )
                            viewModel.setAudioTranscribing(false)
                        }
                    }
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "رفع الصوت",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "قرّب الميكروفون واضغط لبدء التسجيل أو لرفع ملف صوتي",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "سيقوم نظامنا بذكاء بتفريغ حديثك صوتاً وصورة إلى نص مكتوب دقيق للمراجعة والتدبير التثقيفي.",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Verification / Confirmation Screen
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "النص الصوتي المفرغ بذكاء (يمكنك تعديل أي كلمة بالأسفل):",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = transcribedText,
                    onValueChange = { viewModel.updateAudioTranscribedText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("audio_transcription_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = hasConsent,
                        onCheckedChange = { viewModel.setAudioConfirmConsent(it ?: false) },
                        modifier = Modifier.testTag("audio_consent_checkbox")
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "أوافق وأؤكد مطابقة النص المقروء للكلمات المنطوقة.",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.updateAudioTranscribedText("") },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("إعادة تسجيل/رفع")
                    }

                    Button(
                        onClick = { viewModel.generateAudioAnalysis(transcribedText) },
                        enabled = hasConsent && transcribedText.trim().isNotEmpty(),
                        modifier = Modifier.weight(1.5f).height(48.dp).testTag("confirm_audio_button"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("تأكيد المتابعة والتحليل")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        
        // Back Button
        TextButton(
            onClick = { viewModel.selectScreen(AppScreen.METHOD) }
        ) {
            Text("العودة لخيارات التحليل", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}

// 10. Video url Screen (Version 3)
@Composable
fun VideoUrlScreen(
    viewModel: PersonalityViewModel
) {
    val videoUrl by viewModel.videoUrl.collectAsState()
    val isOwn by viewModel.videoIsOwn.collectAsState()
    val hasConsent by viewModel.videoHasConsent.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingVideo.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Title
        Text(
            text = "تحليل السلوك والكلام من فيديو (النسخة الثالثة)",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Disclaimer alert Box
        Card(
            colors = CardDefaults.cardColors(containerColor = ErrorBg),
            border = BorderStroke(1.dp, ErrorBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "تنبيه هام",
                        tint = ErrorText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تنبيه سلوكي وحماية أخلاقية علمية",
                        color = ErrorText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "التحليل من فيديو واحد قد لا يمثل الشخصية كاملة، ويجب اعتباره قراءة احتمالية محدودة.",
                    color = ErrorText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "حظر وتوعية: يمنع منعا باتاً تتبع سلوكيات الأشخاص الآخرين من مقاطع الفيديو دون تفويض صريح وموافقة تامة واضحة.",
                    color = ErrorText.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isAnalyzing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "جاري تتبع ملامح السلوك الخارجي وتحليل البناء اللفظي التفاعلي...",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "أدخل رابط مقطع الفيديو العام (YouTube, Drive, etc...):",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = videoUrl,
                    onValueChange = { viewModel.updateVideoUrl(it) },
                    placeholder = { Text("مثال: https://www.youtube.com/watch?v=...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("video_url_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ملكية المقطع والتصريح الدولي الخاص به:",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Ownership Select Row
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.setVideoIsOwn(true) }
                        ) {
                            RadioButton(
                                selected = isOwn,
                                onClick = { viewModel.setVideoIsOwn(true) },
                                modifier = Modifier.testTag("radio_own_video")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "هذا الفيديو يخصني أنا شخصياً.", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.setVideoIsOwn(false) }
                        ) {
                            RadioButton(
                                selected = !isOwn,
                                onClick = { viewModel.setVideoIsOwn(false) },
                                modifier = Modifier.testTag("radio_other_video")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "هذا الفيديو يخص شخصاً آخر.", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                        }

                        if (!isOwn) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = hasConsent,
                                    onCheckedChange = { viewModel.setVideoHasConsent(it ?: false) },
                                    modifier = Modifier.testTag("consent_checkbox")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "أصرح بموجبه وأؤكد حصولي على موافقة تامة وواضحة من صاحب الفيديو لإجراء التحليل والاطلاع.",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (!hasConsent) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "لا يمكن تحليل شخصية شخص آخر دون موافقته الواضحة",
                                    color = Color(0xFFF87171),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        val consentApproved = !isOwn && hasConsent
                        viewModel.generateVideoAnalysis(videoUrl, consentApproved)
                    },
                    enabled = videoUrl.trim().isNotEmpty() && (isOwn || hasConsent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("run_video_analysis_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("بدء تحليل الفيديو السلوكي", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Back Button
        TextButton(
            onClick = { viewModel.selectScreen(AppScreen.METHOD) }
        ) {
            Text("العودة لخيارات التحليل", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        }
    }
}

