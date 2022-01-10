package project.restapi.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import project.restapi.ApplicationContextLoad;
import project.restapi.model.Usuario;
import project.restapi.repository.UsuarioRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {
	
	//validade do token de dois dias (tempo em milesegundos)
	private static final long EXPIRATION_TIME = 172800000;
	
	//Uma senha unica para compor a autenticação e ajudar na segurança
	private static final String SECRET = "SenhaExtremamenteSecreta";
	
	//Prefixo padrão de Token
	private static final String TOKEN_PREFIX = "Bearer";
	
	//Prefixo que retorna para resposta
	private static final String HEADER_STRING = "Authorization";
	
	//quando faz primeira autorização
	//gerando token de autenticação e adicionando cabeçalho e resposta Http
	public void addAuthentication(HttpServletResponse response, String username) throws IOException{
		
		//montagem do token
		String JWT = Jwts.builder() //chama gerador de token
				.setSubject(username) // adiciona usuario
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //tempo de expiração
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); //compatactação e algoritmo de geração
		
		//junta o token com o prefixo
		String token = TOKEN_PREFIX + " " + JWT; //Bearer 464a4df84fr8x4sa64cdas64dfsa4d4as4cxw
		
		//adiciona um cabçalho http
		response.addHeader(HEADER_STRING, token); //Authorization: Bearer 464a4df84fr8x4sa64cdas64dfsa4d4as4cxw
		
		 ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
			.atualizaTokenUser(JWT, username);
		
		liberacaoCors(response);
		
		//Escreve token como resposta no corpo do http
		response.getWriter().write("{\"Authorization\":\""+token+"\"}");
	
	}
	
	//quando já fez alguma autorização, verifica se tem permissão para acesso
	//retorna o usuário validado com token ou caso não seja valido retorna null
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		
		//Pega o token enviado no cabeçalho http
		String token = request.getHeader(HEADER_STRING);
		
		
		try {

			// faz a validação do token do usuario na requisição
			if (token != null) {

				String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();

				String user = Jwts.parser().setSigningKey(SECRET) // Bearer 464a4df84fr8x4sa64cdas64dfsa4d4as4cxw
						.parseClaimsJws(tokenLimpo) // 464a4df84fr8x4sa64cdas64dfsa4d4as4cxw
						.getBody().getSubject(); // aqui retornaria exemplo: rafael martins
				if (user != null) {

					Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
							.findUserByLogin(user);

					if (usuario != null) {

						if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {
							return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
									usuario.getAuthorities());
						}
					}
				}
			} // fim da condição

		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			try {
			response.getOutputStream().println("Seu TOKEN está expirado, "
					+ "faça o login ou informe u novo TOKEN para autenticação");
			} catch (Exception e2) {}
		}
		
		//liberação de cors
		liberacaoCors(response);
		
		return null; //não autorizado
		
	}

	private void liberacaoCors(HttpServletResponse response) {
		if(response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		if(response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		if(response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		if(response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
}
