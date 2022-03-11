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
public final class UserHomeFragment_MembersInjector implements MembersInjector<UserHomeFragment> {
  private final Provider<MainViewModelFactory> providerFactoryProvider;

  public UserHomeFragment_MembersInjector(Provider<MainViewModelFactory> providerFactoryProvider) {
    this.providerFactoryProvider = providerFactoryProvider;
  }

  public static MembersInjector<UserHomeFragment> create(
      Provider<MainViewModelFactory> providerFactoryProvider) {
    return new UserHomeFragment_MembersInjector(providerFactoryProvider);
  }

  @Override
  public void injectMembers(UserHomeFragment instance) {
    injectProviderFactory(instance, providerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.hyvu.themoviedb.view.home.UserHomeFragment.providerFactory")
  public static void injectProviderFactory(UserHomeFragment instance,
      MainViewModelFactory providerFactory) {
    instance.providerFactory = providerFactory;
  }
}
