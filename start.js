const { spawn } = require('child_process');

// Menjalankan command secara background (digabung menjadi string untuk shell: true)
const frontend = spawn('npm run dev', { cwd: './Frontend', shell: true });
const backend = spawn('.\\apache-maven-3.9.6\\bin\\mvn.cmd quarkus:dev -Dquarkus.console.color=false', { cwd: './Backend', shell: true });

let frontendReady = false;
let backendReady = false;

// Bersihkan terminal saat dimulai
console.clear();
console.log('Sedang menyiapkan server, mohon tunggu...\n');

function checkFrontendReady(text) {
  if ((text.includes('Local:') || text.includes('ready in')) && !frontendReady) {
    frontendReady = true;
    console.log('Frontend Server : READY');
    checkReady();
  }
}

// Pantau output Frontend
frontend.stdout.on('data', (data) => {
  const text = data.toString();
  checkFrontendReady(text);
});
frontend.stderr.on('data', (data) => {
  const text = data.toString();
  checkFrontendReady(text); // Kadang Vite menuliskan info ke stderr
  // Hanya tampilkan jika benar-benar error agar tidak spam
  if (text.toLowerCase().includes('error')) {
    console.log('[Frontend] ' + text.trim());
  }
});

// Pantau output Backend
backend.stdout.on('data', (data) => {
  const text = data.toString();
  if (text.includes('Listening on:') && !backendReady) {
    backendReady = true;
    console.log('Backend Server  : READY');
    checkReady();
  }
  // Hanya tampilkan baris yang mengandung error
  if (text.includes('ERROR') || text.includes('BUILD FAILURE')) {
    console.log('[Backend] ' + text.trim());
  }
});

function checkReady() {
  if (frontendReady && backendReady) {
    console.log('\nSemua sistem berjalan! Silakan buka browser di: http://localhost:5173');
    console.log('Tekan Ctrl + C untuk mematikan server.');
  }
}
