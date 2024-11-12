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