package com.openclassrooms.realestatemanager.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.openclassrooms.realestatemanager.ShowDetailCallback
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.detail.DetailFragment

class MainActivity : AppCompatActivity(), ShowDetailCallback {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        viewModel.onActivityCreated(binding.mainContent?.id, binding.rightContent?.id)

        supportFragmentManager.addOnBackStackChangedListener {
            viewModel.onBackStackChanged(supportFragmentManager.backStackEntryCount)
        }

        viewModel.viewState.observe(this) {
            title = it.title
            supportActionBar?.setDisplayHomeAsUpEnabled(it.isHomeAsUpEnabled)
        }

        viewModel.mainEvent.observe(this) {
            when (it) {
                is ShowDetailEvent -> {
                    val bundle = bundleOf(DetailFragment.ITEM_ARG to it.arg)

                    supportFragmentManager.commit {
                        replace<DetailFragment>(it.containerId, args = bundle)
                        setReorderingAllowed(true)
                        addToBackStack(null)
                    }
                }
                EndActivityEvent -> finish()
                GoBackEvent -> super.onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() = viewModel.onBackPressed()

    override fun onShowDetail(item: String) = viewModel.onShowDetails(item)
}