package br.com.zup

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@Entity
class NovaChavePix(

    @Column(updatable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    val tipoChave: TipoChave,

    @Column(length = 77, unique = true)
    var chave: String,

    @Embedded
    val contaAssociada: ContaAssociada
){
    @Id
    @GeneratedValue
    var id: UUID? = null
}

