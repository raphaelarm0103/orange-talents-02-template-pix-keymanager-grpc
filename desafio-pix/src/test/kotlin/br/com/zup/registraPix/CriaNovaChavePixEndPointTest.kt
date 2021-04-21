package br.com.zup.registraPix

import br.com.zup.*
import br.com.zup.clients.itau.ClientInstituicaoResponse
import br.com.zup.clients.itau.ClientItauResponse
import br.com.zup.clients.itau.ClientTitularResponse
import br.com.zup.clients.itau.ErpItauClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
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

    private fun novaChavePixEmail(): NovaChavePix{
        return NovaChavePix(
            tipoChave = TipoChave.EMAIL,
            chave = "raphael@gmail.com",
            contaAssociada = ContaAssociada(
                tipoConta = TipoConta.CONTA_CORRENTE,
                agencia = "0001",
                numero = "202020",
                titular = Titular(
                    titularId = CLIENTE_ID,
                    nomeTitular = "Raphael Marques",
                    cpf = "10158190602"),
                instituicao = Instituicao(
                    nomeInstituicao = "ITAU",
                    ispb = "60701190"
                )
            )
        )
    }

    private fun novaChavePixCelular(): NovaChavePix{
        return NovaChavePix(
            tipoChave = TipoChave.CELULAR,
            chave = "+5534988223432",
            contaAssociada = ContaAssociada(
                tipoConta = TipoConta.CONTA_CORRENTE,
                agencia = "0001",
                numero = "202020",
                titular = Titular(
                    titularId = CLIENTE_ID,
                    nomeTitular = "Raphael Marques",
                    cpf = "10158190602"),
                instituicao = Instituicao(
                    nomeInstituicao = "ITAU",
                    ispb = "60701190"
                )
            )
        )
    }


    @Test
    fun `Deve registrar uma nova chave pix CPF`() {

        `when`(itauClient.buscarContaPorId(clienteId = CLIENTE_ID.toString(), tipoConta = "CONTA_CORRENTE"))
            .thenReturn((HttpResponse.ok(dadosClienteItauResponse())))

        val response = grpcClient.send(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setTipoChaveEnum(TipoChaveEnum.CPF)
                // não precisa .setChave, pois a lógica não exige, pois já é o próprio CPF do cliente cadastrado no ERP
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(pixId)
            assertEquals("10158190602", dadosClienteItauResponse().titular.cpf)
        }


    }

    @Test
    fun `Deve registrar uma nova chave pix EMAIL`() {

        `when`(itauClient.buscarContaPorId(clienteId = CLIENTE_ID.toString(), tipoConta = "CONTA_CORRENTE"))
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
            assertEquals("raphael@gmail.com", novaChavePixEmail().chave)
        }


    }

    @Test
    fun `Deve registrar uma nova chave pix CELULAR`() {

        `when`(itauClient.buscarContaPorId(clienteId = CLIENTE_ID.toString(), tipoConta = "CONTA_CORRENTE"))
            .thenReturn((HttpResponse.ok(dadosClienteItauResponse())))

        val response = grpcClient.send(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setTipoChaveEnum(TipoChaveEnum.CELULAR)
                .setChave("+5534988223432")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(pixId)
            assertEquals("+5534988223432", novaChavePixCelular().chave )
        }
    }

    @Test
    fun `Deve registrar uma nova chave pix ALEATORIA`() {

        `when`(itauClient.buscarContaPorId(clienteId = CLIENTE_ID.toString(), tipoConta = "CONTA_CORRENTE"))
            .thenReturn((HttpResponse.ok(dadosClienteItauResponse())))

        val response = grpcClient.send(
            RegistraChavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setTipoChaveEnum(TipoChaveEnum.ALEATORIA)
                .setChave(" ")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(pixId)
        }
    }

    @Test
    fun `Nao deve registrar com dados invalidos`() {

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.send(RegistraChavePixRequest.newBuilder().build())
                    }
        with(thrown) {
           assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    fun `Nao deve registrar quando houver uma igual`() {

        `when`(itauClient.buscarContaPorId(clienteId = CLIENTE_ID.toString(), tipoConta = "CONTA_CORRENTE"))
            .thenReturn((HttpResponse.ok(dadosClienteItauResponse())))

        repository.save(novaChavePixCelular())


        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.send(
                RegistraChavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoChaveEnum(TipoChaveEnum.CELULAR)
                    .setChave("+5534988223432")
                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                    .build()
            )}

            with(thrown){
                assertNotNull(Status.ALREADY_EXISTS)

        }
    }


    @Factory
    class Clients {
        @Bean
        fun grpcRegistra(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RegistraChavePixServiceGrpc.RegistraChavePixServiceBlockingStub {
            return RegistraChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

}







