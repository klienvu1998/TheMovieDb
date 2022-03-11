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
public final class CommentFragment_MembersInjector implements MembersInjector<CommentFragment> {
  private final Provider<MainViewModelFactory> providerFactoryProvider;

  public CommentFragment_MembersInjector(Provider<MainViewModelFactory> providerFactoryProvider) {
    this.providerFactoryProvider = providerFactoryProvider;
  }

  public static MembersInjector<CommentFragment> create(
      Provider<MainViewModelFactory> providerFactoryProvider) {
    return new CommentFragment_MembersInjector(providerFactoryProvider);
  }

  @Override
  public void injectMembers(CommentFragment instance) {
    injectProviderFactory(instance, providerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.hyvu.themoviedb.view.home.CommentFragment.providerFactory")
  public static void injectProviderFactory(CommentFragment instance,
      MainViewModelFactory providerFactory) {
    instance.providerFactory = providerFactory;
  }
}