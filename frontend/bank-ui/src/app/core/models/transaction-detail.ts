export interface TransactionDetail {
    id: number;
    type: string;
    amount: number;
    balanceAfter: number;
    referenceId: string | null;
    createdAt: string;
    accountId: number;
    currency: string;
}
