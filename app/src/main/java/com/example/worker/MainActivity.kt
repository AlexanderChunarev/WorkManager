package com.example.worker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequest
import androidx.lifecycle.Observer
import com.example.worker.DataAdapter.Companion.primeNumbers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ADAPTER_STATE)) {
                list_of_primes.layoutManager?.onRestoreInstanceState(
                    savedInstanceState.getParcelable(
                        ADAPTER_STATE
                    )
                )
            }
            if (savedInstanceState.containsKey(RUNNING_STATE)
                && savedInstanceState.getBoolean(RUNNING_STATE)) {
                perform()
            }
        }
        list_of_primes.adapter = adapter
    }

    private fun perform() {
        request = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(createInputData())
            .build()

        WorkManager.getInstance(this).enqueue(request!!)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request!!.id).observe(this, Observer {
            it?.progress?.getInt(PROGRESS, DEFAULT_VALUE)?.apply {
                if (this != DEFAULT_VALUE) {
                    adapter.addItem(this)
                }
            }
        })
    }

    private fun createInputData(): Data {
        if (primeNumbers.isNotEmpty()) {
            currPosition = primeNumbers[primeNumbers.lastIndex] + 1
        }
        return Data.Builder()
            .putInt(CURRENT_POSITION, currPosition)
            .build()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(RUNNING_STATE, isRunning)
        outState.putParcelable(ADAPTER_STATE, list_of_primes.layoutManager?.onSaveInstanceState())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.play_btn -> {
                if (!isExecuting()) {
                    isRunning = true
                    perform()
                } else {
                    Toast.makeText(this, "Task is ${getState()}", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.stop_btn -> {
                isRunning = false
                cancel()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    private fun isExecuting(): Boolean {
        getState().apply {
            return if (this != null) {
                this == WorkInfo.State.RUNNING
            } else false
        }
    }

    private fun getState() =
        request?.id?.let { WorkManager.getInstance(this).getWorkInfoById(it).get()?.state }

    private fun cancel() {
        WorkManager.getInstance(this).cancelAllWork()
    }

    companion object {
        const val ADAPTER_STATE = "adapterState"
        const val RUNNING_STATE = "taskIsRunning"
        const val CURRENT_POSITION = "current_position"
        const val PROGRESS = "getPrimeNumber"
        const val DEFAULT_VALUE = 99
        private var currPosition: Int = 2
        private val adapter by lazy { DataAdapter() }
        private var request: OneTimeWorkRequest? = null
        private var isRunning = false
    }
}
