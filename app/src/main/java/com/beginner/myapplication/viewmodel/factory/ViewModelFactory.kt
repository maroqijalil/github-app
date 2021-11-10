package com.beginner.myapplication.viewmodel.factory

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.beginner.myapplication.features.userdetail.UserFollTabFragment
import com.beginner.myapplication.viewmodel.UserFollViewModel

class ViewModelFactory(
    private val ctx: Any,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return UserFollViewModel(ctx as UserFollTabFragment, handle) as T
    }
}