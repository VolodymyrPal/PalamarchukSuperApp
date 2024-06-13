package com.hfad.palamarchuksuperapp.domain.models

import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import javax.inject.Inject

@Suppress("DEPRECATION", "unused")
class AppVibrator @Inject constructor (val context: Context) {

    fun hasVibrator(): Boolean =
        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).hasVibrator()

    @RequiresApi(Build.VERSION_CODES.O)
    fun hasAmplitudeControl() =
        (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).hasAmplitudeControl()

    @RequiresApi(Build.VERSION_CODES.S)
    fun cancel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).cancel()
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).cancel()
        }
    }

    fun standardClickVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrate(VibrationEffect.createOneShot(2, VIBRATION_AMPLITUDE))
        } else {
            vibrate(1)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun vibrate(milliseconds: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                .defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(2, VIBRATION_AMPLITUDE)
                )
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(milliseconds)
        }
    }

    fun vibrate(pattern: LongArray, repeat: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                .defaultVibrator.vibrate(pattern, repeat)
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(
                pattern,
                repeat
            )
        }
    }

    @Suppress("DEPRECATION")
    fun vibrate(milliseconds: Long, attributes: AudioAttributes?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                .defaultVibrator.vibrate(
                    milliseconds,
                    attributes
                )
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(
                milliseconds,
                attributes
            )
        }
    }

    @Suppress("DEPRECATION")
    fun vibrate(pattern: LongArray, repeat: Int, attributes: AudioAttributes?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                .defaultVibrator.vibrate(pattern, repeat, attributes)
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(
                pattern,
                repeat,
                attributes
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun vibrate(vibrationEffect: VibrationEffect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                .defaultVibrator.vibrate(vibrationEffect)
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(vibrationEffect)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Suppress("DEPRECATION")
    fun vibrate(vibrationEffect: VibrationEffect, attributes: AudioAttributes?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                .defaultVibrator.vibrate(vibrationEffect, attributes)
        } else {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(
                vibrationEffect,
                attributes
            )
        }
    }

    companion object {
        const val VIBRATION_AMPLITUDE : Int = 60
    }
}
