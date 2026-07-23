import okik.tech.AnalyticsClient
import okik.tech.ViewModel
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any


import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.BeforeTest

class ViewModelTest {

//    private val analyticsClient = Mockito.mock(AnalyticsClient::class.java)
    private lateinit var analyticsClient: AnalyticsClient
    private lateinit var viewModel: ViewModel

    @BeforeTest
    fun setUp() {
        analyticsClient = mock()
        viewModel = ViewModel(analyticsClient)

        // whenever and any are available through idiomatic mockito kotlin but it is just an example to define a return
        // value if needed
        //        whenever(analyticsClient.track(any<String>())).thenReturn(Unit)

        // what we actually want to do is verify behavior, again just verify a function is called, it seem we can override
        // the mock object before each test runs by defining that logic here or it can be directly inside each test,
        // this time ill do it inside the test
//        verify(analyticsClient.track(any<String>()))
    }

    // since we want to verify if the interface's(AnalyticsClient) trackEvent() method is called
    // when we call the viewmodel's submitClickEvent(), so we need to create a mock of the interface
    // we could create an anonymous object that implements the interface or an actual class in a file
    // that implements it, we will see later both approaches but in this test we will use Mockito to
    // to verify this behavior without actually implementing the interface, basically is a mock instance
    // of the interface
    @Test
    fun `trackEvent() is called when submitClick() is called `() {
        viewModel.submitClick()
        verify(analyticsClient).track(any<String>())
    }
}