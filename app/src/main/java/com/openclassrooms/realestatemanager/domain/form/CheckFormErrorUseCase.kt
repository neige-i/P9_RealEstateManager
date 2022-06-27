package com.openclassrooms.realestatemanager.domain.form

import android.content.Context
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.UtilsRepository
import com.openclassrooms.realestatemanager.data.current_photo.CurrentPhotoRepository
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckFormErrorUseCase @Inject constructor(
    private val formRepository: FormRepository,
    private val currentPhotoRepository: CurrentPhotoRepository,
    @ApplicationContext private val appContext: Context,
) {

    suspend fun containsNoError(pageToCheck: PageToCheck): Boolean = containsNoError(pageToCheck.ordinal)

    suspend fun containsNoError(pageToCheck: Int): Boolean {
        val form = formRepository.getForm()

        return when (PageToCheck.values()[pageToCheck]) {
            PageToCheck.MAIN -> containsNoMainError(form)
            PageToCheck.DETAIL -> containsNoDetailError(form)
            PageToCheck.ADDRESS -> containsNoAddressError(form)
            PageToCheck.SALE -> containsNoSaleError(form)
            PageToCheck.PICTURE -> containsNoPictureError()
        }
    }

    private fun containsNoMainError(form: FormEntity): Boolean {
        val typeError = if (form.estateType == null) {
            appContext.getString(R.string.error_mandatory_field)
        } else {
            null
        }

        formRepository.setForm(form.copy(typeError = typeError))

        return typeError == null
    }

    private fun containsNoDetailError(form: FormEntity): Boolean {
        val pictureListError = if (form.pictureList.isEmpty()) {
            appContext.getString(R.string.error_empty_photo_list)
        } else {
            null
        }

        formRepository.setForm(form.copy(pictureListError = pictureListError))

        return pictureListError == null
    }

    private suspend fun containsNoPictureError(): Boolean {
        val currentPhoto = currentPhotoRepository.getCurrentPhotoFlow().first()

        val descriptionError = if (currentPhoto.description.isBlank()) {
            appContext.getString(R.string.error_mandatory_field)
        } else {
            null
        }

        val isUriSelected = currentPhoto.uri != null

        currentPhotoRepository.setErrors(descriptionError, isUriSelected)

        return descriptionError == null && isUriSelected
    }

    private fun containsNoAddressError(form: FormEntity): Boolean {
        val currentState = form.state
        val currentZipcode = form.zipcode

        val streetNameError = when {
            form.streetName.isBlank() -> appContext.getString(R.string.error_mandatory_field)
            else -> null
        }

        val cityError = when {
            form.city.isBlank() -> appContext.getString(R.string.error_mandatory_field)
            else -> null
        }

        val stateError = when {
            currentState.isBlank() -> appContext.getString(R.string.error_mandatory_field)
            !UtilsRepository.STATE_POSTAL_ABBR.contains(currentState) -> appContext.getString(R.string.error_unknown_state)
            else -> null
        }

        val zipcodeError = when {
            currentZipcode.isBlank() -> appContext.getString(R.string.error_mandatory_field)
            currentZipcode.length < 5 -> appContext.getString(R.string.error_incomplete_zipcode)
            else -> null
        }

        val countryError = when {
            form.country.isBlank() -> appContext.getString(R.string.error_mandatory_field)
            else -> null
        }

        formRepository.setForm(
            form.copy(
                streetNameError = streetNameError,
                cityError = cityError,
                stateError = stateError,
                zipcodeError = zipcodeError,
                countryError = countryError,
            )
        )

        return streetNameError == null && cityError == null && stateError == null &&
                zipcodeError == null && countryError == null
    }

    private fun containsNoSaleError(form: FormEntity): Boolean {
        val isDateOrderInconsistent = form.marketEntryDate != null &&
                form.saleDate != null &&
                form.marketEntryDate.isAfter(form.saleDate)

        val marketEntryDateError = when {
            form.marketEntryDate == null -> appContext.getString(R.string.error_mandatory_field)
            isDateOrderInconsistent -> appContext.getString(R.string.error_inconsistent_date_order)
            else -> null
        }

        val saleDateError = when {
            !form.isAvailableForSale && form.saleDate == null -> appContext.getString(R.string.error_mandatory_field)
            isDateOrderInconsistent -> appContext.getString(R.string.error_inconsistent_date_order)
            else -> null
        }

        formRepository.setForm(
            form.copy(
                marketEntryDateError = marketEntryDateError,
                saleDateError = saleDateError,
            )
        )

        return marketEntryDateError == null && saleDateError == null
    }

    /**
     * The enum values are declared in the same order as the corresponding fragments are displayed in the pager.
     */
    enum class PageToCheck {
        MAIN,
        DETAIL,
        ADDRESS,
        SALE,
        PICTURE
    }
}