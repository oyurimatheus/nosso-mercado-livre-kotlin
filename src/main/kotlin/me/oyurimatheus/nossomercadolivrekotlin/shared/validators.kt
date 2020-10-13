package me.oyurimatheus.nossomercadolivrekotlin.shared

import org.springframework.validation.Errors
import org.springframework.validation.Validator
import kotlin.reflect.KFunction1

class CampoUnicoValidator<T, P>(val campo: String,
                          val codigoDeErro: String,
                          val classeParaValidacao: Class<T>,
                          val funcaoDeBusca: KFunction1<P, Boolean>) : Validator {
    override fun supports(clazz: Class<*>): Boolean {
        return classeParaValidacao.isAssignableFrom(clazz)
    }

    @Suppress("UNCHECKED_CAST")
    override fun validate(target: Any, errors: Errors) {
        val campoParaValidar = classeParaValidacao.getDeclaredField(campo)
        campoParaValidar.isAccessible = true

        val valorDoCampo = campoParaValidar.get(target) as P

        val existe = funcaoDeBusca.call(valorDoCampo)

        if (existe) {
            errors.rejectValue(campo, codigoDeErro, String.format("%s ja esta registrado", campo));
        }
    }
}