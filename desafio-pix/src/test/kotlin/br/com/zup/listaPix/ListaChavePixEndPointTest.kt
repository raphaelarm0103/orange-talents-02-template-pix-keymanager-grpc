package br.com.zup.listaPix

import br.com.zup.*
import br.com.zup.carregaPix.CarregaChavePixEndPointTest
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
internal class ListaChavePixEndPointTest(private val repository: ChavePixRepository,
private val grpcClient: ListaChavePixServiceGrpc.ListaChavePixServiceBlockingStub){

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
        val CHAVE_ALEATORIA = UUID.randomUUID()
    }

    private fun listaChavePix(tipoChave: TipoChave, chave: String): NovaChavePix {
        return NovaChavePix(
            tipoChave = tipoChave,
            chave = chave,
            ContaAssociada(
                tipoConta = TipoConta.CONTA_CORRENTE,
                instituicao = Instituicao(
                    nomeInstituicao = "ITAU UNIBANCO SA",
                    ispb = "60701190"
                ),
                agencia = "0001",
                numero = "202020",
                titular = Titular(
                    titularId = CarregaChavePixEndPointTest.CLIENTE_ID,
                    nomeTitular = "Raphael Marques",
                    cpf = "10158191002"
                )
            )
        )
    }

    @BeforeEach
    internal fun setUp() {
        repository.save(listaChavePix(TipoChave.CPF, "10158191002"))
        repository.save(listaChavePix(TipoChave.EMAIL, "raphael@zup.com.br"))
        repository.save(listaChavePix(TipoChave.CELULAR, "+553497729203"))
        repository.save(listaChavePix(TipoChave.ALEATORIA, CarregaChavePixEndPointTest.CHAVE_ALEATORIA.toString()))
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }


    @Test
    fun `DEVE retornar lista de chaves de um usuario`() {
        grpcClient.lista(
            ListaChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .build()
        ).let {
            assertNotNull(it)
            assertEquals(CLIENTE_ID.toString(), it.clienteId)

        }

    }

    @Test
    fun `Nao deve retornar lista de chaves sem colocar o ID do cliente`() {
        assertThrows<StatusRuntimeException> {
            grpcClient.lista(
                ListaChavePixRequest.newBuilder()
                    .build()
            ).let { assertEquals(0, it.chavesCount) }
        }.let {
            assertEquals(Status.INVALID_ARGUMENT.code, it.status.code)
            assertEquals("clienteID preenchido incorretamente", it.status.description)
        }
    }

    @Factory
    class Clients {
        @Bean
        fun grpcRegistra(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ListaChavePixServiceGrpc.ListaChavePixServiceBlockingStub {
            return ListaChavePixServiceGrpc.newBlockingStub(channel)
        }
    }




}