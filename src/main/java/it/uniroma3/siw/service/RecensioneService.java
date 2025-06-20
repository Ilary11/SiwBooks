package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.repository.RecensioneRepository;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class RecensioneService {
    
    @Autowired
    private RecensioneRepository recensioneRepository;
    
    public Map<Long, List<Recensione>> getRecensioniPerLibro() {
        Map<Long, List<Recensione>> recensioniPerLibro = new HashMap<>();
        List<Recensione> recensioni = (List<Recensione>) recensioneRepository.findAll();
        
        for (Recensione recensione : recensioni) {
            Libro libro = recensione.getLibro();
            recensioniPerLibro.computeIfAbsent(libro.getId(), k -> new java.util.ArrayList<>()).add(recensione);
        }
        
        return recensioniPerLibro;
    }
} 