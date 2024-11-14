import {getCurrentUserId} from "./utils.js";


document.addEventListener("DOMContentLoaded", async () => {
    //const userId = document.getElementById("userId").value; // Récupérer l'ID utilisateur
    const transactionsTableBody = document.querySelector("#transactionsTable tbody");

    const userId = await getCurrentUserId();
    if (!userId) {
        alert("Impossible de récupérer l'utilisateur actuel.");
        return;
    }


    // Fonction pour charger les transactions
    async function loadTransactions() {
        try {
            const response = await fetch(`/api/transactions/allByUser/${userId}`); // Appel GET à l'API
            if (!response.ok) {
                throw new Error(`Erreur HTTP : ${response.status}`);
            }

            const transactions = await response.json(); // Analyse de la réponse en JSON

            // Effacer les anciennes lignes avant d'ajouter les nouvelles
            transactionsTableBody.innerHTML = "";

            // Ajouter chaque transaction dans la table
            transactions.forEach(transaction => {
                const row = document.createElement("tr");

                if (transaction.receiverId == userId) {
                    row.innerHTML = `
            <td>${transaction.senderName}</td>
            <td>${transaction.description}</td>
            <td>+ ${transaction.amount.toFixed(2)} €</td>
            
        `;
                } else {
                    row.innerHTML = `
            <td>${transaction.receiverName}</td>
            <td>${transaction.description}</td>
            <td>- ${transaction.amount.toFixed(2)} €</td>
            
        `;
                }



                transactionsTableBody.appendChild(row);
            });
        } catch (error) {
            console.error("Erreur lors du chargement des transactions :", error);
            alert("Impossible de charger les transactions.");
        }
    }

    // Charger les transactions au chargement de la page
    await loadTransactions();
});
