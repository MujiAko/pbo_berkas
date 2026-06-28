import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, fireEvent, screen } from '@testing-library/svelte';
import SuratForm from './SuratForm.svelte';
import * as api from '../api';

vi.mock('../api', () => ({
  createSurat: vi.fn(),
  updateSurat: vi.fn()
}));

describe('SuratForm Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders form when isOpen is true', () => {
    render(SuratForm, { isOpen: true });
    expect(screen.getByText('Tambah Surat Baru')).toBeInTheDocument();
  });

  it('does not render when isOpen is false', () => {
    const { queryByText } = render(SuratForm, { isOpen: false });
    expect(queryByText('Tambah Surat Baru')).not.toBeInTheDocument();
  });

  it('submits form correctly for new data', async () => {
    const { getByLabelText, getByText, getByRole } = render(SuratForm, { isOpen: true });
    
    const nomorInput = getByLabelText('Nomor Surat');
    const judulInput = getByLabelText('Judul Surat');
    const pihakInput = getByLabelText('Pengirim');
    
    await fireEvent.input(nomorInput, { target: { value: 'TEST-123' } });
    await fireEvent.input(judulInput, { target: { value: 'Judul Test' } });
    await fireEvent.input(pihakInput, { target: { value: 'Pihak Test' } });
    
    const submitBtn = getByText('Simpan Data');
    await fireEvent.click(submitBtn);

    expect(api.createSurat).toHaveBeenCalled();
    const callArgs = api.createSurat.mock.calls[0][0];
    expect(callArgs.nomorSurat).toBe('TEST-123');
    expect(callArgs.judul).toBe('Judul Test');
    expect(callArgs.pihakTerkait).toBe('Pihak Test');
  });
});
