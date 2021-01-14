package com.android.github_user_search.data


import android.content.Context
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DataSource(context: Context) {
    private val listUser = ArrayList<User>()
    private val initialUserList = ArrayList<User>()
    private val UsersLiveData = MutableLiveData(initialUserList)

    fun addUser(user: User){
        val currentList = UsersLiveData.value
        if (currentList != null) {
            Log.i("DataSorce", user.name)
            currentList.add(user)
        }
        UsersLiveData.postValue(currentList)
    }

    fun clearUser() {
        val currentList = UsersLiveData.value
        if (currentList != null) {
            currentList.clear()
            UsersLiveData.postValue(currentList)
        }
    }

    fun getUserList(): LiveData<ArrayList<User>> {
        return UsersLiveData
    }

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(context: Context): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(context)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}