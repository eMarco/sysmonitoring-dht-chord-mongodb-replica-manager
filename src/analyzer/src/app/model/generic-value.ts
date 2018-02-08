import { Key } from './key';

export abstract class GenericValue {
  key?: Key;

  public toArray(): any[] {
    return [this.key]
  }
}
