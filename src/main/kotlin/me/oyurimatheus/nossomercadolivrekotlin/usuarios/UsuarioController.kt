package me.oyurimatheus.nossomercadolivrekotlin.usuarios

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
}


data class NovoUsuarioRequest(@Email @NotBlank val login: String,
                              @NotBlank @Size(min = 6) val senha: String)
