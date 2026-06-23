<script>
    import { createEventDispatcher } from 'svelte';
    import { createSurat, updateSurat } from '../api.js';

    export let initialData = null;
    export let isOpen = false;

    const dispatch = createEventDispatcher();

    let formData = {
        nomorSurat: '',
        judul: '',
        jenis: 'MASUK',
        tanggal: '',
        pihakTerkait: '',
        keterangan: ''
    };

    let loading = false;

    function initForm() {
        if (initialData) {
            let parsedTanggal = '';
            // Pastikan format tanggal sesuai standar input type="date" (YYYY-MM-DD)
            if (Array.isArray(initialData.tanggal)) {
                const y = initialData.tanggal[0];
                const m = String(initialData.tanggal[1]).padStart(2, '0');
                const d = String(initialData.tanggal[2]).padStart(2, '0');
                parsedTanggal = `${y}-${m}-${d}`;
            } else if (initialData.tanggal) {
                try {
                    parsedTanggal = new Date(initialData.tanggal).toISOString().split('T')[0];
                } catch (e) {
                    // Ignore if date parsing fails
                }
            }
            
            formData = { 
                ...initialData,
                tanggal: parsedTanggal
            };
        } else {
            formData = {
                nomorSurat: '',
                judul: '',
                jenis: 'MASUK',
                tanggal: new Date().toISOString().split('T')[0],
                pihakTerkait: '',
                keterangan: ''
            };
        }
    }

    // Hanya inisialisasi data form ketika modal baru dibuka
    $: if (isOpen) {
        initForm();
    }

    function closeModal() {
        dispatch('close');
    }

    async function handleSubmit() {
        loading = true;
        try {
            if (initialData && initialData.id) {
                await updateSurat(initialData.id, formData);
            } else {
                await createSurat(formData);
            }
            dispatch('success');
        } catch (err) {
            alert('Gagal menyimpan data: ' + err.message);
        } finally {
            loading = false;
        }
    }
</script>

{#if isOpen}
    <div class="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/50 backdrop-blur-sm transition-all duration-300 p-4">
        <div class="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden transform transition-all mx-4 sm:mx-0 max-h-[90vh] overflow-y-auto">
            <div class="px-5 py-4 sm:px-8 sm:py-5 border-b border-slate-100 flex justify-between items-center bg-white sticky top-0 z-10">
                <h3 class="text-base sm:text-lg font-bold text-slate-900">
                    {initialData ? 'Edit Surat' : 'Tambah Surat Baru'}
                </h3>
                <button on:click={closeModal} class="text-slate-400 hover:text-slate-600 bg-slate-100 hover:bg-slate-200 p-2 rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-indigo-500">
                    <svg class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                    </svg>
                </button>
            </div>
            
            <form on:submit|preventDefault={handleSubmit} class="p-5 sm:p-8 space-y-4 sm:space-y-5">
                <div>
                    <label for="nomorSurat" class="block text-xs sm:text-sm font-semibold text-slate-700 mb-1.5">Nomor Surat</label>
                    <input id="nomorSurat" type="text" bind:value={formData.nomorSurat} required class="input-field" placeholder="Contoh: 001/HR/2026" />
                </div>
                
                <div>
                    <label for="judul" class="block text-xs sm:text-sm font-semibold text-slate-700 mb-1.5">Judul Surat</label>
                    <input id="judul" type="text" bind:value={formData.judul} required class="input-field" placeholder="Judul/Perihal Surat" />
                </div>
                
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 sm:gap-5">
                    <div>
                        <label for="jenis" class="block text-xs sm:text-sm font-semibold text-slate-700 mb-1.5">Jenis Surat</label>
                        <select id="jenis" bind:value={formData.jenis} class="input-field bg-white">
                            <option value="MASUK">Surat Masuk</option>
                            <option value="KELUAR">Surat Keluar</option>
                        </select>
                    </div>
                    <div>
                        <label for="tanggal" class="block text-xs sm:text-sm font-semibold text-slate-700 mb-1.5">Tanggal</label>
                        <input id="tanggal" type="date" bind:value={formData.tanggal} required class="input-field" />
                    </div>
                </div>

                <div>
                    <label for="pihakTerkait" class="block text-xs sm:text-sm font-semibold text-slate-700 mb-1.5">
                        {formData.jenis === 'MASUK' ? 'Pengirim' : 'Penerima'}
                    </label>
                    <input id="pihakTerkait" type="text" bind:value={formData.pihakTerkait} required class="input-field" placeholder="Nama instansi/orang" />
                </div>
                
                <div>
                    <label for="keterangan" class="block text-xs sm:text-sm font-semibold text-slate-700 mb-1.5">Keterangan Tambahan</label>
                    <textarea id="keterangan" bind:value={formData.keterangan} rows="3" class="input-field resize-none" placeholder="Opsional..."></textarea>
                </div>

                <div class="mt-6 sm:mt-8 flex flex-col sm:flex-row justify-end space-y-3 sm:space-y-0 sm:space-x-3 pt-4 border-t border-slate-100">
                    <button type="button" on:click={closeModal} class="btn-secondary px-6 w-full sm:w-auto order-last sm:order-first mt-3 sm:mt-0">
                        Batal
                    </button>
                    <button type="submit" disabled={loading} class="btn-primary px-6 flex items-center justify-center w-full sm:w-auto">
                        {#if loading}
                            <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                            </svg>
                            Menyimpan...
                        {:else}
                            Simpan Data
                        {/if}
                    </button>
                </div>
            </form>
        </div>
    </div>
{/if}
