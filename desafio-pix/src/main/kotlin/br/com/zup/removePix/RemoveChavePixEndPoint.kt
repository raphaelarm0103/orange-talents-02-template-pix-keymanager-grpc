package br.com.zup.removePix

import br.com.zup.RemoveChavePixRequest
import br.com.zup.RemoveChavePixResponse
import br.com.zup.RemoveChavePixServiceGrpc
import br.com.zup.execptions.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton


@ErrorHandler
@Singleton
class RemoveChavePixEndPoint(@Inject val service: RemoveChavePixService):
    RemoveChavePixServiceGrpc.RemoveChavePixServiceImplBase() {

    override fun remove(request: RemoveChavePixRequest,
                        responseObserver: StreamObserver<RemoveChavePixResponse>?) {


        service.remove(pixId = request.pixID, clienteId = request.cliendId )

        responseObserver?.onNext(RemoveChavePixResponse.newBuilder()
                            .setMensagem("Chave pix removida com sucesso")
                            .build())

        responseObserver?.onCompleted()


    }
}