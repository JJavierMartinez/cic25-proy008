package es.cic.curso25.proy08.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.proy08.model.Arbol;
import es.cic.curso25.proy08.model.Rama;
import es.cic.curso25.proy08.repository.ArbolRepository;
import es.cic.curso25.proy08.repository.RamaRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class RamaIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArbolRepository arbolRepository;

    @Autowired
    private RamaRepository ramaRepository;

    @Autowired 
    private MockMvc mockMvc;

    @Test
    void testRamaEliminada() {
        //creamos un arbol y sus ramas y las registramos recuperando el id de arbol
        Arbol arbol = new Arbol();
        arbol.setColor("rojizo");
        arbol.setEdad(20);
        arbol.setEspecie("pino");
        arbol.setPerenne(true);
        arbol.setPeso(200);
        Rama rama1 = new Rama();
        rama1.setArbol(arbol);
        rama1.setHojaCompuesta(true);
        rama1.setLongitud(10);
        rama1.setNumHojas(200);
        Rama rama2 = new Rama();
        rama2.setArbol(arbol);
        rama2.setHojaCompuesta(true);
        rama2.setLongitud(5);
        rama2.setNumHojas(100);
        arbol.getRamas().add(rama1);
        arbol.getRamas().add(rama2);
        Long idArbol = arbolRepository.save(arbol).getId();
        //utilizamos ese id para recuperar las ramas
        List<Rama> ramas = arbolRepository.findById(idArbol).get().getRamas();
        //borramos las ramas
        for (Rama rama : ramas) {
            ramaRepository.deleteById(rama.getId());
        }
        //recuperamos la lista del arbol que se generó para ver si está vacia
        assertTrue(arbolRepository.findById(idArbol).get().getRamas().size() == 0);
        
    }
}
