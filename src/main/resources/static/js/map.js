/**
 * map.js — Member C
 * SafeGuard SA | Student Safety Map
 *
 * Initialises the Leaflet.js map, fetches approved tips from the backend,
 * renders custom SVG map pins, and wires the filter controls.
 */

// -----------------------------------------------------------------------
// 1. Color mapping for threat categories
// -----------------------------------------------------------------------
const CATEGORY_COLOURS = {
    CRIME:      '#e74c3c', // Red
    ASSAULT:    '#8e44ad', // Purple/Dark Red
    THEFT:      '#e67e22', // Orange
    SUSPICIOUS: '#f1c40f', // Gold
    OTHER:      '#95a5a6'  // Grey
};

// -----------------------------------------------------------------------
// 2. Initialise Leaflet map — Restricted strictly to South Africa
// -----------------------------------------------------------------------
const saBounds = L.latLngBounds(
    L.latLng(-35.0, 16.0), // South-West
    L.latLng(-22.0, 33.0)  // North-East
);

const map = L.map('map', {
    center: [-28.4793, 24.6727],
    zoom: 6,
    minZoom: 5,
    maxBounds: saBounds,
    maxBoundsViscosity: 1.0
});

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors',
    bounds: saBounds
}).addTo(map);

// ✅ FIX 1: Initialise the Marker Layer Group
// This was missing and caused the "markerLayer is not defined" error.
const markerLayer = L.layerGroup().addTo(map);

// ✅ FIX 2: Global data storage for filters
let allTips = [];

// -----------------------------------------------------------------------
// 3. Fetch data from SafetyMapController (GET /map/tips)
// -----------------------------------------------------------------------
function fetchAndRenderTips() {
    fetch('/map/tips')
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(tips => {
            allTips = tips;
            renderTips(tips, '', ''); // Initial draw with no filters
        })
        .catch(err => console.error('[map.js] Error loading tips:', err));
}

// -----------------------------------------------------------------------
// 4. Function to create real PINS from data
// -----------------------------------------------------------------------
function renderTips(tips, filterProvince, filterCategory) {
    // Clear old pins before drawing new ones
    markerLayer.clearLayers();

    tips.forEach(function (tip) {
        // Apply Filters logic
        if (filterProvince && tip.province !== filterProvince) return;
        if (filterCategory && tip.category !== filterCategory) return;

        const colour = CATEGORY_COLOURS[tip.category] || 'grey';

        // ✅ UPGRADE: Create a custom SVG Map Pin instead of a dot
        const svgPin = `
            <svg viewBox="0 0 24 24" width="36" height="36" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z" 
                      fill="${colour}" stroke="#ffffff" stroke-width="1.5" style="filter: drop-shadow(0px 3px 2px rgba(0,0,0,0.4));"/>
            </svg>`;

        const customIcon = L.divIcon({
            className: 'custom-map-pin',
            html: svgPin,
            iconSize: [36, 36],
            iconAnchor: [18, 36],
            popupAnchor: [0, -32]
        });

        // Use L.marker for pins instead of circleMarker
        const marker = L.marker([tip.latitude, tip.longitude], { icon: customIcon });

        // Popup: no personal info ever shown
        marker.bindPopup(`
            <div style="font-family: Arial, sans-serif; padding: 5px;">
                <strong style="color:${colour}; font-size: 1.1rem;">${tip.category}</strong><br>
                <b style="color: #2c3e50;">${tip.streetArea}</b><br>
                <small style="color: #7f8c8d;">📍 ${tip.city}, ${tip.province}</small><br>
                <div style="margin-top: 8px; border-top: 1px solid #eee; padding-top: 5px; font-style: italic;">
                    "${tip.description}"
                </div>
                <div style="margin-top: 5px; font-size: 0.75rem; color: #bdc3c7;">
                    Time: ${tip.timeOfDay} | Submitted: ${tip.submittedAt}
                </div>
            </div>
        `);

        markerLayer.addLayer(marker);
    });
}

// -----------------------------------------------------------------------
// 5. Connect buttons on page load
// -----------------------------------------------------------------------
document.addEventListener('DOMContentLoaded', function () {
    fetchAndRenderTips();

    const applyBtn = document.getElementById('applyFilters');
    const resetBtn = document.getElementById('resetFilters');

    if (applyBtn) {
        applyBtn.addEventListener('click', function () {
            const province = document.getElementById('filterProvince').value;
            const category = document.getElementById('filterCategory').value;
            renderTips(allTips, province, category);
        });
    }

    if (resetBtn) {
        resetBtn.addEventListener('click', function () {
            document.getElementById('filterProvince').value = '';
            document.getElementById('filterCategory').value = '';
            renderTips(allTips, '', '');
        });
    }
});