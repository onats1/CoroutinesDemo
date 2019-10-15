package com.example.coroutinesdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    val REQUEST_TIMEOUT = 1900L

    val result1 = "Result 1"
    val RESULT_2 = "Result 2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            setText("Clicked!")
            // Scopes: Main, IO, Default
            CoroutineScope(IO).launch {
                //makeApiRequest()
                //makeApiRequestWithTimeout()

                //fakeApiRequest()
                //requestWithAsyncAwait()

                requestSequentialAsyncAndAwait()
            }

        }

        button2.setOnClickListener {
            val intent = Intent(this, JobsActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun fakeApiRequest() {

        val startTime = System.currentTimeMillis()

        val job = CoroutineScope(IO).launch{
            val job1 = launch {
                val time = measureTimeMillis {
                    println("Result 1 retrieved.")
                    val result1 = getResult1()
                    setTextOnMainThread(result1)
                }

                println("Result 1 gotten in $time ms")
            }

            val job2 = launch {
                val time = measureTimeMillis {
                    println("Result 1 retrieved.")
                    val result2 = getResult2()
                    setTextOnMainThread(result2)
                }

                println("Result 2 gotten in $time ms")
            }
        }

        job.invokeOnCompletion {
            println("Total time elapsed: ${System.currentTimeMillis() - startTime} ms")
        }

    }

    private suspend fun requestWithAsyncAwait(){

        withContext(IO) {
            val time = measureTimeMillis {
                val result1 = async {
                    getResult1()
                }

                val result2 = async {
                    getResult2()
                }

                setTextOnMainThread("Retrieved: ${result1.await()}")
                setTextOnMainThread("Retrieved: ${result2.await()}")
            }
            println("Results retrieved in $time ms.")

        }
    }

    private suspend fun requestSequentialAsyncAndAwait(){

        withContext(IO){

            val result1 = async {
                getResult1()
            }.await()

            val result2 = async {
                getAsyncResult2(result1)
            }.await()

            println("Final Result: $result2")
        }
    }

    private suspend fun makeApiRequest(){
        val value = getResult1()

        setTextOnMainThread(value)

        val value2 = getResult2()
        setTextOnMainThread(value2)
    }

    private suspend fun makeApiRequestWithTimeout(){
        val job = withTimeoutOrNull(REQUEST_TIMEOUT){

          //  val result1 = getResult1()
            setTextOnMainThread(getResult1())
       //     val result2 = getResult2()
            setTextOnMainThread(getResult2())

        }

        if(job == null){
            showLog("makeApiRequestWithTimeout", "Network request took longer than $REQUEST_TIMEOUT ms")
        }
    }

    private suspend fun getResult1(): String{
        showLog(result1, "Result 1 works!")
        delay(1000)
        return result1
    }

    private suspend fun getResult2(): String {
        delay(1700)
        return RESULT_2
    }

    private suspend fun getAsyncResult2(result1holder: String): String{
        delay(1700)
        if (result1holder == result1){
            return "$result1holder is called before $RESULT_2"
        }
        return "Incorrect first result."
    }

    private fun setText(text: String){
        val newText = textView.text.toString() + "\n $text"
        textView.text = newText
    }

    private suspend fun setTextOnMainThread(text: String){
        withContext(Main){
            setText(text)
        }
    }

    private fun showLog(methodName: String, description: String){
        Log.d("${methodName }:", Thread.currentThread().name)
        Log.d("${methodName }:", description)
    }

}
