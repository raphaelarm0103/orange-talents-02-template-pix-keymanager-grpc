package br.com.zup.clients.bcb;

import br.com.zup.CarregaChavePixResponse
import br.com.zup.RemoveChavePixRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client


@Client("\${bcb.url}")
interface ClientBcb {

    @Post("/api/v1/pix/keys", processes = [MediaType.APPLICATION_XML])
    fun cadastraChavePix(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>


    @Delete("/api/v1/pix/keys/{key}", processes = [MediaType.APPLICATION_XML])
    fun removeChavePix(@PathVariable key: String, @Body request: DeleteChavePixRequest): HttpResponse<DeleteChavePixResponse>

    @Get("/api/v1/pix/keys/{key}", processes = [MediaType.APPLICATION_XML])
    fun carregaPorChave(@PathVariable key: String): HttpResponse<DetailPixResponse>

}