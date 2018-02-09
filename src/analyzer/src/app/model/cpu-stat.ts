import { GenericStat } from './generic-stat';

export class CPUStat extends GenericStat {
  usage: number;

  static labels: string[] = ["Timestamp", "Usage"];
  static toArray(stat : CPUStat): any {
    // ["Timestamp", "Usage"]
    return [new Date(stat.timestamp), stat.usage];
  }
}
