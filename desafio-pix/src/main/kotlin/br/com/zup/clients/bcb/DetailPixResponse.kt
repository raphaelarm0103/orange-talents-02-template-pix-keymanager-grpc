package br.com.zup.clients.bcb

import br.com.zup.ContaAssociada
import br.com.zup.Instituicao
import br.com.zup.NovaChavePix
import br.com.zup.Titular
import br.com.zup.carregaPix.ChavePixInfo
import br.com.zup.carregaPix.ContaAssociadaInfo
import br.com.zup.carregaPix.TitularInfo
import java.time.LocalDateTime

data class DetailPixResponse(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountResponse,
    val owner: OwnerResponse,
    val createdAt: LocalDateTime
) {
    fun toInfo(): ChavePixInfo {
        return ChavePixInfo(
            tipoChave = KeyType.toTipoChave(keyType),
            chave = key,
            tipoConta = AccountType.toTipoConta(bankAccount.accountType),
            contaInfo = ContaAssociadaInfo(
                instituicao = Instituicao(
                    nomeInstituicao = Instituicoes.nome(bankAccount.participant),
                    ispb = bankAccount.participant
                ), agencia = bankAccount.branch,
                numeroConta = bankAccount.accountNumber,
                titularInfo = TitularInfo(
                    nomeTitular = owner.name,
                    cpf = owner.taxIdNumber
                )
            )
        )
    }

    fun toModel(): NovaChavePix {
        return NovaChavePix(
            tipoChave = KeyType.toTipoChave(keyType),
            chave = key,
            contaAssociada = ContaAssociada(
                tipoConta = AccountType.toTipoConta(bankAccount.accountType),
                instituicao = Instituicao(
                    nomeInstituicao = Instituicoes.nome(bankAccount.participant),
                    ispb = bankAccount.participant
                ),
                agencia = bankAccount.branch,
                numero = bankAccount.accountNumber,
                titular = Titular(
                    titularId = null,
                    nomeTitular = owner.name,
                    cpf = owner.taxIdNumber
                )
            )
        )

    }
}