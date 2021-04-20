package br.com.zup

import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class ContaAssociada (

    @Enumerated(EnumType.STRING)
    val tipoConta: TipoConta,

    val agencia: String,

    val numeroConta: String,

    @Embedded
    val titular: Titular,

    @Embedded
    val instituicao: Instituicao

){

}
    @Embeddable
    data class Instituicao(
    val nomeInstituicao: String,
    val ispb: String) {

    }




