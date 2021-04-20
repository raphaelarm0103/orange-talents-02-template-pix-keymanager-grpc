package br.com.zup.registraPix

import br.com.zup.RegistraChavePixRequest
import br.com.zup.TipoChave
import br.com.zup.TipoConta

fun RegistraChavePixRequest.toRequest(): NovaChavePixRequest {

    return NovaChavePixRequest(
        clienteId = clienteId,
        tipoChave = TipoChave.valueOf(tipoChaveEnum.name),
        chave = chave,
        tipoConta = TipoConta.valueOf(tipoConta.name)
    )

}