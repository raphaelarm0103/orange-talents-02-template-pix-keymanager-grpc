package br.com.zup.clients.bcb

import java.time.LocalDateTime

class DeleteChavePixResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
) {
}