package nl.fuchsia.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "zaak")
public class Zaak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int zaaknr;

    @Column
    private LocalDate overtredingsdatum;

    @Column
    // mag leeg zijn indien het adminstratieve boete is bijv boete niet verzekerd.
    private String pleeglocatie;

    @ManyToOne
    @JoinColumn(name = "persoonnr")
    private Persoon persoon;

    @ManyToMany
    @JoinTable(name = "zaakregel",
            joinColumns =
            @JoinColumn(name = "zaaknr", referencedColumnName = "zaaknr"),
            inverseJoinColumns =
            @JoinColumn(name = "feitnr", referencedColumnName = "feitnr"))
    private List<Feit> feiten;

    @ManyToMany
    @JoinTable(name = "zaakstatus",
            joinColumns =
            @JoinColumn(name = "zaaknr", referencedColumnName = "zaaknr"),
            inverseJoinColumns =
            @JoinColumn(name = "statusnr", referencedColumnName = "statusnr"))
    private List<Status> status;

    public Zaak() {
    }

    public Zaak(LocalDate overtredingsdatum, String pleeglocatie) {
        this.overtredingsdatum = overtredingsdatum;
        this.pleeglocatie = pleeglocatie;
    }

    public Zaak(int zaaknr, LocalDate overtredingsdatum, String pleeglocatie) {
        this(overtredingsdatum, pleeglocatie);
        this.zaaknr = zaaknr;
    }

    public Zaak(LocalDate overtredingsdatum, String pleeglocatie, Persoon persoon, List<Feit> feiten, List<Status> status) {
        this.overtredingsdatum = overtredingsdatum;
        this.pleeglocatie = pleeglocatie;
        this.persoon = persoon;
        this.feiten = feiten;
        this.status = status;
    }

    public List<Status> getStatus() {
        return status;
    }

    public void setStatus(List<Status> status) {
        this.status = status;
    }

    public int getZaaknr() {
        return zaaknr;
    }

    public void setZaaknr(int zaakNr) {
        this.zaaknr = zaakNr;
    }

    public LocalDate getOvertredingsdatum() {
        return overtredingsdatum;
    }

    public void setOvertredingsdatum(LocalDate overtredingsDatum) {
        this.overtredingsdatum = overtredingsDatum;
    }

    public String getPleeglocatie() {
        return pleeglocatie;
    }

    public void setPleeglocatie(String pleegLocatie) {
        this.pleeglocatie = pleegLocatie;
    }

    public Persoon getPersoon() {
        return persoon;
    }

    public void setPersoon(Persoon persoon) {
        this.persoon = persoon;
    }

    public List<Feit> getFeiten() {
        return feiten;
    }

    public void setFeiten(List<Feit> feiten) {
        this.feiten = feiten;
    }

    @Override
    public String toString() {
        return "Zaak{" +
                "zaaknr=" + zaaknr +
                ", overtredingsdatum=" + overtredingsdatum +
                ", pleeglocatie='" + pleeglocatie + '\'' +
                ", persoon=" + persoon +
                ", feiten=" + feiten +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zaak zaak = (Zaak) o;
        return zaaknr == zaak.zaaknr &&
                Objects.equals(overtredingsdatum, zaak.overtredingsdatum) &&
                Objects.equals(pleeglocatie, zaak.pleeglocatie) &&
                Objects.equals(persoon, zaak.persoon) &&
                Objects.equals(feiten, zaak.feiten) &&
                Objects.equals(status, zaak.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zaaknr, overtredingsdatum, pleeglocatie, persoon, feiten, status);
    }
}
