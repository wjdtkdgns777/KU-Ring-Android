package com.ku_stacks.ku_ring.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.ku_stacks.ku_ring.R
import com.ku_stacks.ku_ring.databinding.ActivitySearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SearchActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val searchViewModel by viewModels<SearchViewModel>()

    private var currentPage = noticeSearchPage

    private val pageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            Timber.e("pageSelect detected")
            when (position) {
                0 -> {
                    currentPage = noticeSearchPage
                    if (searchViewModel.noticeList.value?.isEmpty() == false) {
                        hideAdviceText()
                    } else {
                        showAdviceText()
                    }
                }
                1 -> {
                    currentPage = staffSearchPage
                    if (searchViewModel.staffList.value?.isEmpty() == false) {
                        hideAdviceText()
                    } else {
                        showAdviceText()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBinding()
        setupFragment()
        setupView()
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.lifecycleOwner = this
        binding.viewModel = searchViewModel
    }

    private fun setupFragment() {
        val pagerAdapter = SearchPagerAdapter(supportFragmentManager, lifecycle)
        binding.searchViewpager.adapter = pagerAdapter
        binding.searchViewpager.registerOnPageChangeCallback(pageChangeCallback)
        TabLayoutMediator(binding.searchTabLayout, binding.searchViewpager, false) { tab, position ->
            when (position) {
                0 -> tab.text = "공지"
                1 -> tab.text = "교직원"
            }
        }.attach()
    }

    private fun setupView() {
        binding.searchBackBt.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.anim_slide_left_enter, R.anim.anim_slide_left_exit)
        }

        binding.searchKeywordEt.addTextChangedListener(object : TextWatcher {
            var lastEditTime = 0L

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                synchronized(this) {
                    lastEditTime = System.currentTimeMillis()
                }

                lifecycleScope.launch {
                    // 0.2초 후에 lastEditTime 이 변경되지 않았으면 검색
                    val now = lastEditTime
                    delay(200)

                    val searchFlag = synchronized(this) {
                        lastEditTime == now
                    }
                    if (searchFlag) {
                        searchWithKeyword(s.toString())
                    }
                }
            }
        })
    }

    private fun searchWithKeyword(keyword: String) {
        if(keyword.isNotEmpty()) {
            when(currentPage) {
                noticeSearchPage -> {
                    searchViewModel.searchNotice(keyword)
                }
                staffSearchPage -> {
                    searchViewModel.searchStaff(keyword)
                }
            }
        } else {
            when(currentPage) {
                noticeSearchPage -> {
                    searchViewModel.clearNoticeList()
                }
                staffSearchPage -> {
                    searchViewModel.clearStaffList()
                }
            }
        }
    }

    fun showAdviceText() {
        binding.searchAdviceTxt.visibility = View.VISIBLE
    }

    fun hideAdviceText() {
        binding.searchAdviceTxt.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.connectWebSocketIfDisconnected()
    }

    override fun onStop() {
        super.onStop()
        searchViewModel.disconnectWebSocket()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_left_enter, R.anim.anim_slide_left_exit)
    }

    companion object {
        const val noticeSearchPage = 0
        const val staffSearchPage = 1
    }
}