package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository; // O JpaRepository
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Autore;

public interface AutoreRepository extends CrudRepository<Autore, Long> {

    // Trova un autore per nome e cognome
    public Autore findByNomeAndCognome(String nome, String cognome);


    // Metodo per verificare se un autore esiste già per nome e cognome
    public boolean existsByNomeAndCognome(String nome, String cognome);

    // Ricerca autori per nome o cognome (parziale, case-insensitive)
    public Iterable<Autore> findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCase(String nome, String cognome);
    
    // Ricerca autori per nome o cognome che inizia con (starts with, case-insensitive)
    @Query("SELECT a FROM Autore a WHERE LOWER(a.nome) LIKE LOWER(CONCAT(:query, '%')) OR LOWER(a.cognome) LIKE LOWER(CONCAT(:query, '%'))")
    public Iterable<Autore> findByNomeOrCognomeStartingWithIgnoreCase(@Param("query") String query);
}