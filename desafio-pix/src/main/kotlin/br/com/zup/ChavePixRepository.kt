package br.com.zup

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<NovaChavePix, UUID> {

    fun existsByChave(chave: String?): Boolean

    fun findByChave(chave: String): Optional<NovaChavePix>

    fun findByIdAndContaAssociadaTitularTitularId (fromString: UUID?, fromString1: UUID?): Optional<NovaChavePix>

    fun finAllByContaAssociadaTitularTitularId(titularId: UUID): Optional<NovaChavePix>


}