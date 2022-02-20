package com.openclassrooms.realestatemanager.data.form

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.openclassrooms.realestatemanager.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FormRepository @Inject constructor() {

    companion object {
        private val DEFAULT_FORM = FormEntity(
            type = "",
            typeError = null,
            price = "",
            priceCursor = 0,
            area = "",
            areaCursor = 0,
            totalRoomCount = 0,
            bathroomCount = 0,
            bedroomCount = 0,
            description = "",
            descriptionCursor = 0,
            pictureList = emptyList(),
            pictureListError = null,
            streetName = "",
            streetNameError = null,
            streetNameCursor = 0,
            additionalAddressInfo = "",
            additionalAddressInfoCursor = 0,
            city = "",
            cityError = null,
            cityCursor = 0,
            state = "",
            stateError = null,
            stateCursor = 0,
            zipcode = "",
            zipcodeError = null,
            zipcodeCursor = 0,
            country = "",
            countryError = null,
            countryCursor = 0,
            pointsOfInterests = emptyList(),
            agentName = "",
            marketEntryDate = "",
            marketEntryDateError = null,
            saleDate = "",
            saleDateError = null,
            isAvailableForSale = true
        )
    }

    private val formMutableLiveData = MutableLiveData<FormEntity>()
    private val exitRequestMutableLiveData = MutableLiveData<Boolean>()
    private val showPictureDialogMutableLiveData = MutableLiveData<PicturePicker?>()
    private val displayedPictureMutableLiveData = MutableLiveData<DisplayedPictureEntity?>()
    private var initialState: FormEntity? = null
    private var currentState: FormEntity? = null
    private var positionOfPictureToUpdate = -1
    private var displayedPicture: DisplayedPictureEntity? = null

    fun getFormLiveData(): LiveData<FormEntity> = formMutableLiveData

    fun getFormInfoLiveData(): LiveData<FormInfoEntity> = Transformations.map(formMutableLiveData) {
        FormInfoEntity(
            form = it,
            type = if (initialState == DEFAULT_FORM) {
                FormInfoEntity.FormType.ADD
            } else {
                FormInfoEntity.FormType.EDIT
            },
            hasModifications = it != initialState
        )
    }

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

    fun getExitFormLiveData(): LiveData<Boolean> = exitRequestMutableLiveData

    fun setExitRequest(exit: Boolean) {
        exitRequestMutableLiveData.value = exit
    }

    fun getPicturePickerLiveData(): LiveData<PicturePicker?> = showPictureDialogMutableLiveData

    fun setPicturePicker(picturePicker: PicturePicker?) {
        showPictureDialogMutableLiveData.value = picturePicker
    }

    fun getDisplayedPictureLiveData() = displayedPictureMutableLiveData

    fun getCurrentDisplayedPicture(): DisplayedPictureEntity =
        displayedPicture ?: throw NullPointerException("Picture is not initialized!")

    fun isPictureInitialized(): Boolean = displayedPicture != null

    fun setDisplayedPicture(displayedPicture: DisplayedPictureEntity?) {
        this.displayedPicture = displayedPicture
        displayedPictureMutableLiveData.value = displayedPicture
    }

    enum class PicturePicker(@StringRes val labelId: Int) {
        GALLERY(R.string.image_picker_dialog_gallery_item),
        CAMERA(R.string.image_picker_dialog_camera_item),
    }
}