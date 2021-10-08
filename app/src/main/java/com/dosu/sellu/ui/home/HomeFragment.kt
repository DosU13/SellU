package com.dosu.sellu.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dosu.sellu.R
import com.dosu.sellu.databinding.HomeFragmentBinding
import com.dosu.sellu.ui.home.SimpleSectionedRecyclerViewAdapter.Section

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = HomeFragmentBinding.inflate(inflater, container, false)

        val sCheeseStrings = mutableListOf("1", "2", "3", "4", "5", "1", "2", "3", "4", "5", "1", "2", "3", "4", "5", "1", "2", "3", "4", "5")

        //Your RecyclerView
        val mRecyclerView = binding.recyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        //Your RecyclerView.Adapter
        val mAdapter = SimpleAdapter(requireContext(), sCheeseStrings)

        //This is the code to provide a sectioned list
        val sections: MutableList<Section> =
            ArrayList()

        //Sections
        sections.add(Section(0, "Section 1"))
        sections.add(Section(5, "Section 2"))
        sections.add(Section(12, "Section 3"))
        sections.add(Section(14, "Section 4"))
        sections.add(Section(20, "Section 5"))

        //Add your adapter to the sectionAdapter
        val mSectionedAdapter =
            SimpleSectionedRecyclerViewAdapter(requireContext(), R.layout.section, R.id.section_text, mAdapter)
        mSectionedAdapter.setSections(sections.toTypedArray())

        //Apply this adapter to the RecyclerView
        mRecyclerView.adapter = mSectionedAdapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}