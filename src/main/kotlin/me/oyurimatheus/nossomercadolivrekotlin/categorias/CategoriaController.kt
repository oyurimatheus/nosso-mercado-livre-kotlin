package me.oyurimatheus.nossomercadolivrekotlin.categorias

import me.oyurimatheus.nossomercadolivrekotlin.shared.CampoUnicoValidator
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import kotlin.reflect.KFunction1


@RestController
@RequestMapping("/api/categorias")
class CategoriaController(val categoriaRepository: CategoriaRepository) {

    @PostMapping
    fun criar(@RequestBody @Valid novaCategoria: NovaCategoriaRequest,
              uriBuilder: UriComponentsBuilder) : ResponseEntity<Any> {
        val categoria = novaCategoria.paraCategoria(categoriaRepository::findById)

        categoriaRepository.save(categoria)

        val uri = uriBuilder.path("/api/categorias/{id}").buildAndExpand(categoria.id).toUri()

        return ResponseEntity.created(uri).build();
    }

    @InitBinder(value = ["newCategoryRequest"])
    fun initBinder(binder: WebDataBinder) {
        binder.addValidators(CampoUnicoValidator("name",
                                                 "category.name.alreadyExists",
                                                 NovaCategoriaRequest::class.java,
                                                 categoriaRepository::existsByNome),
                SuperCategoryExistsValidator(categoriaRepository))
    }
}

data class NovaCategoriaRequest(@field:NotEmpty val nome: String,
                                @field:Min(1) val superCategoriaId: Long?) {

    fun paraCategoria(procuraPorId: KFunction1<Long, Categoria?>) : Categoria {

        if (superCategoriaId != null) {
            val superCategoria = procuraPorId.call(superCategoriaId)
            return Categoria(nome = nome, superCategoria = superCategoria)
        }

        return Categoria(nome)
    }
}

class SuperCategoryExistsValidator(val categoryRepository: CategoriaRepository) : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return NovaCategoriaRequest::class.java.isAssignableFrom(clazz)
    }

    override fun validate(target: Any, errors: Errors) {
        val novaCategoria = target as NovaCategoriaRequest

        novaCategoria.superCategoriaId?.let {
            val superCategoryId: Long = novaCategoria.superCategoriaId

            if (!categoryRepository.existsById(superCategoryId)) {
                errors.rejectValue("superCategory", "category.superCategory", "The super category does not exists")
            }
        }
    }

}

