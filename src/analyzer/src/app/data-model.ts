export class Key {
  key: string;
}

export abstract class GenericValue {
  key: Key;
}

export class GenericStat extends GenericValue {
  timestamp: number;

  scannerId: string;
}

export class CPUStat extends GenericValue {
  usage: number;
}
