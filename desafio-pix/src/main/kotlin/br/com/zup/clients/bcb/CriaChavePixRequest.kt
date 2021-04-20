package br.com.zup.clients.bcb;

import br.com.zup.NovaChavePix
import br.com.zup.clients.bcb.TipoChave.Companion.toKeyType
import br.com.zup.clients.bcb.TipoConta.Companion.toAccountType
import br.com.zup.TipoConta
import br.com.zup.TipoChave


data class CriaChavePixRequest(
    val tipoChave: br.com.zup.clients.bcb.TipoChave,
    val chave: String?,
    val contaBanco: ContaBanco,
    val titular: Titular
) {
    companion object {
        fun NovaChavePix.toBcb(): CriaChavePixRequest {
            return CriaChavePixRequest(
                chave = chave,
                tipoChave = toKeyType(tipoChave),
                contaBanco = ContaBanco(
                    titular = contaAssociada.instituicao.ispb,
                    agencia = contaAssociada.agencia,
                    tipoConta = toAccountType(contaAssociada.tipoConta),
                    numeroConta = contaAssociada.numeroConta
                ), titular = Titular(
                    tipoPessoa = TipoPessoa.toType("CPF"),
                    nome = contaAssociada.titular.nomeTitular,
                    numeroId = contaAssociada.titular.nomeTitular
                )

            )
        }


    }

}

class Titular(
    val tipoPessoa: TipoPessoa,
    val nome: String,
    val numeroId: String
) {

}

enum class TipoPessoa {
    PESSOA_FISICA,
    PESSOA_JURIDICA;

    companion object {
        fun toType(tipo: String): TipoPessoa {
            if (tipo == "CPF") return PESSOA_FISICA
            else throw IllegalArgumentException("Só cadastramos CPF")
        }
    }

}

class ContaBanco(
    val titular: String,
    val agencia: String,
    val numeroConta: String,
    val tipoConta: br.com.zup.clients.bcb.TipoConta
) {
}

enum class TipoConta {
    CONTA_CORRENTE,
    CONTA_POUPANCA;

    companion object {
        fun toAccountType(tipoconta: TipoConta): br.com.zup.clients.bcb.TipoConta {
            return when (tipoconta) {
               TipoConta.CONTA_CORRENTE -> CONTA_CORRENTE
                TipoConta.CONTA_POUPANCA -> CONTA_POUPANCA
                else -> throw IllegalArgumentException("Conta Corrente e Conta Poupança apenas")
            }
        }

    }
}

enum class TipoChave {
    CPF,
    CNPJ,
    CELULAR,
    EMAIL,
    ALEATORIA;

    companion object {
        fun toKeyType(tipoChave: TipoChave?): br.com.zup.clients.bcb.TipoChave {
            return when (tipoChave) {
                TipoChave.CPF -> CPF
                TipoChave.CELULAR -> CELULAR
                TipoChave.EMAIL -> EMAIL
                TipoChave.ALEATORIA -> ALEATORIA
                else ->
                    throw IllegalArgumentException("Não aceitamos CNPJ por enquanto")
            }
        }
    }
}

