const state = {
  apiBase: localStorage.getItem("domus-api-base") || "http://localhost:8080",
  users: [],
  leads: [],
  accounts: [],
  properties: [],
  features: [],
  valuations: [],
  editing: {
    users: null,
    leads: null,
    accounts: null,
    properties: null,
    features: null,
    valuations: null
  }
};

const $ = (id) => document.getElementById(id);
const $$ = (selector) => document.querySelectorAll(selector);

const refs = {
  apiBase: $("api-base"),
  toast: $("toast"),
  statusDot: $("status-dot"),
  statusLabel: $("status-label"),
  topbarSection: $("topbar-section"),
  statsCards: {
    users: $("sc-users"),
    leads: $("sc-leads"),
    accounts: $("sc-accounts"),
    properties: $("sc-properties"),
    features: $("sc-features"),
    valuations: $("sc-valuations")
  },
  statsNav: {
    users: $("stat-users"),
    leads: $("stat-leads"),
    accounts: $("stat-accounts"),
    properties: $("stat-properties"),
    features: $("stat-features"),
    valuations: $("stat-valuations")
  },
  lists: {
    users: $("users-list"),
    leads: $("leads-list"),
    accounts: $("accounts-list"),
    properties: $("properties-list"),
    features: $("features-list"),
    valuations: $("valuations-list")
  },
  forms: {
    config: document.querySelector("#config-form"),
    users: document.querySelector("#users-form"),
    leads: document.querySelector("#leads-form"),
    accounts: document.querySelector("#accounts-form"),
    properties: document.querySelector("#properties-form"),
    features: document.querySelector("#features-form"),
    valuations: document.querySelector("#valuations-form")
  }, 
  selects: {
    accountLead: $("account-lead-select"),
    propertyOwnerLead: $("property-owner-lead-select"),
    propertyAgent: $("property-agent-select"),
    valuationProperty: $("valuation-property-select"),
    valuationAgent: $("valuation-agent-select")
  }
};

const EP = {
  users: "/api/usuarios",
  leads: "/api/leads",
  accounts: "/api/clientes",
  properties: "/api/propiedades",
  features: "/api/extras",
  valuations: "/api/valoraciones"
};

const SECTION_LABELS = {
  users: "Usuarios",
  leads: "Leads",
  accounts: "Clientes",
  properties: "Propiedades",
  features: "Extras",
  valuations: "Valoraciones"
};

refs.apiBase.value = state.apiBase;

bindTabs();
bindActions();
initializeForms();
hydrate();

function bindTabs() {
  $$(".tab-button").forEach((button) => {
    button.addEventListener("click", () => {
      $$(".tab-button").forEach((item) => item.classList.remove("active"));
      $$(".tab-panel").forEach((item) => item.classList.remove("active"));
      button.classList.add("active");
      document.querySelector(`[data-panel="${button.dataset.tab}"]`).classList.add("active");
      refs.topbarSection.textContent = SECTION_LABELS[button.dataset.tab] || button.dataset.tab;
    });
  });
}

