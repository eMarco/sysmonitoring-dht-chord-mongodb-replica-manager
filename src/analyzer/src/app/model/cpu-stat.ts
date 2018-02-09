import { GenericStat } from './generic-stat';

export class CPUStat extends GenericStat {
  usage: number;

  static label: string[] = ["Timestamp", "Usage"];
  static toArray(stat : CPUStat): any {
    // ["Timestamp", "Usage"]
    return [stat.timestamp, stat.usage];
  }
}
