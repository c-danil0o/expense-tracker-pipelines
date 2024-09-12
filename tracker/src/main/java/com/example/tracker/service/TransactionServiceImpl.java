package com.example.tracker.service;

import com.example.tracker.dto.TransactionDTO;
import com.example.tracker.dto.TransactionGroupDTO;
import com.example.tracker.exceptions.ElementNotFoundException;
import com.example.tracker.exceptions.TransactionGroupAlreadyExistsException;
import com.example.tracker.exceptions.TransactionGroupNotFoundException;
import com.example.tracker.mapper.TransactionMapper;
import com.example.tracker.model.Transaction;
import com.example.tracker.model.TransactionGroup;
import com.example.tracker.model.User;
import com.example.tracker.repository.TransactionGroupRepository;
import com.example.tracker.repository.TransactionRepository;
import com.example.tracker.service.interfaces.TransactionService;
import com.example.tracker.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionGroupRepository transactionGroupRepository;
    private final TransactionMapper transactionMapper;
    private final UserService userService;


    public TransactionGroupDTO createGroup(TransactionGroupDTO transactionGroupDTO) throws TransactionGroupAlreadyExistsException {
        if (transactionGroupDTO.getUserId() == null){
            if (this.transactionGroupRepository.getByName(transactionGroupDTO.getName()) != null){
                 throw new TransactionGroupAlreadyExistsException("Group with given name already exists!");
            }
        }else{
            if (this.transactionGroupRepository.getByUserId(transactionGroupDTO.getUserId()).stream().anyMatch(group1 -> group1.getName().equalsIgnoreCase(transactionGroupDTO.getName()))){
                throw new TransactionGroupAlreadyExistsException("Group with given name already exists!");
            }
        }
        TransactionGroup savedTransactionGroup = this.transactionGroupRepository.save(this.transactionMapper.fromTransactionGroupDTO(transactionGroupDTO));
        return  this.transactionMapper.toTransactionGroupDTO(savedTransactionGroup);
    }

    @Override
    public TransactionGroupDTO getGroupById(Long id) throws TransactionGroupNotFoundException {
        return this.transactionMapper.toTransactionGroupDTO(this.transactionGroupRepository.findById(id).orElseThrow(() -> new TransactionGroupNotFoundException("Transaction group with given id doesn't exist!")));
    }

    @Override
    public List<TransactionDTO> findAll() {
        return this.transactionRepository.findAll().stream().map(this.transactionMapper::toTransactionDTO).toList();
    }

    @Override
    public TransactionDTO findById(Long transactionId) throws ElementNotFoundException {
        return this.transactionMapper.toTransactionDTO(this.transactionRepository.findById(transactionId).orElseThrow(() -> new ElementNotFoundException("No such element with given id!")));
    }

    @Override
    public TransactionDTO save(TransactionDTO transactionDTO) {
        User user = this.userService.findEntityById(transactionDTO.getUserId());
        TransactionGroup transactionGroup = this.transactionGroupRepository.findById(transactionDTO.getTransactionGroupId())
                .orElseThrow(() -> new TransactionGroupNotFoundException("Transaction group with given id doesn't exist!"));
        Transaction savedTransaction = this.transactionRepository.save(this.transactionMapper.fromTransactionDTO(transactionDTO, user, transactionGroup));
        return this.transactionMapper.toTransactionDTO(savedTransaction);
    }

    @Override
    public TransactionDTO update(TransactionDTO newTransaction) throws ElementNotFoundException {
        User user = this.userService.findEntityById(newTransaction.getUserId());
        TransactionGroup transactionGroup = this.transactionGroupRepository.findById(newTransaction.getTransactionGroupId())
                .orElseThrow(() -> new TransactionGroupNotFoundException("Transaction group with given id doesn't exist!"));
        if (!this.transactionRepository.existsById(newTransaction.getId()))
            throw new ElementNotFoundException("Transaction with given id doesn't exist!");
        Transaction transaction = this.transactionMapper.fromTransactionDTO(newTransaction, user, transactionGroup);
        transaction.setId(newTransaction.getId());
        Transaction savedTransaction = this.transactionRepository.save(transaction);
        return this.transactionMapper.toTransactionDTO(savedTransaction);
    }

    @Override
    public void delete(Long transactionId) throws ElementNotFoundException {
        if (this.transactionRepository.existsById(transactionId)){
            this.transactionRepository.deleteById(transactionId);
        }else{
            throw new ElementNotFoundException("No such element with given ID!");
        }


    }
}
