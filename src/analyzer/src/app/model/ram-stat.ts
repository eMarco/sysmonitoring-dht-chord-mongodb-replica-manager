import { GenericStat } from './generic-stat';

export class RAMStat extends GenericStat {
  MemFree: number
  MemTotal: number
  MemAvailable: number
}
