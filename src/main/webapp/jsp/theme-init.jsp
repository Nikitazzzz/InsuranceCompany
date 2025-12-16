<script>
// Apply theme IMMEDIATELY before page render to prevent FOUC
(function() {
    'use strict';
    var theme = localStorage.getItem('insureflow-theme');
    if (!theme) {
        theme = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }
    document.documentElement.setAttribute('data-theme', theme);
    document.documentElement.classList.toggle('dark', theme === 'dark');
})();
</script>

