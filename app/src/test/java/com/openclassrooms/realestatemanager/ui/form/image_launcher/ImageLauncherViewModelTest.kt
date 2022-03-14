package com.openclassrooms.realestatemanager.ui.form.image_launcher

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.data.form.ActionRepository
import com.openclassrooms.realestatemanager.data.form.ImagePicker
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.ui.CoroutineProvider
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import com.openclassrooms.realestatemanager.util.TestLifecycle.isLiveDataTriggered
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ImageLauncherViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockActionRepository: ActionRepository

    @MockK
    private lateinit var mockSetFormUseCase: SetFormUseCase

    @MockK
    private lateinit var mockCoroutineProvider: CoroutineProvider

    private lateinit var imageLauncherViewModel: ImageLauncherViewModel

    companion object {
        private val IMAGE_PICKER_FLOW = MutableStateFlow<ImagePicker?>(null)
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockActionRepository.getImagePickerFlow() } returns IMAGE_PICKER_FLOW
        justRun { mockActionRepository.flushImagePickerFlow() }
        every { mockCoroutineProvider.getIoDispatcher() } returns testCoroutineRule.testCoroutineDispatcher
        justRun { mockSetFormUseCase.setPictureUri(any()) }

        imageLauncherViewModel = ImageLauncherViewModel(
            mockActionRepository,
            mockSetFormUseCase,
            mockCoroutineProvider,
        )
    }

    @After
    fun tearDown() {
        verify(exactly = 1) { mockActionRepository.getImagePickerFlow() }
        verify(exactly = 1) { mockCoroutineProvider.getIoDispatcher() }
        confirmVerified(mockActionRepository, mockSetFormUseCase, mockCoroutineProvider)
    }

    @Test
    fun `open gallery when get flow with GALLERY value`() {
        // GIVEN
        IMAGE_PICKER_FLOW.value = ImagePicker.GALLERY

        // WHEN
        val imageLauncher = getValueForTesting(imageLauncherViewModel.imageLauncherEventLiveData)

        // THEN
        assertEquals(ImageLauncherEvent.OpenGallery, imageLauncher)

        verify(exactly = 1) { mockActionRepository.flushImagePickerFlow() }
    }

    @Test
    fun `open camera when get flow with CAMERA value`() {
        // GIVEN
        IMAGE_PICKER_FLOW.value = ImagePicker.CAMERA

        // WHEN
        val imageLauncher = getValueForTesting(imageLauncherViewModel.imageLauncherEventLiveData)

        // THEN
        assertEquals(ImageLauncherEvent.OpenCamera, imageLauncher)

        verify(exactly = 1) { mockActionRepository.flushImagePickerFlow() }
    }

    @Test
    fun `do nothing when get flow with null value`() {
        // GIVEN
        IMAGE_PICKER_FLOW.value = null

        // WHEN
        val isImageLaunched = isLiveDataTriggered(imageLauncherViewModel.imageLauncherEventLiveData)

        // THEN
        assertFalse(isImageLaunched)
    }

    @Test
    fun `set picture Uri when pick photo with success`() {
        // WHEN
        val mockUri = mockk<Uri>()
        imageLauncherViewModel.onPhotoPicked(uri = mockUri, success = true)

        // THEN
        verify(exactly = 1) { mockSetFormUseCase.setPictureUri(mockUri) }
    }

    @Test
    fun `do NOT set picture Uri when pick photo with failure`() {
        // WHEN
        val mockUri = mockk<Uri>()
        imageLauncherViewModel.onPhotoPicked(uri = mockUri, success = false)

        // THEN
        verify(exactly = 0) { mockSetFormUseCase.setPictureUri(mockUri) }
    }

    @Test
    fun `do NOT set picture Uri when pick photo with null Uri`() {
        // WHEN
        val mockUri = mockk<Uri>()
        imageLauncherViewModel.onPhotoPicked(uri = null)

        // THEN
        verify(exactly = 0) { mockSetFormUseCase.setPictureUri(mockUri) }
    }
}