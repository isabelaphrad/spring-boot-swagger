package br.com.alura.forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.alura.forum.model.Topico;

//JpaRepository(entidade, tipoDeSuaChavePrimaria)
public interface TopicoRepository extends JpaRepository<Topico, Long> {

	//como se fosse a query sql (select)
	Page<Topico> findByCurso_Nome(String nomeCurso, Pageable paginacao);
	

	/*
	//Para criar um metodo do zero, sem ser padrao do jpa
	@Query("SELECT t FROM topico t WHERE t.curso.nome = :nomeCurso")
	List<Topico> carregarPorNome(@Param("nomeCurso")String nomeCurso);
	*/
}
