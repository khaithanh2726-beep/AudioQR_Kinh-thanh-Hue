import test from 'node:test'
import assert from 'node:assert/strict'
import { basemapConfig, OSM_TILE_URL } from '../src/map/config.js'

test('normal builds use the standard OSM background without enabling proposal regions', () => {
  assert.deepEqual(basemapConfig({ VITE_MAP_PREVIEW: 'false' }), { mode: 'osm', url: OSM_TILE_URL })
})

test('offline review is explicit and does not require a remote tile URL', () => {
  assert.deepEqual(basemapConfig({ VITE_MAP_BASEMAP: 'offline', VITE_OSM_TILE_URL: 'not-a-url' }), { mode: 'offline' })
})

test('invalid modes cannot silently switch to an external map provider', () => {
  assert.throws(() => basemapConfig({ VITE_MAP_BASEMAP: 'other' }), /INVALID_BASEMAP_MODE/)
})

test('tile configuration rejects insecure URLs, embedded secrets and incomplete coordinates', () => {
  for (const url of ['http://tile.openstreetmap.org/{z}/{x}/{y}.png', 'https://key:secret@example.com/{z}/{x}/{y}.png', 'https://example.com/{z}/{x}.png', 'https://example.com/{z}/{x}/{y}.png?key=secret', 'https://example.com/{z}/{x}/{y}.png#fragment']) {
    assert.throws(() => basemapConfig({ VITE_OSM_TILE_URL: url }), /INVALID_TILE_URL/)
  }
})

test('a configured HTTPS tile endpoint preserves the z/x/y template', () => {
  const url = 'https://maps.example.com/osm/{z}/{x}/{y}.png'
  assert.deepEqual(basemapConfig({ VITE_OSM_TILE_URL: url }), { mode: 'osm', url })
})
