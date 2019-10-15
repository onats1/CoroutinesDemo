package com.example.coroutinesdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    val result1 = "Result 1"
    val RESULT_2 = "Result 2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {

            // Scopes: Main, IO, Default
            CoroutineScope(IO).launch {
                makeApiRequest()
            }


        }
    }

    private suspend fun makeApiRequest(){
        val value = getResult1()

        setTextOnMainThread(value)

        val value2 = getResult2()
        setTextOnMainThread(value2)
    }

    private suspend fun getResult2(): String {
        delay(1000)
        return RESULT_2
    }

    private fun setText(text: String){
        val newText = textView.text.toString() + text

        textView.text = newText

    }

    private suspend fun setTextOnMainThread(text: String){
        withContext(Main){
            setText(text)
        }
    }

    private suspend fun getResult1(): String{
        showLog(result1)
        delay(1000)
        return result1
    }

    private fun showLog(methodName: String){
        Log.d("${methodName }:", Thread.currentThread().name)
    }

}
