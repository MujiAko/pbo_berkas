package com.pbo.berkas.repository;

import com.pbo.berkas.model.Surat;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SuratRepository implements PanacheRepository<Surat> {

    /**
     * Mencari surat berdasarkan jenis, keyword, dan rentang tanggal.
     */
    public List<Surat> findByJenisAndKeyword(String jenis, String keyword, LocalDate startDate, LocalDate endDate) {
        StringBuilder query = new StringBuilder("jenis = :jenis");
        Map<String, Object> params = new HashMap<>();
        params.put("jenis", jenis);

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append(" and (lower(nomorSurat) like :keyword or lower(judul) like :keyword or lower(pihakTerkait) like :keyword or lower(keterangan) like :keyword)");
            params.put("keyword", "%" + keyword.toLowerCase() + "%");
        }
        if (startDate != null) {
            query.append(" and tanggal >= :startDate");
            params.put("startDate", startDate);
        }
        if (endDate != null) {
            query.append(" and tanggal <= :endDate");
            params.put("endDate", endDate);
        }

        query.append(" order by tanggal desc");
        return find(query.toString(), params).list();
    }

    /**
     * Mencari surat dari SEMUA jenis berdasarkan keyword dan rentang tanggal.
     */
    public List<Surat> findByKeyword(String keyword, LocalDate startDate, LocalDate endDate) {
        StringBuilder query = new StringBuilder("1=1");
        Map<String, Object> params = new HashMap<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append(" and (lower(nomorSurat) like :keyword or lower(judul) like :keyword or lower(pihakTerkait) like :keyword or lower(keterangan) like :keyword)");
            params.put("keyword", "%" + keyword.toLowerCase() + "%");
        }
        if (startDate != null) {
            query.append(" and tanggal >= :startDate");
            params.put("startDate", startDate);
        }
        if (endDate != null) {
            query.append(" and tanggal <= :endDate");
            params.put("endDate", endDate);
        }

        query.append(" order by tanggal desc");
        return find(query.toString(), params).list();
    }
}
