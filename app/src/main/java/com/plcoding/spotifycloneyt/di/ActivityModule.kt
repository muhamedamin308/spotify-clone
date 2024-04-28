package com.plcoding.spotifycloneyt.di

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun provideViewGroup(@ApplicationContext context: Context): ViewGroup {
        return (context as Activity).findViewById(android.R.id.content)
    }
}
