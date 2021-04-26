package br.com.zup.carregaPix

import br.com.zup.*
import java.time.LocalDateTime
import java.util.*

data class ChavePixInfo(
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipoChave: TipoChave,
    val chave: String,
    val tipoConta: TipoConta,
    val contaInfo: ContaAssociadaInfo,
    val registradaEm: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun NovaChavePix.toInfo(): ChavePixInfo {
            return ChavePixInfo(
                pixId = id,
                clienteId = contaAssociada.titular.titularId,
                tipoChave = tipoChave,
                chave = chave!!,
                tipoConta = contaAssociada.tipoConta,
                contaInfo = ContaAssociadaInfo.toContaInfo(contaAssociada),
                registradaEm = criadoEm
            )
        }
    }
}

data class ContaAssociadaInfo(
    val instituicao: Instituicao,
    val agencia: String,
    val numeroConta: String,
    val titularInfo: TitularInfo
) {
    companion object {
        fun toContaInfo(contaAssociada: ContaAssociada): ContaAssociadaInfo {
            return ContaAssociadaInfo(
                instituicao = contaAssociada.instituicao,
                agencia = contaAssociada.agencia,
                numeroConta = contaAssociada.numero,
                titularInfo = TitularInfo(
                    contaAssociada.titular.nomeTitular,
                    cpf = contaAssociada.titular.cpf
                )
            )
        }
    }
}

data class TitularInfo(
    val nomeTitular: String,
    val cpf: String
)