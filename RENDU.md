# Rendu

- **Prénom** : Noah
- **Nom** : Jules
- **Campus** : Reims ESGI

## Partie A : défauts trouvés

| Règle | Description | Test qui le prouve |
|---|---|---|
| C3 | `crediter` acceptait un montant nul (`signum() < 0` au lieu de `<= 0`). | `CompteTest.shouldRejectZeroOrNegativeAmountWhenCrediting` |
| C5 | `debiter` refusait un débit atteignant exactement la limite du découvert (`<= 0` au lieu de `< 0`). | `CompteTest.shouldAcceptDebitUpToOverdraftLimit` |

## Couverture de code

<!-- Capture IntelliJ (Run with Coverage) -->

## Partie B : règles non terminées

<!-- Aucune / liste -->
