import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Transaction } from '../../../../core/models/transaction';
import { TransactionService } from '../../../../core/services/transaction';

@Component({
    selector: 'app-transaction-detail',
    imports: [
        CommonModule,
        RouterLink,
        MatCardModule,
        MatButtonModule,
        MatProgressSpinnerModule
    ],
    templateUrl: './transaction-detail.html',
    styleUrl: './transaction-detail.scss',
})
export class TransactionDetail {

    private route = inject(ActivatedRoute);
    private transactionService = inject(TransactionService);

    accountId = this.route.snapshot.queryParamMap.get('accountId')!;
    currency = this.route.snapshot.queryParamMap.get('currency')!;

    transaction = signal<Transaction | null>(null);
    loading = signal(true);

    id!: number;

    ngOnInit(): void {

        this.id = Number(this.route.snapshot.paramMap.get('id'));

        this.transactionService.getTransaction(this.id)
            .subscribe(tx => {
                this.transaction.set(tx);
                this.loading.set(false);
            });
    }

    downloadPdf(): void {
        this.transactionService
            .downloadReport(this.id)
            .subscribe(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');

                a.href = url;
                a.download = `transaction-${this.id}.pdf`;
                a.click();

                URL.revokeObjectURL(url);
            });
    }
}
