package com.openclassrooms.realestatemanager.domain.form

import android.content.Context
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CheckFormErrorUseCase @Inject constructor(
    private val formRepository: FormRepository,
    @ApplicationContext private val appContext: Context,
) {

    fun containsNoError(pageToCheck: Int): Boolean {
        val form = formRepository.getCurrentForm()

        return when (pageToCheck) {
            2 -> containsNoAddressError(form)
            else -> true
        }
    }

    private fun containsNoAddressError(form: FormEntity): Boolean {
        val currentState = form.state
        val currentZipcode = form.zipcode

        val stateError = when {
            currentState.isNotBlank() && !FormRepository.STATE_POSTAL_ABBR.contains(currentState) -> {
                appContext.getString(R.string.error_unknown_state)
            }
            else -> null
        }

        val zipcodeError = when {
            currentZipcode.isNotEmpty() && currentZipcode.length < 5 -> {
                appContext.getString(R.string.error_incomplete_zipcode)
            }
            else -> null
        }

        formRepository.setForm(form.copy(zipcodeError = zipcodeError, stateError = stateError))

        return stateError == null && zipcodeError == null
    }
}