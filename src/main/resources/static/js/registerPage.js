const registerForm = document.getElementById("registerForm");
const messageDiv = document.getElementById("message");

registerForm.addEventListener("submit", async function (event) {
    event.preventDefault(); // Empêche le rechargement de la page

    const formData = new FormData(this);

    try {
        const response = await fetch("/api/register", {
            method: "POST",
            body: new URLSearchParams(formData),
        });

        if (!response.ok) {
            throw new Error("Request failed with status " + response.status);
        }

        const data = await response.json();

        if (data.status === "success") {
            messageDiv.innerHTML = `<div style="color: green;">${data.message}</div>`;
        } else {
            messageDiv.innerHTML = `<div style="color: red;">${data.message}</div>`;
        }
    } catch (error) {
        messageDiv.innerHTML = `<div style="color: red;">An error occurred. Please try again.</div>`;
        console.error("Error during registration:", error);
    }
});
