package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculFraisTest {

	private final CalculFrais calculFrais = new CalculFrais();

	// ---------- F1 : titulaire majeur (18 ans ou plus) et solde moyen obligatoire ----------

	@ParameterizedTest
	@ValueSource(ints = {0, 10, 17})
	void shouldRejectMinorTitulaire(int age) {
		// Arrange
		BigDecimal soldeMoyen = new BigDecimal("500");

		// Act & Assert
		assertThatThrownBy(() -> calculFrais.fraisMensuels(age, soldeMoyen, false))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldAcceptTitulaireOfExactly18() {
		// Arrange
		BigDecimal soldeMoyen = new BigDecimal("500");

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(18, soldeMoyen, false);

		// Assert
		assertThat(frais).isNotNull();
	}

	@Test
	void shouldRejectNullSoldeMoyen() {
		// Act & Assert
		assertThatThrownBy(() -> calculFrais.fraisMensuels(30, null, false))
				.isInstanceOf(IllegalArgumentException.class);
	}

	// ---------- F2 : frais de tenue de compte de 2,00 € par mois ----------

	@Test
	void shouldCharge2EurosForAccountMaintenance() {
		// Arrange
		int age = 40;
		BigDecimal soldeMoyen = new BigDecimal("500");

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, soldeMoyen, false);

		// Assert
		assertThat(frais).isEqualByComparingTo("2.00");
	}

	// ---------- F3 : tenue offerte aux titulaires de moins de 26 ans ----------

	@ParameterizedTest
	@CsvSource({
			"18, 0.00",
			"25, 0.00",
			"26, 2.00",
			"27, 2.00"
	})
	void shouldOfferMaintenanceFeesUnder26(int age, String fraisAttendus) {
		// Arrange
		BigDecimal soldeMoyen = new BigDecimal("500");

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, soldeMoyen, false);

		// Assert
		assertThat(frais).isEqualByComparingTo(fraisAttendus);
	}

	// ---------- F4 : tenue offerte si le solde moyen atteint 1 000 € ou plus ----------

	@ParameterizedTest
	@CsvSource({
			"500, 2.00",
			"999.99, 2.00",
			"1000, 0.00",
			"1000.00, 0.00",
			"1000.01, 0.00",
			"5000, 0.00"
	})
	void shouldOfferMaintenanceFeesFrom1000EurosAverageBalance(String soldeMoyen, String fraisAttendus) {
		// Arrange
		int age = 40;

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, new BigDecimal(soldeMoyen), false);

		// Assert
		assertThat(frais).isEqualByComparingTo(fraisAttendus);
	}

	// ---------- F5 : carte premium +5,00 € toujours dus, même quand la tenue est offerte ----------

	@ParameterizedTest
	@CsvSource({
			"40, 500, 7.00",
			"25, 500, 5.00",
			"40, 1000, 5.00",
			"25, 1000, 5.00"
	})
	void shouldAlwaysCharge5EurosForPremiumCard(int age, String soldeMoyen, String fraisAttendus) {
		// Arrange
		boolean cartePremium = true;

		// Act
		BigDecimal frais = calculFrais.fraisMensuels(age, new BigDecimal(soldeMoyen), cartePremium);

		// Assert
		assertThat(frais).isEqualByComparingTo(fraisAttendus);
	}

}
