const themeToggle = document.querySelector('#theme-toggle');
const root = document.documentElement;

function applyTheme(theme) {
    const dark = theme === 'dark';
    root.dataset.theme = dark ? 'dark' : 'light';
    if (themeToggle) {
        themeToggle.setAttribute('aria-pressed', String(dark));
        themeToggle.querySelector('span').textContent = dark ? 'Modo claro' : 'Modo oscuro';
    }
    localStorage.setItem('mesa-ti-web-theme', theme);
}

applyTheme(localStorage.getItem('mesa-ti-web-theme') || 'light');

themeToggle?.addEventListener('click', () => {
    applyTheme(root.dataset.theme === 'dark' ? 'light' : 'dark');
});
