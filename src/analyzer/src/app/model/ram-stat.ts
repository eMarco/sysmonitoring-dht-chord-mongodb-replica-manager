import { GenericStat } from './generic-stat';

export class RAMStat extends GenericStat {
  MemFree: number
  MemTotal: number
  MemAvailable: number

  static label: string[] = ["Timestamp", "MemFree", "MemTotal", "MemAvailable"];
  static toArray(stat : RAMStat): any {
    // ["Timestamp", "Usage"]
    return [stat.timestamp, stat.MemFree, stat.MemTotal, stat.MemAvailable];
  }
}
