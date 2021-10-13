package com.dosu.sellu.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.databinding.HomeFragmentBinding
import com.dosu.sellu.ui.home.model.Stat
import com.dosu.sellu.ui.home.util.HomeListener
import com.dosu.sellu.ui.home.viewmodel.HomeViewModel
import com.dosu.sellu.ui.home.viewmodel.HomeViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import com.dosu.sellu.util.price
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class HomeFragment : Fragment(), HomeListener, DIAware {
    override val di: DI by closestDI()
    private val homeViewModelFactory: HomeViewModelFactory by instance()
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        homeViewModel.setListener(this)
        homeViewModel.loadData()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun dataReady() {
        homeViewModel.statistics()
        homeViewModel.singleStat()
    }

    override fun singleStat(singleStat: Stat) {
        binding.sells.text = singleStat.money.toString()
        binding.income.text = (singleStat.money - singleStat.outcome).toString()
    }

    override fun statsReady(stats: MutableList<Stat>) {
        binding.canvas.setValues(stats)
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Log.e("HomeFragment", responseBody?.detail?:"no details")
    }
}