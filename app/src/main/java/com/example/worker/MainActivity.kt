package com.example.worker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import androidx.work.WorkManager
import androidx.work.OneTimeWorkRequest
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SAVED_LAYOUT_STATE)) {
                primeNumbers =
                    savedInstanceState.getSerializable(SAVED_LAYOUT_STATE) as MutableList<Int>
            }
            if (savedInstanceState.containsKey(CURRENT_POSITION)) {
                currPosition = savedInstanceState.getInt(CURRENT_POSITION)
            }
            if (savedInstanceState.containsKey(RUNNING_STATE) && savedInstanceState.getBoolean(
                    RUNNING_STATE)) {
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
                if (this != DEFAULT_VALUE && !primeNumbers.contains(this)) {
                    adapter.addItem(this)
                }
            }
        })
    }

    private fun createInputData(): Data {
        return Data.Builder()
            .putInt(CURRENT_POSITION, currPosition)
            .build()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(RUNNING_STATE, isRunning)
        if (primeNumbers.isNotEmpty()) {
            outState.putInt(CURRENT_POSITION, primeNumbers[primeNumbers.lastIndex])
            outState.putSerializable(SAVED_LAYOUT_STATE, primeNumbers as Serializable)
        }
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
                    if (primeNumbers.isNotEmpty()) {
                        currPosition = primeNumbers[primeNumbers.lastIndex] + 1
                    }
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
        const val RUNNING_STATE = "taskIsRunning"
        const val CURRENT_POSITION = "current_position"
        const val SAVED_LAYOUT_STATE = "save_layout_state"
        const val PROGRESS = "getPrimeNumber"
        const val DEFAULT_VALUE = 99
        private var primeNumbers = mutableListOf<Int>()
        private var currPosition: Int = 2
        private val adapter by lazy { DataAdapter(primeNumbers) }
        private var request: OneTimeWorkRequest? = null
        private var isRunning = false
    }
}
