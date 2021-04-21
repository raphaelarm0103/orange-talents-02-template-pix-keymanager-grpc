package br.com.zup.execptions.handlers

import br.com.zup.execptions.TipoChaveInvalidoException
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class TipoChaveInvalidoExceptionHandler : ExceptionHandler<TipoChaveInvalidoException> {

    override fun handle(e: TipoChaveInvalidoException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is TipoChaveInvalidoException
    }

}