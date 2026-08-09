const root = document.documentElement;
const themeToggle = document.querySelector('#theme-toggle');
const ticketList = document.querySelector('#ticket-list');
const emptyState = document.querySelector('#empty-state');
const ticketSearch = document.querySelector('#ticket-search');
const globalSearch = document.querySelector('#global-search');
const dialog = document.querySelector('#ticket-dialog');
const ticketForm = document.querySelector('#ticket-form');
const toast = document.querySelector('#toast');
const openTicketButton = document.querySelector('#open-ticket');
const closeTicketButton = document.querySelector('#close-ticket');
const cancelTicketButton = document.querySelector('#cancel-ticket');

const tickets = [
  { id: '#1048', title: 'No puedo ingresar a la plataforma de matrícula', requester: 'Sandra Milena Vargas', initials: 'SM', category: 'Acceso y cuentas', status: 'Nuevo', priority: 'Alta', updated: 'Hace 12 min', avatar: 'avatar-teal' },
  { id: '#1047', title: 'La impresora de recepción no responde', requester: 'Jorge Duarte', initials: 'JD', category: 'Equipos', status: 'En curso', priority: 'Media', updated: 'Hace 34 min', avatar: 'avatar-purple' },
  { id: '#1046', title: 'Solicitud de acceso a carpeta compartida', requester: 'Laura Pérez', initials: 'LP', category: 'Acceso y cuentas', status: 'En curso', priority: 'Baja', updated: 'Hace 1 h', avatar: 'avatar-orange' },
  { id: '#1045', title: 'Conexión intermitente en sala de capacitación', requester: 'Andrés Ríos', initials: 'AR', category: 'Red y conectividad', status: 'Resuelto', priority: 'Media', updated: 'Ayer', avatar: 'avatar-blue' },
  { id: '#1044', title: 'Actualización de aplicación contable', requester: 'Camila Torres', initials: 'CT', category: 'Aplicaciones', status: 'En curso', priority: 'Baja', updated: 'Ayer', avatar: 'avatar-teal' },
];

let activeFilter = 'all';

function renderMetrics() {
  const total = tickets.length;
  const open = tickets.filter((ticket) => ticket.status !== 'Resuelto').length;
  const pending = tickets.filter((ticket) => ticket.status === 'Nuevo' || ticket.status === 'En curso').length;
  const resolved = tickets.filter((ticket) => ticket.status === 'Resuelto').length;
  const highPriority = tickets.filter((ticket) => ticket.priority === 'Alta' && ticket.status !== 'Resuelto').length;
  const progress = total === 0 ? 0 : Math.round((resolved / total) * 100);

  document.querySelector('#attention-count').textContent = `${open} tickets`;
  document.querySelector('#open-count').textContent = String(open);
  document.querySelector('#pending-count').textContent = String(pending);
  document.querySelector('#high-priority-count').textContent = `${highPriority} de prioridad alta`;
  document.querySelector('#resolved-count').textContent = String(resolved);
  document.querySelector('#resolved-progress').style.width = `${progress}%`;
  document.querySelector('#total-count').textContent = String(total);
  document.querySelector('#nav-ticket-count').textContent = String(total);

  document.querySelector('.filter-tab[data-filter="all"] span').textContent = String(total);
  document.querySelector('.filter-tab[data-filter="Nuevo"] span').textContent = String(tickets.filter((ticket) => ticket.status === 'Nuevo').length);
  document.querySelector('.filter-tab[data-filter="En curso"] span').textContent = String(tickets.filter((ticket) => ticket.status === 'En curso').length);
  document.querySelector('.filter-tab[data-filter="Resuelto"] span').textContent = String(resolved);
}

function escapeHTML(value) {
  return String(value).replace(/[&<>'"]/g, (character) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#039;', '"': '&quot;' })[character]);
}

function statusClass(status) {
  return { Nuevo: 'status-new', 'En curso': 'status-progress', Resuelto: 'status-done' }[status] || 'status-new';
}

