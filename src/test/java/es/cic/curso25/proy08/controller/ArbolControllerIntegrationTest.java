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
public class ArbolControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArbolRepository arbolRepository;

    @Autowired
    private RamaRepository ramaRepository;

    @Autowired 
    private MockMvc mockMvc;

    @Test
    void testCreateArbol() throws Exception{
        //creamos un arbol y varias ramas
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
        
        //serializamos el objeto
        String arbolJson = objectMapper.writeValueAsString(arbol);
        
        //simulamos la llamada http
        MvcResult result = mockMvc.perform(post("/arbol")
        .contentType("application/json")
        .content(arbolJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

        Arbol arbolGenerado = objectMapper.readValue(result.getResponse().getContentAsString(), Arbol.class);


        assertTrue(arbolGenerado.getRamas().size() >= 2);
    }

    @Test
    void testDeleteArbol() throws Exception{
        //creamos el arbol y las ramas y lo recuperamos para saber el id
        Arbol arbol = new Arbol();
        arbol.setColor("gris");
        arbol.setEdad(20);
        arbol.setEspecie("abeto");
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

        Long idGenerado = arbolRepository.save(arbol).getId();

        //borramos el arbol con el id que se ha generado
        mockMvc.perform(delete("/arbol/" + idGenerado))
        .andDo(print())
        .andExpect(status().isOk());

        //comprobamos tambien que no existe
        assertTrue(arbolRepository.findById(idGenerado).isEmpty());
        //comprobamos si se han borrado las ramas
        assertTrue(ramaRepository.findAll().size() == 0);
        
    }

    @Test
    void testGetAll() throws Exception{
        //creamos los arboles y las ramas

        //árbol 1
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

        //árbol 2
        Arbol arbol2 = new Arbol();
        arbol2.setColor("gris");
        arbol2.setEdad(20);
        arbol2.setEspecie("abeto");
        arbol2.setPerenne(true);
        arbol2.setPeso(200);

        Rama rama1a2 = new Rama();
        rama1a2.setArbol(arbol);
        rama1a2.setHojaCompuesta(true);
        rama1a2.setLongitud(10);
        rama1a2.setNumHojas(200);

        Rama rama2a2 = new Rama();
        rama2a2.setArbol(arbol);
        rama2a2.setHojaCompuesta(true);
        rama2a2.setLongitud(5);
        rama2a2.setNumHojas(100);

        arbol.getRamas().add(rama1a2);
        arbol.getRamas().add(rama2a2);

        //creamos los registros en la base de datos
        arbolRepository.save(arbol);
        arbolRepository.save(arbol2);

        //simulamos la llamada http para la busqueda de todos los arboles y comprobamos que devuelve una lista con 2 o mas árboles
        mockMvc.perform(get("/arbol"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(result -> {
            String response = result.getResponse().getContentAsString();
            List<Arbol> personas = objectMapper.readValue(response,new TypeReference<List<Arbol>>() {}); 

            assertTrue(personas.size() >= 2);
        });
    }

    @Test
    void testGetArbol() throws Exception{
        //creamos el arbol y las ramas y lo recuperamos para saber el id
        Arbol arbol = new Arbol();
        arbol.setColor("gris");
        arbol.setEdad(20);
        arbol.setEspecie("abeto");
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

        Long idGenerado = arbolRepository.save(arbol).getId();

        mockMvc.perform(get("/arbol/" + idGenerado))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(idGenerado))
        .andExpect(jsonPath("$.color").value(arbol.getColor()));
    }

    @Test
    void testUpdateArbol() throws Exception{
        //creamos un árbol y lo recogemos
        Arbol arbol = new Arbol();
        arbol.setColor("gris");
        arbol.setEdad(20);
        arbol.setEspecie("abeto");
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

        Arbol arbolGenerado = arbolRepository.save(arbol);

        //comprobamos el color
        assertTrue(arbolGenerado.getColor().equals("gris"));
        //le cambiamos el color
        arbolGenerado.setColor("amarronado");
        //serializamos el árbol
        String arbolJson = objectMapper.writeValueAsString(arbolGenerado);

        mockMvc.perform(put("/arbol")
        .contentType("application/json")
        .content(arbolJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.color").value("amarronado"));



    }
}
