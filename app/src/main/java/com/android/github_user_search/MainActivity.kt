package com.android.github_user_search

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.android.github_user_search.data.User
import com.android.github_user_search.userList.UserListViewModel
import com.android.github_user_search.userList.UserListViewModelFactory
import com.android.github_user_search.userList.UsersAdapter
import com.android.github_user_search.gituserdata.GitUserData
import kotlinx.coroutines.*
import java.io.FileNotFoundException

private const val TAG = "MainActivity"
private const val NO_ERROR_USER_DATA = 0
private const val ERROR_USER_DATA = 1

class MainActivity : AppCompatActivity() {
    private val userListViewModel by viewModels<UserListViewModel> {
        UserListViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val searchEditText: EditText = findViewById(R.id.header_edit)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val usersAdapter = UsersAdapter(this)
        val gitUserData = GitUserData()
        var count = 1
        var searchString = ""
        var error = NO_ERROR_USER_DATA

        recyclerView.adapter = usersAdapter
        showNoNetwork()

        fun showError(){
            Toast.makeText(this, "Http Rate Error! Please Wait.", Toast.LENGTH_SHORT).show()
        }

        fun initSearch(){
            error = NO_ERROR_USER_DATA
            userListViewModel.clearUser()
            usersAdapter.notifyDataSetChanged()
            count = 1
        }

        fun setGitUserSearchData(page: Int, searchString: String){
            gitUserData.setsearchPage(page)
            gitUserData.setsearchString(searchString)
            gitUserData.clearArrayList()
        }

        fun getUserDaatFromGitHub(page: Int, searchString: String){
            setGitUserSearchData(page, searchString)
            val userList = gitUserData.getUserData()
            for (i in 0..userList.size - 1) {
                userListViewModel.addUser(userList[i])
            }
        }

        fun searchNextPageConditionIfHundredDoSearch(): Int{
            return (usersAdapter.position + 1) % ((count - 1) * 100)
        }

        userListViewModel.userLiveData.observe(this, object : Observer<ArrayList<User>> {
            override fun onChanged(userList: ArrayList<User>) {
                usersAdapter.submitList(userList as MutableList<User>)
            }
        })

        searchEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if ((event.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) && hasInternet()) {
                    searchString = searchEditText.text.toString()
                    initSearch()
                    runBlocking {
                        launch(Dispatchers.Default) {
                            try {
                                getUserDaatFromGitHub(count, searchString)
                            } catch (e: FileNotFoundException) {
                                Log.e("Thread", "Error: No Network")
                                error = ERROR_USER_DATA
                            }
                            count++
                        }
                        if(error != NO_ERROR_USER_DATA) {
                            showError()
                        }
                    }
                    return true
                }
                showNoNetwork()
                return false
            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(((newState == SCROLL_STATE_IDLE || newState == SCROLL_STATE_DRAGGING || newState == SCROLL_STATE_SETTLING) && usersAdapter.position !=0) && hasInternet()) {
                    if (searchNextPageConditionIfHundredDoSearch() == 0) {
                        runBlocking {
                            launch(Dispatchers.IO) {
                                try {
                                    getUserDaatFromGitHub(count, searchString)
                                } catch (e: FileNotFoundException) {
                                    Log.e("Thread", "Error: No Network")
                                    error = ERROR_USER_DATA
                                }
                                count++
                            }
                            if (error == NO_ERROR_USER_DATA) {
                                usersAdapter.notifyDataSetChanged()
                            } else {
                                showError()
                            }
                        }
                    }
                }
            }
        })
    }

    fun hasInternet(): Boolean{
        var result = false
        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info: NetworkInfo? = cm.getActiveNetworkInfo()
        if (info == null || !info.isConnected){
            return false
        }else{
            if(!info.isAvailable()){
                result = false
            }else{
                result = true
            }
        }
        return result
    }


    fun showNoNetwork() {
        if (!hasInternet()) {
            Toast.makeText(this, "Please Open the Network", Toast.LENGTH_SHORT).show()
        }
    }
}
