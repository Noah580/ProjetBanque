package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test d'exemple fourni pour vérifier que le projet est bien configuré.
 * À compléter (partie A).
 */
class CompteTest {

	@Test
	void shouldIncreaseBalanceWhenCrediting() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act
		compte.crediter(new BigDecimal("50.00"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("150.00");
	}

	// ---------- C1 : numéro et titulaire obligatoires ----------

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "   "})
	void shouldRejectMissingNumero(String numero) {
		// Arrange
		BigDecimal solde = new BigDecimal("100");
		BigDecimal decouvert = new BigDecimal("200");

		// Act & Assert
		assertThatThrownBy(() -> new Compte(numero, "Alice", solde, decouvert))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "   "})
	void shouldRejectMissingTitulaire(String titulaire) {
		// Arrange
		BigDecimal solde = new BigDecimal("100");
		BigDecimal decouvert = new BigDecimal("200");

		// Act & Assert
		assertThatThrownBy(() -> new Compte("FR001", titulaire, solde, decouvert))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldCreateAccountWithValidNumeroAndTitulaire() {
		// Act
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), new BigDecimal("200"));

		// Assert
		assertThat(compte.getNumero()).isEqualTo("FR001");
		assertThat(compte.getTitulaire()).isEqualTo("Alice");
	}

	// ---------- C2 : solde initial et découvert obligatoires, découvert >= 0 ----------

	@Test
	void shouldRejectNullSoldeInitial() {
		// Arrange
		BigDecimal decouvert = new BigDecimal("200");

		// Act & Assert
		assertThatThrownBy(() -> new Compte("FR001", "Alice", null, decouvert))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldRejectNullDecouvertAutorise() {
		// Arrange
		BigDecimal solde = new BigDecimal("100");

		// Act & Assert
		assertThatThrownBy(() -> new Compte("FR001", "Alice", solde, null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ValueSource(strings = {"-0.01", "-1", "-200"})
	void shouldRejectNegativeDecouvertAutorise(String decouvert) {
		// Arrange
		BigDecimal solde = new BigDecimal("100");

		// Act & Assert
		assertThatThrownBy(() -> new Compte("FR001", "Alice", solde, new BigDecimal(decouvert)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@CsvSource({"0", "0.00", "0.01", "200"})
	void shouldAcceptPositiveOrZeroDecouvertAutorise(String decouvert) {
		// Act
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), new BigDecimal(decouvert));

		// Assert
		assertThat(compte.getDecouvertAutorise()).isEqualByComparingTo(decouvert);
	}

	// ---------- C3 : crediter n'accepte qu'un montant strictement positif ----------

	@ParameterizedTest
	@ValueSource(strings = {"0", "0.00", "-0.01", "-50"})
	void shouldRejectZeroOrNegativeAmountWhenCrediting(String montant) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act & Assert
		assertThatThrownBy(() -> compte.crediter(new BigDecimal(montant)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldRejectNullAmountWhenCrediting() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act & Assert
		assertThatThrownBy(() -> compte.crediter(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	// ---------- C4 : debiter n'accepte qu'un montant strictement positif ----------

	@ParameterizedTest
	@ValueSource(strings = {"0", "0.00", "-0.01", "-50"})
	void shouldRejectZeroOrNegativeAmountWhenDebiting(String montant) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act & Assert
		assertThatThrownBy(() -> compte.debiter(new BigDecimal(montant)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldRejectNullAmountWhenDebiting() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act & Assert
		assertThatThrownBy(() -> compte.debiter(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldDecreaseBalanceWhenDebiting() {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100.00"), new BigDecimal("200"));

		// Act
		compte.debiter(new BigDecimal("30.00"));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo("70.00");
	}

	// ---------- C5 : débit accepté jusqu'à -découvertAutorisé inclus ----------

	@ParameterizedTest
	@CsvSource({
			"100.00, 200, 300.00, -200.00",
			"100.00, 200, 299.99, -199.99",
			"100.00, 0, 100.00, 0.00"
	})
	void shouldAcceptDebitUpToOverdraftLimit(String soldeInitial, String decouvert, String montant, String soldeAttendu) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal(soldeInitial), new BigDecimal(decouvert));

		// Act
		compte.debiter(new BigDecimal(montant));

		// Assert
		assertThat(compte.getSolde()).isEqualByComparingTo(soldeAttendu);
	}

	@ParameterizedTest
	@CsvSource({
			"100.00, 200, 300.01",
			"100.00, 0, 100.01"
	})
	void shouldRejectDebitBeyondOverdraftLimitAndKeepBalance(String soldeInitial, String decouvert, String montant) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal(soldeInitial), new BigDecimal(decouvert));

		// Act & Assert
		assertThatThrownBy(() -> compte.debiter(new BigDecimal(montant)))
				.isInstanceOf(SoldeInsuffisantException.class);
		assertThat(compte.getSolde()).isEqualByComparingTo(soldeInitial);
	}

	// ---------- C6 : estVide() vrai quand le solde vaut zéro, quelle que soit l'écriture ----------

	@ParameterizedTest
	@ValueSource(strings = {"0", "0.0", "0.00", "0.000"})
	void shouldBeEmptyWhenBalanceIsZero(String solde) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal(solde), new BigDecimal("200"));

		// Act
		boolean vide = compte.estVide();

		// Assert
		assertThat(vide).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {"0.01", "-0.01", "100"})
	void shouldNotBeEmptyWhenBalanceIsNotZero(String solde) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal(solde), new BigDecimal("200"));

		// Act
		boolean vide = compte.estVide();

		// Assert
		assertThat(vide).isFalse();
	}

	// ---------- C7 : estADecouvert() vrai quand le solde est strictement négatif ----------

	@ParameterizedTest
	@CsvSource({
			"-0.01, true",
			"-200, true",
			"0, false",
			"0.00, false",
			"0.01, false",
			"100, false"
	})
	void shouldBeOverdrawnOnlyWhenBalanceIsStrictlyNegative(String solde, boolean attendu) {
		// Arrange
		Compte compte = new Compte("FR001", "Alice", new BigDecimal(solde), new BigDecimal("200"));

		// Act
		boolean aDecouvert = compte.estADecouvert();

		// Assert
		assertThat(aDecouvert).isEqualTo(attendu);
	}

}