function bindActions() {
  refs.forms.config.addEventListener("submit", async (event) => {
    event.preventDefault();
    state.apiBase = refs.apiBase.value.replace(/\/$/, "");
    localStorage.setItem("domus-api-base", state.apiBase);
    await hydrate();
  });

  $$("[data-refresh]").forEach((button) => {
    button.addEventListener("click", () => hydrate(button.dataset.refresh));
  });

  $$("[data-cancel]").forEach((button) => {
    button.addEventListener("click", () => resetFormMode(button.dataset.cancel));
  });

  refs.forms.users.addEventListener("submit", (event) =>
    handleSave("users", event, {
      nombre: event.target.name?.value?.trim() || "",
      email: event.target.email?.value?.trim() || "",
      password: event.target.password?.value?.trim() || "",
      role: event.target.role?.value || ""
    })
  );

  refs.forms.leads.addEventListener("submit", (event) =>
    handleSave("leads", event, {
      nombre: event.target.name?.value?.trim() || "",
      telefono: event.target.phone?.value?.trim() || "",
      email: event.target.email?.value?.trim() || "",
      tipo: event.target.type?.value || "",
      descripcion: event.target.description?.value?.trim() || ""
    })
  );

  refs.forms.accounts.addEventListener("submit", (event) => {
    const editingId = state.editing.accounts;
    const payload = editingId ? {
      nombre: event.target.name?.value?.trim() || "",
      dni: event.target.dni?.value?.trim() || "",
      telefono: event.target.phone?.value?.trim() || "",
      email: event.target.email?.value?.trim() || "",
      tipo: event.target.type?.value || "",
      direccion: event.target.address?.value?.trim() || "",
      descripcion: event.target.description?.value?.trim() || ""
    } : {
      leadId: numberOrNull(event.target.leadId.value),
      dni: event.target.dni?.value?.trim() || "",
      direccion: event.target.address?.value?.trim() || ""
    };

    handleSave("accounts", event, payload);
  });

  refs.forms.properties.addEventListener("submit", (event) =>
    handleSave("properties", event, {
      precio: numberOrNull(event.target.price?.value),
      ciudad: event.target.city?.value?.trim() || "",
      direccion: event.target.address?.value?.trim() || "",
      codigoPostal: event.target.postalCode?.value?.trim() || "",
      habitaciones: numberOrNull(event.target.rooms?.value),
      banios: numberOrNull(event.target.bathrooms?.value),
      planta: numberOrNull(event.target.floor?.value),
      anioConstruccion: numberOrNull(event.target.constructionYear?.value),
      metrosConstruidos: numberOrNull(event.target.builtArea?.value),
      metrosUtiles: numberOrNull(event.target.usableArea?.value),
      propiedadTipo: event.target.propertyType?.value || "",
      operacionTipo: event.target.operationType?.value || "",
      estado: event.target.status?.value || "",
      provincia: event.target.province?.value?.trim() || "",
      exclusiva: booleanOrNull(event.target.exclusive?.value),
      propietarioLeadId: numberOrNull(event.target.ownerLeadId?.value),
      agenteAsignadoId: numberOrNull(event.target.assignedAgentId?.value),
      descripcion: event.target.description?.value?.trim() || "",
      extras: splitCsv(event.target.features?.value || "")
    })
  );

  refs.forms.valuations.addEventListener("submit", (event) =>
    handleSave("valuations", event, {
      tipo: event.target.type.value,
      valorEstimadoAutomatico: numberOrNull(event.target.estimatedAutoValue.value),
      valorRealEstimado: numberOrNull(event.target.estimatedRealValue.value),
      propiedadId: numberOrNull(event.target.propertyId.value),
      agenteId: numberOrNull(event.target.agentId.value)
    })
  );

  if (refs.forms.features) {
    refs.forms.features.addEventListener("submit", (event) =>
      handleSave("features", event, { nombre: event.target.name?.value?.trim() || "" })
    );
  }
}

function initializeForms() {
  Object.keys(refs.forms)
    .filter((entity) => entity !== "config")
    .forEach((entity) => setFormMode(entity, false));
}

async function hydrate(scope) {
  const entities = scope ? [scope] : Object.keys(EP);
  const errors = [];

  for (const entity of entities) {
    try {
      state[entity] = await apiFetch(EP[entity]);
    } catch (error) {
      errors.push(error.message);
    }
  }

  renderAll();

  if (errors.length) {
    setConnectionStatus(false, errors[0]);
    showToast(errors[0], true);
    return;
  }

  setConnectionStatus(true);
  showToast("Datos sincronizados");
}

async function handleSave(entity, event, payload) {
  event.preventDefault();

  try {
    const editingId = state.editing[entity];
    const path = editingId ? `${EP[entity]}/${editingId}` : EP[entity];
    const method = editingId ? "PUT" : "POST";

    await apiFetch(path, {
      method,
      body: JSON.stringify(cleanPayload(payload))
    });

    resetFormMode(entity);
    await hydrate();
    showToast(editingId ? "Registro actualizado" : "Registro creado");
  } catch (error) {
    showToast(error.message, true);
  }
}

