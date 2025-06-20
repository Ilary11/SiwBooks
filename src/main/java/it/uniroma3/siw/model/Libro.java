package it.uniroma3.siw.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType; 
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String titolo;

    @NotNull
    @Min(1900)
    @Max(2025)
    private Integer anno;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ImmagineLibro> immagini; // Un libro può avere molte immagini

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "libro_autori",
        joinColumns = @JoinColumn(name = "libro_id"),
        inverseJoinColumns = @JoinColumn(name = "autore_id")
    )
    private Set<Autore> autori;

   
    public Libro() {
        this.autori = new HashSet<>();
        this.immagini = new HashSet<>(); // Inizializza la collezione
    }

    // --- Metodi Getter e Setter ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    // Getter e Setter per le immagini
    public Set<ImmagineLibro> getImmagini() {
        return immagini;
    }

    public void setImmagini(Set<ImmagineLibro> immagini) {
        this.immagini = immagini;
    }

    public void addImmagine(ImmagineLibro immagine) {
        this.immagini.add(immagine);
        immagine.setLibro(this);
    }

    public void removeImmagine(ImmagineLibro immagine) {
        this.immagini.remove(immagine);
        immagine.setLibro(null);
    }

    public Set<Autore> getAutori() {
        return autori;
    }

    public void setAutori(Set<Autore> autori) {
        this.autori = autori;
    }

    // --- equals e hashCode ---
    @Override
    public int hashCode() {
        return Objects.hash(titolo, anno);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Libro other = (Libro) obj;
        return Objects.equals(titolo, other.titolo) && anno.equals(other.anno);
    }
}