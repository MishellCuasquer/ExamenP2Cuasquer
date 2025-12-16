class SupportTicketsApp {
    constructor() {
        this.API_BASE_URL = 'http://localhost:3001/api/v1/support-tickets';
        this.currentPage = 0;
        this.totalPages = 0;
        this.totalElements = 0;
        
        this.init();
    }

    init() {
        this.bindEvents();
        this.checkApiStatus();
        this.loadTickets();
    }

    bindEvents() {
        // Form submission
        document.getElementById('ticketForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.createTicket();
        });

        // Filter form
        document.getElementById('filterForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.currentPage = 0;
            this.loadTickets();
        });

        // Clear filters
        document.getElementById('clearFilters').addEventListener('submit', (e) => {
            e.preventDefault();
            this.clearFilters();
        });

        // Refresh list
        document.getElementById('refreshList').addEventListener('click', () => {
            this.loadTickets();
        });

        // Tab changes
        document.getElementById('list-tab').addEventListener('shown.bs.tab', () => {
            this.loadTickets();
        });

        // Form validation
        this.setupFormValidation();
    }

    setupFormValidation() {
        const form = document.getElementById('ticketForm');
        const inputs = form.querySelectorAll('input, select');
        
        inputs.forEach(input => {
            input.addEventListener('blur', () => {
                this.validateField(input);
            });
        });
    }

    validateField(field) {
        const value = field.value.trim();
        let isValid = true;

        field.classList.remove('is-invalid', 'is-valid');

        if (field.hasAttribute('required') && !value) {
            isValid = false;
        }

        if (field.id === 'requesterName') {
            if (value.length < 2 || value.length > 100) {
                isValid = false;
            }
        }

        if (field.id === 'category') {
            if (value.length > 50) {
                isValid = false;
            }
        }

        if (field.id === 'estimatedCost') {
            const num = parseFloat(value);
            if (value && (isNaN(num) || num < 0)) {
                isValid = false;
            }
        }

        if (field.id === 'dueDate') {
            if (value) {
                const selectedDate = new Date(value);
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                if (selectedDate < today) {
                    isValid = false;
                    field.setCustomValidity('La fecha límite no puede ser anterior a hoy');
                } else {
                    field.setCustomValidity('');
                }
            }
        }

        field.classList.toggle('is-valid', isValid && value);
        field.classList.toggle('is-invalid', !isValid);

        return isValid;
    }

    async checkApiStatus() {
        const statusElement = document.getElementById('api-status');
        
        try {
            const response = await fetch(`${this.API_BASE_URL}?page=0&size=1`);
            if (response.ok) {
                statusElement.textContent = 'En línea';
                statusElement.className = 'api-status-online';
            } else {
                throw new Error('API no responde correctamente');
            }
        } catch (error) {
            statusElement.textContent = 'Desconectado';
            statusElement.className = 'api-status-offline';
            console.error('Error checking API status:', error);
        }
    }

    async createTicket() {
        const form = document.getElementById('ticketForm');
        
        if (!this.validateForm(form)) {
            this.showError('Por favor, corrija los errores en el formulario');
            return;
        }

        const ticketData = this.getFormData();
        
        try {
            this.showLoading(true);
            
            const response = await fetch(this.API_BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(ticketData)
            });

            if (response.ok) {
                const createdTicket = await response.json();
                this.showSuccess(`Ticket creado exitosamente: ${createdTicket.ticketNumber}`);
                form.reset();
                form.querySelectorAll('.is-valid, .is-invalid').forEach(el => {
                    el.classList.remove('is-valid', 'is-invalid');
                });
                
                // Switch to list tab
                const listTab = new bootstrap.Tab(document.getElementById('list-tab'));
                listTab.show();
            } else {
                const errorData = await response.json();
                this.showError(errorData.message || 'Error al crear el ticket');
            }
        } catch (error) {
            console.error('Error creating ticket:', error);
            this.showError('Error de conexión al crear el ticket');
        } finally {
            this.showLoading(false);
        }
    }

    validateForm(form) {
        const inputs = form.querySelectorAll('input[required], select[required]');
        let isValid = true;

        inputs.forEach(input => {
            if (!this.validateField(input)) {
                isValid = false;
            }
        });

        return isValid;
    }

    getFormData() {
        return {
            requesterName: document.getElementById('requesterName').value.trim(),
            category: document.getElementById('category').value.trim(),
            status: document.getElementById('status').value,
            priority: document.getElementById('priority').value,
            currency: document.getElementById('currency').value || null,
            estimatedCost: document.getElementById('estimatedCost').value ? 
                parseFloat(document.getElementById('estimatedCost').value) : null,
            dueDate: document.getElementById('dueDate').value ? 
                document.getElementById('dueDate').value : null
        };
    }

    async loadTickets() {
        try {
            this.showLoading(true);
            
            const params = this.buildQueryParams();
            const url = `${this.API_BASE_URL}?${params}`;
            
            const response = await fetch(url);
            
            if (response.ok) {
                const data = await response.json();
                this.renderTickets(data.content);
                this.updatePagination(data);
                this.updateTotalCount(data.totalElements);
            } else {
                const errorData = await response.json();
                this.showError(errorData.message || 'Error al cargar los tickets');
                this.renderEmptyState();
            }
        } catch (error) {
            console.error('Error loading tickets:', error);
            this.showError('Error de conexión al cargar los tickets');
            this.renderEmptyState();
        } finally {
            this.showLoading(false);
        }
    }

    buildQueryParams() {
        const params = new URLSearchParams();
        
        // Search
        const searchQuery = document.getElementById('searchQuery').value.trim();
        if (searchQuery) params.append('q', searchQuery);
        
        // Filters
        const status = document.getElementById('filterStatus').value;
        if (status) params.append('status', status);
        
        const currency = document.getElementById('filterCurrency').value;
        if (currency) params.append('currency', currency);
        
        const minCost = document.getElementById('minCost').value;
        if (minCost) params.append('minCost', parseFloat(minCost));
        
        const maxCost = document.getElementById('maxCost').value;
        if (maxCost) params.append('maxCost', parseFloat(maxCost));
        
        const fromDate = document.getElementById('fromDate').value;
        if (fromDate) params.append('from', fromDate);
        
        const toDate = document.getElementById('toDate').value;
        if (toDate) params.append('to', toDate);
        
        // Pagination
        params.append('page', this.currentPage);
        params.append('size', document.getElementById('pageSize').value);
        
        // Sorting
        params.append('sort', document.getElementById('sortBy').value);
        
        return params.toString();
    }

    renderTickets(tickets) {
        const container = document.getElementById('ticketsList');
        
        if (tickets.length === 0) {
            this.renderEmptyState();
            return;
        }
        
        const ticketsHtml = tickets.map(ticket => this.createTicketCard(ticket)).join('');
        container.innerHTML = ticketsHtml;
    }

    createTicketCard(ticket) {
        const createdDate = new Date(ticket.createdAt).toLocaleString('es-ES');
        const dueDate = ticket.dueDate ? new Date(ticket.dueDate).toLocaleDateString('es-ES') : 'No definida';
        
        return `
            <div class="ticket-card card mb-3 status-${ticket.status}">
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-8">
                            <h6 class="card-title mb-2">
                                <span class="ticket-number">${ticket.ticketNumber}</span>
                                <span class="ms-2">${ticket.requesterName}</span>
                            </h6>
                            <p class="card-text">
                                <strong>Categoría:</strong> ${ticket.category}<br>
                                <small class="ticket-meta">
                                    <i class="bi bi-calendar"></i> Creado: ${createdDate}<br>
                                    <i class="bi bi-calendar-event"></i> Límite: ${dueDate}
                                </small>
                            </p>
                        </div>
                        <div class="col-md-4 text-end">
                            <div class="mb-2">
                                <span class="badge status-badge bg-${this.getStatusColor(ticket.status)}">
                                    ${this.getStatusText(ticket.status)}
                                </span>
                            </div>
                            <div class="mb-2">
                                <span class="badge priority-badge priority-${ticket.priority}">
                                    ${this.getPriorityText(ticket.priority)}
                                </span>
                            </div>
                            ${ticket.currency && ticket.estimatedCost ? `
                                <div class="mb-2">
                                    <span class="badge currency-badge">
                                        ${ticket.currency} $${ticket.estimatedCost.toFixed(2)}
                                    </span>
                                </div>
                            ` : ''}
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    renderEmptyState() {
        const container = document.getElementById('ticketsList');
        container.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-inbox"></i>
                <h5>No se encontraron tickets</h5>
                <p>No hay tickets que coincidan con los criterios de búsqueda.</p>
            </div>
        `;
    }

    updatePagination(data) {
        this.currentPage = data.number;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        
        const paginationContainer = document.getElementById('paginationContainer');
        const pagination = document.getElementById('pagination');
        
        if (this.totalPages <= 1) {
            paginationContainer.style.display = 'none';
            return;
        }
        
        paginationContainer.style.display = 'block';
        
        let paginationHtml = '';
        
        // Previous
        paginationHtml += `
            <li class="page-item ${this.currentPage === 0 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${this.currentPage - 1}">Anterior</a>
            </li>
        `;
        
        // Page numbers
        const startPage = Math.max(0, this.currentPage - 2);
        const endPage = Math.min(this.totalPages - 1, this.currentPage + 2);
        
        if (startPage > 0) {
            paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" data-page="0">1</a>
                </li>
            `;
            if (startPage > 1) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
        }
        
        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += `
                <li class="page-item ${i === this.currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>
            `;
        }
        
        if (endPage < this.totalPages - 1) {
            if (endPage < this.totalPages - 2) {
                paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
            }
            paginationHtml += `
                <li class="page-item">
                    <a class="page-link" href="#" data-page="${this.totalPages - 1}">${this.totalPages}</a>
                </li>
            `;
        }
        
        // Next
        paginationHtml += `
            <li class="page-item ${this.currentPage === this.totalPages - 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${this.currentPage + 1}">Siguiente</a>
            </li>
        `;
        
        pagination.innerHTML = paginationHtml;
        
        // Bind pagination events
        pagination.querySelectorAll('.page-link:not(.disabled)').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = parseInt(e.target.dataset.page);
                if (!isNaN(page) && page >= 0 && page < this.totalPages) {
                    this.currentPage = page;
                    this.loadTickets();
                }
            });
        });
    }

    updateTotalCount(total) {
        document.getElementById('totalCount').textContent = 
            `${total} ticket${total !== 1 ? 's' : ''}`;
    }

    clearFilters() {
        document.getElementById('filterForm').reset();
        this.currentPage = 0;
        this.loadTickets();
    }

    getStatusColor(status) {
        const colors = {
            'OPEN': 'info',
            'IN_PROGRESS': 'warning',
            'RESOLVED': 'success',
            'CLOSED': 'secondary',
            'CANCELLED': 'danger'
        };
        return colors[status] || 'secondary';
    }

    getStatusText(status) {
        const texts = {
            'OPEN': 'Abierto',
            'IN_PROGRESS': 'En Progreso',
            'RESOLVED': 'Resuelto',
            'CLOSED': 'Cerrado',
            'CANCELLED': 'Cancelado'
        };
        return texts[status] || status;
    }

    getPriorityText(priority) {
        const texts = {
            'LOW': 'Baja',
            'MEDIUM': 'Media',
            'HIGH': 'Alta',
            'CRITICAL': 'Crítica'
        };
        return texts[priority] || priority;
    }

    showLoading(show) {
        const spinner = document.getElementById('loadingSpinner');
        spinner.style.display = show ? 'block' : 'none';
    }

    showSuccess(message) {
        document.getElementById('successMessage').textContent = message;
        const modal = new bootstrap.Modal(document.getElementById('successModal'));
        modal.show();
    }

    showError(message) {
        document.getElementById('errorMessage').textContent = message;
        const modal = new bootstrap.Modal(document.getElementById('errorModal'));
        modal.show();
    }
}

// Initialize app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new SupportTicketsApp();
});
