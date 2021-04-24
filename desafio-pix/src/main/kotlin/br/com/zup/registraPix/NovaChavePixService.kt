package br.com.zup.registraPix

import br.com.zup.ChavePixRepository
import br.com.zup.NovaChavePix
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.clients.bcb.ClientBcb
import br.com.zup.clients.bcb.CreatePixKeyRequest.Companion.toBcb
import br.com.zup.clients.itau.ErpItauClient
import br.com.zup.execptions.ChavePixExistenteException
import br.com.zup.execptions.TipoChaveInvalidoException
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientException
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid


@Singleton
@Validated
class NovaChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ErpItauClient,
    @Inject val clientBcb: ClientBcb
) {
    private val Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChavePixRequest: NovaChavePixRequest): NovaChavePix {


        // 1 -Verifica se a chave é UNKNOWN
        if (novaChavePixRequest.tipoConta == TipoConta.CONTA_DESCONHECIDA) {
            throw TipoChaveInvalidoException("Tipo de conta inválido")
        }

        // 2 - Verifica se já existe a chave no banco
        if (repository.existsByChave(novaChavePixRequest.chave))
            throw ChavePixExistenteException("Chave pix ${novaChavePixRequest.tipoChave}: ${novaChavePixRequest.chave} já existente")


        // 3 - Busca os dados no ERP do Itau
        val chavePix: NovaChavePix
        try {
            val response =
                itauClient.buscarContaPorId(novaChavePixRequest.clienteId, novaChavePixRequest.tipoConta.name)
            if (response.status !== HttpStatus.OK) {
                throw IllegalArgumentException("Cliente nao cadastrado no ERP Itau")
            }
            val contaAssociada = response.body()!!.toModelItau(novaChavePixRequest.clienteId, novaChavePixRequest.tipoConta)
           chavePix = novaChavePixRequest.toModel(contaAssociada)
        } catch (e: HttpClientResponseException) {
            throw IllegalArgumentException("Id do cliente não encontrado")
        } catch (e: HttpClientException) {
            throw IllegalStateException("Não foi possível conectar ao sistema ItauErp, tente mais tarde")
        }

        // 4 - Verifica se o CPF já está cadastrado
        if (chavePix.tipoChave == TipoChave.CPF && repository.existsByChave(chavePix.contaAssociada.titular.cpf)) {
            throw ChavePixExistenteException("Chave CPF já cadastrada")
        }

        //salvando no banco e conectando ao client do BCB para registro
        repository.save(chavePix)
        val bcbRequest = chavePix.toBcb()
        try {
            val bcbResponse = clientBcb.cadastraChavePix(bcbRequest)
            if (bcbResponse.status == HttpStatus.CREATED) {
                chavePix.atualizaChave(bcbResponse.body())
            }
        } catch (e: HttpClientResponseException) {
            if (e.status == HttpStatus.UNPROCESSABLE_ENTITY) throw ChavePixExistenteException("Chave pix já cadastrada no BCB")

        } catch (e: HttpClientException) {
            throw IllegalStateException("Não foi possível conectar ao sistema BCB, tente mais tarde")
        }

        return chavePix

    }
}

//        //Conta não pode ser UNKNOWN, (Tipo de chave está sendo validada dentro do Enum TipoChave)
//        if (novaChavePixRequest.tipoConta == TipoConta.CONTA_DESCONHECIDA) {
//            throw TipoChaveInvalidoException("Tipo de conta inválido")
//        }
//
//        //validando se a chave já existe, pois não é permitido cadastro de chaves duplicadas
//        if (repository.existsByChave(novaChavePixRequest.chave)) {
//            throw ChavePixExistenteException("Chave ${novaChavePixRequest.tipoChave}: ${novaChavePixRequest.chave} já cadastrada")
//        }
//
//        //Conexão com Client Itau Erp
//        val chavePix: NovaChavePix
//        try {
//            val clientResponse =
//                itauClient.buscarContaPorId(novaChavePixRequest.clienteId, novaChavePixRequest.tipoConta.name)
//            if(clientResponse.status != HttpStatus.OK){
//                throw IllegalArgumentException("Id do cliente não encontrado no ItauErp")
//            }
//            val contaAssociada = clientResponse.body()!!.toModelItau(novaChavePixRequest.tipoConta, novaChavePixRequest.clienteId)
//            chavePix = novaChavePixRequest.toModel(contaAssociada)
//        } catch (e: HttpClientResponseException) {
//            throw IllegalArgumentException("Id do cliente não encontrado")
//        } catch (e: HttpClientException) {
//            throw IllegalStateException("Não foi possível conectar ao sistema ItauErp, tente mais tarde")
//        }
//
//        //Verifica se já existe a chave CPF, pois a mesma não precisa ser inserida no request por ser cadastrada com o próprio CPF do cliente.
//        // então passa direto no teste de cima
//        if (chavePix.tipoChave == TipoChave.CPF && repository.existsByChave(chavePix.contaAssociada.titular.cpf)) {
//            throw ChavePixExistenteException("Chave CPF já cadastrada")
//        }
//
//        //salvando no banco e conectando ao client do BCB para registro
//        repository.save(chavePix)
//        val bcbRequest = chavePix.toBcb()
//        try {
//            val bcbResponse = clientBcb.cadastraChavePix(bcbRequest)
//            if (bcbResponse.status == HttpStatus.CREATED) {
//                chavePix.atualizaChave(bcbResponse.body())
//            }
//        } catch (e: HttpClientResponseException) {
//            if (e.status == HttpStatus.UNPROCESSABLE_ENTITY) throw ChavePixExistenteException("Chave pix já cadastrada no BCB")
//
//        } catch (e: HttpClientException) {
//            throw IllegalStateException("Não foi possível conectar ao sistema BCB, tente mais tarde")
//        }
//
//        return chavePix
//    }




















