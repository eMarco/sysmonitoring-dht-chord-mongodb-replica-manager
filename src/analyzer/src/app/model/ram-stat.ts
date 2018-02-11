import { GenericStat } from './generic-stat';

export class RAMStat extends GenericStat {
  memFree: number
  memTotal: number
  memAvailable: number

  static labels: string[] = ["Timestamp", "MemFree", "MemTotal", "MemAvailable", "MemUsed"];
  static y_label: string = "MB";
  static toArray(stat : RAMStat): any {
    // ["Timestamp", "Usage"]
    return [new Date(stat.timestamp*1000), stat.memFree/1024^2, stat.memTotal/1024^2, stat.memAvailable/1024^2, (stat.memTotal - stat.memAvailable)/1024^2];
  }
}
