package it.uniroma3.siw.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import it.uniroma3.siw.model.Utente;

public interface UtenteRepository extends CrudRepository<Utente, Long> {
	
	Utente findByEmail(String email);
	
	@NonNull
	Optional<Utente> findById(@NonNull Long id);
}