package br.com.zup

import java.util.*
import javax.persistence.Embeddable

@Embeddable
class Titular(
    val titularId: UUID?,
    val nomeTitular: String,
    val cpf: String
) {

}
