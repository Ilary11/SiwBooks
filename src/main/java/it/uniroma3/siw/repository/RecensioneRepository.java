package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Utente;

/**
 * Repository per l'entità Recensione, che gestisce le interazioni con il database.
 * Estende CrudRepository (o JpaRepository) per fornire le operazioni CRUD di base.
 */
public interface RecensioneRepository extends CrudRepository<Recensione, Long> {

    /**
     * Trova tutte le recensioni associate a un determinato Libro.
     * @param libro l'oggetto Libro di cui si vogliono recuperare le recensioni
     * @return un Iterable di Recensione associate al Libro
     */
    public Iterable<Recensione> findByLibro(Libro libro);

    /**
     * Trova tutte le recensioni scritte da un determinato Utente.
     * @param utente l'oggetto Utente di cui si vogliono recuperare le recensioni
     * @return un Iterable di Recensione scritte dall'Utente
     */
    public Iterable<Recensione> findByUtente(Utente utente);

    /**
     * Trova tutte le recensioni con un voto specifico.
     * @param voto il voto intero da cercare (es. 1, 2, 3, 4, 5)
     * @return un Iterable di Recensione con il voto specificato
     */
    public Iterable<Recensione> findByVoto(Integer voto);

    /**
     * Trova tutte le recensioni per un dato Libro con un voto maggiore o uguale a quello specificato.
     * @param libro l'oggetto Libro per cui filtrare le recensioni
     * @param voto il voto minimo incluso
     * @return un Iterable di Recensione che soddisfano i criteri
     */
    public Iterable<Recensione> findByLibroAndVotoGreaterThanEqual(Libro libro, Integer voto);
    
    /**
     * Verifica se esiste una recensione specifica scritta da un utente per un dato libro.
     * Questo è utile per implementare il requisito "ogni utente può aggiungere al più una recensione per libro".
     * @param utente l'utente che ha scritto la recensione
     * @param libro il libro a cui è associata la recensione
     * @return true se esiste una recensione per quel libro scritta da quell'utente, false altrimenti
     */
    public boolean existsByUtenteAndLibro(Utente utente, Libro libro);
}