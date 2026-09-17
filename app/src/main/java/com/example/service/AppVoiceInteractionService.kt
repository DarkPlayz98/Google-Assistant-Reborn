package com.example.service

import android.content.Intent
import android.service.voice.VoiceInteractionService

/**
 * Service required by Android OS to register this app as a system-level Voice Assistant
 * in "Default Digital Assistant app" settings.
 */
class AppVoiceInteractionService : VoiceInteractionService() {

    override fun onReady() {
        super.onReady()
    }

    override fun onShutdown() {
        super.onShutdown()
    }
}
