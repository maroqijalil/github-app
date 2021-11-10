package com.beginner.consumerapp.utils.handler

interface MainHandler {
    fun stopLoad()
    fun showToast(message: String)
    fun checkNetworkConn(): Boolean
}