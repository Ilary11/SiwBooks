package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.LibroRepository;
import it.uniroma3.siw.repository.AutoreRepository;
import it.uniroma3.siw.repository.RecensioneRepository;
import it.uniroma3.siw.repository.UtenteRepository;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.service.AutoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.model.Credentials;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private AutoreRepository autoreRepository;
    @Autowired
    private RecensioneRepository recensioneRepository;
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private CredentialsRepository credentialsRepository;
    @Autowired
    private LibroService libroService;
    @Autowired
    private AutoreService autoreService;

    @GetMapping("/user/index")
    public String indexUser(Model model, Authentication authentication) {
        // Recupera nome utente
        String userName = authentication.getName();
        Utente utente = utenteRepository.findByEmail(userName);
        model.addAttribute("userName", utente != null ? utente.getNome() : userName);

        // Carica libri
        List<Libro> libri = new ArrayList<>();
        libroService.findAll().forEach(libri::add);
        model.addAttribute("libri", libri);

        // Carica autori
        List<Autore> autori = new ArrayList<>();
        autoreService.findAll().forEach(autori::add);
        model.addAttribute("autori", autori);

        // Carica recensioni per ogni libro
        Map<Long, List<Recensione>> recensioniPerLibro = new HashMap<>();
        Map<Long, Boolean> recensionePresentePerLibro = new HashMap<>();
        for (Libro libro : libri) {
            List<Recensione> recensioni = new ArrayList<>();
            recensioneRepository.findByLibro(libro).forEach(recensioni::add);
            recensioniPerLibro.put(libro.getId(), recensioni);
            // Verifica se l'utente ha già recensito questo libro
            boolean presente = false;
            if (utente != null) {
                presente = recensioneRepository.existsByUtenteAndLibro(utente, libro);
            }
            recensionePresentePerLibro.put(libro.getId(), presente);
        }
        model.addAttribute("recensioniPerLibro", recensioniPerLibro);
        model.addAttribute("recensionePresentePerLibro", recensionePresentePerLibro);
        return "user/indexUser";
    }

    @PostMapping("/user/aggiungiRecensione")
    public String aggiungiRecensione(
            @RequestParam("libroId") Long libroId,
            @RequestParam("titolo") String titolo,
            @RequestParam("voto") Integer voto,
            @RequestParam("testo") String testo,
            Authentication authentication) {

        System.out.println("[LOG] Richiesta POST aggiungiRecensione: libroId=" + libroId + ", titolo=" + titolo + ", voto=" + voto + ", testo=" + testo);

        String username = authentication.getName();
        System.out.println("[LOG] Username utente autenticato: " + username);
        Credentials credentials = credentialsRepository.findByUsername(username).orElse(null);
        Utente utente = (credentials != null) ? credentials.getUser() : null;
        System.out.println("[LOG] Utente trovato: " + (utente != null ? utente.getEmail() : "null"));
        Optional<Libro> libroOpt = libroRepository.findById(libroId);
        System.out.println("[LOG] Libro trovato: " + (libroOpt.isPresent() ? libroOpt.get().getTitolo() : "null"));

        if (utente == null || libroOpt.isEmpty()) {
            System.out.println("[LOG] ERRORE: utente o libro non trovato");
            return "redirect:/user/index?error=not_found";
        }

        Libro libro = libroOpt.get();
        boolean recensioneEsistente = recensioneRepository.existsByUtenteAndLibro(utente, libro);
        System.out.println("[LOG] Recensione già esistente per questo utente e libro: " + recensioneEsistente);
        if (recensioneEsistente) {
            System.out.println("[LOG] ERRORE: recensione già esistente");
            return "redirect:/user/index?error=review_exists&message=Puoi inserire al massimo una recensione per libro, mi dispiace.";
        }

        if (voto < 1 || voto > 5) {
            System.out.println("[LOG] ERRORE: voto non valido: " + voto);
            return "redirect:/user/index?error=invalid_vote";
        }

        Recensione recensione = new Recensione();
        recensione.setTitolo(titolo);
        recensione.setVoto(voto);
        recensione.setTesto(testo);
        recensione.setUtente(utente);
        recensione.setLibro(libro);

        try {
            recensioneRepository.save(recensione);
            System.out.println("[LOG] Recensione salvata con successo!");
            return "redirect:/user/index?success=true";
        } catch (Exception e) {
            System.out.println("[LOG] ERRORE durante il salvataggio della recensione: " + e.getMessage());
            return "redirect:/user/index?error=save_error";
        }
    }

    @GetMapping("/user/search")
    public String searchUser(@RequestParam(name = "tipo", required = false, defaultValue = "libro") String tipo,
                             @RequestParam(name = "query", required = false) String query,
                             Model model, Authentication authentication) {
        // Se la query è vuota o null, reindirizza alla home page utente
        if (query == null || query.trim().isEmpty()) {
            return "redirect:/user/index";
        }
        
        String userName = authentication.getName();
        it.uniroma3.siw.model.Utente utente = utenteRepository.findByEmail(userName);
        model.addAttribute("userName", utente != null ? utente.getNome() : userName);
        
        String trimmedQuery = query.trim();
        
        if ("autore".equalsIgnoreCase(tipo)) {
            List<it.uniroma3.siw.model.Autore> autori = autoreService.findByNomeOrCognomeStartingWithIgnoreCase(trimmedQuery);
            model.addAttribute("autori", autori);
            List<it.uniroma3.siw.model.Libro> libri = new ArrayList<>();
            libroService.findAll().forEach(libri::add);
            model.addAttribute("libri", libri);
        } else {
            List<it.uniroma3.siw.model.Libro> libri = libroService.findByTitoloStartingWithIgnoreCase(trimmedQuery);
            model.addAttribute("libri", libri);
            List<it.uniroma3.siw.model.Autore> autori = new ArrayList<>();
            autoreService.findAll().forEach(autori::add);
            model.addAttribute("autori", autori);
        }
        Map<Long, List<it.uniroma3.siw.model.Recensione>> recensioniPerLibro = new HashMap<>();
        Map<Long, Boolean> recensionePresentePerLibro = new HashMap<>();
        List<it.uniroma3.siw.model.Libro> libri = (List<it.uniroma3.siw.model.Libro>) model.getAttribute("libri");
        if (libri != null) {
            for (it.uniroma3.siw.model.Libro libro : libri) {
                List<it.uniroma3.siw.model.Recensione> recensioni = new ArrayList<>();
                recensioneRepository.findByLibro(libro).forEach(recensioni::add);
                recensioniPerLibro.put(libro.getId(), recensioni);
                boolean presente = false;
                if (utente != null) {
                    presente = recensioneRepository.existsByUtenteAndLibro(utente, libro);
                }
                recensionePresentePerLibro.put(libro.getId(), presente);
            }
        }
        model.addAttribute("recensioniPerLibro", recensioniPerLibro);
        model.addAttribute("recensionePresentePerLibro", recensionePresentePerLibro);
        model.addAttribute("searchTipo", tipo);
        model.addAttribute("searchQuery", query);
        return "user/indexUser";
    }
} 