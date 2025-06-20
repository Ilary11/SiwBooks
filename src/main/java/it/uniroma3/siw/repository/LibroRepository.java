package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository; // O JpaRepository
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Libro;

public interface LibroRepository extends CrudRepository<Libro, Long> {

 
    public Libro findByTitoloIgnoreCase(String titolo);

    // Trova libri pubblicati in un certo anno
    public Iterable<Libro> findByAnno(Integer anno);

    
   // Metodo per verificare se un libro esiste già per titolo e anno
    public boolean existsByTitoloAndAnno(String titolo, Integer anno);

    // Ricerca libri per titolo (parziale, case-insensitive)
    public Iterable<Libro> findByTitoloContainingIgnoreCase(String titolo);
    
    // Ricerca libri per titolo che inizia con (starts with, case-insensitive)
    @Query("SELECT l FROM Libro l WHERE LOWER(l.titolo) LIKE LOWER(CONCAT(:titolo, '%'))")
    public Iterable<Libro> findByTitoloStartingWithIgnoreCase(@Param("titolo") String titolo);
}