package br.com.zup.carregaPix

import br.com.zup.CarregaChavePixRequest
import br.com.zup.CarregaChavePixResponse
import br.com.zup.CarregaChavePixServiceGrpc
import br.com.zup.ChavePixRepository
import br.com.zup.clients.bcb.ClientBcb
import br.com.zup.clients.itau.ErpItauClient
import br.com.zup.execptions.handlers.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import io.micronaut.validation.Validated
import javax.validation.Validator


@ErrorHandler
@Singleton
class CarregaChavePixEndPoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val clienteBcb: ClientBcb,
    @Inject private val validator: Validator
) : CarregaChavePixServiceGrpc.CarregaChavePixServiceImplBase() {

    override fun carrega(request: CarregaChavePixRequest, responseObserver: StreamObserver<CarregaChavePixResponse>) {
        val chaveInfo = request.toModel(validator).filtra(repository, clienteBcb)
        val carregaChavePixResponse: CarregaChavePixResponse = CarregaChavePixResponseConverter.toResponse(chaveInfo)

        responseObserver.onNext(carregaChavePixResponse)
        responseObserver.onCompleted()
    }

}