package com.example.my_timer_app

import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.KeyData.CHANNEL
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant


class MainActivity : FlutterActivity() {

    var TAG = "MainActivity"

    private val TIMER_CHANNEL = "com.example.my_timer_app/timer_channel"

    private val timerEventChannel = "com.example.restart_trigger/timer_event_channel"
    private var eventChannel: EventChannel? = null
    private var eventSink: EventChannel.EventSink? = null

    override
    fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)

        // Register the method channel
        MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), TIMER_CHANNEL)
            .setMethodCallHandler { call, result ->
                if (call.method.equals("getElapsedTime")) {
                    result.success(mapOf("mElapsedTime" to getProcessedTime()))
                } else {
                    result.notImplemented()
                }
            }


        eventChannel = EventChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), timerEventChannel)
        eventChannel!!.setStreamHandler(object :EventChannel.StreamHandler{
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                eventSink = events
            }

            override fun onCancel(arguments: Any?) {
                eventSink = null
            }

        })
    }

    fun getProcessedTime(): Long {
        val prevTime = SharedPrefUtils.getValue(this, SharedPrefUtils.START_DATE_TIME)
        val currentTime = SystemClock.elapsedRealtime()
        val elapsedTime = currentTime - prevTime

        val tt = elapsedTime / 1000

        Log.e(TAG, "gpt startTime:$tt")

        return tt
    }

    fun trackTime(from: String) {
        val startTime = SystemClock.elapsedRealtime()
        SharedPrefUtils.setVal(this, SharedPrefUtils.START_DATE_TIME, startTime)
        Log.e(TAG, "$from startTime:$startTime")
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun onPause() {
        trackTime("onPause");
        showMessage("onPause");
        super.onPause()
    }

    override fun onResume() {
        eventSink?.success(true)
        showMessage("onResume")
        super.onResume()
    }

    override fun onDestroy() {
        trackTime("onDestroy");
        super.onDestroy()
    }

    fun showMessage(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
