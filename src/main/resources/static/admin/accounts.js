"use strict";

const accountsUrl = "/api/admin/admins";
const rows = document.querySelector("#account-rows");
const statusMessage = document.querySelector("#status");
const addButton = document.querySelector("#add-admin");
const dialog = document.querySelector("#account-dialog");
const dialogTitle = document.querySelector("#dialog-title");
const form = document.querySelector("#account-form");
const formError = document.querySelector("#form-error");
const passwordField = document.querySelector("#password-field");
const passwordInput = form.elements.namedItem("password");
const saveButton = document.querySelector("#save-admin");
const cancelButton = document.querySelector("#cancel-edit");

const deleteDialog = document.querySelector("#delete-dialog");
const deleteMessage = document.querySelector("#delete-message");

let editingAccount = null;
let pendingDeletion = null;

function setStatus(message, isError = false) {
    statusMessage.textContent = message;
    statusMessage.classList.toggle("error", isError);
}

async function request(url, options) {
    const response = await fetch(url, options);
    if (response.redirected) {
        window.location.reload();
        throw new Error("Your session has ended. Sign in again.");
    }
    return response;
}

async function loadAccounts(message) {
    const response = await request(accountsUrl);
    if (!response.ok) throw new Error("Could not load admin accounts.");

    const accounts = await response.json();
    rows.replaceChildren(...accounts.map(accountRow));
    addButton.disabled = false;
    setStatus(message || (accounts.length ? "" : "No admin accounts found."));
}

function accountRow(account) {
    const row = document.createElement("tr");
    // Add account values as text so they are never interpreted as HTML.
    row.insertCell().textContent = account.username;
    row.insertCell().textContent = account.email;
    const role = row.insertCell();
    const isSuperAdmin = account.adminLevel === "SUPER_ADMIN";
    const roleBadge = document.createElement("span");
    roleBadge.textContent = isSuperAdmin ? "Super admin" : "Admin";
    roleBadge.className = isSuperAdmin ? "badge badge-soft badge-super" : "badge badge-soft";
    role.append(roleBadge);

    const actions = document.createElement("div");
    actions.className = "actions";
    const edit = actionButton("Edit", () => openForm(account));
    edit.classList.add("btn-outline-secondary");
    const remove = actionButton("Delete", () => openDeleteDialog(account, remove));
    remove.classList.add("btn-outline-danger");
    actions.append(edit, remove);
    row.insertCell().append(actions);
    return row;
}

function actionButton(label, handler) {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "btn btn-sm";
    button.textContent = label;
    button.addEventListener("click", handler);
    return button;
}

function openForm(account = null) {
    editingAccount = account;
    form.reset();
    formError.hidden = true;
    dialogTitle.textContent = account ? "Edit admin" : "Add admin";
    passwordField.hidden = Boolean(account);
    passwordInput.disabled = Boolean(account);
    passwordInput.required = !account;

    if (account) {
        form.elements.namedItem("username").value = account.username;
        form.elements.namedItem("email").value = account.email;
        form.elements.namedItem("adminLevel").value = account.adminLevel;
    }

    dialog.showModal();
}

async function writeAccount(url, method, payload) {
    // Spring Security supplies the header name and token for this session.
    const csrfResponse = await request(`${accountsUrl}/csrf`);
    if (!csrfResponse.ok) throw new Error("Could not get a security token.");

    const { headerName, token } = await csrfResponse.json();
    const options = { method, headers: { [headerName]: token } };
    if (payload) {
        options.headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(payload);
    }
    return request(url, options);
}

function errorMessage(status, action) {
    if (status === 400) return "Check the account details and try again.";
    if (status === 403) return "You do not have permission to make this change.";
    if (status === 404) return "This account no longer exists. Refresh the list.";
    if (status === 409) {
        if (action === "create") return "An account with that username already exists.";
        if (action === "edit") {
            return "Use a unique username and keep at least one super admin.";
        }
        return "You cannot delete your own account or the last super admin.";
    }
    return "BookNest could not save the change.";
}

async function refreshAfterSave(message) {
    try {
        await loadAccounts(message);
    } catch {
        setStatus(`${message} Refresh the page to see the updated list.`, true);
    }
}

async function saveAccount(event) {
    event.preventDefault();
    const account = editingAccount;
    const fields = new FormData(form);
    const payload = {
        username: fields.get("username").trim(),
        email: fields.get("email").trim(),
        adminLevel: fields.get("adminLevel")
    };

    if (!account) payload.password = fields.get("password");
    const url = account ? `${accountsUrl}/${encodeURIComponent(account.userId)}` : accountsUrl;

    saveButton.disabled = true;
    cancelButton.disabled = true;
    formError.hidden = true;
    try {
        const response = await writeAccount(url, account ? "PUT" : "POST", payload);
        if (!response.ok) throw new Error(errorMessage(response.status, account ? "edit" : "create"));
        dialog.close();
        await refreshAfterSave(account ? "Account updated." : "Account created.");
    } catch (error) {
        formError.textContent = error.message;
        formError.hidden = false;
    } finally {
        saveButton.disabled = false;
        cancelButton.disabled = false;
    }
}

function openDeleteDialog(account, button) {
    pendingDeletion = { account, button };
    deleteMessage.textContent = `${account.username} will no longer be able to sign in.`;
    deleteDialog.returnValue = "";
    deleteDialog.showModal();
}

async function deleteAccount(account, button) {
    button.disabled = true;
    try {
        const response = await writeAccount(
            `${accountsUrl}/${encodeURIComponent(account.userId)}`, "DELETE");
        if (!response.ok) throw new Error(errorMessage(response.status, "delete"));
        await refreshAfterSave("Account deleted.");
    } catch (error) {
        setStatus(error.message, true);
    } finally {
        button.disabled = false;
    }
}

addButton.addEventListener("click", () => openForm());
cancelButton.addEventListener("click", () => dialog.close());
dialog.addEventListener("cancel", event => {
    if (saveButton.disabled) event.preventDefault();
});
dialog.addEventListener("close", () => form.reset());
form.addEventListener("submit", saveAccount);
deleteDialog.addEventListener("close", () => {
    const selection = pendingDeletion;
    pendingDeletion = null;
    if (selection && deleteDialog.returnValue === "delete") {
        deleteAccount(selection.account, selection.button);
    }
});

loadAccounts().catch(error => setStatus(error.message, true));
