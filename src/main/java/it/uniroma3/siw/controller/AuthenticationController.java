package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.UtenteRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UtenteService;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.service.AutoreService;
import it.uniroma3.siw.service.RecensioneService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class AuthenticationController {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
	
	@Autowired  //gestisce le operazioni legate alle credenziali dell'utente
	private CredentialsService credentialsService;

    @Autowired //gestisce le operazioni legate all'entità user
	private UtenteService utenteService;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private LibroService libroService;

    @Autowired
    private AutoreService autoreService;

    @Autowired
    private RecensioneService recensioneService;

    @Autowired
    private it.uniroma3.siw.repository.LibroRepository libroRepository;
    @Autowired
    private it.uniroma3.siw.repository.AutoreRepository autoreRepository;
	
    
    //form registrazione utente
	@GetMapping(value = "/register") 
	public String showRegisterForm (Model model) {
		model.addAttribute("utente", new Utente());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterUser";
	}
	
	//form per il login (non serve passare i dati al modello) 
	@GetMapping(value = "/login") 
	public String showLoginForm (Model model) {
		return "formLogin";
	}

	//home page con autenticazione
	@GetMapping(value = "/") 
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) { //se non è autenticato mostra homepage pubblica
			// Add necessary data for the public index page
			model.addAttribute("libri", libroService.findAll());
			model.addAttribute("autori", autoreService.findAll());
			model.addAttribute("recensioniPerLibro", recensioneService.getRecensioniPerLibro());
	        return "index.html";
		}
		else {		//se invece è admin lo manda alla dashboard dell'admin con funzioni in più
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
				return "redirect:/admin/index";
			}
			return "redirect:/user/index"; // Redirect authenticated users to their dashboard
		}
	}
		
	//dopo il login viene reindirizzato qui e ottiene pagine diverse in base a se è admin o utente
    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
            return "redirect:/admin/index";
        }
        // Se è utente normale (DEFAULT), reindirizza a /user/index
        if (credentials.getRole().equals(Credentials.DEFAULT_ROLE)) {
            return "redirect:/user/index";
        }
        return "index.html";
    }

    //gestisce invio del form di registrazione
	@PostMapping(value = { "/register" })
    public String registerUser(@Valid @ModelAttribute("utente") Utente utente,
                 BindingResult userBindingResult, @Valid
                 @ModelAttribute("credentials") Credentials credentials,
                 BindingResult credentialsBindingResult,
                 Model model) {
        logger.info("Inizio processo di registrazione per email: {}", utente.getEmail());
        
        boolean hasError = false;
        
        // Log degli errori di validazione
        if (userBindingResult.hasErrors()) {
            logger.error("Errori di validazione utente: {}", userBindingResult.getAllErrors());
            hasError = true;
        }
        
        if (credentialsBindingResult.hasErrors()) {
            logger.error("Errori di validazione credenziali: {}", credentialsBindingResult.getAllErrors());
            hasError = true;
        }
        
        // Controllo password
        String password = credentials.getPassword();
        if (password == null || password.length() < 6 || !password.matches(".*[!@#$%^&*()_+|,.<>/?].*")) {
            logger.error("Password non valida per email: {}", utente.getEmail());
            model.addAttribute("passwordError", "La password deve essere lunga almeno 6 caratteri e contenere almeno un carattere speciale.");
            hasError = true;
        }
        
        // Email già esistente
        if (utenteRepository.findByEmail(utente.getEmail()) != null) {
            logger.error("Email già esistente: {}", utente.getEmail());
            model.addAttribute("emailError", "Email già esistente.");
            hasError = true;
        }
        
        if (!hasError) {
            try {
                logger.info("Tentativo di salvataggio utente: {}", utente.getEmail());
                utenteService.saveUser(utente);
                credentials.setUser(utente);
                credentialsService.saveCredentials(credentials);
                logger.info("Registrazione completata con successo per: {}", utente.getEmail());
                return "registrationSuccessful";
            } catch (DataIntegrityViolationException e) {
                logger.error("Errore di integrità dati durante la registrazione: {}", e.getMessage());
                model.addAttribute("emailError", "Email già esistente.");
            } catch (Exception e) {
                logger.error("Errore durante la registrazione: {}", e.getMessage(), e);
                model.addAttribute("error", "Si è verificato un errore durante la registrazione. Riprova più tardi.");
            }
        }
        model.addAttribute("utente", utente);
        model.addAttribute("credentials", credentials);
        return "formRegisterUser";
    }

    @GetMapping("/api/check-email")
    @ResponseBody
    public boolean checkEmailExists(@RequestParam("email") String email) {
        return utenteRepository.findByEmail(email) != null;
    }

    @GetMapping("/api/check-username")
    @ResponseBody
    public boolean checkUsernameExists(@RequestParam("username") String username) {
        return credentialsService.getCredentials(username) != null;
    }

    @GetMapping("/searchLibri")
    public String searchLibri(@RequestParam(name = "titolo", required = false) String titolo, Model model) {
        // Se la query è vuota o null, reindirizza alla home page
        if (titolo == null || titolo.trim().isEmpty()) {
            return "redirect:/";
        }
        
        List<it.uniroma3.siw.model.Libro> libri = libroService.findByTitoloStartingWithIgnoreCase(titolo.trim());
        model.addAttribute("libri", libri);
        model.addAttribute("autori", autoreService.findAll());
        model.addAttribute("recensioniPerLibro", recensioneService.getRecensioniPerLibro());
        model.addAttribute("searchTipo", "libro");
        model.addAttribute("searchQuery", titolo.trim());
        return "index.html";
    }

    @GetMapping("/searchAutori")
    public String searchAutori(@RequestParam(name = "nomeCognome", required = false) String nomeCognome, Model model) {
        // Se la query è vuota o null, reindirizza alla home page
        if (nomeCognome == null || nomeCognome.trim().isEmpty()) {
            return "redirect:/";
        }
        
        List<it.uniroma3.siw.model.Autore> autori = autoreService.findByNomeOrCognomeStartingWithIgnoreCase(nomeCognome.trim());
        model.addAttribute("libri", libroService.findAll());
        model.addAttribute("autori", autori);
        model.addAttribute("recensioniPerLibro", recensioneService.getRecensioniPerLibro());
        model.addAttribute("searchTipo", "autore");
        model.addAttribute("searchQuery", nomeCognome.trim());
        return "index.html";
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "tipo", required = false, defaultValue = "libro") String tipo,
                        @RequestParam(name = "query", required = false) String query,
                        Model model) {
        // Se la query è vuota o null, reindirizza alla home page
        if (query == null || query.trim().isEmpty()) {
            return "redirect:/";
        }
        
        String trimmedQuery = query.trim();
        
        if ("autore".equalsIgnoreCase(tipo)) {
            List<it.uniroma3.siw.model.Autore> autori = autoreService.findByNomeOrCognomeStartingWithIgnoreCase(trimmedQuery);
            model.addAttribute("autori", autori);
            model.addAttribute("libri", libroService.findAll());
        } else {
            List<it.uniroma3.siw.model.Libro> libri = libroService.findByTitoloStartingWithIgnoreCase(trimmedQuery);
            model.addAttribute("libri", libri);
            model.addAttribute("autori", autoreService.findAll());
        }
        model.addAttribute("recensioniPerLibro", recensioneService.getRecensioniPerLibro());
        model.addAttribute("searchTipo", tipo);
        model.addAttribute("searchQuery", trimmedQuery);
        return "index.html";
    }
}