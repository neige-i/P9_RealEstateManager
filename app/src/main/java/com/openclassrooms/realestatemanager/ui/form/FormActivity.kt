package com.openclassrooms.realestatemanager.ui.form

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.openclassrooms.realestatemanager.databinding.ActivityFormBinding
import com.openclassrooms.realestatemanager.ui.form.address.EditAddressFragment
import com.openclassrooms.realestatemanager.ui.form.detail_info.EditDetailInfoFragment
import com.openclassrooms.realestatemanager.ui.form.main_info.EditMainInfoFragment
import com.openclassrooms.realestatemanager.ui.form.sale.EditSaleFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormActivity : AppCompatActivity() {

    private val viewModel: FormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFormBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.formToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val addEditPagerAdapter = FormPagerAdapter(
            fragmentActivity = this,
            fragmentList = listOf(
                EditMainInfoFragment(),
                EditDetailInfoFragment(),
                EditAddressFragment(),
                EditSaleFragment()
            ),
        )
        viewModel.onInitPagerAdapter(addEditPagerAdapter.itemCount)

        binding.formPager.isUserInputEnabled = false
        binding.formPager.adapter = addEditPagerAdapter
        binding.formPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onPageChanged(position)
            }
        })

        binding.formToolbar.setNavigationOnClickListener { onBackPressed() }

        binding.formSubmitButton.setOnClickListener { viewModel.onSubmitButtonClicked() }

        viewModel.viewStateLiveData.observe(this) {
            binding.formToolbar.title = it.toolbarTitle
            binding.formSubmitButton.text = it.submitButtonText
        }

        viewModel.formEventLiveData.observe(this) {
            when (it) {
                FormEvent.ExitActivity -> finish()
                is FormEvent.GoToPage -> {
                    binding.formPager.currentItem = it.pageToGo
                }
            }
        }
    }

    override fun onBackPressed() {
        viewModel.onGoBack()
    }
}