async function handleDelete(entity, id) {
  if (!window.confirm("¿Eliminar este registro?")) return;

  try {
    await apiFetch(`${EP[entity]}/${id}`, { method: "DELETE" });
    if (state.editing[entity] === id) resetFormMode(entity);
    await hydrate();
    showToast("Registro eliminado");
  } catch (error) {
    showToast(error.message, true);
  }
}

async function apiFetch(path, options = {}) {
  const response = await fetch(`${state.apiBase}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options
  });

  if (!response.ok) throw new Error(await extractError(response));
  if (response.status === 204) return null;

  const text = await response.text();
  if (!text) return null;

  try {
    return JSON.parse(text);
  } catch {
    throw new Error(`Respuesta JSON invalida en ${path}`);
  }
}

async function extractError(response) {
  try {
    const data = await response.json();
    if (data.messages) {
      return Object.entries(data.messages)
        .map(([field, message]) => `${field}: ${message}`)
        .join(" | ");
    }
    return data.message || `${response.status} ${response.statusText}`;
  } catch {
    return `${response.status} ${response.statusText}`;
  }
}

function renderAll() {
  renderStats();
  renderLists();
  fillSelect(refs.selects.accountLead, state.leads, "Selecciona un lead", (item) => ({ value: item.id, label: `${item.nombre} | ${item.tipo}` }));
  fillSelect(refs.selects.propertyOwnerLead, state.leads, "Selecciona un lead propietario", (item) => ({ value: item.id, label: `${item.nombre} | ${item.tipo}` }));
  fillSelect(refs.selects.propertyAgent, state.users, "Selecciona un agente", (item) => ({ value: item.id, label: `${item.nombre} | ${item.role}` }));
  fillSelect(refs.selects.valuationProperty, state.properties, "Selecciona una propiedad", (item) => ({ value: item.id, label: `${item.direccion || item.ciudad || `Propiedad ${item.id}`} | ${formatCurrency(item.precio)}` }));
  fillSelect(refs.selects.valuationAgent, state.users, "Sin agente", (item) => ({ value: item.id, label: `${item.nombre} | ${item.role}` }));
  reapplyEditingSelections();
  bindListActions();
}

function renderStats() {
  Object.entries(refs.statsCards).forEach(([key, node]) => {
    if (node) node.textContent = state[key].length;
  });
  Object.entries(refs.statsNav).forEach(([key, node]) => {
    if (node) node.textContent = state[key].length;
  });
}

function renderLists() {
  renderCollection(refs.lists.users, state.users, "users", userCard);
  renderCollection(refs.lists.leads, state.leads, "leads", leadCard);
  renderCollection(refs.lists.accounts, state.accounts, "accounts", accountCard);
  renderCollection(refs.lists.properties, state.properties, "properties", propertyCard);
  if (refs.lists.features) renderCollection(refs.lists.features, state.features, "features", featureCard);
  renderCollection(refs.lists.valuations, state.valuations, "valuations", valuationCard);
}

function bindListActions() {
  $$("[data-edit]").forEach((button) => {
    button.onclick = () => startEdit(button.dataset.edit, Number(button.dataset.id));
  });
  $$("[data-delete]").forEach((button) => {
    button.onclick = () => handleDelete(button.dataset.delete, Number(button.dataset.id));
  });
}

function startEdit(entity, id) {
  state.editing[entity] = id;
  const item = state[entity].find((entry) => entry.id === id);
  if (!item) return;

  const form = refs.forms[entity];
  setFormMode(entity, true);

  if (entity === "users") {
    form.name.value = item.nombre || "";
    form.email.value = item.email || "";
    form.password.value = "";
    form.role.value = item.role || "";
  }

  if (entity === "leads") {
    form.name.value = item.nombre || "";
    form.phone.value = item.telefono || "";
    form.email.value = item.email || "";
    form.type.value = item.tipo || "";
    if (form.status) form.status.value = item.estado || "";
    form.description.value = item.descripcion || "";
  }

  if (entity === "accounts") {
    form.leadId.value = item.leadId || "";
    form.name.value = item.nombre || "";
    form.dni.value = item.dni || "";
    form.phone.value = item.telefono || "";
    form.email.value = item.email || "";
    form.type.value = item.tipo || "";
    if (form.address) form.address.value = item.direccion || "";
    if (form.description) form.description.value = item.descripcion || "";
  }

  if (entity === "properties") {
    form.price.value = item.precio ?? "";
    form.city.value = item.ciudad || "";
    form.address.value = item.direccion || "";
    form.postalCode.value = item.codigoPostal || "";
    form.rooms.value = item.habitaciones ?? "";
    form.bathrooms.value = item.banios ?? "";
    if (form.floor) form.floor.value = item.planta ?? "";
    if (form.constructionYear) form.constructionYear.value = item.anioConstruccion ?? "";
    form.builtArea.value = item.metrosConstruidos ?? "";
    form.usableArea.value = item.metrosUtiles ?? "";
    form.propertyType.value = item.propiedadTipo || "";
    form.operationType.value = item.operacionTipo || "";
    form.status.value = item.estado || "";
    if (form.province) form.province.value = item.provincia || "";
    if (form.exclusive) form.exclusive.value = item.exclusiva === true ? "true" : item.exclusiva === false ? "false" : "";
    if (form.ownerLeadId) form.ownerLeadId.value = item.leadPropietario?.id || "";
    form.assignedAgentId.value = item.asignadoAgente?.id || "";
    form.description.value = item.descripcion || "";
    form.features.value = (item.extras || []).join(", ");
  }

  if (entity === "valuations") {
    form.type.value = item.tipo || "";
    form.estimatedAutoValue.value = item.valorEstimadoAutomatico ?? "";
    form.estimatedRealValue.value = item.valorRealEstimado ?? "";
    form.propertyId.value = item.propiedadId || "";
    form.agentId.value = item.agenteId || "";
  }

  if (entity === "features" && form) {
    form.name.value = item.nombre || "";
    if (form.description) form.description.value = "";
  }

  form.scrollIntoView({ behavior: "smooth", block: "start" });
}

function resetFormMode(entity) {
  state.editing[entity] = null;
  refs.forms[entity].reset();
  setFormMode(entity, false);
}

function setFormMode(entity, editing) {
  const labels = {
    users: "Crear usuario",
    leads: "Crear lead",
    accounts: "Crear cliente",
    properties: "Crear propiedad",
    features: "Crear extra",
    valuations: "Crear valoracion"
  };

  const form = refs.forms[entity];
  if (!form) return;
  form.querySelector('button[type="submit"]').textContent = editing ? "Guardar cambios" : labels[entity];
  form.querySelector(`[data-cancel="${entity}"]`).classList.toggle("hidden", !editing);

  if (entity === "accounts") {
    form.querySelectorAll(".account-edit-only").forEach((node) => node.classList.toggle("hidden", !editing));
    form.querySelectorAll(".account-create-only").forEach((node) => node.classList.toggle("hidden", editing));
    if (form.leadId) form.leadId.required = !editing;
    if (form.name) form.name.required = editing;
    if (form.phone) form.phone.required = editing;
    if (form.email) form.email.required = false;
    if (form.type) form.type.required = editing;
  }
}

function reapplyEditingSelections() {
  Object.entries(state.editing).forEach(([entity, id]) => {
    if (!id) return;
    const item = state[entity].find((entry) => entry.id === id);
    if (!item) {
      resetFormMode(entity);
      return;
    }
    startEdit(entity, id);
  });
}

function renderCollection(container, items, entity, renderer) {
  if (!items.length) {
    container.innerHTML = `
      <div class="empty">
        <div class="empty-ico"></div>
        <span>No hay registros todavía.</span>
      </div>
    `;
    return;
  }

  container.innerHTML = items
    .slice()
    .reverse()
    .map((item) => renderer(item, entity))
    .join("");
}

function userCard(item, entity) {
  return card(item.nombre, [item.email, item.role].filter(Boolean).join(" | "), [item.activo === false ? "Inactivo" : "Activo"], item.id, entity);
}

function leadCard(item, entity) {
  return card(item.nombre, [item.telefono, item.email].filter(Boolean).join(" | "), [item.tipo, item.estado], item.id, entity);
}

function accountCard(item, entity) {
  return card(item.nombre, [item.dni, item.telefono, item.email].filter(Boolean).join(" | "), [item.tipo], item.id, entity);
}

function propertyCard(item, entity) {
  return card(
    item.direccion || item.ciudad || `Propiedad #${item.id}`,
    [formatCurrency(item.precio), item.ciudad, item.provincia, item.propietario?.nombre].filter(Boolean).join(" | "),
    [item.operacionTipo, item.estado, ...(item.extras || [])],
    item.id,
    entity
  );
}

