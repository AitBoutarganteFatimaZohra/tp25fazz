# Microservices Docker + Consul

Projet du TP 25 : conteneurisation de trois microservices Spring Boot et découverte de services avec Consul. L'infrastructure est lancée avec Docker Compose.

> Les captures sont produites à partir des API et de l'interface Consul réellement lancées. Après le démarrage, exécutez le script de capture décrit ci-dessous.

## Architecture

```text
Navigateur
  ├── http://localhost:8888/api/clients   ──> Gateway ──> service-client ──> MySQL
  ├── http://localhost:8888/api/voitures  ──> Gateway ──> service-voiture ──> MySQL
  ├── http://localhost:8500               ──> Consul UI
  └── http://localhost:8081               ──> phpMyAdmin ──> MySQL

Tous les services Spring Boot s'enregistrent auprès de Consul sur le réseau Docker.
```

| Composant | Port hôte | Rôle |
|---|---:|---|
| Gateway | 8888 | Routage vers les API Client et Voiture |
| Client | 8088 | API REST des clients, base `Micro_ClientDB` |
| Voiture | 8089 | API REST des voitures, base `Micro_VoitureDB` |
| Consul | 8500 | Registre de services et interface web |
| MySQL | 3306 | Stockage des données |
| phpMyAdmin | 8081 | Administration de MySQL |

## Prérequis

- Docker Desktop récent, avec Docker Compose v2 (`docker compose`)
- Ports `3306`, `8081`, `8088`, `8089`, `8500` et `8888` disponibles
- Java 17 et Maven 3.8+ uniquement pour compiler et exécuter les services en dehors de Docker

## Démarrage avec Docker Compose

Depuis la racine du dépôt :

```powershell
Copy-Item .env.example .env
docker compose up -d --build
docker compose ps
```

Le premier démarrage télécharge les images et dépendances Maven ; il peut prendre quelques minutes. Les services applicatifs attendent que MySQL et Consul soient prêts avant de démarrer.

Pour consulter les logs :

```powershell
docker compose logs -f gateway-service
docker compose logs -f client-service
docker compose logs -f voiture-service
```

## Vérifications

Quand les conteneurs sont démarrés :

```powershell
Invoke-RestMethod http://localhost:8888/api/clients
Invoke-RestMethod http://localhost:8888/api/voitures
Invoke-RestMethod http://localhost:8500/v1/health/service/gateway?passing=true
Invoke-RestMethod http://localhost:8500/v1/health/service/service-client?passing=true
Invoke-RestMethod http://localhost:8500/v1/health/service/service-voiture?passing=true
```

Les appels aux API retournent des exemples de clients et de voitures initialisés à la première connexion à la base. Les interfaces sont accessibles à :

- API Client via Gateway : <http://localhost:8888/api/clients>
- API Voiture via Gateway : <http://localhost:8888/api/voitures>
- Consul : <http://localhost:8500>
- phpMyAdmin : <http://localhost:8081> (serveur `mysql`, utilisateur `root`, mot de passe configuré dans `.env`)

Les API sont aussi directement accessibles sur `http://localhost:8088/api/clients` et `http://localhost:8089/api/voitures`.

## Captures des résultats

Une fois l'architecture lancée et les API disponibles, générez des captures locales réelles avec Microsoft Edge :

```powershell
.\scripts\capturer-resultats.ps1
```

Le script vérifie les deux API et la présence des trois services sains dans Consul avant d'écrire les captures dans `docs/captures/`. S'il manque un service ou une donnée, il s'arrête avec une erreur plutôt que de produire de fausses captures.

| API Client via Gateway | API Voiture via Gateway | Services Consul |
|---|---|---|
| ![Réponse réelle de l'API Client](docs/captures/api-clients.png) | ![Réponse réelle de l'API Voiture](docs/captures/api-voitures.png) | ![Interface Consul](docs/captures/consul-services.png) |


## Arrêt et nettoyage

```powershell
docker compose down
```

Pour supprimer également le volume de données MySQL (suppression irréversible des données locales) :

```powershell
docker compose down -v
```

## Structure

```text
.
├── clientService/       # API Spring Boot Client + Dockerfile multi-stage
├── voitureService/      # API Spring Boot Voiture + Dockerfile multi-stage
├── gatewayService/      # Gateway Spring Cloud + Dockerfile multi-stage
├── docs/captures/       # Captures d'exécution à ajouter
├── .env.example
└── docker-compose.yml
```

## Sécurité

Les identifiants par défaut (`root` / `root`) sont réservés au TP en local. Ne publiez pas de mot de passe réel dans Git et ne déployez pas cette configuration telle quelle sur Internet. Le fichier `.env` est ignoré par Git ; adaptez `.env.example` à un secret local avant le démarrage si nécessaire.
