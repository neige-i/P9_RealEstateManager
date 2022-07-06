package com.openclassrooms.realestatemanager.data.form

import android.net.Uri
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class FormRepositoryTest {

    private val formRepository = FormRepository()

    companion object {
        // region IN
        private val DEFAULT_URI = mockk<Uri>()
        private val DEFAULT_PICTURE = FormEntity.PictureEntity(DEFAULT_URI, "Lounge")
        private val TEST_FORM = FormEntity(
            estateType = "MANSION",
            typeError = "Wrong type",
            price = "345200",
            priceCursor = 4,
            area = "150",
            areaCursor = 2,
            totalRoomCount = 23,
            bathroomCount = 4,
            bedroomCount = 10,
            description = "This is a mansion",
            descriptionCursor = 5,
            pictureList = listOf(DEFAULT_PICTURE),
            pictureListError = "Error in the pictures",
            streetName = "345 main street",
            streetNameError = "Wrong address",
            streetNameCursor = 5,
            additionalAddressInfo = "Apt 56",
            additionalAddressInfoCursor = 1,
            city = "Old York",
            cityError = "Error in the city",
            cityCursor = 6,
            state = "NYC",
            stateError = "State with 3 letters",
            stateCursor = 2,
            zipcode = "123",
            zipcodeError = "Incomplete zipcode",
            zipcodeCursor = 1,
            country = "United States",
            countryError = "Error in the country",
            countryCursor = 9,
            pointsOfInterests = listOf(3, 6, 7),
            agentName = "Agent K",
            marketEntryDate = "01/03/2022",
            marketEntryDateError = "Error in the entry date",
            saleDate = "02/03/2022",
            saleDateError = "Error in the sale date",
            isAvailableForSale = false
        )
        // endregion IN
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `return default form when initialize repository`() = runTest {
        val formSync = formRepository.getForm()
        val formAsync = formRepository.getFormFlow().first()
        val initialState = formRepository.getInitialState()

        // THEN
        assertEquals(FormRepository.DEFAULT_FORM, formSync)
        assertEquals(FormRepository.DEFAULT_FORM, formAsync)
        assertEquals(FormRepository.DEFAULT_FORM, initialState)
    }

    @Test
    fun `change initial state when initialize form`() {
        // WHEN
        formRepository.setInitialState(TEST_FORM)
        val initialState = formRepository.getInitialState()

        // THEN
        assertEquals(TEST_FORM, initialState)
    }

    @Test
    fun `change current form when set with a new value`() {
        // WHEN
        formRepository.setForm(TEST_FORM)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(TEST_FORM, currentForm)
    }

    @Test
    fun `return default form when reset`() {
        // GIVEN
        formRepository.setInitialState(TEST_FORM)
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.resetAll()
        val initialState = formRepository.getInitialState()
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(FormRepository.DEFAULT_FORM, initialState)
        assertEquals(FormRepository.DEFAULT_FORM, currentForm)
    }

    @Test
    fun `return form without any errors when reset all errors`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.resetAllErrors()
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(
                typeError = null,
                pictureListError = null,
                streetNameError = null,
                cityError = null,
                stateError = null,
                zipcodeError = null,
                countryError = null,
                marketEntryDateError = null,
                saleDateError = null,
            ),
            currentForm
        )
    }

    @Test
    fun `change type when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setType("BIG HOUSE")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(estateType = "BIG HOUSE"),
            currentForm
        )
    }

    @Test
    fun `change type error when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setTypeError("ERR")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(typeError = "ERR"),
            currentForm
        )
    }

    @Test
    fun `change price when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setPrice("99", 0)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(price = "99", priceCursor = 0),
            currentForm
        )
    }

    @Test
    fun `change area when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setArea("40", 3)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(area = "40", areaCursor = 3),
            currentForm
        )
    }

    @Test
    fun `change total room count when increment value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.incTotalRoom()
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(totalRoomCount = 24),
            currentForm
        )
    }

    @Test
    fun `change total room count when decrement`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.decTotalRoom()
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(totalRoomCount = 22),
            currentForm
        )
    }

    @Test
    fun `change bathroom count when increment value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.incBathroom()
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(bathroomCount = 5),
            currentForm
        )
    }

    @Test
    fun `change bathroom count when decrement`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.decBathroom()
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(bathroomCount = 3),
            currentForm
        )
    }

    @Test
    fun `change bedroom count when increment value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.incBedroom()
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(bedroomCount = 11),
            currentForm
        )
    }

    @Test
    fun `change bedroom count when decrement`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.decBedroom()
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(bedroomCount = 9),
            currentForm
        )
    }

    @Test
    fun `change description when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setDescription("short description", 0)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(description = "short description", descriptionCursor = 0),
            currentForm
        )
    }

    @Test
    fun `return -1 when get current picture position while not being set yet`() {
        // WHEN
        val currentPosition = formRepository.getCurrentPicturePosition()

        // THEN
        assertEquals(-1, currentPosition)
    }

    @Test
    fun `change picture position when set a new value`() {
        // WHEN
        formRepository.setCurrentPicturePosition(20)
        val currentPosition = formRepository.getCurrentPicturePosition()

        // THEN
        assertEquals(20, currentPosition)
    }

    @Test
    fun `change picture error when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setPictureListError("ERR")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(pictureListError = "ERR"),
            currentForm
        )
    }

    @Test
    fun `add a second picture to list when add a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.addPicture(FormEntity.PictureEntity(DEFAULT_URI, "Kitchen"))
        val pictureList = formRepository.getForm().pictureList

        // THEN
        assertEquals(
            listOf(
                DEFAULT_PICTURE,
                FormEntity.PictureEntity(DEFAULT_URI, "Kitchen")
            ),
            pictureList
        )
    }

    @Test
    fun `change first picture when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setPictureAt(0, FormEntity.PictureEntity(DEFAULT_URI, "Kitchen"))
        val pictureList = formRepository.getForm().pictureList

        // THEN
        assertEquals(
            listOf(FormEntity.PictureEntity(DEFAULT_URI, "Kitchen")),
            pictureList
        )
    }

    @Test
    fun `return empty picture list when delete the unique item`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.deletePictureAt(0)
        val pictureList = formRepository.getForm().pictureList

        // THEN
        assertTrue(pictureList.isEmpty())
    }

    @Test
    fun `change street name when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setStreetName("Main avenue", 2)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(streetName = "Main avenue", streetNameCursor = 2),
            currentForm
        )
    }

    @Test
    fun `change street name error when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setStreetNameError("ERR")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(streetNameError = "ERR"),
            currentForm
        )
    }

    @Test
    fun `change additional address when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setAdditionalAddress("1st floor", 3)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(additionalAddressInfo = "1st floor", additionalAddressInfoCursor = 3),
            currentForm
        )
    }

    @Test
    fun `change city when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setCity("Philadelphia", 2)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(city = "Philadelphia", cityCursor = 2),
            currentForm
        )
    }

    @Test
    fun `change city error when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setCityError("ERR")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(cityError = "ERR"),
            currentForm
        )
    }

    @Test
    fun `change state when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setState("NY", 0)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(state = "NY", stateCursor = 0),
            currentForm
        )
    }

    @Test
    fun `change state error when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setStateError("ERR")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(stateError = "ERR"),
            currentForm
        )
    }

    @Test
    fun `change zipcode when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setZipcode("55555", 4)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(zipcode = "55555", zipcodeCursor = 4),
            currentForm
        )
    }

    @Test
    fun `change zipcode error when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setZipcodeError("ERR")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(zipcodeError = "ERR"),
            currentForm
        )
    }

    @Test
    fun `change country when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setCountry("Canada", 4)
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(country = "Canada", countryCursor = 4),
            currentForm
        )
    }

    @Test
    fun `change country error when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setCountryError("ERR")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(countryError = "ERR"),
            currentForm
        )
    }

    @Test
    fun `add a 4th poi to list when add a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.addPoi(19)
        val poiList = formRepository.getForm().pointsOfInterests

        // THEN
        assertEquals(
            listOf(3, 6, 7, 19),
            poiList
        )
    }

    @Test
    fun `delete poi when remove it`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.removePoi(6)
        val poiList = formRepository.getForm().pointsOfInterests

        // THEN
        assertEquals(
            listOf(3, 7),
            poiList
        )
    }

    @Test
    fun `change agent name when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setAgentName("J")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(agentName = "J"),
            currentForm
        )
    }

    @Test
    fun `change entry date when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setEntryDate("01/01/1970")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(marketEntryDate = "01/01/1970"),
            currentForm
        )
    }

    @Test
    fun `change entry date error when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setEntryDateError("ERR")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(marketEntryDateError = "ERR"),
            currentForm
        )
    }

    @Test
    fun `change sale date when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setSaleDate("01/01/2000")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(saleDate = "01/01/2000"),
            currentForm
        )
    }

    @Test
    fun `change sale date error when set a new value`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setSaleDateError("ERR")
        val currentForm = formRepository.getForm()

        // THEN
        assertEquals(
            TEST_FORM.copy(saleDateError = "ERR"),
            currentForm
        )
    }

    @Test
    fun `return true when enable availability`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setAvailability(true)
        val currentForm = formRepository.getForm()

        // THEN
        assertTrue(currentForm.isAvailableForSale)
    }

    @Test
    fun `return false when disable availability`() {
        // GIVEN
        formRepository.setForm(TEST_FORM)

        // WHEN
        formRepository.setAvailability(false)
        val currentForm = formRepository.getForm()

        // THEN
        assertFalse(currentForm.isAvailableForSale)
    }
}