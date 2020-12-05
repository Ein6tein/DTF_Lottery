package com.dtf.lottery.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dtf.lottery.model.Comment
import com.dtf.lottery.model.User
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*
import kotlin.coroutines.CoroutineContext

class NetworkRepository : CoroutineScope {

    companion object {
        private const val APIKEY = "YOUR_API_KEY"
        private const val ONE_WEEK = 7 * 24 * 60 * 60 * 1000
        private val FROM = Calendar.getInstance()
        private val TO = Calendar.getInstance()
    }

    private val _users: MutableLiveData<List<User>> = MutableLiveData()
    val users: LiveData<List<User>>
        get() = _users

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    fun start() {
        FROM.set(2020, 11, 7, 8, 0, 0)
        TO.set(2020, 11, 7, 20, 0, 0)
        "https://api.dtf.ru/v1.9/entry/275911/comments"
            .httpGet()
            .header("X-Device-Token" to APIKEY)
            .responseString { _, _, result ->
                when (result) {
                    is Result.Failure -> {}
                    is Result.Success -> {
                        handlePost(result)
                    }
                }
            }
    }

    private fun handlePost(result: Result<String, FuelError>) {
        val data = result.get()
        val jsonObject = JSONObject(data)
        val jsonArray = jsonObject.getJSONArray("result")
        val comments = mutableListOf<Comment>()
        for (i in 0 until jsonArray.length()) {
            val commentJson = jsonArray.getJSONObject(i)
            comments.add(
                Comment(
                    commentJson.optInt("id"),
                    commentJson.optLong("date"),
                    commentJson.optString("text"),
                    commentJson.optJSONObject("author")?.optInt("id") ?: 0
                )
            )
        }
        if (comments.isEmpty()) {
            _users.postValue(emptyList())
        } else {
            convertComments(comments)
        }
    }

    private fun convertComments(comments: MutableList<Comment>) {
        val users = mutableListOf<User>()
        for (comment in comments) {
            val userIndex = users.indexOfFirst { it.id == comment.userId }
            if (userIndex > -1 ||
                (comment.timestamp < FROM.timeInMillis || comment.timestamp > TO.timeInMillis || comment.text != "+")) continue
            "https://api.dtf.ru/v1.9/user/${comment.userId}"
                .httpGet()
                .header("X-Device-Token" to APIKEY)
                .responseString { _, _, result ->
                    when (result) {
                        is Result.Failure -> {}
                        is Result.Success -> {
                            handleUser(result, users)
                        }
                    }
                }
            Thread.sleep(400)
        }
        launch {
            withContext(Dispatchers.Main) {
                val oneWeekAgo = Calendar.getInstance().timeInMillis - ONE_WEEK
                _users.postValue(users.filter { it.rating > -26 && it.dateCreated < oneWeekAgo })
            }
        }
    }

    private fun handleUser(result: Result<String, FuelError>, users: MutableList<User>) {
        val data = result.get()
        val json = JSONObject(data)
        val userJson = json.getJSONObject("result")
        val user = User(
            userJson.optInt("id"),
            userJson.optString("name"),
            userJson.optLong("created") * 1_000,
            userJson.optString("avatar_url"),
            userJson.optInt("karma")
        )
        val index = users.indexOfFirst { it.id == user.id }
        if (index == -1) users.add(user)
    }
}