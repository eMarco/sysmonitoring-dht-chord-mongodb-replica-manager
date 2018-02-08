import { GenericStat } from './generic-stat';

export class UptimeStat extends GenericStat {
  seconds: number;
  minutes: number;
  hours: number;
  days: number;

  static label: string[] = ["Timestamp", "seconds", "minutes", "hours", "days"];
  static toArray(stat : UptimeStat): any {
    // ["Timestamp", "Usage"]
    return [stat.timestamp, stat.seconds, stat.minutes, stat.hours, stat.days];
  }
}
