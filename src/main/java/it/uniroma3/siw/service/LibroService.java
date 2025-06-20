package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.repository.LibroRepository;
import java.util.List;

@Service
public class LibroService {
    
    @Autowired
    private LibroRepository libroRepository;
    
    public List<Libro> findAll() {
        return (List<Libro>) libroRepository.findAll();
    }

    public List<Libro> findByTitoloContainingIgnoreCase(String Titolo) {
        return (List<Libro>) libroRepository.findByTitoloContainingIgnoreCase(Titolo);
    }
    
    public List<Libro> findByTitoloStartingWithIgnoreCase(String titolo) {
        return (List<Libro>) libroRepository.findByTitoloStartingWithIgnoreCase(titolo);
    }
} 