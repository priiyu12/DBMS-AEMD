// script.js
console.log("script loaded");

// URL prefix for backend (change if needed)
const BACKEND_BASE = "http://localhost:8080/aemd-backend";

// UI elements
const splash = document.getElementById("splash");
const app = document.getElementById("app");
const loginBtn = document.getElementById("login-btn");
const usernameInput = document.getElementById("username-input");
const userGreeting = document.getElementById("user-greeting");
const tableContainer = document.getElementById("table-container");
const formContainer = document.getElementById("form-container");

let currentUser = "";
let currentTable = "";

/* ---------- LOGIN ---------- */
loginBtn.addEventListener("click", () => doLogin());

function doLogin(){
  const name = usernameInput.value.trim();
  if(!name){
    alert("Please enter your name");
    return;
  }

  fetch(`${BACKEND_BASE}/api?name=${encodeURIComponent(name)}`)
    .then(r => {
      if(!r.ok) throw new Error("Server error");
      return r.json();
    })
    .then(json => {
      if(json.error){
        alert("Login error: " + json.error);
        return;
      }
      currentUser = name;
      localStorage.setItem("aemdUser", name);
      userGreeting.textContent = `Hello, ${name}`;
      splash.classList.add("hidden");
      app.classList.remove("hidden");
    })
    .catch(err => {
      console.error("Login fetch error:", err);
      alert("Login failed. Backend not responding.");
    });
}

// auto restore (disabled on reload)
document.addEventListener("DOMContentLoaded", () => {
  localStorage.removeItem("aemdUser");
  splash.classList.remove("hidden");
  app.classList.add("hidden");
});

/* ---------- LOAD TABLES ---------- */
function loadTable(tableName){
  currentTable = tableName;
  tableContainer.innerHTML = "<p>Loading...</p>";
  fetch(`${BACKEND_BASE}/api/query?table=${encodeURIComponent(tableName)}`)
    .then(r => r.json())
    .then(data => {
      renderTable(data, tableName);
    })
    .catch(err => {
      console.error("Fetch table error:", err);
      tableContainer.innerHTML = `<p style="color:#c00">Error loading ${tableName}: ${err.message}</p>`;
    });
}

function renderTable(data, tableName){
  if (!data || !data.top10 || !data.bottom10) {
    tableContainer.innerHTML = `<p>No data returned for <strong>${tableName}</strong></p>`;
    return;
  }

  let html = `<div class="table-card"><h4>${tableName.toUpperCase()} — Top 10</h4>${buildDataTable("topTable", data.top10)}</div>`;
  html += `<div class="table-card"><h4>${tableName.toUpperCase()} — Last 10</h4>${buildDataTable("bottomTable", data.bottom10)}</div>`;

  tableContainer.innerHTML = html;

  // Init DataTables
  $("#topTable").DataTable({ paging:true, searching:true, ordering:true, pageLength:10 });
  $("#bottomTable").DataTable({ paging:true, searching:true, ordering:true, pageLength:10 });
}

function buildDataTable(id, rows) {
  if (!rows || rows.length === 0) return `<p>No rows</p>`;
  const cols = Object.keys(rows[0]);
  let header = cols.map(c => `<th>${c}</th>`).join("");
  let body = rows.map(r => "<tr>" + cols.map(c => `<td>${escapeHtml(r[c])}</td>`).join("") + "</tr>").join("");
  return `<table id="${id}" class="display" style="width:100%"><thead><tr>${header}</tr></thead><tbody>${body}</tbody></table>`;
}

