
package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizaTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.model.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

	// Dto: quando a api recebe(trás) os dados
	// Form: quando a api envia os dados

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private CursoRepository cursoRepository;

	// get com lista com paginacao com ordenacao SIMPLIFICADA em CACHE
	@GetMapping // required = pode ter ou nao o parametro nomeCurso
	@Cacheable(value = "listaDeTopicos") // id do cache para diferencia-lo dos demais caches
	public Page<TopicoDto> lista(@RequestParam(required = false) String nomeCurso,
			@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10) Pageable paginacao) {

		if (nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		} else {

			// Curso_Nome: relacionamento curso e nome: anderline separa os relacionamentos
			// CursoNome: atributo CursoNome
			Page<Topico> topicos = topicoRepository.findByCurso_Nome(nomeCurso, paginacao);
			return TopicoDto.converter(topicos);

		}

	}
	
	
	//metodo post com cache ativo
	@PostMapping
	@Transactional // comitar ao final do método, atualizando as info no banco
	@CacheEvict(value = "listaDeTopicos", allEntries = true) //atualizar o cache
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);

		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}

	@GetMapping("/{id}")
	public ResponseEntity<DetalhesTopicoDto> detalhar(@PathVariable Long id) {
		// optional-> recebe ou nao um registro
		Optional<Topico> topico = topicoRepository.findById(id);

		if (topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesTopicoDto(topico.get())); // .get() -> pegar o topico dentro de
																			// optional
		}

		return ResponseEntity.notFound().build();
	}
	
	//put com cache
	@PutMapping("/{id}")
	@Transactional // comitar ao final do método, atualizando as info no banco
	@CacheEvict(value = "listaDeTopicos", allEntries = true) //atualizar o cache
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizaTopicoForm form) {
		// optional-> recebe ou nao um registro
		Optional<Topico> optional = topicoRepository.findById(id);

		if (optional.isPresent()) {
			Topico topico = form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}

		return ResponseEntity.notFound().build();

	}
	
	//delete com cache
	@DeleteMapping("/{id}") // ? -> ResponseEntity n'ao possui tipo, pois o metodo nao retorna um obj
	@Transactional // comitar ao final do método, atualizando as info no banco
	@CacheEvict(value = "listaDeTopicos", allEntries = true) //atualizar o cache
	public ResponseEntity<?> remover(@PathVariable Long id) {
		// optional-> recebe ou nao um registro
		Optional<Topico> optional = topicoRepository.findById(id);

		if (optional.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build();
	}
	
	
}
