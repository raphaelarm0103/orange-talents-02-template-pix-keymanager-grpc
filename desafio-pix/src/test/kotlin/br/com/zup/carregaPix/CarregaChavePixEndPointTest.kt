package br.com.zup.carregaPix

import br.com.zup.*
import br.com.zup.clients.bcb.*
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class CarregaChavePixEndPointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: CarregaChavePixServiceGrpc.CarregaChavePixServiceBlockingStub
) {

    @Inject
    lateinit var clientBcb: ClientBcb

    lateinit var chaveCpf: NovaChavePix
    lateinit var chaveEmail: NovaChavePix
    lateinit var chaveCelular: NovaChavePix
    lateinit var chaveAleatoria: NovaChavePix


    @MockBean(ClientBcb::class)
    fun bcbClient(): ClientBcb? {
        return Mockito.mock(ClientBcb::class.java)
    }

    @Factory
    class Clients {
        @Bean
        fun grpcRegistra(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CarregaChavePixServiceGrpc.CarregaChavePixServiceBlockingStub {
            return CarregaChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
        val CHAVE_ALEATORIA = UUID.randomUUID()
    }


    private fun criaChavePix(tipoChave: TipoChave, chave: String): NovaChavePix {
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
                    titularId = CLIENTE_ID,
                    nomeTitular = "Raphael Marques",
                    cpf = "10158191002"
                )
            )
        )
    }


    private fun criaPixDetailResponse(chavePix: NovaChavePix): DetailPixResponse {
        return DetailPixResponse(
            keyType = KeyType.toKeyType(chavePix.tipoChave),
            key = chavePix.chave!!,
            bankAccount = BankAccountResponse(
                participant = chavePix.contaAssociada.instituicao.ispb,
                branch = chavePix.contaAssociada.agencia,
                accountNumber = chavePix.contaAssociada.numero,
                accountType = AccountType.toAccountType(chavePix.contaAssociada.tipoConta)
            ),
            owner = OwnerResponse(
                type = Type.toType("CPF"),
                name = chavePix.contaAssociada.titular.nomeTitular,
                taxIdNumber = chavePix.contaAssociada.titular.cpf
            ), createdAt = chavePix.criadoEm
        )
    }

    @BeforeEach
    internal fun setUp() {
        chaveCpf = repository.save(criaChavePix(TipoChave.CPF, "10158191002"))
        chaveEmail = repository.save(criaChavePix(TipoChave.EMAIL, "raphael@zup.com.br"))
        chaveCelular = repository.save(criaChavePix(TipoChave.CELULAR, "+553497729203"))
        chaveAleatoria = repository.save(criaChavePix(TipoChave.ALEATORIA, CHAVE_ALEATORIA.toString()))
    }

    @AfterEach
    internal fun tearDown() {
        repository.deleteAll()
    }


    @Test
    fun `Deve carregar uma chave Pix por pixID e clienteID`() {
        `when`(clientBcb.carregaPorChave(chaveCpf.chave!!)).thenReturn(HttpResponse.ok(criaPixDetailResponse(chaveCpf)))

        grpcClient.carrega(
            CarregaChavePixRequest.newBuilder()
                .setPixEClienteId(
                    CarregaChavePixRequest.FiltroPorPixEClienteId.newBuilder()
                        .setPixId(chaveCpf.id.toString())
                        .setClienteId(chaveCpf.contaAssociada.titular.titularId.toString())
                        .build()
                ).build()
        ).let {
            assertEquals(chaveCpf.id.toString(), it.pixId)
            assertEquals(chaveCpf.tipoChave.name, it.chavePix.tipoChave.name)
            assertEquals(chaveCpf.chave, it.chavePix.chave)
        }

    }


    @Test
    fun `Nao deve carregar uma chave pix com dados Invalidos`(){
        assertThrows<StatusRuntimeException> {
            grpcClient.carrega(
                CarregaChavePixRequest.newBuilder()
                    .setPixEClienteId(
                        CarregaChavePixRequest.FiltroPorPixEClienteId.newBuilder()
                            .setPixId("")
                            .setClienteId("")
                            .build()
                    )
                    .build()
            )
        }.let {
            assertEquals(Status.INVALID_ARGUMENT.code, it.status.code)
        }
    }

    @Test
    fun `NAO deve carregar chave Pix, quando não houver preenchimento dos campos`() {

        assertThrows<StatusRuntimeException> {
            grpcClient.carrega(CarregaChavePixRequest.newBuilder().build())
        }.let {
            assertEquals(Status.INVALID_ARGUMENT.code, it.status.code)
            assertEquals("Chave Pix inválida ou não informada", it.status.description)
        }
    }


}



