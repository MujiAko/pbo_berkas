<script>
    import { onMount } from 'svelte';
    import { getSuratList, deleteSurat, getExportPdfUrl, getExportSinglePdfUrl, bulkDeleteSurat } from '../lib/api.js';
    import SuratForm from '../lib/components/SuratForm.svelte';
    import SuratView from '../lib/components/SuratView.svelte';

    let activeTab = 'SEMUA';
    let searchQuery = '';
    let startDate = '';
    let endDate = '';
    let debounceTimer = null;

    let suratList = [];
    let loading = true;
    let error = null;

    let isFormOpen = false;
    let editingData = null;

    let isViewOpen = false;
    let viewingData = null;

    let selectedIds = [];

    // --- Sorting ---
    let sortField = 'tanggal';   // 'tanggal' | 'nomorSurat' | 'judul' | 'pihakTerkait'
    let sortDir = 'desc';        // 'asc' | 'desc'

    // --- Pagination ---
    let currentPage = 1;
    const PAGE_SIZE = 8;

    // Derived: sorted list
    $: sortedList = [...suratList].sort((a, b) => {
        let valA = a[sortField] ?? '';
        let valB = b[sortField] ?? '';
        if (sortField === 'tanggal') {
            valA = new Date(valA).getTime();
            valB = new Date(valB).getTime();
        } else {
            valA = valA.toString().toLowerCase();
            valB = valB.toString().toLowerCase();
        }
        if (valA < valB) return sortDir === 'asc' ? -1 : 1;
        if (valA > valB) return sortDir === 'asc' ? 1 : -1;
        return 0;
    });

    // Derived: paginated list
    $: totalPages = Math.max(1, Math.ceil(sortedList.length / PAGE_SIZE));
    $: pagedList = sortedList.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE);

    // Reset to page 1 when filters change
    $: if (suratList || searchQuery || activeTab || sortField || sortDir) {
        currentPage = 1;
    }

    function setSort(field) {
        if (sortField === field) {
            sortDir = sortDir === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDir = field === 'tanggal' ? 'desc' : 'asc';
        }
    }

    function goToPage(p) {
        if (p >= 1 && p <= totalPages) currentPage = p;
    }

    // Visible page numbers (max 5 around current)
    $: pageNumbers = (() => {
        const range = [];
        const delta = 2;
        for (let i = Math.max(1, currentPage - delta); i <= Math.min(totalPages, currentPage + delta); i++) {
            range.push(i);
        }
        return range;
    })();

    async function loadData() {
        loading = true;
        error = null;
        selectedIds = [];
        try {
            const jenisFilter = activeTab === 'SEMUA' ? '' : activeTab;
            suratList = await getSuratList(jenisFilter, searchQuery, startDate, endDate);
        } catch (err) {
            error = 'Gagal menghubungi server. Pastikan backend Quarkus berjalan.';
            console.error(err);
        } finally {
            loading = false;
        }
    }

    onMount(() => { loadData(); });

    function setTab(tab) { activeTab = tab; loadData(); }

    function handleSearchInput() {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => { loadData(); }, 350);
    }

    function clearSearch() {
        searchQuery = '';
        clearTimeout(debounceTimer);
        loadData();
    }

    function handleDateChange() {
        currentPage = 1;
        loadData();
    }

    function handleExportPdf() {
        const jenisFilter = activeTab === 'SEMUA' ? '' : activeTab;
        window.open(getExportPdfUrl(jenisFilter, searchQuery, startDate, endDate), '_blank');
    }

    function handleDownloadSingle(id) {
        window.open(getExportSinglePdfUrl(id), '_blank');
    }

    function openAddForm() { editingData = null; isFormOpen = true; }
    function openEditForm(surat) { editingData = surat; isFormOpen = true; }
    function openViewForm(surat) { viewingData = surat; isViewOpen = true; }

    async function handleDelete(id) {
        if (confirm('Apakah Anda yakin ingin menghapus surat ini?')) {
            try { await deleteSurat(id); loadData(); }
            catch { alert('Gagal menghapus data.'); }
        }
    }

    function toggleSelectAll(e) {
        selectedIds = e.target.checked ? pagedList.map(s => s.id) : [];
    }

    $: isAllSelected = pagedList.length > 0 && pagedList.every(s => selectedIds.includes(s.id));

    async function handleBulkDelete() {
        if (selectedIds.length === 0) return;
        if (confirm(`Apakah Anda yakin ingin menghapus ${selectedIds.length} surat yang dipilih?`)) {
            try { await bulkDeleteSurat(selectedIds); selectedIds = []; loadData(); }
            catch { alert('Gagal menghapus data secara massal.'); }
        }
    }

    function onFormSuccess() { isFormOpen = false; loadData(); }
