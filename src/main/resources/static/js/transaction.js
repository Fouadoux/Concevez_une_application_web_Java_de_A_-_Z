document.addEventListener("DOMContentLoaded", () => {
    const inputAmount = document.querySelector("#amount");
    const chevronUp = document.querySelector(".chevron-up");
    const chevronDown = document.querySelector(".chevron-down");

    if (!inputAmount || !chevronUp || !chevronDown) {
        console.error("Les éléments nécessaires pour les chevrons n'ont pas été trouvés dans le DOM.");
        return;
    }

    function incrementAmount() {
        let currentValue = parseInt(inputAmount.value) || 0;
        const max = parseInt(inputAmount.max) || 9999; // Valeur maximale
        if (currentValue < max) {
            inputAmount.value = currentValue + 1;
        }
    }

    function decrementAmount() {
        let currentValue = parseInt(inputAmount.value) || 0;
        const min = parseInt(inputAmount.min) || 0; // Valeur minimale
        if (currentValue > min) {
            inputAmount.value = currentValue - 1;
        }
    }

    chevronUp.addEventListener("click", incrementAmount);
    chevronDown.addEventListener("click", decrementAmount);

    inputAmount.addEventListener("keydown", (event) => {
        if (event.key === "ArrowUp") {
            incrementAmount();
            event.preventDefault();
        } else if (event.key === "ArrowDown") {
            decrementAmount();
            event.preventDefault();
        }
    });
});
