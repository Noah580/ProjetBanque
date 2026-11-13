package fr.esgi.banque.service.impl;

import fr.esgi.banque.service.CompteRepository;
import fr.esgi.banque.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class VirementServiceImplTest {

	@Mock
	private CompteRepository repository;

	@Mock
	private NotificationService notifications;

	// Mercredi 11 novembre 2026, 10h00
	private final Clock horloge = Clock.fixed(Instant.parse("2026-11-11T10:00:00Z"), ZoneId.of("Europe/Paris"));

	private VirementServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new VirementServiceImpl(repository, notifications, horloge);
	}

	// ---------- V1 : montant null, nul ou négatif refusé ----------

	@ParameterizedTest
	@ValueSource(strings = {"0", "0.00", "-0.01", "-100"})
	void shouldRejectZeroOrNegativeAmount(String montant) {
		// Act & Assert
		assertThatThrownBy(() -> service.virer("FR001", "FR002", new BigDecimal(montant)))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldRejectNullAmount() {
		// Act & Assert
		assertThatThrownBy(() -> service.virer("FR001", "FR002", null))
				.isInstanceOf(IllegalArgumentException.class);
	}

}
