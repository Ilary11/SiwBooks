package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.repository.AutoreRepository;
import java.util.List;

@Service
public class AutoreService {
    
    @Autowired
    private AutoreRepository autoreRepository;
    
    public List<Autore> findAll() {
        return (List<Autore>) autoreRepository.findAll();
    }
    
    public List<Autore> findByNomeOrCognomeStartingWithIgnoreCase(String query) {
        return (List<Autore>) autoreRepository.findByNomeOrCognomeStartingWithIgnoreCase(query);
    }
} 