package com.example.tracker.filter;

import com.example.tracker.model.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TransactionSpecification {

    private TransactionSpecification(){}

    public static Specification<Transaction> hasCurrency(String currency){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("currency"), currency.toUpperCase()));
    }

    public static Specification<Transaction> hasTransactionType(String type){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get("type")),
                "%"+ type.toUpperCase() + "%"));
    }


    public static Specification<Transaction> hasTransactionStatus(String status){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get("status")),
                "%"+ status.toUpperCase() + "%"));
    }


    public static Specification<Transaction> isBetweenDates(LocalDateTime startDate, LocalDateTime endDate){
        return ((root, query, criteriaBuilder) -> { if (startDate != null && endDate != null) {
            return criteriaBuilder.between(root.get("timestamp"), startDate, endDate);
        } else if (startDate != null) {
            return criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate);
        } else if (endDate != null) {
            return criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate);
        }
            return criteriaBuilder.conjunction();});
    }

    public static Specification<Transaction> isCategory(String category){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.like(root.join("transactionGroup").get("name"), "%" + category + "%"));
    }
}
