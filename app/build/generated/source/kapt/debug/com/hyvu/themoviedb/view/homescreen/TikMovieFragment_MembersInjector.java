// Generated by Dagger (https://dagger.dev).
package com.hyvu.themoviedb.view.homescreen;

import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import javax.inject.Provider;

@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class TikMovieFragment_MembersInjector implements MembersInjector<TikMovieFragment> {
  private final Provider<MainViewModelFactory> providerFactoryProvider;

  public TikMovieFragment_MembersInjector(Provider<MainViewModelFactory> providerFactoryProvider) {
    this.providerFactoryProvider = providerFactoryProvider;
  }

  public static MembersInjector<TikMovieFragment> create(
      Provider<MainViewModelFactory> providerFactoryProvider) {
    return new TikMovieFragment_MembersInjector(providerFactoryProvider);
  }

  @Override
  public void injectMembers(TikMovieFragment instance) {
    injectProviderFactory(instance, providerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.hyvu.themoviedb.view.home.TikMovieFragment.providerFactory")
  public static void injectProviderFactory(TikMovieFragment instance,
      MainViewModelFactory providerFactory) {
    instance.providerFactory = providerFactory;
  }
}
