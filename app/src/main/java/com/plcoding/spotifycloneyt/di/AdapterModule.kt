package com.plcoding.spotifycloneyt.di

import android.view.LayoutInflater
import android.view.ViewGroup
import com.plcoding.spotifycloneyt.databinding.ListItemBinding
import com.plcoding.spotifycloneyt.databinding.SwipeItemBinding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(ActivityComponent::class)
object AdapterModule {
    @Provides
    fun provideListItemBinding(inflater: LayoutInflater, parent: ViewGroup?): ListItemBinding {
        return ListItemBinding.inflate(inflater, parent, false)
    }

    @Provides
    fun provideSwipeItemBinding(inflater: LayoutInflater, parent: ViewGroup?): SwipeItemBinding {
        return SwipeItemBinding.inflate(inflater, parent, false)
    }
}