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
public final class UserFragment_MembersInjector implements MembersInjector<UserFragment> {
  private final Provider<MainViewModelFactory> providerFactoryProvider;

  public UserFragment_MembersInjector(Provider<MainViewModelFactory> providerFactoryProvider) {
    this.providerFactoryProvider = providerFactoryProvider;
  }

  public static MembersInjector<UserFragment> create(
      Provider<MainViewModelFactory> providerFactoryProvider) {
    return new UserFragment_MembersInjector(providerFactoryProvider);
  }

  @Override
  public void injectMembers(UserFragment instance) {
    injectProviderFactory(instance, providerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.hyvu.themoviedb.view.home.UserFragment.providerFactory")
  public static void injectProviderFactory(UserFragment instance,
      MainViewModelFactory providerFactory) {
    instance.providerFactory = providerFactory;
  }
}
