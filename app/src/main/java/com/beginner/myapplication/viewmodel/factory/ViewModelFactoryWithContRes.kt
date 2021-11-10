package com.beginner.myapplication.viewmodel.factory

import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.beginner.myapplication.viewmodel.UserFavViewModel
import com.beginner.myapplication.viewmodel.UserViewModel

class ViewModelFactoryWithContRes(
    private val ctx: Context,
    owner: SavedStateRegistryOwner,
    private val contRes: ContentResolver,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(ctx, handle, contRes) as T
            }
            modelClass.isAssignableFrom(UserFavViewModel::class.java) -> {
                UserFavViewModel(handle, contRes) as T
            }
            else -> throw IllegalArgumentException("Unknown View Model Class")
        }
    }
}