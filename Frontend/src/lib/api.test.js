import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getSuratList, createSurat, updateSurat, deleteSurat, bulkDeleteSurat } from './api';

describe('API Functions', () => {
  beforeEach(() => {
    global.fetch = vi.fn();
  });

  it('getSuratList fetches data correctly', async () => {
    const mockData = [{ id: 1, judul: 'Surat A' }];
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockData
    });

    const data = await getSuratList('MASUK', 'test');
    expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/surat?jenis=MASUK&search=test');
    expect(data).toEqual(mockData);
  });

  it('createSurat posts data correctly', async () => {
    const mockSurat = { judul: 'Surat Baru' };
    const mockResponse = { id: 2, judul: 'Surat Baru' };
    
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse
    });

    const data = await createSurat(mockSurat);
    expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/surat', expect.objectContaining({
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(mockSurat)
    }));
    expect(data).toEqual(mockResponse);
  });

  it('updateSurat puts data correctly', async () => {
    const mockSurat = { id: 1, judul: 'Surat Edit' };
    const mockResponse = { id: 1, judul: 'Surat Edit' };
    
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => mockResponse
    });

    const data = await updateSurat(1, mockSurat);
    expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/surat/1', expect.objectContaining({
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(mockSurat)
    }));
    expect(data).toEqual(mockResponse);
  });

  it('deleteSurat deletes correctly', async () => {
    global.fetch.mockResolvedValueOnce({ ok: true });
    
    await deleteSurat(1);
    expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/surat/1', expect.objectContaining({
      method: 'DELETE'
    }));
  });

  it('bulkDeleteSurat deletes multiple correctly', async () => {
    global.fetch.mockResolvedValueOnce({ ok: true });
    
    await bulkDeleteSurat([1, 2]);
    expect(global.fetch).toHaveBeenCalledWith('http://localhost:8080/api/surat/bulk-delete', expect.objectContaining({
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify([1, 2])
    }));
  });
});
