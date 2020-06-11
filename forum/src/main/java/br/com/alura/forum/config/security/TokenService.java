package br.com.alura.forum.config.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.alura.forum.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class TokenService {

	// Jwts -> Api -> Json Web Token

	// Para anotacoes do properties eh utilizado @Value
	@Value("${forum.jwt.expiration}")
	private String expiration;

	@Value("${forum.jwt.secret}")
	private String secret;

	public String gerarToken(Authentication authentication) {

		Usuario logado = (Usuario) authentication.getPrincipal();
		Date hoje = new Date();

		// somar a data atual com o tempo de expiracao
		Date dataExpiracao = new Date(hoje.getTime() + Long.parseLong(expiration));

		return Jwts.builder().setIssuer("API do forum da Alura") // qual a api que fez a geracao do token
				.setSubject(logado.getId().toString()) // usuario -> quem eh o dono do token
				.setIssuedAt(hoje).setExpiration(dataExpiracao).signWith(SignatureAlgorithm.HS256, secret).compact();
	}

	// fazer a validacao do token
	public boolean isTokenValido(String token) {
		// parse -> discriptografar o token para fazer a validacao dele
		// setSigningKey() -> secret -> passa a chave para discriptografar
		// parseClaimsJws() -> devolve um objeto onde [e possivel recuperar o token e
		// suas informacoes nele contida

		// se token invalido, dispara uma exception
		try {
			Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public Long getIdUsuario(String token) {
		// atraves do token devolver o id do usuario ligado a ele
		Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
		return Long.parseLong(claims.getSubject());
	}

}
