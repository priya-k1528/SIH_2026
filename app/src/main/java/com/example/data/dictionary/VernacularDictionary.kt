package com.example.data.dictionary

data class VernacularEntry(
    val english: String,
    val hindi: String,
    val santhaliOlChiki: String,
    val santhaliRoman: String,
    val mundariDevanagari: String,
    val mundariRoman: String,
    val category: String,
    val emoji: String = "📚",
    val pronunciationNote: String = ""
)

object VernacularDictionary {

    val entries: List<VernacularEntry> = listOf(
        // === CLASSROOM INSTRUCTIONS ===
        VernacularEntry(
            english = "Stand up",
            hindi = "खड़े हो जाओ",
            santhaliOlChiki = "ᱛᱤᱸᱜᱩᱱ ᱢᱮ",
            santhaliRoman = "Tingun me",
            mundariDevanagari = "तिंगुन मे",
            mundariRoman = "Tingun me",
            category = "Classroom",
            emoji = "🧍",
            pronunciationNote = "Teacher command to stand"
        ),
        VernacularEntry(
            english = "Sit down",
            hindi = "बैठ जाओ",
            santhaliOlChiki = "ᱫᱩᱲᱩᱵ ᱢᱮ",
            santhaliRoman = "Durub me",
            mundariDevanagari = "दुबुंग मे",
            mundariRoman = "Dubung me",
            category = "Classroom",
            emoji = "🪑",
            pronunciationNote = "Teacher command to sit"
        ),
        VernacularEntry(
            english = "Open your book",
            hindi = "अपनी किताब खोलो",
            santhaliOlChiki = "ᱟᱢᱟᱜ ᱯᱚᱛᱚᱵ ᱠᱷᱩᱞᱟᱹᱣ ᱢᱮ",
            santhaliRoman = "Amak' potob khulau me",
            mundariDevanagari = "अमाः पुथी उगुई मे",
            mundariRoman = "Amah puthi ugui me",
            category = "Classroom",
            emoji = "📖",
            pronunciationNote = "Direct instruction for reading"
        ),
        VernacularEntry(
            english = "Look at the blackboard",
            hindi = "ब्लैकबोर्ड पर देखो",
            santhaliOlChiki = "ᱠᱟᱞᱤ ᱵᱚᱨᱰ ᱨᱮ ᱧᱮᱞ ᱢᱮ",
            santhaliRoman = "Kali bord re nyel me",
            mundariDevanagari = "बोर्ड रे लेल मे",
            mundariRoman = "Bord re lel me",
            category = "Classroom",
            emoji = "🧑‍🏫",
            pronunciationNote = "Direct student attention"
        ),
        VernacularEntry(
            english = "Listen carefully",
            hindi = "ध्यान से सुनो",
            santhaliOlChiki = "ᱫᱷᱮᱭᱟᱱ ᱛᱮ ᱟᱧᱡᱚᱢ ᱢᱮ",
            santhaliRoman = "Dhiyan te anjom me",
            mundariDevanagari = "सुनाऊ मे ध्यान ते",
            mundariRoman = "Sunau me dhiyan te",
            category = "Classroom",
            emoji = "👂",
            pronunciationNote = "Listening instruction"
        ),
        VernacularEntry(
            english = "Write in your notebook",
            hindi = "अपनी कॉपी में लिखो",
            santhaliOlChiki = "ᱟᱢᱟᱜ ᱠᱷᱟᱛᱟ ᱨᱮ ᱚᱞ ᱢᱮ",
            santhaliRoman = "Amak' khata re ol me",
            mundariDevanagari = "अमाः खाता रे ओल मे",
            mundariRoman = "Amah khata re ol me",
            category = "Classroom",
            emoji = "✍️",
            pronunciationNote = "Writing instruction"
        ),
        VernacularEntry(
            english = "Good morning",
            hindi = "सुप्रभात",
            santhaliOlChiki = "ᱡᱚᱦᱟᱨ",
            santhaliRoman = "Johar",
            mundariDevanagari = "जोहार",
            mundariRoman = "Johar",
            category = "Classroom",
            emoji = "🌅",
            pronunciationNote = "Traditional respectful greeting"
        ),
        VernacularEntry(
            english = "Thank you",
            hindi = "धन्यवाद",
            santhaliOlChiki = "ᱥᱟᱨᱦᱟᱣ",
            santhaliRoman = "Sarhao",
            mundariDevanagari = "सराहाओ",
            mundariRoman = "Sarahao",
            category = "Classroom",
            emoji = "🙏",
            pronunciationNote = "Expressing gratitude"
        ),
        VernacularEntry(
            english = "Well done / Very good",
            hindi = "बहुत अच्छा",
            santhaliOlChiki = "ᱟᱹᱰᱤ ᱱᱟᱯᱟᱭ",
            santhaliRoman = "Adi napai",
            mundariDevanagari = "पुरः बेशर",
            mundariRoman = "Purah beshar",
            category = "Classroom",
            emoji = "🌟",
            pronunciationNote = "Teacher praise to student"
        ),
        VernacularEntry(
            english = "Do you understand?",
            hindi = "क्या आप समझे?",
            santhaliOlChiki = "ᱪᱮᱫ ᱟᱢᱮᱢ ᱵᱩᱡᱷᱟᱹᱣ ᱠᱮᱫᱼᱟ?",
            santhaliRoman = "Ched amem bujhau ked-a?",
            mundariDevanagari = "चिना आम बुझौ केदाम?",
            mundariRoman = "China aam bujhau kedam?",
            category = "Classroom",
            emoji = "❓",
            pronunciationNote = "Checking comprehension"
        ),

        // === NUMBERS ===
        VernacularEntry(
            english = "One",
            hindi = "एक",
            santhaliOlChiki = "ᱢᱤᱫ",
            santhaliRoman = "Mit'",
            mundariDevanagari = "मियाद",
            mundariRoman = "Miyad",
            category = "Numbers",
            emoji = "1️⃣"
        ),
        VernacularEntry(
            english = "Two",
            hindi = "दो",
            santhaliOlChiki = "ᱵᱟᱨ",
            santhaliRoman = "Bar",
            mundariDevanagari = "बारिया",
            mundariRoman = "Bariya",
            category = "Numbers",
            emoji = "2️⃣"
        ),
        VernacularEntry(
            english = "Three",
            hindi = "तीन",
            santhaliOlChiki = "ᱯᱮ",
            santhaliRoman = "Pe",
            mundariDevanagari = "अपिया",
            mundariRoman = "Apiya",
            category = "Numbers",
            emoji = "3️⃣"
        ),
        VernacularEntry(
            english = "Four",
            hindi = "चार",
            santhaliOlChiki = "ᱯᱩᱱ",
            santhaliRoman = "Pun",
            mundariDevanagari = "उपूनिया",
            mundariRoman = "Upuniya",
            category = "Numbers",
            emoji = "4️⃣"
        ),
        VernacularEntry(
            english = "Five",
            hindi = "पाँच",
            santhaliOlChiki = "ᱢᱚᱬᱮ",
            santhaliRoman = "More",
            mundariDevanagari = "मोड़ेया",
            mundariRoman = "Modeya",
            category = "Numbers",
            emoji = "5️⃣"
        ),
        VernacularEntry(
            english = "Six",
            hindi = "छह",
            santhaliOlChiki = "ᱛᱩᱨᱩᱭ",
            santhaliRoman = "Turui",
            mundariDevanagari = "तुरुइया",
            mundariRoman = "Turuiya",
            category = "Numbers",
            emoji = "6️⃣"
        ),
        VernacularEntry(
            english = "Seven",
            hindi = "सात",
            santhaliOlChiki = "ᱮᱭᱟᱭ",
            santhaliRoman = "Eyai",
            mundariDevanagari = "एया",
            mundariRoman = "Eya",
            category = "Numbers",
            emoji = "7️⃣"
        ),
        VernacularEntry(
            english = "Eight",
            hindi = "आठ",
            santhaliOlChiki = "ᱤᱨᱟᱹᱞ",
            santhaliRoman = "Iral",
            mundariDevanagari = "इरलिया",
            mundariRoman = "Irliya",
            category = "Numbers",
            emoji = "8️⃣"
        ),
        VernacularEntry(
            english = "Nine",
            hindi = "नौ",
            santhaliOlChiki = "ᱟᱨᱮ",
            santhaliRoman = "Are",
            mundariDevanagari = "अरेया",
            mundariRoman = "Areya",
            category = "Numbers",
            emoji = "9️⃣"
        ),
        VernacularEntry(
            english = "Ten",
            hindi = "दस",
            santhaliOlChiki = "ᱜᱮᱞ",
            santhaliRoman = "Gel",
            mundariDevanagari = "गेलेया",
            mundariRoman = "Geleya",
            category = "Numbers",
            emoji = "🔟"
        ),

        // === COLOURS ===
        VernacularEntry(
            english = "Red",
            hindi = "लाल",
            santhaliOlChiki = "ᱟᱨᱟᱜ",
            santhaliRoman = "Ara'",
            mundariDevanagari = "आराः",
            mundariRoman = "Arah",
            category = "Colours",
            emoji = "🔴"
        ),
        VernacularEntry(
            english = "Green",
            hindi = "हरा",
            santhaliOlChiki = "ᱦᱟᱹᱨᱤᱭᱟᱹᱲ",
            santhaliRoman = "Hariyad",
            mundariDevanagari = "हरियर",
            mundariRoman = "Hariyar",
            category = "Colours",
            emoji = "🟢"
        ),
        VernacularEntry(
            english = "Blue",
            hindi = "नीला",
            santhaliOlChiki = "ᱞᱤᱞ",
            santhaliRoman = "Lil",
            mundariDevanagari = "लील",
            mundariRoman = "Leel",
            category = "Colours",
            emoji = "🔵"
        ),
        VernacularEntry(
            english = "Yellow",
            hindi = "पीला",
            santhaliOlChiki = "ᱥᱟᱥᱟᱝ",
            santhaliRoman = "Sasang",
            mundariDevanagari = "ससंग",
            mundariRoman = "Sasang",
            category = "Colours",
            emoji = "🟡"
        ),
        VernacularEntry(
            english = "White",
            hindi = "सफेद",
            santhaliOlChiki = "ᱯᱩᱸᱰ",
            santhaliRoman = "Pund",
            mundariDevanagari = "पुंडी",
            mundariRoman = "Pundi",
            category = "Colours",
            emoji = "⚪"
        ),
        VernacularEntry(
            english = "Black",
            hindi = "काला",
            santhaliOlChiki = "ᱦᱮᱸᱫᱮ",
            santhaliRoman = "Hende",
            mundariDevanagari = "हेंदे",
            mundariRoman = "Hende",
            category = "Colours",
            emoji = "⚫"
        ),

        // === ANIMALS ===
        VernacularEntry(
            english = "Cow",
            hindi = "गाय",
            santhaliOlChiki = "ᱜᱟᱹᱭ",
            santhaliRoman = "Gai",
            mundariDevanagari = "उरीः",
            mundariRoman = "Urih",
            category = "Animals",
            emoji = "🐄"
        ),
        VernacularEntry(
            english = "Dog",
            hindi = "कुत्ता",
            santhaliOlChiki = "ᱥᱮᱛᱟ",
            santhaliRoman = "Seta",
            mundariDevanagari = "सेता",
            mundariRoman = "Seta",
            category = "Animals",
            emoji = "🐕"
        ),
        VernacularEntry(
            english = "Cat",
            hindi = "बिल्ली",
            santhaliOlChiki = "ᱯᱩᱥᱤ",
            santhaliRoman = "Pusi",
            mundariDevanagari = "पुसी",
            mundariRoman = "Pusi",
            category = "Animals",
            emoji = "🐈"
        ),
        VernacularEntry(
            english = "Elephant",
            hindi = "हाथी",
            santhaliOlChiki = "ᱦᱟᱹᱛᱤ",
            santhaliRoman = "Hati",
            mundariDevanagari = "हाती",
            mundariRoman = "Hati",
            category = "Animals",
            emoji = "🐘"
        ),
        VernacularEntry(
            english = "Tiger",
            hindi = "बाघ",
            santhaliOlChiki = "ᱛᱟᱹᱨᱩᱵ",
            santhaliRoman = "Tarub",
            mundariDevanagari = "कुला",
            mundariRoman = "Kula",
            category = "Animals",
            emoji = "🐅"
        ),
        VernacularEntry(
            english = "Goat",
            hindi = "बकरी",
            santhaliOlChiki = "ᱢᱮᱨᱚᱢ",
            santhaliRoman = "Merom",
            mundariDevanagari = "मेरों",
            mundariRoman = "Merom",
            category = "Animals",
            emoji = "🐐"
        ),
        VernacularEntry(
            english = "Bird",
            hindi = "चिड़िया",
            santhaliOlChiki = "ᱪᱮᱬᱮ",
            santhaliRoman = "Chene",
            mundariDevanagari = "चेणें",
            mundariRoman = "Chene",
            category = "Animals",
            emoji = "🐦"
        ),
        VernacularEntry(
            english = "Fish",
            hindi = "मछली",
            santhaliOlChiki = "ᱦᱟᱠᱚ",
            santhaliRoman = "Hako",
            mundariDevanagari = "हाइ",
            mundariRoman = "Hai",
            category = "Animals",
            emoji = "🐟"
        ),

        // === FRUITS ===
        VernacularEntry(
            english = "Mango",
            hindi = "आम",
            santhaliOlChiki = "ᱩᱞ",
            santhaliRoman = "Ul",
            mundariDevanagari = "उली",
            mundariRoman = "Uli",
            category = "Fruits",
            emoji = "🥭"
        ),
        VernacularEntry(
            english = "Banana",
            hindi = "केला",
            santhaliOlChiki = "ᱠᱟᱭᱨᱟ",
            santhaliRoman = "Kaira",
            mundariDevanagari = "कदेरा",
            mundariRoman = "Kadera",
            category = "Fruits",
            emoji = "🍌"
        ),
        VernacularEntry(
            english = "Apple",
            hindi = "सेब",
            santhaliOlChiki = "ᱥᱮᱣ",
            santhaliRoman = "Seo",
            mundariDevanagari = "सेव",
            mundariRoman = "Sev",
            category = "Fruits",
            emoji = "🍎"
        ),
        VernacularEntry(
            english = "Guava",
            hindi = "अमरूद",
            santhaliOlChiki = "ᱵᱮᱞᱚᱠ",
            santhaliRoman = "Belok",
            mundariDevanagari = "अमरूद",
            mundariRoman = "Amrud",
            category = "Fruits",
            emoji = "🍐"
        ),

        // === SHAPES ===
        VernacularEntry(
            english = "Circle",
            hindi = "गोला / वृत्त",
            santhaliOlChiki = "ᱜᱳᱞ",
            santhaliRoman = "Gol",
            mundariDevanagari = "गोल",
            mundariRoman = "Gol",
            category = "Shapes",
            emoji = "⭕"
        ),
        VernacularEntry(
            english = "Square",
            hindi = "वर्ग / चौकोर",
            santhaliOlChiki = "ᱪᱟᱹᱣᱠᱟᱹ",
            santhaliRoman = "Chauka",
            mundariDevanagari = "चौका",
            mundariRoman = "Chauka",
            category = "Shapes",
            emoji = "⏹️"
        ),
        VernacularEntry(
            english = "Triangle",
            hindi = "त्रिकोण",
            santhaliOlChiki = "ᱯᱮ ᱠᱳᱬ",
            santhaliRoman = "Pe kon",
            mundariDevanagari = "अपी कोना",
            mundariRoman = "Api kona",
            category = "Shapes",
            emoji = "🔺"
        ),

        // === COMMON OBJECTS / SCHOOL ===
        VernacularEntry(
            english = "Book",
            hindi = "किताब",
            santhaliOlChiki = "ᱯᱚᱛᱚᱵ",
            santhaliRoman = "Potob",
            mundariDevanagari = "पुथी",
            mundariRoman = "Puthi",
            category = "Objects",
            emoji = "📚"
        ),
        VernacularEntry(
            english = "Water",
            hindi = "पानी",
            santhaliOlChiki = "ᱫᱟᱜ",
            santhaliRoman = "Dak'",
            mundariDevanagari = "दाः",
            mundariRoman = "Dah",
            category = "Objects",
            emoji = "💧"
        ),
        VernacularEntry(
            english = "Tree",
            hindi = "पेड़",
            santhaliOlChiki = "ᱫᱟᱨᱮ",
            santhaliRoman = "Dare",
            mundariDevanagari = "दारू",
            mundariRoman = "Daru",
            category = "Objects",
            emoji = "🌳"
        ),
        VernacularEntry(
            english = "Sun",
            hindi = "सूरज",
            santhaliOlChiki = "ᱥᱤᱧ ᱪᱟᱸᱫᱚ",
            santhaliRoman = "Sing Chando",
            mundariDevanagari = "सिंगी",
            mundariRoman = "Singi",
            category = "Objects",
            emoji = "☀️"
        ),
        VernacularEntry(
            english = "Moon",
            hindi = "चाँद",
            santhaliOlChiki = "ᱧᱤᱫᱟᱹ ᱪᱟᱸᱫᱚ",
            santhaliRoman = "Nyinda Chando",
            mundariDevanagari = "चांदु",
            mundariRoman = "Chandu",
            category = "Objects",
            emoji = "🌙"
        ),
        VernacularEntry(
            english = "School",
            hindi = "विद्यालय / स्कूल",
            santhaliOlChiki = "ᱟᱥᱲᱟ",
            santhaliRoman = "Asda",
            mundariDevanagari = "इस्कुल",
            mundariRoman = "Iskul",
            category = "Objects",
            emoji = "🏫"
        ),
        VernacularEntry(
            english = "Teacher",
            hindi = "शिक्षक / गुरुजी",
            santhaliOlChiki = "ᱢᱟᱪᱮᱛ",
            santhaliRoman = "Machet'",
            mundariDevanagari = "माचेत",
            mundariRoman = "Machet",
            category = "Classroom",
            emoji = "👨‍🏫"
        ),
        VernacularEntry(
            english = "Student",
            hindi = "छात्र / विद्यार्थी",
            santhaliOlChiki = "ᱯᱟᱹᱴᱷᱩᱣᱟᱹ",
            santhaliRoman = "Pathua",
            mundariDevanagari = "पढुआ",
            mundariRoman = "Padhua",
            category = "Classroom",
            emoji = "🎒"
        )
    )

    fun findDirectMatch(query: String): VernacularEntry? {
        val clean = query.trim().lowercase()
        return entries.find {
            it.hindi.equals(clean, ignoreCase = true) ||
            it.english.equals(clean, ignoreCase = true) ||
            it.santhaliRoman.equals(clean, ignoreCase = true) ||
            it.santhaliOlChiki.equals(clean, ignoreCase = true) ||
            it.mundariRoman.equals(clean, ignoreCase = true) ||
            it.mundariDevanagari.equals(clean, ignoreCase = true)
        }
    }

    fun searchMatches(query: String): List<VernacularEntry> {
        val clean = query.trim().lowercase()
        if (clean.isBlank()) return entries
        return entries.filter {
            it.hindi.contains(clean, ignoreCase = true) ||
            it.english.contains(clean, ignoreCase = true) ||
            it.santhaliRoman.contains(clean, ignoreCase = true) ||
            it.santhaliOlChiki.contains(clean, ignoreCase = true) ||
            it.mundariRoman.contains(clean, ignoreCase = true) ||
            it.mundariDevanagari.contains(clean, ignoreCase = true) ||
            it.category.contains(clean, ignoreCase = true)
        }
    }
}
