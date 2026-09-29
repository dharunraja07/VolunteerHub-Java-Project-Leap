let data = [];

const $ = id => document.getElementById(id);

async function load() {
  try {
    data = await api(API.volunteers);
    render(data);
  } catch (error) {
    $("rows").innerHTML =
      `<tr><td colspan="5" class="empty">${esc(error.message)}</td></tr>`;
  }
}

function render(volunteers) {
  $("rows").innerHTML = volunteers.length
    ? volunteers.map(volunteer => `
        <tr>
          <td>
            <div class="cell">
              <div class="avatar">${esc(initials(volunteer.name))}</div>
              <div>
                <strong>${esc(volunteer.name)}</strong>
                <div class="muted">Volunteer ID #${volunteer.id}</div>
              </div>
            </div>
          </td>
          <td>${esc(volunteer.email)}</td>
          <td>${esc(volunteer.phone)}</td>
          <td id="h${volunteer.id}">—</td>
          <td>
            <div class="table-actions">
              <button class="btn ghost small" onclick="edit(${volunteer.id})">Edit</button>
              <button class="btn danger small" onclick="del(${volunteer.id})">Delete</button>
            </div>
          </td>
        </tr>
      `).join("")
    : '<tr><td colspan="5" class="empty">No volunteers found.</td></tr>';

  volunteers.forEach(volunteer => {
    api(`${API.volunteers}/${volunteer.id}/hours`)
      .then(hours => {
        const cell = $("h" + volunteer.id);
        if (cell) {
          cell.textContent = Number(hours).toFixed(1) + " h";
        }
      })
      .catch(() => {});
  });
}

function openVolunteer(volunteer = null) {
  $("modal").classList.remove("hidden");
  $("modalTitle").textContent = volunteer
    ? "Edit volunteer"
    : "Add volunteer";

  $("id").value = volunteer?.id || "";
  $("name").value = volunteer?.name || "";
  $("email").value = volunteer?.email || "";
  $("phone").value = volunteer?.phone || "";
}

function closeVolunteer() {
  $("modal").classList.add("hidden");
}

function edit(id) {
  openVolunteer(data.find(volunteer => volunteer.id === id));
}

async function del(id) {
  if (!confirm("Delete this volunteer?")) return;

  try {
    await api(`${API.volunteers}/${id}`, { method: "DELETE" });
    alertBox("Volunteer deleted successfully.");
    load();
  } catch (error) {
    alertBox(error.message, "error");
  }
}

function validateVolunteer() {
  const name = $("name").value.trim();
  const email = $("email").value.trim();
  const phone = $("phone").value.trim();

  if (!name) return "Name is required";

  if (!/^[\p{L}]+(?:\s+[\p{L}]+)*$/u.test(name)) {
    return "Name must contain only letters and spaces";
  }

  if (!email) return "Email is required";

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    return "Enter a valid email address";
  }

  if (!/^\d{10}$/.test(phone)) {
    return "Phone must contain exactly 10 digits";
  }

  return "";
}

$("form").addEventListener("submit", async event => {
  event.preventDefault();

  const validationMessage = validateVolunteer();

  if (validationMessage) {
    alertBox(validationMessage, "error");
    return;
  }

  const id = $("id").value;

  const body = {
    name: $("name").value.trim(),
    email: $("email").value.trim(),
    phone: $("phone").value.trim()
  };

  try {
    await api(
      id ? `${API.volunteers}/${id}` : API.volunteers,
      {
        method: id ? "PUT" : "POST",
        body: JSON.stringify(body)
      }
    );

    closeVolunteer();
    alertBox(
      id
        ? "Volunteer updated successfully."
        : "Volunteer added successfully."
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
      `${item.name} ${item.email} ${item.phone}`
        .toLowerCase()
        .includes(query)
    )
  );
});

$("phone").addEventListener("input", () => {
  $("phone").value = $("phone").value.replace(/\D/g, "").slice(0, 10);
});

load();
