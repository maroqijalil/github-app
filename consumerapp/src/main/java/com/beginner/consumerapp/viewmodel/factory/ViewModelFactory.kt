package com.beginner.consumerapp.viewmodel.factory

import android.content.ContentResolver
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.beginner.consumerapp.viewmodel.UserFavViewModel

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val contentResolver: ContentResolver,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return UserFavViewModel(handle, contentResolver) as T
    }
}