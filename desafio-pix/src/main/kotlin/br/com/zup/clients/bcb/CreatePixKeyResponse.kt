package br.com.zup.clients.bcb;

import java.time.LocalDateTime

class CreatePixKeyResponse (
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: LocalDateTime
    )

    data class BankAccountResponse(
        val participant: String,
        val branch: String,
        val accountNumber: String,
        val accountType: AccountType
    )

    data class OwnerResponse(
        val type: Type,
        val name: String,
        val taxIdNumber: String
    )