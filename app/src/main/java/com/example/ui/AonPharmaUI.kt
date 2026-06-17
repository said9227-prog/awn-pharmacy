package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.MedicationData
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AonPharmaMainApp(viewModel: AonPharmaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
            when (screen) {
                "welcome_check" -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                "registration" -> {
                    RegistrationScreen(viewModel)
                }
                "main" -> {
                    MainDashboardScreen(viewModel)
                }
            }
        }
    }
}

// --- REGISTRATION SCREEN ---
@Composable
fun RegistrationScreen(viewModel: AonPharmaViewModel) {
    val nameInput by viewModel.regNameInput.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    
    // Choose a random motivational quote on load
    val quoteIndex = remember { (0..4).random() }
    val quoteAr = viewModel.medicalQuotesAr[quoteIndex]
    val quoteEn = viewModel.medicalQuotesEn[quoteIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MedicalTealLight.copy(alpha = 0.5f), CreamWhite)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Spacer(modifier = Modifier.height(30.dp))
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.aon_pharma_logo),
                contentDescription = "Aon Pharma Emblem",
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(2.dp, MedicalTealPrimary, RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "عَوْن فَرْمَا",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MedicalTealDark
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Aon Pharma",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MedicalTealPrimary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Beautiful Bilingual Inspirational Pharmacy Quote Card (Clean Minimalism styled with thin mint border)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MedicalTealLight),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Medical",
                        tint = RedAlert,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "« $quoteAr »",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MedicalTealDark,
                            lineHeight = 24.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "“ $quoteEn ”",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = SlateText.copy(alpha = 0.7f),
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enter Username input card (Clean Minimalism styled with thin mint border)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MedicalTealLight),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Text(
                        text = "لتشغيل الصيدلية الذكية، ما اسمك الكريم؟",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MedicalTealDark
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = "Please enter your name to register:",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateText.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Left
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { viewModel.regNameInput.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        placeholder = { 
                            Text(
                                "مثلاً: أحمد، علي، سارة...",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Right
                            ) 
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.registerUser() },
                        enabled = nameInput.trim().isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                    ) {
                        Text(
                            text = "ابدأ التعلم والاستشارة الطبية 🚀",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = "Online status",
                    tint = if (isOnline) MedicalTealPrimary else RedAlert,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOnline) "متصل بالإنترنت (الذكاء الاصطناعي نشط)" else "أوفلاين (الوصول محلي فقط)",
                    fontSize = 12.sp,
                    color = if (isOnline) MedicalTealPrimary else RedAlert
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- MAIN DASHBOARD SCREEN (Multi-Tab Nav) ---
@Composable
fun MainDashboardScreen(viewModel: AonPharmaViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = PureWhite,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("صيدليتي الذكية", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MedicalTealPrimary,
                        selectedTextColor = MedicalTealPrimary,
                        indicatorColor = MedicalTealLight
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(imageVector = Icons.Default.MailOutline, contentDescription = "Chat") },
                    label = { Text("الاستشارة الحية", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MedicalTealPrimary,
                        selectedTextColor = MedicalTealPrimary,
                        indicatorColor = MedicalTealLight
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(imageVector = Icons.Default.Star, contentDescription = "Quiz") },
                    label = { Text("منافسة الزملاء", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MedicalTealPrimary,
                        selectedTextColor = MedicalTealPrimary,
                        indicatorColor = MedicalTealLight
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> SearchTab(viewModel)
                1 -> ChatTab(viewModel)
                2 -> CompetitionTab(viewModel)
            }
        }
    }
}

// --- TAB 1: SEARCH & DRUG CARDS ---
@Composable
fun SearchTab(viewModel: AonPharmaViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val searchedMedication by viewModel.searchedMedication.collectAsStateWithLifecycle()
    val searchError by viewModel.searchError.collectAsStateWithLifecycle()
    val scores by viewModel.competitionScores.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamWhite)
            .padding(16.dp)
    ) {
        // Dynamic Competition Banner (Clean Minimalism style: white background with thin borders and vertical dividers)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("competition_tracker_card"),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MedicalTealLight),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${userProfile?.name ?: "أنت"} (أنت)",
                            color = SlateText.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${scores?.myScore ?: 0}",
                            color = MedicalTealPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp)
                            .background(MedicalTealLight)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "المنافسة الحالية",
                            color = SlateText.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${userProfile?.name ?: "أحمد"} ⚔️ ${scores?.friendName ?: "علي"}",
                            color = SlateText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp)
                            .background(MedicalTealLight)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${scores?.friendName ?: "الصديق"} (الزميل)",
                            color = SlateText.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${scores?.friendScore ?: 0}",
                            color = OrangeAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Branding and Title Header in Arabic (صيدلية العوني الذكية)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "صَيْدَلِيَّةُ العَوْنِي الذَّكِيَّة",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = MedicalTealDark
                        )
                    )
                    Text(
                        text = "ابحث عن أي تركيب دوائي للحصول على بطاقة الدواء المعتمدة",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateText.copy(alpha = 0.6f)
                        )
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.aon_pharma_logo),
                    contentDescription = "Emblem Small",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }

        // Simple Search Box Card (Clean Minimalism styled with thin mint border)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MedicalTealLight),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.performMedicationSearch() },
                        enabled = !isSearching
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search button",
                            tint = MedicalTealPrimary
                        )
                    }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { 
                            Text(
                                "مثلاً: Paracetamol, Amoxicillin...",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Right
                            ) 
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("drug_search_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search", tint = Color.LightGray)
                        }
                    }
                }
            }
        }

        // Loading Progress Indicator resembling a searching magnifying glass lens
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MedicalTealPrimary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "جاري الاتصال بقاعدة بيانات FDA وتوليد بطاقة الدواء...",
                            fontSize = 12.sp,
                            color = MedicalTealPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Error rendering
        if (searchError != null && !isSearching) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = RedAlert.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Error icon", tint = RedAlert)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = searchError!!, color = RedAlert, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Successful Med Card Result
        if (searchedMedication != null && !isSearching) {
            item {
                MedicalDrugCard(viewModel, searchedMedication!!)
            }
        } else if (searchedMedication == null && !isSearching) {
            // Friendly Empty State Placeholder for beautiful initial launch UX as requested in frontend-design skill!
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Search drugs",
                        tint = MedicalTealPrimary.copy(alpha = 0.4f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "جرب البحث عن باراسيتامول لترى قوة الترميز الطبي بـ (عون فرما)!",
                        fontSize = 13.sp,
                        color = SlateText.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// --- MEDICINE CARD LAYOUT (THE MEDI VERIFIED 'DRUG CARD' WITH EXCLUSIVE DESIGN RULES) ---
@Composable
fun MedicalDrugCard(viewModel: AonPharmaViewModel, med: MedicationData) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MedicalTealLight),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Medical Identity and clickable Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.openGoogleTranslate(context, med.nameEn) }
                    ) {
                        Text(
                            text = "(${med.nameEn})",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = MedicalTealPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = med.nameAr,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = MedicalTealDark
                            )
                        )
                    }
                    Text(
                        text = med.drugClassAr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OrangeAccent
                        )
                    )
                    Text(
                        text = "Class: ${med.drugClassEn}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SlateText.copy(alpha = 0.5f)
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Drug info",
                    tint = MedicalTealPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Click Tip for translation
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalTealLight.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "Tip", tint = MedicalTealPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تلميح: اضغط على أي مصطلح طبي بالإنجليزية للانتقال المباشر وترجمته في Google Translate!",
                        fontSize = 11.sp,
                        color = MedicalTealDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // Attribute 1: Mechanism of Action (آلية العمل)
            MedicalCardAttribute(
                titleAr = "1️⃣ آلية العمل",
                titleEn = "Mechanism of Action",
                contentAr = med.mechanismOfActionAr,
                contentEn = med.mechanismOfActionEn,
                onTermClick = { viewModel.openGoogleTranslate(context, med.mechanismOfActionEn) }
            )

            // Attribute 2: Indications / Uses (دواعي الاستعمال)
            MedicalCardAttribute(
                titleAr = "2️⃣ دواعي الاستعمال",
                titleEn = "Indications / Approved Uses",
                contentAr = med.indicationsAr,
                contentEn = med.indicationsEn,
                onTermClick = { viewModel.openGoogleTranslate(context, med.indicationsEn) }
            )

            // Attribute 3: Dosage block for and age groups
            Text(
                text = "3️⃣ الجُرَعَات الطِّبِّيَّة المُنَسَّقَة",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = MedicalTealDark),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "👶 الرُّضَّع (Infants):\n${med.dosageInfantsAr}\n${med.dosageInfantsEn}",
                        fontSize = 12.sp,
                        color = SlateText,
                        lineHeight = 16.sp,
                        modifier = Modifier.clickable { viewModel.openGoogleTranslate(context, med.dosageInfantsEn) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "👦 الأَطْفَال (Children):\n${med.dosageChildrenAr}\n${med.dosageChildrenEn}",
                        fontSize = 12.sp,
                        color = SlateText,
                        lineHeight = 16.sp,
                        modifier = Modifier.clickable { viewModel.openGoogleTranslate(context, med.dosageChildrenEn) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "👨 البَالِغِين (Adults):\n${med.dosageAdultsAr}\n${med.dosageAdultsEn}",
                        fontSize = 12.sp,
                        color = SlateText,
                        lineHeight = 16.sp,
                        modifier = Modifier.clickable { viewModel.openGoogleTranslate(context, med.dosageAdultsEn) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = OrangeAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${med.dosageWarningAr} / ${med.dosageWarningEn}",
                            fontSize = 11.sp,
                            color = OrangeAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Attribute 4: Maximum Dose & Toxicity
            MedicalCardAttribute(
                titleAr = "4️⃣ الجرعة القصوى وأعراض التسمم",
                titleEn = "Maximum Dose & Toxicity",
                contentAr = med.maxDoseToxicityAr,
                contentEn = med.maxDoseToxicityEn,
                colorTheme = RedAlert,
                onTermClick = { viewModel.openGoogleTranslate(context, med.maxDoseToxicityEn) }
            )

            // Attribute 5: Contraindications
            MedicalCardAttribute(
                titleAr = "5️⃣ مَوَانِعُ الِاسْتِعْمَالِ الشَّامِلَةِ",
                titleEn = "Contraindications",
                contentAr = med.contraindicationsAr,
                contentEn = med.contraindicationsEn,
                colorTheme = RedAlert,
                onTermClick = { viewModel.openGoogleTranslate(context, med.contraindicationsEn) }
            )

            // Attribute 6: Antidote
            MedicalCardAttribute(
                titleAr = "6️⃣ المُنْتَدَب / التِّرْيَاق (Antidote)",
                titleEn = "Antidote",
                contentAr = med.antidoteAr,
                contentEn = med.antidoteEn,
                colorTheme = MedicalTealPrimary,
                onTermClick = { viewModel.openGoogleTranslate(context, med.antidoteEn) }
            )

            // Attribute 7: Drug Interactions
            MedicalCardAttribute(
                titleAr = "7️⃣ التداخلات الدوائية",
                titleEn = "Drug Interactions (Major, Moderate, Minor)",
                contentAr = med.interactionsAr,
                contentEn = med.interactionsEn,
                colorTheme = OrangeAccent,
                onTermClick = { viewModel.openGoogleTranslate(context, med.interactionsEn) }
            )

            // Attribute 8: Receptors
            MedicalCardAttribute(
                titleAr = "8️⃣ المُسْتَقْبِلَات العِلَاجِيَّة الهَدَفِيَّة",
                titleEn = "Receptors & Target Site",
                contentAr = med.receptorsAr,
                contentEn = med.receptorsEn,
                onTermClick = { viewModel.openGoogleTranslate(context, med.receptorsEn) }
            )

            // Attribute 9: Side Effects
            Text(
                text = "9️⃣ الأَعْرَاضُ الجَانِبِيَّةُ (Side Effects)",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black, color = MedicalTealDark),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🔸 شائعة (Common):\n${med.sideEffectsCommonAr}\n${med.sideEffectsCommonEn}",
                        fontSize = 12.sp,
                        color = SlateText,
                        lineHeight = 16.sp,
                        modifier = Modifier.clickable { viewModel.openGoogleTranslate(context, med.sideEffectsCommonEn) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🚨 خطيرة (Serious):\n${med.sideEffectsSeriousAr}\n${med.sideEffectsSeriousEn}",
                        fontSize = 12.sp,
                        color = RedAlert,
                        lineHeight = 16.sp,
                        modifier = Modifier.clickable { viewModel.openGoogleTranslate(context, med.sideEffectsSeriousEn) }
                    )
                }
            }

            // Attribute 10: Special Warnings
            MedicalCardAttribute(
                titleAr = "🔟 تحذيرات سريرية خاصة",
                titleEn = "Special Warnings & Impairment Adjustments",
                contentAr = med.specialWarningsAr,
                contentEn = med.specialWarningsEn,
                colorTheme = OrangeAccent,
                onTermClick = { viewModel.openGoogleTranslate(context, med.specialWarningsEn) }
            )

            // Attribute 11: ATD / Allergy Testing (If non-trivial, render, else skip)
            if (med.allergyTestingEn.isNotBlank() && med.allergyTestingEn != "Not required") {
                MedicalCardAttribute(
                    titleAr = "1️⃣1️⃣ اختبار التحسس الدوائي",
                    titleEn = "Allergy Testing (ATD) Protocol",
                    contentAr = med.allergyTestingAr,
                    contentEn = med.allergyTestingEn,
                    colorTheme = MedicalTealPrimary,
                    onTermClick = { viewModel.openGoogleTranslate(context, med.allergyTestingEn) }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color.LightGray.copy(alpha = 0.5f))

            // Attribute 12: Sources used
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "المصادر المرجعية الموثوقة للفحص والترميز:",
                    fontSize = 10.sp,
                    color = SlateText.copy(alpha = 0.6f)
                )
                Text(
                    text = med.sourcesUsedEn,
                    fontSize = 10.sp,
                    color = MedicalTealPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Reusable single attribute renderer for our medication cards
@Composable
fun MedicalCardAttribute(
    titleAr: String,
    titleEn: String,
    contentAr: String,
    contentEn: String,
    colorTheme: Color = MedicalTealDark,
    onTermClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = titleAr,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Black,
                color = colorTheme
            )
        )
        Text(
            text = "($titleEn)",
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = SlateText.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = contentAr,
            fontSize = 13.sp,
            color = SlateText,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right
        )
        Text(
            text = contentEn,
            fontSize = 12.sp,
            color = SlateText.copy(alpha = 0.7f),
            lineHeight = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onTermClick() },
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

// --- TAB 2: ADVANCED CHAT SCREEN ---
@Composable
fun ChatTab(viewModel: AonPharmaViewModel) {
    val chatInput by viewModel.chatInput.collectAsStateWithLifecycle()
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamWhite)
    ) {
        // Chat Header (Clean Minimalism style with thin border)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
            border = BorderStroke(1.dp, MedicalTealLight),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { viewModel.clearChat() }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear chat", tint = RedAlert)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "الدردشة الصيدلانية المتقدمة",
                        fontWeight = FontWeight.Black,
                        color = MedicalTealDark,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "صيدلية عون فرما الذكية مع ${userProfile?.name ?: "مستخدم عون"}",
                        fontSize = 11.sp,
                        color = MedicalTealPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = "Chat icon",
                    tint = MedicalTealPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Chat conversation bubble list
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (chatHistory.isEmpty()) {
                // Empty Chat Greeting Customized with User Name & Encouragements
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.aon_pharma_logo),
                        contentDescription = "Emblem",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "أهلاً بك يا صيدلاني المستقبل: ( ${userProfile?.name ?: "أحمد"} ) 🌿",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalTealDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "أنا العميل الذكي لـ (Aon Pharma) عَوْن فَرْمَا.\nاطرح غمر أسئلتك بخصوص عائلات الأدوية، تداخلاتها الدوائية (Drug interactions)، أو الجرعات الدقيقة.\nسأجيبك بنصوص دقيقة مع إدراج المصطلحات الطبية كاملة بالإنجليزية متبوعة بنطقها العربي وتشكيله!",
                        fontSize = 13.sp,
                        color = SlateText.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    reverseLayout = false
                ) {
                    items(chatHistory) { message ->
                        ChatBubble(viewModel, message)
                    }
                    if (isChatLoading) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MedicalTealPrimary)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("جاري البحث في PubMed و FDA والمزامنة...", fontSize = 11.sp, color = MedicalTealDark)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Under Chat input card (Clean Minimalism style with thin border)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            border = BorderStroke(1.dp, MedicalTealLight),
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.sendChatMessage() },
                    modifier = Modifier
                        .background(MedicalTealPrimary, CircleShape)
                        .size(46.dp)
                        .testTag("send_chat_button"),
                    enabled = chatInput.isNotBlank() && !isChatLoading
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(10.dp))

                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { viewModel.updateChatInput(it) },
                    placeholder = { 
                        Text(
                            "اكتب سؤالك الدوائي هنا...",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right
                        ) 
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedicalTealPrimary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
        }
    }
}

