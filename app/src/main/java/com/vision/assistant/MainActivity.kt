
package com.vision.assistant

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.content.Intent
import java.util.Locale

class MainActivity : Activity() {

    private var tts: TextToSpeech? = null
    private val voiceRequest = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        tts = TextToSpeech(this) {
            tts?.language = Locale.US
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 24, 24, 24)
            setBackgroundColor(Color.rgb(5, 5, 12))
        }

        val title = TextView(this).apply {
            text = "VISION"
            textSize = 38f
            setTextColor(Color.RED)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "YOUR PERSONAL AI ASSISTANT"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val status = TextView(this).apply {
            text = "SYSTEM READY"
            textSize = 18f
            setTextColor(Color.RED)
            gravity = Gravity.CENTER
        }

        val button = Button(this).apply {
            text = "🎙 ACTIVATE VISION"
            textSize = 18f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(150, 0, 0))
            setOnClickListener { listen() }
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(status)
        layout.addView(button)

        setContentView(layout)
    }

    private fun listen() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "en-US"
        )

        try {
            startActivityForResult(intent, voiceRequest)
        } catch (e: Exception) {
            speak("Voice recognition is not available")
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == voiceRequest &&
            resultCode == RESULT_OK) {

            val words = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )

            val command = words?.firstOrNull()?.lowercase() ?: ""

            when {
                command.contains("youtube") -> openApp(
                    "com.google.android.youtube"
                )
                command.contains("chrome") -> openApp(
                    "com.android.chrome"
                )
                command.contains("hello") -> speak("Hello! I am VISION.")
                else -> speak("I heard you. More features are coming.")
            }
        }
    }

    private fun openApp(packageName: String) {
        val launch = packageManager.getLaunchIntentForPackage(packageName)
        if (launch != null) {
            startActivity(launch)
            speak("Opening application")
        } else {
            speak("Application not found")
        }
    }

    private fun speak(message: String) {
        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, null, "VISION")
    }

    override fun onDestroy() {
        tts?.shutdown()
        super.onDestroy()
    }
}
