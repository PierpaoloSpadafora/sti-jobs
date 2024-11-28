
import { MachineTypeDTO } from './machineTypeDTO';
import { UserDTO } from './userDTO';

export interface JobDTO {
    id?: number;
    title?: string;
    description?: string;
    status?: JobDTO.StatusEnum;
    assignee?: UserDTO;
    priority?: JobDTO.PriorityEnum;
    duration?: number;
    requiredMachineType?: MachineTypeDTO;
}
export namespace JobDTO {
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
