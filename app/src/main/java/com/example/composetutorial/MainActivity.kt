
package com.example.composetutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.composetutorial.ui.theme.ComposeTutorialTheme
import kotlinx.coroutines.delay
import java.util.Locale

private val Black = Color.Black
private val White = Color.White
private val Gray = Color(0xFF505050)
private val Orange = Color(0xFFFF8529)

data class Faq(
    val question: String,
    val answer: String
)

private val faqItems = listOf(
    Faq(
        "На сколько эффективна планка?",
        "Планка помогает укреплять мышцы корпуса, живота и спины. Главное — соблюдать правильную технику."
    ),
    Faq(
        "Сколько калорий сжигается во время планки?",
        "Количество калорий зависит от массы тела, продолжительности упражнения и интенсивности нагрузки."
    ),
    Faq(
        "Какие мышцы укрепляет планка?",
        "Основная нагрузка приходится на мышцы пресса, спины, плечевого пояса и ягодиц."
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeTutorialTheme {
                SportLifeApp()
            }
        }
    }
}

@Composable
fun SportLifeApp() {
    var screen by rememberSaveable { mutableStateOf("login") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (screen) {
                "login" -> LoginScreen {
                    screen = "workout"
                }

                "workout" -> WorkoutScreen()
                "stats" -> StatisticsScreen()
                "help" -> HelpScreen()
                "profile" -> ProfileScreen()
            }
        }

        if (screen != "login") {
            BottomNavigationBar(
                selected = screen,
                onSelect = { screen = it }
            )
        }
    }
}

@Composable
fun SportLogo() {
    Image(
        painter = painterResource(R.drawable.sport_logo),
        contentDescription = "SPORT-LIFE",
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        contentScale = androidx.compose.ui.layout.ContentScale.Fit
    )
}

@Composable
fun ScreenTitle(text: String, size: Int = 26) {
    Text(
        text = text,
        color = White,
        fontSize = size.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SportButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(Gray)
            .border(1.5.dp, White, RoundedCornerShape(40.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = White,
            fontSize = 19.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun SportInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    password: Boolean = false
) {
    val shape = RoundedCornerShape(35.dp)

    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(Color(0xFF777777))
            .border(1.5.dp, White, shape)
            .padding(horizontal = 15.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                placeholder,
                color = Color.LightGray,
                fontSize = 17.sp
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = White,
                fontSize = 17.sp,
                fontFamily = FontFamily.Monospace
            ),
            visualTransformation = if (password) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            }
        )
    }
}

// ЭКРАН 1. АВТОРИЗАЦИЯ

@Composable
fun LoginScreen(onContinue: () -> Unit) {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SportLogo()

        Spacer(Modifier.height(20.dp))

        ScreenTitle("Добро пожаловать!", 25)

        Spacer(Modifier.height(55.dp))

        SportInput(
            value = login,
            onValueChange = { login = it },
            placeholder = "Логин",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(18.dp))

        SportInput(
            value = password,
            onValueChange = { password = it },
            placeholder = "Пароль",
            password = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        SportButton(
            text = "Далее  ❯❯",
            modifier = Modifier.fillMaxWidth(0.85f),
            onClick = onContinue
        )

        Spacer(Modifier.height(12.dp))
    }
}

// ЭКРАН 2. ТРЕНИРОВКА

@Composable
fun WorkoutScreen() {
    var remainingSeconds by rememberSaveable {
        mutableIntStateOf(60)
    }

    var isRunning by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds =
                    (remainingSeconds - 1).coerceAtLeast(0)
            }
            isRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SportLogo()

        ScreenTitle("Планка", 32)

        Spacer(Modifier.height(18.dp))

        Image(
            painter = painterResource(R.drawable.plank_photo),
            contentDescription = "Упражнение планка",
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Spacer(Modifier.height(20.dp))

        SportButton(
            text = "Начать",
            modifier = Modifier.fillMaxWidth(0.78f)
        ) {
            if (remainingSeconds == 0) {
                remainingSeconds = 60
            }
            isRunning = true
        }

        Spacer(Modifier.height(12.dp))

        SportButton(
            text = "Пауза",
            modifier = Modifier.fillMaxWidth(0.78f)
        ) {
            isRunning = false
        }

        Spacer(Modifier.height(22.dp))

        ScreenTitle("Цель: 1 минута", 23)

        Spacer(Modifier.height(32.dp))

        Text(
            text = String.format(
                Locale.US,
                "00:%02d:%02d",
                remainingSeconds / 60,
                remainingSeconds % 60
            ),
            color = White,
            fontSize = 43.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(25.dp))
    }
}

// ЭКРАН 3. СТАТИСТИКА

