package br.com.zup.removePix

import br.com.zup.ChavePixRepository
import br.com.zup.clients.bcb.ClientBcb
import br.com.zup.clients.bcb.DeleteChavePixRequest
import br.com.zup.execptions.ChavePixNaoEncontradaException
import br.com.zup.validacoes.ValidUUID
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank


@Singleton
@Validated
class RemoveChavePixService(
    @Inject private val repository: ChavePixRepository,
    @Inject private val clientBcb: ClientBcb

) {
    @Transactional
    fun remove(
        @NotBlank @ValidUUID(message = "pix ID no formato invalido") pixId: String?,
        @NotBlank @ValidUUID(message = "cliente ID no formato inválido") clienteId: String?
    ): String {

        val novaChavePix =
            repository.findByIdAndContaAssociadaTitularTitularId(UUID.fromString(pixId), UUID.fromString(clienteId))
                .orElseThrow { ChavePixNaoEncontradaException("Chave pix não encontrada ou não cadastrada") }

        val valorChave = "${novaChavePix.tipoChave}: ${novaChavePix.chave}"

        try {
            clientBcb.removeChavePix(
                novaChavePix.chave,
                DeleteChavePixRequest(
                    key = novaChavePix.chave,
                    participant = novaChavePix.contaAssociada.instituicao.ispb
                )
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("Não foi possivel deletar a chave")
        }

        repository.delete(novaChavePix)

        return valorChave

    }
}