/**
 * map.js — Member C
 * SafeGuard SA | Student Safety Map
 *
 * Initialises the Leaflet.js map, fetches approved tips from the backend,
 * renders colour-coded circle markers, and wires the filter controls.
 *
 * No API key required — Leaflet.js + OpenStreetMap are completely free.
 */

// -----------------------------------------------------------------------
// 1. Colour per threat category — matches TipCategory table (Member D)
// -----------------------------------------------------------------------
const CATEGORY_COLOURS = {
    CRIME:      'red',
    ASSAULT:    'darkred',
    THEFT:      'orange',
    SUSPICIOUS: 'gold',
    OTHER:      'grey'
};

// -----------------------------------------------------------------------
// 2. Initialise Leaflet map — centred on South Africa
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
// 2. Initialise Leaflet map — Restricted to South Africa
// -----------------------------------------------------------------------

// Define the geographical bounds for South Africa (South-West to North-East)
const saBounds = L.latLngBounds(
    L.latLng(-35.0, 16.0), // Bottom Left (Cape Agulhas area)
    L.latLng(-22.0, 33.0)  // Top Right (Kruger/Limpopo border)
);

const map = L.map('map', {
    center: [-28.4793, 24.6727],
    zoom: 6,
    minZoom: 5,               // Prevent zooming out to a world view
    maxBounds: saBounds,      // Lock the map view to SA
    maxBoundsViscosity: 1.0   // Makes the boundary solid (prevents "drifting")
});

// OpenStreetMap tiles — restricted to SA bounds
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    maxZoom: 18,
    bounds: saBounds 
}).addTo(map);

// -----------------------------------------------------------------------
// 4. Fetch approved tips from SafetyMapController (GET /map/tips)
// -----------------------------------------------------------------------
let allTips = [];

function fetchAndRenderTips() {
    fetch('/map/tips')
        .then(function(response) {
            if (!response.ok) throw new Error('Could not load tips.');
            return response.json();
        })
        .then(function(tips) {
            allTips = tips;
            renderTips(tips, '', '');
        })
        .catch(function(err) {
            console.error('[map.js] Error loading tips:', err);
        });
}

// -----------------------------------------------------------------------
// 5. Render markers — filters by province and/or category
// -----------------------------------------------------------------------
function renderTips(tips, filterProvince, filterCategory) {
    markerLayer.clearLayers();

    tips.forEach(function(tip) {
        if (filterProvince  && tip.province !== filterProvince)  return;
        if (filterCategory  && tip.category !== filterCategory)  return;

        var colour = CATEGORY_COLOURS[tip.category] || 'grey';

        var marker = L.circleMarker([tip.latitude, tip.longitude], {
            color:       colour,
            fillColor:   colour,
            fillOpacity: 0.75,
            radius:      8,
            weight:      2
        });

        // Popup: area, category, time, city/province — no personal info ever shown
        marker.bindPopup(
            '<b>' + tip.streetArea + '</b><br>' +
            tip.category + ' | ' + tip.timeOfDay + '<br>' +
            '📍 ' + tip.city + ', ' + tip.province + '<br>' +
            '<em>' + tip.description + '</em><br>' +
            '<small>Submitted: ' + tip.submittedAt + '</small>'
        );

        markerLayer.addLayer(marker);
    });
}

// -----------------------------------------------------------------------
// 6. Wire filter buttons
// -----------------------------------------------------------------------
document.addEventListener('DOMContentLoaded', function() {
    fetchAndRenderTips();

    var applyBtn = document.getElementById('applyFilters');
    var resetBtn = document.getElementById('resetFilters');

    if (applyBtn) {
        applyBtn.addEventListener('click', function() {
            var province = document.getElementById('filterProvince').value;
            var category = document.getElementById('filterCategory').value;
            renderTips(allTips, province, category);
        });
    }

    if (resetBtn) {
        resetBtn.addEventListener('click', function() {
            document.getElementById('filterProvince').value = '';
            document.getElementById('filterCategory').value = '';
            renderTips(allTips, '', '');
        });
    }
});
