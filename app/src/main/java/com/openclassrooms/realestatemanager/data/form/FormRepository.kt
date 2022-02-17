package com.openclassrooms.realestatemanager.data.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormRepository @Inject constructor() {

    companion object {
        private val DEFAULT_FORM = FormEntity(
            displayedPage = 0,
            type = "",
            typeError = null,
            price = "",
            area = "",
            totalRoomCount = 0,
            bathroomCount = 0,
            bedroomCount = 0,
            description = "",
            pictureList = emptyList(),
            pictureListError = null,
            streetName = "",
            streetNameError = null,
            additionalAddressInfo = "",
            city = "",
            cityError = null,
            state = "",
            stateError = null,
            zipcode = "",
            zipcodeError = null,
            country = "",
            countryError = null,
            pointsOfInterests = emptyList(),
            agentName = "",
            marketEntryDate = "",
            marketEntryDateError = null,
            saleDate = "",
            saleDateError = null,
            isAvailableForSale = true
        )
    }

    private val formInfoMediatorLiveData = MediatorLiveData<FormInfoEntity>()
    private val formMutableLiveData = MutableLiveData<FormEntity>()
    private val formPageCountMutableLiveData = MutableLiveData<Int>()
    private val exitRequestMutableLiveData = MutableLiveData<Boolean>()
    private var initialState: FormEntity? = null
    private var currentState: FormEntity? = null
    private var positionOfPictureToUpdate = -1

    init {
        formInfoMediatorLiveData.addSource(formMutableLiveData) {
            combineFormInfo(it, formPageCountMutableLiveData.value)
        }
        formInfoMediatorLiveData.addSource(formPageCountMutableLiveData) {
            combineFormInfo(formMutableLiveData.value, it)
        }
    }

    private fun combineFormInfo(form: FormEntity?, pageCount: Int?) {
        val capturedInitialState = initialState

        if (form == null || pageCount == null || capturedInitialState == null) {
            return
        }

        formInfoMediatorLiveData.value = FormInfoEntity(
            form = form,
            pageCount = pageCount,
            type = if (initialState == DEFAULT_FORM) {
                FormInfoEntity.FormType.ADD
            } else {
                FormInfoEntity.FormType.EDIT
            },
            hasModifications = form != capturedInitialState
        )
    }

    fun getFormLiveData(): LiveData<FormEntity> {
        return Transformations.distinctUntilChanged(formMutableLiveData)
    }

    fun getFormInfoLiveData(): LiveData<FormInfoEntity> = formInfoMediatorLiveData

    fun getCurrentForm(): FormEntity = currentState ?: throw NullPointerException(
        "The form has not been initialized. Please call initForm() before accessing it"
    )

    fun initForm(form: FormEntity? = null) {
        initialState = form ?: DEFAULT_FORM
        setForm(form ?: currentState ?: DEFAULT_FORM)
    }

    fun setForm(form: FormEntity) {
        currentState = form
        formMutableLiveData.value = form
    }

    fun resetForm() {
        initialState = null
        currentState = null
    }

    fun getPositionOfPictureToUpdate(): Int = positionOfPictureToUpdate

    fun setPositionOfPictureToUpdate(position: Int) {
        positionOfPictureToUpdate = position
    }

    fun setPageCount(pageCount: Int) {
        formPageCountMutableLiveData.value = pageCount
    }

    fun getExitFormLiveData(): LiveData<Boolean> = exitRequestMutableLiveData

    fun setExitRequest(exit: Boolean) {
        exitRequestMutableLiveData.value = exit
    }
}