import { GenericValue } from './generic-value';

export class GenericStat extends GenericValue {
  timestamp: number;

  scannerId?: string;

  className?: string;
}
