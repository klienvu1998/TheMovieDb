package com.hyvu.themoviedb.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.databinding.FragmentHomeBinding
import com.hyvu.themoviedb.utils.Constraints
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.factory.HomeViewModelFactory

class HomeFragment : Fragment() {

    private val mViewModel by lazy {
        ViewModelProvider(this, HomeViewModelFactory()).get(HomeViewModel::class.java)
    }

    private lateinit var mBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_home, container, false)
        mBinding = FragmentHomeBinding.bind(v)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val moviesByGenreFragment = MoviesHomeFragment()
        moviesByGenreFragment.setListener(listenerHomeFragment)
        parentFragmentManager.beginTransaction().replace(R.id.home_container, moviesByGenreFragment)
            .commit()
    }

    private val listenerHomeFragment = object : MoviesHomeFragment.Listener {
        override fun onClickedSeeAll(genre: Genre) {
            val fragment = MoviesByGenreFragment.newInstance(genre)
            parentFragmentManager.beginTransaction()
                .add(R.id.home_container, fragment)
                .addToBackStack(MoviesByGenreFragment::class.java.simpleName)
                .commit()
        }

        override fun onClickTrending() {
            val fragment = MoviesByGenreFragment.newInstance(Constraints.TRENDING_MOVIE)
            parentFragmentManager.beginTransaction()
                    .add(R.id.home_container, fragment)
                    .addToBackStack(MoviesByGenreFragment::class.java.simpleName)
                    .commit()
        }
    }

}