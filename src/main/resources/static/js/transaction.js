document.addEventListener("DOMContentLoaded", () => {
    const transactionForm = document.getElementById("transactionForm");
    const userIdElement = document.getElementById("userId"); // Récupérer le div contenant l'ID utilisateur

    // Vérifiez que l'élément existe
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
