package br.com.zup.registraPix

import br.com.zup.ContaAssociada
import br.com.zup.NovaChavePix
import br.com.zup.TipoChave
import br.com.zup.TipoConta
import br.com.zup.validacoes.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
data class NovaChavePixRequest(

    @ValidUUID
    @field:NotBlank val clienteId: String,
    @field:NotNull val tipoChave: TipoChave,
    @field:Size(max =77) val chave: String?,
    @field:NotNull val tipoConta: TipoConta

) {
    fun toModel(contaAssociada: ContaAssociada): NovaChavePix {
        return NovaChavePix(
            tipoChave = TipoChave.valueOf(this.tipoChave.name),
            chave = when(tipoChave){
                TipoChave.ALEATORIA -> UUID.randomUUID().toString()
                TipoChave.CPF -> contaAssociada.titular.cpf
                else -> chave!!
            },

            contaAssociada = contaAssociada
        )

    }
}