package com.example.CuasquerMishell_Leccion1.service;

import com.example.CuasquerMishell_Leccion1.dto.SupportTicketRequest;
import com.example.CuasquerMishell_Leccion1.dto.SupportTicketResponse;
import com.example.CuasquerMishell_Leccion1.model.SupportTicket;
import com.example.CuasquerMishell_Leccion1.repository.SupportTicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class SupportTicketService {
    
    private final SupportTicketRepository repository;
    
    public SupportTicketService(SupportTicketRepository repository) {
        this.repository = repository;
    }
    
    public SupportTicketResponse createTicket(SupportTicketRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setRequesterName(request.getRequesterName());
        ticket.setStatus(request.getStatus());
        ticket.setPriority(request.getPriority());
        ticket.setCategory(request.getCategory());
        ticket.setEstimatedCost(request.getEstimatedCost());
        ticket.setCurrency(request.getCurrency());
        ticket.setDueDate(request.getDueDate());
        
        SupportTicket savedTicket = repository.save(ticket);
        return convertToResponse(savedTicket);
    }
    
    @Transactional(readOnly = true)
    public Page<SupportTicketResponse> findWithFilters(
            String q, String status, String currency, 
            BigDecimal minCost, BigDecimal maxCost,
            LocalDateTime from, LocalDateTime to,
            Pageable pageable) {
        
        Page<SupportTicket> tickets = repository.findWithFilters(
            q, 
            parseStatus(status), 
            parseCurrency(currency), 
            minCost, 
            maxCost, 
            from, 
            to, 
            pageable
        );
        
        return tickets.map(this::convertToResponse);
    }
    
    private SupportTicketResponse convertToResponse(SupportTicket ticket) {
        return new SupportTicketResponse(
            ticket.getId(),
            ticket.getTicketNumber(),
            ticket.getRequesterName(),
            ticket.getStatus(),
            ticket.getPriority(),
            ticket.getCategory(),
            ticket.getEstimatedCost(),
            ticket.getCurrency(),
            ticket.getCreatedAt(),
            ticket.getDueDate()
        );
    }
    
    private com.example.CuasquerMishell_Leccion1.model.TicketStatus parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            return com.example.CuasquerMishell_Leccion1.model.TicketStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + status + 
                ". Valores permitidos: OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED");
        }
    }
    
    private com.example.CuasquerMishell_Leccion1.model.Currency parseCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            return null;
        }
        try {
            return com.example.CuasquerMishell_Leccion1.model.Currency.valueOf(currency.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Moneda inválida: " + currency + 
                ". Valores permitidos: USD, EUR");
        }
    }
}
