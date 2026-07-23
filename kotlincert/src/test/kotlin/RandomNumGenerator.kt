import okik.tech.Prueba
import okik.tech.main
import okik.tech.randonNumberGenerator
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

class RandomNumGenerator {

    @Test
    fun verifyValueIsGenerated() {
        assert(true == true)
    }

    @Test
    fun `generate random number between 0 and 100`() {
        val randon = randonNumberGenerator(100)
        assert(randon in 0..100)
    }

    @Test
    fun `generate random throws exception when negative value is passed`() {
        assertThrowsExactly(
            IllegalArgumentException::class.java,
            {
                randonNumberGenerator(-100)
            },
            "make sure you enter a negative number to verify exception assertion"
        )
    }
}