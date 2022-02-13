package com.openclassrooms.realestatemanager.ui.form

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityFormBinding
import com.openclassrooms.realestatemanager.ui.form.address.EditAddressFragment
import com.openclassrooms.realestatemanager.ui.form.detail_info.EditDetailInfoFragment
import com.openclassrooms.realestatemanager.ui.form.main_info.EditMainInfoFragment
import com.openclassrooms.realestatemanager.ui.form.sale.EditSaleFragment
import com.openclassrooms.realestatemanager.ui.form.picture.PictureActivity
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
                is FormEvent.ShowDialog -> MaterialAlertDialogBuilder(this)
                    .setTitle(it.title)
                    .setMessage(it.message)
                    .setPositiveButton(it.positiveButtonText) { _, _ ->
                        viewModel.onDialogPositiveButtonClicked()
                    }
                    .setNegativeButton(it.negativeButtonText) { _, _ ->
                        viewModel.onDialogNegativeButtonClicked()
                    }
                    .show()
                FormEvent.ShowPicture -> startActivity(Intent(this, PictureActivity::class.java))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.close_form -> {
                viewModel.onCloseMenuItemClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        viewModel.onGoBack()
    }
}