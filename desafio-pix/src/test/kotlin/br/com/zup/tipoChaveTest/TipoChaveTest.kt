package br.com.zup.tipoChaveTest

import br.com.zup.TipoChave
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveTest {

    @Nested
    inner class ChaveCpfTest {

        @Test
        fun `Deve ser valido quando nao passar a chave CPF`() {

            val tipoChaveEnum = TipoChave.CPF

            assertTrue(tipoChaveEnum.valida(null))
            assertTrue(tipoChaveEnum.valida(""))
        }
    }

    @Nested
    inner class ChaveAleatoriaTest {

        @Test
        fun `Deve ser valido quando nao passar chave ALEATORIA`() {

            val tipoChaveEnum = TipoChave.ALEATORIA

            assertTrue(tipoChaveEnum.valida(null))
            assertTrue(tipoChaveEnum.valida(""))
        }


        @Test
        fun `Nao deve ser valido quando passar uma chave ALEATORIA`() {

            val tipoChaveEnum = TipoChave.ALEATORIA

            assertFalse(tipoChaveEnum.valida("chave"))
        }
    }

    @Nested
    inner class ChaveCelularTest {

        @Test
        fun `Deve ser valido quando tiver o preenchimento correto com a Regex`() {

            val tipoChaveEnum = TipoChave.CELULAR

            assertTrue(tipoChaveEnum.valida("+5534988223432"))

        }


    @Test
    fun `Nao deve ser valido quando preenchido com celular incorreto`() {

        val tipoChaveEnum = TipoChave.CELULAR

        assertFalse(tipoChaveEnum.valida("11111"))
        assertFalse(tipoChaveEnum.valida("aa552344"))
        assertFalse(tipoChaveEnum.valida(""))
        assertFalse(tipoChaveEnum.valida(null))
    }
}

    @Nested
    inner class ChaveEmailTest {

        @Test
        fun `Deve ser valido quando preenchido o email corretamente`() {

            val tipoChaveEnum = TipoChave.EMAIL

            assertTrue(tipoChaveEnum.valida("raphael@gmail.com"))

        }


        @Test
        fun `Nao deve ser valido quando o email preenchido incorretamente ou nulo`() {

            val tipoChaveEnum = TipoChave.EMAIL

            assertFalse(tipoChaveEnum.valida("errado"))
            assertFalse(tipoChaveEnum.valida(null))
            assertFalse(tipoChaveEnum.valida("@errado.com"))
            assertFalse(tipoChaveEnum.valida("errado.errado.com"))

        }

    }
}