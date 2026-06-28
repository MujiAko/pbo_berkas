const fs = require('fs');
let code = fs.readFileSync('c:\\project\\pbo_berkas\\Frontend\\src\\routes\\+page.svelte', 'utf8');

code = code.replace(
`    // Derived: sorted list
    $: sortedList = [...suratList].sort((a, b) => {
        let valA = a[sortField] ?? '';
        let valB = b[sortField] ?? '';
        if (sortField === 'tanggal') {
            valA = new Date(valA).getTime();
            valB = new Date(valB).getTime();
        } else {
            valA = valA.toString().toLowerCase();
            valB = valB.toString().toLowerCase();
        }
        if (valA < valB) return sortDir === 'asc' ? -1 : 1;
        if (valA > valB) return sortDir === 'asc' ? 1 : -1;
        return 0;
    });

    // Derived: paginated list
    $: totalPages = Math.max(1, Math.ceil(sortedList.length / PAGE_SIZE));
    $: pagedList = sortedList.slice((currentPage - 1) * PAGE_SIZE, currentPage * PAGE_SIZE);

    // Reset to page 1 when filters change
    $: if (suratList || searchQuery || activeTab || sortField || sortDir) {
        currentPage = 1;
    }`,
`    let totalPages = 1;
    let totalElements = 0;`
);

code = code.replace(
`    function setSort(field) {
        if (sortField === field) {
            sortDir = sortDir === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDir = field === 'tanggal' ? 'desc' : 'asc';
        }
    }`,
`    function setSort(field) {
        if (sortField === field) {
            sortDir = sortDir === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDir = field === 'tanggal' ? 'desc' : 'asc';
        }
        currentPage = 1;
        loadData();
    }`
);

code = code.replace(
`    function goToPage(p) {
        if (p >= 1 && p <= totalPages) currentPage = p;
    }`,
`    function goToPage(p) {
        if (p >= 1 && p <= totalPages) {
            currentPage = p;
            loadData();
        }
    }`
);

code = code.replace(
`            suratList = await getSuratList(jenisFilter, searchQuery, startDate, endDate);`,
`            const res = await getSuratList(jenisFilter, searchQuery, startDate, endDate, currentPage - 1, PAGE_SIZE, sortField, sortDir);
            suratList = res.content || [];
            totalPages = res.totalPages || 1;
            totalElements = res.totalElements || 0;`
);

code = code.replaceAll('pagedList', 'suratList');

code = code.replace(
`                    {(currentPage - 1) * PAGE_SIZE + 1}–{Math.min(currentPage * PAGE_SIZE, sortedList.length)} dari {sortedList.length} surat`,
`                    {(currentPage - 1) * PAGE_SIZE + 1}–{Math.min(currentPage * PAGE_SIZE, totalElements)} dari {totalElements} surat`
);

fs.writeFileSync('c:\\project\\pbo_berkas\\Frontend\\src\\routes\\+page.svelte', code);
