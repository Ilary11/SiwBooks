package it.uniroma3.siw.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller globale che inietta automaticamente i dettagli dell'utente autenticato
 * in ogni Model, rendendoli disponibili a tutte le viste.
 */
@ControllerAdvice
public class GlobalController {

    /**
     * Recupera i dettagli dell'utente autenticato e li aggiunge al Model
     * con il nome "userDetails". Se l'utente non è autenticato, restituisce null.
     * Questo metodo viene eseguito prima di ogni metodo handler del controller.
     *
     * @return i dettagli dell'utente autenticato (UserDetails) o null se l'utente non è autenticato.
     */
    @ModelAttribute("userDetails")
    public UserDetails getUser() {
        UserDetails user = null;

        // Recupera l'oggetto Authentication dal SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se l'utente è autenticato (cioè, non è un token anonimo)
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            // Se l'utente è autenticato, casta il principal a UserDetails
            user = (UserDetails) authentication.getPrincipal();
        }
        return user;
    }
}