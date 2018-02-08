import { GenericStat } from './generic-stat';

export class UptimeStat extends GenericStat {
  seconds: number;
  minutes: number;
  hours: number;
  days: number;
}
