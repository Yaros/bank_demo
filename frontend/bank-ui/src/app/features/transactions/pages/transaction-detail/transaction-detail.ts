import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { TransactionService } from '../../../../core/services/transaction';
import { TransactionStore } from '../../../transactions/store/transaction.store';

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
    readonly store = inject(TransactionStore);
    private transactionService = inject(TransactionService);

    id!: number;

    ngOnInit(): void {
        this.id = Number(this.route.snapshot.paramMap.get('id'));
        this.store.loadTransaction(this.id);
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
