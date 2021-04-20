package br.com.zup.clients.itau

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import java.net.http.HttpResponse

@Client("\${itau.url}")
interface ErpItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas?tipo={tipoConta}")
    fun buscarContaPorId(
        @PathVariable clienteId: String,
        @QueryValue tipoConta: String
    ) {}

}