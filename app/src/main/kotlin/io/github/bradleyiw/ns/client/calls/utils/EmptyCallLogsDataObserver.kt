package io.github.bradleyiw.ns.client.calls.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.bradleyiw.ns.databinding.FragmentDashboardBinding

class EmptyCallLogsDataObserver(
    private val recyclerView: RecyclerView,
    private val dashboardBinding: FragmentDashboardBinding
) : RecyclerView.AdapterDataObserver() {

    init {
        checkIfEmpty()
    }

    private fun checkIfEmpty() {
        val adapter = recyclerView.adapter
        if (adapter != null) {
            val emptyViewVisible = adapter.itemCount == 0
            dashboardBinding.callLogEmptyViewVisible = emptyViewVisible
            recyclerView.visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }

    override fun onChanged() {
        super.onChanged()
        checkIfEmpty()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.onItemRangeChanged(positionStart, itemCount)
        checkIfEmpty()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        checkIfEmpty()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        super.onItemRangeChanged(positionStart, itemCount, payload)
        checkIfEmpty()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        super.onItemRangeMoved(fromPosition, toPosition, itemCount)
        checkIfEmpty()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        super.onItemRangeRemoved(positionStart, itemCount)
        checkIfEmpty()
    }
}
