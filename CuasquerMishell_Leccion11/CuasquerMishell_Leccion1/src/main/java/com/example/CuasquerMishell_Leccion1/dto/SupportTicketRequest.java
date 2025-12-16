package com.example.CuasquerMishell_Leccion1.dto;

import com.example.CuasquerMishell_Leccion1.model.TicketStatus;
import com.example.CuasquerMishell_Leccion1.model.TicketPriority;
import com.example.CuasquerMishell_Leccion1.model.Currency;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SupportTicketRequest {
    
    @NotBlank(message = "El nombre del solicitante es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String requesterName;
    
    @NotNull(message = "El estado es obligatorio")
    private TicketStatus status;
    
    @NotNull(message = "La prioridad es obligatoria")
    private TicketPriority priority;
    
    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 50, message = "La categoría no puede exceder 50 caracteres")
    private String category;
    
    @DecimalMin(value = "0.0", message = "El costo estimado debe ser mayor o igual a 0")
    private BigDecimal estimatedCost;
    
    private Currency currency;
    
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate dueDate;
    
    // Constructors
    public SupportTicketRequest() {}
    
    // Getters and Setters
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
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
