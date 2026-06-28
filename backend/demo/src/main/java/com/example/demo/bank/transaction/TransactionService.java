package com.example.demo.bank.transaction;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Currency;
import java.util.UUID;

import org.openpdf.text.Document;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.bank.account.AccountEntity;
import com.example.demo.bank.common.domain.TransactionType;
import com.example.demo.bank.common.exception.TransactionNotFoundException;
import com.example.demo.bank.transaction.dto.TransactionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public void createTransaction(AccountEntity account, TransactionType type, BigDecimal amount, String referenceId) {
        saveTransaction(account, type, amount, referenceId);
    }

    public Page<TransactionResponse> findByAccountId(Long accountId, int page, int size) {

        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId, PageRequest.of(page, size))
                .map(transactionMapper::toResponse);
    }

    public TransactionResponse getTransaction(Long transactionId) {

        return transactionMapper.toResponse(
                transactionRepository.findById(transactionId)
                        .orElseThrow(() -> new TransactionNotFoundException(transactionId)));
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public String generateReferenceId() {
        return UUID.randomUUID().toString();
    }

    public byte[] generatePdfReport(Long transactionId) {

        TransactionEntity tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        AccountEntity account = tx.getAccount();
        Currency currency = account.getCurrency();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setCurrency(currency);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();

        PdfWriter.getInstance(document, out);

        document.open();

        document.add(new Paragraph("--- Demo bank ---"));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Transaction Report"));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Transaction ID: " + tx.getId()));
        document.add(new Paragraph("Currency: " + currency));
        document.add(new Paragraph("Type: " + tx.getType()));
        document.add(new Paragraph("Amount: " + nf.format(tx.getAmount())));
        document.add(new Paragraph("Balance After: " + nf.format(tx.getBalanceAfter())));
        if (tx.getReferenceId() != null) {
            document.add(new Paragraph("ReferenceId: " + tx.getReferenceId()));
        }
        document.add(new Paragraph("Date: " + tx.getCreatedAt()));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Generated: " + Instant.now()));

        document.close();

        return out.toByteArray();
    }

    private void saveTransaction(AccountEntity account, TransactionType type, BigDecimal amount, String referenceId) {

        TransactionEntity transaction = TransactionEntity.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .referenceId(referenceId)
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(transaction);

        log.info(
                "Transaction {} created: {} {} on account {}",
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                account.getId());
    }
}
