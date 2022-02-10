package com.openclassrooms.realestatemanager.domain.form

import android.content.Context
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject

class CheckFormErrorUseCase @Inject constructor(
    private val formRepository: FormRepository,
    @ApplicationContext private val appContext: Context,
) {

    fun containsNoError(pageToCheck: Int): Boolean {
        val form = formRepository.getCurrentForm()

        return when (pageToCheck) {
            0 -> containsNoMainError(form)
            2 -> containsNoAddressError(form)
            3 -> containsNoSaleError(form)
            else -> true
        }
    }

    private fun containsNoMainError(form: FormEntity): Boolean {
        val typeError = if (form.type.isEmpty()) {
            appContext.getString(R.string.error_mandatory_field)
        } else {
            null
        }

        formRepository.setForm(form.copy(typeError = typeError))

        return typeError == null
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

    private fun containsNoSaleError(form: FormEntity): Boolean {
        val marketEntryDateString = form.marketEntryDate
        val saleDateString = form.saleDate

        val isDateOrderInconsistent = marketEntryDateString.isNotEmpty() &&
                saleDateString.isNotEmpty() &&
                toLocalDate(marketEntryDateString).isAfter(toLocalDate(saleDateString))

        val marketEntryDateError = when {
            marketEntryDateString.isEmpty() -> appContext.getString(R.string.error_mandatory_field)
            isDateOrderInconsistent -> appContext.getString(R.string.error_inconsistent_date_order)
            else -> null
        }

        val saleDateError = when {
            !form.isAvailableForSale && saleDateString.isEmpty() -> appContext.getString(R.string.error_mandatory_field)
            isDateOrderInconsistent -> appContext.getString(R.string.error_inconsistent_date_order)
            else -> null
        }

        formRepository.setForm(form.copy(
            marketEntryDateError = marketEntryDateError,
            saleDateError = saleDateError
        ))

        return marketEntryDateError == null && saleDateError == null
    }

    private fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, FormRepository.DATE_FORMATTER)
    }
}