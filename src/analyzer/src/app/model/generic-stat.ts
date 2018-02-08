import { GenericValue } from './generic-value';

export class GenericStat extends GenericValue {
  timestamp: number;

  scannerId?: string;

  className?: string;

  public toArray(): any[] {
    var arr = super.toArray();
    arr.push(this.timestamp, this.scannerId, this.className);

    return arr;
  }
}
