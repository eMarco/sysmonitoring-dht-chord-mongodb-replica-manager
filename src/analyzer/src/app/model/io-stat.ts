import { GenericStat } from './generic-stat';

export class IOStat extends GenericStat {
  disk: string;
  readKBps: number;
  writeKBps: number;


  static labels = ["Timestamp", "Disk", "Read", "Write"];
  static y_label: string = "kBps";
  static toArray(stat : IOStat): any {
    // ["Timestamp", "Usage"]
    return [new Date(stat.timestamp*1000), stat.disk, stat.readKBps, stat.writeKBps];
  }
}
