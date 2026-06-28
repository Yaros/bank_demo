import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Transaction } from '../models/transaction';

@Injectable({
    providedIn: 'root',
})
export class TransactionService {
    private http = inject(HttpClient);
    private readonly api = `api/transactions`;

    getTransaction(id: number): Observable<Transaction> {
        return this.http.get<Transaction>(
            `${this.api}/${id}`
        );
    }

    downloadReport(id: number): Observable<Blob> {
        return this.http.get(
            `${this.api}/${id}/report`,
            { responseType: 'blob' }
        );
    }
}
