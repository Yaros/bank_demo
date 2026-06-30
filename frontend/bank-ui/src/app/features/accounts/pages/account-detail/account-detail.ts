import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

import { Transaction } from '../../../../core/models/transaction';
import { TransactionStore } from '../../../transactions/store/transaction.store';
import { AccountStore } from '../../store/account.store';

@Component({
    selector: 'app-account-detail',
    imports: [
        CommonModule,
        RouterLink,
        MatCardModule,
        MatTableModule,
        MatButtonModule,
        MatProgressSpinnerModule,
        BaseChartDirective
    ],
    templateUrl: './account-detail.html',
    styleUrl: './account-detail.scss',
})
export class AccountDetail implements OnInit {
    private router = inject(Router);
    private route = inject(ActivatedRoute);
    readonly accountStore = inject(AccountStore);
    readonly transactionStore = inject(TransactionStore);

    id!: number;

    cols = ['id', 'type', 'amount', 'balanceAfter', 'referenceId', 'createdAt'];

    page = 0;
    size = 20;

    ngOnInit(): void {
        this.id = Number(this.route.snapshot.paramMap.get('id'));
        this.accountStore.loadAccount(this.id);
        this.transactionStore.loadTransactions(this.id);
    }

    onScroll(event: any): void {
        const element = event.target;

        const atBottom =
            element.scrollHeight - element.scrollTop <= element.clientHeight + 50;

        if (atBottom) {
            this.transactionStore.loadMore();
        }
    }

    openTransaction(tx: Transaction): void {
        this.router.navigate(['/transactions', tx.id]);
    }

    // Chart configuration

    lineChartOptions: ChartOptions<'line'> = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                display: false
            }
        },
        scales: {
            x: {
                title: {
                    display: true,
                    text: 'Time'
                }
            },
            y: {
                title: {
                    display: true,
                    text: 'Balance'
                }
            }
        }
    };

    lineChartData = computed<ChartConfiguration<'line'>['data']>(() => {

        const sorted = [...this.transactionStore.transactions()]
            .sort((a, b) =>
                new Date(a.createdAt).getTime() -
                new Date(b.createdAt).getTime());

        return {
            labels: sorted.map(tx =>
                new Date(tx.createdAt).toLocaleString()),
            datasets: [
                {
                    label: 'Balance',
                    data: sorted.map(tx => tx.balanceAfter),
                    tension: 0.3
                }
            ]
        };
    });

}
