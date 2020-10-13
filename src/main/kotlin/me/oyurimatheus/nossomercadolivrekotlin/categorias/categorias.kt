package me.oyurimatheus.nossomercadolivrekotlin.categorias

import org.springframework.data.repository.Repository
import javax.persistence.*

@Table(name = "categorias")
@Entity
class Categoria(@Column(name = "nome") val nome: String,
                @ManyToOne @JoinColumn(name = "super_categoria_id") val superCategoria: Categoria? = null) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}

interface CategoriaRepository : Repository<Categoria, Long> {

    fun save(categoria: Categoria) : Categoria

    fun findById(id: Long) : Categoria?
    fun existsById(superCategoryId: Long): Boolean
    fun existsByNome(nome: String): Boolean
}
