import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Router } from '@angular/router';

import { Account } from '../../../../core/models/account';
import { AccountStore } from '../../store/account.store';

@Component({
    selector: 'app-account-list',
    imports: [
        CommonModule,
        MatCardModule,
        MatProgressSpinnerModule
    ],
    templateUrl: './account-list.html',
    styleUrl: './account-list.scss',
})
export class AccountList implements OnInit {
    readonly store = inject(AccountStore);
    private router = inject(Router);

    ngOnInit(): void {
        this.store.loadAccounts();
    }

    openAccount(account: Account): void {
        this.store.selectAccount(account);
        this.router.navigate(['/accounts', account.id]);
    }
}
