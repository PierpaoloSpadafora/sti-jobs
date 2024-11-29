export interface MachineDTO {
    id?: number;
    name?: string;
    status?: string;
    typeId?: number;
    description?: string;
    createdAt?: Date | string;  
    updatedAt?: Date | string; 
}

export interface MachineTypeDTO {
    id?: number;
    name?: string;
    description?: string;
}

export interface Job {
    id?: number;
    title: string;
    description: string;
    duration: number;
    priority: string | null;
    status: string | null;
    assignee: string | null;
    requiredMachineType: string | null;
}

export interface Machine {
    id: number;
    createdAt: Date;
    description: string;
    name: string;
    status: string;
    updatedAt: Date;
    typeId: number;
}

export interface MachineType {
    id: number;
    name: string;
    description: string;
}