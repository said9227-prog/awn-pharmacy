package com.example.data

object DefaultQuestions {
    val list = listOf(
        PharmacyQuestion(
            questionText = "ما هي الفئة الدوائية الأساسية لعقار (Amoxicillin) أَمُوكْسِيسِيلِين؟",
            correctAnswer = "البنسلينات (Penicillins) / مضادات حيوية بيتا-لاكتام",
            difficulty = "سهل",
            hintForUser = "ينتمي لعائلة البنسلينات الشهيرة بالقضاء على الجدار الخلوي للبكتيريا.",
            hintForFriend = "فئة دوائية تستخدم لعلاج الالتهابات البكتيرية الصدرية والجلدية وتبدأ بحرف الباء.",
            referenceSource = "FDA (https://www.fda.gov) / DailyMed"
        ),
        PharmacyQuestion(
            questionText = "ما هو الترياق أو المضاد (Antidote) النوعي لحالة التسمم بالجرعة الزائدة من (Paracetamol) بَارَاسِيتَامُول؟",
            correctAnswer = "أسيتيل سيستيين (N-acetylcysteine) أو الاختصار (NAC)",
            difficulty = "صعب",
            hintForUser = "يحفز تصنيع الجلوتاثيون (Glutathione) في الكبد لحمايته من السموم.",
            hintForFriend = "مذيب للبلغم بالأساس ويُعطى وريدياً لحماية خلايا الكبد في الطوارئ ويرمز له بـ (NAC).",
            referenceSource = "MedlinePlus (https://medlineplus.gov) / DrugInfo"
        ),
        PharmacyQuestion(
            questionText = "ما هي الفئة العلاجية التي ينتمي إليها دواء (Metformin) مِيتْفُورْمِين وعائلته الدوائية الكيميائية؟",
            correctAnswer = "مضادات السكري الفموية / عائلة البيغوانيد (Biguanides)",
            difficulty = "صعب",
            hintForUser = "يقلل من إنتاج الجلوكوز في الكبد ويزيد من حساسية الخلايا للأنسولين.",
            hintForFriend = "الدواء الأول لعلاج السكري من النوع الثاني واسم عائلته الكيميائية تسمى البيغوانيد.",
            referenceSource = "FDA (https://www.fda.gov) / DailyMed"
        ),
        PharmacyQuestion(
            questionText = "ما الاستخدام العلاجي الأساسي لـ (Atorvastatin) أَتُورْفَاسْتَاتِين وما اسم الإنزيم الذي يثبطه؟",
            correctAnswer = "خافض للكوليسترول والدهون ثلاثية الجزيئات / يثبط إنزيم (HMG-CoA Reductase)",
            difficulty = "سهل",
            hintForUser = "ينتمي لعائلة الستاتينات (Statins) ويستهدف دهون الدم الشريانية.",
            hintForFriend = "يمنع إنتاج الكوليسترول داخل الكبد مباشرة عبر تثبيط خميرة أو إنزيم كبدي شهير.",
            referenceSource = "EMA (https://www.ema.europa.eu)"
        ),
        PharmacyQuestion(
            questionText = "ما هي عائلة المضاد الحيوي (Ciprofloxacin) سِيبْرُوفْلُوكْسَاسِين وما هو محذوره الحاد على الأوتار؟",
            correctAnswer = "عائلة الفلوروكوينولونات (Fluoroquinolones) ومحذوره هو خطر التهاب أو تمزق الأوتار (Tendon rupture)",
            difficulty = "سهل",
            hintForUser = "يمنع تضاعف الحمض النووي البكتيري وله تحذير صندوقي أسود من الـ FDA بخصوص الأوتار.",
            hintForFriend = "أحد أبرز جيل الكوينولونات المفلورة ويسبب خطراً نادراً على وتر أخيل (Achilles tendon).",
            referenceSource = "FDA (https://www.fda.gov) / DailyMed"
        ),
        PharmacyQuestion(
            questionText = "ما هي آلية العمل الأساسية لدواء الضغط (Lisinopril) لِيسِينُوبْرِيل؟",
            correctAnswer = "مثبط للإنزيم المحول للأنجيوتنسين (ACE Inhibitor)",
            difficulty = "صعب",
            hintForUser = "يمنع تحويل الأنجيوتنسين الأول إلى الثاني المؤدي لضيق الأوعية السريرية.",
            hintForFriend = "يرمز لعائلته بـ (ACE Inhibitors) ويسبب أحياناً سعالاً جافاً للمستخدمين.",
            referenceSource = "FDA (https://www.fda.gov)"
        ),
        PharmacyQuestion(
            questionText = "لماذا يحذر استخدام (Aspirin) أَسْبِرِين للأطفال تحت سن 18 عاماً عند إصابتهم بعدوى فيروسية؟",
            correctAnswer = "خوفاً من حدوث متلازمة راي (Reye's Syndrome) الخطيرة على الكبد والدماغ",
            difficulty = "صعب",
            hintForUser = "متلازمة نادرة تسبب تلفاً دماغياً وكبدياً حاداً للأطفال المعالجين بالأسبرين خلال الإنفلونزا.",
            hintForFriend = "متلازمة طبية خطيرة تحمل اسم مكتشفها الدكتور راي (Reye).",
            referenceSource = "MedlinePlus (https://medlineplus.gov) / FDA"
        ),
        PharmacyQuestion(
            questionText = "إلى أي عائلة دوائية ينتمي مسكن الألم (Ibuprofen) إِيبُوبْرُوفِين؟",
            correctAnswer = "مضادات الالتهاب غير الستيروئيدية (NSAIDs)",
            difficulty = "سهل",
            hintForUser = "عائلة مشهورة تمنع عمل إنزيمات الأكسدة الحلقية (COX-1) و (COX-2) غير الستيرويدية.",
            hintForFriend = "عائلة دوائية تضم مسكنات الألم ومضادات الالتهاب مثل الديكلوفيناك والنابروكسين والأسبرين.",
            referenceSource = "DrugInfo / DailyMed"
        ),
        PharmacyQuestion(
            questionText = "ما هي الفئة العلاجية لدواء الحموضة (Omeprazole) أُومِيبْرَازُول وما اسم المضخة الخلايا التي يشلها؟",
            correctAnswer = "مثبطات مضخة البروتون (Proton Pump Inhibitors - PPIs)",
            difficulty = "سهل",
            hintForUser = "يقلل إفراز حمض الهيدروكلوريك عن طريق تثبيط التبادل الأيوني للهيدروجين والبوتاسيوم (H+/K+ ATPase).",
            hintForFriend = "مثبط نهائي لإفراز الأسيد بالمعدة ويرمز لعائلته بالاختصار (PPI).",
            referenceSource = "FDA (https://www.fda.gov) / PubMed"
        ),
        PharmacyQuestion(
            questionText = "ما هي آلية عمل البخاخ الإسعافي (Ventolin / Albuterol) فِنْتُولِين / أَلْبُوتِيرُول في الجهاز التنفسي؟",
            correctAnswer = "محفز أو منشط لمستقبلات بيتا-2 الأدرينالية (Beta-2 Adrenergic Agonist / Bronchodilator)",
            difficulty = "صعب",
            hintForUser = "يعمل على مستقبلات بيتا-2 في القصبات الهوائية ليرخي العضلات الملساء ويوسع مجرى الهواء سريعاً.",
            hintForFriend = "يعاكس التشنج الشعبي بإنعاش وتنشيط مستقبلات بيتا الرئوية سريعة المفعول.",
            referenceSource = "DailyMed / FDA"
        )
    )
}
