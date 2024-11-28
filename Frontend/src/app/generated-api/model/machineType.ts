
import { Job } from './job';
import { Machine } from './machine';

export interface MachineType {
    id?: number;
    name?: string;
    description?: string;
    machines?: Array<Machine>;
    jobs?: Array<Job>;
}
