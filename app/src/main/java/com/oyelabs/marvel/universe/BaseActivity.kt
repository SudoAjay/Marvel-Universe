package com.oyelabs.marvel.universe

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.AndroidEntryPoint

import java.util.*

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    private lateinit var currentTheme: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        currentTheme =
            getDarkMode(
                applicationContext
            )
        setAppTheme(currentTheme)

    }

    override fun onResume() {
        super.onResume()
        val theme =
            getDarkMode(
                applicationContext
            )


        if (currentTheme != theme)
            recreate()

    }

    private fun setAppTheme(currentTheme: String) {
        when (currentTheme) {
            getString(R.string.off_text) -> {
                setValue(false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            getString(
                R.string.automatic_at_sunset_text
            ) -> setDarkMode(isSunset())
            getString(
                R.string.set_by_battery_saver_text
            ) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setValue(isPowerSaveMode())
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    )

                } else {
                    setValue(true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

            }
            getString(
                R.string.system_default_text
            ) -> {
                setValue(isSystemDefaultOn())
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
            else -> {
                setValue(true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

    }

    private fun setDarkMode(isDarkMode: Boolean) {
        setValue(isDarkMode)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    private fun isSunset(): Boolean {
        val rightNow: Calendar = Calendar.getInstance()
        val hour: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour > 18
    }


    private fun setValue(isDarkMode: Boolean) {
        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(getString(R.string.is_dark_mode_text), isDarkMode).apply()
    }


    private fun isSystemDefaultOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun isPowerSaveMode(): Boolean {
        val powerManager =
            getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isPowerSaveMode

    }


    companion object {

        fun getDarkMode(context: Context): String {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.dark_mode_text),
                    context.getString(R.string.system_default_text)
                )
                .toString()
        }

        fun isDarkMode(context: Context): Boolean {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getBoolean(
                    context.getString(R.string.is_dark_mode_text), false
                )
        }


    }


}