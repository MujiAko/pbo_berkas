package com.pbo.berkas.resource;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pbo.berkas.model.Surat;
import com.pbo.berkas.repository.SuratRepository;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Path("/api/surat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SuratResource {

    @Inject
    SuratRepository suratRepository;

    @GET
    public List<Surat> getAll(@QueryParam("jenis") String jenis, @QueryParam("search") String search) {
        if (jenis != null && !jenis.isEmpty()) {
            return suratRepository.findByJenisAndKeyword(jenis, search);
        }
        return suratRepository.findByKeyword(search);
    }

    @POST
    @Transactional
    public Response create(Surat surat) {
        suratRepository.persist(surat);
        return Response.status(Response.Status.CREATED).entity(surat).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Surat surat) {
        Surat entity = suratRepository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Surat dengan id " + id + " tidak ditemukan.", 404);
        }
        entity.setNomorSurat(surat.getNomorSurat());
        entity.setJudul(surat.getJudul());
        entity.setJenis(surat.getJenis());
        entity.setTanggal(surat.getTanggal());
        entity.setPihakTerkait(surat.getPihakTerkait());
        entity.setKeterangan(surat.getKeterangan());
        
        suratRepository.persist(entity);
        
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Surat entity = suratRepository.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Surat dengan id " + id + " tidak ditemukan.", 404);
        }
        suratRepository.delete(entity);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/export/pdf")
    @Produces("application/pdf")
    public Response exportPdf(@QueryParam("jenis") String jenis, @QueryParam("search") String search) {
        List<Surat> listSurat;
        if (jenis != null && !jenis.isEmpty()) {
            listSurat = suratRepository.findByJenisAndKeyword(jenis, search);
        } else {
            listSurat = suratRepository.findByKeyword(search);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            String titleStr = "Laporan Data Surat " + (jenis != null ? jenis : "Keseluruhan");
            Paragraph title = new Paragraph(titleStr, titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 3f, 2f, 2f, 3f});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            String[] headers = {"No", "Nomor Surat", "Judul", "Tanggal", "Pihak Terkait", "Keterangan"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            int no = 1;
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA);
            for (Surat s : listSurat) {
                table.addCell(new Phrase(String.valueOf(no++), cellFont));
                table.addCell(new Phrase(s.getNomorSurat() != null ? s.getNomorSurat() : "", cellFont));
                table.addCell(new Phrase(s.getJudul() != null ? s.getJudul() : "", cellFont));
                table.addCell(new Phrase(s.getTanggal() != null ? s.getTanggal().toString() : "", cellFont));
                table.addCell(new Phrase(s.getPihakTerkait() != null ? s.getPihakTerkait() : "", cellFont));
                table.addCell(new Phrase(s.getKeterangan() != null ? s.getKeterangan() : "", cellFont));
            }

            document.add(table);
            document.close();

            return Response.ok(baos.toByteArray())
                    .header("Content-Disposition", "attachment; filename=\"Laporan_Surat.pdf\"")
                    .build();
        } catch (Exception e) {
            throw new WebApplicationException("Error generating PDF", e, 500);
        }
    }
    @GET
    @Path("/{id}/export/pdf")
    @Produces("application/pdf")
    public Response exportSinglePdf(@PathParam("id") Long id) {
        Surat surat = suratRepository.findById(id);
        if (surat == null) {
            throw new WebApplicationException("Surat dengan id " + id + " tidak ditemukan.", 404);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Detail Surat " + (surat.getJenis() != null ? surat.getJenis() : ""), titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(30);
            document.add(title);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 4f});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            String[][] details = {
                {"Nomor Surat", surat.getNomorSurat() != null ? surat.getNomorSurat() : "-"},
                {"Judul / Perihal", surat.getJudul() != null ? surat.getJudul() : "-"},
                {"Tanggal", surat.getTanggal() != null ? surat.getTanggal().toString() : "-"},
                {surat.getJenis() != null && surat.getJenis().equals("MASUK") ? "Pengirim" : "Penerima", surat.getPihakTerkait() != null ? surat.getPihakTerkait() : "-"},
                {"Keterangan", surat.getKeterangan() != null ? surat.getKeterangan() : "-"}
            };

            for (String[] detail : details) {
                PdfPCell headerCell = new PdfPCell(new Phrase(detail[0], headerFont));
                headerCell.setPadding(10);
                
                PdfPCell valueCell = new PdfPCell(new Phrase(detail[1], cellFont));
                valueCell.setPadding(10);

                table.addCell(headerCell);
                table.addCell(valueCell);
            }

            document.add(table);
            document.close();

            String fileName = "Surat_" + (surat.getNomorSurat() != null ? surat.getNomorSurat().replaceAll("[^a-zA-Z0-9.-]", "_") : id) + ".pdf";

            return Response.ok(baos.toByteArray())
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        } catch (Exception e) {
            throw new WebApplicationException("Error generating single PDF", e, 500);
        }
    }

    @POST
    @Path("/bulk-delete")
    @Transactional
    public Response bulkDelete(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                Surat entity = suratRepository.findById(id);
                if (entity != null) {
                    suratRepository.delete(entity);
                }
            }
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
