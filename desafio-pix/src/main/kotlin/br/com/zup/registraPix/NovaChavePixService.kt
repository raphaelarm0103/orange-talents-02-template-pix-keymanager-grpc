package br.com.zup.registraPix

import br.com.zup.ChavePixRepository
import br.com.zup.NovaChavePix
import br.com.zup.TipoChave
import br.com.zup.clients.bcb.CriaChavePixRequest.Companion.toBcb
import br.com.zup.clients.itau.ErpItauClient
import br.com.zup.execptions.ChavePixExistenteException
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

        // 1 - Verifica se já existe a chave no banco
        if (repository.existsByChave(novaChavePixRequest.chave))
            throw ChavePixExistenteException("Chave pix ${novaChavePixRequest.chave} já existente")

        // 2 - Busca os dados no ERP do Itau
        val response = itauClient.buscarContaPorId(novaChavePixRequest.clienteId, novaChavePixRequest.tipoConta!!.name)
        val conta = response.body()?.toModelItau(novaChavePixRequest.tipoConta, novaChavePixRequest.clienteId) ?: throw IllegalArgumentException("Cliente não foi encontrado")


        // 3 - Verifica se o CPF já está cadastrado
        val novaChavePix: NovaChavePix = novaChavePixRequest.toModel(conta)
        if (novaChavePixRequest.tipoChave == TipoChave.CPF && repository.existsByChave(novaChavePix.contaAssociada.titular.cpf)) {
            throw ChavePixExistenteException("Chave CPF já cadastrada")
        }

        // 4 - Salva a chave no banco
        val chave = novaChavePixRequest.toModel(conta)
        repository.save(chave)

        return chave

    }



}


