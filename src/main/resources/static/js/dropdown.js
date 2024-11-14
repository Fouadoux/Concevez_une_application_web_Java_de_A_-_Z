import { getCurrentUserId } from "./utils.js";

document.addEventListener("DOMContentLoaded", async () => {
    const relationSelect = document.getElementById("relationSelect");

    if (!relationSelect) {
        console.error("L'élément relationSelect n'a pas été trouvé dans le DOM.");
        return;
    }

    try {
        // Récupérer l'ID de l'utilisateur actuel
        const userId = await getCurrentUserId();
        console.log("ID utilisateur récupéré :", userId);
        if (userId) {
            // Remplir le dropdown avec les relations de l'utilisateur
            await populateDropdown(userId);

            // Gérer les changements de sélection dans le dropdown
            relationSelect.addEventListener("change", (event) => {
                const selectedOption = event.target.options[event.target.selectedIndex];
                const selectedUserId = selectedOption.value;
                const selectedUserName = selectedOption.textContent;

                alert(`Relation sélectionnée : ${selectedUserName} (ID: ${selectedUserId})`);
            });
        } else {
            console.error("Impossible de récupérer l'ID de l'utilisateur actuel.");
        }
    } catch (error) {
        console.error("Erreur lors de l'initialisation du dropdown :", error);
    }

    /**
     * Remplit dynamiquement le dropdown avec les relations de l'utilisateur.
     * @param {string} userId - L'ID de l'utilisateur actuel.
     */
    async function populateDropdown(userId) {
        try {
            const response = await fetch(`/api/relation/all/${userId}`);
            if (!response.ok) {
                throw new Error(`Erreur HTTP : ${response.status}`);
            }

            const relations = await response.json();

            // Réinitialiser le contenu du dropdown
            relationSelect.innerHTML = "";

            // Ajouter une option par défaut
            const defaultOption = document.createElement("option");
            defaultOption.value = "";
            defaultOption.textContent = "Sélectionner une relation";
            defaultOption.disabled = true;
            defaultOption.selected = true;
            relationSelect.appendChild(defaultOption);

            // Ajouter les options des relations
            relations.forEach((relation) => {
                const option = document.createElement("option");
                option.value = relation.id;
                option.textContent = relation.name;
                relationSelect.appendChild(option);
            });
        } catch (error) {
            console.error("Erreur lors du remplissage du dropdown :", error);
            alert("Une erreur est survenue lors du chargement des relations.");
        }
    }
});
