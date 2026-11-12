"use strict";

async function showAdminNavigation() {
    try {
        const response = await fetch("/api/admin/me", {
            headers: { Accept: "application/json" }
        });
        if (response.redirected || !response.ok) return;

        const account = await response.json();
        const isSuperAdmin = account.adminLevel?.trim().toUpperCase() === "SUPER_ADMIN";
        document.querySelector("[data-admin-role]").textContent =
            isSuperAdmin ? "Super admin" : "Administrator";
        if (isSuperAdmin) {
            document.querySelectorAll("[data-super-admin]").forEach(element => {
                element.hidden = false;
            });
        }
    } catch (error) {
        console.error("Could not load admin navigation.", error);
    }
}

showAdminNavigation();
