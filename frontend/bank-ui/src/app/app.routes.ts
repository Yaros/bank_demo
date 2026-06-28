import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'accounts',
        pathMatch: 'full'
    },
    {
        path: 'accounts',
        loadComponent: () =>
            import('./features/accounts/pages/account-list/account-list')
                .then(m => m.AccountList)
    },
    {
        path: 'accounts/:id',
        loadComponent: () =>
            import('./features/accounts/pages/account-detail/account-detail')
                .then(m => m.AccountDetail)
    },
    {
        path: 'transactions/:id',
        loadComponent: () =>
            import('./features/transactions/pages/transaction-detail/transaction-detail')
                .then(m => m.TransactionDetail)
    },
    {
        path: '**',
        redirectTo: 'accounts'
    }
];
