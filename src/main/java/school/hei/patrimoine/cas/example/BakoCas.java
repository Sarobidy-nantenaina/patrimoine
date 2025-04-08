package school.hei.patrimoine.cas.example;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static school.hei.patrimoine.modele.Argent.ariary;
import static school.hei.patrimoine.modele.Devise.MGA;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import school.hei.patrimoine.modele.Patrimoine;
import school.hei.patrimoine.modele.Personne;
import school.hei.patrimoine.modele.possession.Compte;
import school.hei.patrimoine.modele.possession.FluxArgent;
import school.hei.patrimoine.modele.possession.Materiel;
import school.hei.patrimoine.modele.possession.Possession;
import school.hei.patrimoine.modele.possession.TransfertArgent;

public class BakoCas implements Supplier<Patrimoine> {

  public static final LocalDate AU_8_AVRIL_2025 = LocalDate.of(2025, APRIL, 8);
  public static final LocalDate AU_31_DECEMBRE_2025 = LocalDate.of(2025, DECEMBER, 31);

  private Compte compteBNI() {
    return new Compte("Compte BNI", AU_8_AVRIL_2025, ariary(2_000_000));
  }

  private Compte compteBMOI() {
    return new Compte("Compte BMOI", AU_8_AVRIL_2025, ariary(625_000));
  }

  private Compte coffreFort() {
    return new Compte("Coffre-fort", AU_8_AVRIL_2025, ariary(1_750_000));
  }

  private Materiel ordinateur() {
    return new Materiel(
        "Ordinateur portable", 
        AU_8_AVRIL_2025, 
        AU_8_AVRIL_2025, 
        ariary(3_000_000), 
        -0.12);
  }

  private Set<Possession> possessionsBako(Compte compteBNI, Compte compteBMOI) {
    Set<Possession> possessions = new HashSet<>();

    // Salaire mensuel
    possessions.add(new FluxArgent(
        "Salaire net",
        compteBNI,
        AU_8_AVRIL_2025,
        LocalDate.MAX,
        2,
        ariary(2_125_000)));

    // Virement vers l'épargne
    possessions.add(new TransfertArgent(
        "Épargne mensuelle",
        compteBNI,
        compteBMOI,
        AU_8_AVRIL_2025,
        LocalDate.MAX,
        3,
        ariary(200_000)));

    // Dépenses de vie
    possessions.add(new FluxArgent(
        "Dépenses de vie",
        compteBNI,
        AU_8_AVRIL_2025,
        LocalDate.MAX,
        1,
        ariary(-700_000)));

    // Loyer
    possessions.add(new FluxArgent(
        "Loyer colocation",
        compteBNI,
        AU_8_AVRIL_2025,
        LocalDate.MAX,
        26,
        ariary(-600_000)));

    return possessions;
  }

  @Override
  public Patrimoine get() {
    var bako = new Personne("Bako");
    var compteBNI = compteBNI();
    var compteBMOI = compteBMOI();
    var coffreFort = coffreFort();
    var ordinateur = ordinateur();

    Set<Possession> possessions = new HashSet<>();
    possessions.add(compteBNI);
    possessions.add(compteBMOI);
    possessions.add(coffreFort);
    possessions.add(ordinateur);
    possessions.addAll(possessionsBako(compteBNI, compteBMOI));

    return Patrimoine.of(
        "Patrimoine de Bako au 8 avril 2025",
        MGA,
        AU_8_AVRIL_2025,
        bako,
        possessions);
  }

  public Patrimoine patrimoineAu31Decembre2025() {
    return get().projectionFuture(AU_31_DECEMBRE_2025);
  }
}