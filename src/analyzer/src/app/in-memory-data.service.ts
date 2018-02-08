import { InMemoryDbService } from 'angular-in-memory-web-api';

export class InMemoryDataService implements InMemoryDbService {
  createDb() {
    const CPUStats = [
      {"usage":0.5, "timestamp": 4, "scannerId":"asd","key":{"key":"1699d6b5508374cf2becc8778548b263271da293"}},
      {"usage":0.5, "timestamp": 4, "scannerId":"asd","key":{"key":"1699d6b5508374cf2becc8778548b263271da293"}},
      {"usage":0.5, "timestamp": 4, "scannerId":"asd","key":{"key":"1699d6b5508374cf2becc8778548b263271da293"}},
      {"usage":0.5, "timestamp": 4, "scannerId":"asd","key":{"key":"1699d6b5508374cf2becc8778548b263271da293"}},
    ];
    return {CPUStats};
  }
}
