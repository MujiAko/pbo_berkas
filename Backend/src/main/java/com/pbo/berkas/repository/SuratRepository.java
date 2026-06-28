package com.pbo.berkas.repository;

import com.pbo.berkas.model.Surat;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import com.pbo.berkas.model.PagedResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ApplicationScoped
public class SuratRepository implements PanacheRepository<Surat> {

    /**
     * Mencari surat berdasarkan jenis, keyword, dan rentang tanggal (dengan pagination).
     */
    public PagedResponse<Surat> findByJenisAndKeyword(String jenis, String keyword, LocalDate startDate, LocalDate endDate, int pageIndex, int pageSize, String sortField, String sortDir) {
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

        sortField = validateSortField(sortField);
        sortDir = validateSortDir(sortDir);
        query.append(" order by ").append(sortField).append(" ").append(sortDir).append(", id ").append(sortDir);

        PanacheQuery<Surat> pQuery = find(query.toString(), params).page(pageIndex, pageSize);
        return new PagedResponse<>(pQuery.list(), pQuery.count(), pQuery.pageCount(), pageIndex);
    }

    /**
     * Mencari surat dari SEMUA jenis berdasarkan keyword dan rentang tanggal (dengan pagination).
     */
    public PagedResponse<Surat> findByKeyword(String keyword, LocalDate startDate, LocalDate endDate, int pageIndex, int pageSize, String sortField, String sortDir) {
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

        sortField = validateSortField(sortField);
        sortDir = validateSortDir(sortDir);
        query.append(" order by ").append(sortField).append(" ").append(sortDir).append(", id ").append(sortDir);

        PanacheQuery<Surat> pQuery = find(query.toString(), params).page(pageIndex, pageSize);
        return new PagedResponse<>(pQuery.list(), pQuery.count(), pQuery.pageCount(), pageIndex);
    }

    private String validateSortField(String sortField) {
        if (sortField == null || sortField.trim().isEmpty()) return "tanggal";
        List<String> allowed = List.of("tanggal", "nomorSurat", "judul", "pihakTerkait");
        return allowed.contains(sortField) ? sortField : "tanggal";
    }

    private String validateSortDir(String sortDir) {
        if (sortDir != null && sortDir.equalsIgnoreCase("asc")) return "asc";
        return "desc";
    }
}
