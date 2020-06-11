package br.com.alura.forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.forum.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	//Iptional -> pode ser que ache o usuario ou nao
	Optional<Usuario> findByEmail(String email);
}
