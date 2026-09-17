package com.example.service

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import android.util.Log
import com.example.MainActivity

/**
 * Session service bound by Android framework when system assistant is invoked.
 */
class AppVoiceInteractionSessionService : VoiceInteractionSessionService() {

    override fun onNewSession(args: Bundle?): VoiceInteractionSession {
        return object : VoiceInteractionSession(this) {

            override fun onCreate() {
                super.onCreate()
                try {
                    window?.window?.apply {
                        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        setDimAmount(0f)
                    }
                } catch (e: Exception) {
                    Log.e("VoiceInteractionSession", "Error configuring session window", e)
                }
            }

            override fun onShow(args: Bundle?, showFlags: Int) {
                super.onShow(args, showFlags)
                launchMainActivity()
            }

            override fun onHandleAssist(state: AssistState) {
                super.onHandleAssist(state)
                launchMainActivity()
            }

            private fun launchMainActivity() {
                try {
                    val intent = Intent(this@AppVoiceInteractionSessionService, MainActivity::class.java).apply {
                        action = Intent.ACTION_ASSIST
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra("EXTRA_START_VOICE_LISTENING", true)
                    }
                    try {
                        startAssistantActivity(intent)
                    } catch (e: Throwable) {
                        try {
                            startVoiceActivity(intent)
                        } catch (e2: Throwable) {
                            this@AppVoiceInteractionSessionService.startActivity(intent)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("VoiceInteractionSession", "Error launching MainActivity", e)
                } finally {
                    try {
                        hide()
                        finish()
                    } catch (e: Exception) {
                        Log.e("VoiceInteractionSession", "Error finishing session", e)
                    }
                }
            }
        }
    }
}

