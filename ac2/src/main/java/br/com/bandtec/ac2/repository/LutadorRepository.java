package br.com.bandtec.ac2.repository;

import br.com.bandtec.ac2.entity.Lutador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LutadorRepository extends JpaRepository<Lutador, Integer> {

    List<Lutador> findAllByOrderByForcaGolpeDesc();

    Integer countAllByVivoIsTrue();
}
