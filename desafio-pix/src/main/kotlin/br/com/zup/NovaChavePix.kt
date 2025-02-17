package br.com.zup

import br.com.zup.clients.bcb.CreatePixKeyResponse
import br.com.zup.validacoes.ValidPixKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@Entity
@ValidPixKey
class NovaChavePix(

    @Enumerated(EnumType.STRING)
    @Column(unique = true, updatable = false)
    val tipoChave: TipoChave,

    @Column(length = 77, unique = true)
    var chave: String,

    @Embedded
    val contaAssociada: ContaAssociada
){
    fun atualizaChave(body: CreatePixKeyResponse?) {
        if (tipoChave == TipoChave.ALEATORIA) {
            this.chave = body!!.key
        }
    }

    @Id
    @GeneratedValue
    var id: UUID? = null

    @Column(updatable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now()
}

