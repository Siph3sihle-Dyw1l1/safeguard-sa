/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
// dashboard.js — Chart.js graphs for SafeGuard SA admin dashboard

// ── Doughnut: Tips by Category ──────────────────────────────────────────────
new Chart(document.getElementById('categoryChart'), {
    type: 'doughnut',
    data: {
        labels: categoryLabels,
        datasets: [{
            data: categoryData,
            backgroundColor: categoryColors,
            borderWidth: 2,
            borderColor: '#fff'
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: { position: 'bottom' },
            tooltip: {
                callbacks: {
                    label: ctx => ` ${ctx.label}: ${ctx.parsed} tips`
                }
            }
        }
    }
});

// ── Bar: Tips by Province ────────────────────────────────────────────────────
new Chart(document.getElementById('provinceChart'), {
    type: 'bar',
    data: {
        labels: provinceLabels,
        datasets: [{
            label: 'Approved Tips',
            data: provinceData,
            backgroundColor: '#3498db',
            borderRadius: 4
        }]
    },
    options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
            y: {
                beginAtZero: true,
                ticks: { stepSize: 1 }
            }
        }
    }
});

// ── Line: Monthly Trend ──────────────────────────────────────────────────────
new Chart(document.getElementById('monthlyChart'), {
    type: 'line',
    data: {
        labels: monthLabels,
        datasets: [
            {
                label: 'Safety Tips',
                data: tipMonthly,
                borderColor: '#e74c3c',
                backgroundColor: 'rgba(231,76,60,0.1)',
                tension: 0.3,
                fill: true,
                pointRadius: 4
            },
            {
                label: 'Chat Queries',
                data: chatMonthly,
                borderColor: '#3498db',
                backgroundColor: 'rgba(52,152,219,0.1)',
                tension: 0.3,
                fill: true,
                pointRadius: 4
            }
        ]
    },
    options: {
        responsive: true,
        interaction: { mode: 'index', intersect: false },
        plugins: {
            legend: { position: 'top' }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: { stepSize: 1 }
            }
        }
    }
});

