package com.hyvu.themoviedb.di

import dagger.Module

@Module(subcomponents = [MainComponent::class, MovieImageComponent::class, LoginComponent::class])
class AppSubComponent