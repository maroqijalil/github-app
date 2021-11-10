package com.beginner.myapplication.utils

import android.content.Context
import java.io.IOException

object JsonProcess {
    fun getJsonString (ctx: Context?, nameOfFile: String): String? {
        return try {
            ctx?.assets?.open(nameOfFile)?.bufferedReader().use { it?.readText() }.toString()
        }
        catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}