package com.github.ashutoshgngwr.noice.di

import android.content.Context
import com.github.ashutoshgngwr.noice.provider.GitHubReviewFlowProvider
import com.github.ashutoshgngwr.noice.provider.PlaystoreReviewFlowProvider
import com.github.ashutoshgngwr.noice.provider.ReviewFlowProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReviewFlowProviderModule {

  @Provides
  @Singleton
  fun reviewFlowProvider(@ApplicationContext context: Context): ReviewFlowProvider {
    if (isGoogleMobileServiceAvailable(context)) {
      return PlaystoreReviewFlowProvider
    }

    return GitHubReviewFlowProvider
  }
}
