package es.cic.curso25.proy08.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.cic.curso25.proy08.model.Arbol;

public interface ArbolRepository extends JpaRepository<Arbol, Long>{
    
}
