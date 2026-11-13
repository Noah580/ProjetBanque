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

		BigDecimal solde = new BigDecimal("100");
		BigDecimal decouvert = new BigDecimal("200");


		assertThatThrownBy(() -> new Compte(numero, "Alice", solde, decouvert))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {" ", "   "})
	void shouldRejectMissingTitulaire(String titulaire) {

		BigDecimal solde = new BigDecimal("100");
		BigDecimal decouvert = new BigDecimal("200");


		assertThatThrownBy(() -> new Compte("FR001", titulaire, solde, decouvert))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldCreateAccountWithValidNumeroAndTitulaire() {

		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), new BigDecimal("200"));


		assertThat(compte.getNumero()).isEqualTo("FR001");
		assertThat(compte.getTitulaire()).isEqualTo("Alice");
	}

	// ---------- C2 : solde initial et découvert obligatoires, découvert >= 0 ----------

	@Test
	void shouldRejectNullSoldeInitial() {

		BigDecimal decouvert = new BigDecimal("200");


		assertThatThrownBy(() -> new Compte("FR001", "Alice", null, decouvert))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldRejectNullDecouvertAutorise() {

		BigDecimal solde = new BigDecimal("100");


		assertThatThrownBy(() -> new Compte("FR001", "Alice", solde, null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@ValueSource(strings = {"-0.01", "-1", "-200"})
	void shouldRejectNegativeDecouvertAutorise(String decouvert) {

		BigDecimal solde = new BigDecimal("100");


		assertThatThrownBy(() -> new Compte("FR001", "Alice", solde, new BigDecimal(decouvert)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@ParameterizedTest
	@CsvSource({"0", "0.00", "0.01", "200"})
	void shouldAcceptPositiveOrZeroDecouvertAutorise(String decouvert) {

		Compte compte = new Compte("FR001", "Alice", new BigDecimal("100"), new BigDecimal(decouvert));


		assertThat(compte.getDecouvertAutorise()).isEqualByComparingTo(decouvert);
	}

}
