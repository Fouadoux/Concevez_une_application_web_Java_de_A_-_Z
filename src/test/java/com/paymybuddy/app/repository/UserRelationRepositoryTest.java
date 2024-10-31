package com.paymybuddy.app.repository;

import com.paymybuddy.app.entity.Role;
import com.paymybuddy.app.entity.User;
import com.paymybuddy.app.entity.UserRelation;
import com.paymybuddy.app.entity.id.UserRelationId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test pour {@link UserRelationRepository}.
 * <p>
 * Cette classe teste les opérations CRUD sur les relations entre utilisateurs
 * via le repository {@link UserRelationRepository}. Elle couvre la création
 * et la mise à jour des relations entre utilisateurs.
 * </p>
 */
@DataJpaTest
public class UserRelationRepositoryTest {

    @Autowired
    private UserRelationRepository userRelationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user1;
    private User user2;
    private UserRelation userRelation;

    /**
     * Initialisation des données avant chaque test.
     * <p>
     * Cette méthode crée deux utilisateurs {@link User} et une relation
     * {@link UserRelation} entre eux. Elle est exécutée avant chaque test afin
     * de garantir un état cohérent des objets à tester.
     * </p>
     */
    @BeforeEach
    public void setUp() {
        Role role=new Role();
        role.setRoleName("user");
        roleRepository.save(role);
        // Création et sauvegarde de l'utilisateur 1
        user1 = new User();
        user1.setUserName("User 1");
        user1.setEmail("User1@example.com");
        user1.setPassword("password");
        user1.setRole(role);
        userRepository.save(user1);

        // Création et sauvegarde de l'utilisateur 2
        user2 = new User();
        user2.setUserName("User 2");
        user2.setEmail("User2@example.com");
        user2.setPassword("password");
        user2.setRole(role);
        userRepository.save(user2);

        // Création de la relation entre user1 et user2
        userRelation = new UserRelation();
        userRelation.setUserId(user1.getId());
        userRelation.setUserRelationId(user2.getId());
        userRelation.setUser(user1);
        userRelation.setStatus(true);
        userRelation.setCreatedAt(LocalDateTime.now());
    }

    /**
     * Test de la création d'une relation utilisateur.
     * <p>
     * Ce test vérifie qu'une relation entre deux utilisateurs peut être correctement
     * sauvegardée dans la base de données via {@link UserRelationRepository}.
     * </p>
     * <ul>
     *     <li>Sauvegarde de la relation dans le repository.</li>
     *     <li>Récupération et vérification de la relation sauvegardée.</li>
     *     <li>Vérification que les données sont correctes.</li>
     * </ul>
     */
    @Test
    public void testCreateUserRelation() {
        // Sauvegarde de la relation
        userRelationRepository.save(userRelation);

        // Récupération de la relation sauvegardée via findById
        UserRelationId userRelationId = new UserRelationId(user1.getId(), user2.getId());
        Optional<UserRelation> savedRelation = userRelationRepository.findById(userRelationId);
        // Vérifications
        assertTrue(savedRelation.isPresent());
        assertEquals(user1, savedRelation.get().getUser());
        assertEquals(user2.getId(), savedRelation.get().getUserRelationId());
        assertTrue(savedRelation.get().isStatus());
    }

    /**
     * Test de la mise à jour du statut d'une relation utilisateur.
     * <p>
     * Ce test vérifie que le statut d'une relation entre utilisateurs peut être
     * modifié et sauvegardé dans la base de données via {@link UserRelationRepository}.
     * </p>
     * <ul>
     *     <li>Sauvegarde initiale de la relation avec un statut.</li>
     *     <li>Mise à jour du statut et sauvegarde.</li>
     *     <li>Vérification de la mise à jour dans la base de données.</li>
     * </ul>
     */
    @Test
    public void testUpdateStatus() {
        // Sauvegarde initiale de la relation
        userRelationRepository.save(userRelation);

        // Mise à jour du statut et sauvegarde
        userRelation.setStatus(false);
        userRelationRepository.save(userRelation);

        // Récupération de la relation mise à jour via findById
        UserRelationId userRelationId = new UserRelationId(user1.getId(), user2.getId());
        Optional<UserRelation> updatedRelation  = userRelationRepository.findById(userRelationId);
        // Vérifications
        assertTrue(updatedRelation .isPresent());
        assertFalse(updatedRelation .get().isStatus());
    }

    /**
     * Test de la suppression d'une relation utilisateur.
     * <p>
     * Ce test vérifie que la relation entre deux utilisateurs peut être supprimée de la base de données
     * via {@link UserRelationRepository}.
     * </p>
     * <ul>
     *     <li>Sauvegarde initiale de la relation.</li>
     *     <li>Suppression de la relation.</li>
     *     <li>Vérification que la relation n'existe plus dans la base de données.</li>
     * </ul>
     */
    @Test
    public void testDeleteUserRelation() {
        // Sauvegarde initiale de la relation
        userRelationRepository.save(userRelation);

        // Suppression de la relation
        userRelationRepository.delete(userRelation);

        // Vérification que la relation a été supprimée
        Optional<UserRelation> deletedRelation = userRelationRepository.findById(new UserRelationId(user1.getId(), user2.getId()));
        assertFalse(deletedRelation.isPresent());
    }

}