function featureCard(item, entity) {
  return card(item.nombre, "Extra inmobiliario", ["EXTRA"], item.id, entity);
}

function valuationCard(item, entity) {
  return card(
    `Valoracion #${item.id}`,
    [item.nombreAgente || "Sin agente", item.propiedadId ? `Propiedad ${item.propiedadId}` : null].filter(Boolean).join(" | "),
    [item.tipo, item.valorRealEstimado ? formatCurrency(item.valorRealEstimado) : null, item.valorEstimadoAutomatico ? formatCurrency(item.valorEstimadoAutomatico) : null],
    item.id,
    entity
  );
}

function card(title, description, tags, id, entity) {
  const initials = String(title)
    .split(" ")
    .slice(0, 2)
    .map((chunk) => chunk[0] || "")
    .join("")
    .toUpperCase() || "#";

  return `
    <article class="icard">
      <div class="iavatar">${escapeHtml(initials)}</div>
      <div class="imain">
        <h3>${escapeHtml(title)}</h3>
        <p>${escapeHtml(description || "Sin detalle")}</p>
      </div>
      <div class="imeta">
        ${tags.filter(Boolean).map((tag) => `<span class="pill">${escapeHtml(String(tag))}</span>`).join("")}
      </div>
      <div class="iactions">
        <button type="button" class="ghost" data-edit="${entity}" data-id="${id}">Editar</button>
        <button type="button" class="btn-del" data-delete="${entity}" data-id="${id}">Borrar</button>
      </div>
    </article>
  `;
}

