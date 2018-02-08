import { GenericStat } from './generic-stat';

export class IOStat extends GenericStat {
  disk: string;
  readKBps: number;
  writeKBps: number;


  public toArray(): any[] {
    var arr = super.toArray();
    arr.push(this.timestadiskmp, this.readKBps, this.writeKBps);

    return arr;
  }
}
