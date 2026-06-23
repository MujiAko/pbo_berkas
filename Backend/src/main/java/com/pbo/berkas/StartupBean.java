package com.pbo.berkas;

import com.pbo.berkas.model.Surat;
import com.pbo.berkas.repository.SuratRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@ApplicationScoped
public class StartupBean {

    @Inject
    SuratRepository suratRepository;

    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        // Initialize dummy data only if empty
        if (suratRepository.count() == 0) {
            
            Surat s1 = new Surat();
            s1.setNomorSurat("001/IT/2026");
            s1.setJudul("Undangan Rapat Divisi IT");
            s1.setJenis("MASUK");
            s1.setTanggal(LocalDate.now().minusDays(2));
            s1.setPihakTerkait("PT Teknologi Nusantara");
            s1.setKeterangan("Rapat pembahasan proyek baru bulan depan.");
            suratRepository.persist(s1);

            Surat s2 = new Surat();
            s2.setNomorSurat("002/HR/2026");
            s2.setJudul("Pemberitahuan Cuti Bersama");
            s2.setJenis("MASUK");
            s2.setTanggal(LocalDate.now().minusDays(5));
            s2.setPihakTerkait("Kementerian Ketenagakerjaan");
            s2.setKeterangan("Libur nasional Hari Raya.");
            suratRepository.persist(s2);

            Surat s3 = new Surat();
            s3.setNomorSurat("010/OUT/2026");
            s3.setJudul("Penawaran Kerja Sama Software");
            s3.setJenis("KELUAR");
            s3.setTanggal(LocalDate.now().minusDays(1));
            s3.setPihakTerkait("CV Maju Mundur");
            s3.setKeterangan("Proposal pembuatan aplikasi POS.");
            suratRepository.persist(s3);

            Surat s4 = new Surat();
            s4.setNomorSurat("011/OUT/2026");
            s4.setJudul("Tagihan Invoice Bulan Mei");
            s4.setJenis("KELUAR");
            s4.setTanggal(LocalDate.now());
            s4.setPihakTerkait("PT Berkah Selalu");
            s4.setKeterangan("Jatuh tempo minggu depan.");
            suratRepository.persist(s4);

            Surat s5 = new Surat();
            s5.setNomorSurat("003/FIN/2026");
            s5.setJudul("Laporan Keuangan Q1");
            s5.setJenis("MASUK");
            s5.setTanggal(LocalDate.now().minusDays(10));
            s5.setPihakTerkait("Auditor Eksternal");
            s5.setKeterangan("Laporan telah diverifikasi.");
            suratRepository.persist(s5);
            
            System.out.println("Dummy data initialized: 5 records inserted.");
        }
    }
}
