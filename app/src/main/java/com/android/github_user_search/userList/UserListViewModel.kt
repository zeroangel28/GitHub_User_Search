package com.android.github_user_search.userList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.github_user_search.data.DataSource
import com.android.github_user_search.data.User

class UserListViewModel(val dataSource: DataSource) : ViewModel() {

    val userLiveData = dataSource.getUserList()

    fun addUser(user: User) {
        dataSource.addUser(user)
    }

    fun clearUser(){
        dataSource.clearUser()
    }
}

class UserListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserListViewModel(
                    dataSource = DataSource.getDataSource(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}