package com.openclassrooms.realestatemanager.ui.form.picture

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.data.form.CurrentPictureEntity
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import com.openclassrooms.realestatemanager.util.TestLifecycle.isLiveDataTriggered
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class PictureViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockGetFormUseCase: GetFormUseCase

    @MockK
    private lateinit var mockCheckFormErrorUseCase: CheckFormErrorUseCase

    @MockK
    private lateinit var mockSetFormUseCase: SetFormUseCase

    @MockK
    private lateinit var mockCoroutineProvider: CoroutineProvider

    private lateinit var pictureViewModel: PictureViewModel

    companion object {
        // region IN
        private val MOCK_URI = mockk<Uri>()
        private val DEFAULT_CURRENT_PICTURE = CurrentPictureEntity(
            uri = MOCK_URI,
            description = "Garden",
            descriptionError = null,
            descriptionCursor = 6
        )
        // endregion IN

        // region OUT
        private val DEFAULT_VIEW_STATE = PictureViewState(
            uri = MOCK_URI,
            description = "Garden",
            descriptionError = null,
            descriptionSelection = 6
        )
        // endregion OUT
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockGetFormUseCase.getCurrentPictureFlow() } returns flowOf(DEFAULT_CURRENT_PICTURE)
        every { mockCoroutineProvider.getIoDispatcher() } returns testCoroutineRule.testCoroutineDispatcher
        justRun { mockSetFormUseCase.updatePictureDescription(any(), any()) }
        justRun { mockSetFormUseCase.savePicture() }
        justRun { mockSetFormUseCase.resetPicture() }
        every {
            mockCheckFormErrorUseCase.containsNoError(CheckFormErrorUseCase.PageToCheck.PICTURE)
        } returns true

        pictureViewModel = PictureViewModel(
            mockGetFormUseCase,
            mockCheckFormErrorUseCase,
            mockSetFormUseCase,
            mockCoroutineProvider,
        )
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { mockGetFormUseCase.getCurrentPictureFlow() }
        verify(exactly = 1) { mockCoroutineProvider.getIoDispatcher() }
        confirmVerified(
            mockGetFormUseCase,
            mockCheckFormErrorUseCase,
            mockSetFormUseCase,
            mockCoroutineProvider
        )
    }

    @Test
    fun `returns default view state when get default current picture`() {
        // WHEN
        val viewState = getValueForTesting(pictureViewModel.viewStateLiveData)

        // THEN
        assertEquals(DEFAULT_VIEW_STATE, viewState)
    }

    @Test
    fun `update description when change input with non-null content`() {
        // WHEN
        pictureViewModel.onDescriptionChanged("Lounge", 5)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updatePictureDescription("Lounge", 5) }
    }

    @Test
    fun `update description when change input with null content`() {
        // WHEN
        pictureViewModel.onDescriptionChanged(null, 3)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.updatePictureDescription("", 3) }
    }

    @Test
    fun `save picture & exit when click on save MenuItem without errors`() {
        // WHEN
        pictureViewModel.onSaveMenuItemClicked()
        val isExitEventTriggered = isLiveDataTriggered(pictureViewModel.exitEventLiveData)

        // THEN
        assertTrue(isExitEventTriggered)

        verify(exactly = 1) {
            mockCheckFormErrorUseCase.containsNoError(CheckFormErrorUseCase.PageToCheck.PICTURE)
        }
        verify(exactly = 1) { mockSetFormUseCase.savePicture() }
    }

    @Test
    fun `do nothing when click on save MenuItem with errors on picture`() {
        // GIVEN
        every {
            mockCheckFormErrorUseCase.containsNoError(CheckFormErrorUseCase.PageToCheck.PICTURE)
        } returns false

        // WHEN
        pictureViewModel.onSaveMenuItemClicked()
        val isExitEventTriggered = isLiveDataTriggered(pictureViewModel.exitEventLiveData)

        // THEN
        assertFalse(isExitEventTriggered)

        verify(exactly = 1) {
            mockCheckFormErrorUseCase.containsNoError(CheckFormErrorUseCase.PageToCheck.PICTURE)
        }
        verify(exactly = 0) { mockSetFormUseCase.savePicture() }
    }

    @Test
    fun `reset picture when finish activity`() {
        // WHEN
        pictureViewModel.onActivityFinished()

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.resetPicture() }
    }
}