package br.com.inter.testejava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import br.com.inter.testejava.dto.Usuario;

@Service
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    List<Usuario> findByPubKey(String pubKey);
}
