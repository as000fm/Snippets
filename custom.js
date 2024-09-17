document.addEventListener('DOMContentLoaded', function() {
    const theme = localStorage.getItem('theme');
    if (theme) {
        document.documentElement.setAttribute('data-md-color-scheme', theme);
    }
});
