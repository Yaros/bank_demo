import { inject } from '@angular/core';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { firstValueFrom } from 'rxjs';

import { Account } from '../../../core/models/account';
import { AccountService } from '../../../core/services/account';

interface AccountState {
    accounts: Account[];
    selectedAccount: Account | null;
    loading: boolean;
    error: string | null;
}

const initialState: AccountState = {
    accounts: [],
    selectedAccount: null,
    loading: false,
    error: null
};

export const AccountStore = signalStore(
    { providedIn: 'root' },

    withState(initialState),

    withMethods((store, accountService = inject(AccountService)) => ({
        async loadAccounts() {

            patchState(store, {
                selectedAccount: null,
                loading: true,
                error: null
            });

            try {
                const page = await firstValueFrom(
                    accountService.getAccounts()
                );
                patchState(store, {
                    accounts: page.content,
                    loading: false
                });
            } catch (e) {
                patchState(store, {
                    loading: false,
                    error: 'Unable to load accounts'
                });
            }
        },

        async loadAccount(id: number) {
            if (store.selectedAccount()?.id === id) {
                return;
            }

            const existing = store.accounts().find(a => a.id === id);

            if (existing) {
                patchState(store, {
                    selectedAccount: existing
                });
                return;
            }

            patchState(store, {
                loading: true,
                error: null
            });

            try {
                const account = await firstValueFrom(
                    accountService.getAccount(id)
                );
                patchState(store, {
                    selectedAccount: account,
                    loading: false
                });
            } catch (e) {
                patchState(store, {
                    selectedAccount: null,
                    loading: false,
                    error: 'Unable to load accounts'
                });
            }
        },

        selectAccount(account: Account) {
            patchState(store, {
                selectedAccount: account
            });
        }
    }))
);
