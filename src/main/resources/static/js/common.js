const API = {
  events: "/api/events",
  volunteers: "/api/volunteers",
  signups: "/api/signups",
  attendance: "/api/attendance"
};

document.addEventListener("DOMContentLoaded", () => {
  const page = document.body.dataset.page;

  document.querySelectorAll("[data-nav]").forEach(link => {
    link.classList.toggle("active", link.dataset.nav === page);
  });

  setupMobileNavigation();
});

async function api(url, opt = {}) {
  const options = { ...opt, headers: { ...(opt.headers || {}) } };

  if (opt.body && !options.headers["Content-Type"]) {
    options.headers["Content-Type"] = "application/json";
  }

  let response;

  try {
    response = await fetch(url, options);
  } catch (error) {
    throw new Error("Unable to connect to the server. Please make sure Spring Boot is running.");
  }

  const text = await response.text();

  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }

  if (!response.ok) {
    throw new Error(
      data?.message ||
      data?.error ||
      "Request failed. Please try again."
    );
  }

  return data;
}

function esc(value) {
  return String(value ?? "").replace(
    /[&<>"']/g,
    char => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#039;"
    }[char])
  );
}

function initials(value) {
  return String(value || "?")
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map(x => x[0].toUpperCase())
    .join("");
}

function date(value) {
  return value
    ? new Date(value + "T00:00:00").toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric"
      })
    : "—";
}

function todayISO() {
  const now = new Date();
  const offset = now.getTimezoneOffset();
  return new Date(now.getTime() - offset * 60000)
    .toISOString()
    .slice(0, 10);
}

function alertBox(message, type = "success") {
  const element = document.getElementById("alert");
  if (!element) return;

  element.innerHTML = `<div class="alert ${type}" role="alert">${esc(message)}</div>`;

  window.clearTimeout(element._timer);
  element._timer = window.setTimeout(() => {
    element.innerHTML = "";
  }, 5000);
}

function filterSelect(selectId, searchId) {
  const select = document.getElementById(selectId);
  const search = document.getElementById(searchId);

  if (!select || !search) return;

  search.addEventListener("input", () => {
    const query = search.value.trim().toLowerCase();

    Array.from(select.options).forEach(option => {
      if (!option.value) {
        option.hidden = false;
        return;
      }

      option.hidden = query !== "" &&
        !option.textContent.toLowerCase().includes(query);
    });

    const selected = select.options[select.selectedIndex];
    if (selected && selected.hidden) {
      select.value = "";
    }
  });

  select.addEventListener("change", () => {
    search.value = "";
    Array.from(select.options).forEach(option => {
      option.hidden = false;
    });
  });
}

function resetSelectSearch(searchId) {
  const search = document.getElementById(searchId);
  if (!search) return;

  search.value = "";
  search.dispatchEvent(new Event("input"));
}

function setupMobileNavigation() {
  const button = document.getElementById("menuToggle");
  const backdrop = document.getElementById("mobileBackdrop");

  if (button) {
    button.addEventListener("click", () => {
      document.body.classList.contains("menu-open")
        ? closeMobileMenu()
        : openMobileMenu();
    });
  }

  if (backdrop) {
    backdrop.addEventListener("click", closeMobileMenu);
  }

  document.querySelectorAll(".nav a").forEach(link => {
    link.addEventListener("click", closeMobileMenu);
  });

  document.addEventListener("keydown", event => {
    if (event.key === "Escape") {
      closeMobileMenu();
    }
  });
}

function closeMobileMenu() {
  document.body.classList.remove("menu-open");

  const button = document.getElementById("menuToggle");
  if (button) {
    button.setAttribute("aria-expanded", "false");
    button.setAttribute("aria-label", "Open navigation");
  }
}

function openMobileMenu() {
  document.body.classList.add("menu-open");

  const button = document.getElementById("menuToggle");
  if (button) {
    button.setAttribute("aria-expanded", "true");
    button.setAttribute("aria-label", "Close navigation");
  }
}
