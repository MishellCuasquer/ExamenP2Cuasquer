package com.example.CuasquerMishell_Leccion1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "El número de ticket es obligatorio")
    @Pattern(regexp = "^ST-\\d{4}-\\d{6}$", message = "Formato de ticket inválido. Ejemplo: ST-2025-000145")
    private String ticketNumber;
    
    @Column(nullable = false)
    @NotBlank(message = "El nombre del solicitante es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String requesterName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketPriority priority;
    
    @Column(nullable = false)
    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String category;
    
    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "El costo estimado debe ser mayor o igual a 0")
    private BigDecimal estimatedCost;
    
    @Enumerated(EnumType.STRING)
    private Currency currency;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDate dueDate;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (ticketNumber == null || ticketNumber.isEmpty()) {
            ticketNumber = generateTicketNumber();
        }
    }
    
    private String generateTicketNumber() {
        int year = java.time.Year.now().getValue();
        long sequence = System.currentTimeMillis() % 1000000;
        return String.format("ST-%d-%06d", year, sequence);
    }
    
    // Constructors
    public SupportTicket() {}
    
    public SupportTicket(String ticketNumber, String requesterName, TicketStatus status, 
                        TicketPriority priority, String category, BigDecimal estimatedCost, 
                        Currency currency, LocalDate dueDate) {
        this.ticketNumber = ticketNumber;
        this.requesterName = requesterName;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.estimatedCost = estimatedCost;
        this.currency = currency;
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    
    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }
    
    public TicketPriority getPriority() { return priority; }
    public void setPriority(TicketPriority priority) { this.priority = priority; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    
    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
