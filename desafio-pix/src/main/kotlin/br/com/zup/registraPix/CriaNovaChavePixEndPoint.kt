package br.com.zup.registraPix

import br.com.zup.RegistraChavePixRequest
import br.com.zup.RegistraChavePixResponse
import br.com.zup.RegistraChavePixServiceGrpc
import br.com.zup.execptions.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@ErrorHandler
class CriaNovaChavePixEndPoint(@Inject private val service: NovaChavePixService ): RegistraChavePixServiceGrpc.RegistraChavePixServiceImplBase() {

    override fun send(request: RegistraChavePixRequest, responseObserver: StreamObserver<RegistraChavePixResponse>?) {

        val novaChavePixRequest = request.toRequest()
        val chaveCriada = service.registra(novaChavePixRequest)

        responseObserver?.onNext(RegistraChavePixResponse.newBuilder()
            .setPixId(chaveCriada.id.toString())
            .build())
        responseObserver?.onCompleted()
    }


}