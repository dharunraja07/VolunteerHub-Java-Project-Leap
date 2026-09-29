let attendanceData = [];
let signupData = [];

const $ = id => document.getElementById(id);

async function load() {
  try {
    const [attendance, signups, volunteers] = await Promise.all([
      api(API.attendance),
      api(API.signups),
      api(API.volunteers)
    ]);

    attendanceData = attendance;
    signupData = signups;

    fillSignups(signups);
    fillVolunteers(volunteers);
    render(attendance);
  } catch (error) {
    $("rows").innerHTML =
      `<tr><td colspan="7" class="empty">${esc(error.message)}</td></tr>`;
  }
}

function fillSignups(signups) {
  $("signup").innerHTML =
    '<option value="">Select signup</option>' +
    signups.map(signup => `
      <option value="${signup.id}">
        ${esc(signup.volunteer?.name)} · ${esc(signup.event?.title)} · Signup #${signup.id}
      </option>
    `).join("");
}

function fillVolunteers(volunteers) {
  $("hoursVolunteer").innerHTML =
    '<option value="">Select a volunteer</option>' +
    volunteers.map(volunteer => `
      <option value="${volunteer.id}">
        ${esc(volunteer.name)} · Volunteer ID #${volunteer.id}
      </option>
    `).join("");
}

function render(records) {
  $("rows").innerHTML = records.length
    ? records.map(record => `
        <tr>
          <td>#${record.id}</td>
          <td>#${record.signup?.volunteer?.id ?? "—"}</td>
          <td><strong>${esc(record.signup?.volunteer?.name)}</strong></td>
          <td>${esc(record.signup?.event?.title)}</td>
          <td>
            <span class="badge-status ${record.attended ? "present" : "absent"}">
              ${record.attended ? "Present" : "Absent"}
            </span>
          </td>
          <td>${Number(record.hours || 0).toFixed(1)} h</td>
          <td>
            <div class="table-actions">
              <button class="btn ghost small" onclick="openEditAttendance(${record.id})">
                Edit
              </button>
              <button class="btn danger small" onclick="del(${record.id})">
                Delete
              </button>
            </div>
          </td>
        </tr>
      `).join("")
    : '<tr><td colspan="7" class="empty">No attendance records found.</td></tr>';
}

$("attendanceForm").addEventListener("submit", async event => {
  event.preventDefault();

  const signupId = $("signup").value;
  const attended = $("present").value;
  const hours = Number($("hours").value);

  if (!signupId) {
    alertBox("Please select a signup.", "error");
    return;
  }

  if (!Number.isFinite(hours) || hours < 0 || hours > 24) {
    alertBox("Hours must be between 0 and 24.", "error");
    return;
  }

  if (attended === "false" && hours > 0) {
    alertBox("Hours cannot be recorded when the volunteer is absent.", "error");
    return;
  }

  try {
    await api(
      `${API.attendance}?signupId=${encodeURIComponent(signupId)}&attended=${attended}&hours=${hours}`,
      { method: "POST" }
    );

    alertBox("Attendance recorded successfully.");
    event.target.reset();
    $("hours").value = 0;
    resetSelectSearch("signupSearch");
    load();
  } catch (error) {
    alertBox(error.message, "error");
  }
});

function openEditAttendance(id) {
  const record = attendanceData.find(item => item.id === id);

  if (!record) {
    alertBox("Attendance record could not be found.", "error");
    return;
  }

  $("editAttendanceId").value = record.id;
  $("editVolunteerName").value =
    `${record.signup?.volunteer?.name || "Unknown"} (Volunteer ID #${record.signup?.volunteer?.id ?? "—"})`;
  $("editEventName").value = record.signup?.event?.title || "Unknown";
  $("editPresent").value = String(record.attended);
  $("editHours").value = Number(record.hours || 0);

  $("editAttendanceModal").classList.remove("hidden");
}

function closeEditAttendance() {
  $("editAttendanceModal").classList.add("hidden");
}

$("editAttendanceForm").addEventListener("submit", async event => {
  event.preventDefault();

  const id = $("editAttendanceId").value;
  const attended = $("editPresent").value;
  const hours = Number($("editHours").value);

  if (!Number.isFinite(hours) || hours < 0 || hours > 24) {
    alertBox("Hours must be between 0 and 24.", "error");
    return;
  }

  if (attended === "false" && hours > 0) {
    alertBox("Hours cannot be recorded when the volunteer is absent.", "error");
    return;
  }

  try {
    await api(
      `${API.attendance}/${id}?attended=${attended}&hours=${hours}`,
      { method: "PUT" }
    );

    closeEditAttendance();
    alertBox("Attendance updated successfully.");
    load();
  } catch (error) {
    alertBox(error.message, "error");
  }
});

async function del(id) {
  if (!confirm("Delete this attendance record?")) return;

  try {
    await api(`${API.attendance}/${id}`, { method: "DELETE" });
    alertBox("Attendance deleted successfully.");
    load();
  } catch (error) {
    alertBox(error.message, "error");
  }
}

async function checkHours() {
  const volunteerId = $("hoursVolunteer").value;

  if (!volunteerId) {
    alertBox("Please select a volunteer.", "error");
    return;
  }

  try {
    const hours = await api(
      `${API.volunteers}/${volunteerId}/hours`
    );

    $("totalHours").textContent =
      Number(hours).toFixed(1) + " h";
  } catch (error) {
    $("totalHours").textContent = "—";
    alertBox(error.message, "error");
  }
}

$("present").addEventListener("change", () => {
  if ($("present").value === "false") {
    $("hours").value = 0;
  }
});

$("editPresent").addEventListener("change", () => {
  if ($("editPresent").value === "false") {
    $("editHours").value = 0;
  }
});

filterSelect("signup", "signupSearch");
filterSelect("hoursVolunteer", "hoursVolunteerSearch");

load();
