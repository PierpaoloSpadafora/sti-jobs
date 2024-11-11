export interface Job {
    id: number;
    title: string;
    description: string;
    duration: number;
    priority: string | null;
    status: string | null;
    assignee: string | null;
    requiredMachineType: string | null;
}