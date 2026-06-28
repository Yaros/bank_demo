export interface Transaction {
    id: number;
    type: string;
    amount: number;
    balanceAfter: number;
    referenceId: string | null;    
    createdAt: string;
}

