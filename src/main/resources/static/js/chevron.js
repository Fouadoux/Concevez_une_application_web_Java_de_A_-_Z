document.addEventListener("DOMContentLoaded", () => {
    const inputAmount = document.getElementById("amount");
    const chevronUp = document.querySelector(".chevron-up");
    const chevronDown = document.querySelector(".chevron-down");

    if (!inputAmount || !chevronUp || !chevronDown) {
        console.error("Les éléments nécessaires pour les chevrons n'ont pas été trouvés dans le DOM.");
        return;
    }

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
});
