const API = "http://localhost:8080/api";

document.querySelectorAll(".nav-btn").forEach(btn => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".nav-btn").forEach(b => b.classList.remove("active"));
    document.querySelectorAll(".page").forEach(p => p.classList.remove("active"));
    btn.classList.add("active");
    document.getElementById(btn.dataset.page).classList.add("active");
  });
});

async function api(path, options = {}) {
  const res = await fetch(API + path, options);
  if (!res.ok) throw new Error("Backend returned " + res.status);
  return res.json();
}

function timeLabel(h) {
  if (h < 0) return "-";
  return `${String(h).padStart(2, "0")}:00`;
}

async function checkBackend() {
  try {
    await api("/dashboard");
    document.getElementById("backendStatus").textContent = "Backend Online";
    document.getElementById("backendStatus").style.background = "#166534";
  } catch (e) {
    document.getElementById("backendStatus").textContent = "Backend Offline";
  }
}

async function loadDashboard() {
  const d = await api("/dashboard");
  document.getElementById("patientsCount").textContent = d.patients;
  document.getElementById("resourcesCount").textContent = d.resources;
  document.getElementById("scheduledCount").textContent = d.scheduled;
  document.getElementById("waitingCount").textContent = d.waiting;
  document.getElementById("utilisation").textContent = d.utilization.toFixed(1) + "%";
  document.getElementById("avgWaiting").textContent = d.averageWaiting.toFixed(1) + "h";
  document.getElementById("makespan").textContent = timeLabel(d.makespan);

  document.getElementById("reportUtil").textContent = d.utilization.toFixed(1) + "%";
  document.getElementById("reportWait").textContent = d.averageWaiting.toFixed(1) + "h";
  document.getElementById("reportMake").textContent = timeLabel(d.makespan);
}

async function loadPatients() {
  const data = await api("/patients");
  document.getElementById("patientsTable").innerHTML = data.map(p => `
    <tr>
      <td>${p.id}</td><td>${escapeHtml(p.name)}</td><td>${p.priority}</td>
      <td>${p.duration}h</td><td>${escapeHtml(p.type)}</td>
      <td>${escapeHtml(p.doctor)}</td><td>${p.deadline}</td>
    </tr>
  `).join("");
}

async function loadResources() {
  const data = await api("/resources");
  document.getElementById("resourcesTable").innerHTML = data.map(r => `
    <tr>
      <td>${r.id}</td><td>${escapeHtml(r.name)}</td><td>${escapeHtml(r.type)}</td>
      <td>${escapeHtml(r.doctor)}</td><td>${r.capacity}</td>
      <td>${timeLabel(r.availableFrom)} - ${timeLabel(r.availableTo)}</td>
      <td><span class="badge ${r.available ? "ok" : "wait"}">${r.available ? "Available" : "Busy"}</span></td>
    </tr>
  `).join("");
}

async function loadSchedule() {
  const data = await api("/schedule");
  document.getElementById("scheduleTable").innerHTML = data.map(s => `
    <tr>
      <td>${escapeHtml(s.patientName)}</td><td>${s.priority}</td>
      <td>${escapeHtml(s.resourceName)}</td><td>${escapeHtml(s.resourceType)}</td>
      <td>${escapeHtml(s.doctor)}</td>
      <td>${timeLabel(s.start)} - ${timeLabel(s.end)}</td>
      <td><span class="badge ${s.status === "SCHEDULED" ? "ok" : "wait"}">${s.status}</span></td>
    </tr>
  `).join("");
}

async function refreshAll() {
  try {
    await Promise.all([loadDashboard(), loadPatients(), loadResources(), loadSchedule()]);
    document.getElementById("backendStatus").textContent = "Backend Online";
    document.getElementById("backendStatus").style.background = "#166534";
  } catch (e) {
    document.getElementById("backendStatus").textContent = "Backend Offline";
    console.error(e);
  }
}

async function generateSchedule() {
  try {
    const result = await api("/generate", { method: "POST" });
    alert(`Schedule generated.\nAssigned: ${result.assigned}/${result.total}\nUtilisation: ${result.utilization.toFixed(1)}%\nAverage waiting: ${result.averageWaiting.toFixed(1)}h`);
    await refreshAll();
    document.querySelector('[data-page="schedule"]').click();
  } catch (e) {
    alert("Cannot generate schedule. Start the Java backend first.");
  }
}

async function resetData() {
  if (!confirm("Reset all demo data?")) return;
  await api("/reset", { method: "POST" });
  await refreshAll();
}

document.getElementById("patientForm").addEventListener("submit", async e => {
  e.preventDefault();
  const form = new FormData(e.target);
  const body = new URLSearchParams(form).toString();

  try {
    await api("/patients", {
      method: "POST",
      headers: {"Content-Type":"application/x-www-form-urlencoded"},
      body
    });
    e.target.reset();
    await refreshAll();
    alert("Patient added successfully.");
  } catch (err) {
    alert("Could not add patient.");
  }
});

function escapeHtml(value) {
  return String(value)
    .replaceAll("&","&amp;")
    .replaceAll("<","&lt;")
    .replaceAll(">","&gt;")
    .replaceAll('"',"&quot;")
    .replaceAll("'","&#039;");
}

checkBackend().then(refreshAll);
