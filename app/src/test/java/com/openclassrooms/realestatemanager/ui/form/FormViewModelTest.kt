package com.openclassrooms.realestatemanager.ui.form

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.domain.form.CheckFormErrorUseCase
import com.openclassrooms.realestatemanager.domain.form.FormInfo
import com.openclassrooms.realestatemanager.domain.form.GetFormUseCase
import com.openclassrooms.realestatemanager.domain.form.SetFormUseCase
import com.openclassrooms.realestatemanager.domain.real_estate.SaveRealEstateUseCase
import com.openclassrooms.realestatemanager.ui.util.CoroutineProvider
import com.openclassrooms.realestatemanager.util.TestCoroutineRule
import com.openclassrooms.realestatemanager.util.TestLifecycle.getValueForTesting
import com.openclassrooms.realestatemanager.util.TestLifecycle.isLiveDataTriggered
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FormViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockGetFormUseCase: GetFormUseCase

    @MockK
    private lateinit var mockActionRepository: ActionRepository

    @MockK
    private lateinit var mockCheckFormUseCase: CheckFormErrorUseCase

    @MockK
    private lateinit var mockSetFormUseCase: SetFormUseCase

    @MockK
    private lateinit var mockSaveRealEstateUseCase: SaveRealEstateUseCase

    @MockK
    private lateinit var mockCoroutineProvider: CoroutineProvider

    @MockK
    private lateinit var mockApplication: Application

    private lateinit var formViewModel: FormViewModel

    companion object {
        // region IN
        private val MODIFIED_ADD_FORM_INFO = FormInfo(
            formType = FormInfo.FormType.ADD,
            isModified = true,
            estateType = "Manor"
        )
        // endregion IN
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { mockGetFormUseCase.getFormInfo() } returns MODIFIED_ADD_FORM_INFO
        every { mockActionRepository.getPictureOpenerFlow() } returns flowOf()
        every { mockCoroutineProvider.getIoDispatcher() } returns testCoroutineRule.testCoroutineDispatcher
        every { mockCoroutineProvider.getMainDispatcher() } returns testCoroutineRule.testCoroutineDispatcher
        justRun { mockSetFormUseCase.updateAgent(any()) }
        justRun { mockSetFormUseCase.updateMarketEntryDate(any()) }
        justRun { mockSetFormUseCase.updateSaleDate(any()) }
        justRun { mockSetFormUseCase.updateAvailability(any()) }
        justRun { mockSetFormUseCase.reset() }
        justRun { mockSetFormUseCase.initForm() }
        every { mockApplication.getString(R.string.draft_form_dialog_title) } returns "Draft"
        every { mockApplication.getString(R.string.exit_form_dialog_title) } returns "Exit"
        every {
            mockApplication.getString(R.string.draft_form_dialog_message, "Manor")
        } returns "Started: Manor"
        every { mockApplication.getString(R.string.exit_add_form_dialog_message) } returns "Keep?"
        every { mockApplication.getString(R.string.exit_edit_form_dialog_message) } returns "Save?"
        every { mockApplication.getString(R.string.draft_form_dialog_positive_button) } returns "Resume draft"
        every { mockApplication.getString(R.string.exit_form_dialog_positive_button) } returns "Yes"
        every { mockApplication.getString(R.string.draft_form_dialog_negative_button) } returns "Ignore draft"
        every { mockApplication.getString(R.string.exit_form_dialog_negative_button) } returns "No"
        every { mockApplication.getString(R.string.toolbar_title_add) } returns "Add estate"
        every { mockApplication.getString(R.string.button_text_save) } returns "SAVE"
        every { mockApplication.getString(R.string.button_text_next) } returns "NEXT"
        every { mockCheckFormUseCase.containsNoError(any<Int>()) } returns true
        coJustRun { mockSaveRealEstateUseCase.invoke() }

        formViewModel = getViewModel()
    }

    @After
    fun tearDown() {
        verify { mockGetFormUseCase.getFormInfo() }
        verify { mockApplication.getString(any()) }
        verify { mockApplication.getString(any(), any()) }
        verify { mockActionRepository.getPictureOpenerFlow() }
        verify { mockCoroutineProvider.getIoDispatcher() }
        confirmVerified(
            mockGetFormUseCase,
            mockActionRepository,
            mockCheckFormUseCase,
            mockSetFormUseCase,
            mockSaveRealEstateUseCase,
            mockCoroutineProvider,
            mockApplication,
        )
    }

    @Test
    fun `show draft dialog when current form is a modified ADD form`() {
        // WHEN
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(
            FormEvent.ShowDialog(
                type = FormViewModel.DialogType.SAVE_DRAFT,
                title = "Draft",
                message = "Started: Manor",
                positiveButtonText = "Resume draft",
                negativeButtonText = "Ignore draft"
            ),
            formEvent
        )
    }

    @Test
    fun `do NOT show draft dialog when current form is an unmodified ADD form`() {
        // GIVEN
        every {
            mockGetFormUseCase.getFormInfo()
        } returns MODIFIED_ADD_FORM_INFO.copy(isModified = false)
        val viewModel = getViewModel()

        // WHEN
        val isShowDialogEventTriggered = isLiveDataTriggered(viewModel.formEventLiveData)

        // THEN
        assertFalse(isShowDialogEventTriggered)
    }

    @Test
    fun `do NOT show draft dialog when current form is an EDIT form`() {
        // GIVEN
        every {
            mockGetFormUseCase.getFormInfo()
        } returns MODIFIED_ADD_FORM_INFO.copy(formType = FormInfo.FormType.EDIT)
        val viewModel = getViewModel()

        // WHEN
        val isShowDialogEventTriggered = isLiveDataTriggered(viewModel.formEventLiveData)

        // THEN
        assertFalse(isShowDialogEventTriggered)
    }

    @Test
    fun `show picture when get opener request with non-null Boolean value`() {
        // GIVEN
        every { mockActionRepository.getPictureOpenerFlow() } returns flowOf(true)
        val viewModel = getViewModel()

        // WHEN
        val formEvent = getValueForTesting(viewModel.formEventLiveData)

        // THEN
        assertEquals(FormEvent.ShowPicture, formEvent)
    }

    @Test
    fun `update view state when change page to non-last one`() {
        // GIVEN
        formViewModel.onInitPagerAdapter(4)

        // WHEN
        formViewModel.onPageChanged(1)
        val viewState = getValueForTesting(formViewModel.viewStateLiveData)

        // THEN
        assertEquals(
            FormViewState(
                toolbarTitle = "Add estate (2/4)",
                submitButtonText = "NEXT"
            ),
            viewState
        )
    }

    @Test
    fun `update view state when change page to last one`() {
        // GIVEN
        formViewModel.onInitPagerAdapter(7)

        // WHEN
        formViewModel.onPageChanged(6)
        val viewState = getValueForTesting(formViewModel.viewStateLiveData)

        // THEN
        assertEquals(
            FormViewState(
                toolbarTitle = "Add estate (7/7)",
                submitButtonText = "SAVE"
            ),
            viewState
        )
    }

    @Test
    fun `create estate & exit when click on submit button with no errors on last page`() = runTest {
        // GIVEN
        formViewModel.onInitPagerAdapter(10)
        formViewModel.onPageChanged(9)

        // WHEN
        formViewModel.onSubmitButtonClicked()
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(FormEvent.ExitActivity, formEvent)

        verify(exactly = 1) { mockCheckFormUseCase.containsNoError(9) }
        coVerify(exactly = 1) { mockSaveRealEstateUseCase.invoke() }
        verify(exactly = 1) { mockCoroutineProvider.getMainDispatcher() }
        verify(exactly = 1) { mockSetFormUseCase.reset() }
    }

    @Test
    fun `go to next page when click on submit button with no errors on non-last page`() {
        // GIVEN
        formViewModel.onInitPagerAdapter(8)
        formViewModel.onPageChanged(5)

        // WHEN
        formViewModel.onSubmitButtonClicked()

        // THEN
        verify(exactly = 1) { mockCheckFormUseCase.containsNoError(5) }
    }

    @Test
    fun `do nothing when click on submit button with errors on current page`() {
        // GIVEN
        every { mockCheckFormUseCase.containsNoError(any<Int>()) } returns false
        formViewModel.onPageChanged(1)

        // WHEN
        formViewModel.onSubmitButtonClicked()

        // THEN
        verify(exactly = 1) { mockCheckFormUseCase.containsNoError(1) }
    }

    @Test
    fun `go to previous page when go back with current page greater than 0`() {
        // GIVEN
        formViewModel.onPageChanged(7)

        // WHEN
        formViewModel.onGoBack()
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(FormEvent.GoToPage(6), formEvent)
    }

    @Test
    fun `show EXIT dialog when go back with current page equals 0 in modified ADD form type`() {
        // GIVEN
        formViewModel.onPageChanged(0)

        // WHEN
        formViewModel.onGoBack()
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(
            FormEvent.ShowDialog(
                type = FormViewModel.DialogType.EXIT_FORM,
                title = "Exit",
                message = "Keep?",
                positiveButtonText = "Yes",
                negativeButtonText = "No"
            ),
            formEvent
        )
    }

    @Test
    fun `show EXIT dialog when go back with current page equals 0 in modified EDIT form type`() {
        // GIVEN
        every {
            mockGetFormUseCase.getFormInfo()
        } returns MODIFIED_ADD_FORM_INFO.copy(formType = FormInfo.FormType.EDIT)
        formViewModel.onPageChanged(0)

        // WHEN
        formViewModel.onGoBack()
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(
            FormEvent.ShowDialog(
                type = FormViewModel.DialogType.EXIT_FORM,
                title = "Exit",
                message = "Save?",
                positiveButtonText = "Yes",
                negativeButtonText = "No"
            ),
            formEvent
        )
    }

    @Test
    fun `exit when go back with current page equals 0 in unmodified form`() {
        // GIVEN
        every {
            mockGetFormUseCase.getFormInfo()
        } returns MODIFIED_ADD_FORM_INFO.copy(isModified = false)
        formViewModel.onPageChanged(0)

        // WHEN
        formViewModel.onGoBack()
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(FormEvent.ExitActivity, formEvent)
    }

    @Test
    fun `show EXIT dialog when click on close MenuItem with modified ADD form type`() {
        // WHEN
        formViewModel.onCloseMenuItemClicked()
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(
            FormEvent.ShowDialog(
                type = FormViewModel.DialogType.EXIT_FORM,
                title = "Exit",
                message = "Keep?",
                positiveButtonText = "Yes",
                negativeButtonText = "No"
            ),
            formEvent
        )
    }

    @Test
    fun `show EXIT dialog when click on close MenuItem with modified EDIT form type`() {
        // GIVEN
        every {
            mockGetFormUseCase.getFormInfo()
        } returns MODIFIED_ADD_FORM_INFO.copy(formType = FormInfo.FormType.EDIT)

        // WHEN
        formViewModel.onCloseMenuItemClicked()
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(
            FormEvent.ShowDialog(
                type = FormViewModel.DialogType.EXIT_FORM,
                title = "Exit",
                message = "Save?",
                positiveButtonText = "Yes",
                negativeButtonText = "No"
            ),
            formEvent
        )
    }

    @Test
    fun `exit when click on close MenuItem with unmodified form`() {
        // GIVEN
        every {
            mockGetFormUseCase.getFormInfo()
        } returns MODIFIED_ADD_FORM_INFO.copy(isModified = false)

        // WHEN
        formViewModel.onCloseMenuItemClicked()
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(FormEvent.ExitActivity, formEvent)
    }

    @Test
    fun `exit when click on EXIT dialog positive button with no errors on 1st page`() {
        // WHEN
        formViewModel.onDialogPositiveButtonClicked(FormViewModel.DialogType.EXIT_FORM)
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(FormEvent.ExitActivity, formEvent)

        verify { mockCheckFormUseCase.containsNoError(0) }
    }

    @Test
    fun `do NOT exit when click on EXIT dialog positive button with errors on 1st page`() {
        // GIVEN
        every { mockCheckFormUseCase.containsNoError(0) } returns false

        // WHEN
        formViewModel.onDialogPositiveButtonClicked(FormViewModel.DialogType.EXIT_FORM)

        // THEN
        verify { mockCheckFormUseCase.containsNoError(0) }
    }

    @Test
    fun `reset form & exit when click on EXIT dialog negative button`() {
        // WHEN
        formViewModel.onDialogNegativeButtonClicked(FormViewModel.DialogType.EXIT_FORM)
        val formEvent = getValueForTesting(formViewModel.formEventLiveData)

        // THEN
        assertEquals(FormEvent.ExitActivity, formEvent)

        verify { mockSetFormUseCase.reset() }
    }

    @Test
    fun `reset then init form when click on DRAFT dialog negative button`() {
        // WHEN
        formViewModel.onDialogNegativeButtonClicked(FormViewModel.DialogType.SAVE_DRAFT)

        // THEN
        verifySequence {
            mockSetFormUseCase.reset()
            mockSetFormUseCase.initForm()
        }
    }

    private fun getViewModel() = FormViewModel(
        mockGetFormUseCase,
        mockActionRepository,
        mockCheckFormUseCase,
        mockSetFormUseCase,
        mockSaveRealEstateUseCase,
        mockCoroutineProvider,
        mockApplication,
    )
}