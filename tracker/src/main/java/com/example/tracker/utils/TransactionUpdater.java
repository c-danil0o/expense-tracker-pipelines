package com.example.tracker.utils;

import com.example.tracker.model.Reminder;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionStatus;
import com.example.tracker.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@AllArgsConstructor
public class TransactionUpdater {
    private final TransactionRepository transactionRepository;
    private final EntityManager entityManager;

    @Scheduled(cron = "0 */10 * ? * *")
    public void checkForTransactionStatusUpdates(){
        List<Transaction> scheduledTransactions = this.transactionRepository.findPendingTransactions();
        for (Transaction tr: scheduledTransactions){
            entityManager.detach(tr);
            this.transactionRepository.delete(tr);
            tr.setStatus(TransactionStatus.Done);
            tr.setId(null);
            this.transactionRepository.save(tr);
        }
    }
}
