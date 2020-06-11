package br.com.alura.forum.config.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.model.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;

public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

	private TokenService tokenService;
	private UsuarioRepository usuarioRepository;
	
	public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
		this.tokenService = tokenService;
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// a autenticacao eh feita a cada requisicao atraves do token

		// recuperar o token do cabecalho
		String token = recuperarToken(request);

		// validar o token
		boolean valido = tokenService.isTokenValido(token);

		// Autenticar o usuario
		if (valido) {
			autenticarCliente(token);
		}

		//se nao estiver valido, segue o fluxo da requisicao e entao o spring vai barrar a requisicao
		filterChain.doFilter(request, response);

	}

	private void autenticarCliente(String token) {
		
		//Atraves de seu token buscar o id do usuario para acessar suas informacoes
		Long idUsuario = tokenService.getIdUsuario(token);
		
		//recuperar o usuario atraves de seu id no banco de dados
		Usuario usuario = usuarioRepository.findById(idUsuario).get();
		
		//criar autenticacao
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
		
		//forcando a autenticacao
		SecurityContextHolder.getContext().setAuthentication(authentication); 
	}

	private String recuperarToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");

		if (token == null || token.isEmpty() || !token.startsWith("Bearer")) {
			return null;
		}

		// retorna somente o token que eh a partir do 8 caractere
		return token.substring(7, token.length());

	}

}
