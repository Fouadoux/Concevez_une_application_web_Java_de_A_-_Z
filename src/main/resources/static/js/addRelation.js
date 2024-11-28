document.addEventListener("DOMContentLoaded", () => {
    const userIdElement = document.getElementById("userId");
    const userId = userIdElement?.dataset.userId;
    const addRelationForm = document.getElementById("addRelationForm");
    const addButton = document.getElementById("addButton");
    const messageDiv = document.getElementById("message");

    if (!userId) {
        console.error("ID utilisateur non trouvé. Veuillez vérifier l'attribut `data-user-id`.");
        return;
    }

    // Fonction pour gérer la soumission du formulaire
    const handleSubmit = async () => {
        const email = document.getElementById("email").value;

        try {
            const response = await fetch("/api/relation/add", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: new URLSearchParams({ userId, email }),
            });

            if (response.ok) {
                const result = await response.text();
                messageDiv.textContent = `Relation ajouter a votre liste d'amis`;
                messageDiv.className = "alert alert-success";
                messageDiv.style.display = "block";
                addRelationForm.reset();
            } else {
                const errorText = await response.text();
                messageDiv.textContent = `Erreur : ${errorText}`;
                messageDiv.className = "alert alert-danger";
                messageDiv.style.display = "block";
            }
        } catch (error) {
            console.error("Erreur lors de la requête :", error);
            messageDiv.textContent = "Une erreur s'est produite. Veuillez réessayer.";
            messageDiv.className = "alert alert-danger";
            messageDiv.style.display = "block";
        }
    };

    // Associer l'événement "click" sur l'image à la soumission du formulaire
    addButton.addEventListener("click", (event) => {
        event.preventDefault();
        handleSubmit();
    });

    // Associer également l'événement "submit" au cas où l'utilisateur utiliserait "Entrée"
    addRelationForm.addEventListener("submit", (event) => {
        event.preventDefault();
        handleSubmit();
    });
});
