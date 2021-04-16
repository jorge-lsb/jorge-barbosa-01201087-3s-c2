package br.com.bandtec.ac2.controller;

import br.com.bandtec.ac2.entity.Luta;
import br.com.bandtec.ac2.entity.Lutador;
import br.com.bandtec.ac2.repository.LutadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lutadores")
public class LutadorController {

    @Autowired
    LutadorRepository repository;

    @PostMapping
    public ResponseEntity postLutador(@Valid @RequestBody Lutador lutador) {

        repository.save(lutador);

        return ResponseEntity.status(201).build();
    }

    @GetMapping
    public ResponseEntity getLutadoresByForca() {

        List<Lutador> lutadores = repository.findAllByOrderByForcaGolpeDesc();

        if (lutadores.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok().body(lutadores);
    }


    @GetMapping("/contagem-vivos")
    public ResponseEntity getVivos() {
        Integer lutadoresVivos = repository.countAllByVivoIsTrue();

        return ResponseEntity.ok().body(lutadoresVivos);
    }

    @PostMapping("/{id}/concentrar")
    public ResponseEntity postConcentrar(@PathVariable Integer id) {

        Optional<Lutador> lutador = repository.findById(id);

        if (lutador.isPresent()) {
            Integer concentracoes = lutador.get().getConcentracoesRealizadas();

            if (concentracoes < 3) {
                lutador.get().setConcentracoesRealizadas(concentracoes + 1);
                lutador.get().setVida(lutador.get().getVida() * 1.15);
                lutador.get().setId(id);
                repository.save(lutador.get());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(400).body("Lutador já se concentrou 3 vezes!");
            }

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/golpe")
    public ResponseEntity postLuta(@RequestBody Luta luta) {

        Optional<Lutador> lutadorApanha = repository.findById(luta.getIdLutadorApanha());
        Optional<Lutador> lutadorBate = repository.findById(luta.getIdLutadorBate());

        if (lutadorApanha.isPresent() && lutadorBate.isPresent()){
            Double vida = lutadorApanha.get().getVida() - lutadorBate.get().getForcaGolpe();
            lutadorApanha.get().setVida(vida < 0 ? 0 : vida);

            if (vida <= 0) {
                lutadorApanha.get().setVivo(false);
            }

            lutadorApanha.get().setId(luta.getIdLutadorApanha());
            repository.save(lutadorApanha.get());

            List<Lutador> lutadorsEnvolvidos = new ArrayList<>();

            lutadorsEnvolvidos.add(lutadorApanha.get());
            lutadorsEnvolvidos.add(lutadorBate.get());

            return ResponseEntity.status(201).body(lutadorsEnvolvidos);
        }else {
            return ResponseEntity.notFound().build();
        }



    }

    @GetMapping("/mortos ")
    public ResponseEntity getMortos() {
        Integer lutadoresmortos = repository.countAllByVivoIsFalse();

        return ResponseEntity.ok().body(lutadoresmortos);
    }


}
