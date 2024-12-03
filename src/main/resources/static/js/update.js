document.getElementById("submitButton").addEventListener("click", async () => {
    // Récupérez les données du formulaire
    const userIdElement = document.getElementById("userId");
    const userId = userIdElement.getAttribute("data-user-id");
    console.log("User ID:", userId);
    const data = {
        userName: document.getElementById("userName").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };

    try {
        // Envoie de la requête PUT avec JSON
        const response = await fetch(`/api/users/update/${userId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const message = await response.text(); // Récupère le message du serveur
            displaySuccessMessage(message); // Affiche le message
        } else {
            const error = await response.text();
            console.error("Erreur : ", error);
            alert("Une erreur s'est produite lors de la mise à jour.");
        }
    } catch (error) {
        console.error("Erreur de requête : ", error);
        alert("Impossible de contacter le serveur.");
    }
});

// Fonction pour afficher le message de succès
function displaySuccessMessage(message) {
    const successMessage = document.getElementById("successMessage");
    successMessage.textContent = message;
    successMessage.style.display = "block"; // Affiche le message
}