/* ---------- CRUD Forms ---------- */
function showForm(type){
  formContainer.classList.remove("hidden");
  let html = "";
  if(type === "create"){ // CREATE -> insert visitor
    html += `<h4>Create Visitor</h4>
      <input id="vis-tickettype" placeholder="Ticket type (e.g. VIP, One-day)" />
      <input id="vis-visitdate" type="date" placeholder="Visit date (YYYY-MM-DD)" />
      <textarea id="vis-feedback" placeholder="Feedback"></textarea>
      <input id="vis-feedbackratings" type="number" min="1" max="5" placeholder="Rating (1-5)" />
      <div style="margin-top:8px">
        <button onclick="performOperation('insertVisitor')" class="create-btn">Submit</button>
        <button onclick="cancelForm()">Cancel</button>
      </div>`;
  } else if(type === "insert"){ // INSERT -> insert artwork
    html += `<h4>Insert Artwork</h4>
      <input id="art-title" placeholder="Title (required)" />
      <input id="art-medium" placeholder="Medium" />
      <input id="art-dimensions" placeholder="Dimensions (e.g. 24x36)" />
      <input id="art-year" placeholder="Year Created (YYYY)" />
      <input id="art-imageurl" placeholder="Image URL" />
      <textarea id="art-description" placeholder="Description"></textarea>
      <input id="art-studentid" placeholder="Student ID (required)" />
      <label><input id="art-isforsale" type="checkbox" /> For sale</label>
      <div style="margin-top:8px">
        <button onclick="performOperation('insertArtwork')" class="insert-btn">Submit</button>
        <button onclick="cancelForm()">Cancel</button>
      </div>`;
  } else if(type === "update"){
    html += `<h4>UPDATE Notification</h4>
      <input id="notif-id" placeholder="Notification ID (required)" />
      <select id="notif-status"><option value="Unread">Unread</option><option value="Read">Read</option></select>
      <div style="margin-top:8px">
        <button onclick="performOperation('updateNotification')" class="update-btn">Update</button>
        <button onclick="cancelForm()">Cancel</button>
      </div>`;
  } else if(type === "delete"){
    html += `<h4>DELETE Notification</h4>
      <input id="notif-id-delete" placeholder="Notification ID (required)" />
      <div style="margin-top:8px">
        <button onclick="performOperation('deleteNotification')" class="delete-btn">Delete</button>
        <button onclick="cancelForm()">Cancel</button>
      </div>`;
  }
  formContainer.innerHTML = html;
}

function cancelForm(){
  formContainer.classList.add("hidden");
  formContainer.innerHTML = "";
}

/* ---------- PERFORM OPERATION ---------- */
function performOperation(action){
  const params = new URLSearchParams();
  params.append("action", action);

  if(action === "insertArtwork"){
    const title = document.getElementById("art-title").value.trim();
    const medium = document.getElementById("art-medium").value.trim();
    const dimensions = document.getElementById("art-dimensions").value.trim();
    const yearcreated = document.getElementById("art-year").value.trim();
    const imageurl = document.getElementById("art-imageurl").value.trim();
    const description = document.getElementById("art-description").value.trim();
    const studentid = document.getElementById("art-studentid").value.trim();
    const isforsale = document.getElementById("art-isforsale").checked ? "on" : "off";

    console.log("INSERT ART debug:", {title, medium, dimensions, yearcreated, imageurl, studentid, isforsale});

    if(!title || !studentid){ alert("Please provide title and student id"); return; }
    params.append("title", title);
    params.append("medium", medium);
    params.append("dimensions", dimensions);
    params.append("yearcreated", yearcreated);
    params.append("imageurl", imageurl);
    params.append("description", description);
    params.append("studentid", studentid);
    params.append("isforsale", isforsale);

  } else if(action === "insertVisitor"){
    const tickettype = document.getElementById("vis-tickettype").value.trim();
    const visitdate = document.getElementById("vis-visitdate").value;
    const feedback = document.getElementById("vis-feedback").value.trim();
    const ratings = document.getElementById("vis-feedbackratings").value.trim();

    console.log("INSERT VISITOR debug:", {tickettype, visitdate, feedback, ratings});

    if(!tickettype){ alert("Ticket type required"); return; }
    params.append("tickettype", tickettype);
    params.append("visitdate", visitdate);
    params.append("feedback", feedback);
    params.append("feedbackratings", ratings);

  } else if(action === "updateNotification"){
    const id = document.getElementById("notif-id").value.trim();
    const status = document.getElementById("notif-status").value;
    if(!id){ alert("Provide notification id"); return; }
    params.append("id", id);
    params.append("status", status);

  } else if(action === "deleteNotification"){
    const id = document.getElementById("notif-id-delete").value.trim();
    if(!id){ alert("Provide notification id"); return; }
    params.append("id", id);
  }

  console.log("Sending POST:", params.toString());

  fetch(`${BACKEND_BASE}/api`, {
    method: "POST",
    headers: {'Content-Type':'application/x-www-form-urlencoded'},
    body: params.toString()
  })
  .then(r => r.json())
  .then(json => {
    console.log("Server returned:", json);
    if(json && typeof json === 'object'){
      if(json.success){
        alert("Success: " + (json.message || "Operation completed"));
      } else {
        alert("Failed: " + (json.message || "Operation failed"));
      }
    } else {
      alert("Unexpected server response");
    }
    if(json && json.success){
      cancelForm();
      if(currentTable) loadTable(currentTable);
    }
  })
  .catch(err => {
    console.error("Perform op error:", err);
    alert("Operation failed: " + err.message);
  });
}

/* ---------- small helpers ---------- */
function escapeHtml(v){
  if(v === null || v === undefined) return "";
  return String(v)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
}
