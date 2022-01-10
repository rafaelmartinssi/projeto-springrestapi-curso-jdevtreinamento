package project.restapi.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import project.restapi.model.Usuario;
import project.restapi.model.UsuarioDTO;
import project.restapi.repository.UsuarioRepository;

@CrossOrigin
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@GetMapping(value = "/{id}", produces = "application/json")
	public ResponseEntity<UsuarioDTO> init(@PathVariable(value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<List<Usuario>> findAll() {
		List<Usuario> list = new ArrayList<>();
		list = (List<Usuario>) usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception{
		
		for(int i = 0; i < usuario.getTelefones().size(); i++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}
		
		//consumindo api externa - via cep
			URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
			URLConnection connection = url.openConnection();
			InputStream iStream = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(iStream, "UTF-8"));
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			
			while((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			
			Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
			
			usuario.setCep(userAux.getCep());
			usuario.setLogradouro(userAux.getLogradouro());
			usuario.setComplemento(userAux.getComplemento());
			usuario.setBairro(userAux.getBairro());
			usuario.setLocalidade(userAux.getLocalidade());
			usuario.setUf(userAux.getUf());
			
		//fim
		
		String senhaBcry = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaBcry);
		
		Usuario user = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(user, HttpStatus.OK);
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario){
		
		for(int i = 0; i < usuario.getTelefones().size(); i++) {
			usuario.getTelefones().get(i).setUsuario(usuario);
		}
		
		Usuario userBcry = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		if(!BCrypt.checkpw(usuario.getSenha(), userBcry.getSenha())) {
			String senhaBcry = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaBcry);
		}else {
			usuario.setSenha(userBcry.getSenha());
		}
		
		Usuario user = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(user, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable(value = "id") Long id){
		usuarioRepository.deleteById(id);
		return "ok";
	}

}
