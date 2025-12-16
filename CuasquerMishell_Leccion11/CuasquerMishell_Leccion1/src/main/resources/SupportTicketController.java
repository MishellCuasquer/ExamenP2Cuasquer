package com.example.CuasquerMishell_Leccion1.controller;

import com.example.CuasquerMishell_Leccion1.dto.SupportTicketRequest;
import com.example.CuasquerMishell_Leccion1.dto.SupportTicketResponse;
import com.example.CuasquerMishell_Leccion1.model.TicketStatus;
import com.example.CuasquerMishell_Leccion1.model.Currency;
import com.example.CuasquerMishell_Leccion1.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api/v1/support-tickets")
public class SupportTicketController {
    
    private final SupportTicketService service;
    
    public SupportTicketController(SupportTicketService service) {
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<SupportTicketResponse> createTicket(@Valid @RequestBody SupportTicketRequest request) {
        SupportTicketResponse response = service.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<Page<SupportTicketResponse>> getTickets(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) BigDecimal minCost,
            @RequestParam(required = false) BigDecimal maxCost,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        
        // Normalizar q: si está vacío, tratarlo como null
        if (q != null && q.trim().isEmpty()) {
            q = null;
        }
        
        // Validaciones
        validateFilters(minCost, maxCost, from, to, status, currency);
        
        // Parsear y crear Sort
        Sort sortObj = parseSort(sort);
        PageRequest pageRequest = PageRequest.of(page, size, sortObj);
        
        Page<SupportTicketResponse> tickets = service.findWithFilters(
            q, status, currency, minCost, maxCost, from, to, pageRequest);
        
        return ResponseEntity.ok(tickets);
    }
    
    private void validateFilters(BigDecimal minCost, BigDecimal maxCost, LocalDateTime from, LocalDateTime to, String status, String currency) {
        if (minCost != null && minCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("minCost debe ser mayor o igual a 0");
        }
        
        if (maxCost != null && maxCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("maxCost debe ser mayor o igual a 0");
        }
        
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("La fecha 'from' debe ser anterior o igual a la fecha 'to'");
        }
        
        // Validar status
        if (status != null) {
            try {
                TicketStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Estado inválido. Valores permitidos: OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED");
            }
        }
        
        // Validar currency
        if (currency != null) {
            try {
                Currency.valueOf(currency.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Moneda inválida. Valores permitidos: USD, EUR");
            }
        }
    }
    
    private Sort parseSort(String sort) {
        String[] sortParams = sort.split(",");
        if (sortParams.length != 2) {
            throw new IllegalArgumentException("Formato de sort inválido. Use: campo,direccion (ej: createdAt,desc)");
        }
        
        String field = sortParams[0];
        String direction = sortParams[1].toLowerCase();
        
        // Validar campo
        if (!field.equals("id") && !field.equals("ticketNumber") && 
            !field.equals("requesterName") && !field.equals("status") && 
            !field.equals("priority") && !field.equals("category") && 
            !field.equals("estimatedCost") && !field.equals("currency") && 
            !field.equals("createdAt") && !field.equals("dueDate")) {
            throw new IllegalArgumentException("Campo de ordenación inválido: " + field);
        }
        
        Sort.Direction sortDirection;
        if (direction.equals("asc")) {
            sortDirection = Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new IllegalArgumentException("Dirección de ordenación inválida: " + direction + 
                ". Use 'asc' o 'desc'");
        }
        
        return Sort.by(sortDirection, field);
    }
}
