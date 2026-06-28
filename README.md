# PBO Berkas Project

## Anggota Kelompok

| No | Nama | NIM |
|----|------|-----|
| 1 | Ahmad Muqarrobin | 242502040003 |
| 2 | Akmal Fauzan | 12110078 |
| 3 | Muhamad Yusuf | 242502040108 |
| 4 | Raffy Andreano Pratama | 242502040030 |
| 5 | Ramzy Fadlil Agusanddawi | 242502040052 |

> Tugas UAS — Pemrograman Berbasis Objek (PBO) · Semester 4

---

## Overview

This repository contains a simple **full‑stack application** built with:

- **Frontend** – Vite + Svelte (JavaScript) located in the `Frontend` folder.
- **Backend** – Quarkus 3.10 (Java) using Maven, located in the `Backend` folder.

The application manages *Surat* records (documents) and exposes a REST API at `http://localhost:8080/api/surat`.

---

## Prerequisites

| Tool | Minimum version | Why?
|------|----------------|------
| **Node.js** | 18.x or newer | Needed for the Vite dev server and to install npm packages.
| **npm** | 9.x or newer | Package manager for the frontend.
| **Java** | 17 (JDK) | Quarkus runs on Java 17.
| **Maven** | 3.9.x (the bundled wrapper) | Builds and runs the backend.
| **Git** (optional) | any | To clone the repo if you haven't already.

Make sure the above commands are available in your `PATH`.

---

## Project Structure

```
PBO Berkas/
├─ Frontend/          # Svelte + Vite UI
│   ├─ src/
│   └─ package.json
├─ Backend/           # Quarkus Java server
│   ├─ src/main/java/…
│   └─ pom.xml
└─ README.md          # THIS FILE
```

---

## Setup & Installation

### 1. Clone the repository (skip if you already have the files)
```bash
git clone <repository‑url>
cd "PBO Berkas"
```

### 2. Install Dependencies
```bash
# Install root dependencies
npm install

# Install Frontend dependencies
cd Frontend
npm install
cd ..
```

### 3. Menjalankan Aplikasi
Anda tidak perlu repot menyalakan frontend dan backend secara terpisah. Kami sudah menyediakan satu perintah sakti:

```bash
npm run dev
```
Perintah ini akan menyalakan Quarkus (Backend) di port 8080 dan Vite (Frontend) di port 5173 secara bersamaan menggunakan *script* kustom.

> **Tip:** Buka browser dan arahkan ke `http://localhost:5173`. Aplikasi otomatis terhubung ke `http://localhost:8080/api/surat`.

---

## Testing

Untuk menjalankan pengujian otomatis di Frontend (Vitest) dan Backend (JUnit 5 + REST Assured), gunakan satu perintah ini di *root directory*:
```bash
npm run test
```

### Seeding Data / Mengisi Dummy Data
Jika Anda membutuhkan data palsu dalam jumlah banyak (misal untuk menguji fitur *pagination* atau filter), Anda dapat menjalankan *script seeder* yang tersedia. Pastikan backend server sudah berjalan (`npm run dev`), lalu jalankan perintah ini di terminal baru:
```bash
node seed.js
```
Script ini akan secara otomatis menembak API dan menambahkan lebih dari 100 surat dengan nomor, keterangan, dan tanggal yang bervariasi (mundur hingga beberapa bulan ke belakang).

## Development Workflow

- **Frontend:** Edit files under `Frontend/src/`. Vite automatically reloads the page.
- **Backend:** Edit Java files under `Backend/src/main/java/`. Quarkus dev mode detects changes and recompiles on‑the‑fly.
- **Database:** Aplikasi menggunakan database **SQLite** berbasis file (`berkasdb.sqlite`). Semua perubahan (termasuk data seeder) akan **otomatis tersimpan secara permanen** dan tidak akan hilang meskipun server dimatikan. Konfigurasi ini dapat dilihat pada `application.properties`.

---

## Common Issues & Tips

- **Port conflicts:** Ensure no other service is using ports `5173` (frontend) or `8080` (backend).
- **CORS errors:** The backend is already configured to allow the Vite dev server origin. If you move the frontend to a different host, update the CORS settings in `application.properties`.
- **Telemetry prompt:** On first run Quarkus asks whether to send anonymous build‑time data. The prompt has been answered automatically (`y`). You can change this by editing `application.properties` (`quarkus.telemetry.enabled=false`).

---

## Stopping the Application

- **Cara Menghentikan:** Cukup tekan `Ctrl+C` pada terminal yang menjalankan `npm run dev`. *Script* bawaan kami akan secara otomatis mematikan proses Frontend dan Backend.

---

## License

This project is for educational purposes. Feel free to modify, redistribute, or use it as a reference.

---

## Acknowledgements

- **Quarkus** – Supersonic Subatomic Java framework.
- **Vite** – Fast frontend tooling.
- **Svelte** – Reactive UI library.

---

**Enjoy coding!**