@Composable
fun StatisticsScreen() {
    val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

    val attendance = listOf(
        listOf("✓", "X", "✓", "✓", "X", "✓", "✓"),
        listOf("X", "✓", "X", "✓", "✓", "✓", "✓"),
        listOf("X", "✓", "✓", "✓", "X", "✓", "✓"),
        listOf("✓", "X", "✓", "✓", "✓", "✓", "✓")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SportLogo()

        Spacer(Modifier.height(10.dp))

        ScreenTitle("Регулярность занятий", 23)

        Row(Modifier.fillMaxWidth()) {
            days.forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    color = White,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                )
            }
        }

        attendance.forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { value ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(28.dp)
                            .border(0.5.dp, Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            value,
                            color = if (value == "✓") {
                                Color(0xFF64C4FF)
                            } else {
                                White
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        ScreenTitle("Калории", 27)

        LineChart(
            values = listOf(
                40f, 55f, 70f, 85f, 100f, 100f,
                100f, 150f, 140f, 130f, 150f,
                200f, 170f, 250f, 240f, 300f,
                300f, 200f, 240f, 250f, 200f, 230f
            )
        )

        Spacer(Modifier.height(12.dp))

        ScreenTitle("Вес", 27)

        LineChart(
            values = listOf(
                80f, 79.8f, 79.7f, 79.8f, 79.5f,
                79.3f, 79.4f, 79.0f, 78.9f, 79.0f,
                78.8f, 78.6f, 78.5f, 78.2f, 78.1f,
                77.8f, 76.5f, 75.9f, 75.7f, 75.2f,
                75f, 75.2f, 75.5f, 75.7f, 76.1f,
                76.3f
            )
        )

        Spacer(Modifier.height(15.dp))
    }
}

@Composable
fun LineChart(values: List<Float>) {
    val minValue = values.minOrNull() ?: 0f
    val maxValue = values.maxOrNull() ?: 1f
    val range = (maxValue - minValue).coerceAtLeast(1f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .border(2.dp, Color.LightGray)
            .padding(8.dp)
    ) {
        val left = 15f
        val right = size.width - 8f
        val top = 8f
        val bottom = size.height - 10f

        repeat(5) { index ->
            val y = top + (bottom - top) * index / 4f

            drawLine(
                color = Color.LightGray,
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val path = Path()

        values.forEachIndexed { index, value ->
            val x = left +
                    (right - left) * index / (values.size - 1).coerceAtLeast(1)

            val y = bottom -
                    ((value - minValue) / range) * (bottom - top)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = Orange,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}

// ЭКРАН 4. ПОМОЩЬ ТРЕНЕРА

@Composable
fun HelpScreen() {
    var expandedFaq by rememberSaveable {
        mutableIntStateOf(-1)
    }

    var question by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        SportLogo()

        Spacer(Modifier.height(12.dp))

        Text(
            "Вопрос-ответ:",
            color = White,
            fontSize = 26.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(Modifier.height(12.dp))

        faqItems.forEachIndexed { index, faq ->
            Column {
                val shape = RoundedCornerShape(35.dp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(Gray)
                        .border(1.5.dp, White, shape)
                        .clickable {
                            expandedFaq =
                                if (expandedFaq == index) -1 else index
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            faq.question,
                            modifier = Modifier.weight(1f),
                            color = White,
                            fontSize = 14.sp
                        )

                        Text(
                            if (expandedFaq == index) "↑" else "↓",
                            color = White,
                            fontSize = 25.sp
                        )
                    }
                }

                if (expandedFaq == index) {
                    Text(
                        faq.answer,
                        modifier = Modifier.padding(
                            start = 12.dp,
                            top = 8.dp,
                            bottom = 12.dp
                        ),
                        color = White,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Gray)
                .border(
                    1.5.dp,
                    White,
                    RoundedCornerShape(22.dp)
                )
                .padding(12.dp)
        ) {
            ScreenTitle("Введите свой вопрос", 20)

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.trainer_avatar),
                    contentDescription = "Тренер",
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .border(2.dp, White, CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        "Тренер Петя БОСС",
                        color = White,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Text(
                        "Стаж работы: 27 лет",
                        color = White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            SportInput(
                value = question,
                onValueChange = { question = it },
                placeholder = "Вопрос",
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(25.dp))
    }
}

// ЭКРАН 5. ЛИЧНЫЙ КАБИНЕТ

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        SportLogo()

        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.user_avatar),
                contentDescription = "Фото пользователя",
                modifier = Modifier
                    .size(105.dp)
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Text(
                "Станов\nПерс\nПриседалович",
                color = White,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 26.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        ProfileInfo("Вес: 120")

        ProfileInfo("Потрачено Ккал: 7777")

        ProfileInfo("Предстоящая тренировка:\nПланка")

        ProfileInfo("Выполнено:\n6 кругов")

        Spacer(Modifier.height(10.dp))

        Image(
            painter = painterResource(R.drawable.exercise_sheet),
            contentDescription = "Комплекс упражнений",
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
        )

        Spacer(Modifier.height(18.dp))
    }
}

@Composable
fun ProfileInfo(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(vertical = 12.dp),
        color = White,
        fontSize = 19.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 25.sp
    )
}

// НИЖНЯЯ НАВИГАЦИЯ

@Composable
fun BottomNavigationBar(
    selected: String,
    onSelect: (String) -> Unit
) {
    val tabs = listOf(
        Triple("workout", "💪", "Упражнения"),
        Triple("stats", "▥", "Статистика"),
        Triple("help", "♜", "Помощь тренера"),
        Triple("profile", "●", "Личный кабинет")
    )

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Black)
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onSelect(tab.first)
                        }
                        .padding(vertical = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tab.second,
                        color = White,
                        fontSize = 25.sp,
                        fontWeight = if (selected == tab.first) {
                            FontWeight.Bold
                        } else {
                            FontWeight.Normal
                        }
                    )

                    Text(
                        text = tab.third,
                        color = White,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 11.sp
                    )
                }
            }
        }
    }
}
