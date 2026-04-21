package com.lugat.app.di

import android.content.Context
import com.lugat.app.data.LugatDatabase
import com.lugat.app.data.dao.LugatDao
import com.lugat.app.repository.LugatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LugatDatabase {
        return LugatDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideDao(database: LugatDatabase): LugatDao {
        return database.lugatDao()
    }

    @Provides
    @Singleton
    fun provideRepository(
        dao: LugatDao,
        @ApplicationContext context: Context
    ): LugatRepository {
        return LugatRepository(dao, context)
    }
}
