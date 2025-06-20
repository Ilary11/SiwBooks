package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UtenteRepository;

@Service //componente di servizio che spring gestisce autonomamente in questo caso per la gestione credenziali
public class CredentialsService {

    @Autowired // L'annotazione inietta automaticamente un'istanza di PasswordEncoder nella variabile passwordEncoder
    protected PasswordEncoder passwordEncoder; //Il PasswordEncoder è utilizzato per criptare (o codificare) la password dell'utente.

    @Autowired
    protected CredentialsRepository credentialsRepository; //si occupera delle interazioni con il db

    @Autowired
    protected UtenteRepository utenteRepository;

    @Transactional //tutte le operazioni nel metodo vengono eseguite come parte di una transazione. Se qualcosa va storto, tutte le operazioni vengono annullate (rollback).
    public Credentials getCredentials(Long id) { //cerca credenziali utente tramite l'id
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional //fa la stessa cosa usando come criterio di ricerca l'username
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    @Transactional //fa la stessa cosa usando come criterio di ricerca l'email
    public Credentials getCredentialsByEmail(String email) {
        Optional<Credentials> result = this.credentialsRepository.findByUtenteEmail(email);
        return result.orElse(null);
    }

    @Transactional //per salvare le credenziali nel db
    public Credentials saveCredentials(Credentials credentials) {
        credentials.setRole(Credentials.DEFAULT_ROLE); //setta il ruolo dell'utente che di default e user
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword())); //cripta la password
        return this.credentialsRepository.save(credentials); //salva oggetto credential
    }
}
