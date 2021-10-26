package com.dosu.sellu.ui.history

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dosu.sellu.data.network.product.model.Product
import com.dosu.sellu.data.network.selling.model.Selling
import com.dosu.sellu.databinding.HistoryFragmentBinding
import com.dosu.sellu.ui.history.util.HistoryListener
import com.dosu.sellu.ui.history.util.HistoryRecyclerViewAdapter
import com.dosu.sellu.ui.history.viewmodel.HistoryViewModel
import com.dosu.sellu.ui.history.viewmodel.HistoryViewModelFactory
import com.dosu.sellu.util.ErrorResponse
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI
import org.kodein.di.instance

class HistoryFragment : Fragment(), DIAware, HistoryListener {
    override val di: DI by closestDI()
    private val historyViewModelFactory: HistoryViewModelFactory by instance()
    private lateinit var historyViewModel: HistoryViewModel
    private var _binding: HistoryFragmentBinding? = null
    private val binding: HistoryFragmentBinding get() = _binding!!
    private lateinit var adapter: HistoryRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = HistoryFragmentBinding.inflate(inflater, container, false)
        historyViewModel = ViewModelProvider(this, historyViewModelFactory).get(HistoryViewModel::class.java)
        historyViewModel.setListener(this)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter =  HistoryRecyclerViewAdapter(context, historyViewModel)
        recyclerView.adapter = adapter
        historyViewModel.getHSellingList()
        return binding.root
    }

    override fun getSellingList(products: List<Product>, sellingList: List<Selling>) {
        adapter.updateSectionedList(products, sellingList)
    }

    override fun anyError(errorCode: Int?, responseBody: ErrorResponse?) {
        Toast.makeText(context, "error:$errorCode ${responseBody?.detail}", Toast.LENGTH_LONG).show()
    }
}