// Theme Toggle - Simple and Reliable
(function() {
    'use strict';
    
    const THEME_KEY = 'insureflow-theme';
    const html = document.documentElement;
    
    // Get current theme
    function getCurrentTheme() {
        const saved = localStorage.getItem(THEME_KEY);
        if (saved === 'dark' || saved === 'light') {
            return saved;
        }
        // Default to system preference
        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }
    
    // Apply theme
    function applyTheme(theme) {
        html.setAttribute('data-theme', theme);
        html.classList.toggle('dark', theme === 'dark');
        localStorage.setItem(THEME_KEY, theme);
        
        // Update button text
        const themeSwitch = document.querySelector('[data-toggle-theme="switch"]');
        if (themeSwitch) {
            if (theme === 'dark') {
                themeSwitch.textContent = '☀️ Светлая';
            } else {
                themeSwitch.textContent = '🌙 Тёмная';
            }
        }
    }
    
    // Toggle theme
    function toggleTheme() {
        const current = html.getAttribute('data-theme') || getCurrentTheme();
        const newTheme = current === 'dark' ? 'light' : 'dark';
        applyTheme(newTheme);
    }
    
    // Helper function to update button text
    function updateButtonText(theme) {
        const themeSwitch = document.querySelector('[data-toggle-theme="switch"]');
        if (themeSwitch) {
            if (theme === 'dark') {
                themeSwitch.textContent = '☀️ Светлая';
            } else {
                themeSwitch.textContent = '🌙 Тёмная';
            }
        }
    }
    
    // Initialize
    function init() {
        // Theme is already applied by theme-init.jsp, just update button text
        const currentTheme = html.getAttribute('data-theme') || getCurrentTheme();
        updateButtonText(currentTheme);
        
        // Setup toggle button
        const themeToggle = document.querySelector('.theme-toggle');
        if (themeToggle) {
            themeToggle.addEventListener('click', toggleTheme);
        }
        
        // Listen for system theme changes (only if no manual preference)
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', function(e) {
            if (!localStorage.getItem(THEME_KEY)) {
                applyTheme(e.matches ? 'dark' : 'light');
            }
        });
    }
    
    // Run immediately if DOM is ready, otherwise wait
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        // DOM already ready, run immediately
        init();
    }
})();
