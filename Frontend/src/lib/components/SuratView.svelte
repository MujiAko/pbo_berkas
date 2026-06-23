<script>
    import { createEventDispatcher } from 'svelte';
    import { getExportSinglePdfUrl } from '../api.js';

    export let data = null;
    export let isOpen = false;

    const dispatch = createEventDispatcher();

    function closeModal() {
        dispatch('close');
    }

    function handleDownloadPdf() {
        if (data && data.id) {
            const url = getExportSinglePdfUrl(data.id);
            window.open(url, '_blank');
        }
    }
</script>

{#if isOpen && data}
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm transition-all duration-300 p-4">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden transform transition-all mx-4 sm:mx-0 max-h-[90vh] overflow-y-auto">
            <div class="px-5 py-4 sm:px-8 sm:py-5 border-b border-slate-100 flex justify-between items-center bg-white sticky top-0 z-10">
                <h3 class="text-base sm:text-lg font-bold text-slate-900">
                    Detail Surat
                </h3>
                <button on:click={closeModal} class="text-slate-400 hover:text-slate-600 bg-slate-100 hover:bg-slate-200 p-2 rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-500">
                    <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                    </svg>
                </button>
            </div>
            
            <div class="p-5 sm:p-8 space-y-5 sm:space-y-6">
                <div class="flex items-center space-x-3 sm:space-x-4">
                    <div class="flex-shrink-0 h-12 w-12 rounded-xl flex items-center justify-center 
                        {data.jenis === 'MASUK' ? 'bg-emerald-100/50 text-emerald-600' : 'bg-amber-100/50 text-amber-600'}">
                        {#if data.jenis === 'MASUK'}
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3"></path></svg>
                        {:else}
                            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18"></path></svg>
                        {/if}
                    </div>
                    <div>
                        <div class="text-sm font-semibold text-slate-500 uppercase tracking-wider">{data.jenis === 'MASUK' ? 'Surat Masuk' : 'Surat Keluar'}</div>
                        <div class="text-xl font-bold text-slate-900">{data.nomorSurat}</div>
                    </div>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-6 pt-4 border-t border-slate-100">
                    <div>
                        <div class="text-sm font-semibold text-slate-500 mb-1">Tanggal</div>
                        <div class="text-slate-900 font-medium">
                            {new Date(data.tanggal).toLocaleDateString('id-ID', { year: 'numeric', month: 'long', day: 'numeric' })}
                        </div>
                    </div>
                    <div>
                        <div class="text-sm font-semibold text-slate-500 mb-1">
                            {data.jenis === 'MASUK' ? 'Pengirim' : 'Penerima'}
                        </div>
                        <div class="text-slate-900 font-medium">{data.pihakTerkait}</div>
                    </div>
                </div>

                <div>
                    <div class="text-sm font-semibold text-slate-500 mb-1">Judul / Perihal</div>
                    <div class="text-slate-900 font-medium">{data.judul}</div>
                </div>

                <div>
                    <div class="text-sm font-semibold text-slate-500 mb-1">Keterangan</div>
                    <div class="text-slate-900 bg-slate-50 rounded-xl p-4 text-sm leading-relaxed whitespace-pre-wrap border border-slate-100">
                        {data.keterangan || 'Tidak ada keterangan tambahan.'}
                    </div>
                </div>
            </div>

            <div class="px-5 py-4 sm:px-8 sm:py-5 border-t border-slate-100 flex justify-between bg-slate-50">
                <button type="button" on:click={handleDownloadPdf} class="flex items-center text-sm font-semibold text-indigo-600 bg-indigo-50 hover:bg-indigo-100 px-4 py-2 rounded-xl transition-colors">
                    <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
                    Unduh PDF
                </button>
                <button type="button" on:click={closeModal} class="btn-primary px-6 w-auto sm:w-auto">
                    Tutup
                </button>
            </div>
        </div>
    </div>
{/if}
