import { GenericStat } from './generic-stat';

export class IOStat extends GenericStat {
  disk: string;
  readKBps: number;
  writeKBps: number;


  static labels = ["Timestamp", "Disk", "ReadkBps", "WritekBps"];
  static toArray(stat : IOStat): any {
    // ["Timestamp", "Usage"]
    return [new Date(stat.timestamp), stat.disk, [stat.readKBps, stat.writeKBps]];
  }
}