function priorityClass(priority) {
  return { Alta: 'priority-high', Media: 'priority-medium', Baja: 'priority-low' }[priority] || 'priority-low';
}

function renderTickets() {
  const query = ticketSearch.value.trim().toLowerCase();
  const visibleTickets = tickets.filter((ticket) => {
    const matchesFilter = activeFilter === 'all' || ticket.status === activeFilter;
    const searchable = `${ticket.id} ${ticket.title} ${ticket.requester} ${ticket.category}`.toLowerCase();
    return matchesFilter && searchable.includes(query);
  });

  ticketList.innerHTML = visibleTickets.map((ticket) => `
    <article class="ticket-row" data-ticket-id="${escapeHTML(ticket.id)}">
      <span class="avatar ${escapeHTML(ticket.avatar)}">${escapeHTML(ticket.initials)}</span>
      <div class="ticket-main"><strong>${escapeHTML(ticket.title)}</strong><small>${escapeHTML(ticket.id)} · ${escapeHTML(ticket.category)}</small></div>
      <span class="ticket-meta">${escapeHTML(ticket.updated)}</span>
      <span class="ticket-status ${statusClass(ticket.status)}">${escapeHTML(ticket.status)}</span>
      <span class="ticket-priority ${priorityClass(ticket.priority)}">${escapeHTML(ticket.priority)}</span>
    </article>
  `).join('');
  emptyState.hidden = visibleTickets.length > 0;
  renderMetrics();
}

function applyTheme(theme) {
  const isDark = theme === 'dark';
  root.dataset.theme = isDark ? 'dark' : 'light';
  themeToggle.setAttribute('aria-pressed', String(isDark));
  themeToggle.setAttribute('aria-label', isDark ? 'Activar modo claro' : 'Activar modo oscuro');
  localStorage.setItem('mesa-ti-theme', theme);
}

function showToast(message) {
  toast.textContent = message;
  toast.classList.add('show');
  window.setTimeout(() => toast.classList.remove('show'), 3200);
}

function openDialog() {
  dialog.showModal();
  document.querySelector('#requester').focus();
}

const savedTheme = localStorage.getItem('mesa-ti-theme');
applyTheme(savedTheme || 'light');
renderTickets();

themeToggle.addEventListener('click', () => applyTheme(root.dataset.theme === 'dark' ? 'light' : 'dark'));
ticketSearch.addEventListener('input', renderTickets);
globalSearch.addEventListener('input', () => {
  ticketSearch.value = globalSearch.value;
  renderTickets();
});

document.querySelectorAll('.filter-tab').forEach((button) => {
  button.addEventListener('click', () => {
    activeFilter = button.dataset.filter;
    document.querySelectorAll('.filter-tab').forEach((tab) => tab.classList.toggle('active', tab === button));
    renderTickets();
  });
});

openTicketButton.addEventListener('click', openDialog);
closeTicketButton.addEventListener('click', () => dialog.close());
cancelTicketButton.addEventListener('click', () => dialog.close());
dialog.addEventListener('click', (event) => { if (event.target === dialog) dialog.close(); });

ticketForm.addEventListener('submit', (event) => {
  event.preventDefault();
  const formData = new FormData(ticketForm);
  const detail = formData.get('detail').trim();
  const requester = formData.get('requester').trim();
  const initials = requester.split(/\s+/).map((part) => part[0]).join('').slice(0, 2).toUpperCase();
  tickets.unshift({
    id: `#${1050 + tickets.length}`,
    title: detail,
    requester,
    initials,
    category: formData.get('category'),
    status: 'Nuevo',
    priority: formData.get('priority'),
    updated: 'Ahora',
    avatar: 'avatar-teal',
  });
  activeFilter = 'all';
  document.querySelectorAll('.filter-tab').forEach((tab) => tab.classList.toggle('active', tab.dataset.filter === 'all'));
  ticketForm.reset();
  dialog.close();
  renderTickets();
  showToast('Ticket creado correctamente y añadido a la lista.');
});
