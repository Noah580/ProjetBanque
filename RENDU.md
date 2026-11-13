# Rendu

- **Prénom** : Noah
- **Nom** : Jules
- **Campus** : Reims ESGI

## Partie A : défauts trouvés

| Règle | Description | Test qui le prouve |
|---|---|---|
| C3 | `crediter` acceptait un montant nul (`signum() < 0` au lieu de `<= 0`). | `CompteTest.shouldRejectZeroOrNegativeAmountWhenCrediting` |
| C5 | `debiter` refusait un débit atteignant exactement la limite du découvert (`<= 0` au lieu de `< 0`). | `CompteTest.shouldAcceptDebitUpToOverdraftLimit` |
| C6 | `estVide()` utilisait `equals(BigDecimal.ZERO)`, qui compare aussi l'échelle : un solde de `0.00` n'était pas considéré comme vide. | `CompteTest.shouldBeEmptyWhenBalanceIsZero` |
| F3 | La tenue de compte était offerte aux titulaires de 26 ans (`<= 26` au lieu de `< 26`). | `CalculFraisTest.shouldOfferMaintenanceFeesUnder26` |
| F4 | La tenue n'était pas offerte pour un solde moyen d'exactement 1 000 € (`> 0` au lieu de `>= 0`). | `CalculFraisTest.shouldOfferMaintenanceFeesFrom1000EurosAverageBalance` |
| F5 | Quand la tenue était offerte (F3 ou F4), la méthode renvoyait 0 immédiatement et le supplément premium de 5 € n'était pas facturé. | `CalculFraisTest.shouldAlwaysCharge5EurosForPremiumCard` |

## Couverture de code

<!-- Capture IntelliJ (Run with Coverage) -->

## Partie B : règles non terminées

<!-- Aucune / liste -->
