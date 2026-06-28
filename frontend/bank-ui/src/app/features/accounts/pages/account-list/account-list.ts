import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';

import { Account } from '../../../../core/models/account';
import { AccountService } from '../../../../core/services/account';

@Component({
    selector: 'app-account-list',
    imports: [
        CommonModule,
        MatCardModule
    ],
    templateUrl: './account-list.html',
    styleUrl: './account-list.scss',
})
export class AccountList implements OnInit {
    private accountService = inject(AccountService);
    private router = inject(Router);

    accounts = signal<Account[]>([]);

    ngOnInit(): void {
        this.accountService.getAccounts().subscribe(res => {
            this.accounts.set(res.content);
        });
    }

    openAccount(account: Account): void {
        this.router.navigate(['/accounts', account.id]);
    }
}
