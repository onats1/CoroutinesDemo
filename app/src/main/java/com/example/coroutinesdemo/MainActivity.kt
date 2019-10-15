package com.example.coroutinesdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    val REQUEST_TIMEOUT = 1900L

    val result1 = "Result 1"
    val RESULT_2 = "Result 2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {

            // Scopes: Main, IO, Default
            CoroutineScope(IO).launch {
                //makeApiRequest()

                setText("Clicked!")

                makeApiRequestWithTimeout()
            }

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

    private suspend fun getResult2(): String {
        delay(1000)
        return RESULT_2
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

    private suspend fun getResult1(): String{
        showLog(result1, "Result 1 works!")
        delay(1000)
        return result1
    }

    private fun showLog(methodName: String, description: String){
        Log.d("${methodName }:", Thread.currentThread().name)
        Log.d("${methodName }:", description)
    }

}
