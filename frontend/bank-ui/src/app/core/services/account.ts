import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { Account } from '../models/account';
import { PageResponse } from '../models/page-response';
import { Transaction } from '../models/transaction';

@Injectable({
    providedIn: 'root',
})
export class AccountService {
    private http = inject(HttpClient);
    private readonly api = `api/accounts`;

    getAccounts(page = 0, size = 20): Observable<PageResponse<Account>> {
        return this.http.get<PageResponse<Account>>(
            `${this.api}?page=${page}&size=${size}`
        );
    }

    getAccount(id: number): Observable<Account> {
        return this.http.get<Account>(`${this.api}/${id}`);
    }

    getTransactions(accountId: number, page = 0, size = 20) {
        return this.http.get<{
            content: Transaction[];
            page: number;
            size: number;
            totalElements: number;
            totalPages: number;
            last: boolean;
        }>(
            `${this.api}/${accountId}/transactions?page=${page}&size=${size}`
        );
    }
}
