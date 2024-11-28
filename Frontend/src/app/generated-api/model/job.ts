
import { MachineType } from './machineType';
import { User } from './user';

export interface Job {
    id?: number;
    title?: string;
    description?: string;
    status?: Job.StatusEnum;
    assignee?: User;
    priority?: Job.PriorityEnum;
    duration?: number;
    requiredMachineType?: MachineType;
}
export namespace Job {
    export type StatusEnum = 'PENDING' | 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
    export const StatusEnum = {
        PENDING: 'PENDING' as StatusEnum,
        SCHEDULED: 'SCHEDULED' as StatusEnum,
        INPROGRESS: 'IN_PROGRESS' as StatusEnum,
        COMPLETED: 'COMPLETED' as StatusEnum,
        CANCELLED: 'CANCELLED' as StatusEnum
    };
    export type PriorityEnum = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
    export const PriorityEnum = {
        LOW: 'LOW' as PriorityEnum,
        MEDIUM: 'MEDIUM' as PriorityEnum,
        HIGH: 'HIGH' as PriorityEnum,
        URGENT: 'URGENT' as PriorityEnum
    };
}
