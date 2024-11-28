
import { MachineType } from './machineType';

export interface Machine {
    id?: number;
    name?: string;
    description?: string;
    status?: Machine.StatusEnum;
    type?: MachineType;
    createdAt?: Date;
    updatedAt?: Date;
}
export namespace Machine {
    export type StatusEnum = 'AVAILABLE' | 'BUSY' | 'MAINTENANCE' | 'OUT_OF_SERVICE';
    export const StatusEnum = {
        AVAILABLE: 'AVAILABLE' as StatusEnum,
        BUSY: 'BUSY' as StatusEnum,
        MAINTENANCE: 'MAINTENANCE' as StatusEnum,
        OUTOFSERVICE: 'OUT_OF_SERVICE' as StatusEnum
    };
}