</script>

<div class="bg-white rounded-2xl shadow-[0_8px_30px_rgb(0,0,0,0.04)] border border-slate-100 overflow-hidden">

    <!-- ===== TOOLBAR ===== -->
    <div class="px-5 py-4 sm:px-8 sm:py-5 border-b border-slate-100 bg-white">

        <!-- Row 1: Tabs + Bulk Delete + Actions -->
        <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">

            <!-- Left: Tabs + Bulk Delete -->
            <div class="flex flex-wrap items-center gap-2">
                <!-- Tab group -->
                <div class="flex p-1 bg-slate-100/80 rounded-xl">
                    {#each [['SEMUA','Semua'],['MASUK','Masuk'],['KELUAR','Keluar']] as [val, label]}
                        <button
                            on:click={() => setTab(val)}
                            class="px-3 py-1.5 rounded-lg text-xs font-semibold transition-all whitespace-nowrap
                                {activeTab === val ? 'bg-white shadow-sm text-indigo-600' : 'text-slate-500 hover:text-slate-800'}">
                            {label}
                        </button>
                    {/each}
                </div>

                <!-- Bulk Delete pill (appears when selected) -->
                {#if selectedIds.length > 0}
                    <button on:click={handleBulkDelete}
                        class="flex items-center gap-1.5 px-3 py-1.5 bg-rose-50 hover:bg-rose-100 text-rose-600 hover:text-rose-700 rounded-xl border border-rose-100 text-xs font-bold transition-colors">
                        <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                        Hapus ({selectedIds.length})
                    </button>
                {/if}
            </div>

            <!-- Right: Search + Export + Tambah -->
            <div class="flex items-center gap-2 w-full sm:w-auto flex-wrap sm:flex-nowrap">
                
                <!-- Filter Tanggal -->
                <div class="flex items-center gap-1.5 w-full sm:w-auto">
                    <input type="date" bind:value={startDate} on:change={handleDateChange} title="Dari Tanggal" class="w-full sm:w-auto py-2 px-2.5 bg-slate-100/80 rounded-xl text-xs sm:text-sm border-none focus:ring-2 focus:ring-indigo-400 transition-all text-slate-500 cursor-pointer" />
                    <span class="text-slate-400 text-xs font-semibold px-0.5">-</span>
                    <input type="date" bind:value={endDate} on:change={handleDateChange} title="Sampai Tanggal" class="w-full sm:w-auto py-2 px-2.5 bg-slate-100/80 rounded-xl text-xs sm:text-sm border-none focus:ring-2 focus:ring-indigo-400 transition-all text-slate-500 cursor-pointer" />
                </div>

                <!-- Search -->
                <div class="relative flex-1 sm:flex-none">
                    <svg class="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 pointer-events-none" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                    </svg>
                    <input
                        type="text"
                        bind:value={searchQuery}
                        on:input={handleSearchInput}
                        placeholder="Cari surat..."
                        class="pl-9 {searchQuery ? 'pr-8' : 'pr-4'} py-2 bg-slate-100/80 rounded-xl text-sm border-none focus:ring-2 focus:ring-indigo-400 w-full sm:w-56 transition-all"
                    />
                    {#if searchQuery}
                        <button on:click={clearSearch} class="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M6 18L18 6M6 6l12 12"/></svg>
                        </button>
                    {/if}
                </div>

                <!-- Export -->
                <button on:click={handleExportPdf} class="btn-secondary flex items-center gap-1.5 py-2 px-3 text-xs sm:text-sm whitespace-nowrap">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
                    <span class="hidden sm:inline">Export PDF</span>
                    <span class="sm:hidden">Export</span>
                </button>

                <!-- Tambah Baru -->
                <button on:click={openAddForm} class="btn-primary flex items-center gap-1.5 py-2 px-3 text-xs sm:text-sm whitespace-nowrap">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
                    <span class="hidden sm:inline">Tambah Baru</span>
                    <span class="sm:hidden">Tambah</span>
                </button>
            </div>
        </div>

        <!-- Row 2: Sort controls (desktop only) + Stats -->
        <div class="hidden sm:flex items-center justify-between mt-3 pt-3 border-t border-slate-50">
            <div class="flex items-center gap-1 text-xs text-slate-400">
                <span>Urutkan:</span>
                {#each [['tanggal','Tanggal'],['nomorSurat','Nomor'],['judul','Judul'],['pihakTerkait','Pihak']] as [f, label]}
                    <button on:click={() => setSort(f)}
                        class="flex items-center gap-0.5 px-2.5 py-1 rounded-lg font-semibold transition-all
                            {sortField === f ? 'bg-indigo-50 text-indigo-600' : 'text-slate-400 hover:text-slate-600 hover:bg-slate-50'}">
                        {label}
                        {#if sortField === f}
                            <svg class="w-3 h-3 {sortDir === 'desc' ? 'rotate-180' : ''} transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 15l7-7 7 7"/></svg>
                        {/if}
                    </button>
                {/each}
            </div>
            <span class="text-xs text-slate-400">
                {#if suratList.length > 0}
                    {(currentPage - 1) * PAGE_SIZE + 1}–{Math.min(currentPage * PAGE_SIZE, sortedList.length)} dari {sortedList.length} surat
                {:else}
                    0 surat
                {/if}
            </span>
        </div>
    </div>

    <!-- ===== TABLE CONTENT ===== -->
    <div>
        {#if loading}
            <div class="p-16 text-center text-slate-400">
                <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-500 mx-auto mb-3"></div>
                <p class="text-sm">Memuat data surat...</p>
            </div>

        {:else if error}
            <div class="p-16 text-center">
                <div class="w-12 h-12 bg-rose-100 text-rose-500 rounded-full flex items-center justify-center mx-auto mb-3">
                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
                </div>
                <p class="text-sm font-medium text-rose-500">{error}</p>
            </div>

        {:else if suratList.length === 0}
            <div class="p-20 text-center text-slate-400">
                <div class="w-16 h-16 bg-slate-50 rounded-2xl flex items-center justify-center mx-auto mb-4 border border-slate-100">
                    <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
                </div>
                <p class="font-semibold text-slate-500">Belum ada surat</p>
                <p class="text-sm mt-1">Coba sesuaikan pencarian atau tambah surat baru.</p>
            </div>

        {:else}
            <!-- Mobile: Sort bar -->
            <div class="sm:hidden flex items-center justify-between px-4 py-2.5 bg-slate-50 border-b border-slate-100 gap-2">
                <div class="flex items-center gap-1 flex-1 overflow-x-auto scrollbar-hide">
                    <span class="text-[11px] text-slate-400 font-medium shrink-0">Urut:</span>
                    {#each [['tanggal','Tgl'],['nomorSurat','No.'],['judul','Judul'],['pihakTerkait','Pihak']] as [f, label]}
                        <button on:click={() => setSort(f)}
                            class="flex items-center gap-0.5 px-2 py-1 rounded-lg text-[11px] font-bold whitespace-nowrap transition-all shrink-0
                                {sortField === f ? 'bg-indigo-500 text-white' : 'bg-white text-slate-500 border border-slate-200'}">
                            {label}
                            {#if sortField === f}
                                <svg class="w-2.5 h-2.5 {sortDir === 'desc' ? 'rotate-180' : ''}" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 15l7-7 7 7"/></svg>
                            {/if}
                        </button>
                    {/each}
                </div>
                <label class="flex items-center gap-2 shrink-0 cursor-pointer">
                    <span class="text-[11px] text-slate-500 font-medium">Pilih Semua</span>
                    <input type="checkbox" on:change={toggleSelectAll} checked={isAllSelected}
                        class="rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 cursor-pointer w-4 h-4" />
                </label>
            </div>

            <!-- Desktop: Table -->
            <div class="hidden sm:block overflow-x-auto">
                <table class="w-full">
                    <thead>
                        <tr class="bg-slate-50/60 border-b border-slate-100">
                            <th class="w-10 px-4 py-3 text-center">
                                <input type="checkbox" on:change={toggleSelectAll} checked={isAllSelected}
                                    class="rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 cursor-pointer w-4 h-4" />
                            </th>
                            <!-- Sortable headers -->
                            {#each [['nomorSurat','Nomor Surat','px-4'],['judul','Judul & Keterangan','px-4'],['pihakTerkait','Pihak Terkait','px-4'],['tanggal','Tanggal','px-4']] as [f, label, pad]}
                                <th class="{pad} py-3 text-left">
                                    <button on:click={() => setSort(f)}
                                        class="flex items-center gap-1 text-xs font-semibold text-slate-500 uppercase tracking-wider hover:text-indigo-600 transition-colors group">
                                        {label}
                                        <span class="transition-all {sortField === f ? 'opacity-100 text-indigo-500' : 'opacity-0 group-hover:opacity-40'}">
                                            <svg class="w-3 h-3 {sortField === f && sortDir === 'desc' ? 'rotate-180' : ''} transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 15l7-7 7 7"/></svg>
                                        </span>
                                    </button>
                                </th>
                            {/each}
                            <th class="px-4 py-3 text-right text-xs font-semibold text-slate-500 uppercase tracking-wider">Aksi</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-50">
                        {#each pagedList as surat (surat.id)}
                            <!-- svelte-ignore a11y_click_events_have_key_events -->
                            <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
                            <tr on:click={() => openViewForm(surat)}
                                class="hover:bg-indigo-50/30 transition-colors cursor-pointer group {selectedIds.includes(surat.id) ? 'bg-indigo-50/40' : ''}">

                                <!-- svelte-ignore a11y_click_events_have_key_events -->
                                <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
                                <td class="px-4 py-4 text-center" on:click|stopPropagation>
                                    <input type="checkbox" bind:group={selectedIds} value={surat.id}
                                        class="rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 cursor-pointer w-4 h-4" />
                                </td>

                                <!-- Nomor Surat -->
                                <td class="px-4 py-4">
                                    <div class="flex items-center gap-3">
                                        <div class="w-8 h-8 rounded-lg flex items-center justify-center shrink-0
                                            {surat.jenis === 'MASUK' ? 'bg-emerald-100 text-emerald-600' : 'bg-amber-100 text-amber-600'}">
                                            {#if surat.jenis === 'MASUK'}
                                                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"/></svg>
                                            {:else}
                                                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"/></svg>
                                            {/if}
                                        </div>
                                        <span class="text-sm font-semibold text-slate-800">{surat.nomorSurat}</span>
                                    </div>
                                </td>

                                <!-- Judul -->
                                <td class="px-4 py-4 max-w-xs">
                                    <div class="text-sm font-medium text-slate-800 truncate">{surat.judul}</div>
                                    <div class="text-xs text-slate-400 truncate mt-0.5">{surat.keterangan || '—'}</div>
                                </td>

                                <!-- Pihak Terkait -->
                                <td class="px-4 py-4">
                                    <div class="text-sm text-slate-700">{surat.pihakTerkait}</div>
                                    <div class="text-xs font-medium mt-0.5 {surat.jenis === 'MASUK' ? 'text-emerald-600' : 'text-amber-600'}">
                                        {surat.jenis === 'MASUK' ? 'Pengirim' : 'Penerima'}
                                    </div>
                                </td>

                                <!-- Tanggal -->
                                <td class="px-4 py-4">
                                    <div class="text-sm text-slate-700">
                                        {new Date(surat.tanggal).toLocaleDateString('id-ID', { day: 'numeric', month: 'short', year: 'numeric' })}
                                    </div>
                                </td>

                                <!-- Actions -->
                                <td class="px-4 py-4 text-right" on:click|stopPropagation>
                                    <!-- svelte-ignore a11y_click_events_have_key_events -->
                                    <!-- svelte-ignore a11y_no_noninteractive_element_interactions -->
                                    <div class="flex items-center justify-end gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                        <button on:click|stopPropagation={() => handleDownloadSingle(surat.id)}
                                            title="Unduh PDF"
                                            class="p-1.5 rounded-lg text-emerald-600 hover:bg-emerald-50 transition-colors">
                                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
                                        </button>
                                        <button on:click|stopPropagation={() => openEditForm(surat)}
                                            title="Edit"
                                            class="p-1.5 rounded-lg text-indigo-600 hover:bg-indigo-50 transition-colors">
                                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                                        </button>
                                        <button on:click|stopPropagation={() => handleDelete(surat.id)}
                                            title="Hapus"
                                            class="p-1.5 rounded-lg text-rose-500 hover:bg-rose-50 transition-colors">
                                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>

            <!-- Mobile: Card list -->
            <div class="sm:hidden divide-y divide-slate-100">
                {#each pagedList as surat (surat.id)}
                    <!-- svelte-ignore a11y_click_events_have_key_events -->
                    <!-- svelte-ignore a11y_no_static_element_interactions -->
                    <button type="button" on:click={() => openViewForm(surat)}
                        class="w-full text-left p-4 cursor-pointer transition-colors {selectedIds.includes(surat.id) ? 'bg-indigo-50/50' : 'hover:bg-slate-50'}">

                        <!-- Card Header -->
                        <div class="flex items-start gap-3 mb-3">
                            <div class="w-9 h-9 rounded-xl flex items-center justify-center shrink-0 mt-0.5
                                {surat.jenis === 'MASUK' ? 'bg-emerald-100 text-emerald-600' : 'bg-amber-100 text-amber-600'}">
                                {#if surat.jenis === 'MASUK'}
                                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"/></svg>
                                {:else}
                                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"/></svg>
                                {/if}
                            </div>
                            <div class="flex-1 min-w-0">
                                <div class="flex items-center justify-between gap-2">
                                    <span class="text-xs font-bold text-slate-500 truncate">{surat.nomorSurat}</span>
                                    <input type="checkbox" bind:group={selectedIds} value={surat.id}
                                        on:click|stopPropagation
                                        class="rounded border-slate-300 text-indigo-600 focus:ring-indigo-500 cursor-pointer w-4 h-4 shrink-0" />
                                </div>
                                <div class="text-sm font-semibold text-slate-800 mt-0.5 truncate">{surat.judul}</div>
                                {#if surat.keterangan}
                                    <div class="text-xs text-slate-400 mt-0.5 line-clamp-2">{surat.keterangan}</div>
                                {/if}
                            </div>
                        </div>

                        <!-- Card Meta -->
                        <div class="flex items-center justify-between text-xs mb-3">
                            <div>
                                <span class="text-slate-500">{surat.pihakTerkait}</span>
                                <span class="ml-1.5 font-medium {surat.jenis === 'MASUK' ? 'text-emerald-600' : 'text-amber-600'}">
                                    ({surat.jenis === 'MASUK' ? 'Pengirim' : 'Penerima'})
                                </span>
                            </div>
                            <span class="text-slate-400">
                                {new Date(surat.tanggal).toLocaleDateString('id-ID', { day: 'numeric', month: 'short', year: 'numeric' })}
                            </span>
                        </div>

                        <!-- Card Actions -->
                        <div class="flex gap-2 pt-3 border-t border-slate-100">
                            <button on:click|stopPropagation={() => handleDownloadSingle(surat.id)}
                                class="flex-1 flex flex-col items-center gap-1 py-2 rounded-xl bg-emerald-50 text-emerald-700 hover:bg-emerald-100 transition-colors">
                                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
                                <span class="text-[10px] font-bold uppercase tracking-wide">Unduh</span>
                            </button>
                            <button on:click|stopPropagation={() => openEditForm(surat)}
                                class="flex-1 flex flex-col items-center gap-1 py-2 rounded-xl bg-indigo-50 text-indigo-700 hover:bg-indigo-100 transition-colors">
                                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
                                <span class="text-[10px] font-bold uppercase tracking-wide">Edit</span>
                            </button>
                            <button on:click|stopPropagation={() => handleDelete(surat.id)}
                                class="flex-1 flex flex-col items-center gap-1 py-2 rounded-xl bg-rose-50 text-rose-700 hover:bg-rose-100 transition-colors">
                                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
                                <span class="text-[10px] font-bold uppercase tracking-wide">Hapus</span>
                            </button>
                        </div>
                    </button>
                {/each}
            </div>

            <!-- ===== PAGINATION ===== -->
            {#if totalPages > 1}
                <div class="flex items-center justify-between px-5 py-3 sm:px-8 border-t border-slate-100 bg-slate-50/50">
                    <!-- Info text -->
                    <span class="text-xs text-slate-400 hidden sm:block">
                        Halaman {currentPage} dari {totalPages}
                    </span>
                    <span class="text-xs text-slate-400 sm:hidden">
                        {currentPage}/{totalPages}
                    </span>

                    <!-- Page controls -->
                    <div class="flex items-center gap-1">
                        <!-- Prev -->
                        <button on:click={() => goToPage(currentPage - 1)} disabled={currentPage === 1}
                            class="w-8 h-8 flex items-center justify-center rounded-lg text-slate-500 hover:bg-white hover:shadow-sm disabled:opacity-30 disabled:cursor-not-allowed transition-all">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/></svg>
                        </button>

                        <!-- First page if needed -->
                        {#if pageNumbers[0] > 1}
                            <button on:click={() => goToPage(1)} class="w-8 h-8 flex items-center justify-center rounded-lg text-xs font-semibold text-slate-500 hover:bg-white hover:shadow-sm transition-all">1</button>
                            {#if pageNumbers[0] > 2}<span class="text-slate-300 text-xs px-1">…</span>{/if}
                        {/if}

                        <!-- Page numbers -->
                        {#each pageNumbers as p}
                            <button on:click={() => goToPage(p)}
                                class="w-8 h-8 flex items-center justify-center rounded-lg text-xs font-semibold transition-all
                                    {p === currentPage ? 'bg-indigo-600 text-white shadow-sm shadow-indigo-200' : 'text-slate-500 hover:bg-white hover:shadow-sm'}">
                                {p}
                            </button>
                        {/each}

                        <!-- Last page if needed -->
                        {#if pageNumbers[pageNumbers.length - 1] < totalPages}
                            {#if pageNumbers[pageNumbers.length - 1] < totalPages - 1}<span class="text-slate-300 text-xs px-1">…</span>{/if}
                            <button on:click={() => goToPage(totalPages)} class="w-8 h-8 flex items-center justify-center rounded-lg text-xs font-semibold text-slate-500 hover:bg-white hover:shadow-sm transition-all">{totalPages}</button>
                        {/if}

                        <!-- Next -->
                        <button on:click={() => goToPage(currentPage + 1)} disabled={currentPage === totalPages}
                            class="w-8 h-8 flex items-center justify-center rounded-lg text-slate-500 hover:bg-white hover:shadow-sm disabled:opacity-30 disabled:cursor-not-allowed transition-all">
                            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
                        </button>
                    </div>
                </div>
            {/if}
        {/if}
    </div>
</div>

<SuratForm
    isOpen={isFormOpen}
    initialData={editingData}
    on:close={() => isFormOpen = false}
    on:success={onFormSuccess}
/>

<SuratView
    isOpen={isViewOpen}
    data={viewingData}
    on:close={() => isViewOpen = false}
/>
