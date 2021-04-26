package br.com.zup.listaPix

import br.com.zup.*
import br.com.zup.execptions.handlers.ErrorHandler
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@ErrorHandler
@Singleton
class ListaChavePixEndPoint(@Inject private val repository: ChavePixRepository): ListaChavePixServiceGrpc.ListaChavePixServiceImplBase() {

    override fun lista(request: ListaChavePixRequest, responseObserver: StreamObserver<ListaChavePixResponse>) {
        if (request.clienteId.isNullOrBlank()) {
            throw IllegalArgumentException("clienteID preenchido incorretamente")
        }


        val chavesPorId = repository.findAllByContaAssociadaTitularTitularId(UUID.fromString(request.clienteId))

        val chavesResponse = chavesPorId.map {
            ListaChavePixResponse.ChavePixLista.newBuilder()
                .setPixId(it.id.toString())
                .setTipoChave(TipoChaveEnum.valueOf(it.tipoChave.name))
                .setChave(it.chave)
                .setTipoConta(TipoConta.valueOf(it.contaAssociada.tipoConta.name))
                .setCriadaEm(it.criadoEm.let {
                    val createdAt = it.atZone(ZoneId.of("GMT-3")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
                .build()
        }

            responseObserver.onNext(
                ListaChavePixResponse.newBuilder()
                    .setClienteId(request.clienteId)
                    .addAllChaves(chavesResponse)
                    ?.build()
            )
            responseObserver.onCompleted()
        }

    }




