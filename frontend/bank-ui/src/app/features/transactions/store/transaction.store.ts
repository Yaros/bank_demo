import { inject } from '@angular/core';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { firstValueFrom } from 'rxjs';

import { Transaction } from '../../../core/models/transaction';
import { TransactionDetail } from '../../../core/models/transaction-detail';
import { AccountService } from '../../../core/services/account';
import { TransactionService } from '../../../core/services/transaction';

interface TransactionState {
    transactions: Transaction[];
    selectedTransactionDetail: TransactionDetail | null;

    accountId: number | null;
    page: number;
    size: number;

    loading: boolean;
    finished: boolean;
    error: string | null;
}

const initialState: TransactionState = {
    transactions: [],
    selectedTransactionDetail: null,

    accountId: null,
    page: 0,
    size: 20,

    loading: false,
    finished: false,
    error: null
};

export const TransactionStore = signalStore(
    { providedIn: 'root' },

    withState(initialState),

    withMethods((store, accountService = inject(AccountService), transactionService = inject(TransactionService)) => ({

        async loadTransactions(accountId: number) {
            patchState(store, {
                transactions: [],
                accountId: accountId,
                page: 0,
                loading: true,
                finished: false,
                error: null
            });

            try {
                const pageData = await firstValueFrom(
                    accountService.getTransactions(store.accountId()!, store.page(), store.size())
                );
                patchState(store, {
                    transactions: pageData.content,
                    loading: false,
                    page: store.page() + 1
                });
            } catch (e) {
                patchState(store, {
                    loading: false,
                    error: 'Unable to load transactions'
                });
            }
        },

        async loadMore() {
            if (store.loading() || store.finished()) return;

            patchState(store, {
                loading: true,
                error: null
            });

            try {
                const pageData = await firstValueFrom(
                    accountService.getTransactions(store.accountId()!, store.page(), store.size())
                );

                patchState(store, {
                    transactions: [...store.transactions(), ...pageData.content],
                    loading: false,
                    page: store.page() + 1
                });

                if (pageData.last || pageData.content.length < store.size()) {
                    patchState(store, {
                        finished: true
                    });
                }
            } catch (e) {
                patchState(store, {
                    loading: false,
                    error: 'Unable to load transactions'
                });
            }
        },

        async loadTransaction(id: number) {

            patchState(store, {
                loading: true,
                error: null
            });

            try {
                const transaction = await firstValueFrom(
                    transactionService.getTransactionDetail(id)
                );
                patchState(store, {
                    selectedTransactionDetail: transaction,
                    loading: false
                });
            } catch (e) {
                patchState(store, {
                    selectedTransactionDetail: null,
                    loading: false,
                    error: 'Unable to load transaction'
                });
            }
        },
    }))
);
