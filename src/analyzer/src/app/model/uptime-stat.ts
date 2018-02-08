import { GenericStat } from './generic-stat';

export class UptimeStat extends GenericStat {
  seconds: number;
  minutes: number;
  hours: number;
  days: number;

  public toArray(): any[] {
    var arr = super.toArray();
    arr.push(this.seconds, this.minutes, this.hours, this.days);

    return arr;
  }
}
