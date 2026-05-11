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
    CRIME: '#e74c3c', // Red
    ASSAULT: '#8e44ad', // Purple/Dark Red
    THEFT: '#e67e22', // Orange
    SUSPICIOUS: '#f1c40f', // Gold
    OTHER: '#95a5a6'  // Grey
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
                if (!response.ok)
                    throw new Error('Network response was not ok');
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
// -----------------------------------------------------------------------
// 4. Function to create 3D GLOSSY PINS from data
// -----------------------------------------------------------------------
function renderTips(tips, filterProvince, filterCategory) {
    // Clear old pins before drawing new ones
    markerLayer.clearLayers();

    tips.forEach(function (tip) {
        // Apply Filters logic
        if (filterProvince && tip.province !== filterProvince)
            return;
        if (filterCategory && tip.category !== filterCategory)
            return;

        const colour = CATEGORY_COLOURS[tip.category] || 'grey';

        // We create a unique ID for the gradient based on the category
        const gradId = `grad-${tip.category}`;

        // ✅ UPGRADE: The 3D Programmatic Pushpin Code
        const svgPin = `
            <svg viewBox="0 0 40 65" width="32" height="52" xmlns="http://www.w3.org/2000/svg">
                <defs>
                    <radialGradient id="${gradId}" cx="30%" cy="30%" r="70%">
                        <stop offset="0%" stop-color="#ffffff" stop-opacity="0.9" />
                        <stop offset="25%" stop-color="${colour}" stop-opacity="1" />
                        <stop offset="85%" stop-color="${colour}" stop-opacity="1" />
                        <stop offset="100%" stop-color="#000000" stop-opacity="0.5" />
                    </radialGradient>
                    
                    <linearGradient id="needleGrad" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" stop-color="#888" />
                        <stop offset="50%" stop-color="#f8f8f8" />
                        <stop offset="100%" stop-color="#444" />
                    </linearGradient>
                </defs>
                
                <g style="filter: drop-shadow(4px 6px 3px rgba(0,0,0,0.4));">
                    <polygon points="20,60 18.5,30 21.5,30" fill="url(#needleGrad)" />
                    
                    <ellipse cx="20" cy="30" rx="7" ry="2.5" fill="${colour}" />
                    <ellipse cx="20" cy="30" rx="7" ry="2.5" fill="#000" opacity="0.3" /> 
                    
                    <polygon points="13,30 27,30 24,16 16,16" fill="${colour}" />
                    <polygon points="13,30 27,30 24,16 16,16" fill="#000" opacity="0.1" /> 

                    <ellipse cx="20" cy="16" rx="14" ry="4" fill="${colour}" />

                    <path d="M 6,16 A 14,14 0 1,1 34,16 Z" fill="url(#${gradId})" />
                </g>
            </svg>
        `;

        const customIcon = L.divIcon({
            className: 'custom-map-pin',
            html: svgPin,
            iconSize: [32, 52], // Size of the new pin
            iconAnchor: [16, 52], // The exact point of the needle that touches the map
            popupAnchor: [0, -45]      // Where the text box pops up
        });

        // Create the marker and bind the popup
        const marker = L.marker([tip.latitude, tip.longitude], {icon: customIcon});

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