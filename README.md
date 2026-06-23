# PBO Berkas Project

## Anggota Kelompok

| No | Nama | NIM |
|----|------|-----|
| 1 | Ahmad Muqarrobin | 24250204003 |
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

### 2. Install Frontend dependencies
```bash
cd Frontend
npm install
```

### 3. Build / Run the Backend
The backend ships with a **Maven wrapper** (`apache-maven-3.9.6/bin/mvn.cmd`). No global Maven installation is required.

```bash
cd ../Backend
.\apache-maven-3.9.6\bin\mvn.cmd quarkus:dev
```
This starts Quarkus in **dev mode**:
- HTTP server listens on **http://localhost:8080**.
- An in‑memory H2 database is created and pre‑populated with five dummy `Surat` records (see the console output).
- Live‑coding is enabled – any code change will be hot‑reloaded.

> **Tip:** If you prefer to use a globally installed Maven, you can run `mvn quarkus:dev` instead.

---

## Running the Application

1. **Start the backend** (see step 3 above). Keep this terminal open.
2. **Start the frontend** in a new terminal:
```bash
cd Frontend
npm run dev
```
   The Vite dev server will start and print something like:
   ```
   > Local:   http://localhost:5173/
   > Network: use --host to expose
   ```
3. Open your browser and navigate to `http://localhost:5173`. The UI will fetch data from the backend (`http://localhost:8080/api/surat`).

---

## Development Workflow

- **Frontend:** Edit files under `Frontend/src/`. Vite automatically reloads the page.
- **Backend:** Edit Java files under `Backend/src/main/java/`. Quarkus dev mode detects changes and recompiles on‑the‑fly.
- **Database:** The H2 DB is in‑memory; changes are lost when the backend stops. To persist data, edit the `application.properties` file in `Backend/src/main/resources/` and configure a file‑based datasource if needed.

---

## Common Issues & Tips

- **Port conflicts:** Ensure no other service is using ports `5173` (frontend) or `8080` (backend).
- **CORS errors:** The backend is already configured to allow the Vite dev server origin. If you move the frontend to a different host, update the CORS settings in `application.properties`.
- **Telemetry prompt:** On first run Quarkus asks whether to send anonymous build‑time data. The prompt has been answered automatically (`y`). You can change this by editing `application.properties` (`quarkus.telemetry.enabled=false`).

---

## Stopping the Application

- **Frontend:** In the terminal where `npm run dev` is running, press `Ctrl+C`.
- **Backend:** In the terminal where the Quarkus dev server is running (`mvn quarkus:dev`), press `Ctrl+C`. If you started the backend as a background task via the IDE, you can stop it with the task manager (`manage_task` kill) or close the terminal.
- **Background tasks (IDE):** To stop a background task you can run:
  ```
  manage_task Action=kill TaskId=task-73
  ```
  (replace with the actual task id shown in your task list).

After stopping both servers, the application is fully deactivated.

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
