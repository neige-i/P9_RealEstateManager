package com.openclassrooms.realestatemanager.list

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.DetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(private val detailRepository: DetailRepository) :
    ViewModel() {

    fun onItemClicked(item: String) = detailRepository.setItem(item)
}