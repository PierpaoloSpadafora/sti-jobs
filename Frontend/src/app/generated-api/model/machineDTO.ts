
export interface MachineDTO {
    id?: number;
    name?: string;
    description?: string;
    status?: MachineDTO.StatusEnum;
    typeId?: number;
    typeName?: string;
    createdAt?: Date;
    updatedAt?: Date;
}
export namespace MachineDTO {
    export type StatusEnum = 'AVAILABLE' | 'BUSY' | 'MAINTENANCE' | 'OUT_OF_SERVICE';
    export const StatusEnum = {
        AVAILABLE: 'AVAILABLE' as StatusEnum,
        BUSY: 'BUSY' as StatusEnum,
        MAINTENANCE: 'MAINTENANCE' as StatusEnum,
        OUTOFSERVICE: 'OUT_OF_SERVICE' as StatusEnum
    };
}
