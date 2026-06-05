package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color definitions for a peaceful, soothing UI
val SoftBlue = Color(0xFF3B82F6) // Soft, reassuring blue
val SoftBlueBg = Color(0xFFEFF6FF)
val SoftGreen = Color(0xFF10B981) // Reassuring green
val SoftGreenBg = Color(0xFFECFDF5)
val LightGray = Color(0xFFF3F4F6)
val TextGray = Color(0xFF4B5563)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: PersonalityViewModel) {
    val currentPlan by viewModel.currentUserPlan.collectAsState()
    val pastReports by viewModel.pastReports.collectAsState()
    val userPoints by viewModel.userPoints.collectAsState()
    val reportStyle by viewModel.reportStyle.collectAsState()

    var showPointsCard by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("commercial_dashboard_container")
    ) {
        // Dashboard Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftBlueBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "النقاط",
                        tint = SoftBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "رصيد النقاط: $userPoints نقطة",
                        color = SoftBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(
                onClick = { viewModel.selectScreen(AppScreen.START) },
                modifier = Modifier.testTag("dashboard_back_to_start")
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "الصفحة الرئيسية",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = "لوحة التحكم الذكية",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Right
        )

        Text(
            text = "مرحباً بك في لوحة تحكم النسخة التجارية. هنا تستطيع إدارة اشتراكاتك والاطلاع على التحليلات السابقة والتحكم بإمكانيات التطبيق بسلاسة في واجهة عربية دافئة.",
            fontSize = 14.sp,
            color = TextGray,
            lineHeight = 22.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subscription Plans (نظام الباقات)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "نظام الباقات المعتمد",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
                Text(
                    text = "اختر باقة لتجربة قيودها ومميزاتها التنموية على الفور:",
                    fontSize = 13.sp,
                    color = TextGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    textAlign = TextAlign.Right
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Free Plan
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setUserPlan(UserPlan.FREE) }
                            .testTag("plan_free_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentPlan == UserPlan.FREE) SoftBlueBg else LightGray
                        ),
                        border = if (currentPlan == UserPlan.FREE) BorderStroke(1.5.dp, SoftBlue) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("المجانية", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("٥ أسئلة قصير", fontSize = 11.sp, textAlign = TextAlign.Center, color = TextGray)
                            Text("محدودة", fontSize = 11.sp, textAlign = TextAlign.Center, color = TextGray)
                        }
                    }

                    // Silver Plan
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setUserPlan(UserPlan.SILVER) }
                            .testTag("plan_silver_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentPlan == UserPlan.SILVER) SoftBlueBg else LightGray
                        ),
                        border = if (currentPlan == UserPlan.SILVER) BorderStroke(1.5.dp, SoftBlue) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("الفضية", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("محادثة كاملة", fontSize = 11.sp, textAlign = TextAlign.Center, color = TextGray)
                            Text("حفظ محدود (٣)", fontSize = 11.sp, textAlign = TextAlign.Center, color = TextGray)
                        }
                    }

                    // Gold Plan
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setUserPlan(UserPlan.GOLD) }
                            .testTag("plan_gold_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentPlan == UserPlan.GOLD) SoftGreenBg else LightGray
                        ),
                        border = if (currentPlan == UserPlan.GOLD) BorderStroke(1.5.dp, SoftGreen) else null
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("الذهبية ⭐", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SoftGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("صوت وفيديو", fontSize = 11.sp, textAlign = TextAlign.Center, color = TextGray)
                            Text("خطة تنموية ٣٠ م", fontSize = 11.sp, textAlign = TextAlign.Center, color = TextGray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Points rewards simulation action button
                Button(
                    onClick = {
                        viewModel.addPoints(50)
                        showPointsCard = true
                    },
                    modifier = Modifier.fillMaxWidth().testTag("simulate_points_reward"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("محاكاة ربح نقاط ترحيبية (+٥٠ نقطة)")
                }
            }
        }

        // Quick analysis start (إنشاء تحليل جديد)
        Text(
            text = "إنشاء تحليل جديد",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Right
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    viewModel.setSelectedMethod("qa")
                    viewModel.selectScreen(AppScreen.FORMAT)
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("dashboard_new_qa_analysis"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("اختبار ذكي", fontSize = 13.sp)
                }
            }

            Button(
                onClick = {
                    if (currentPlan == UserPlan.FREE) {
                        viewModel.setBlockedReason("free_plan_blocked")
                        viewModel.selectScreen(AppScreen.ERROR_BLOCKED)
                    } else {
                        viewModel.setSelectedMethod("chat")
                        viewModel.selectScreen(AppScreen.CHAT_QUIZ)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("dashboard_new_chat_analysis"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("محادثة حرة", fontSize = 13.sp)
                }
            }

            Button(
                onClick = {
                    if (currentPlan == UserPlan.GOLD) {
                        viewModel.setSelectedMethod("audio")
                        viewModel.selectScreen(AppScreen.AUDIO_UPLOAD)
                    } else {
                        viewModel.setBlockedReason("gold_required_blocked")
                        viewModel.selectScreen(AppScreen.ERROR_BLOCKED)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("dashboard_new_audio_analysis"),
                colors = ButtonDefaults.buttonColors(containerColor = if (currentPlan == UserPlan.GOLD) SoftGreen else Color.Gray)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("تحليل صوتي", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // History list of past reports
        Text(
            text = "تقاريري السابقة",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Right
        )

        if (pastReports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightGray)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد تقارير محفوظة حالياً. باقات الفضية والذهبية تمكنك من حفظ التقارير تلقائياً فور صدورها.",
                    fontSize = 13.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            pastReports.forEach { savedReport ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("past_report_item_${savedReport.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(SoftBlueBg)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "موثوقية ${savedReport.confidence}",
                                    color = SoftBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "التاريخ: ${savedReport.date}",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = savedReport.report.report_title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right
                        )

                        Text(
                            text = "نوع التحليل: ${savedReport.type}",
                            fontSize = 12.sp,
                            color = TextGray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    viewModel.deleteReportFromHistory(savedReport.id)
                                },
                                modifier = Modifier.testTag("delete_report_btn_${savedReport.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2), contentColor = Color(0xFFEF4444)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف التقرير", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("حذف", fontSize = 12.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (currentPlan == UserPlan.GOLD) {
                                    Button(
                                        onClick = {
                                            viewModel.generatePersonalDevPlan(savedReport.report)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SoftGreen, contentColor = Color.White),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("الخطة الخمسية", fontSize = 12.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        viewModel.generatePersonalDevPlan(savedReport.report) // open it dynamically or generate development plan
                                        // or just open report screen
                                        // let's restore this report as the active generatedReport
                                        // so that the REPORT screen opens it
                                        // first cast list or search:
                                    },
                                    modifier = Modifier.testTag("open_report_btn_${savedReport.id}"),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("فتح التقرير", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Report Comparison button (مقارنة التقارير)
            if (pastReports.size >= 2 && currentPlan == UserPlan.GOLD) {
                Button(
                    onClick = { viewModel.selectScreen(AppScreen.PLAN_COMPARISON) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("compare_reports_dashboard_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftBlue)
                ) {
                    Text("مقارنة بين التقارير السابقة (ميزة ذهبية ⭐)")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Report Style (اختيار نمط التقرير)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "تفضيلات نمط التقرير الكرنفالي",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
                Text(
                    text = "يتغير التقرير تلقائياً تماشياً مع نمطك المختار:",
                    fontSize = 12.sp,
                    color = TextGray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Detailed Style
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.updateReportStyle("مفصل") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (reportStyle == "مفصل") SoftBlueBg else LightGray
                        )
                    ) {
                        Text(
                            text = "مفصل وعلمي",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Compact Style
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.updateReportStyle("مختصر") },
                        colors = CardDefaults.cardColors(
                            containerColor = if (reportStyle == "مختصر") SoftBlueBg else LightGray
                        )
                    ) {
                        Text(
                            text = "مختصر وعملي",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Settings (إعدادات الخصوصية داخل لوحة التحكم)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            val encryptLocally by viewModel.privacyEncrypt.collectAsState()
            val allowBehavioral by viewModel.privacyBehavioral.collectAsState()
            val clearOnExit by viewModel.privacyClearOnExit.collectAsState()

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "إعدادات الخصوصية والتحكم بالبيانات",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = encryptLocally,
                        onCheckedChange = { viewModel.setPrivacyEncrypt(it) }
                    )
                    Text(
                        text = "تشفير بيانات اختبار الـ Big Five محلياً",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = allowBehavioral,
                        onCheckedChange = { viewModel.setPrivacyBehavioral(it) }
                    )
                    Text(
                        text = "السماح بالتحليل اللغوي الوجداني (السمة الصوتية)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = clearOnExit,
                        onCheckedChange = { viewModel.setPrivacyClearOnExit(it) }
                    )
                    Text(
                        text = "طمس ومسح كافة التقارير عند إغلاق التطبيق",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Policy Links Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = { viewModel.selectScreen(AppScreen.TERMS) },
                modifier = Modifier.testTag("dashboard_terms_btn")
            ) {
                Text("شروط الاستخدام ⚖️", color = SoftBlue, fontSize = 14.sp)
            }

            TextButton(
                onClick = { viewModel.selectScreen(AppScreen.PRIVACY) },
                modifier = Modifier.testTag("dashboard_privacy_btn")
            ) {
                Text("سياسة الخصوصية 🔒", color = SoftBlue, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun TermsScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("terms_screen_layout"),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("")
            IconButton(onClick = onBack, modifier = Modifier.testTag("terms_back_btn")) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "اغلاق")
            }
        }

        Text(
            text = "شروط الاستخدام والخدمة",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "يرجى قراءة بنود الاستخدام القانونية بعناية لتفهم حدود الخدمة قبل بدء قياس السمات:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )

                Spacer(modifier = Modifier.height(12.dp))

                val termsPoints = listOf(
                    "تطبيقنا تعليمي وتثقيفي سلوكي بحت، ويسعى لمساعدة الأفراد على فهم سماتهم الوجدانية الكبرى المعتمدة أكاديمياً.",
                    "التحليل الذي يوفره التطبيق هو تحليل احتمالي فقط وليس حكماً قطعياً، ويعتمد بصورة حصرية على دقة ومصداقية الإدخالات التي تقدمها.",
                    "هذا التطبيق ومخرجاته ليست بأي حال من الأحوال أداة تشخيص نفسي، طبي، أو إكلينيكي، ولا يغني عن مراجعة طبيب مرخص.",
                    "يُمنع منعاً باتاً استخدام التطبيق لغرض الحكم السلبي على الآخرين، أو تصنيفهم عاطفياً، أو توظيف هذه المخرجات في تقييم الموظفين مهنياً أو استقصاء قدرات الأفراد دون موافقتهم المعلنة صراحة."
                )

                termsPoints.forEachIndexed { idx, pt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = pt,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Right
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "•",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftBlue
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("أوافق على الشروط والأحكام")
        }
    }
}

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("privacy_screen_layout"),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("")
            IconButton(onClick = onBack, modifier = Modifier.testTag("privacy_back_btn")) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "اغلاق")
            }
        }

        Text(
            text = "سياسة السرية والخصوصية",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "خصوصية بياناتك وسرية إجاباتك هي أساس عمل تطبيق تحليل الشخصية الذكي:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )

                Spacer(modifier = Modifier.height(12.dp))

                val privacyItems = listOf(
                    "كيفية معالجة الإجابات: تُستخدم الإجابات الوجدانية واللغوية بخصوصية تامة لمعالجة وقياس سمات العوامل الخمسة الكبرى محلياً أو عبر خوادم آمنة لتوليد التقارير.",
                    "سرية النتائج التامة: نلتزم التزاماً أخلاقياً وقانونياً صارماً بعدم مشاركة نتائجك أو تقاريرك الخاصة مع أي طرف خارجي على الإطلاق دون موافقتك الصريحة المنطوقة.",
                    "ملكية البيانات الكاملة: يمتلك المستخدم الحق الكامل والأسهل لمسح أي تقرير سلوكي من التاريخ، أو طمس كافة سجلاته فور تفعيل خيار المسح السريع في لوحة التحكم."
                )

                privacyItems.forEach { pt ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = pt,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Right
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "✓",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SoftGreen)
        ) {
            Text("حماية بياناتي آمنة")
        }
    }
}

@Composable
fun DevelopmentPlanScreen(viewModel: PersonalityViewModel) {
    val devPlan by viewModel.currentDevPlan.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("personal_development_plan_container"),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.selectScreen(AppScreen.DASHBOARD) },
                colors = ButtonDefaults.buttonColors(containerColor = LightGray, contentColor = TextGray)
            ) {
                Text("لوحة التحكم")
            }

            Text(
                text = "خطة التنمية الشخصية",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (devPlan == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("لم يتم العثور على خطة علاجية مخصصة. يرجى المضي بالتحليل واستخلاص النتائج أولاً.")
            }
        } else {
            val plan = devPlan!!

            Card(
                colors = CardDefaults.cardColors(containerColor = SoftGreenBg),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SoftGreen.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = plan.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SoftGreen)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "هذه الخطة مخصصة لمساعدتك في صقل سماتك الشخصية بروية وعفوية عبر سلوكيات مبسطة وعملية خفيفة، ولا تمثل بأي حال من الأحوال علاجاً علاجياً أو تشخيصاً نفسياً.",
                        fontSize = 12.sp,
                        color = TextGray,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Habit (عادة يومية)
            DevelopmentCard(title = "عادة يومية بسيطة ☀️", body = plan.dailyHabit, icon = Icons.Default.Star)
            // Cognitive (تمرين تفكير)
            DevelopmentCard(title = "تمرين تفكير مقتضب 💭", body = plan.cognitiveExercise, icon = Icons.Default.Edit)
            // Tip (نصيحة عملية)
            DevelopmentCard(title = "نصيحة عملية ميسرة 💡", body = plan.practicalTip, icon = Icons.Default.Check)
            // Goal (هدف أسبوعي)
            DevelopmentCard(title = "هدف الأسبوع الموجه 🎯", body = plan.weeklyGoal, icon = Icons.Default.Info)
            // Review (مراجعة نهاية الأسبوع)
            DevelopmentCard(title = "تأمل ومراجعة نهاية الأسبوع 📝", body = plan.endOfWeekReview, icon = Icons.Default.Refresh)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "تقويم التقدم اليومي لـ ٣٠ يوماً (خفيف)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )

            plan.dayPlanItems.take(5).forEachIndexed { idx, dayValue ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("اليوم ${idx + 1}: مارس السلوك الإيجابي بهدوء ويسر بدون ضغوط.", fontSize = 13.sp, textAlign = TextAlign.Right)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = SoftGreen, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DevelopmentCard(title: String, body: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = body, fontSize = 13.sp, color = TextGray, textAlign = TextAlign.Right, modifier = Modifier.fillMaxWidth())
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SoftBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = SoftBlue, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun PlanComparisonScreen(viewModel: PersonalityViewModel) {
    val pastReports by viewModel.pastReports.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("plan_comparison_layout"),
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.selectScreen(AppScreen.DASHBOARD) },
                colors = ButtonDefaults.buttonColors(containerColor = LightGray, contentColor = TextGray)
            ) {
                Text("تراجع")
            }

            Text(
                text = "مقارنة التقارير الفنية",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "تتيح لك الميزة الذهبية مقارنة أبعادك الخمسة الكبرى بين مختلف القياسات السابقة لرؤية التذبذب والتقدم الطبيعي لسماتك الشخصية بفحص دقيق ومقروء.",
            fontSize = 13.sp,
            color = TextGray,
            textAlign = TextAlign.Right,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pastReports.size < 2) {
            Text("الرجاء توفير تقريرين على الأقل لإجراء المقارنة الشاملة.", color = Color.Red)
        } else {
            val rep1 = pastReports[0].report
            val rep2 = pastReports[1].report

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("جدول مقارنة الأبعاد الخمسة الكبرى:", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                    Spacer(modifier = Modifier.height(12.dp))

                    val traitKeys = listOf("openness", "conscientiousness", "extraversion", "agreeableness", "neuroticism")
                    traitKeys.forEach { key ->
                        val t1 = rep1.traits[key]
                        val t2 = rep2.traits[key]
                        if (t1 != null && t2 != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(text = t1.arabic_name, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right, color = SoftBlue)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("شخصيتك بالفيديو: %${t2.score} (${t2.level})", fontSize = 12.sp, color = TextGray)
                                    Text("شخصيتك بالاختبار: %${t1.score} (${t1.level})", fontSize = 12.sp, color = TextGray)
                                }
                                LinearProgressIndicator(
                                    progress = t1.score.toFloat() / 100f,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = SoftBlue,
                                    trackColor = LightGray
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
