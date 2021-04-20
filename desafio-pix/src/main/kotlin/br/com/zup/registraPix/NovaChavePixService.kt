package br.com.zup.registraPix

import br.com.zup.ChavePixRepository
import br.com.zup.NovaChavePix
import br.com.zup.TipoChave
import br.com.zup.clients.itau.ErpItauClient
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid


@Singleton
@Validated
class NovaChavePixService(@Inject val repository: ChavePixRepository,
                          @Inject val itauClient: ErpItauClient) {

    private val Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChavePixRequest: NovaChavePixRequest): NovaChavePix {

        if (repository.existsByChave(novaChavePixRequest.chave))
            throw IllegalArgumentException("Chave pix ${novaChavePixRequest.chave} já existente")

        val response = itauClient.buscarContaPorId(novaChavePixRequest.clienteId, novaChavePixRequest.tipoConta!!.name)
        val conta = response.body()?.toModelItau(novaChavePixRequest.tipoConta, novaChavePixRequest.clienteId) ?: throw IllegalArgumentException("Cliente não foi encontrado")

        val novaChavePix: NovaChavePix = novaChavePixRequest.toModel(conta)
        if (novaChavePixRequest.tipoChave == TipoChave.CPF && repository.existsByChave(novaChavePix.contaAssociada.titular.cpf)) {
            throw IllegalArgumentException("Chave CPF já cadastrada")
        }

        val chave = novaChavePixRequest.toModel(conta)
        repository.save(chave)

        return chave
    }

}


