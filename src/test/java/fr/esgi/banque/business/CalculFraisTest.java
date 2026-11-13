package fr.esgi.banque.business;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

}
