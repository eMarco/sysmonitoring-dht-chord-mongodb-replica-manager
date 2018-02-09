import { GenericStat } from './generic-stat';

export class UptimeStat extends GenericStat {
  seconds: number;
  minutes: number;
  hours: number;
  days: number;

  static labels: string[] = ["Timestamp", "seconds", "minutes", "hours", "days"];
  static toArray(stat : UptimeStat): any {
    // ["Timestamp", "Usage"]
    return [new Date(stat.timestamp), stat.seconds, stat.minutes, stat.hours, stat.days];
  }
}
