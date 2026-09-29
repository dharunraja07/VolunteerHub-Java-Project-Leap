let data = [];

const $ = id => document.getElementById(id);

async function load() {
  try {
    data = await api(API.events);
    render(data);
  } catch (error) {
    $("rows").innerHTML =
      `<tr><td colspan="5" class="empty">${esc(error.message)}</td></tr>`;
  }
}

function render(events) {
  $("rows").innerHTML = events.length
    ? events.map(event => `
        <tr>
          <td>
            <div class="cell">
              <div class="avatar">E</div>
              <div>
                <strong>${esc(event.title)}</strong>
                <div class="muted">${esc(event.description)}</div>
              </div>
            </div>
          </td>
          <td>${date(event.eventDate)}</td>
          <td>${esc(event.location)}</td>
          <td>${event.capacity}</td>
          <td>
            <div class="table-actions">
              <button class="btn ghost small" onclick="edit(${event.id})">Edit</button>
              <button class="btn danger small" onclick="del(${event.id})">Delete</button>
            </div>
          </td>
        </tr>
      `).join("")
    : '<tr><td colspan="5" class="empty">No events found.</td></tr>';
}

function openEvent(event = null) {
  $("modal").classList.remove("hidden");
  $("modalTitle").textContent = event ? "Edit event" : "Create event";
  $("id").value = event?.id || "";
  $("title").value = event?.title || "";
  $("description").value = event?.description || "";
  $("date").value = event?.eventDate || "";
  $("location").value = event?.location || "";
  $("capacity").value = event?.capacity ?? "";

  $("date").min = todayISO();

  if (!event) {
    $("date").value = "";
  }
}

function closeEvent() {
  $("modal").classList.add("hidden");
}

function edit(id) {
  openEvent(data.find(event => event.id === id));
}

async function del(id) {
  if (!confirm("Delete this event?")) return;

  try {
    await api(`${API.events}/${id}`, { method: "DELETE" });
    alertBox("Event deleted successfully.");
    load();
  } catch (error) {
    alertBox(error.message, "error");
  }
}

function validateEvent() {
  const title = $("title").value.trim();
  const description = $("description").value.trim();
  const eventDate = $("date").value;
  const location = $("location").value.trim();
  const capacity = Number($("capacity").value);

  if (!title) return "Title is required";
  if (!/\p{L}/u.test(title)) return "Title must contain at least one letter";
  if (!description) return "Description is required";
  if (!eventDate) return "Event date is required";
  if (eventDate < todayISO()) return "Event date cannot be in the past";
  if (!location) return "Location is required";
  if (!Number.isInteger(capacity) || capacity < 1) {
    return "Capacity must be at least 1";
  }
  if (capacity > 1000) {
    return "Capacity cannot exceed 1000";
  }

  return "";
}

$("form").addEventListener("submit", async event => {
  event.preventDefault();

  const validationMessage = validateEvent();

  if (validationMessage) {
    alertBox(validationMessage, "error");
    return;
  }

  const id = $("id").value;

  const body = {
    title: $("title").value.trim(),
    description: $("description").value.trim(),
    eventDate: $("date").value,
    location: $("location").value.trim(),
    capacity: Number($("capacity").value)
  };

  try {
    await api(
      id ? `${API.events}/${id}` : API.events,
      {
        method: id ? "PUT" : "POST",
        body: JSON.stringify(body)
      }
    );

    closeEvent();
    alertBox(
      id ? "Event updated successfully." : "Event created successfully."
    );
    load();
  } catch (error) {
    alertBox(error.message, "error");
  }
});

$("search").addEventListener("input", event => {
  const query = event.target.value.toLowerCase();

  render(
    data.filter(item =>
      `${item.title} ${item.description} ${item.location}`
        .toLowerCase()
        .includes(query)
    )
  );
});

$("date").min = todayISO();

load();
