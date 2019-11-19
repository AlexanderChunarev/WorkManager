package com.example.worker

import android.content.Context
import androidx.work.*
import com.example.worker.MainActivity.Companion.CURRENT_POSITION
import com.example.worker.MainActivity.Companion.DEFAULT_VALUE
import com.example.worker.MainActivity.Companion.PROGRESS
import java.util.concurrent.TimeUnit

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        var currentPosition = inputData.getInt(CURRENT_POSITION, DEFAULT_VALUE)
        while (!this.isStopped && currentPosition < Short.MAX_VALUE) {
            if (isPrime(currentPosition)) {
                setProgressAsync(Data.Builder().putInt(PROGRESS, currentPosition).build())
                TimeUnit.MILLISECONDS.sleep(150)
            }
            currentPosition++
        }
        return Result.success()
    }

    private fun isPrime(number: Int): Boolean {
        if (number <= 1) {
            return false
        }
        for (i in 2 until number)
            if (number % i == 0) {
                return false

            }
        return true
    }
}