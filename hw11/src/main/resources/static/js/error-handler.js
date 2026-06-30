function show404Error(containerId, errorMessage) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = `
     <div style="display: flex; justify-content: center; align-items: center; min-height: 80vh;">
        <div style="text-align: center;">
            <div style="font-size: 120px; font-weight: bold; margin-bottom: 20px;">404</div>
            <h3 style="margin-bottom: 20px;">${errorMessage}</h3>
            <a class="btn-blue" onclick="goBack()">Go back</a>
        </div>
    `;
}

function goBack() {
    if (window.history.length > 1) {
        window.history.back();
    } else {
        window.location.href = '/';
    }
}