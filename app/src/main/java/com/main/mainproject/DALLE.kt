package com.main.mainproject

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch

class DALLE {
    private val API_KEY = "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"
    private val API_ENDPOINT = "https://api.openai.com/v1/images/generations"
    private var url: String? = null
    fun generateImage( Description: String): String? {
        val latch = CountDownLatch(1)
        val requestBody = """
            {
                "model": "image-alpha-001",
                "prompt": "$Description",
                "num_images":1,
                "size":"1024x1024",
                "response_format":"url"
            }
        """.trimIndent()
        val request = Request.Builder()
            .url(API_ENDPOINT)
            .header("Authorization", "Bearer $API_KEY")
            .header("Content-Type", "application/json")
            .post(requestBody.toRequestBody(MIME_TYPE_JSON))
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API", "Request failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body?.string()
                Log.d("API", "Response: $responseString")
                val json_contact: JSONObject = JSONObject(responseString)
                if(json_contact.has("error")){
                    url=""
                    latch.countDown()
                }else{
                var jsonarray_info: JSONArray = json_contact.getJSONArray("data")
                var json_objectdetail:JSONObject=jsonarray_info.getJSONObject(0)
                url = json_objectdetail.getString("url")
                latch.countDown()

            }}
        })
        latch.await()
        return url
    }
    private val MIME_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

}