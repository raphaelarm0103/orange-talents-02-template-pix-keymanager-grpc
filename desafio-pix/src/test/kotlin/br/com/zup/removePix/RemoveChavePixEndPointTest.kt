package br.com.zup.removePix

import br.com.zup.*

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*


@MicronautTest(transactional = false)
internal class RemoveChavePixEndPointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub
) {


    lateinit var CHAVE_EXISTENTE: NovaChavePix

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repository.save(
            NovaChavePix(
                tipoChave = TipoChave.EMAIL,
                chave = "raphael@gmail.com",
                contaAssociada = ContaAssociada(
                    tipoConta = TipoConta.CONTA_CORRENTE,
                    agencia = "0001",
                    numero = "202020",
                    titular = Titular(
                        titularId = CLIENTE_ID,
                        nomeTitular = "Raphael Marques",
                        cpf = "10158190602"
                    ),
                    instituicao = Instituicao(
                        nomeInstituicao = "ITAU",
                        ispb = "60701190"
                    )
                )
            )
        )
    }

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
    }


    @Test
    fun `Deve remover uma chave pix existente`() {

        val response = grpcClient.remove(
            RemoveChavePixRequest.newBuilder()
                .setPixID(CHAVE_EXISTENTE.id.toString())
                .setCliendId(CLIENTE_ID.toString())
                .build()
        ).let {
            assertEquals(0, repository.findAll().size)
            assertEquals("Chave pix removida com sucesso", it.mensagem)
        }
    }

    @Test
    fun `Nao deve retornar quando nao encontrar o ID pix`() {

        assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                (RemoveChavePixRequest.newBuilder()
                    .setPixID("112312333aaa-12313")
                    .setCliendId(CHAVE_EXISTENTE.id.toString())
                    .build())
            )
        }.let {
            assertEquals(Status.INVALID_ARGUMENT.code, it.status.code)
            assertEquals(1, repository.findAll().size)
        }

    }

    @Test
    fun `Nao Deve retornar quando nao encontrar ID cliente`(){
        assertThrows<StatusRuntimeException> {
            grpcClient.remove(
                (RemoveChavePixRequest.newBuilder()
                    .setPixID(CLIENTE_ID.toString())
                    .setCliendId("Cliente ID Errado")
                    .build())
            )
        }.let {
            assertEquals(Status.INVALID_ARGUMENT.code, it.status.code)
            assertEquals(1, repository.findAll().size)
        }
    }


    @Factory
    class Clients {
        @Bean
        fun grpcRemove(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub {
            return RemoveChavePixServiceGrpc.newBlockingStub(channel)
        }
    }
}