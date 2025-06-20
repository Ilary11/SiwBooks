package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.UtenteRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The UserService handles logic for Users.
 */
@Service
public class UtenteService {

    @Autowired
    protected UtenteRepository userRepository; //per recupero e salvataggio utenti db 

    /**
     * This method retrieves a User from the DB based on its ID.
     * @param id the id of the User to retrieve from the DB
     * @return the retrieved User, or null if no User with the passed ID could be found in the DB
     */
    @Transactional //le operazioni all'interno del metodo verranno eseguite in un'unica transazione. Se qualcosa va storto, la transazione verrà annullata.
    public Utente getUser(Long id) {
        Optional<Utente> result = this.userRepository.findById(id); //cerca utenti tramite l'id
        return result.orElse(null); //se esiste restituisce utente altrimenti null 
    }

    /**
     * This method saves a User in the DB.
     * @param user the User to save into the DB
     * @return the saved User
     * @throws DataIntegrityViolationException if a User with the same username
     *                              as the passed User already exists in the DB
     */
    @Transactional
    public Utente saveUser(Utente utente) { //salvare utente nel db 
        return this.userRepository.save(utente);
    }

    /**
     * This method retrieves all Users from the DB.
     * @return a List with all the retrieved Users
     */
    @Transactional
    public List<Utente> getAllUsers() {
        List<Utente> result = new ArrayList<>();
        Iterable<Utente> iterable = this.userRepository.findAll(); //recupera tutti gli utenti dal db 
        for(Utente utente : iterable) //itera su tutti gli user
            result.add(utente); //e li aggiunge alla lista dei result
        return result;
    }
}
