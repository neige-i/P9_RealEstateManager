package com.openclassrooms.realestatemanager.ui.form.detail_info

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.data.form.FormEntity
import com.openclassrooms.realestatemanager.data.form.FormRepository
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.CoroutineProvider
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EditDetailInfoViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockGetFormUseCase: GetFormUseCase

    @MockK
    private lateinit var mockSetFormUseCase: SetFormUseCase

    @MockK
    private lateinit var mockCoroutineProvider: CoroutineProvider

    private lateinit var editDetailInfoViewModel: EditDetailInfoViewModel

    companion object {
        // region IN
        private val mockUri1 = mockk<Uri>()
        private val mockUri2 = mockk<Uri>()
        private val NOMINAL_FLOW = FormRepository.DEFAULT_FORM.copy(
            description = "Beautiful penthouse",
            descriptionCursor = 2,
            pictureList = listOf(
                FormEntity.PictureEntity(mockUri1, "Patio"),
                FormEntity.PictureEntity(mockUri2, "Garage"),
            ),
            pictureListError = null
        )
        private val FORM_FLOW = MutableStateFlow(NOMINAL_FLOW)
        // endregion IN

        // region OUT
        private val DEFAULT_VIEW_STATE = DetailInfoViewState(
            description = "Beautiful penthouse",
            descriptionSelection = 2,
            photoList = listOf(
                DetailInfoViewState.PhotoViewState.Picture(mockUri1, "Patio"),
                DetailInfoViewState.PhotoViewState.Picture(mockUri2, "Garage"),
                DetailInfoViewState.PhotoViewState.Add
            ),
        )
        // endregion OUT
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockGetFormUseCase.getFormFlow() } returns FORM_FLOW
        every { mockCoroutineProvider.getIoDispatcher() } returns testCoroutineRule.testCoroutineDispatcher
        justRun { mockSetFormUseCase.resetPictureError() }
        justRun { mockSetFormUseCase.updateDescription(any(), any()) }
        justRun { mockSetFormUseCase.updatePicturePosition(any()) }
        justRun { mockSetFormUseCase.updatePicturePosition(any()) }
        justRun { mockSetFormUseCase.setPicture(any(), any()) }
        justRun { mockSetFormUseCase.removePictureAt(any()) }

        editDetailInfoViewModel = EditDetailInfoViewModel(
            mockGetFormUseCase,
            mockSetFormUseCase,
            mockCoroutineProvider,
        )
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { mockGetFormUseCase.getFormFlow() }
        verify(exactly = 1) { mockCoroutineProvider.getIoDispatcher() }
        confirmVerified(mockGetFormUseCase, mockSetFormUseCase, mockCoroutineProvider)
    }

    @Test
    fun `return default view state when get nominal form`() {
        // GIVEN
        FORM_FLOW.value = NOMINAL_FLOW

        // WHEN
        val viewState = getValueForTesting(editDetailInfoViewModel.viewStateLiveData)

        // THEN
        assertEquals(DEFAULT_VIEW_STATE, viewState)
    }

    @Test
    fun `return view state, show & reset picture error when get form with non-null picture error`() {
        // GIVEN
        FORM_FLOW.update { it.copy(pictureListError = "ERR") }

        // WHEN
        val viewState = getValueForTesting(editDetailInfoViewModel.viewStateLiveData)
        val showPictureError = getValueForTesting(editDetailInfoViewModel.showErrorEventLiveData)

        // THEN
        assertEquals(DEFAULT_VIEW_STATE, viewState)
        assertEquals("ERR", showPictureError)

        verify(exactly = 1) { mockSetFormUseCase.resetPictureError() }
    }

    @Test
    fun `update description when change input with non-null content`() {
        // WHEN
        editDetailInfoViewModel.onDescriptionChanged("This is a big house", 3)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateDescription("This is a big house", 3) }
    }

    @Test
    fun `update description when change input with null content`() {
        // WHEN
        editDetailInfoViewModel.onDescriptionChanged(null, 3)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updateDescription("", 3) }
    }

    @Test
    fun `update picture position when add photo`() {
        // WHEN
        editDetailInfoViewModel.onPhotoAdded(23)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updatePicturePosition(23) }
    }

    @Test
    fun `update picture position & set picture when open photo`() {
        // WHEN
        val mockUri = mockk<Uri>()
        editDetailInfoViewModel.onPhotoOpened(
            position = 5,
            picture = DetailInfoViewState.PhotoViewState.Picture(
                uri = mockUri,
                description = "Bedroom"
            )
        )

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updatePicturePosition(5) }
        verify(exactly = 1) { mockSetFormUseCase.setPicture(mockUri, "Bedroom") }
    }

    @Test
    fun `remove picture when remove photo`() {
        // WHEN
        editDetailInfoViewModel.onPhotoRemoved(0)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.removePictureAt(0) }
    }
}