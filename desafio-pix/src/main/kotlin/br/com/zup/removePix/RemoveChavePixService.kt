package br.com.zup.removePix

import br.com.zup.ChavePixRepository
import br.com.zup.clients.itau.ErpItauClient
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
class RemoveChavePixService( @Inject val repository: ChavePixRepository,
//                             @Inject val itauClient: ErpItauClient
) {
    @Transactional
    fun remove(
        @NotBlank @ValidUUID(message = "pix ID no formato invalido") pixId: String?,
        @NotBlank @ValidUUID(message = "cliente ID no formato inválido") clienteId: String?
    ) {

        val novaChavePix = repository.findByIdAndContaAssociadaTitularTitularId(UUID.fromString(pixId), UUID.fromString(clienteId))
            .orElseThrow {ChavePixNaoEncontradaException("Chave pix não encontrada ou não cadastrada")}

        repository.delete(novaChavePix)

    }


}