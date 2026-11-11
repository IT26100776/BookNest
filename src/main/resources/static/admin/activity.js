"use strict";

const rows = document.querySelector("#activity-rows");
const statusMessage = document.querySelector("#activity-status");
const refreshButton = document.querySelector("#refresh-activity");
const dateFormat = new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
    timeStyle: "short"
});
const actionLabels = { CREATED: "Created", UPDATED: "Edited", DELETED: "Deleted" };

function activityRow(entry) {
    const row = document.createElement("tr");
    const date = row.insertCell();
    date.className = "date";
    date.textContent = dateFormat.format(new Date(entry.timestamp));

    const action = row.insertCell();
    const badge = document.createElement("span");
    badge.className = "badge badge-soft";
    if (Object.hasOwn(actionLabels, entry.action)) {
        badge.classList.add(`badge-${entry.action.toLowerCase()}`);
    }
    badge.textContent = actionLabels[entry.action] || entry.action;
    action.append(badge);

    row.insertCell().textContent = entry.actor;
    row.insertCell().textContent = entry.accountId;
    return row;
}

async function loadActivity() {
    refreshButton.disabled = true;
    statusMessage.classList.remove("error");
    statusMessage.textContent = "Loading activity…";

    try {
        const response = await fetch("/api/admin/activity");
        if (response.redirected) {
            window.location.reload();
            return;
        }
        if (!response.ok) {
            throw new Error("Could not load the activity log. Try refreshing.");
        }

        const activity = await response.json();
        rows.replaceChildren(...activity.map(activityRow));
        statusMessage.textContent = activity.length
            ? `Showing ${activity.length} recent account ${activity.length === 1 ? "change" : "changes"}.`
            : "No account changes have been recorded yet.";
    } catch {
        statusMessage.classList.add("error");
        statusMessage.textContent = "Could not load the activity log. Try refreshing.";
    } finally {
        refreshButton.disabled = false;
    }
}

refreshButton.addEventListener("click", loadActivity);
loadActivity();
