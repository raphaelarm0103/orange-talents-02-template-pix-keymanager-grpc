package br.com.zup.carregaPix

import br.com.zup.ChavePix
import br.com.zup.ChavePixRepository
import br.com.zup.NovaChavePix
import br.com.zup.carregaPix.ChavePixInfo.Companion.toInfo
import br.com.zup.clients.bcb.ClientBcb
import br.com.zup.execptions.ChavePixNaoEncontradaException
import br.com.zup.validacoes.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpException
import io.micronaut.http.exceptions.HttpStatusException
import java.net.http.HttpResponse
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

sealed class Filtro {

    abstract fun filtra(repository: ChavePixRepository, clientBcb: ClientBcb): ChavePixInfo

    @Introspected
    data class PorPixEclienteId(
        @field:NotBlank @field:ValidUUID(message = "ClienteId inválido") val clienteId: String,
        @field:NotBlank @field:ValidUUID(message = "PixId inválido") val pixId: String,
    ) : Filtro() {
        override fun filtra(repository: ChavePixRepository, clientBcb: ClientBcb): ChavePixInfo {
            val novaChavePix =
                repository.findByIdAndContaAssociadaTitularTitularId(UUID.fromString(pixId), UUID.fromString(clienteId))
                    .orElseThrow {
                        ChavePixNaoEncontradaException("A Chave Pix não foi encontrada")
                    }
            try {
                clientBcb.carregaPorChave(novaChavePix.chave)
            } catch (e: HttpStatusException) {
                throw IllegalArgumentException("Não conseguimos encontrar a chave no Bcb")
            } catch (e: HttpException) {
                throw IllegalArgumentException("Não conseguimos encontrar a chave no Bcb")
            }

            return novaChavePix.toInfo()
        }
    }


    @Introspected
    data class PorChave(@field:NotBlank(message = "Chave deve ser preenchida") @field:Size(max = 77) val chave: String) :
        Filtro() {
        override fun filtra(repository: ChavePixRepository, bcbClient: ClientBcb): ChavePixInfo {
            val chavePix: NovaChavePix
            if (chave.isNullOrBlank()) {
                throw IllegalArgumentException("Chave deve ser preenchida")
            }
            try {
                val response = bcbClient.carregaPorChave(chave)
                if (response.status == HttpStatus.OK) {
                    chavePix = repository.findByChave(chave).orElseGet {
                        bcbClient.carregaPorChave(chave).body()!!.toModel()
                    }
                } else {
                    throw IllegalArgumentException("Não foi possível encontrar a chave no BCB, tente novamente")
                }
            } catch (e: HttpStatusException) {
                throw IllegalArgumentException("Não foi possível encontrar a chave no BCB, tente novamente")
            } catch (e: HttpException) {
                throw IllegalStateException("Não foi possível consultar a chave no BCB, tente novamente")
            }
            return chavePix.toInfo()
        }

    }


    @Introspected
    object Invalido : Filtro() {
        override fun filtra(repository: ChavePixRepository, bcbClient: ClientBcb): ChavePixInfo {
            throw IllegalArgumentException("Chave Pix inválida ou não informada")
        }
    }

}

