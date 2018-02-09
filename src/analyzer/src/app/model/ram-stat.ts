import { GenericStat } from './generic-stat';

export class RAMStat extends GenericStat {
  MemFree: number
  MemTotal: number
  MemAvailable: number

  static labels: string[] = ["Timestamp", "MemFree", "MemTotal", "MemAvailable"];
  static toArray(stat : RAMStat): any {
    // ["Timestamp", "Usage"]
    return [new Date(stat.timestamp), stat.MemFree, stat.MemTotal, stat.MemAvailable];
  }
}
