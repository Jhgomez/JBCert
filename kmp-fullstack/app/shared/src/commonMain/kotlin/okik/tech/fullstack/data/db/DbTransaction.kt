package okik.tech.fullstack.data.db

import app.cash.sqldelight.SuspendingTransactionWithReturn
import app.cash.sqldelight.SuspendingTransactionWithoutReturn
import okik.tech.fullstack.db.FullstackDb

interface DbTransaction {
    suspend fun <R> transactionWithResult(bodyWithReturn: suspend SuspendingTransactionWithReturn<R>.() -> R): R
    suspend fun transaction(body: suspend SuspendingTransactionWithoutReturn.() -> Unit)
}

class DbTransactionImpl(private val database: FullstackDb): DbTransaction {
    override suspend fun <R> transactionWithResult(
        bodyWithReturn: suspend SuspendingTransactionWithReturn<R>.() -> R
    ): R = database.transactionWithResult(bodyWithReturn = bodyWithReturn)

    override suspend fun transaction(body: suspend SuspendingTransactionWithoutReturn.() -> Unit) {
        database.transaction(body = body)
    }

}
