package com.pegahjadidi.todoapp

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun hideKeyBoard(activity: Activity){
    val inputMethodManager : InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusView = activity.currentFocus
    currentFocusView.let { inputMethodManager.hideSoftInputFromWindow(
        currentFocusView?.windowToken , InputMethodManager.HIDE_NOT_ALWAYS
    ) }
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
    observe(lifecycleOwner,object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this) }

    })
}