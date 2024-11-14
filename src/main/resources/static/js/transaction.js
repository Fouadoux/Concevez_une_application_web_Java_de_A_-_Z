import { getCurrentUserId } from "./utils.js";

document.addEventListener("DOMContentLoaded", () => {
    const transactionForm = document.getElementById("transactionForm");

    if (transactionForm) {
        transactionForm.addEventListener("submit", async (event) => {
            event.preventDefault(); // Empêche le rechargement de la page

            try {
                // Récupérer l'ID de l'utilisateur actuel
                const senderId = await getCurrentUserId();
                if (!senderId) {
                    alert("Impossible de récupérer l'utilisateur actuel.");
                    return;
                }

                // Récupérer les données du formulaire
                const formData = new FormData(transactionForm);

                // Ajouter explicitement les champs nécessaires
                formData.append("senderId", senderId);

                // Facultatif : afficher les données pour débogage
                console.log("Données envoyées :", Object.fromEntries(formData.entries()));

                // Envoyer la requête POST
                const response = await fetch(transactionForm.action, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                    },
                    body: new URLSearchParams(formData),
                });

                if (response.ok) {
                    const result = await response.json();
                    alert("Transaction créée avec succès !");
                    console.log("Détails de la transaction :", result);

                    // Recharger la page
                    window.location.reload();

                } else {
                    const errorText = await response.text();
                    alert(`Erreur lors de la création de la transaction : ${errorText}`);
                }
            } catch (error) {
                console.error("Erreur lors de la requête :", error);
                alert("Une erreur est survenue lors de la requête. Veuillez réessayer.");
            }
        });
    }
});
