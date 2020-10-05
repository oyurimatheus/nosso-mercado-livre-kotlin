package me.oyurimatheus.nossomercadolivrekotlin.usuarios

import org.springframework.data.repository.Repository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.util.Assert
import java.time.LocalDateTime
import javax.persistence.*


@Table(name = "usuarios")
@Entity
class Usuario(val email: String,
              senha: Senha) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    val senha: String = senha.senhaCriptografada
    lateinit var criadoEm: LocalDateTime

}

class Senha private constructor(senhaEmTextoPuro: String) {

    val senhaCriptografada: String

    init {
        Assert.isTrue(!senhaEmTextoPuro.isBlank(), "Senha nao pode estar em branco")
        this.senhaCriptografada = BCryptPasswordEncoder().encode(senhaEmTextoPuro)
    }

    companion object {
        fun criptografa(senhaEmTextoPuro: String): Senha {
            return Senha(senhaEmTextoPuro)
        }
    }
}

interface UsuarioRepository : Repository<Usuario, Long> {

    fun save(usuario: Usuario) : Usuario

    fun existsByEmail(email: String) : Boolean
}
