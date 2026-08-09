const root = document.documentElement;
const themeToggle = document.querySelector('#theme-toggle');
const simulator = document.querySelector('#simulator-dialog');
const openButtons = [
  document.querySelector('#open-simulator'),
  document.querySelector('#open-simulator-secondary'),
].filter(Boolean);
const closeButton = document.querySelector('#close-simulator');
const ticketForm = document.querySelector('#ticket-form');
const ticketPreview = document.querySelector('#ticket-preview');

function applyTheme(theme) {
  const isDark = theme === 'dark';
  root.dataset.theme = isDark ? 'dark' : 'light';
  themeToggle.setAttribute('aria-pressed', String(isDark));
  themeToggle.setAttribute('aria-label', isDark ? 'Activar modo claro' : 'Activar modo oscuro');
  themeToggle.querySelector('.button-label').textContent = isDark ? 'Modo claro' : 'Modo oscuro';
  localStorage.setItem('soporte-ti-theme', theme);
}

const savedTheme = localStorage.getItem('soporte-ti-theme');
applyTheme(savedTheme || 'light');

themeToggle.addEventListener('click', () => {
  applyTheme(root.dataset.theme === 'dark' ? 'light' : 'dark');
});

openButtons.forEach((button) => {
  button.addEventListener('click', () => {
    ticketPreview.textContent = '';
    ticketPreview.hidden = true;
    simulator.showModal();
  });
});

closeButton.addEventListener('click', () => simulator.close());

simulator.addEventListener('click', (event) => {
  if (event.target === simulator) simulator.close();
});

ticketForm.addEventListener('submit', (event) => {
  event.preventDefault();
  const formData = new FormData(ticketForm);
  const service = formData.get('service');
  const impact = formData.get('impact');
  const detail = formData.get('detail');
  ticketPreview.textContent = `Resumen listo: ${service}. Impacto ${impact.toLowerCase()}. ${detail}`;
  ticketPreview.hidden = false;
});
