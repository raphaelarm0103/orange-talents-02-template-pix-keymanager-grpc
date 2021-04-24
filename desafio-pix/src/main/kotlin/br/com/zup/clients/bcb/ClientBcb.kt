package br.com.zup.clients.bcb;



import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client


@Client("\${bcb.url}")
interface ClientBcb {

    @Post("/api/v1/pix/keys", processes = [MediaType.APPLICATION_XML])
    fun cadastraChavePix(@Body request: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>}

//    @Delete("/api/v1/pix/keys/{key}", processes = [MediaType.APPLICATION_XML])
//    fun removeChavePix(@Body request: RemoveChavePixRequest): HttpResponse<RemoveChavePixResponse>
