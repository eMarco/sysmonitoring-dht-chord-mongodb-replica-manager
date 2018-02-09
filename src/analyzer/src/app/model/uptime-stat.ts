import { GenericStat } from './generic-stat';

export class UptimeStat extends GenericStat {
  seconds: number;
  minutes: number;
  hours: number;
  days: number;

  static labels: string[] = ["Timestamp", "Seconds", "minutes", "hours", "days"];
  static y_label: string = "m";
  static toArray(stat : UptimeStat): any {
    // ["Timestamp", "Usage"]
    return [new Date(stat.timestamp*1000), ((stat.seconds/60) + stat.minutes + stat.hours*60 + stat.days*60*24)];
  }
}
