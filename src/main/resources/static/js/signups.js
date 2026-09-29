const $ = id => document.getElementById(id);

async function start() {
  try {
    const [events, volunteers] = await Promise.all([
      api(API.events),
      api(API.volunteers)
    ]);

    fill(
      $("event"),
      events,
      event => `${event.title} · ${event.eventDate} · Capacity ${event.capacity}`,
      "Select an event"
    );

    fill(
      $("filter"),
      events,
      event => `${event.title} · ${event.eventDate}`,
      "Select an event"
    );

    fill(
      $("volunteer"),
      volunteers,
      volunteer => `${volunteer.name} · Volunteer ID #${volunteer.id}`,
      "Select a volunteer"
    );

    filterSelect("event", "eventSearch");
    filterSelect("volunteer", "volunteerSearch");
    filterSelect("filter", "filterSearch");

    load();
  } catch (error) {
    alertBox(error.message, "error");
  }
}

function fill(element, array, label, placeholder) {
  element.innerHTML =
    `<option value="">${esc(placeholder)}</option>` +
    array
      .map(item =>
        `<option value="${item.id}">${esc(label(item))}</option>`
      )
      .join("");
}

async function load() {
  try {
    const signups = await api(API.signups);

    $("rows").innerHTML = signups.length
      ? signups.map(signup => `
          <tr>
            <td>#${signup.id}</td>
            <td>
              <strong>${esc(signup.volunteer?.name)}</strong>
              <div class="muted">Volunteer ID #${signup.volunteer?.id ?? "—"}</div>
            </td>
            <td>${esc(signup.event?.title)}</td>
            <td>${date(signup.signupDate)}</td>
            <td>
              <button class="btn danger small" onclick="del(${signup.id})">
                Remove
              </button>
            </td>
          </tr>
        `).join("")
      : '<tr><td colspan="5" class="empty">No signups found.</td></tr>';
  } catch (error) {
    $("rows").innerHTML =
      `<tr><td colspan="5" class="empty">${esc(error.message)}</td></tr>`;
  }
}

$("filter").addEventListener("change", async () => {
  const eventId = $("filter").value;

  if (!eventId) {
    $("participants").innerHTML =
      '<div class="empty">Select an event.</div>';
    return;
  }

  try {
    const signups = await api(`${API.signups}/event/${eventId}`);

    $("participants").innerHTML = signups.length
      ? signups.map(signup => `
          <div class="participant">
            <div class="avatar">${esc(initials(signup.volunteer?.name))}</div>
            <div>
              <strong>${esc(signup.volunteer?.name)}</strong>
              <small>${esc(signup.volunteer?.email || "")}</small>
              <small>Volunteer ID #${signup.volunteer?.id ?? "—"} · Signup ID #${signup.id}</small>
            </div>
          </div>
        `).join("")
      : '<div class="empty">No volunteers have signed up yet.</div>';
  } catch (error) {
    $("participants").innerHTML =
      `<div class="empty">${esc(error.message)}</div>`;
  }
});

$("signupForm").addEventListener("submit", async event => {
  event.preventDefault();

  if (!$("event").value || !$("volunteer").value) {
    alertBox("Please select both an event and a volunteer.", "error");
    return;
  }

  const selectedEventId = $("event").value;

  try {
    await api(
      `${API.signups}?eventId=${encodeURIComponent(selectedEventId)}&volunteerId=${encodeURIComponent($("volunteer").value)}`,
      { method: "POST" }
    );

    alertBox("Signup created successfully.");
    event.target.reset();

    resetSelectSearch("eventSearch");
    resetSelectSearch("volunteerSearch");

    load();

    if ($("filter").value === selectedEventId) {
      $("filter").dispatchEvent(new Event("change"));
    }
  } catch (error) {
    alertBox(error.message, "error");
  }
});

async function del(id) {
  if (!confirm("Remove this signup?")) return;

  try {
    await api(`${API.signups}/${id}`, { method: "DELETE" });
    alertBox("Signup removed successfully.");
    load();

    if ($("filter").value) {
      $("filter").dispatchEvent(new Event("change"));
    }
  } catch (error) {
    alertBox(error.message, "error");
  }
}

start();
