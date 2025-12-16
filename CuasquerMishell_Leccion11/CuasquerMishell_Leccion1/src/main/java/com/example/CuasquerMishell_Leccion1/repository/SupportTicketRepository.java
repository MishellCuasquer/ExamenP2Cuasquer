package com.example.CuasquerMishell_Leccion1.repository;

import com.example.CuasquerMishell_Leccion1.model.SupportTicket;
import com.example.CuasquerMishell_Leccion1.model.TicketStatus;
import com.example.CuasquerMishell_Leccion1.model.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    
    @Query("SELECT t FROM SupportTicket t WHERE " +
           "(:q IS NULL OR :q = '' OR LOWER(t.ticketNumber) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(t.requesterName) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:currency IS NULL OR t.currency = :currency) AND " +
           "(:minCost IS NULL OR t.estimatedCost >= :minCost) AND " +
           "(:maxCost IS NULL OR t.estimatedCost <= :maxCost) AND " +
           "(:from IS NULL OR t.createdAt >= :from) AND " +
           "(:to IS NULL OR t.createdAt <= :to)")
    Page<SupportTicket> findWithFilters(
        @Param("q") String q,
        @Param("status") TicketStatus status,
        @Param("currency") Currency currency,
        @Param("minCost") BigDecimal minCost,
        @Param("maxCost") BigDecimal maxCost,
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to,
        Pageable pageable
    );
    
    boolean existsByTicketNumber(String ticketNumber);
}
