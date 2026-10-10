const CACHE_NAME = "manhwa-app-v2";
const ASSETS = [
 './',
 './index.html',
 './manifest.webmanifest',
 './icons/icon-192.png',
 './icons/icon-512.png'
];


// نصب: کش اولیه
self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(ASSETS))
  );
  self.skipWaiting();
});

// فعال‌سازی: پاکسازی کش‌های قدیمی
self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.map((k) => (k !== CACHE_NAME ? caches.delete(k) : null)))
    )
  );
  self.clients.claim();
});

// fetch: اول از کش، بعد نت (برای آفلاین)
self.addEventListener("fetch", (event) => {
  const req = event.request;

  // فقط GET
  if (req.method !== "GET") return;

  event.respondWith(
    caches.match(req).then((cached) => {
      if (cached) return cached;

      return fetch(req)
        .then((res) => {
          // کپی پاسخ و کش کردن
          const copy = res.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(req, copy));
          return res;
        })
        .catch(() => caches.match("./index.html"));
    })
  );
});
