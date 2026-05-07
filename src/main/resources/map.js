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
const map = L.map('map').setView([-28.4793, 24.6727], 6);

// OpenStreetMap tiles — free, no API key ever needed
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    maxZoom: 18
}).addTo(map);

// -----------------------------------------------------------------------
// 3. Layer group — cleared and redrawn when filters change
// -----------------------------------------------------------------------
const markerLayer = L.layerGroup().addTo(map);

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
