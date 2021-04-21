package br.com.zup.registraPix

import br.com.zup.*
import br.com.zup.clients.itau.ClientInstituicaoResponse
import br.com.zup.clients.itau.ClientItauResponse
import br.com.zup.clients.itau.ClientTitularResponse
import br.com.zup.clients.itau.ErpItauClient
import io.grpc.ManagedChannel
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.util.*
import javax.inject.Inject
import org.mockito.Mockito
import org.mockito.Mockito.`when`


@MicronautTest(transactional = false)
internal class CriaNovaChavePixEndPointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: RegistraChavePixServiceGrpc.RegistraChavePixServiceBlockingStub
) {
    @Inject
    lateinit var itauClient: ErpItauClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    @MockBean(ErpItauClient::class)
    fun erpItauClient(): ErpItauClient? {
        return Mockito.mock(ErpItauClient::class.java)
    }

    @BeforeEach
    internal fun setUp() {
        repository.deleteAll()
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }



    private fun dadosClienteItauResponse(): ClientItauResponse {
        return ClientItauResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = ClientInstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "0001",
            numero = "202020",
            titular = ClientTitularResponse("Raphael Marques", "10158190602")
        )
    }

    @Test
    fun `Deve registrar uma nova chave pix`() {

        `when`(erpItauClient()?.buscarContaPorId(clienteId = CLIENTE_ID.toString(), tipoConta = "CONTA_CORRENTE"))
            .thenReturn((HttpResponse.ok(dadosClienteItauResponse())))

        val response = grpcClient.send(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setTipoChaveEnum(TipoChaveEnum.EMAIL)
                .setChave("raphael@gmail.com")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(pixId)
        }


    }

    @Factory
    class Clients {
        fun grpcRegistra(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RegistraChavePixServiceGrpc.RegistraChavePixServiceBlockingStub {
            return RegistraChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

}






