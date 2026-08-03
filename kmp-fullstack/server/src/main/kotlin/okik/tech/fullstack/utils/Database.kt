package okik.tech.fullstack.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

inline suspend fun <T> dbQuery(crossinline block: suspend CoroutineScope.() -> T): T = suspendTransaction {
    withContext(Dispatchers.IO) {
        block()
    }
}