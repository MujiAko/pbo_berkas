package com.pbo.berkas.repository;

import com.pbo.berkas.model.Surat;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SuratRepository implements PanacheRepository<Surat> {

    /**
     * Mencari surat berdasarkan jenis DAN keyword.
     * Keyword dicari di kolom: nomorSurat, judul, pihakTerkait, keterangan.
     */
    public List<Surat> findByJenisAndKeyword(String jenis, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String like = "%" + keyword.toLowerCase() + "%";
            String query = "jenis = ?1 and (lower(nomorSurat) like ?2 or lower(judul) like ?2 or lower(pihakTerkait) like ?2 or lower(keterangan) like ?2) order by tanggal desc";
            return find(query, jenis, like).list();
        }
        return find("jenis = ?1 order by tanggal desc", jenis).list();
    }

    /**
     * Mencari surat dari SEMUA jenis berdasarkan keyword.
     * Keyword dicari di kolom: nomorSurat, judul, pihakTerkait, keterangan.
     */
    public List<Surat> findByKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String like = "%" + keyword.toLowerCase() + "%";
            String query = "lower(nomorSurat) like ?1 or lower(judul) like ?1 or lower(pihakTerkait) like ?1 or lower(keterangan) like ?1 order by tanggal desc";
            return find(query, like).list();
        }
        return find("order by tanggal desc").list();
    }
}
