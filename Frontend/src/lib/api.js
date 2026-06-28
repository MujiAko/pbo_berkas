const API_URL = 'http://localhost:8080/api/surat';

export async function getSuratList(jenis = '', search = '', startDate = '', endDate = '', page = 0, size = 8, sortField = 'tanggal', sortDir = 'desc') {
    const params = new URLSearchParams();
    if (jenis) params.append('jenis', jenis);
    if (search) params.append('search', search);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    params.append('page', page);
    params.append('size', size);
    params.append('sortField', sortField);
    params.append('sortDir', sortDir);

    const url = `${API_URL}?${params.toString()}`;
    const res = await fetch(url);
    if (!res.ok) throw new Error('Failed to fetch data');
    return res.json();
}

export async function createSurat(data) {
    const res = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error('Failed to create data');
    return res.json();
}

export async function updateSurat(id, data) {
    const res = await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!res.ok) throw new Error('Failed to update data');
    return res.json();
}

export async function deleteSurat(id) {
    const res = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Failed to delete data');
    return true;
}

export async function bulkDeleteSurat(ids) {
    const res = await fetch(`${API_URL}/bulk-delete`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(ids)
    });
    if (!res.ok) throw new Error('Failed to bulk delete data');
    return true;
}

export function getExportPdfUrl(jenis = '', search = '', startDate = '', endDate = '') {
    const params = new URLSearchParams();
    if (jenis) params.append('jenis', jenis);
    if (search) params.append('search', search);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    return `${API_URL}/export/pdf?${params.toString()}`;
}

export function getExportSinglePdfUrl(id) {
    return `${API_URL}/${id}/export/pdf`;
}
