package br.com.inter.testejava.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.inter.testejava.dto.Resultados;
import br.com.inter.testejava.dto.Usuario;
import br.com.inter.testejava.repository.UsuarioRepository;
import br.com.inter.testejava.security.Crypto;
import br.com.inter.testejava.service.DigitoUnicoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Api(value = "Teste Inter")
@Slf4j
public class TesteController {

  @Autowired
  UsuarioRepository usuarioRepository;

  @Autowired
  DigitoUnicoService dus;

  @ApiOperation(value = "Retorna todos os usuários")
  @GetMapping("/usuarios")
  public ResponseEntity<List<Usuario>> getAllUsuarios() {
    try {
      List<Usuario> usuarios = new ArrayList<Usuario>();

      usuarioRepository.findAll().forEach(usuarios::add);

      if (usuarios.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(usuarios, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ApiOperation(value = "Retorna o usuário com o id especificado")
  @GetMapping("/usuarios/{id}")
  public ResponseEntity<Usuario> getUsuarioById(
      @RequestHeader(value = "private-key", required = false) String privateKey, @PathVariable("id") Integer id) {
    Optional<Usuario> usuarioData = usuarioRepository.findById(id);
    if (privateKey != null && usuarioData.isPresent()) {
      Usuario _usuario = new Usuario();
      if (usuarioData.get().getPubKey() == "" || usuarioData.get().getPubKey() == null) {
        return new ResponseEntity<>(usuarioData.get(), HttpStatus.OK);
      } else {
        try {
          _usuario.setId(usuarioData.get().getId());
          _usuario.setNome(Crypto.decrypt(usuarioData.get().getCryptNome(), privateKey).toString());
          _usuario.setEmail(Crypto.decrypt(usuarioData.get().getCryptEmail(), privateKey).toString());
          _usuario.setDigitos(usuarioData.get().getDigitos());
          _usuario.setPubKey(usuarioData.get().getPubKey());
          return new ResponseEntity<>(_usuario, HttpStatus.OK);
        } catch (InvalidKeyException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (IllegalBlockSizeException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (BadPaddingException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (NoSuchAlgorithmException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (NoSuchPaddingException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
      }
    }
    if (usuarioData.isPresent()) {
      return new ResponseEntity<>(usuarioData.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @ApiOperation(value = "Retorna todos os digitos calculados para um usuário com o id especificado")
  @GetMapping("/usuarios/digitos/{id}")
  public ResponseEntity<List<String>> getUsuarioDigitosById(@PathVariable("id") Integer id) {
    Optional<Usuario> usuarioData = usuarioRepository.findById(id);
    if (usuarioData.isPresent()) {
      return new ResponseEntity<>(usuarioData.get().getDigitos(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @ApiOperation(value = "Cria um novo usuário")
  @PostMapping("/usuarios")
  public ResponseEntity<String> createUsuario(@RequestBody Usuario usuario) {
    try {
      usuarioRepository.save(new Usuario(usuario.getNome(), usuario.getEmail()));
      return new ResponseEntity<>("Usuário criado com sucesso!", HttpStatus.CREATED);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ApiOperation(value = "Deleta todos os usuários")
  @DeleteMapping("/usuarios")
  public ResponseEntity<String> deleteAllUsuarios() {
    try {
      usuarioRepository.deleteAll();
      return new ResponseEntity<>("Todos os usuários deletados com sucesso!", HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ApiOperation(value = "Deleta o usuário com o id especificado")
  @DeleteMapping("/usuarios/{id}")
  public ResponseEntity<String> deleteUsuario(@PathVariable("id") Integer id) {
    try {
      usuarioRepository.deleteById(id);
      return new ResponseEntity<>("Usuário deletado com sucesso!", HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @ApiOperation(value = "Atualiza as informações do usuário com o id especificado")
  @PutMapping("/usuarios/{id}")
  public ResponseEntity<String> updateUsuario(@PathVariable("id") Integer id, @RequestBody Usuario usuario) {
    Optional<Usuario> usuarioData = usuarioRepository.findById(id);

    if (usuarioData.isPresent()) {
      Usuario _usuario = usuarioData.get();
      _usuario.setNome(usuario.getNome());
      _usuario.setEmail(usuario.getEmail());
      usuarioRepository.save(_usuario);
      return new ResponseEntity<>("Usuário atualizado com sucesso!", HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @ApiOperation(value = "Calcula o dígito. Sem o id, apenas calcula um dígito e o exibe. Com o id, associa o dígito calculado a um usuário")
  @PutMapping(value = { "/calculodigito/{id}" })
  public ResponseEntity<Integer> calculoDigito(@RequestBody Resultados resultados,
      @PathVariable(required = false) Integer id) {
    if (id == null) {
      try {
        return new ResponseEntity<>(dus.uniqueDigit(resultados.getN(), resultados.getK()), HttpStatus.OK);
      } catch (Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      try {
        Optional<Usuario> usuarioData = usuarioRepository.findById(id);
        if (usuarioData.isPresent()) {
          Integer digito = dus.uniqueDigit(resultados.getN(), resultados.getK());
          Usuario _usuario = usuarioData.get();
          _usuario.getDigitos().add(
              "n = " + resultados.getN() + ", k = " + resultados.getK().toString() + ", digito = " + digito.toString());
          usuarioRepository.save(_usuario);
          return new ResponseEntity<>(digito, HttpStatus.OK);
        } else {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
      } catch (Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  @ApiOperation(value = "Cadastra uma chave pública no usuário com o id especificado")
  @PutMapping("/chavepublica/{id}")
  public ResponseEntity<String> addPubKey(@RequestBody String pubKey, @PathVariable("id") Integer id) {
    Optional<Usuario> usuarioData = usuarioRepository.findById(id);
    if (usuarioData.isPresent()) {
      Usuario _usuario = usuarioData.get();
      if (usuarioRepository.findByPubKey(pubKey).size() > 0) {
        return new ResponseEntity<>("Esta chave já esta em uso!", HttpStatus.CONFLICT);
      } else {
        _usuario.setPubKey(pubKey);
        try {
          byte[] cryptNome = Crypto.encrypt(_usuario.getNome(), _usuario.getPubKey());
          byte[] cryptEmail = Crypto.encrypt(_usuario.getEmail(), _usuario.getPubKey());
          _usuario.setNome("**********");
          _usuario.setEmail("**********");
          _usuario.setCryptNome(cryptNome);
          _usuario.setCryptEmail(cryptEmail);
          usuarioRepository.save(_usuario);
        } catch (InvalidKeyException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (BadPaddingException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (IllegalBlockSizeException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (NoSuchPaddingException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (NoSuchAlgorithmException e) {
          log.error(e.getMessage());
          return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>("Dados criptografados com sucesso!", HttpStatus.OK);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}