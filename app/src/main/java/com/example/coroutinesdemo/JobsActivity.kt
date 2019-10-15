package com.example.coroutinesdemo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_jobs.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class JobsActivity : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val PROGRESS_TIME = 4000 //ms
    private lateinit var job: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)

        job_button.setOnClickListener {
            if(!::job.isInitialized){
                initjob()
            }
            job_progress_bar.startJobOrCancel(job)
        }
    }

    fun resetjob(){
        if(job.isActive || job.isCompleted){
            job.cancel(CancellationException("Resetting job"))
        }
        initjob()
    }

    fun initjob(){
        job_button.text = "Start Job #1"
        updateJobCompleteTextView("")
        job = Job()
        job.invokeOnCompletion {
            it?.message.let{
                var msg = it
                if(msg.isNullOrBlank()){
                    msg = "Unknown cancellation error."
                }
                println("${job} was cancelled. Reason: ${msg}")
                showToast(msg)
            }
        }
        job_progress_bar.max = PROGRESS_MAX
        job_progress_bar.progress = PROGRESS_START
    }


    fun ProgressBar.startJobOrCancel(job: Job){
        if(this.progress > 0){
            println("${job} is already active. Cancelling...")
            resetjob()
        }
        else{
            job_button.text = "Cancel Job #1"
            CoroutineScope(IO + job).launch{
                println("coroutine ${this} is activated with job ${job}.")

                for(i in PROGRESS_START..PROGRESS_MAX){
                    delay((PROGRESS_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }
                updateJobCompleteTextView("Job is complete!")
            }
        }
    }

    private fun updateJobCompleteTextView(text: String){
        GlobalScope.launch (Main){
            job_complete_text.setText(text)
        }
    }

    private fun showToast(text: String){
        GlobalScope.launch (Main){
            Toast.makeText(this@JobsActivity, text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
