package br.com.zup.clients.bcb;


import br.com.zup.clients.bcb.CriaChavePixRequest
import br.com.zup.clients.bcb.CriaChavePixResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client


@Client("\${bcb.url}")
interface ClientBcb {

    @Post("api/v1/pix/keys", processes = arrayOf(MediaType.APPLICATION_XML))
    fun cadastraChavePix(@Body request: CriaChavePixRequest): HttpResponse<CriaChavePixResponse>
}