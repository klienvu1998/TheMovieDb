package com.hyvu.themoviedb.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.databinding.FragmentHomeContainerBinding
import com.hyvu.themoviedb.utils.Constraints
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.MovieInfoViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class HomeContainerFragment : Fragment() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[HomeViewModel::class.java]
    }

    private lateinit var mBinding: FragmentHomeContainerBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_home_container, container, false)
        mBinding = FragmentHomeContainerBinding.bind(v)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val movieHomeFragment = MoviesHomeFragment()
        movieHomeFragment.setListener(listenerHomeFragment)
        childFragmentManager.beginTransaction().replace(R.id.home_container, movieHomeFragment)
                .commit()
//        findNavController().navigate(R.id.action_homeContainerFragment_to_moviesHomeFragment)
    }

    private val listenerHomeFragment = object : MoviesHomeFragment.Listener {
        override fun onClickedSeeAll(genre: Genre) {
            val fragment = MoviesByGenreFragment.newInstance(genre)
            childFragmentManager.beginTransaction()
                .add(R.id.home_container, fragment)
                .addToBackStack(MoviesByGenreFragment::class.java.simpleName)
                .commit()
        }

        override fun onClickTrending() {
            val fragment = MoviesByGenreFragment.newInstance(Constraints.TRENDING_MOVIE)
            childFragmentManager.beginTransaction()
                    .add(R.id.home_container, fragment)
                    .addToBackStack(MoviesByGenreFragment::class.java.simpleName)
                    .commit()
        }
    }

}