import { GenericStat } from './generic-stat';

export class CPUStat extends GenericStat {
  usage: number;

  public toArray(): any[] {
    var arr = super.toArray();
    arr.push(this.usage);

    return arr;
  }
}
