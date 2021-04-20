package br.com.zup

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@Entity
class NovaChavePix(

    @Enumerated(EnumType.STRING)
    @Column(unique = true, updatable = false)
    val tipoChave: TipoChave,

    @Column(length = 77, unique = true)
    var chave: String,

    @Embedded
    val contaAssociada: ContaAssociada
){
    @Id
    @GeneratedValue
    var id: UUID? = null

    @Column(updatable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now()
}

