import com.dekonoplyov.getExtendedKeyCodeForChar
import org.junit.Test
import java.awt.event.KeyEvent
import kotlin.test.*


class KeyCodesTest {
    @Test
    fun checkLowerCase() {
        val unicodeOWithDots = 0x010000f6
        assertEquals(getKeyCode("ö"), unicodeOWithDots)
        assertEquals(getKeyCode("ö"), getKeyCode("Ö".toLowerCase()))
    }

    // Reason why I've implemented my getExtendedKeyCodeForChar
    // parsed unicode points for ö and Ö are the same
    @Test
    fun checkBuiltInWrongBehaviour() {
        val unicodeOWithDots = 0x010000f6
        assertNotEquals(KeyEvent.getExtendedKeyCodeForChar(toInt("ö")), unicodeOWithDots)
        assertEquals(KeyEvent.getExtendedKeyCodeForChar(toInt("ö")),
                KeyEvent.getExtendedKeyCodeForChar(toInt("Ö")))
    }

    @Test
    fun basicParse() {
        assertEquals(getKeyCode("/"), KeyEvent.VK_SLASH)
        assertEquals(getKeyCode("s"), KeyEvent.VK_S)
        assertEquals(getKeyCode("1"), KeyEvent.VK_1)
    }

    @Test
    fun unknownKeyCodes() {
        assertEquals(getExtendedKeyCodeForChar(4242), KeyEvent.VK_UNDEFINED)
    }

    private fun getKeyCode(str: String): Int {
        return getExtendedKeyCodeForChar(toInt(str))
    }

    private fun toInt(str: String): Int {
        assertEquals(str.length, 1)
        return str[0].toInt()
    }
}