package com.ku_stacks.ku_ring.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.ku_stacks.ku_ring.R
import com.ku_stacks.ku_ring.analytics.EventAnalytics
import com.ku_stacks.ku_ring.databinding.ActivityHomeBinding
import com.ku_stacks.ku_ring.ui.home.dialog.HomeBottomSheet
import com.ku_stacks.ku_ring.ui.home.dialog.NextActivityItem
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    @Inject
    lateinit var analytics : EventAnalytics

    private lateinit var binding: ActivityHomeBinding
    private val viewModel by viewModels<HomeViewModel>()

    private val pageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            Timber.e("pageSelect detected")
            when(position){
                0 -> viewModel.onBchTabClick()
                1 -> viewModel.onSchTabClick()
                2 -> viewModel.onEmpTabClick()
                3 -> viewModel.onNatTabClick()
                4 -> viewModel.onStuTabClick()
                5 -> viewModel.onIndTabClick()
                6 -> viewModel.onNorTabClick()
                7 -> viewModel.onLibTabClick()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBinding()
        setupHeader()
        observeData()

        getFcmToken()

//      anlytics, crashlytics 예시
//        binding.homeText.setOnClickListener {
//            Timber.e("homeText clicked")
//            analytics.click("home btn", "HomeActivity")
//            //throw RuntimeException("Crash On Release ver")
//        }

    }

    private fun setupBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun setupHeader(){
        binding.homeViewpager.adapter = HomePagerAdapter(supportFragmentManager,lifecycle)
        binding.homeViewpager.registerOnPageChangeCallback(pageChangeCallback)

        TabLayoutMediator(binding.homeHeader.tabLayout, binding.homeViewpager,true) { tab, position ->
            //여기서 등록한 푸시알림으로 색깔 변경도 가능할듯?
            when(position){
                0 -> tab.text = "학사"
                1 -> tab.text = "장학"
                2 -> tab.text = "취창업"
                3 -> tab.text = "국제"
                4 -> tab.text = "학생"
                5 -> tab.text = "산학"
                6 -> tab.text = "일반"
                7 -> tab.text = "도서관"
            }
        }.attach()

        binding.homeHeader.menuImg.setOnClickListener {
            invokeMenuDialog()
        }
    }

    private fun observeData(){
        viewModel.homeTabState.observe(this){
            binding.homeText.text = "${it.name} in HomeActivity"
            Timber.e("${it.name} observed")
        }
    }

    private fun getFcmToken() {
        CoroutineScope(Dispatchers.Default).launch {
            val instance = FirebaseInstallations.getInstance()
            Timber.e("FCM instance : $instance")

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    Timber.e("Firebase instanceId fail : ${task.exception}")
                    return@addOnCompleteListener
                }
                val token = task.result
                Timber.e("FCM token : $token")
            }
        }

    }
    private fun invokeMenuDialog() {
        val bottomSheet = HomeBottomSheet()
        bottomSheet.setArgument {
            when (it) {
                NextActivityItem.Feedback -> {
                    Snackbar.make(binding.root,"Feedback Activity",Snackbar.LENGTH_SHORT ).show()
                }
                NextActivityItem.OpenSource -> {
                    Snackbar.make(binding.root,"OpenSource Activity",Snackbar.LENGTH_SHORT ).show()
                }
                NextActivityItem.PersonalInfo -> {
                    Snackbar.make(binding.root,"PersonalInfo Activity",Snackbar.LENGTH_SHORT ).show()
                }
            }
        }
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}