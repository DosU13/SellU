package com.dosu.sellu.ui.home

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import java.util.*

class SimpleSectionedRecyclerViewAdapter(
    private val context: Context, private val sectionResourceId: Int, private val textResourceId: Int,
    private val baseAdapter: SimpleAdapter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mValid = true
    private val mSections = SparseArray<Section?>()
    companion object {
        private const val SECTION_TYPE = 0
    }
    init {
        baseAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                mValid = baseAdapter.itemCount > 0
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                mValid = baseAdapter.itemCount > 0
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                mValid = baseAdapter.itemCount > 0
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                mValid = baseAdapter.itemCount > 0
                notifyItemRangeRemoved(positionStart, itemCount)
            }
        })
    }

    class SectionViewHolder(view: View, mTextResourceid: Int) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(mTextResourceid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, typeView: Int): RecyclerView.ViewHolder {
        return if (typeView == SECTION_TYPE) {
            val view: View = LayoutInflater.from(context).inflate(sectionResourceId, parent, false)
            SectionViewHolder(view, textResourceId)
        } else {
            baseAdapter.onCreateViewHolder(parent, typeView - 1)
        }
    }

    override fun onBindViewHolder(sectionViewHolder: RecyclerView.ViewHolder, position: Int) {
        if (isSectionHeaderPosition(position)) {
            (sectionViewHolder as SectionViewHolder).title.text =
                mSections[position]!!.title
        } else {
            baseAdapter.onBindViewHolder(sectionViewHolder as SimpleAdapter.SimpleViewHolder, sectionedPositionToPosition(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSectionHeaderPosition(position)) SECTION_TYPE else baseAdapter.getItemViewType(
            sectionedPositionToPosition(position)
        ) + 1
    }

    class Section(var firstPosition: Int, var title: CharSequence) {
        var sectionedPosition = 0

    }

    fun setSections(sections: Array<Section>) {
        mSections.clear()
        Arrays.sort(sections) { o, o1 ->
            when {
                o.firstPosition == o1.firstPosition -> 0
                o.firstPosition < o1.firstPosition -> -1
                else -> 1
            }
        }
        for ((offset, section) in sections.withIndex()) {
            section.sectionedPosition = section.firstPosition + offset
            mSections.append(section.sectionedPosition, section)
        }
        notifyDataSetChanged()
    }

    fun positionToSectionedPosition(position: Int): Int {
        var offset = 0
        for (i in 0 until mSections.size()) {
            if (mSections.valueAt(i)!!.firstPosition > position) {
                break
            }
            ++offset
        }
        return position + offset
    }

    fun sectionedPositionToPosition(sectionedPosition: Int): Int {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION
        }
        var offset = 0
        for (i in 0 until mSections.size()) {
            if (mSections.valueAt(i)!!.sectionedPosition > sectionedPosition) {
                break
            }
            --offset
        }
        return sectionedPosition + offset
    }

    fun isSectionHeaderPosition(position: Int): Boolean {
        return mSections[position] != null
    }

    override fun getItemId(position: Int): Long {
        return if (isSectionHeaderPosition(position)) (Int.MAX_VALUE - mSections.indexOfKey(position)).toLong() else baseAdapter.getItemId(
            sectionedPositionToPosition(position)
        )
    }

    override fun getItemCount(): Int {
        return if (mValid) baseAdapter.itemCount + mSections.size() else 0
    }
}