package com.example.quizapp

import javafx.application.Application
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage

data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

class QuizApp : Application() {

    // 10 questions (we'll shuffle them in initQuiz())
    private val questionBank = listOf(
        Question("Which data structure uses FIFO?", listOf("Stack", "Queue", "Tree", "Graph"), 1),
        Question("What does JVM stand for?", listOf("Java Visual Machine", "Java Virtual Machine", "Joint Vector Memory", "Java Verified Module"), 1),
        Question("Which keyword defines a compile-time constant in Kotlin?", listOf("val", "final", "const", "let"), 2),
        Question("Which is NOT an OOP principle?", listOf("Encapsulation", "Polymorphism", "Compilation", "Inheritance"), 2),
        Question("Binary search average time complexity is:", listOf("O(n)", "O(log n)", "O(n log n)", "O(1)"), 1),

        Question("Which collection in Kotlin does NOT allow duplicates?", listOf("List", "Set", "ArrayList", "MutableList"), 1),
        Question("HTTP status code 404 means:", listOf("OK", "Unauthorized", "Not Found", "Server Error"), 2),
        Question("Which sorting algorithm is typically O(n log n) average-case?", listOf("Bubble Sort", "Insertion Sort", "Merge Sort", "Selection Sort"), 2),
        Question("In SQL, which command is used to remove a table completely?", listOf("DROP TABLE", "DELETE TABLE", "REMOVE TABLE", "TRUNCATE TABLE"), 0),
        Question("Which Git command is used to upload commits to a remote repository?", listOf("git push", "git pull", "git commit", "git merge"), 0)
    )

    private var questions: List<Question> = emptyList()
    private var currentIndex = 0
    private var score = 0
    private var hasSubmitted = false

    private lateinit var progressLabel: Label
    private lateinit var questionLabel: Label
    private lateinit var feedbackLabel: Label

    private lateinit var toggleGroup: ToggleGroup
    private lateinit var optionRadios: List<RadioButton>

    private lateinit var actionButton: Button
    private lateinit var restartButton: Button

    override fun start(stage: Stage) {
        val titleLabel = Label("Quiz Application").apply {
            font = Font.font("System", 22.0)
        }

        progressLabel = Label("").apply {
            font = Font.font("System", 13.0)
        }

        questionLabel = Label("").apply {
            font = Font.font("System", 16.0)
            isWrapText = true
        }

        feedbackLabel = Label("").apply {
            font = Font.font("System", 13.0)
        }

        toggleGroup = ToggleGroup()
        optionRadios = List(4) { idx ->
            RadioButton("").apply {
                toggleGroup = this@QuizApp.toggleGroup
                userData = idx
                isWrapText = true
            }
        }

        actionButton = Button("Submit").apply {
            setOnAction { handleSubmitOrNext() }
        }

        restartButton = Button("Restart").apply {
            isVisible = false
            setOnAction { restartQuiz() }
        }

        val buttonRow = HBox(10.0, actionButton, restartButton).apply {
            alignment = Pos.CENTER_LEFT
        }

        val root = VBox(12.0).apply {
            padding = Insets(20.0)
            children.addAll(
                titleLabel,
                progressLabel,
                Separator(),
                questionLabel,
                optionRadios[0],
                optionRadios[1],
                optionRadios[2],
                optionRadios[3],
                Separator(),
                buttonRow,
                feedbackLabel
            )
        }

        initQuiz()
        loadQuestion()

        stage.title = "Quiz App (Kotlin + JavaFX)"
        stage.scene = Scene(root, 620.0, 420.0)
        stage.show()
    }

    private fun initQuiz() {
        // Shuffle each time to make it feel dynamic
        questions = questionBank.shuffled()
        currentIndex = 0
        score = 0
        hasSubmitted = false
    }

    private fun loadQuestion() {
        val q = questions[currentIndex]

        progressLabel.text = "Question ${currentIndex + 1} of ${questions.size} | Score: $score"
        questionLabel.text = q.text

        toggleGroup.selectToggle(null)
        optionRadios.forEachIndexed { i, rb ->
            rb.text = q.options[i]
            rb.isDisable = false
            rb.isVisible = true
        }

        feedbackLabel.text = ""
        hasSubmitted = false
        actionButton.text = "Submit"
        actionButton.isVisible = true
        restartButton.isVisible = false
    }

    private fun handleSubmitOrNext() {
        // If already submitted, move to next question
        if (hasSubmitted) {
            currentIndex++
            if (currentIndex < questions.size) {
                loadQuestion()
            } else {
                showFinalResult()
            }
            return
        }

        // Submit flow
        val selected = toggleGroup.selectedToggle
        if (selected == null) {
            feedbackLabel.text = "Please select an answer."
            return
        }

        val selectedIndex = selected.userData as Int
        val q = questions[currentIndex]

        optionRadios.forEach { it.isDisable = true }

        if (selectedIndex == q.correctIndex) {
            score++
            feedbackLabel.text = "✅ Correct!"
        } else {
            feedbackLabel.text = "❌ Incorrect. Correct answer: ${q.options[q.correctIndex]}"
        }

        progressLabel.text = "Question ${currentIndex + 1} of ${questions.size} | Score: $score"
        hasSubmitted = true
        actionButton.text = if (currentIndex == questions.lastIndex) "Finish" else "Next"
    }

    private fun showFinalResult() {
        questionLabel.text = "🎉 Quiz Completed!"
        optionRadios.forEach { it.isVisible = false }

        val total = questions.size
        val percent = (score * 100.0) / total
        feedbackLabel.text = "Final Score: $score / $total (${String.format("%.1f", percent)}%)"

        actionButton.isVisible = false
        restartButton.isVisible = true
        progressLabel.text = "Done"
    }

    private fun restartQuiz() {
        initQuiz()

        // bring back options if hidden
        optionRadios.forEach { it.isVisible = true }

        loadQuestion()
    }
}

fun main() {
    Application.launch(QuizApp::class.java)
}
