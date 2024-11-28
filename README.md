PayMyBuddy

PayMyBuddy est une application de transfert d'argent permettant aux utilisateurs de gérer leurs transactions, leurs relations et leurs frais de transaction. L'application fournit une interface sécurisée pour l'ajout de relations entre utilisateurs, la gestion des informations des utilisateurs, et l'application des frais sur les transactions.
Fonctionnalités principales

    Gestion des utilisateurs : Les utilisateurs peuvent s'enregistrer, mettre à jour leurs informations, changer leur rôle, et supprimer leur compte.
    Gestion des relations entre utilisateurs : Les utilisateurs peuvent ajouter ou supprimer des relations avec d'autres utilisateurs, et consulter la liste de leurs relations.
    Transactions : Les utilisateurs peuvent effectuer des transactions entre eux, annuler des transactions et consulter l'historique des transactions.
    Frais de transaction : Les administrateurs peuvent définir et mettre à jour les frais de transaction applicables à l'ensemble des utilisateurs.
    Comptes : Les utilisateurs peuvent créer et gérer des comptes d'application et des comptes bancaires.
    Rôles : L'application permet de gérer les rôles des utilisateurs pour contrôler l'accès aux fonctionnalités.
    Monétisation : Suivi des frais générés par les transactions effectuées sur la plateforme.

Prérequis

Avant de commencer, assurez-vous d'avoir les éléments suivants installés :

    Java 17 ou supérieur
    Maven ou Gradle pour la gestion des dépendances
    Base de données : Une base de données relationnelle (par exemple, MySQL ou PostgreSQL)
    IDE : Un IDE comme IntelliJ IDEA, Eclipse ou VSCode pour développer et exécuter le projet

Installation
1. Cloner le projet

git clone https://github.com/votre-utilisateur/pay-my-buddy.git

2. Configuration de la base de données

Utilisez le script SQL qui se trouve dans le fichier script.sql pour initialiser les données de la base de données. Ce script crée un utilisateur admin avec l'email admin@example.com et le mot de passe Password123. 

3. Dépendances et build

Le projet utilise Maven pour la gestion des dépendances. Pour installer les dépendances et compiler le projet, exécutez la commande suivante :

mvn clean install

4. Exécuter l'application

Une fois les dépendances installées et le projet compilé, vous pouvez démarrer l'application en exécutant la commande suivante :

mvn spring-boot:run

L'application sera alors accessible à l'adresse suivante : http://localhost:8080.
API Endpoints
Utilisateurs

    POST /api/register : Inscription d'un nouvel utilisateur. Les utilisateurs peuvent s'inscrire en fournissant un email, un mot de passe, etc.
    GET /api/users/{userId} : Récupère les informations d'un utilisateur par son ID.
    PUT /api/users/{userId} : Met à jour les informations d'un utilisateur.
    GET /api/users : Récupère la liste de tous les utilisateurs. Seuls les utilisateurs avec le rôle ROLE_ADMIN peuvent accéder à cette route.
    PUT /api/users/{id}/role/{roleName} : Met à jour le rôle d'un utilisateur.
    DELETE /api/users/{id} : Supprime un utilisateur par son ID.
    GET /api/users/role/{role} : Récupère les utilisateurs ayant un rôle spécifique.

Relations Utilisateurs

    POST /api/relation/add : Ajoute une relation entre deux utilisateurs par email.
    DELETE /api/relation/delete : Supprime une relation entre deux utilisateurs.
    GET /api/relation/all/{userId} : Récupère toutes les relations d'un utilisateur.
    GET /api/relation/check : Vérifie si une relation existe entre deux utilisateurs.

Transactions

    POST /api/transactions/create : Crée une nouvelle transaction entre deux utilisateurs.
    GET /api/transactions/allByUser/{userId} : Récupère l'historique des transactions d'un utilisateur.
    DELETE /api/transactions/cancel/{transactionId} : Annule une transaction existante.
    GET /api/transactions/fee : Calcule et retourne le total des frais de transaction appliqués.

Frais de Transaction

    GET /api/transactionfee : Récupère le frais de transaction actuellement actif.
    POST /api/transactionfee : Crée un nouveau frais de transaction.
    PUT /api/transactionfee/update/id/{id}/percent/{newPercent} : Met à jour le pourcentage du frais de transaction.
    DELETE /api/transactionfee/id/{id} : Supprime un frais de transaction.

Comptes

    POST /api/appAccounts/user/{userId} : Crée un compte pour un utilisateur avec l'ID spécifié.
    GET /api/appAccounts/{accountId} : Récupère un compte d'application par son ID.
    GET /api/appAccounts/{userId}/balance : Récupère le solde du compte d'application d'un utilisateur.
    PUT /api/appAccounts/{accountId}/balance : Met à jour le solde du compte d'application d'un utilisateur.
    POST /api/bankAccounts/user/{userId} : Crée un compte bancaire pour un utilisateur.
    GET /api/bankAccounts/user/{userId} : Récupère tous les comptes bancaires associés à un utilisateur.

Rôles

    GET /api/roles : Récupère tous les rôles disponibles dans l'application.
    POST /api/roles : Crée un nouveau rôle dans l'application.
    PUT /api/roles/{roleId} : Met à jour un rôle existant par son ID.
    DELETE /api/roles/{roleId} : Supprime un rôle par son ID.

Monétisation

    GET /api/monetization/transaction/{transactionId} : Récupère les détails de la monétisation d'une transaction spécifique.
    GET /api/monetization/total : Récupère le total des montants générés par la monétisation de toutes les transactions.

Sécurité

    Authentification et Autorisation : L'application utilise Spring Security pour sécuriser l'accès aux différentes ressources. Seuls les administrateurs peuvent accéder aux endpoints relatifs aux utilisateurs, aux relations, aux rôles, et aux frais de transaction.
    Préfixe @PreAuthorize : Utilisé pour limiter l'accès aux méthodes basées sur les rôles des utilisateurs. Par exemple, @PreAuthorize("hasRole('ROLE_ADMIN')") garantit que seuls les utilisateurs avec le rôle ROLE_ADMIN peuvent accéder à certaines routes.

Architecture

L'architecture du projet suit une approche MVC (Modèle-Vue-Contrôleur) avec les couches suivantes :

    Contrôleurs : Gèrent les requêtes HTTP et appellent les services appropriés pour traiter les demandes des utilisateurs.
    Services : Contiennent la logique métier pour gérer les comptes utilisateurs, les relations, les transactions, les rôles, et la monétisation.
    Repositories : Fournissent des méthodes pour interagir avec la base de données via JPA (Java Persistence API).
    Entités : Représentent les modèles de données, tels que User, Transaction, TransactionFee, etc.

Logs et Debugging

Des logs détaillés sont utilisés tout au long de l'application pour faciliter le suivi des actions effectuées, comme la création et la mise à jour des utilisateurs, des relations, des transactions, des rôles et des frais. Les logs sont particulièrement utiles pour :

    Suivre les requêtes HTTP effectuées.
    Détecter des erreurs et anomalies dans l'application.
    Assurer une traçabilité des actions réalisées par les utilisateurs.
