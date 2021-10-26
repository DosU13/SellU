package com.dosu.sellu.ui.home

import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dosu.sellu.databinding.HomeFragmentBinding
import com.dosu.sellu.ui.home.model.Stat
import com.dosu.sellu.ui.home.util.StatListener
import com.dosu.sellu.ui.home.viewmodel.StatViewModel
import com.dosu.sellu.ui.home.viewmodel.StatViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import com.dosu.sellu.util.enlarge
import com.dosu.sellu.util.price
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

    override fun singleStat(todayStat: Stat?, allStat: Stat) {
        if(todayStat!=null) {
            binding.todaySold.text = todayStat.count.toString().enlarge
            binding.todayMoney.text = todayStat.money.price
            binding.todayIncome.text = (todayStat.money - todayStat.outcome).price
        }else{
            binding.todaySold.text = "0".enlarge
            binding.todayMoney.text = 0.0.price
            binding.todayIncome.text = 0.0.price
        }
        binding.allSold.text = allStat.count.toString().enlarge
        binding.allMoney.text = allStat.money.price
        binding.allIncome.text = (allStat.money - allStat.outcome).price
    }

    override fun statsReady(stats: MutableList<Stat>) {
        binding.canvas.drawMoney(stats)
    }

    override fun anyError(code: Int?, responseBody: ErrorResponse?) {
        Log.e("HomeFragment", responseBody?.detail?:"no details")
    }
}