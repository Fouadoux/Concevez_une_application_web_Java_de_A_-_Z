document.addEventListener("DOMContentLoaded", () => {
    const userIdElement = document.getElementById("userId"); // Récupérer le div contenant l'ID utilisateur
    const userId = userIdElement.dataset.userId; // Extraire l'ID utilisateur depuis l'attribut `data-user-id`
    const addRelationForm = document.getElementById("addRelationForm");
    const messageDiv = document.getElementById("message");

    if (!userId) {
        console.error("ID utilisateur non trouvé. Veuillez vérifier l'attribut `data-user-id`.");
        return;
    }

    addRelationForm.addEventListener("submit", async (event) => {
        event.preventDefault(); // Empêche le rechargement de la page

        // Récupérer l'email du formulaire
        const email = document.getElementById("email").value;

        try {
            // Envoyer une requête POST à l'API pour ajouter la relation
            const response = await fetch("/api/relation/add", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: new URLSearchParams({ userId, email }),
            });

            if (response.ok) {
                // Succès
                const result = await response.text();
                messageDiv.textContent = result;
                messageDiv.className = "alert alert-success";
                messageDiv.style.display = "block";
                addRelationForm.reset(); // Réinitialise le formulaire
            } else {
                // Erreur côté serveur
                const errorText = await response.text();
                messageDiv.textContent = `Erreur : ${errorText}`;
                messageDiv.className = "alert alert-danger";
                messageDiv.style.display = "block";
            }
        } catch (error) {
            // Erreur côté client
            console.error("Erreur lors de la requête :", error);
            messageDiv.textContent = "Une erreur s'est produite. Veuillez réessayer.";
            messageDiv.className = "alert alert-danger";
            messageDiv.style.display = "block";
        }
    });
});
