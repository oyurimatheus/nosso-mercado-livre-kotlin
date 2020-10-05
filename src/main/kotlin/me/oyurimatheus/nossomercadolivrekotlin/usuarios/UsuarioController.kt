package me.oyurimatheus.nossomercadolivrekotlin.usuarios

import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


@RestController
@RequestMapping("/api/usuarios")
class UsuarioController(val usuarioRepository: UsuarioRepository) {

    @PostMapping
    fun cria(@RequestBody @Valid novoUsuario: NovoUsuarioRequest) : ResponseEntity<Any> {
        val senha: Senha = Senha.criptografa(novoUsuario.senha)
        val usuario = Usuario(novoUsuario.login, senha)

        usuarioRepository.save(usuario)

        val location: URI = URI.create("/api/users/" + usuario.id)
        return ResponseEntity.created(location).build()
    }

    @InitBinder(value = [ "novoUsuarioRequest" ])
    fun initBinder(binder: WebDataBinder) {
        binder.addValidators(LoginUsuarioValidator(usuarioRepository))
    }

}

class LoginUsuarioValidator(val usuarioRepository: UsuarioRepository) : Validator {

    override fun validate(obj: Any, errors: Errors) {
        val (login, _) = obj as NovoUsuarioRequest

        if (usuarioRepository.existsByEmail(login)) {
            errors.rejectValue("login", "usuario.login.jaCadastrado", "esse email ja esta cadastrado")
        }
    }

    override fun supports(klass: Class<*>): Boolean {
        return NovoUsuarioRequest::class.java.isAssignableFrom(klass)
    }

}


data class NovoUsuarioRequest(@Email @NotBlank val login: String,
                              @NotBlank @Size(min = 6) val senha: String)
