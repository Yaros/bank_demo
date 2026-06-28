import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

import { Account } from '../../../../core/models/account';
import { Transaction } from '../../../../core/models/transaction';
import { AccountService } from '../../../../core/services/account';

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
    private accountService = inject(AccountService);

    id!: number;
    account = signal<Account | undefined>(undefined);
    transactions = signal<Transaction[]>([]);
    loadingAcc = signal(true);
    loadingTx = signal(false);

    cols = ['id', 'type', 'amount', 'balanceAfter', 'referenceId', 'createdAt'];

    page = 0;
    size = 20;
    finished = false;

    private reset(): void {
        this.page = 0;
        this.finished = false;
        this.loadingTx.set(false);
    }

    ngOnInit(): void {
        this.id = Number(this.route.snapshot.paramMap.get('id'));
        this.reset();

        this.accountService.getAccount(this.id).subscribe({
            next: (data) => {
                this.account.set(data);
                this.loadingAcc.set(false);
            },
            error: () => {
                this.loadingAcc.set(false);
            }
        });

        this.loadMore();
    }

    onScroll(event: any): void {
        const element = event.target;

        const atBottom =
            element.scrollHeight - element.scrollTop <= element.clientHeight + 50;

        if (atBottom) {
            this.loadMore();
        }
    }

    loadMore(): void {
        if (this.loadingTx() || this.finished) return;

        this.loadingTx.set(true);

        this.accountService.getTransactions(this.id, this.page, this.size)
            .subscribe(res => {

                this.transactions.update(current => [
                    ...current,
                    ...res.content
                ]);

                this.page++;

                if (res.last || res.content.length < this.size) {
                    this.finished = true;
                }

                this.loadingTx.set(false);
            });
    }

    openTransaction(tx: Transaction): void {
        this.router.navigate(['/transactions', tx.id],
            { queryParams: { accountId: this.id, currency: this.account()?.currency } }
        );
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

        const sorted = [...this.transactions()]
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
