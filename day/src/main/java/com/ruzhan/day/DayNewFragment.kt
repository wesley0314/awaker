package com.ruzhan.day

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ruzhan.common.OnRefreshHelper
import com.ruzhan.day.adapter.DayNewAdapter
import kotlinx.android.synthetic.main.day_frag_new.*

class DayNewFragment : Fragment() {

    companion object {

        private const val DAY_TAG = "DAY_TAG"

        @JvmStatic
        fun newInstance(dayTag: String): DayNewFragment {
            val args = Bundle()
            args.putString(DAY_TAG, dayTag)
            val frag = DayNewFragment()
            frag.arguments = args
            return frag
        }
    }

    private var dayTag = ""
    private var dayViewModel: DayViewModel? = null
    private val dayNewAdapter = DayNewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dayTag = arguments?.getString(DAY_TAG, "") ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.day_frag_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dayViewModel = ViewModelProviders.of(activity!!)
                .get(DayViewModel::class.java)
        this.dayViewModel = dayViewModel
        initData()
        initListener()
        initLiveData(dayViewModel)
    }

    private fun initData() {
        recyclerView.adapter = dayNewAdapter
        val tagDayNewList = dayViewModel?.getRefreshTagDayModelList(dayTag)
        progressBar.visibility = if (tagDayNewList != null && tagDayNewList.isNotEmpty())
            View.GONE else View.VISIBLE
        dayNewAdapter.setRefreshData(tagDayNewList)
    }

    private fun initListener() {
        OnRefreshHelper.setOnRefreshStatusListener(swipeRefreshLayout, recyclerView,
                object : OnRefreshHelper.OnRefreshStatusListener {
                    override fun onLoadMore() {
                        dayViewModel?.refreshDayNewList()
                    }

                    override fun onRefresh() {
                        dayViewModel?.loadMoreDayNewList()
                    }
                })
    }

    private fun initLiveData(dayViewModel: DayViewModel) {
        dayViewModel.refreshDayNewLiveData.observe(this, Observer { dayNewList ->
            if (dayNewList != null) {
                progressBar.visibility = View.GONE
                val tagDayNewList = dayViewModel.getRefreshTagDayModelList(dayTag)
                dayNewAdapter.setRefreshData(tagDayNewList)
            }
        })
        dayViewModel.loadMoreDayNewLiveData.observe(this, Observer { dayNewList ->
            if (dayNewList != null) {
                progressBar.visibility = View.GONE
                val tagDayNewList = dayViewModel.getLoadMoreTagDayModelList(dayTag)
                dayNewAdapter.setLoadMoreData(tagDayNewList)
            }
        })
        dayViewModel.loadStatusLiveData.observe(this, Observer { isLoadStatus ->
            if (isLoadStatus != null && !isLoadStatus) {
                swipeRefreshLayout.isRefreshing = isLoadStatus
            }
        })
    }
}