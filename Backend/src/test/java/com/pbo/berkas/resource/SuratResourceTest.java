package com.pbo.berkas.resource;

import com.pbo.berkas.model.Surat;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class SuratResourceTest {

    @Test
    public void testGetAllEndpoint() {
        given()
          .when().get("/api/surat")
          .then()
             .statusCode(200)
             .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testCreateSuratEndpoint() {
        Surat surat = new Surat();
        surat.setNomorSurat("TEST-001");
        surat.setJudul("Surat Uji Coba");
        surat.setJenis("MASUK");
        surat.setPihakTerkait("Penguji");
        surat.setTanggal(LocalDate.now());

        given()
          .contentType(ContentType.JSON)
          .body(surat)
          .when().post("/api/surat")
          .then()
             .statusCode(201)
             .body("nomorSurat", equalTo("TEST-001"))
             .body("judul", equalTo("Surat Uji Coba"));
    }

    @Test
    public void testExportPdfEndpoint() {
        given()
          .when().get("/api/surat/export/pdf")
          .then()
             .statusCode(200)
             .contentType("application/pdf");
    }

    @Test
    public void testFilterByDateRange() {
        given()
          .queryParam("startDate", "2024-01-01")
          .queryParam("endDate", "2026-12-31")
          .when().get("/api/surat")
          .then()
             .statusCode(200);
    }
}
