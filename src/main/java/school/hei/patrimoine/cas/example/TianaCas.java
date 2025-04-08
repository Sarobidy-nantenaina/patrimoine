package school.hei.patrimoine.cas.example;

import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static java.time.Month.MARCH;
import static java.time.Month.MAY;
import static school.hei.patrimoine.modele.Argent.ariary;
import static school.hei.patrimoine.modele.Devise.MGA;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.Personne;
import school.hei.patrimoine.modele.possession.Compte;
import school.hei.patrimoine.modele.possession.Dette;
import school.hei.patrimoine.modele.possession.FluxArgent;
import school.hei.patrimoine.modele.possession.Materiel;
import school.hei.patrimoine.modele.possession.Possession;

public class TianaCas implements Supplier<Patrimoine> {

  // Dates clés
  public static final LocalDate AU_8_AVRIL_2025 = LocalDate.of(2025, APRIL, 8);
  public static final LocalDate AU_1_MAI_2025 = LocalDate.of(2025, MAY, 1);
  public static final LocalDate AU_1_JUIN_2025 = LocalDate.of(2025, JUNE, 1);
  public static final LocalDate AU_27_JUILLET_2025 = LocalDate.of(2025, JULY, 27);
  public static final LocalDate AU_27_AOUT_2025 = LocalDate.of(2025, AUGUST, 27);
  public static final LocalDate AU_31_DECEMBRE_2025 = LocalDate.of(2025, DECEMBER, 31);
  public static final LocalDate AU_31_JANVIER_2026 = LocalDate.of(2026, JANUARY, 31);
  public static final LocalDate AU_31_MARS_2026 = LocalDate.of(2026, MARCH, 31);
  public static final LocalDate AU_27_JUILLET_2026 = LocalDate.of(2026, JULY, 27);

  private Compte compteBancaire() {
    return new Compte("Compte bancaire", AU_8_AVRIL_2025, ariary(60_000_000));
  }

  private Materiel terrain() {
    return new Materiel(
        "Terrain bâti",
        AU_8_AVRIL_2025,
        AU_8_AVRIL_2025,
        ariary(100_000_000),
        0.10); // Appréciation de 10 % par an
  }

  private Dette pretBancaire() {
    return new Dette("Prêt bancaire", AU_8_AVRIL_2025, ariary(0)); // Initialisé à 0, ajusté par flux
  }

  private Set<Possession> possessionsTiana(Compte compte, Dette pret) {
    Set<Possession> possessions = new HashSet<>();

    // Dépenses mensuelles famille
    possessions.add(new FluxArgent(
        "Dépenses famille",
        compte,
        AU_8_AVRIL_2025,
        LocalDate.MAX,
        1,
        ariary(-4_000_000)));

    // Projet : Dépenses mensuelles
    possessions.add(new FluxArgent(
        "Dépenses projet",
        compte,
        AU_1_JUIN_2025,
        AU_31_DECEMBRE_2025,
        5,
        ariary(-5_000_000)));

    // Projet : Revenu 10 % (1er mai 2025)
    possessions.add(new FluxArgent(
        "Revenu projet 10%",
        compte,
        AU_1_MAI_2025,
        AU_1_MAI_2025,
        1,
        ariary(7_000_000)));

    // Projet : Revenu 90 % (31 janvier 2026)
    possessions.add(new FluxArgent(
        "Revenu projet 90%",
        compte,
        AU_31_JANVIER_2026,
        AU_31_JANVIER_2026,
        31,
        ariary(63_000_000)));

    // Prêt : Entrée
    possessions.add(new FluxArgent(
        "Entrée prêt bancaire",
        compte,
        AU_27_JUILLET_2025,
        AU_27_JUILLET_2025,
        27,
        ariary(20_000_000)));

    // Prêt : Création dette
    possessions.add(new FluxArgent(
        "Création dette",
        pret,
        AU_27_JUILLET_2025,
        AU_27_JUILLET_2025,
        27,
        ariary(-20_000_000)));

    // Prêt : Remboursement mensuel
    possessions.add(new FluxArgent(
        "Remboursement prêt",
        compte,
        AU_27_AOUT_2025,
        AU_27_JUILLET_2026,
        27,
        ariary(-2_000_000)));

    // Prêt : Réduction dette
    possessions.add(new FluxArgent(
        "Réduction dette",
        pret,
        AU_27_AOUT_2025,
        AU_27_JUILLET_2026,
        27,
        ariary(2_000_000)));

    return possessions;
  }

  @Override
  public Patrimoine get() {
    var tiana = new Personne("Tiana");
    var compte = compteBancaire();
    var terrain = terrain();
    var pret = pretBancaire();

    Set<Possession> possessions = new HashSet<>();
    possessions.add(compte);
    possessions.add(terrain);
    possessions.add(pret);
    possessions.addAll(possessionsTiana(compte, pret));

    return Patrimoine.of(
        "Patrimoine de Tiana au 8 avril 2025",
        MGA,
        AU_8_AVRIL_2025,
        tiana,
        possessions);
  }

  public Patrimoine patrimoineAu31Mars2026() {
    return get().projectionFuture(AU_31_MARS_2026);
  }
}