package com.openclassrooms.realestatemanager.ui.form

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityFormBinding
import com.openclassrooms.realestatemanager.ui.form.FormEvent.*
import com.openclassrooms.realestatemanager.ui.form.address.EditAddressFragment
import com.openclassrooms.realestatemanager.ui.form.detail_info.EditDetailInfoFragment
import com.openclassrooms.realestatemanager.ui.form.image_launcher.ImageLauncherActivity
import com.openclassrooms.realestatemanager.ui.form.main_info.EditMainInfoFragment
import com.openclassrooms.realestatemanager.ui.form.picture.PictureActivity
import com.openclassrooms.realestatemanager.ui.form.sale.EditSaleFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormActivity : ImageLauncherActivity() {

    private val viewModel: FormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityFormBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.formToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val formPagerAdapter = FormPagerAdapter(
            fragmentActivity = this,
            fragmentList = listOf(
                EditMainInfoFragment(),
                EditDetailInfoFragment(),
                EditAddressFragment(),
                EditSaleFragment(),
            ),
        )
        viewModel.onInitPagerAdapter(formPagerAdapter.itemCount)

        binding.formPager.isUserInputEnabled = false
        binding.formPager.adapter = formPagerAdapter
        binding.formPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.onPageChanged(position)
            }
        })

        binding.formSubmitButton.setOnClickListener { viewModel.onSubmitButtonClicked() }

        viewModel.viewStateLiveData.observe(this) { formViewState ->
            binding.formToolbar.title = formViewState.toolbarTitle
            binding.formSubmitButton.text = formViewState.submitButtonText
        }

        viewModel.formEventLiveData.observe(this) { formEvent ->
            when (formEvent) {
                ExitActivity -> finish()
                is GoToPage -> binding.formPager.currentItem = formEvent.pageToGo
                is ShowDialog -> MaterialAlertDialogBuilder(this)
                    .setTitle(formEvent.title)
                    .setMessage(formEvent.message)
                    .setPositiveButton(formEvent.positiveButtonText) { _, _ ->
                        viewModel.onDialogPositiveButtonClicked(formEvent.type)
                    }
                    .setNegativeButton(formEvent.negativeButtonText) { _, _ ->
                        viewModel.onDialogNegativeButtonClicked(formEvent.type)
                    }
                    .show()
                ShowPicture -> startActivity(Intent(this, PictureActivity::class.java))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        R.id.close_form -> {
            viewModel.onCloseMenuItemClicked()
            true
        }
        else -> false
    }

    override fun onBackPressed() {
        viewModel.onGoBack()
    }
}