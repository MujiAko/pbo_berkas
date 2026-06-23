<script>
    import '../app.css';
</script>

<div class="min-h-screen flex flex-col bg-slate-50 font-sans text-slate-900">
    <!-- Glassmorphism Header -->
    <header class="sticky top-0 z-40 w-full backdrop-blur-xl bg-white/75 border-b border-slate-200/80 shadow-sm">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-indigo-600 rounded-xl flex items-center justify-center text-white shadow-lg shadow-indigo-600/30">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path></svg>
                </div>
                <div>
                    <h1 class="text-xl font-bold tracking-tight text-slate-900 leading-none">E-Surat App</h1>
                    <div class="text-xs font-medium text-indigo-600 mt-1">Sistem Manajemen Berkas</div>
                </div>
            </div>
            
            <div class="hidden sm:block text-sm font-medium text-slate-500 bg-slate-100/80 px-4 py-1.5 rounded-full border border-slate-200/60">
                Tugas PBO Java 2026
            </div>
        </div>
    </header>

    <!-- Main Content Area -->
    <main class="flex-grow max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 md:py-10">
        <slot />
    </main>

    <!-- Footer -->
    <footer class="mt-auto py-8">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col sm:flex-row justify-between items-center text-sm text-slate-500 font-medium">
            <div>&copy; 2026 E-Surat App.</div>
            <div class="mt-2 sm:mt-0">Dibuat untuk Keperluan Tugas PBO</div>
        </div>
    </footer>
</div>
