// utils.js

/**
 * Récupère l'ID de l'utilisateur actuellement connecté via une requête à l'API.
 * @returns {Promise<string|null>} - L'ID utilisateur si trouvé, ou null en cas d'échec.
 */
async function getCurrentUserId() {
    try {
        const response = await fetch('/api/currentUser', {
            method: 'GET',
            credentials: 'include' // Inclut les cookies pour l'authentification
        });

        if (!response.ok) {
            throw new Error(`Erreur HTTP : ${response.status}`);
        }

        const data = await response.json();
        return data.id; // Retourne l'ID utilisateur
    } catch (error) {
        console.error("Erreur lors de la récupération de l'utilisateur actuel :", error);
        return null; // Retourne null en cas d'échec
    }
}

// Exporter la fonction (nécessaire si vous utilisez des modules ES)
export { getCurrentUserId };
