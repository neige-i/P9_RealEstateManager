package com.openclassrooms.realestatemanager.ui.add_edit

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.openclassrooms.realestatemanager.databinding.ActivityAddEditBinding
import com.openclassrooms.realestatemanager.ui.add_edit.pages.AddressFragment
import com.openclassrooms.realestatemanager.ui.add_edit.pages.DetailInfoFragment
import com.openclassrooms.realestatemanager.ui.add_edit.pages.MainInfoFragment
import com.openclassrooms.realestatemanager.ui.add_edit.pages.SaleStatusFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditActivity : AppCompatActivity() {

    private val viewModel: AddEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAddEditBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.addEditToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val addEditPagerAdapter = AddEditPagerAdapter(
            fragmentActivity = this,
            fragmentList = listOf(
                MainInfoFragment(),
                DetailInfoFragment(),
                AddressFragment(),
                SaleStatusFragment()
            ),
        )
        viewModel.onInitPagerAdapter(addEditPagerAdapter.itemCount)

        binding.addEditPager.isUserInputEnabled = false
        binding.addEditPager.adapter = addEditPagerAdapter
        binding.addEditPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                viewModel.onPageChanged(position)
            }
        })

        binding.addEditToolbar.setNavigationOnClickListener { onBackPressed() }

        binding.addEditSubmitButton.setOnClickListener { viewModel.onSubmitButtonClicked() }

        viewModel.viewStateLiveData.observe(this) {
            binding.addEditToolbar.title = it.toolbarTitle
            binding.addEditSubmitButton.text = it.submitButtonText
        }

        viewModel.addEditEventLiveData.observe(this) {
            when (it) {
                AddEditEvent.ExitActivity -> finish()
                is AddEditEvent.GoToPage -> {
                    binding.addEditPager.currentItem = it.pageToGo
                }
            }
        }
    }

    override fun onBackPressed() {
        viewModel.onGoBack()
    }
}