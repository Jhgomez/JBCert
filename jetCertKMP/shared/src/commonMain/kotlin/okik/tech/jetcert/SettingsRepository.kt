package okik.tech.jetcert

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.asStateFlow
import eu.anifantakis.lib.ksafe.compose.mutableStateOf
import eu.anifantakis.lib.ksafe.invoke
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class SettingsRepository(
    private val settingsVault: KSafe
): KoinComponent {
    // for plain text only(no encryption)
    val settingsPreferences: KSafe by inject(named("prefs"))

    // The variable name is used as the storage key when no explicit key is supplied.
    var hasShownOnboarding: Boolean by settingsPreferences(true)
        private set // so we can only update it form this class

    val token by settingsVault("")

    var stars by settingsPreferences.mutableStateOf(0)
        private set

    val PERSON_KEY = "PERSON"
    val COUNTER = "COUNTER"

    suspend fun saveUser(person: Person) = settingsPreferences.put(PERSON_KEY, person)

    suspend fun getUser(person: Person) = settingsPreferences.get(PERSON_KEY, null)

    // 6. Direct API — non-suspend, hot-cache reads, background-flushed writes (~1000x faster for bulk ops)
    fun getCounter() = settingsPreferences.getDirect(COUNTER, 0)

    fun incrementCounter() = settingsPreferences.putDirect(
        COUNTER,
        getCounter() + 1
    )

    fun decrementCounter() = settingsPreferences.putDirect(
        COUNTER,
        getCounter() - 1
    )

    fun addStar() = stars++

    fun subtractStar() = stars--

    internal inline fun <reified T> getStateFlowToValue(
        defaultVal: T,
        coroutineScope: CoroutineScope,
        key: String
    ) = settingsVault.asStateFlow(defaultVal, coroutineScope, key)
}

@Serializable
data class Person(val name: String, val age: UByte)