@Composable
fun ChatBubble(viewModel: AonPharmaViewModel, message: Pair<String, String>) {
    val isUser = message.first == "user"
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (isUser) 14.dp else 2.dp,
                    bottomEnd = if (isUser) 2.dp else 14.dp
                ),
                border = if (isUser) null else BorderStroke(1.dp, MedicalTealLight),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) MedicalTealPrimary else PureWhite
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.second,
                        color = if (isUser) PureWhite else SlateText,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = if (isUser) TextAlign.Right else TextAlign.Left
                    )
                }
            }

            // Exclusive instruction: Share button for spreading pharmacy wisdom!
            // "يظهر زر يعطي نصيحة للمستخدم بإعطاء هذه الأسئلة لصديقك الصيدلاني من أجل الفائدة ... انشر العلم لصديق لتعم الفائدة واختبر معلوماته"
            if (!isUser) {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { viewModel.shareAdviceFromChat(context, message.second) },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealLight),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MedicalTealDark, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "انشر العلم لصديق لتعم الفائدة واختبر معلوماته 📬",
                            fontSize = 10.sp,
                            color = MedicalTealDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// --- TAB 3: FRIEND COMPETITIONS & QUIZZES ---
@Composable
fun CompetitionTab(viewModel: AonPharmaViewModel) {
    val questions by viewModel.questionsList.collectAsStateWithLifecycle()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
    val userAnswer by viewModel.userAnswerInput.collectAsStateWithLifecycle()
    val friendAnswer by viewModel.friendAnswerInput.collectAsStateWithLifecycle()
    val friendName by viewModel.friendNameInput.collectAsStateWithLifecycle()
    val showHintForUser by viewModel.showHintForUser.collectAsStateWithLifecycle()
    val showHintForFriend by viewModel.showHintForFriend.collectAsStateWithLifecycle()

    val isChecking by viewModel.isCheckingAnswer.collectAsStateWithLifecycle()
    val checkingProgress by viewModel.checkingProgress.collectAsStateWithLifecycle()
    val evaluationResult by viewModel.evaluationResult.collectAsStateWithLifecycle()
    val isSmsGranted by viewModel.isSmsPermissionGranted.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamWhite)
            .padding(16.dp)
    ) {
        // Screen Title Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalTealPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆 مِصْطَبَةُ التَّنَافُسِ الصَّيْدَلَانِيِّ 🏆",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = PureWhite
                    )
                    Text(
                        text = "التحدي المشترك مع زميل الدراسة لترسيخ أسماء المركبات والمصطلحات الطبية",
                        fontSize = 11.sp,
                        color = MedicalTealLight,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Friend Setup Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "اسم زميل التنافس الحالي:",
                        fontWeight = FontWeight.Bold,
                        color = MedicalTealDark,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = friendName,
                        onValueChange = { viewModel.updateFriendNameInput(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("friend_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // SMS Permission Status block as requested
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isSmsGranted) MedicalTealLight else RedAlert.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSmsGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = "SMS state",
                            tint = if (isSmsGranted) MedicalTealPrimary else RedAlert,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isSmsGranted) "تم تفعيل صلاحية الرسائل SMS" else "صلاحية الـ SMS لم تُفعل بنجاح",
                                fontWeight = FontWeight.Bold,
                                color = if (isSmsGranted) MedicalTealDark else RedAlert,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "ملاحظة: تفعيل الصلاحية إلزامي لتسجيل نتيجة صديقك على لوحة النتائج وإرسال الردود التلقائية له لمصداقية التنافس!",
                                fontSize = 9.sp,
                                color = SlateText.copy(alpha = 0.7f),
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }

        if (questions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MedicalTealPrimary)
                }
            }
        } else {
            val q = questions.getOrNull(currentQuestionIndex)
            if (q != null) {
                // Quiz display card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header with level tag
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (q.difficulty == "سهل") MedicalTealLight else RedAlert.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "المستوى: ${q.difficulty}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (q.difficulty == "سهل") MedicalTealDark else RedAlert,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "السؤال ${currentQuestionIndex + 1} من ${questions.size}",
                                    fontSize = 11.sp,
                                    color = SlateText.copy(alpha = 0.5f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Question Title
                            Text(
                                text = q.questionText,
                                fontSize = 15.sp,
                                color = MedicalTealDark,
                                fontWeight = FontWeight.Black,
                                lineHeight = 22.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openGoogleTranslate(context, q.questionText) },
                                textAlign = TextAlign.Right
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action share SMS to friend
                            Button(
                                onClick = { viewModel.shareQuestionWithFriend(context) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "SMS", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "أرسل هذا التحدي لصديقك بالرسائل SMS لتعم المنفعة ✉️",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Two interactive inputs: My Answer, and Friend Answer
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // User input
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "✍️ إجابة المُستخدِم الحالية ( ${userProfile?.name ?: "أنت"} ):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalTealPrimary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = userAnswer,
                                    onValueChange = { viewModel.updateUserAnswerInput(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("user_input_answer"),
                                    placeholder = { Text("اكتب إجابتك هنا بتركيز وتدقيق...", fontSize = 12.sp) }
                                )
                                
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                // Hint for User
                                TextButton(
                                    onClick = { viewModel.toggleHintUser() },
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Text(
                                        text = if (showHintForUser) "اخفاء التلميح 💡" else "💡 اضغط لتلميح للمستخدم",
                                        fontSize = 11.sp,
                                        color = MedicalTealPrimary
                                    )
                                }
                                if (showHintForUser) {
                                    Text(
                                        text = "شرح تلميحي مقتضب: ${q.hintForUser}",
                                        fontSize = 11.sp,
                                        color = OrangeAccent,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        // Friend input (Binds strictly to checking SMS permission state)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "✍️ خانة إجابة صديقي العزيز ( $friendName ):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalTealDark,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                OutlinedTextField(
                                    value = friendAnswer,
                                    onValueChange = { viewModel.updateFriendAnswerInput(it) },
                                    // Strictly disable or request SMS first to ensure maximum competitive fidelity as requested!
                                    enabled = isSmsGranted,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("friend_input_answer"),
                                    placeholder = { 
                                        Text(
                                            if (isSmsGranted) "اكتب إجابة زميلك هنا..." else "يرجى منح صلاحية الـ SMS أولاً حتى نتمكن من المزامنة بحرية!",
                                            fontSize = 12.sp
                                        ) 
                                    }
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Hint for Friend
                                TextButton(
                                    onClick = { viewModel.toggleHintFriend() },
                                    enabled = isSmsGranted,
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Text(
                                        text = if (showHintForFriend) "اخفاء التلميح 💡" else "💡 اضغط لتلميح للصديق",
                                        fontSize = 11.sp,
                                        color = MedicalTealDark
                                    )
                                }
                                if (showHintForFriend) {
                                    Text(
                                        text = "شرح تلميحي مقتضب: ${q.hintForFriend}",
                                        fontSize = 11.sp,
                                        color = OrangeAccent,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Check answers button with counting lens progress animation
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = { viewModel.checkQuizAnswers() },
                                enabled = userAnswer.isNotBlank() && friendAnswer.isNotBlank() && !isChecking && isSmsGranted,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("check_answers_btn"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("🔍 تحقق من الإجابات الصحيحة", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            // Lens styled progress count down from 10% to 100%
                            if (isChecking) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    CircularProgressIndicator(
                                        progress = { checkingProgress / 100f },
                                        modifier = Modifier.size(24.dp),
                                        color = MedicalTealPrimary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "جاري الفحص الدقيق والتقييم بالعدسة الذكية: %$checkingProgress",
                                        fontSize = 12.sp,
                                        color = MedicalTealPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Quiz Evaluation Success/Defeat details
                if (evaluationResult != null && !isChecking) {
                    item {
                        val eval = evaluationResult!!
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(2.dp, if (eval.userIsCorrect) MedicalTealPrimary else RedAlert)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Dynamic result button to send results back directly to friend via SMS!
                                Button(
                                    onClick = { viewModel.shareResultWithFriend(context) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealDark),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Share, contentDescription = "SMS output", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "رد نتيجة السؤال إلى الصديق مباشرة عبر SMS 📬",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // User Result
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "المُستخدِم الحالي ( ${userProfile?.name ?: "أنت"} ) :",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Right
                                    )
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (eval.userIsCorrect) MedicalTealLight else RedAlert.copy(alpha = 0.1f)
                                        ),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (eval.userIsCorrect) "إجابة صحيحة (+10) 🎉" else "إجابة خاطئة ✖️",
                                            color = if (eval.userIsCorrect) MedicalTealDark else RedAlert,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                // Friend Result
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "صديق الدراسة ( $friendName ) :",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Right
                                    )
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (eval.friendIsCorrect) MedicalTealLight else RedAlert.copy(alpha = 0.1f)
                                        ),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (eval.friendIsCorrect) "إجابة صحيحة (+10) 🎉" else "إجابة خاطئة ✖️",
                                            color = if (eval.friendIsCorrect) MedicalTealDark else RedAlert,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                Spacer(modifier = Modifier.height(10.dp))

                                // Detailed Answer
                                Text(
                                    text = "💡 الإجابة الصحيحة المفسّرة:",
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalTealDark,
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right
                                )
                                Text(
                                    text = eval.correctAnswerDetailed,
                                    fontSize = 13.sp,
                                    color = SlateText,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    textAlign = TextAlign.Right
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // AI Explanation
                                Text(
                                    text = "🔬 تحليل الذكاء الاصطناعي الفوري للنتيجة والمغالطات:",
                                    fontWeight = FontWeight.Bold,
                                    color = MedicalTealPrimary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right
                                )
                                Text(
                                    text = eval.explanation,
                                    fontSize = 12.sp,
                                    color = SlateText,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Encouragement warning "العلم يحتاج صبر واجتهاد"
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = YellowWarning.copy(alpha = 0.12f)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Heart", tint = OrangeAccent, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = eval.encouragementMessage,
                                            fontSize = 11.sp,
                                            color = OrangeAccent,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { viewModel.loadNextQuestion() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("التالي: الانتقال لسؤال صيدلاني جديد ⏩", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
