document.addEventListener("DOMContentLoaded", () => {
    // Fonctionnalité des chevrons
    const inputAmount = document.getElementById("amount");
    const chevronUp = document.querySelector(".chevron-up");
    const chevronDown = document.querySelector(".chevron-down");

    if (!inputAmount || !chevronUp || !chevronDown) {
        console.error("Les éléments nécessaires pour les chevrons n'ont pas été trouvés dans le DOM.");
    } else {
        // Fonction pour incrémenter la valeur
        function incrementAmount() {
            let currentValue = parseInt(inputAmount.value) || 0;
            const max = parseInt(inputAmount.max) || 9999; // Valeur maximale
            if (currentValue < max) {
                inputAmount.value = currentValue + 1;
            }
        }

        // Fonction pour décrémenter la valeur
        function decrementAmount() {
            let currentValue = parseInt(inputAmount.value) || 0;
            const min = parseInt(inputAmount.min) || 0; // Valeur minimale
            if (currentValue > min) {
                inputAmount.value = currentValue - 1;
            }
        }

        // Écouter les clics sur les chevrons
        chevronUp.addEventListener("click", incrementAmount);
        chevronDown.addEventListener("click", decrementAmount);

        // Permettre l'ajustement avec les flèches haut/bas du clavier
        inputAmount.addEventListener("keydown", (event) => {
            if (event.key === "ArrowUp") {
                incrementAmount();
                event.preventDefault(); // Empêche le défilement de la page
            } else if (event.key === "ArrowDown") {
                decrementAmount();
                event.preventDefault();
            }
        });
    }

    // Fonctionnalité de transaction
    const transactionForm = document.getElementById("transactionForm");
    const userIdElement = document.getElementById("userId"); // Récupérer le div contenant l'ID utilisateur

    if (!userIdElement) {
        console.error("L'élément contenant l'ID utilisateur est introuvable.");
        return;
    }

    // Récupérez l'ID utilisateur depuis l'attribut `data-user-id`
    const senderId = userIdElement.dataset.userId;

    // Vérifiez que l'ID utilisateur est valide
    if (!senderId) {
        console.error("ID utilisateur manquant.");
        return;
    }

    if (transactionForm) {
        transactionForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            try {
                const formData = new FormData(transactionForm);
                formData.append("senderId", senderId);

                console.log("Données envoyées :", Object.fromEntries(formData.entries()));

                const response = await fetch(transactionForm.action, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded",
                    },
                    body: new URLSearchParams(formData),
                });

                if (response.ok) {
                    alert("Transaction créée avec succès !");
                    window.location.reload();
                } else {
                    const errorText = await response.text();
                    alert(`Erreur lors de la création de la transaction : ${errorText}`);
                }
            } catch (error) {
                console.error("Erreur lors de la requête :", error);
                alert("Une erreur est survenue. Veuillez réessayer.");
            }
        });
    }
});
