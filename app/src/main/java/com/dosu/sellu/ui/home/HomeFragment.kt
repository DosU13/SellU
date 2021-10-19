package com.dosu.sellu.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.databinding.HomeFragmentBinding
import com.dosu.sellu.ui.stat.model.Stat
import com.dosu.sellu.ui.stat.util.StatListener
import com.dosu.sellu.ui.stat.viewmodel.StatViewModel
import com.dosu.sellu.ui.stat.viewmodel.StatViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class HomeFragment : Fragment(), StatListener, DIAware {
    override val di: DI by closestDI()
    private val statViewModelFactory: StatViewModelFactory by instance()
    private lateinit var statViewModel: StatViewModel
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        statViewModel = ViewModelProvider(this, statViewModelFactory).get(StatViewModel::class.java)
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        statViewModel.setListener(this)
        statViewModel.loadData()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun dataReady() {
        statViewModel.statistics()
    }

    override fun singleStat(singleStat: Stat) {
        binding.sells.text = singleStat.money.toString()
        binding.income.text = (singleStat.money - singleStat.outcome).toString()
    }

    override fun statsReady(stats: MutableList<Stat>) {
        binding.canvas.drawMoney(stats)
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Log.e("HomeFragment", responseBody?.detail?:"no details")
    }
}