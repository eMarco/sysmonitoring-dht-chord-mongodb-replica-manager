import { GenericStat } from './generic-stat';

export class IOStat extends GenericStat {
  disk: string;
  readKBps: number;
  writeKBps: number;
}