function fillSelect(select, items, placeholder, mapper) {
  const current = select.value;
  select.innerHTML = [
    `<option value="">${placeholder}</option>`,
    ...items.map((item) => {
      const mapped = mapper(item);
      return `<option value="${mapped.value}">${escapeHtml(mapped.label)}</option>`;
    })
  ].join("");

  if ([...select.options].some((option) => option.value === current)) {
    select.value = current;
  }
}

function collectFields(form, keys) {
  return keys.reduce((accumulator, key) => {
    accumulator[key] = form[key]?.value?.trim?.() || "";
    return accumulator;
  }, {});
}

function cleanPayload(payload) {
  return Object.fromEntries(
    Object.entries(payload).filter(([, value]) => {
      if (Array.isArray(value)) return value.length > 0;
      return value !== "" && value !== null && value !== undefined;
    })
  );
}

function splitCsv(value) {
  return value.split(",").map((item) => item.trim()).filter(Boolean);
}

function numberOrNull(value) {
  if (value === "" || value === null || value === undefined) return null;
  const parsed = Number(value);
  return Number.isNaN(parsed) ? null : parsed;
}

function booleanOrNull(value) {
  if (value === "" || value === null || value === undefined) return null;
  if (value === "true") return true;
  if (value === "false") return false;
  return null;
}

function formatCurrency(value) {
  return new Intl.NumberFormat("es-ES", {
    style: "currency",
    currency: "EUR",
    maximumFractionDigits: 0
  }).format(value || 0);
}

function setConnectionStatus(connected, message = "") {
  if (refs.statusDot) refs.statusDot.className = connected ? "dot ok" : "dot err";
  if (refs.statusLabel) refs.statusLabel.textContent = connected ? "Conectado" : message || "Sin conexion";
}

function showToast(message, isError = false) {
  refs.toast.textContent = message;
  refs.toast.classList.toggle("err", isError);
  refs.toast.classList.add("on");
  clearTimeout(showToast.timer);
  showToast.timer = setTimeout(() => refs.toast.classList.remove("on"), 2600);
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}
