package br.com.zup.clients.itau;

import br.com.zup.ContaAssociada
import br.com.zup.Instituicao
import br.com.zup.TipoConta
import br.com.zup.Titular
import java.util.*

data class ClientItauResponse(
    val tipo: String,
    val instituicao: ClientInstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: ClientTitularResponse
) {
    fun toModelItau(tipoConta: TipoConta, clienteId: String): ContaAssociada{
        return ContaAssociada(
            tipoConta = tipoConta,
            instituicao = instituicao.toModel(),
            agencia = agencia,
            numero = numero,
            titular = titular.toModel(clienteId)
        )
    }


}

data class ClientTitularResponse(
    val nome: String, val cpf: String)
 {
    fun toModel(clienteId: String): Titular{
        return Titular(titularId = UUID.fromString(clienteId), cpf = cpf, nomeTitular = nome)
    }

}

data class ClientInstituicaoResponse(
    val nome: String, val ispb: String) {

    fun toModel(): Instituicao{
        return Instituicao(nomeInstituicao = nome, ispb = ispb)
    }

}
