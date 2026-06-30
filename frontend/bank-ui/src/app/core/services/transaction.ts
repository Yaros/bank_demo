import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { TransactionDetail } from '../models/transaction-detail';

@Injectable({
    providedIn: 'root',
})
export class TransactionService {
    private http = inject(HttpClient);
    private readonly api = `api/transactions`;

    getTransactionDetail(id: number): Observable<TransactionDetail> {
        return this.http.get<TransactionDetail>(
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
