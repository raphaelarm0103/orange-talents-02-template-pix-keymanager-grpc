package br.com.zup.clients.bcb;

import java.time.LocalDateTime

class CriaChavePixResponse {

    data class CriarChavePixResponse(
        val tipoChave: TipoChave,
        val chave: String,
        val tipoConta: ContaBancoResponse,
        val titular: TitularResponse,
        val criadoEm: LocalDateTime
    )
}

data class TitularResponse(
    val tipoPessoa: TipoPessoa,
    val nome: String,
    val numeroId: String
)

data class ContaBancoResponse(
    val titular: String,
    val agencia: String,
    val numeroConta: String,
    val tipoConta: TipoConta
)
