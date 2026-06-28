const apiUrl = 'http://localhost:8080/api/surat';

async function seedData() {
    console.log("Memulai proses seeding data...");
    let successCount = 0;
    
    for (let i = 1; i <= 120; i++) {
        // Variasi Jenis Surat
        const jenis = i % 2 === 0 ? 'MASUK' : 'KELUAR';
        
        // Variasi Nomor Surat
        const nomor = `TEST-${String(i).padStart(3, '0')}`;
        
        // Variasi Judul & Keterangan
        const judul = `Surat Testing Ke-${i}`;
        const keterangan = `Ini adalah surat seeder otomatis nomor ${i} untuk pengujian pagination dan sistem data.`;
        
        // Variasi Tanggal (Mundur dari hari ini hingga 100+ hari ke belakang)
        const d = new Date();
        d.setDate(d.getDate() - i); 
        const dateStr = d.toISOString().split('T')[0];

        const body = {
            jenis: jenis,
            nomorSurat: nomor,
            judul: judul,
            pihakTerkait: `Instansi/Pihak ${i}`,
            keterangan: keterangan,
            tanggal: dateStr
        };

        try {
            const res = await fetch(apiUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
            
            if (res.ok) {
                successCount++;
                console.log(`[OK] Surat ${nomor} berhasil ditambahkan (${dateStr})`);
            } else {
                console.error(`[GAGAL] Surat ${nomor}: ${res.statusText}`);
            }
        } catch (e) {
            console.error(`[ERROR] Surat ${nomor}:`, e.message);
        }
    }
    
    console.log(`\nSelesai! Berhasil memasukkan ${successCount} surat ke database.`);
}

seedData();
