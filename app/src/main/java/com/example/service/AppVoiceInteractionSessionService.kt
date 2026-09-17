package com.example.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService

/**
 * Session service bound by Android framework when system assistant is invoked.
 */
class AppVoiceInteractionSessionService : VoiceInteractionSessionService() {

    override fun onNewSession(args: android.os.Bundle?): VoiceInteractionSession {
        return object : VoiceInteractionSession(this) {
            override fun onHandleAssist(state: AssistState) {
                super.onHandleAssist(state)
                // When invoked via home long press / power gesture, launch the Assistant UI
                val intent = Intent(this@AppVoiceInteractionSessionService, com.example.MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startVoiceActivity(intent)
                finish()
            }
        }
    }
}
