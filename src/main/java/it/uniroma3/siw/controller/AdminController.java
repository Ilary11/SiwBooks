package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.model.ImmagineLibro;
import it.uniroma3.siw.repository.AutoreRepository;
import it.uniroma3.siw.repository.LibroRepository;
import it.uniroma3.siw.repository.RecensioneRepository;
import it.uniroma3.siw.repository.ImmagineLibroRepository;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.service.AutoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PathVariable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.util.*;

@Controller
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private LibroRepository libroRepository;
    @Autowired
    private RecensioneRepository recensioneRepository;
    @Autowired
    private AutoreRepository autoreRepository;
    @Autowired
    private ImmagineLibroRepository immagineLibroRepository;
    @Autowired
    private LibroService libroService;
    @Autowired
    private AutoreService autoreService;

    private final String uploadDirectory = "uploads/images/";

    @GetMapping("/admin/index")
    public String indexAdmin(Model model, Authentication authentication) {
        try {
            logger.info("Loading admin dashboard for user: {}", authentication.getName());
            
            // Recupera il nome completo dell'utente
            String adminName = authentication.getName();
            model.addAttribute("adminName", adminName);
            logger.info("Admin name set to: {}", adminName);

            // Carica tutti i libri
            List<Libro> libri = new ArrayList<>();
            libroRepository.findAll().forEach(libri::add);
            model.addAttribute("libri", libri);
            logger.info("Loaded {} books", libri.size());
            
            // Carica le recensioni per ogni libro
            Map<Long, List<Recensione>> recensioniPerLibro = new HashMap<>();
            for (Libro libro : libri) {
                List<Recensione> recensioni = new ArrayList<>();
                recensioneRepository.findByLibro(libro).forEach(recensioni::add);
                recensioniPerLibro.put(libro.getId(), recensioni);
            }
            model.addAttribute("recensioniPerLibro", recensioniPerLibro);
            
            // Carica tutti gli autori
            List<Autore> autori = new ArrayList<>();
            autoreRepository.findAll().forEach(autori::add);
            model.addAttribute("autori", autori);
            logger.info("Loaded {} authors", autori.size());
            
            return "Admin/indexAdmin";
        } catch (Exception e) {
            logger.error("Error loading admin dashboard: ", e);
            model.addAttribute("error", "Si è verificato un errore durante il caricamento della dashboard: " + e.getMessage());
            return "Admin/indexAdmin";
        }
    }

    @GetMapping("/admin/aggiungiAutore")
    public String mostraFormAggiungiAutore() {
        return "Admin/aggiungiAutore";
    }

    @GetMapping("/admin/aggiungiLibro")
    public String mostraFormAggiungiLibro(Model model) {
        model.addAttribute("autori", autoreRepository.findAll());
        return "Admin/aggiungiLibro";
    }

    @PostMapping("/admin/aggiungiLibro")
    public String aggiungiLibro(@RequestParam String titolo,
                               @RequestParam Integer anno,
                               @RequestParam List<Long> autoreIds,
                               @RequestParam("immagini") MultipartFile[] immagini,
                               Model model) throws IOException {
        try {
            if (autoreIds == null || autoreIds.isEmpty()) {
                model.addAttribute("error", "Devi selezionare almeno un autore per il libro.");
                model.addAttribute("autori", autoreRepository.findAll());
                return "Admin/aggiungiLibro";
            }
            Libro libro = new Libro();
            libro.setTitolo(titolo);
            libro.setAnno(anno);
            // Associa tutti gli autori selezionati
            Set<Autore> autori = new HashSet<>();
            for (Long autoreId : autoreIds) {
                Autore autore = autoreRepository.findById(autoreId).orElse(null);
                if (autore != null) {
                    autori.add(autore);
                }
            }
            libro.setAutori(autori);
            libro = libroRepository.save(libro);
            
            boolean almenoUnaImmagineSalvata = false;
            if (immagini != null && immagini.length > 0) {
            for (MultipartFile file : immagini) {
                if (!file.isEmpty()) {
                        String originalFileName = file.getOriginalFilename();
                        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
                        String baseName = libro.getTitolo() != null ? libro.getTitolo().replaceAll("[^a-zA-Z0-9.-]", "_") : "libro";
                        String uniqueFileName = baseName + "_" + UUID.randomUUID() + "." + fileExtension;
                        Path filePath = Paths.get(uploadDirectory, uniqueFileName);
                        Files.createDirectories(filePath.getParent());
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                        logger.info("Immagine libro salvata in: {}", filePath.toAbsolutePath());
                    ImmagineLibro img = new ImmagineLibro();
                        img.setUrl("/images/" + uniqueFileName);
                    img.setLibro(libro);
                    immagineLibroRepository.save(img);
                    libro.getImmagini().add(img);
                        almenoUnaImmagineSalvata = true;
                    }
                }
            }
            if (!almenoUnaImmagineSalvata) {
                model.addAttribute("error", "Nessuna immagine è stata caricata o salvata. Riprova e controlla il formato e la dimensione del file.");
                model.addAttribute("autori", autoreRepository.findAll());
                return "Admin/aggiungiLibro";
            }
            libroRepository.save(libro);
            return "redirect:/admin/index";
        } catch (Exception e) {
            logger.error("Error saving book: ", e);
            model.addAttribute("error", "Si è verificato un errore durante il salvataggio del libro: " + e.getMessage());
            model.addAttribute("autori", autoreRepository.findAll());
            return "Admin/aggiungiLibro";
        }
    }

    @PostMapping("/admin/aggiungiAutore")
    public String aggiungiAutore(@RequestParam String nome,
                                 @RequestParam String cognome,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDiNascita,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDiMorte,
                                 @RequestParam String nazionalita,
                                 @RequestParam("foto") MultipartFile foto,
                                 Model model) {
        try {
            logger.info("Starting author addition process for: {} {}", nome, cognome);
            Autore autore = new Autore();
            autore.setNome(nome);
            autore.setCognome(cognome);
            autore.setDataDiNascita(dataDiNascita);
            autore.setDataDiMorte(dataDiMorte);
            autore.setNazionalita(nazionalita);
            if (foto != null && !foto.isEmpty()) {
                String originalFileName = foto.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
                String baseName = nome != null ? nome.replaceAll("[^a-zA-Z0-9.-]", "_") : "autore";
                String uniqueFileName = baseName + "_" + UUID.randomUUID() + "." + fileExtension;
                Path filePath = Paths.get(uploadDirectory, uniqueFileName);
                Files.createDirectories(filePath.getParent());
                Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Foto autore salvata in: {}", filePath.toAbsolutePath());
                autore.setUrlFotografia("/images/" + uniqueFileName);
            }
            autore = autoreRepository.save(autore);
            logger.info("Author saved successfully with ID: {}", autore.getId());
            return "redirect:/admin/index";
        } catch (Exception e) {
            logger.error("Error saving author: ", e);
            model.addAttribute("error", "Si è verificato un errore durante il salvataggio dell'autore: " + e.getMessage());
            return "Admin/aggiungiAutore";
        }
    }

    @GetMapping("/admin/modificaAutore/{id}")
    public String mostraFormModificaAutore(@PathVariable Long id, Model model) {
        Autore autore = autoreRepository.findById(id).orElse(null);
        if (autore == null) {
            return "redirect:/admin/index";
        }
        model.addAttribute("autore", autore);
        return "Admin/modificaAutore";
    }

    @PostMapping("/admin/modificaAutore/{id}")
    public String modificaAutore(@PathVariable Long id,
                               @RequestParam String nome,
                               @RequestParam String cognome,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDiNascita,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDiMorte,
                               @RequestParam String nazionalita,
                               @RequestParam(value = "foto", required = false) MultipartFile foto,
                               Model model) {
        try {
            Autore autore = autoreRepository.findById(id).orElse(null);
            if (autore == null) {
                return "redirect:/admin/index";
            }

            autore.setNome(nome);
            autore.setCognome(cognome);
            autore.setDataDiNascita(dataDiNascita);
            autore.setDataDiMorte(dataDiMorte);
            autore.setNazionalita(nazionalita);

            if (foto != null && !foto.isEmpty()) {
                String uploadDir = new ClassPathResource("static/images/").getFile().getAbsolutePath();
                String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
                File dest = new File(uploadDir, fileName);
                foto.transferTo(dest);
                autore.setUrlFotografia("/images/" + fileName);
            }

            autoreRepository.save(autore);
            return "redirect:/admin/index";
        } catch (Exception e) {
            model.addAttribute("error", "Si è verificato un errore durante la modifica dell'autore: " + e.getMessage());
            return "Admin/modificaAutore";
        }
    }

    @GetMapping("/admin/modificaLibro/{id}")
    public String mostraFormModificaLibro(@PathVariable Long id, Model model) {
        Libro libro = libroRepository.findById(id).orElse(null);
        if (libro == null) {
            return "redirect:/admin/index";
        }
        model.addAttribute("libro", libro);
        model.addAttribute("autori", autoreRepository.findAll());
        return "Admin/modificaLibro";
    }

    @PostMapping("/admin/modificaLibro/{id}")
    public String modificaLibro(@PathVariable Long id,
                              @RequestParam String titolo,
                              @RequestParam Integer anno,
                              @RequestParam List<Long> autoreIds,
                              @RequestParam(value = "immagini", required = false) MultipartFile[] immagini,
                              Model model) throws IOException {
        try {
            if (autoreIds == null || autoreIds.isEmpty()) {
                model.addAttribute("error", "Devi selezionare almeno un autore per il libro.");
                Libro libro = libroRepository.findById(id).orElse(null);
                model.addAttribute("libro", libro);
                model.addAttribute("autori", autoreRepository.findAll());
                return "Admin/modificaLibro";
            }
            Libro libro = libroRepository.findById(id).orElse(null);
            if (libro == null) {
                return "redirect:/admin/index";
            }
            libro.setTitolo(titolo);
            libro.setAnno(anno);
            // Aggiorna autori
            Set<Autore> autori = new HashSet<>();
            for (Long autoreId : autoreIds) {
                Autore autore = autoreRepository.findById(autoreId).orElse(null);
                if (autore != null) {
                    autori.add(autore);
                }
            }
            libro.setAutori(autori);

            // Aggiungi nuove immagini se presenti
            if (immagini != null && immagini.length > 0) {
                for (MultipartFile file : immagini) {
                    if (!file.isEmpty()) {
                        String originalFileName = file.getOriginalFilename();
                        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
                        String baseName = libro.getTitolo() != null ? libro.getTitolo().replaceAll("[^a-zA-Z0-9.-]", "_") : "libro";
                        String uniqueFileName = baseName + "_" + java.util.UUID.randomUUID() + "." + fileExtension;

                        Path filePath = Paths.get(uploadDirectory, uniqueFileName);
                        Files.createDirectories(filePath.getParent());
                        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                        ImmagineLibro img = new ImmagineLibro();
                        img.setUrl("/images/" + uniqueFileName);
                        img.setLibro(libro);
                        immagineLibroRepository.save(img);
                        libro.getImmagini().add(img);
                    }
                }
            }

            libroRepository.save(libro);
            return "redirect:/admin/index";
        } catch (Exception e) {
            model.addAttribute("error", "Si è verificato un errore durante la modifica del libro: " + e.getMessage());
            return "Admin/modificaLibro";
        }
    }

    @GetMapping("/admin/eliminaAutore/{id}")
    public String eliminaAutore(@PathVariable Long id, Model model) {
        try {
            Autore autore = autoreRepository.findById(id).orElse(null);
            if (autore != null) {
                // Verifica se l'autore ha libri associati
                if (autore.getLibriScritti() != null && !autore.getLibriScritti().isEmpty()) {
                    StringBuilder libriAssociati = new StringBuilder();
                    for (Libro libro : autore.getLibriScritti()) {
                        if (libriAssociati.length() > 0) {
                            libriAssociati.append(", ");
                        }
                        libriAssociati.append(libro.getTitolo());
                    }
                    
                    model.addAttribute("error", "Impossibile eliminare l'autore '" + autore.getNome() + " " + autore.getCognome() + 
                        "'. È autore dei seguenti libri: " + libriAssociati.toString() + 
                        ". Elimina prima i libri associati.");
                    
                    // Ricarica i dati per la dashboard
                    String adminName = "Admin"; // Potresti passarlo come parametro se necessario
                    model.addAttribute("adminName", adminName);
                    
                    List<Libro> libri = new ArrayList<>();
                    libroRepository.findAll().forEach(libri::add);
                    model.addAttribute("libri", libri);
                    
                    Map<Long, List<Recensione>> recensioniPerLibro = new HashMap<>();
                    for (Libro libro : libri) {
                        List<Recensione> recensioni = new ArrayList<>();
                        recensioneRepository.findByLibro(libro).forEach(recensioni::add);
                        recensioniPerLibro.put(libro.getId(), recensioni);
                    }
                    model.addAttribute("recensioniPerLibro", recensioniPerLibro);
                    
                    List<Autore> autori = new ArrayList<>();
                    autoreRepository.findAll().forEach(autori::add);
                    model.addAttribute("autori", autori);
                    
                    return "Admin/indexAdmin";
                }
                
                autoreRepository.delete(autore);
                return "redirect:/admin/index?success=autore_deleted";
            }
            return "redirect:/admin/index";
        } catch (Exception e) {
            // Gestisce anche altre eccezioni di database
            logger.error("Errore durante l'eliminazione dell'autore: ", e);
            model.addAttribute("error", "Si è verificato un errore durante l'eliminazione dell'autore. " + e.getMessage());
            
            // Ricarica i dati per la dashboard
            String adminName = "Admin";
            model.addAttribute("adminName", adminName);
            
            List<Libro> libri = new ArrayList<>();
            libroRepository.findAll().forEach(libri::add);
            model.addAttribute("libri", libri);
            
            Map<Long, List<Recensione>> recensioniPerLibro = new HashMap<>();
            for (Libro libro : libri) {
                List<Recensione> recensioni = new ArrayList<>();
                recensioneRepository.findByLibro(libro).forEach(recensioni::add);
                recensioniPerLibro.put(libro.getId(), recensioni);
            }
            model.addAttribute("recensioniPerLibro", recensioniPerLibro);
            
            List<Autore> autori = new ArrayList<>();
            autoreRepository.findAll().forEach(autori::add);
            model.addAttribute("autori", autori);
            
            return "Admin/indexAdmin";
        }
    }

    @GetMapping("/admin/eliminaLibro/{id}")
    public String eliminaLibro(@PathVariable Long id, Model model) {
        try {
            Libro libro = libroRepository.findById(id).orElse(null);
            if (libro != null) {
                // Prima elimina tutte le recensioni associate al libro
                List<Recensione> recensioni = new ArrayList<>();
                recensioneRepository.findByLibro(libro).forEach(recensioni::add);
                if (!recensioni.isEmpty()) {
                    recensioneRepository.deleteAll(recensioni);
                    logger.info("Eliminate {} recensioni associate al libro '{}'", recensioni.size(), libro.getTitolo());
                }
                
                // Poi elimina il libro
                libroRepository.delete(libro);
                logger.info("Libro '{}' eliminato con successo", libro.getTitolo());
                return "redirect:/admin/index?success=libro_deleted";
            }
            return "redirect:/admin/index";
        } catch (Exception e) {
            logger.error("Errore durante l'eliminazione del libro: ", e);
            model.addAttribute("error", "Si è verificato un errore durante l'eliminazione del libro: " + e.getMessage());
            
            // Ricarica i dati per la dashboard
            String adminName = "Admin";
            model.addAttribute("adminName", adminName);
            
            List<Libro> libri = new ArrayList<>();
            libroRepository.findAll().forEach(libri::add);
            model.addAttribute("libri", libri);
            
            Map<Long, List<Recensione>> recensioniPerLibro = new HashMap<>();
            for (Libro libroItem : libri) {
                List<Recensione> recensioni = new ArrayList<>();
                recensioneRepository.findByLibro(libroItem).forEach(recensioni::add);
                recensioniPerLibro.put(libroItem.getId(), recensioni);
            }
            model.addAttribute("recensioniPerLibro", recensioniPerLibro);
            
            List<Autore> autori = new ArrayList<>();
            autoreRepository.findAll().forEach(autori::add);
            model.addAttribute("autori", autori);
            
            return "Admin/indexAdmin";
        }
    }

    @PostMapping("/admin/deleteRecensione")
    public String deleteRecensione(@RequestParam("recensioneId") Long recensioneId, Model model) {
        try {
            recensioneRepository.deleteById(recensioneId);
            return "redirect:/admin/index?success=recensione_deleted";
        } catch (Exception e) {
            model.addAttribute("error", "Si è verificato un errore durante l'eliminazione della recensione: " + e.getMessage());
            
            // Ricarica i dati per la dashboard
            String adminName = "Admin";
            model.addAttribute("adminName", adminName);
            
            List<Libro> libri = new ArrayList<>();
            libroRepository.findAll().forEach(libri::add);
            model.addAttribute("libri", libri);
            
            Map<Long, List<Recensione>> recensioniPerLibro = new HashMap<>();
            for (Libro libro : libri) {
                List<Recensione> recensioni = new ArrayList<>();
                recensioneRepository.findByLibro(libro).forEach(recensioni::add);
                recensioniPerLibro.put(libro.getId(), recensioni);
            }
            model.addAttribute("recensioniPerLibro", recensioniPerLibro);
            
            List<Autore> autori = new ArrayList<>();
            autoreRepository.findAll().forEach(autori::add);
            model.addAttribute("autori", autori);
            
            return "Admin/indexAdmin";
        }
    }

    @GetMapping("/admin/search")
    public String searchAdmin(@RequestParam(name = "tipo", required = false, defaultValue = "libro") String tipo,
                             @RequestParam(name = "query", required = false) String query,
                             Model model, Authentication authentication) {
        // Se la query è vuota o null, reindirizza alla home page admin
        if (query == null || query.trim().isEmpty()) {
            return "redirect:/admin/index";
        }
        
        String adminName = authentication.getName();
        model.addAttribute("adminName", adminName);
        
        String trimmedQuery = query.trim();
        
        if ("autore".equalsIgnoreCase(tipo)) {
            List<Autore> autori = autoreService.findByNomeOrCognomeStartingWithIgnoreCase(trimmedQuery);
            model.addAttribute("autori", autori);
            List<Libro> libri = new ArrayList<>();
            libroService.findAll().forEach(libri::add);
            model.addAttribute("libri", libri);
        } else {
            List<Libro> libri = libroService.findByTitoloStartingWithIgnoreCase(trimmedQuery);
            model.addAttribute("libri", libri);
            List<Autore> autori = new ArrayList<>();
            autoreService.findAll().forEach(autori::add);
            model.addAttribute("autori", autori);
        }
        Map<Long, List<Recensione>> recensioniPerLibro = new HashMap<>();
        List<Libro> libri = (List<Libro>) model.getAttribute("libri");
        if (libri != null) {
            for (Libro libro : libri) {
                List<Recensione> recensioni = new ArrayList<>();
                recensioneRepository.findByLibro(libro).forEach(recensioni::add);
                recensioniPerLibro.put(libro.getId(), recensioni);
            }
        }
        model.addAttribute("recensioniPerLibro", recensioniPerLibro);
        model.addAttribute("searchTipo", tipo);
        model.addAttribute("searchQuery", trimmedQuery);
        return "Admin/indexAdmin";
    }
} 