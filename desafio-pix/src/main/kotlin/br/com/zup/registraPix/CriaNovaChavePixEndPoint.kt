package br.com.zup.registraPix

import br.com.zup.RegistraChavePixRequest
import br.com.zup.RegistraChavePixResponse
import br.com.zup.RegistraChavePixServiceGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton


@Singleton
class CriaNovaChavePixEndPoint: RegistraChavePixServiceGrpc.RegistraChavePixServiceImplBase() {

    override fun send(request: RegistraChavePixRequest, responseObserver: StreamObserver<RegistraChavePixResponse>) {

       val novaChavePixRequest = request.toRequest()


    }
}